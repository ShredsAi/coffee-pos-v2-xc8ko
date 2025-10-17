package ai.shreds.product_management_availability_shred_g5ds.adapters.primary;

import ai.shreds.product_management_availability_shred_g5ds.application.services.ApplicationServiceVariant;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOCreateVariant;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOUpdateVariant;
import ai.shreds.product_management_availability_shred_g5ds.shared.dtos.SharedVariantDTO;
import ai.shreds.product_management_availability_shred_g5ds.shared.utils.SharedUtilMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/{productId}/variants")
@Validated
public class AdapterVariantController {

    private final ApplicationServiceVariant variantApplicationService;
    private final SharedUtilMapper variantMapper;

    @Autowired
    public AdapterVariantController(ApplicationServiceVariant variantApplicationService,
                                   SharedUtilMapper variantMapper) {
        this.variantApplicationService = variantApplicationService;
        this.variantMapper = variantMapper;
    }

    @GetMapping
    public ResponseEntity<List<SharedVariantDTO>> getVariantsByProduct(@PathVariable UUID productId) {
        List<SharedVariantDTO> variants = variantApplicationService.getVariantsByProduct(productId);
        return ResponseEntity.ok(variants);
    }

    @PostMapping
    public ResponseEntity<SharedVariantDTO> createVariant(
            @PathVariable UUID productId,
            @Valid @RequestBody ApplicationDTOCreateVariant request) {
        
        SharedVariantDTO createdVariant = variantApplicationService.createVariant(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVariant);
    }

    @PutMapping("/{variantId}")
    public ResponseEntity<SharedVariantDTO> updateVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId,
            @Valid @RequestBody ApplicationDTOUpdateVariant request) {
        
        SharedVariantDTO updatedVariant = variantApplicationService.updateVariant(
                productId, variantId, request);
        return ResponseEntity.ok(updatedVariant);
    }

    @DeleteMapping("/{variantId}")
    public ResponseEntity<Void> deleteVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId) {
        
        variantApplicationService.deleteVariant(productId, variantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{variantId}")
    public ResponseEntity<SharedVariantDTO> getVariantById(
            @PathVariable UUID productId,
            @PathVariable UUID variantId) {
        
        // For now, get all variants and find the specific one
        // This is a temporary solution until we add getVariantById to the service
        List<SharedVariantDTO> variants = variantApplicationService.getVariantsByProduct(productId);
        SharedVariantDTO variant = variants.stream()
            .filter(v -> v.getVariantId().equals(variantId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Variant not found with ID: " + variantId));
        
        return ResponseEntity.ok(variant);
    }
}