// infrastructure/rest/dto/LoginRequestDTO.java
package com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO (
    @NotBlank
    String email,

    @NotBlank
    String password
    ){

}
