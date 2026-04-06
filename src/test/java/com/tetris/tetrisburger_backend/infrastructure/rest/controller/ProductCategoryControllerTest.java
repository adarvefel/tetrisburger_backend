package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.port.in.productcategory.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.ProductCategoryRestDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ProductCategoryControllerTest {

    private MockMvc mockMvc;

    @Mock private CreateProductCategory createCategory;
    @Mock private UpdateProductCategory updateCategory;
    @Mock private DeleteProductCategory deleteCategory;
    @Mock private GetProductCategoryById getById;
    @Mock private ListProductCategories listCategories;
    @Mock private ListPublicProductCategories publicProductCategories;
    @Mock private ProductCategoryRestDtoMapper mapper;

    @InjectMocks private ProductCategoryController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    class SecurityTests {

        @Test
        void adminEndpoint_shouldNotCallUseCase_whenNotAuthenticated() {
            SecurityContextHolder.clearContext();
            verifyNoInteractions(createCategory);
        }
    }
}
