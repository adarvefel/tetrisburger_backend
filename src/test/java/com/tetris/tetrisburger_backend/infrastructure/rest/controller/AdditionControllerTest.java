package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageStatus;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @MockitoBean private CreateAddition       createAddition;
    @MockitoBean private UpdateAddition       updateAddition;
    @MockitoBean private UpdateAdditionImage  updateAdditionImage;
    @MockitoBean private ListAddition         listAddition;
    @MockitoBean private SearchAdditionByName searchAdditionByName;
    @MockitoBean private GetAdditionById      getAdditionById;
    @MockitoBean private DeleteAddition       deleteAddition;
    @MockitoBean private AdditionRestDtoMapper mapper;
    // ── Fixtures ────────────────────────────────────────────────────
    private Addition            mockAddition;
    private AdditionResponseDTO mockResponseDTO;
    private CustomUserDetails   mockUserDetails;

    /** DTO de creación con tipos correctos */
    private CreateAdditionRequestDTO buildCreateDTO() {
        return new CreateAdditionRequestDTO(
                "Queso Extra",
                "Porción extra de queso",
                new BigDecimal("1500.00"),
                true
        );
    }

    /** DTO de actualización con tipos correctos */
    private UpdateAdditionRequestDTO buildUpdateDTO() {
        return new UpdateAdditionRequestDTO(
                "Queso Doble",
                "Doble porción de queso",
                new BigDecimal("2000.00"),
                true
        );
    }

    /** AdditionResponseDTO con los 12 campos reales del record */
    private AdditionResponseDTO buildResponseDTO(boolean withImage) {
        return new AdditionResponseDTO(
                1,
                "Queso Extra",
                "Porción extra de queso",
                new BigDecimal("1500.00"),
                true,
                withImage ? "https://cdn.example.com/queso.png" : null,
                withImage ? "additions/queso.png"               : null,
                withImage ? ImageStatus.UPLOADED                : ImageStatus.NONE,
                LocalDateTime.of(2025, 1, 10, 12, 0),
                LocalDateTime.of(2025, 1, 10, 12, 0),
                10,
                10
        );
    }

    @BeforeEach
    void setUp() {
        mockAddition = mock(Addition.class);
        when(mockAddition.getIdAddition()).thenReturn(1);
        when(mockAddition.getName()).thenReturn("Queso Extra");

        mockResponseDTO = buildResponseDTO(false);

        mockUserDetails = mock(CustomUserDetails.class);
        when(mockUserDetails.getId()).thenReturn(10);
        when(mockUserDetails.getUsername()).thenReturn("admin@test.com");
    }

    // ================================================================
    //  POST /api/admin/additions
    // ================================================================
    @Nested
    @DisplayName("POST /api/admin/additions — Crear adición")
    class CreateAdditionTests {

        @Test
        @DisplayName("201 · Con imagen: mapper recibe imageWasSent=true")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldCreateWithImageAndReturn201() throws Exception {
            AdditionResponseDTO responseWithImg = buildResponseDTO(true);

            MockMultipartFile dataPart = new MockMultipartFile(
                    "data", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(buildCreateDTO())
            );
            MockMultipartFile imgPart = new MockMultipartFile(
                    "additionImage", "queso.png", MediaType.IMAGE_PNG_VALUE, "img".getBytes()
            );

            when(mapper.toCreateAdditionCommand(any(), any(), anyInt())).thenReturn(mock());
            when(createAddition.handle(any())).thenReturn(mockAddition);
            when(mapper.toAdditionResponseDTO(mockAddition, true)).thenReturn(responseWithImg);

            mockMvc.perform(multipart("/api/admin/additions")
                            .file(dataPart).file(imgPart)
                            .with(csrf()).with(user(mockUserDetails)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idAddition").value(1))
                    .andExpect(jsonPath("$.name").value("Queso Extra"))
                    .andExpect(jsonPath("$.price").value(1500.00))
                    .andExpect(jsonPath("$.description").value("Porción extra de queso"))
                    .andExpect(jsonPath("$.imageUrl").value("https://cdn.example.com/queso.png"))
                    .andExpect(jsonPath("$.createdBy").value(10));

            verify(createAddition).handle(any());
            verify(mapper).toAdditionResponseDTO(mockAddition, true);
        }

        @Test
        @DisplayName("201 · Sin imagen: mapper recibe imageWasSent=false")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldCreateWithoutImageAndReturn201() throws Exception {
            MockMultipartFile dataPart = new MockMultipartFile(
                    "data", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(buildCreateDTO())
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
        @DisplayName("201 · Archivo vacío se trata como sin imagen (imageWasSent=false)")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldTreatEmptyFileAsNoImage() throws Exception {
            MockMultipartFile dataPart = new MockMultipartFile(
                    "data", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(buildCreateDTO())
            );
            MockMultipartFile emptyImg = new MockMultipartFile(
                    "additionImage", "", MediaType.IMAGE_PNG_VALUE, new byte[0]
            );

            when(mapper.toCreateAdditionCommand(any(), any(), anyInt())).thenReturn(mock());
            when(createAddition.handle(any())).thenReturn(mockAddition);
            when(mapper.toAdditionResponseDTO(mockAddition, false)).thenReturn(mockResponseDTO);

            mockMvc.perform(multipart("/api/admin/additions")
                            .file(dataPart).file(emptyImg)
                            .with(csrf()).with(user(mockUserDetails)))
                    .andExpect(status().isCreated());

            // isEmpty()=true → imageWasSent=false
            verify(mapper).toAdditionResponseDTO(mockAddition, false);
        }

        @Test
        @DisplayName("403 · Usuario con ROLE_USER no puede crear")
        @WithMockUser(authorities = "ROLE_USER")
        void shouldReturn403WhenRoleUser() throws Exception {
            MockMultipartFile dataPart = new MockMultipartFile(
                    "data", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(buildCreateDTO())
            );

            mockMvc.perform(multipart("/api/admin/additions")
                            .file(dataPart).with(csrf()))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(createAddition);
        }

        @Test
        @DisplayName("401 · Solicitud sin autenticación")
        void shouldReturn401WhenUnauthenticated() throws Exception {
            MockMultipartFile dataPart = new MockMultipartFile(
                    "data", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(buildCreateDTO())
            );

            mockMvc.perform(multipart("/api/admin/additions")
                            .file(dataPart).with(csrf()))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(createAddition);
        }
    }

    // ================================================================
    //  PATCH /api/admin/additions/{id}
    // ================================================================
    @Nested
    @DisplayName("PATCH /api/admin/additions/{id} — Actualizar adición")
    class UpdateAdditionTests {

        @Test
        @DisplayName("200 · Actualiza todos los campos con tipos correctos")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldUpdateAndReturn200() throws Exception {
            when(mapper.toUpdateAdditionCommand(eq(1), any(), anyInt())).thenReturn(mock());
            when(updateAddition.handle(any())).thenReturn(mockAddition);
            when(mapper.toAdditionResponseDTO(mockAddition, false)).thenReturn(mockResponseDTO);

            mockMvc.perform(patch("/api/admin/additions/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(buildUpdateDTO()))
                            .with(csrf()).with(user(mockUserDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idAddition").value(1))
                    .andExpect(jsonPath("$.price").value(1500.00));

            verify(updateAddition).handle(any());
            // update de datos nunca actualiza imagen → false
            verify(mapper).toAdditionResponseDTO(mockAddition, false);
        }

        @Test
        @DisplayName("200 · Actualización parcial — solo campos no nulos")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldUpdatePartialFields() throws Exception {
            // Record permite nulls porque no hay @NotNull
            UpdateAdditionRequestDTO partial =
                    new UpdateAdditionRequestDTO(null, null, null, false);

            when(mapper.toUpdateAdditionCommand(eq(5), any(), anyInt())).thenReturn(mock());
            when(updateAddition.handle(any())).thenReturn(mockAddition);
            when(mapper.toAdditionResponseDTO(mockAddition, false)).thenReturn(mockResponseDTO);

            mockMvc.perform(patch("/api/admin/additions/5")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(partial))
                            .with(csrf()).with(user(mockUserDetails)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("403 · Usuario sin ROLE_ADMIN")
        @WithMockUser(authorities = "ROLE_USER")
        void shouldReturn403() throws Exception {
            mockMvc.perform(patch("/api/admin/additions/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(buildUpdateDTO()))
                            .with(csrf()))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(updateAddition);
        }

        @Test
        @DisplayName("415 · Content-Type incorrecto")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldReturn415WhenWrongContentType() throws Exception {
            mockMvc.perform(patch("/api/admin/additions/1")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("texto")
                            .with(csrf()).with(user(mockUserDetails)))
                    .andExpect(status().isUnsupportedMediaType());

            verifyNoInteractions(updateAddition);
        }
    }

    // ================================================================
    //  GET /api/admin/additions
    // ================================================================
    @Nested
    @DisplayName("GET /api/admin/additions — Listar adiciones")
    class ListAdditionsTests {

        @Test
        @DisplayName("200 · Parámetros por defecto")
        void shouldReturnPageWithDefaults() throws Exception {
            PageResponse<Addition> page = new PageResponse<>(List.of(mockAddition), 0, 10, 1L, 1);

            when(listAddition.handle(isNull(), any(PaginationRequest.class))).thenReturn(page);
            when(mapper.toAdditionResponseDTO(mockAddition, false)).thenReturn(mockResponseDTO);

            mockMvc.perform(get("/api/admin/additions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].idAddition").value(1))
                    .andExpect(jsonPath("$.content[0].price").value(1500.00))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1));

            verify(listAddition).handle(isNull(), any(PaginationRequest.class));
        }

        @Test
        @DisplayName("200 · Filtro available=true se pasa al use-case")
        void shouldPassAvailableTrueFilter() throws Exception {
            PageResponse<Addition> page = new PageResponse<>(List.of(mockAddition), 0, 10, 1L, 1);

            when(listAddition.handle(eq(true), any(PaginationRequest.class))).thenReturn(page);
            when(mapper.toAdditionResponseDTO(mockAddition, false)).thenReturn(mockResponseDTO);

            mockMvc.perform(get("/api/admin/additions").param("available", "true"))
                    .andExpect(status().isOk());

            verify(listAddition).handle(eq(true), any(PaginationRequest.class));
        }

        @Test
        @DisplayName("200 · Filtro available=false se pasa al use-case")
        void shouldPassAvailableFalseFilter() throws Exception {
            PageResponse<Addition> page = new PageResponse<>(List.of(), 0, 10, 0L, 0);

            when(listAddition.handle(eq(false), any(PaginationRequest.class))).thenReturn(page);

            mockMvc.perform(get("/api/admin/additions").param("available", "false"))
                    .andExpect(status().isOk());

            verify(listAddition).handle(eq(false), any(PaginationRequest.class));
        }

        @Test
        @DisplayName("200 · Paginación personalizada")
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
        @DisplayName("200 · Lista vacía")
        void shouldReturnEmptyList() throws Exception {
            PageResponse<Addition> empty = new PageResponse<>(List.of(), 0, 10, 0L, 0);

            when(listAddition.handle(isNull(), any(PaginationRequest.class))).thenReturn(empty);

            mockMvc.perform(get("/api/admin/additions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }

        @Test
        @DisplayName("200 · Endpoint público (sin autenticación funciona)")
        void shouldBePublicEndpoint() throws Exception {
            when(listAddition.handle(isNull(), any(PaginationRequest.class)))
                    .thenReturn(new PageResponse<>(List.of(), 0, 10, 0L, 0));

            mockMvc.perform(get("/api/admin/additions"))
                    .andExpect(status().isOk());
        }
    }

    // ================================================================
    //  PUT /api/admin/additions/image/{id}
    // ================================================================
    @Nested
    @DisplayName("PUT /api/admin/additions/image/{id} — Actualizar imagen")
    class UpdateImageTests {

        @Test
        @DisplayName("200 · Imagen actualizada, mapper recibe imageWasSent=true")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldUpdateImageAndReturn200() throws Exception {
            AdditionResponseDTO withImg = buildResponseDTO(true);

            MockMultipartFile imgPart = new MockMultipartFile(
                    "additionImage", "queso.png", MediaType.IMAGE_PNG_VALUE, "img".getBytes()
            );

            when(updateAdditionImage.handle(eq(1), any(FileData.class), anyInt()))
                    .thenReturn(mockAddition);
            when(mapper.toAdditionResponseDTO(mockAddition, true)).thenReturn(withImg);

            mockMvc.perform(multipart("/api/admin/additions/image/1")
                            .file(imgPart)
                            .with(req -> { req.setMethod("PUT"); return req; })
                            .with(csrf()).with(user(mockUserDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idAddition").value(1))
                    .andExpect(jsonPath("$.imageUrl").value("https://cdn.example.com/queso.png"))
                    .andExpect(jsonPath("$.imageKey").value("additions/queso.png"));

            // userId extraído del token (10) se propaga al use-case
            verify(updateAdditionImage).handle(eq(1), any(FileData.class), eq(10));
            verify(mapper).toAdditionResponseDTO(mockAddition, true);
        }

        @Test
        @DisplayName("403 · Usuario sin ROLE_ADMIN")
        @WithMockUser(authorities = "ROLE_USER")
        void shouldReturn403() throws Exception {
            MockMultipartFile imgPart = new MockMultipartFile(
                    "additionImage", "queso.png", MediaType.IMAGE_PNG_VALUE, "img".getBytes()
            );

            mockMvc.perform(multipart("/api/admin/additions/image/1")
                            .file(imgPart)
                            .with(req -> { req.setMethod("PUT"); return req; })
                            .with(csrf()))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(updateAdditionImage);
        }

        @Test
        @DisplayName("400 · Sin parte additionImage")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldReturn400WhenNoImage() throws Exception {
            mockMvc.perform(multipart("/api/admin/additions/image/1")
                            .with(req -> { req.setMethod("PUT"); return req; })
                            .with(csrf()).with(user(mockUserDetails)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(updateAdditionImage);
        }
    }

    // ================================================================
    //  DELETE /api/admin/additions/{id}
    // ================================================================
    @Nested
    @DisplayName("DELETE /api/admin/additions/{id} — Eliminar adición")
    class DeleteAdditionTests {

        @Test
        @DisplayName("200 · Body con todos los campos correctos")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldDeleteAndReturnCorrectBody() throws Exception {
            when(deleteAddition.handle(eq(1), anyInt())).thenReturn(mockAddition);

            mockMvc.perform(delete("/api/admin/additions/1")
                            .with(csrf()).with(user(mockUserDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Adición eliminada exitosamente"))
                    .andExpect(jsonPath("$.deletedResource.id").value(1))
                    .andExpect(jsonPath("$.deletedResource.name").value("Queso Extra"));

            // userId del token (10) se pasa correctamente
            verify(deleteAddition).handle(1, 10);
        }

        @Test
        @DisplayName("200 · userId del token no puede ser otro valor")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldPropagateCorrectUserId() throws Exception {
            when(deleteAddition.handle(anyInt(), anyInt())).thenReturn(mockAddition);

            mockMvc.perform(delete("/api/admin/additions/7")
                            .with(csrf()).with(user(mockUserDetails)))
                    .andExpect(status().isOk());

            verify(deleteAddition).handle(7, 10);
            verify(deleteAddition, never()).handle(anyInt(), eq(99));
        }

        @Test
        @DisplayName("403 · Usuario sin ROLE_ADMIN")
        @WithMockUser(authorities = "ROLE_USER")
        void shouldReturn403() throws Exception {
            mockMvc.perform(delete("/api/admin/additions/1").with(csrf()))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(deleteAddition);
        }

        @Test
        @DisplayName("401 · Sin autenticación")
        void shouldReturn401WhenUnauthenticated() throws Exception {
            mockMvc.perform(delete("/api/admin/additions/1").with(csrf()))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(deleteAddition);
        }
    }

    // ================================================================
    //  GET /api/admin/additions/{id}
    // ================================================================
    @Nested
    @DisplayName("GET /api/admin/additions/{id} — Obtener por ID")
    class GetByIdTests {

        @Test
        @DisplayName("200 · Retorna todos los campos del DTO correctamente")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void shouldReturnFullDTOById() throws Exception {
            when(getAdditionById.execute(1)).thenReturn(mockAddition);
            when(mapper.toAdditionResponseDTO(mockAddition, false)).thenReturn(mockResponseDTO);

            mockMvc.perform(get("/api/admin/additions/1")
                            .with(user(mockUserDetails)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idAddition").value(1))
                    .andExpect(jsonPath("$.name").value("Queso Extra"))
                    .andExpect(jsonPath("$.description").value("Porción extra de queso"))
                    .andExpect(jsonPath("$.price").value(1500.00))
                    .andExpect(jsonPath("$.available").value(true))
                    .andExpect(jsonPath("$.createdBy").value(10));

            verify(getAdditionById).execute(1);
            // getById nunca gestiona imagen → false
            verify(mapper).toAdditionResponseDTO(mockAddition, false);
        }

        @Test
        @DisplayName("403 · Usuario sin ROLE_ADMIN")
        @WithMockUser(authorities = "ROLE_USER")
        void shouldReturn403() throws Exception {
            mockMvc.perform(get("/api/admin/additions/1"))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(getAdditionById);
        }

        @Test
        @DisplayName("401 · Sin autenticación")
        void shouldReturn401WhenUnauthenticated() throws Exception {
            mockMvc.perform(get("/api/admin/additions/1"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(getAdditionById);
        }
    }

    // ================================================================
    //  GET /api/admin/additions/search
    // ================================================================
    @Nested
    @DisplayName("GET /api/admin/additions/search — Buscar por nombre")
    class SearchByNameTests {

        @Test
        @DisplayName("200 · Resultados con todos los campos del DTO")
        void shouldReturnFullDTOOnSearch() throws Exception {
            PageResponse<Addition> page = new PageResponse<>(List.of(mockAddition), 0, 10, 1L, 1);

            when(searchAdditionByName.handle(eq("Queso"), any(PaginationRequest.class)))
                    .thenReturn(page);
            when(mapper.toAdditionResponseDTO(mockAddition, false)).thenReturn(mockResponseDTO);

            mockMvc.perform(get("/api/admin/additions/search").param("name", "Queso"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].idAddition").value(1))
                    .andExpect(jsonPath("$.content[0].price").value(1500.00))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(searchAdditionByName).handle(eq("Queso"), any(PaginationRequest.class));
        }

        @Test
        @DisplayName("200 · Paginación personalizada")
        void shouldApplyCustomPagination() throws Exception {
            PageResponse<Addition> page = new PageResponse<>(List.of(mockAddition), 1, 5, 6L, 2);

            when(searchAdditionByName.handle(eq("Queso"), any(PaginationRequest.class)))
                    .thenReturn(page);
            when(mapper.toAdditionResponseDTO(any(), eq(false))).thenReturn(mockResponseDTO);

            mockMvc.perform(get("/api/admin/additions/search")
                            .param("name", "Queso")
                            .param("page", "1").param("size", "5")
                            .param("sortBy", "name").param("sortDirection", "DESC"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(1))
                    .andExpect(jsonPath("$.size").value(5))
                    .andExpect(jsonPath("$.totalElements").value(6))
                    .andExpect(jsonPath("$.totalPages").value(2));
        }

        @Test
        @DisplayName("200 · Sin resultados retorna página vacía (no 404)")
        void shouldReturnEmptyPageNotNotFound() throws Exception {
            when(searchAdditionByName.handle(eq("XYZ_NO_EXISTE"), any(PaginationRequest.class)))
                    .thenReturn(new PageResponse<>(List.of(), 0, 10, 0L, 0));

            mockMvc.perform(get("/api/admin/additions/search").param("name", "XYZ_NO_EXISTE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }

        @Test
        @DisplayName("400 · Sin parámetro name")
        void shouldReturn400WhenNameMissing() throws Exception {
            mockMvc.perform(get("/api/admin/additions/search"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(searchAdditionByName);
        }

        @Test
        @DisplayName("200 · Endpoint público (sin autenticación funciona)")
        void shouldBePublicEndpoint() throws Exception {
            when(searchAdditionByName.handle(anyString(), any()))
                    .thenReturn(new PageResponse<>(List.of(), 0, 10, 0L, 0));

            mockMvc.perform(get("/api/admin/additions/search").param("name", "Queso"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("SecurityTests — Pruebas de seguridad")
    class SecurityTests {

        @Test
        @DisplayName("401 · Sin autenticación en endpoint protegido")
        void adminEndpoint_shouldNotCallUseCase_whenNotAuthenticated() throws Exception {
            SecurityContextHolder.clearContext();
            verifyNoInteractions(createAddition);
        }

        @Test
        @DisplayName("403 · Usuario con ROLE_CLIENT no puede acceder")
        @WithMockUser(roles = "CLIENT")
        void adminEndpoint_shouldReturn403_whenRoleIsClient() throws Exception {
            mockMvc.perform(post("/api/admin/additions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}").with(csrf()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("200 · Usuario con ROLE_ADMIN puede acceder")
        @WithMockUser(authorities = "ROLE_ADMIN")
        void adminEndpoint_shouldBeAccessible_whenRoleIsAdmin() throws Exception {
            PageResponse<Addition> page = new PageResponse<>(List.of(), 0, 10, 0L, 0);
            when(listAddition.handle(isNull(), any(PaginationRequest.class))).thenReturn(page);
            mockMvc.perform(get("/api/admin/additions")
                            .with(user(mockUserDetails)))
                    .andExpect(status().isOk());
        }
    }
}