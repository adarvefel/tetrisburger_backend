package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.MenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menucategory.CreateMenuCategoryRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menucategory.MenuCategoryResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menucategory.UpdateMenuCategoryRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.MenuCategoryDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MenuCategoryController — Pruebas Unitarias")
class MenuCategoryControllerTest {

    private MockMvc      mockMvc;
    private ObjectMapper objectMapper;

    @Mock private CreateMenuCategory    createMenuCategory;
    @Mock private UpdateMenuCategory    updateMenuCategory;
    @Mock private DeleteMenuCategory    deleteMenuCategory;
    @Mock private GetMenuCategoryById   getMenuCategoryById;
    @Mock private ListMenuCategory      listMenuCategory;
    @Mock private MenuCategoryDtoMapper menuCategoryDtoMapper;

    @InjectMocks private MenuCategoryController controller;

    private static final Integer CATEGORY_ID = 3;
    private static final Integer USER_ID     = 10;

    private MenuCategory            mockCategory;
    private MenuCategoryResponseDTO mockResponseDTO;
    private CustomUserDetails       mockUserDetails;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockCategory    = mock(MenuCategory.class);
        mockResponseDTO = mock(MenuCategoryResponseDTO.class);
        mockUserDetails = mock(CustomUserDetails.class);

        // lenient: no todos los tests consumen estos stubs
        lenient().when(mockCategory.getIdMenuCategory()).thenReturn(CATEGORY_ID);
        lenient().when(mockCategory.getMenuCategoryName()).thenReturn("Categoría Test");
        lenient().when(mockUserDetails.getId()).thenReturn(USER_ID);

