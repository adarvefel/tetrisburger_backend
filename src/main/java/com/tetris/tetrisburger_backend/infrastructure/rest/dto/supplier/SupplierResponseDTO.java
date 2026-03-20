package com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SupplierResponseDTO {
    private Integer id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private LocalDate registrationDate;
}
