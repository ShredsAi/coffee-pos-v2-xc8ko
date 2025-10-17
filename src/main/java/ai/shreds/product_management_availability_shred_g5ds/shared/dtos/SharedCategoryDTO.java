package ai.shreds.product_management_availability_shred_g5ds.shared.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

public class SharedCategoryDTO {
    @NotNull
    @JsonProperty("categoryId")
    private UUID categoryId;

    @NotBlank
    @Size(min = 1, max = 100)
    @JsonProperty("name")
    private String name;

    @Size(max = 500)
    @JsonProperty("description")
    private String description;

    @JsonProperty("parentCategoryId")
    private UUID parentCategoryId;

    @JsonProperty("displayOrder")
    private Integer displayOrder = 0;

    @JsonProperty("isActive")
    private Boolean isActive = true;

    public SharedCategoryDTO() {}

    public SharedCategoryDTO(UUID categoryId, String name, String description, UUID parentCategoryId,
                            Integer displayOrder, Boolean isActive) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.parentCategoryId = parentCategoryId;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(UUID parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}