package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.port.in.productcategory.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.ProductCategoryRestDtoMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductCategoryController.class)
class ProductCategoryControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private CreateProductCategory createCategory;
    @MockitoBean private UpdateProductCategory updateCategory;
    @MockitoBean private DeleteProductCategory deleteCategory;
    @MockitoBean private GetProductCategoryById getById;
    @MockitoBean private ListProductCategories listCategories;
    @MockitoBean private ListPublicProductCategories publicProductCategories;
    @MockitoBean private ProductCategoryRestDtoMapper mapper;

    @Nested
    class SecurityTests {

        @Test
        void adminEndpoint_shouldNotCallUseCase_whenNotAuthenticated() throws Exception {
            SecurityContextHolder.clearContext();
            verifyNoInteractions(createCategory);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        void adminEndpoint_shouldReturn403_whenRoleIsClient() throws Exception {
            mockMvc.perform(post("/api/product-categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}")
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void adminEndpoint_shouldBeAccessible_whenRoleIsAdmin() throws Exception {
            mockMvc.perform(get("/api/product-categories"))
                    .andExpect(status().isOk());
        }
    }
}
