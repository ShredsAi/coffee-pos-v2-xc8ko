package ai.shreds.product_management_availability_shred_g5ds.adapters.primary;

import ai.shreds.product_management_availability_shred_g5ds.application.services.ApplicationServiceCategory;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOCreateCategory;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOUpdateCategory;
import ai.shreds.product_management_availability_shred_g5ds.shared.dtos.SharedCategoryDTO;
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
@RequestMapping("/api/v1/categories")
@Validated
public class AdapterCategoryController {

    private final ApplicationServiceCategory categoryApplicationService;
    private final SharedUtilMapper categoryMapper;

    @Autowired
    public AdapterCategoryController(ApplicationServiceCategory categoryApplicationService,
                                    SharedUtilMapper categoryMapper) {
        this.categoryApplicationService = categoryApplicationService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping
    public ResponseEntity<List<SharedCategoryDTO>> getCategories(
            @RequestParam(required = false) Boolean active) {
        
        List<SharedCategoryDTO> categories = categoryApplicationService.listCategories(active);
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<SharedCategoryDTO> createCategory(@Valid @RequestBody ApplicationDTOCreateCategory request) {
        SharedCategoryDTO createdCategory = categoryApplicationService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SharedCategoryDTO> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody ApplicationDTOUpdateCategory request) {
        
        SharedCategoryDTO updatedCategory = categoryApplicationService.updateCategory(id, request);
        return ResponseEntity.ok(updatedCategory);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SharedCategoryDTO> getCategoryById(@PathVariable UUID id) {
        SharedCategoryDTO category = categoryApplicationService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/hierarchy")
    public ResponseEntity<List<SharedCategoryDTO>> getCategoryHierarchy() {
        List<SharedCategoryDTO> hierarchy = categoryApplicationService.getCategoryHierarchy();
        return ResponseEntity.ok(hierarchy);
    }
}