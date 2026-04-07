package com.tetris.tetrisburger_backend.infrastructure.rest.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.CreateAddition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.DeleteAddition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.GetAdditionById;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.ListAddition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.SearchAdditionByName;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.UpdateAddition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.UpdateAdditionImage;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.command.CreateAdditionCommand;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.command.UpdateAdditionCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition.AdditionResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition.UpdateAdditionRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.AdditionRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AdditionControllerTest {

    // ─── Mocks compartidos ────────────────────────────────────────────────────
    @Mock private CreateAddition createAddition;
    @Mock private UpdateAddition updateAddition;
    @Mock private UpdateAdditionImage updateAdditionImage;
    @Mock private ListAddition listAddition;
    @Mock private SearchAdditionByName searchAdditionByName;
    @Mock private GetAdditionById getAdditionById;
    @Mock private DeleteAddition deleteAddition;
    @Mock private AdditionRestDtoMapper mapper;
    @Mock private CustomUserDetails mockUserDetails;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    static final Integer USER_ID    = 1;
    static final Integer ADDITION_ID = 10;

    // ─── Setup / Teardown ─────────────────────────────────────────────────────

    @BeforeEach
    void setUp() {
        AdditionController controller = new AdditionController(
                createAddition, updateAddition, updateAdditionImage,
                listAddition, searchAdditionByName, getAdditionById,
                deleteAddition, mapper
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Stub lenient: usado solo en los tests que llaman a userDetails.getId()
        lenient().when(mockUserDetails.getId()).thenReturn(USER_ID);

        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUserDetails, null, List.of())
        );
        SecurityContextHolder.setContext(ctx);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private MockPart buildDataPart(String json) {
        MockPart part = new MockPart("data", json.getBytes());
        part.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return part;
    }

    // =========================================================================
    // POST /api/admin/additions
    // =========================================================================

    @Nested
    class CreateAdditionTests {

        @Test
        void create_conImagen_retorna201() throws Exception {
            Addition mockAddition       = mock(Addition.class);
            AdditionResponseDTO mockDto = mock(AdditionResponseDTO.class);

            when(mapper.toCreateAdditionCommand(any(), any(), eq(USER_ID)))
                    .thenReturn(mock(CreateAdditionCommand.class));
            when(createAddition.handle(any())).thenReturn(mockAddition);
            when(mapper.toAdditionResponseDTO(mockAddition, true)).thenReturn(mockDto);

            MockPart dataPart = buildDataPart(
                    "{\"name\":\"Queso Extra\",\"description\":\"Cheddar\",\"price\":2000,\"available\":true}");
            MockMultipartFile imagePart = new MockMultipartFile(
                    "additionImage", "queso.jpg", "image/jpeg", "bytes".getBytes());

            mockMvc.perform(multipart("/api/admin/additions")
                            .part(dataPart)
                            .file(imagePart))
                    .andExpect(status().isCreated());

            verify(createAddition).handle(any());
            verify(mapper).toAdditionResponseDTO(mockAddition, true);
        }

        @Test
        void create_sinImagen_retorna201() throws Exception {
            Addition mockAddition       = mock(Addition.class);
            AdditionResponseDTO mockDto = mock(AdditionResponseDTO.class);

            when(mapper.toCreateAdditionCommand(any(), isNull(), eq(USER_ID)))
                    .thenReturn(mock(CreateAdditionCommand.class));
            when(createAddition.handle(any())).thenReturn(mockAddition);
            when(mapper.toAdditionResponseDTO(mockAddition, false)).thenReturn(mockDto);

            MockPart dataPart = buildDataPart(
                    "{\"name\":\"Queso Extra\",\"description\":\"Cheddar\",\"price\":2000,\"available\":true}");

            mockMvc.perform(multipart("/api/admin/additions")
                            .part(dataPart))
                    .andExpect(status().isCreated());

            verify(createAddition).handle(any());
            verify(mapper).toAdditionResponseDTO(mockAddition, false);
        }
    }

    // =========================================================================
    // PATCH /api/admin/additions/{id}
    // =========================================================================

    @Nested
    class UpdateAdditionTests {

        @Test
        void update_requestValido_retorna200() throws Exception {
            UpdateAdditionRequestDTO dto = new UpdateAdditionRequestDTO(
                    "Queso Actualizado", null, BigDecimal.valueOf(2500), true);
            Addition mockAddition       = mock(Addition.class);
            AdditionResponseDTO mockDto = mock(AdditionResponseDTO.class);

            when(mapper.toUpdateAdditionCommand(eq(ADDITION_ID), any(), eq(USER_ID)))
                    .thenReturn(mock(UpdateAdditionCommand.class));
            when(updateAddition.handle(any())).thenReturn(mockAddition);
            when(mapper.toAdditionResponseDTO(mockAddition, false)).thenReturn(mockDto);

            mockMvc.perform(patch("/api/admin/additions/{id}", ADDITION_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());

            verify(updateAddition).handle(any());
            verify(mapper).toAdditionResponseDTO(mockAddition, false);
        }
    }

    // =========================================================================
    // PUT /api/admin/additions/image/{id}
    // =========================================================================

    @Nested
    class UpdateAdditionImageTests {

        @Test
        void updateImage_requestValido_retorna200() throws Exception {
            Addition mockAddition       = mock(Addition.class);
            AdditionResponseDTO mockDto = mock(AdditionResponseDTO.class);

            when(updateAdditionImage.handle(eq(ADDITION_ID), any(FileData.class), eq(USER_ID)))
                    .thenReturn(mockAddition);
            when(mapper.toAdditionResponseDTO(mockAddition, true)).thenReturn(mockDto);

            MockMultipartFile imagePart = new MockMultipartFile(
                    "additionImage", "test.jpg", "image/jpeg", "data".getBytes());

            mockMvc.perform(multipart("/api/admin/additions/image/{id}", ADDITION_ID)
                            .file(imagePart)
                            // PUT en vez de POST (multipart() usa POST por defecto)
                            .with(req -> { req.setMethod("PUT"); return req; }))
                    .andExpect(status().isOk());

            verify(updateAdditionImage).handle(eq(ADDITION_ID), any(FileData.class), eq(USER_ID));
            verify(mapper).toAdditionResponseDTO(mockAddition, true);
        }
    }

    // =========================================================================
    // GET /api/admin/additions
    // =========================================================================

    @Nested
    class ListAdditionsTests {

        @Test
        void getAll_sinFiltros_retorna200() throws Exception {
            PageResponse<Addition> page = new PageResponse<>(List.of(), 0, 10, 0L, 0);
            when(listAddition.handle(isNull(), any(PaginationRequest.class))).thenReturn(page);

            mockMvc.perform(get("/api/admin/additions"))
                    .andExpect(status().isOk());

            verify(listAddition).handle(isNull(), any(PaginationRequest.class));
        }

        @Test
        void getAll_conFiltroAvailable_retorna200() throws Exception {
            Addition mockAddition       = mock(Addition.class);
            AdditionResponseDTO mockDto = mock(AdditionResponseDTO.class);
            PageResponse<Addition> page = new PageResponse<>(List.of(mockAddition), 0, 10, 1L, 1);

            when(listAddition.handle(eq(true), any(PaginationRequest.class))).thenReturn(page);
            when(mapper.toAdditionResponseDTO(any(Addition.class), eq(false))).thenReturn(mockDto);

            mockMvc.perform(get("/api/admin/additions").param("available", "true"))
                    .andExpect(status().isOk());

            verify(listAddition).handle(eq(true), any(PaginationRequest.class));
            verify(mapper).toAdditionResponseDTO(mockAddition, false);
        }

        @Test
        void getAll_paginacionPersonalizada_pasaParametrosCorrectos() throws Exception {
            PageResponse<Addition> page = new PageResponse<>(List.of(), 1, 5, 0L, 0);
            when(listAddition.handle(isNull(), any(PaginationRequest.class))).thenReturn(page);

            mockMvc.perform(get("/api/admin/additions")
                            .param("page", "1")
                            .param("size", "5")
                            .param("sortBy", "name")
                            .param("sortDirection", "DESC"))
                    .andExpect(status().isOk());

            verify(listAddition).handle(isNull(), any(PaginationRequest.class));
        }
    }

    // =========================================================================
    // GET /api/admin/additions/{id}
    // =========================================================================

    @Nested
    class GetByIdTests {

        @Test
        void getById_idExistente_retorna200() throws Exception {
            Addition mockAddition       = mock(Addition.class);
            AdditionResponseDTO mockDto = mock(AdditionResponseDTO.class);

            when(getAdditionById.execute(ADDITION_ID)).thenReturn(mockAddition);
            when(mapper.toAdditionResponseDTO(mockAddition, false)).thenReturn(mockDto);

            mockMvc.perform(get("/api/admin/additions/{id}", ADDITION_ID))
                    .andExpect(status().isOk());

            verify(getAdditionById).execute(ADDITION_ID);
            verify(mapper).toAdditionResponseDTO(mockAddition, false);
        }

        @Test
        void getById_useCaseLanzaExcepcion_propagaExcepcion() {
            when(getAdditionById.execute(ADDITION_ID))
                    .thenThrow(new RuntimeException("Adición no encontrada"));

            assertThrows(Exception.class, () ->
                    mockMvc.perform(get("/api/admin/additions/{id}", ADDITION_ID)));
        }
    }

    // =========================================================================
    // DELETE /api/admin/additions/{id}
    // =========================================================================

    @Nested
    class DeleteAdditionTests {

        @Test
        void delete_idExistente_retorna200() throws Exception {
            Addition mockAddition = mock(Addition.class);
            when(mockAddition.getIdAddition()).thenReturn(ADDITION_ID);
            when(mockAddition.getName()).thenReturn("Queso Extra");
            when(deleteAddition.handle(ADDITION_ID, USER_ID)).thenReturn(mockAddition);

            mockMvc.perform(delete("/api/admin/additions/{id}", ADDITION_ID))
                    .andExpect(status().isOk());

            verify(deleteAddition).handle(ADDITION_ID, USER_ID);
        }

        @Test
        void delete_useCaseLanzaExcepcion_propagaExcepcion() {
            when(deleteAddition.handle(ADDITION_ID, USER_ID))
                    .thenThrow(new RuntimeException("Adición no encontrada"));

            assertThrows(Exception.class, () ->
                    mockMvc.perform(delete("/api/admin/additions/{id}", ADDITION_ID)));
        }
    }

    // =========================================================================
    // GET /api/admin/additions/search
    // =========================================================================

    @Nested
    class SearchByNameTests {

        @Test
        void search_conNombre_retorna200() throws Exception {
            PageResponse<Addition> page = new PageResponse<>(List.of(), 0, 10, 0L, 0);
            when(searchAdditionByName.handle(eq("queso"), any(PaginationRequest.class))).thenReturn(page);

            mockMvc.perform(get("/api/admin/additions/search").param("name", "queso"))
                    .andExpect(status().isOk());

            verify(searchAdditionByName).handle(eq("queso"), any(PaginationRequest.class));
        }

        @Test
        void search_paginacionPersonalizada_pasaParametrosCorrectos() throws Exception {
            PageResponse<Addition> page = new PageResponse<>(List.of(), 0, 5, 0L, 0);
            when(searchAdditionByName.handle(eq("papa"), any(PaginationRequest.class))).thenReturn(page);

            mockMvc.perform(get("/api/admin/additions/search")
                            .param("name", "papa")
                            .param("page", "0")
                            .param("size", "5"))
                    .andExpect(status().isOk());

            verify(searchAdditionByName).handle(eq("papa"), any(PaginationRequest.class));
        }
    }
}