package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.model.AdditionSettings;
import com.tetris.tetrisburger_backend.domain.port.in.additionsettings.GetAdditionSettings;
import com.tetris.tetrisburger_backend.domain.port.in.additionsettings.UpdateAdditionSettings;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.additionsettings.AdditionSettingsResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.additionsettings.UpdateAdditionSettingsRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.AdditionSettingsDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdditionSettingsControllerTest {

    @Mock private GetAdditionSettings getAdditionSettings;
    @Mock private UpdateAdditionSettings updateAdditionSettings;
    @Mock private AdditionSettingsDtoMapper mapper;

    @InjectMocks
    private AdditionSettingsController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ─── GET ────────────────────────────────────────────────────────────────

    @Test
    void get_returnsOk_withMappedSettings() throws Exception {
        AdditionSettings domain = mock(AdditionSettings.class);
        AdditionSettingsResponseDTO responseDTO = mock(AdditionSettingsResponseDTO.class);

        when(getAdditionSettings.handle()).thenReturn(domain);
        when(mapper.toResponseDTO(domain)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/addition-settings"))
                .andExpect(status().isOk());

        verify(getAdditionSettings).handle();
        verify(mapper).toResponseDTO(domain);
    }

    @Test
    void get_whenServiceThrows_propagatesException() {
        when(getAdditionSettings.handle()).thenThrow(new RuntimeException("DB error"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(get("/api/addition-settings")));
    }

    // ─── PUT ────────────────────────────────────────────────────────────────

    @Test
    void update_returnsOk_withMappedSettings() throws Exception {
        AdditionSettings domain = mock(AdditionSettings.class);
        AdditionSettingsResponseDTO responseDTO = mock(AdditionSettingsResponseDTO.class);

        // mapper.toCommand() → null (default mock), any() lo captura igual
        when(updateAdditionSettings.handle(any())).thenReturn(domain);
        when(mapper.toResponseDTO(domain)).thenReturn(responseDTO);

        mockMvc.perform(put("/api/addition-settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(mapper).toCommand(any(UpdateAdditionSettingsRequestDTO.class));
        verify(updateAdditionSettings).handle(any());
        verify(mapper).toResponseDTO(domain);
    }

    @Test
    void update_whenServiceThrows_propagatesException() {
        when(updateAdditionSettings.handle(any())).thenThrow(new RuntimeException("Update failed"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(put("/api/addition-settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")));
    }

    @Test
    void update_withoutContentType_returnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/addition-settings")
                        .content("{}"))
                .andExpect(status().isUnsupportedMediaType());
    }
}