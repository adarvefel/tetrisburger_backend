package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.model.WhatsappSettings;
import com.tetris.tetrisburger_backend.domain.port.in.whatsapp.GetWhatsappSettings;
import com.tetris.tetrisburger_backend.domain.port.in.whatsapp.UpdateWhatsappSettings;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.whatsapp.UpdateWhatsappSettingsRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.whatsapp.WhatsappSettingsResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.WhatsappSettingsRestMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/whatsapp-settings")
public class WhatsappSettingsController {

    private final GetWhatsappSettings getSettings;
    private final UpdateWhatsappSettings updateSettings;
    private final WhatsappSettingsRestMapper mapper;

    public WhatsappSettingsController(
            GetWhatsappSettings getSettings,
            UpdateWhatsappSettings updateSettings,
            WhatsappSettingsRestMapper mapper
    ) {
        this.getSettings = getSettings;
        this.updateSettings = updateSettings;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<WhatsappSettingsResponseDTO> get() {
        return ResponseEntity.ok(mapper.toResponseDTO(getSettings.handle()));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<WhatsappSettingsResponseDTO> update(
            @RequestBody UpdateWhatsappSettingsRequestDTO dto
    ) {
        WhatsappSettings updated = updateSettings.handle(
                dto.businessNumber(),
                dto.apiKey(),
                dto.messageTemplate(),
                dto.autoSend()
        );
        return ResponseEntity.ok(mapper.toResponseDTO(updated));
    }
}