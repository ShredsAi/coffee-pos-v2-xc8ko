package ai.shreds.product_management_availability_shred_g5ds.infrastructure.external_services;

import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainOutputPortInventoryClient;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueLocationId;
import ai.shreds.product_management_availability_shred_g5ds.infrastructure.exceptions.InfrastructureExceptionExternalService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Component
public class InfrastructureClientInventoryManagement implements DomainOutputPortInventoryClient {

    private final RestTemplate restTemplate;
    private final String inventoryServiceUrl;
    private final CircuitBreaker circuitBreaker;
    private final RetryTemplate retryTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public InfrastructureClientInventoryManagement(
            RestTemplate restTemplate,
            @Value("${inventory.service.url:http://localhost:8081}") String inventoryServiceUrl,
            CircuitBreaker circuitBreaker,
            RetryTemplate retryTemplate,
            ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.inventoryServiceUrl = inventoryServiceUrl;
        this.circuitBreaker = circuitBreaker;
        this.retryTemplate = retryTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<DomainValueProductId, Integer> getInventoryStatus(DomainValueLocationId locationId, List<DomainValueProductId> productIds) {
        return circuitBreaker.executeSupplier(() -> {
            return retryTemplate.execute(context -> {
                try {
                    InfrastructureInventoryRequest request = buildInventoryRequest(locationId, productIds);
                    String url = inventoryServiceUrl + "/api/v1/inventory/status";
                    
                    String response = restTemplate.postForObject(url, request, String.class);
                    InfrastructureInventoryResponse inventoryResponse = parseInventoryResponse(response);
                    
                    return inventoryResponse.getInventoryData().stream()
                            .collect(Collectors.toMap(
                                item -> new DomainValueProductId(java.util.UUID.fromString(item.getProductId())),
                                InfrastructureInventoryItemData::getCurrentStock
                            ));
                } catch (HttpClientErrorException | HttpServerErrorException e) {
                    throw new InfrastructureExceptionExternalService(
                        "Inventory Management Service", e.getRawStatusCode()
                    );
                } catch (Exception e) {
                    throw new InfrastructureExceptionExternalService(
                        "Failed to get inventory status: " + e.getMessage(),
                        "Inventory Management Service", 500
                    );
                }
            });
        });
    }

    @Override
    public Map<DomainValueLocationId, Map<DomainValueProductId, Integer>> getAllLocationsInventoryStatus() {
        return circuitBreaker.executeSupplier(() -> {
            return retryTemplate.execute(context -> {
                try {
                    String url = inventoryServiceUrl + "/api/v1/inventory/status/all";
                    String response = restTemplate.getForObject(url, String.class);
                    
                    // Parse the response for all locations - simplified implementation
                    Map<DomainValueLocationId, Map<DomainValueProductId, Integer>> allLocationsData = new HashMap<>();
                    // Implementation would parse the actual response structure
                    return allLocationsData;
                } catch (HttpClientErrorException | HttpServerErrorException e) {
                    throw new InfrastructureExceptionExternalService(
                        "Inventory Management Service", e.getRawStatusCode()
                    );
                } catch (Exception e) {
                    throw new InfrastructureExceptionExternalService(
                        "Failed to get all locations inventory status: " + e.getMessage(),
                        "Inventory Management Service", 500
                    );
                }
            });
        });
    }

    @Override
    public boolean isServiceAvailable() {
        try {
            String healthUrl = inventoryServiceUrl + "/health";
            String response = restTemplate.getForObject(healthUrl, String.class);
            return response != null;
        } catch (Exception e) {
            return false;
        }
    }

    public InfrastructureInventoryRequest buildInventoryRequest(DomainValueLocationId locationId, List<DomainValueProductId> productIds) {
        InfrastructureInventoryRequest request = new InfrastructureInventoryRequest();
        request.setRequestType("INVENTORY_STATUS");
        request.setLocationId(locationId.getValue().toString());
        request.setProductIds(productIds.stream()
                .map(id -> id.getValue().toString())
                .collect(Collectors.toList()));
        request.setTimestamp(java.time.LocalDateTime.now().toString());
        return request;
    }

    public InfrastructureInventoryResponse parseInventoryResponse(String response) {
        try {
            return objectMapper.readValue(response, InfrastructureInventoryResponse.class);
        } catch (Exception e) {
            throw new InfrastructureExceptionExternalService(
                "Failed to parse inventory response: " + e.getMessage(),
                "Inventory Management Service", 500
            );
        }
    }
}