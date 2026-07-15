package com.interviewdocs.server.utils;

import io.micronaut.serde.annotation.Serdeable;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@Serdeable
public class PagedResponse<T> {
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private List<T> content;
    private long totalSize;
    private int pageSize;
    private int pageNumber;

    public PagedResponse(List<T> content, long totalSize, int pageSize, int pageNumber) {
        this.content = content;
        this.totalSize = totalSize;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }
    
    public long getTotalSize() { return totalSize; }
    public void setTotalSize(long totalSize) { this.totalSize = totalSize; }
    
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    
    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }
}