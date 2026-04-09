package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.*;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.query.GetProductCategoryByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.query.ListProductCategoriesQuery;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.CreateProductCategoryRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.ListProductCategoryResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.ProductCategoryResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.UpdateProductCategoryRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.ProductCategoryRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductCategoryControllerTest {

    @Mock private CreateProductCategory createCategory;
    @Mock private UpdateProductCategory updateCategory;
    @Mock private DeleteProductCategory deleteCategory;
    @Mock private GetProductCategoryById getById;
    @Mock private ListProductCategories listCategories;
    @Mock private ListPublicProductCategories publicProductCategories;
    @Mock private ProductCategoryRestDtoMapper mapper;
    @Mock private CustomUserDetails mockUserDetails;

    @InjectMocks
    private ProductCategoryController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Integer USER_ID     = 42;
    private static final Integer CATEGORY_ID = 7;

    // ✅ Fix — campos @NotNull incluidos en todos los bodies
    private static final String VALID_CREATE_BODY =
            """
            {"name": "Bebidas", "available": true}
            """;

    private static final String VALID_UPDATE_BODY =
            """
            {"name": "Postres", "available": true}
            """;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        // Fix 1 — lenient porque GET públicos no usan el principal
        lenient().when(mockUserDetails.getId()).thenReturn(USER_ID);

        // Fix 2 — endpoints protegidos necesitan el SecurityContext
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

    // ─── GET /api/product-categories/{id} ────────────────────────────────────

    @Test
    void get_returnsOk_withMappedCategory() throws Exception {
        ProductCategory domain         = mock(ProductCategory.class);
        ProductCategoryResponseDTO dto = mock(ProductCategoryResponseDTO.class);

        when(getById.get(any(GetProductCategoryByIdQuery.class))).thenReturn(domain);
        when(mapper.toResponseDTO(domain)).thenReturn(dto);

        mockMvc.perform(get("/api/product-categories/{id}", CATEGORY_ID))
                .andExpect(status().isOk());

        verify(getById).get(any(GetProductCategoryByIdQuery.class));
        verify(mapper).toResponseDTO(domain);
    }

    @Test
    void get_whenServiceThrows_propagatesException() {
        when(getById.get(any(GetProductCategoryByIdQuery.class)))
                .thenThrow(new RuntimeException("Not found"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(get("/api/product-categories/{id}", CATEGORY_ID)));
    }

    // ─── GET /api/product-categories (lista paginada) ────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void list_returnsOk_withDefaultParams() throws Exception {
        PageResponse<ProductCategory> pageResult = mock(PageResponse.class);
        ListProductCategoryResponseDTO listDTO   = mock(ListProductCategoryResponseDTO.class);

        when(listCategories.list(
                any(ListProductCategoriesQuery.class),
                any(PaginationRequest.class)))
                .thenReturn(pageResult);
        when(mapper.toListResponseDTO(pageResult)).thenReturn(listDTO);

        mockMvc.perform(get("/api/product-categories"))
                .andExpect(status().isOk());

        verify(listCategories).list(
                any(ListProductCategoriesQuery.class),
                any(PaginationRequest.class));
        verify(mapper).toListResponseDTO(pageResult);
    }

    @Test
    @SuppressWarnings("unchecked")
    void list_returnsOk_withSearchQueryAndCustomParams() throws Exception {
        PageResponse<ProductCategory> pageResult = mock(PageResponse.class);
        ListProductCategoryResponseDTO listDTO   = mock(ListProductCategoryResponseDTO.class);

        when(listCategories.list(any(), any())).thenReturn(pageResult);
        when(mapper.toListResponseDTO(pageResult)).thenReturn(listDTO);

        mockMvc.perform(get("/api/product-categories")
                        .param("q", "beb")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sortBy", "name")
                        .param("direction", "DESC"))
                .andExpect(status().isOk());

        verify(listCategories).list(
                any(ListProductCategoriesQuery.class),
                any(PaginationRequest.class));
    }

    @Test
    void list_whenServiceThrows_propagatesException() {
        when(listCategories.list(any(), any())).thenThrow(new RuntimeException("DB error"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(get("/api/product-categories")));
    }

    // ─── GET /api/product-categories/public ──────────────────────────────────

    @Test
    void getPublicCategories_returnsOk_withMappedList() throws Exception {
        ProductCategory domain         = mock(ProductCategory.class);
        ProductCategoryResponseDTO dto = mock(ProductCategoryResponseDTO.class);

        when(publicProductCategories.execute()).thenReturn(List.of(domain));
        when(mapper.toResponseDTO(domain)).thenReturn(dto);

        mockMvc.perform(get("/api/product-categories/public"))
                .andExpect(status().isOk());

        verify(publicProductCategories).execute();
        verify(mapper).toResponseDTO(domain);
    }

    @Test
    void getPublicCategories_returnsOk_whenListIsEmpty() throws Exception {
        when(publicProductCategories.execute()).thenReturn(List.of());

        mockMvc.perform(get("/api/product-categories/public"))
                .andExpect(status().isOk());

        verify(publicProductCategories).execute();
        verifyNoInteractions(mapper); // stream vacío → toResponseDTO nunca se llama
    }

    @Test
    void getPublicCategories_whenServiceThrows_propagatesException() {
        when(publicProductCategories.execute())
                .thenThrow(new RuntimeException("Service error"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(get("/api/product-categories/public")));
    }

    // ─── POST /api/product-categories ────────────────────────────────────────

    @Test
    void create_returnsCreated_withMappedCategory() throws Exception {
        ProductCategory domain         = mock(ProductCategory.class);
        ProductCategoryResponseDTO dto = mock(ProductCategoryResponseDTO.class);

        when(mapper.toCreateCommand(any(CreateProductCategoryRequestDTO.class), eq(USER_ID)))
                .thenReturn(null);
        when(createCategory.create(any())).thenReturn(domain);
        when(mapper.toResponseDTO(domain)).thenReturn(dto);

        mockMvc.perform(post("/api/product-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_BODY))
                .andExpect(status().isCreated());

        verify(mapper).toCreateCommand(any(CreateProductCategoryRequestDTO.class), eq(USER_ID));
        verify(createCategory).create(any());
        verify(mapper).toResponseDTO(domain);
    }

    @Test
    void create_withoutContentType_returnsUnsupportedMediaType() throws Exception {
        mockMvc.perform(post("/api/product-categories")
                        .content(VALID_CREATE_BODY))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void create_whenServiceThrows_propagatesException() {
        when(mapper.toCreateCommand(any(), any())).thenReturn(null);
        when(createCategory.create(any())).thenThrow(new RuntimeException("Duplicate name"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/product-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_BODY)));
    }

    // ─── PUT /api/product-categories/{id} ────────────────────────────────────

    @Test
    void update_returnsOk_withMappedCategory() throws Exception {
        ProductCategory domain         = mock(ProductCategory.class);
        ProductCategoryResponseDTO dto = mock(ProductCategoryResponseDTO.class);

        when(mapper.toUpdateCommand(
                eq(CATEGORY_ID),
                any(UpdateProductCategoryRequestDTO.class),
                eq(USER_ID)))
                .thenReturn(null);
        when(updateCategory.update(any())).thenReturn(domain);
        when(mapper.toResponseDTO(domain)).thenReturn(dto);

        mockMvc.perform(put("/api/product-categories/{id}", CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_UPDATE_BODY))
                .andExpect(status().isOk());

        verify(mapper).toUpdateCommand(eq(CATEGORY_ID), any(), eq(USER_ID));
        verify(updateCategory).update(any());
    }

    @Test
    void update_whenServiceThrows_propagatesException() {
        when(mapper.toUpdateCommand(any(), any(), any())).thenReturn(null);
        when(updateCategory.update(any())).thenThrow(new RuntimeException("Not found"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(put("/api/product-categories/{id}", CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_UPDATE_BODY)));
    }

    // ─── DELETE /api/product-categories/{id} ─────────────────────────────────

    @Test
    void delete_returnsNoContent() throws Exception {
        doNothing().when(deleteCategory).delete(CATEGORY_ID, USER_ID);

        mockMvc.perform(delete("/api/product-categories/{id}", CATEGORY_ID))
                .andExpect(status().isNoContent());

        verify(deleteCategory).delete(CATEGORY_ID, USER_ID);
    }

    @Test
    void delete_whenServiceThrows_propagatesException() {
        doThrow(new RuntimeException("Category in use"))
                .when(deleteCategory).delete(CATEGORY_ID, USER_ID);

        assertThrows(Exception.class, () ->
                mockMvc.perform(delete("/api/product-categories/{id}", CATEGORY_ID)));
    }
}