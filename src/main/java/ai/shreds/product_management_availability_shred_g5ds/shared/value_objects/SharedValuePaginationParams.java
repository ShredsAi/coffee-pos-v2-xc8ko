package ai.shreds.product_management_availability_shred_g5ds.shared.value_objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import java.util.Objects;

public class SharedValuePaginationParams {
    @Min(0)
    @JsonProperty("page")
    private final Integer page;

    @Min(1)
    @JsonProperty("size")
    private final Integer size;

    @JsonProperty("sort")
    private final String sort;

    public SharedValuePaginationParams(@JsonProperty("page") Integer page,
                                      @JsonProperty("size") Integer size,
                                      @JsonProperty("sort") String sort) {
        this.page = page != null ? page : 0;
        this.size = size != null ? size : 20;
        this.sort = sort;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getSize() {
        return size;
    }

    public String getSort() {
        return sort;
    }

    public Integer getOffset() {
        return page * size;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        SharedValuePaginationParams that = (SharedValuePaginationParams) other;
        return Objects.equals(page, that.page) &&
               Objects.equals(size, that.size) &&
               Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, size, sort);
    }
}