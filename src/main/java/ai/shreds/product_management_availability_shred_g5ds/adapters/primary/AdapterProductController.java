package ai.shreds.product_management_availability_shred_g5ds.adapters.primary;

import ai.shreds.product_management_availability_shred_g5ds.application.services.ApplicationServiceProduct;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOCreateProduct;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOUpdateProduct;
import ai.shreds.product_management_availability_shred_g5ds.shared.dtos.SharedProductDTO;
import ai.shreds.product_management_availability_shred_g5ds.shared.dtos.SharedPagedResultDTO;
import ai.shreds.product_management_availability_shred_g5ds.shared.value_objects.SharedValuePaginationParams;
import ai.shreds.product_management_availability_shred_g5ds.shared.utils.SharedUtilMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@Validated
public class AdapterProductController {

    private final ApplicationServiceProduct productApplicationService;
    private final SharedUtilMapper productMapper;

    @Autowired
    public AdapterProductController(ApplicationServiceProduct productApplicationService,
                                   SharedUtilMapper productMapper) {
        this.productApplicationService = productApplicationService;
        this.productMapper = productMapper;
    }

    @GetMapping
    public ResponseEntity<SharedPagedResultDTO<SharedProductDTO>> getProducts(
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean active) {
        
        UUID categoryId = null;
        if (category != null && !category.trim().isEmpty()) {
            try {
                categoryId = UUID.fromString(category);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid category UUID format: " + category);
            }
        }
        
        SharedValuePaginationParams paginationParams = new SharedValuePaginationParams(
            page, size, null);
        
        SharedPagedResultDTO<SharedProductDTO> products = productApplicationService.listProducts(
                paginationParams, categoryId, active);
        
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<SharedProductDTO> createProduct(@Valid @RequestBody ApplicationDTOCreateProduct request) {
        SharedProductDTO createdProduct = productApplicationService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SharedProductDTO> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ApplicationDTOUpdateProduct request) {
        
        SharedProductDTO updatedProduct = productApplicationService.updateProduct(id, request);
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SharedProductDTO> getProductById(@PathVariable UUID id) {
        SharedProductDTO product = productApplicationService.getProductById(id);
        return ResponseEntity.ok(product);
    }
}