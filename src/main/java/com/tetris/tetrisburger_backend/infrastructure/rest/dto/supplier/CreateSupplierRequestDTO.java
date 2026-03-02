package com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateSupplierRequestDTO {
    @NotBlank
    @Size(max = 255)
    private String name;
    @Size(max = 20)
    private String phone;
    @Email
    @Size(max = 255)
    private String email;
    @Size(max = 255)
    private String address;
    private LocalDate registrationDate;
}