        // Inyecta el principal en el SecurityContext para @AuthenticationPrincipal
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUserDetails, null, List.of())
        );
        SecurityContextHolder.setContext(ctx);

        // CRÍTICO: registrar AuthenticationPrincipalArgumentResolver para que
        // standaloneSetup sepa resolver @AuthenticationPrincipal correctamente
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private String validCreateBody() throws Exception {
        return objectMapper.writeValueAsString(
                new CreateMenuCategoryRequestDTO("Categoría Test", "Descripción de prueba")
        );
    }

    private String validUpdateBody() throws Exception {
        return objectMapper.writeValueAsString(
                new UpdateMenuCategoryRequestDTO("Categoría Actualizada", "Descripción actualizada")
        );
    }

    private String nullFieldsCreateBody() throws Exception {
        return objectMapper.writeValueAsString(
                new CreateMenuCategoryRequestDTO(null, null)
        );
    }

    // =========================================================================
    //  POST /api/menu-category
    // =========================================================================
    @Nested
    @DisplayName("POST /api/menu-category — Crear")
    class CreateMenuCategoryTests {

        @Test
        @DisplayName("201 · crea categoría con ambos campos y retorna DTO")
        void shouldReturn201_onCreate() throws Exception {
            when(menuCategoryDtoMapper.toCreateCommand(any(), eq(USER_ID))).thenReturn(mock());
            when(createMenuCategory.create(any())).thenReturn(mockCategory);
            when(menuCategoryDtoMapper.toResponseDTO(mockCategory)).thenReturn(mockResponseDTO);

            mockMvc.perform(post("/api/menu-category")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validCreateBody()))
                    .andExpect(status().isCreated());

            verify(createMenuCategory).create(any());
            verify(menuCategoryDtoMapper).toResponseDTO(mockCategory);
        }

        @Test
        @DisplayName("201 · userId del SecurityContext se propaga al comando de creación")
        void shouldPassUserId_toCreateCommand() throws Exception {
            when(menuCategoryDtoMapper.toCreateCommand(any(), eq(USER_ID))).thenReturn(mock());
            when(createMenuCategory.create(any())).thenReturn(mockCategory);
            when(menuCategoryDtoMapper.toResponseDTO(any())).thenReturn(mockResponseDTO);

            mockMvc.perform(post("/api/menu-category")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validCreateBody()))
                    .andExpect(status().isCreated());

            verify(menuCategoryDtoMapper).toCreateCommand(any(), eq(USER_ID));
        }

        @Test
        @DisplayName("201 · DTO sin @NotBlank acepta campos null")
        void shouldReturn201_whenFieldsAreNull() throws Exception {
            when(menuCategoryDtoMapper.toCreateCommand(any(), eq(USER_ID))).thenReturn(mock());
            when(createMenuCategory.create(any())).thenReturn(mockCategory);
            when(menuCategoryDtoMapper.toResponseDTO(any())).thenReturn(mockResponseDTO);

            mockMvc.perform(post("/api/menu-category")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(nullFieldsCreateBody()))
                    .andExpect(status().isCreated());

            verify(createMenuCategory).create(any());
        }

        @Test
        @DisplayName("415 · Content-Type incorrecto retorna 415")
        void shouldReturn415_whenWrongContentType() throws Exception {
            mockMvc.perform(post("/api/menu-category")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("texto plano"))
                    .andExpect(status().isUnsupportedMediaType());

            verifyNoInteractions(createMenuCategory);
        }

        @Test
        @DisplayName("201 · use case se invoca exactamente una vez")
        void shouldCallUseCase_exactlyOnce() throws Exception {
            when(menuCategoryDtoMapper.toCreateCommand(any(), eq(USER_ID))).thenReturn(mock());
            when(createMenuCategory.create(any())).thenReturn(mockCategory);
            when(menuCategoryDtoMapper.toResponseDTO(any())).thenReturn(mockResponseDTO);

            mockMvc.perform(post("/api/menu-category")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validCreateBody()))
                    .andExpect(status().isCreated());

            verify(createMenuCategory, times(1)).create(any());
        }
    }

    // =========================================================================
    //  PUT /api/menu-category/{id}
    // =========================================================================
    @Nested
    @DisplayName("PUT /api/menu-category/{id} — Actualizar")
    class UpdateMenuCategoryTests {

        @Test
        @DisplayName("200 · actualiza y retorna DTO")
        void shouldReturn200_onUpdate() throws Exception {
            when(menuCategoryDtoMapper.toUpdateCommand(any(), eq(USER_ID))).thenReturn(mock());
            when(updateMenuCategory.handle(eq(CATEGORY_ID), any())).thenReturn(mockCategory);
            when(menuCategoryDtoMapper.toResponseDTO(mockCategory)).thenReturn(mockResponseDTO);

            mockMvc.perform(put("/api/menu-category/{id}", CATEGORY_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validUpdateBody()))
                    .andExpect(status().isOk());

            verify(updateMenuCategory).handle(eq(CATEGORY_ID), any());
        }

        @Test
        @DisplayName("200 · ID del path y userId del contexto se pasan correctamente")
        void shouldPassIdAndUserId_toUseCase() throws Exception {
            when(menuCategoryDtoMapper.toUpdateCommand(any(), eq(USER_ID))).thenReturn(mock());
            when(updateMenuCategory.handle(eq(CATEGORY_ID), any())).thenReturn(mockCategory);
            when(menuCategoryDtoMapper.toResponseDTO(any())).thenReturn(mockResponseDTO);

            mockMvc.perform(put("/api/menu-category/{id}", CATEGORY_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validUpdateBody()))
                    .andExpect(status().isOk());

            verify(menuCategoryDtoMapper).toUpdateCommand(any(), eq(USER_ID));
            verify(updateMenuCategory).handle(eq(CATEGORY_ID), any());
        }

        @Test
        @DisplayName("200 · ID diferente en el path se respeta sin confusión")
        void shouldPassDifferentId_correctly() throws Exception {
            int otherId = 99;
            when(menuCategoryDtoMapper.toUpdateCommand(any(), anyInt())).thenReturn(mock());
            when(updateMenuCategory.handle(eq(otherId), any())).thenReturn(mockCategory);
            when(menuCategoryDtoMapper.toResponseDTO(any())).thenReturn(mockResponseDTO);

            mockMvc.perform(put("/api/menu-category/{id}", otherId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validUpdateBody()))
                    .andExpect(status().isOk());

            verify(updateMenuCategory).handle(eq(otherId), any());
            verify(updateMenuCategory, never()).handle(eq(CATEGORY_ID), any());
        }

        @Test
        @DisplayName("200 · mapper.toResponseDTO recibe la categoría del use case")
        void shouldCallMapper_withUseCaseResult() throws Exception {
            when(menuCategoryDtoMapper.toUpdateCommand(any(), eq(USER_ID))).thenReturn(mock());
            when(updateMenuCategory.handle(eq(CATEGORY_ID), any())).thenReturn(mockCategory);
            when(menuCategoryDtoMapper.toResponseDTO(mockCategory)).thenReturn(mockResponseDTO);

            mockMvc.perform(put("/api/menu-category/{id}", CATEGORY_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validUpdateBody()))
                    .andExpect(status().isOk());

            verify(menuCategoryDtoMapper).toResponseDTO(mockCategory);
        }
    }

    // =========================================================================
    //  DELETE /api/menu-category/{id}
    // =========================================================================
    @Nested
    @DisplayName("DELETE /api/menu-category/{id} — Eliminar")
    class DeleteMenuCategoryTests {

        @Test
        @DisplayName("200 · elimina y retorna 200")
        void shouldReturn200_onDelete() throws Exception {
            when(deleteMenuCategory.handle(eq(CATEGORY_ID), eq(USER_ID))).thenReturn(mockCategory);

            mockMvc.perform(delete("/api/menu-category/{id}", CATEGORY_ID))
                    .andExpect(status().isOk());

            verify(deleteMenuCategory).handle(CATEGORY_ID, USER_ID);
        }

        @Test
        @DisplayName("200 · ID del path y userId del SecurityContext son los correctos")
        void shouldPassCorrectIdAndUserId() throws Exception {
            int targetId = 7;
            when(deleteMenuCategory.handle(eq(targetId), eq(USER_ID))).thenReturn(mockCategory);

            mockMvc.perform(delete("/api/menu-category/{id}", targetId))
                    .andExpect(status().isOk());

            verify(deleteMenuCategory).handle(targetId, USER_ID);
            verify(deleteMenuCategory, never()).handle(eq(CATEGORY_ID), anyInt());
        }

        @Test
        @DisplayName("200 · use case se invoca exactamente una vez")
        void shouldCallUseCase_exactlyOnce() throws Exception {
            when(deleteMenuCategory.handle(eq(CATEGORY_ID), eq(USER_ID))).thenReturn(mockCategory);

            mockMvc.perform(delete("/api/menu-category/{id}", CATEGORY_ID))
                    .andExpect(status().isOk());

            verify(deleteMenuCategory, times(1)).handle(CATEGORY_ID, USER_ID);
        }
    }

    // =========================================================================
    //  GET /api/menu-category/{id}
    // =========================================================================
    @Nested
    @DisplayName("GET /api/menu-category/{id} — Obtener por ID")
    class GetMenuCategoryByIdTests {

        @Test
        @DisplayName("200 · retorna DTO cuando la categoría existe")
        void shouldReturn200_whenFound() throws Exception {
            when(getMenuCategoryById.handle(CATEGORY_ID)).thenReturn(mockCategory);
            when(menuCategoryDtoMapper.toResponseDTO(mockCategory)).thenReturn(mockResponseDTO);

            mockMvc.perform(get("/api/menu-category/{id}", CATEGORY_ID))
                    .andExpect(status().isOk());

            verify(getMenuCategoryById).handle(CATEGORY_ID);
            verify(menuCategoryDtoMapper).toResponseDTO(mockCategory);
        }

        @Test
        @DisplayName("200 · ID del path se pasa directamente al use case")
        void shouldPassCorrectId_toUseCase() throws Exception {
            when(getMenuCategoryById.handle(CATEGORY_ID)).thenReturn(mockCategory);
            when(menuCategoryDtoMapper.toResponseDTO(mockCategory)).thenReturn(mockResponseDTO);

            mockMvc.perform(get("/api/menu-category/{id}", CATEGORY_ID))
                    .andExpect(status().isOk());

            verify(getMenuCategoryById).handle(CATEGORY_ID);
            verify(getMenuCategoryById, never()).handle(eq(99));
        }

        @Test
        @DisplayName("200 · mapper recibe exactamente el objeto retornado por el use case")
        void shouldCallMapper_withUseCaseResult() throws Exception {
            when(getMenuCategoryById.handle(CATEGORY_ID)).thenReturn(mockCategory);
            when(menuCategoryDtoMapper.toResponseDTO(mockCategory)).thenReturn(mockResponseDTO);

            mockMvc.perform(get("/api/menu-category/{id}", CATEGORY_ID))
                    .andExpect(status().isOk());

            verify(menuCategoryDtoMapper).toResponseDTO(mockCategory);
            verify(menuCategoryDtoMapper, never()).toResponseDTO(null);
        }
    }

    // =========================================================================
    //  GET /api/menu-categories
    // =========================================================================
    @Nested
    @DisplayName("GET /api/menu-categories — Listar")
    class ListMenuCategoriesTests {

        @Test
        @DisplayName("200 · parámetros por defecto page=0, size=12")
        void shouldReturn200_withDefaultPagination() throws Exception {
            PageResponse<MenuCategory> page =
                    new PageResponse<>(List.of(mockCategory), 0, 12, 1L, 1);

            when(listMenuCategory.handle(any(PaginationRequest.class))).thenReturn(page);
            when(menuCategoryDtoMapper.toResponseDTO(mockCategory)).thenReturn(mockResponseDTO);

            mockMvc.perform(get("/api/menu-categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1));

            verify(listMenuCategory).handle(
                    argThat(pr -> pr.getPage() == 0 && pr.getSize() == 12)
            );
        }

        @Test
        @DisplayName("200 · paginación y ordenamiento personalizados se propagan al use case")
        void shouldApplyCustomPagination() throws Exception {
            PageResponse<MenuCategory> page =
                    new PageResponse<>(List.of(), 2, 5, 0L, 0);
            when(listMenuCategory.handle(any(PaginationRequest.class))).thenReturn(page);

            mockMvc.perform(get("/api/menu-categories")
                            .param("page",      "2")
                            .param("size",      "5")
                            .param("sortBy",    "menuCategoryName")
                            .param("direction", "DESC"))
                    .andExpect(status().isOk());

            verify(listMenuCategory).handle(argThat(pr ->
                    pr.getPage() == 2
                            && pr.getSize() == 5
                            && "menuCategoryName".equals(pr.getSortBy())
                            && "DESC".equals(pr.getDirection())
            ));
        }

        @Test
        @DisplayName("200 · mapper se llama exactamente una vez por cada ítem")
        void shouldCallMapper_oncePerItem() throws Exception {
            MenuCategory cat1 = mock(MenuCategory.class);
            MenuCategory cat2 = mock(MenuCategory.class);
            PageResponse<MenuCategory> page =
                    new PageResponse<>(List.of(cat1, cat2), 0, 12, 2L, 1);

            when(listMenuCategory.handle(any(PaginationRequest.class))).thenReturn(page);
            when(menuCategoryDtoMapper.toResponseDTO(cat1)).thenReturn(mock(MenuCategoryResponseDTO.class));
            when(menuCategoryDtoMapper.toResponseDTO(cat2)).thenReturn(mock(MenuCategoryResponseDTO.class));

            mockMvc.perform(get("/api/menu-categories"))
                    .andExpect(status().isOk());

            verify(menuCategoryDtoMapper, times(1)).toResponseDTO(cat1);
            verify(menuCategoryDtoMapper, times(1)).toResponseDTO(cat2);
        }

        @Test
        @DisplayName("200 · lista vacía retorna 200 con content vacío (nunca 404)")
        void shouldReturn200_whenEmptyList() throws Exception {
            PageResponse<MenuCategory> empty =
                    new PageResponse<>(List.of(), 0, 12, 0L, 0);
            when(listMenuCategory.handle(any(PaginationRequest.class))).thenReturn(empty);

            mockMvc.perform(get("/api/menu-categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(0))
                    .andExpect(jsonPath("$.content").isEmpty());
        }

        @Test
        @DisplayName("200 · listMenuCategory se invoca exactamente una vez por request")
        void shouldCallUseCase_exactlyOnce() throws Exception {
            PageResponse<MenuCategory> page =
                    new PageResponse<>(List.of(), 0, 12, 0L, 0);
            when(listMenuCategory.handle(any(PaginationRequest.class))).thenReturn(page);

            mockMvc.perform(get("/api/menu-categories"))
                    .andExpect(status().isOk());

            verify(listMenuCategory, times(1)).handle(any(PaginationRequest.class));
        }
    }
}