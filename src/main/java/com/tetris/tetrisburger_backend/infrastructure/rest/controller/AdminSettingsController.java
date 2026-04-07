package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;

import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.GetBurgerSettings;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.UpdateBurgerSettings;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateBurgerSettingsCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin.BurgerSettingsResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin.UpdateBurgerSettingsRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/settings")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Settings", description = "Configuración de hamburguesas personalizadas")
public class AdminSettingsController {

    
    private final GetBurgerSettings getBurgerSettings;
    private final UpdateBurgerSettings updateBurgerSettings;

    public AdminSettingsController(
            GetBurgerSettings getBurgerSettings,
            UpdateBurgerSettings updateBurgerSettings
    ) {
        this.getBurgerSettings = getBurgerSettings;
        this.updateBurgerSettings = updateBurgerSettings;
    }

    @GetMapping("/burgers")
    @Operation(
            summary = "Obtener configuración actual de hamburguesas personalizadas",
            description = "Retorna los límites de precio e ingredientes para burgers personalizadas"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Configuración obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = BurgerSettingsResponseDTO.class))
            )
    })
    public ResponseEntity<BurgerSettingsResponseDTO> getBurgerSettings() {
        
        BurgerSettings settings = getBurgerSettings.handle();
        BurgerSettingsResponseDTO response = BurgerSettingsResponseDTO.from(settings);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/burgers")
    @Operation(
            summary = "Actualizar límites de hamburguesas personalizadas",
            description = "Permite al admin configurar precios mínimo/máximo y límites de ingredientes"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Configuración actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = BurgerSettingsResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos"
            )
    })
    public ResponseEntity<BurgerSettingsResponseDTO> updateBurgerSettings(
            @AuthenticationPrincipal CustomUserDetails admin,
            @Valid @RequestBody UpdateBurgerSettingsRequestDTO dto
    ) {
        
        UpdateBurgerSettingsCommand command = new UpdateBurgerSettingsCommand(
                dto.customBurgerMinPrice(),
                dto.customBurgerMaxPrice(),
                dto.minIngredients(),
                dto.maxIngredients(),
                dto.customBurgersEnabled()
        );

        BurgerSettings updated = updateBurgerSettings.handle(command);
        BurgerSettingsResponseDTO response = BurgerSettingsResponseDTO.from(updated);

                return ResponseEntity.ok(response);
    }
}
