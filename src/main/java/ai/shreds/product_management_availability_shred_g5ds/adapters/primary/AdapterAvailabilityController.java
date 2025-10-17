package ai.shreds.product_management_availability_shred_g5ds.adapters.primary;

import ai.shreds.product_management_availability_shred_g5ds.application.services.ApplicationServiceAvailability;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOAvailabilityUpdate;
import ai.shreds.product_management_availability_shred_g5ds.shared.dtos.SharedAvailabilityDTO;
import ai.shreds.product_management_availability_shred_g5ds.shared.utils.SharedUtilMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/availability")
@Validated
public class AdapterAvailabilityController {

    private final ApplicationServiceAvailability availabilityApplicationService;
    private final SharedUtilMapper availabilityMapper;

    @Autowired
    public AdapterAvailabilityController(ApplicationServiceAvailability availabilityApplicationService,
                                        SharedUtilMapper availabilityMapper) {
        this.availabilityApplicationService = availabilityApplicationService;
        this.availabilityMapper = availabilityMapper;
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<SharedAvailabilityDTO>> getAvailabilityByLocation(
            @PathVariable UUID locationId) {
        
        List<SharedAvailabilityDTO> availability = availabilityApplicationService.getAvailabilityByLocation(locationId);
        return ResponseEntity.ok(availability);
    }

    @PutMapping("/location/{locationId}/product/{productId}")
    public ResponseEntity<SharedAvailabilityDTO> updateAvailability(
            @PathVariable UUID locationId,
            @PathVariable UUID productId,
            @Valid @RequestBody ApplicationDTOAvailabilityUpdate request) {
        
        SharedAvailabilityDTO updatedAvailability = availabilityApplicationService.updateAvailability(
                locationId, productId, request);
        return ResponseEntity.ok(updatedAvailability);
    }

    @GetMapping("/location/{locationId}/product/{productId}")
    public ResponseEntity<SharedAvailabilityDTO> getProductAvailabilityAtLocation(
            @PathVariable UUID locationId,
            @PathVariable UUID productId) {
        
        SharedAvailabilityDTO availability = availabilityApplicationService.checkProductAvailability(
                productId, locationId);
        return ResponseEntity.ok(availability);
    }
}