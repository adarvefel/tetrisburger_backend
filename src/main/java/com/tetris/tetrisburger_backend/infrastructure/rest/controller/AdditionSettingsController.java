package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.model.AdditionSettings;
import com.tetris.tetrisburger_backend.domain.port.in.additionsettings.GetAdditionSettings;
import com.tetris.tetrisburger_backend.domain.port.in.additionsettings.UpdateAdditionSettings;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.additionSettings.UpdateAdditionSettingsRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.additionsettings.AdditionSettingsResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.AdditionSettingsDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addition-settings")
public class AdditionSettingsController {

    private final GetAdditionSettings getAdditionSettings;
    private final UpdateAdditionSettings updateAdditionSettings;
    private final AdditionSettingsDtoMapper mapper;

    public AdditionSettingsController(GetAdditionSettings getAdditionSettings,
                                      UpdateAdditionSettings updateAdditionSettings,
                                      AdditionSettingsDtoMapper mapper) {
        this.getAdditionSettings = getAdditionSettings;
        this.updateAdditionSettings = updateAdditionSettings;
        this.mapper = mapper;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<AdditionSettingsResponseDTO> get() {
        AdditionSettings settings = getAdditionSettings.handle();
        return ResponseEntity.ok(mapper.toResponseDTO(settings));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping
    public ResponseEntity<AdditionSettingsResponseDTO> update(
            @RequestBody UpdateAdditionSettingsRequestDTO requestDTO) {
        AdditionSettings settings = updateAdditionSettings.handle(mapper.toCommand(requestDTO));
        return ResponseEntity.ok(mapper.toResponseDTO(settings));
    }
}