package com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition;


import java.util.List;

public record ListAdditionResponseDTO(
    List<AdditionResponseDTO> items,
    int page,
    int size,
    long totalElements,
    int totalPages
){
}
