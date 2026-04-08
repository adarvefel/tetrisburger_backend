package com.tetris.tetrisburger_backend.infrastructure.persistence.util;

import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import org.springframework.data.domain.Sort;

public class SortBuilder {

    private SortBuilder() {}

    public static Sort build(PaginationRequest pagination) {
        String sortBy = pagination.getSortBy();

        if (sortBy == null || sortBy.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Sort.Direction direction = "DESC".equalsIgnoreCase(pagination.getDirection())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return Sort.by(direction, sortBy);
    }
}