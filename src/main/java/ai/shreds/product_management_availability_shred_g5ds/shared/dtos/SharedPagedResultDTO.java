package ai.shreds.product_management_availability_shred_g5ds.shared.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

public class SharedPagedResultDTO<T> {
    @NotNull
    @JsonProperty("content")
    private List<T> content;

    @Min(0)
    @JsonProperty("totalElements")
    private Long totalElements;

    @Min(0)
    @JsonProperty("totalPages")
    private Integer totalPages;

    @Min(0)
    @JsonProperty("currentPage")
    private Integer currentPage;

    @Min(1)
    @JsonProperty("pageSize")
    private Integer pageSize;

    @JsonProperty("hasNext")
    private Boolean hasNext;

    @JsonProperty("hasPrevious")
    private Boolean hasPrevious;

    public SharedPagedResultDTO() {}

    public SharedPagedResultDTO(List<T> content, Long totalElements, Integer totalPages, 
                               Integer currentPage, Integer pageSize) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.hasNext = currentPage < totalPages - 1;
        this.hasPrevious = currentPage > 0;
    }

    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }

    public Long getTotalElements() { return totalElements; }
    public void setTotalElements(Long totalElements) { this.totalElements = totalElements; }

    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }

    public Integer getCurrentPage() { return currentPage; }
    public void setCurrentPage(Integer currentPage) { this.currentPage = currentPage; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }

    public Boolean getHasNext() { return hasNext; }
    public void setHasNext(Boolean hasNext) { this.hasNext = hasNext; }

    public Boolean getHasPrevious() { return hasPrevious; }
    public void setHasPrevious(Boolean hasPrevious) { this.hasPrevious = hasPrevious; }
}