package com.tetris.tetrisburger_backend.domain.common;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public PageResponse {
        if (content == null) throw new IllegalArgumentException("items no puede ser null");
        if (page < 0) throw new IllegalArgumentException("page no puede ser negativo");
        if (size < 0) throw new IllegalArgumentException("size no puede ser negativo");
        if (totalElements < 0) throw new IllegalArgumentException("totalElements no puede ser negativo");
        if (totalPages < 0) throw new IllegalArgumentException("totalPages no puede ser negativo");
    }

    public <R> PageResponse<R> map(Function<T, R> mapper) {
        List<R> mappedContent = content.stream()
                .map(mapper)
                .toList();
        return new PageResponse<>(mappedContent, page, size, totalElements, totalPages);
    }

}