package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition.AdditionResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition.CreateAdditionRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition.UpdateAdditionRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.AdditionRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdditionController.class)
@DisplayName("AdditionController - Pruebas Unitarias")
class AdditionControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private CreateAddition createAddition;
    @MockBean private UpdateAddition updateAddition;
    @MockBean private UpdateAdditionImage updateAdditionImage;
    @MockBean private ListAddition listAddition;
    @MockBean private SearchAdditionByName searchAdditionByName;
    @MockBean private GetAdditionById getAdditionById;
    @MockBean private DeleteAddition deleteAddition;
    @MockBean private AdditionRestDtoMapper mapper;

    private Addition mockAddition;
    private AdditionResponseDTO mockResponseDTO;
    private CustomUserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        mockAddition = mock(Addition.class);
        when(mockAddition.getIdAddition()).thenReturn(1);
        when(mockAddition.getName()).thenReturn("Queso Extra");

        mockResponseDTO = new AdditionResponseDTO(1, "Queso Extra", 1500.0, true, null);

        mockUserDetails = mock(CustomUserDetails.class);
        when(mockUserDetails.getId()).thenReturn(10);
        when(mockUserDetails.getUsername()).thenReturn("admin@test.com");
    }

    // ── POST /api/admin/additions ────────────────────────────────────
    @Nested
    @DisplayName("POST /api/admin/additions - Crear adición")
    class CreateAdditionTests {

        @Test
        @DisplayName("Debe crear adición con imagen y retornar 201")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldCreateAdditionWithImageAndReturn201() throws Exception {
            CreateAdditionRequestDTO dto = new CreateAdditionRequestDTO("Queso Extra", 1500.0, true);
            MockMultipartFile dataPart = new MockMultipartFile(
                    "data", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(dto)
            );
            MockMultipartFile imagePart = new MockMultipartFile(
                    "additionImage", "queso.png", MediaType.IMAGE_PNG_VALUE, "img".getBytes()
            );

            when(mapper.toCreateAdditionCommand(any(), any(), anyInt())).thenReturn(mock());
            when(createAddition.handle(any())).thenReturn(mockAddition);
            when(mapper.toAdditionResponseDTO(mockAddition, true)).thenReturn(mockResponseDTO);

            mockMvc.perform(multipart("/api/admin/additions")
                            .file(dataPart).file(imagePart)
                            .with(csrf()).with(user(mockUserDetails)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idAddition").value(1))
                    .andExpect(jsonPath("$.name").value("Queso Extra"));

            verify(createAddition).handle(any());
            verify(mapper).toAdditionResponseDTO(mockAddition, true);
        }

        @Test
        @DisplayName("Debe crear adición sin imagen — imageWasSent=false")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldCreateAdditionWithoutImage() throws Exception {
            CreateAdditionRequestDTO dto = new CreateAdditionRequestDTO("Queso Extra", 1500.0, true);
            MockMultipartFile dataPart = new MockMultipartFile(
                    "data", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(dto)
            );

            when(mapper.toCreateAdditionCommand(any(), isNull(), anyInt())).thenReturn(mock());
            when(createAddition.handle(any())).thenReturn(mockAddition);
            when(mapper.toAdditionResponseDTO(mockAddition, false)).thenReturn(mockResponseDTO);

            mockMvc.perform(multipart("/api/admin/additions")
                            .file(dataPart)
                            .with(csrf()).with(user(mockUserDetails)))
                    .andExpect(status().isCreated());

            verify(mapper).toAdditionResponseDTO(mockAddition, false);
        }

        @Test
        @DisplayName("Debe retornar 403 si el usuario no tiene ROLE_ADMIN")
        @WithMockUser(authorities = "ROLE_USER")
        void shouldReturn403WhenNotAdmin() throws Exception {
            MockMultipartFile dataPart = new MockMultipartFile(
                    "data", "", MediaType.APPLICATION_JSON_VALUE, "{}".getBytes()
            );
            mockMvc.perform(multipart("/api/admin/additions")
                            .file(dataPart).with(csrf()))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(createAddition);
        }

        @Test
        @DisplayName("Debe retornar 401 si no está autenticado")
        void shouldReturn401WhenUnauthenticated() throws Exception {
            MockMultipartFile dataPart = new MockMultipartFile(
                    "data", "", MediaType.APPLICATION_JSON_VALUE, "{}".getBytes()
            );
            mockMvc.perform(multipart("/api/admin/additions")
                            .file(dataPart).with(csrf()))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(createAddition);
        }
    }

    // ── PATCH /api/admin/additions/{id} ─────────────────────────────
    @Nested
    @DisplayName("PATCH /api/admin/additions/{id} - Actualizar adición")
    class UpdateAdditionTests {

        @Test
        @DisplayName("Debe actualizar adición y retornar 200")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldUpdateAndReturn200() throws Exception {
            UpdateAdditionRequestDTO dto = new UpdateAdditionRequestDTO("Queso Doble", 2000.0, true);

            when(mapper.toUpdateAdditionCommand(eq(1), any(), anyInt())).thenReturn(mock());
            when(updateAddition.handle(any())).thenReturn(mockAddition);
            when(mapper.toAdditionResponseDTO(mockAddition, false)).thenReturn(mockResponseDTO);

            mockMvc.perform(patch("/api/admin/additions/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(csrf()).with(user(mockUserDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idAddition").value(1));

            verify(updateAddition).handle(any());
        }

        @Test
        @DisplayName("Debe retornar 403 si no tiene ROLE_ADMIN")
        @WithMockUser(authorities = "ROLE_USER")
        void shouldReturn403() throws Exception {
            UpdateAdditionRequestDTO dto = new UpdateAdditionRequestDTO("Queso Doble", 2000.0, true);

            mockMvc.perform(patch("/api/admin/additions/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(csrf()))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(updateAddition);
        }

        @Test
        @DisplayName("Debe retornar 400 si el body es inválido")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldReturn400WithInvalidBody() throws Exception {
            mockMvc.perform(patch("/api/admin/additions/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}")
                            .with(csrf()).with(user(mockUserDetails)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ── GET /api/admin/additions ─────────────────────────────────────
    @Nested
    @DisplayName("GET /api/admin/additions - Listar adiciones")
    class ListAdditionsTests {

        @Test
        @DisplayName("Debe retornar página con parámetros por defecto")
        void shouldReturnPagedWithDefaults() throws Exception {
            PageResponse<Addition> page = new PageResponse<>(List.of(mockAddition), 0, 10, 1L, 1);

            when(listAddition.handle(isNull(), any(PaginationRequest.class))).thenReturn(page);
            when(mapper.toAdditionResponseDTO(mockAddition, false)).thenReturn(mockResponseDTO);

            mockMvc.perform(get("/api/admin/additions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].idAddition").value(1))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1));

            verify(listAddition).handle(isNull(), any(PaginationRequest.class));
        }

        @Test
        @DisplayName("Debe filtrar por available=true")
        void shouldFilterByAvailable() throws Exception {
            PageResponse<Addition> page = new PageResponse<>(List.of(mockAddition), 0, 10, 1L, 1);

            when(listAddition.handle(eq(true), any(PaginationRequest.class))).thenReturn(page);
            when(mapper.toAdditionResponseDTO(mockAddition, false)).thenReturn(mockResponseDTO);

            mockMvc.perform(get("/api/admin/additions").param("available", "true"))
                    .andExpect(status().isOk());

            verify(listAddition).handle(eq(true), any(PaginationRequest.class));
        }

        @Test
        @DisplayName("Debe aplicar paginación personalizada")
        void shouldApplyCustomPagination() throws Exception {
            PageResponse<Addition> page = new PageResponse<>(List.of(), 2, 5, 0L, 0);

            when(listAddition.handle(isNull(), any(PaginationRequest.class))).thenReturn(page);

            mockMvc.perform(get("/api/admin/additions")
                            .param("page", "2").param("size", "5")
                            .param("sortBy", "name").param("sortDirection", "DESC"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(2))
                    .andExpect(jsonPath("$.size").value(5));
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay adiciones")
        void shouldReturnEmptyList() throws Exception {
            PageResponse<Addition> emptyPage = new PageResponse<>(List.of(), 0, 10, 0L, 0);

            when(listAddition.handle(isNull(), any(PaginationRequest.class))).thenReturn(emptyPage);

            mockMvc.perform(get("/api/admin/additions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }
    }

    // ── PUT /api/admin/additions/image/{id} ──────────────────────────
    @Nested
    @DisplayName("PUT /api/admin/additions/image/{id} - Actualizar imagen")
    class UpdateImageTests {

        @Test
        @DisplayName("Debe actualizar imagen y retornar 200")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldUpdateImageAndReturn200() throws Exception {
            MockMultipartFile imagePart = new MockMultipartFile(
                    "additionImage", "queso.png", MediaType.IMAGE_PNG_VALUE, "img".getBytes()
            );

            when(updateAdditionImage.handle(eq(1), any(FileData.class), anyInt())).thenReturn(mockAddition);
            when(mapper.toAdditionResponseDTO(mockAddition, true)).thenReturn(mockResponseDTO);

            mockMvc.perform(multipart("/api/admin/additions/image/1")
                            .file(imagePart)
                            .with(req -> { req.setMethod("PUT"); return req; })
                            .with(csrf()).with(user(mockUserDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idAddition").value(1));

            verify(updateAdditionImage).handle(eq(1), any(FileData.class), eq(10));
            verify(mapper).toAdditionResponseDTO(mockAddition, true);
        }

        @Test
        @DisplayName("Debe retornar 403 si no tiene ROLE_ADMIN")
        @WithMockUser(authorities = "ROLE_USER")
        void shouldReturn403() throws Exception {
            MockMultipartFile imagePart = new MockMultipartFile(
                    "additionImage", "queso.png", MediaType.IMAGE_PNG_VALUE, "img".getBytes()
            );

            mockMvc.perform(multipart("/api/admin/additions/image/1")
                            .file(imagePart)
                            .with(req -> { req.setMethod("PUT"); return req; })
                            .with(csrf()))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(updateAdditionImage);
        }

        @Test
        @DisplayName("Debe retornar 400 si no se envía imagen")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldReturn400WhenNoImage() throws Exception {
            mockMvc.perform(multipart("/api/admin/additions/image/1")
                            .with(req -> { req.setMethod("PUT"); return req; })
                            .with(csrf()).with(user(mockUserDetails)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ── DELETE /api/admin/additions/{id} ────────────────────────────
    @Nested
    @DisplayName("DELETE /api/admin/additions/{id} - Eliminar adición")
    class DeleteAdditionTests {

        @Test
        @DisplayName("Debe eliminar adición y retornar mensaje de éxito")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldDeleteAndReturnSuccessMessage() throws Exception {
            when(deleteAddition.handle(eq(1), anyInt())).thenReturn(mockAddition);

            mockMvc.perform(delete("/api/admin/additions/1")
                            .with(csrf()).with(user(mockUserDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Adición eliminada exitosamente"))
                    .andExpect(jsonPath("$.deletedResource.id").value(1))
                    .andExpect(jsonPath("$.deletedResource.name").value("Queso Extra"));

            verify(deleteAddition).handle(1, 10);
        }

        @Test
        @DisplayName("Debe retornar 403 si no tiene ROLE_ADMIN")
        @WithMockUser(authorities = "ROLE_USER")
        void shouldReturn403() throws Exception {
            mockMvc.perform(delete("/api/admin/additions/1").with(csrf()))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(deleteAddition);
        }

        @Test
        @DisplayName("Debe retornar 401 si no está autenticado")
        void shouldReturn401WhenUnauthenticated() throws Exception {
            mockMvc.perform(delete("/api/admin/additions/1").with(csrf()))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(deleteAddition);
        }
    }

    // ── GET /api/admin/additions/{id} ────────────────────────────────
    @Nested
    @DisplayName("GET /api/admin/additions/{id} - Obtener por ID")
    class GetByIdTests {

        @Test
        @DisplayName("Debe retornar adición por ID y retornar 200")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldReturnAdditionById() throws Exception {
            when(getAdditionById.execute(1)).thenReturn(mockAddition);
            when(mapper.toAdditionResponseDTO(mockAddition, false)).thenReturn(mockResponseDTO);

            mockMvc.perform(get("/api/admin/additions/1").with(user(mockUserDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idAddition").value(1))
                    .andExpect(jsonPath("$.name").value("Queso Extra"));

            verify(getAdditionById).execute(1);
        }

        @Test
        @DisplayName("Debe retornar 403 si no tiene ROLE_ADMIN")
        @WithMockUser(authorities = "ROLE_USER")
        void shouldReturn403() throws Exception {
            mockMvc.perform(get("/api/admin/additions/1"))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(getAdditionById);
        }

        @Test
        @DisplayName("Debe retornar 401 si no está autenticado")
        void shouldReturn401WhenUnauthenticated() throws Exception {
            mockMvc.perform(get("/api/admin/additions/1"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(getAdditionById);
        }
    }

    // ── GET /api/admin/additions/search ──────────────────────────────
    @Nested
    @DisplayName("GET /api/admin/additions/search - Buscar por nombre")
    class SearchByNameTests {

        @Test
        @DisplayName("Debe buscar adiciones por nombre y retornar 200")
        void shouldSearchByNameAndReturn200() throws Exception {
            PageResponse<Addition> page = new PageResponse<>(List.of(mockAddition), 0, 10, 1L, 1);

            when(searchAdditionByName.handle(eq("Queso"), any(PaginationRequest.class))).thenReturn(page);
            when(mapper.toAdditionResponseDTO(mockAddition, false)).thenReturn(mockResponseDTO);

            mockMvc.perform(get("/api/admin/additions/search").param("name", "Queso"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].idAddition").value(1))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(searchAdditionByName).handle(eq("Queso"), any(PaginationRequest.class));
        }

        @Test
        @DisplayName("Debe retornar 400 si falta el parámetro name")
        void shouldReturn400WhenNameMissing() throws Exception {
            mockMvc.perform(get("/api/admin/additions/search"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(searchAdditionByName);
        }

        @Test
        @DisplayName("Debe paginar resultados de búsqueda")
        void shouldPaginateResults() throws Exception {
            PageResponse<Addition> page = new PageResponse<>(List.of(), 1, 5, 0L, 0);

            when(searchAdditionByName.handle(eq("Tomate"), any(PaginationRequest.class))).thenReturn(page);

            mockMvc.perform(get("/api/admin/additions/search")
                            .param("name", "Tomate")
                            .param("page", "1").param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(1))
                    .andExpect(jsonPath("$.size").value(5));
        }

        @Test
        @DisplayName("Debe retornar página vacía si no hay coincidencias")
        void shouldReturnEmptyWhenNoMatches() throws Exception {
            PageResponse<Addition> emptyPage = new PageResponse<>(List.of(), 0, 10, 0L, 0);

            when(searchAdditionByName.handle(eq("XYZ123"), any(PaginationRequest.class))).thenReturn(emptyPage);

            mockMvc.perform(get("/api/admin/additions/search").param("name", "XYZ123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }
    }
}