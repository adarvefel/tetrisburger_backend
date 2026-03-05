package com.tetris.tetrisburger_backend.domain.common;

public class PaginationRequest {
    private int page;
    private int size;
    private String sortBy;
    private String direction; // "ASC" o "DESC"

    public PaginationRequest() { this(0, 12, null, "ASC"); }

    public PaginationRequest(int page, int size, String sortBy) {
        this(page, size, sortBy, "ASC");
    }

    public PaginationRequest(int page, int size) { this(page, size, null, "ASC"); }


    public PaginationRequest(int page, int size, String sortBy, String direction) {
        setPage(page);
        setSize(size);
        setSortBy(sortBy);
        setDirection(direction);
    }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = Math.max(0, page); }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size <= 0 ? 12 : Math.min(size, 100); }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = (sortBy == null || sortBy.isBlank()) ? null : sortBy; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) {
        if (direction == null || direction.isBlank()) this.direction = "ASC";
        else this.direction = "DESC".equalsIgnoreCase(direction) ? "DESC" : "ASC";
    }
}