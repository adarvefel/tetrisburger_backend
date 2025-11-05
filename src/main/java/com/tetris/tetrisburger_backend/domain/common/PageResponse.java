package com.tetris.tetrisburger_backend.domain.common;

import java.util.List;

/**
 * Respuesta genérica paginada para cualquier tipo de contenido.
 * Inmutable por diseño.
 */
public record PageResponse<T>(
        List<T> content,
        int page ,// Contenido de la página
        long totalElements,        // Total de registros en la BD
        int totalPages,            // Total de páginas
        int pageNumber             // Página actual (0-indexed)
) {

    /**
     * Constructor compacto para validación
     */
    public PageResponse {
        if (content == null) {
            throw new IllegalArgumentException("El contenido no puede ser nulo");
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("Total de elementos no puede ser negativo");
        }
        if (totalPages < 0) {
            throw new IllegalArgumentException("Total de páginas no puede ser negativo");
        }
        if (pageNumber < 0) {
            throw new IllegalArgumentException("El número de página no puede ser negativo");
        }
    }

    /**
     * Obtiene el tamaño de la página actual
     */
    public int getPageSize() {
        return content.size();
    }

    /**
     * Verifica si es la primera página
     */
    public boolean isFirst() {
        return pageNumber == 0;
    }

    /**
     * Verifica si es la última página
     */
    public boolean isLast() {
        return pageNumber >= totalPages - 1;
    }

    /**
     * Obtiene el número de página (1-indexed) para mostrar al usuario
     */
    public int getPageNumberForDisplay() {
        return pageNumber + 1;
    }
}
