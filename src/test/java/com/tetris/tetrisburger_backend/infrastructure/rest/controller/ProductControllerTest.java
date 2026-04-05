package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.enums.ProductType;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.*;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.GetProductByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.ListProductsQuery;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.SearchProductsQuery;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.BurgerExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.GlobalExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ProductExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ValidationExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.CreateProductRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.ListProductResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.ProductResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.UpdateProductRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.ProductRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock private CreateProduct createProduct;
    @Mock private ListProducts listProducts;
    @Mock private GetProductById getProductById;
    @Mock private UpdateProduct updateProduct;
    @Mock private UpdateProductImage updateProductImage;
    @Mock private DeleteProduct deleteProduct;
    @Mock private ProductRestDtoMapper mapper;
    @Mock private ImageStoragePort imageStoragePort;
    @Mock private SearchProducts searchProducts;
    @Mock private SetProductAvailability setProductAvailability;
    @Mock private AdjustProductStock adjustProductStock;
    @Mock private ListPublicProducts listPublicProducts;

    @InjectMocks private ProductController controller;

    private CustomUserDetails mockUserDetails;
    private Product mockProduct;

    private void mockAuthenticatedUser(Long userId) {
        lenient().when(mockUserDetails.getId()).thenReturn(userId.intValue());
        lenient().when(mockUserDetails.getUsername()).thenReturn(userId.toString());
    }

    private ProductResponseDTO sampleProductDto() {
        return ProductResponseDTO.builder()
                .idProduct(1)
                .name("Carne")
                .price(new BigDecimal("8000"))
                .quantity(10)
                .availability(true)
                .build();
    }

    @BeforeEach
    void setUp() {
        mockUserDetails = mock(CustomUserDetails.class);
        mockAuthenticatedUser(1L);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(
                        new ValidationExceptionHandler(),
                        new BurgerExceptionHandler(),
                        new ProductExceptionHandler(),
                        new GlobalExceptionHandler()
                )
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterAnnotation(AuthenticationPrincipal.class) != null;
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return mockUserDetails;
                    }
                })
                .build();

        mockProduct = mock(Product.class);
        lenient().when(mockProduct.getId()).thenReturn(1);
        lenient().when(mockProduct.getImageKey()).thenReturn(null);
    }

    private CreateProductRequestDTO validCreateDto() {
        CreateProductRequestDTO dto = new CreateProductRequestDTO();
        dto.setName("Carne Premium");
        dto.setDescription("Desc");
        dto.setQuantity(10);
        dto.setPrice(new BigDecimal("8000.00"));
        dto.setAvailability(true);
        dto.setProductType("INGREDIENT");
        dto.setProductCategoryId(1);
        return dto;
    }

    private UpdateProductRequestDTO validUpdateDto() {
        UpdateProductRequestDTO dto = new UpdateProductRequestDTO();
        dto.setName("Carne Premium");
        dto.setQuantity(5);
        dto.setPrice(new BigDecimal("9000.00"));
        dto.setAvailability(true);
        dto.setProductType("INGREDIENT");
        return dto;
    }

    @Nested
    class CreateProductTests {
        @Test
        void shouldReturn201WhenCreatedSuccessfully() throws Exception {
            MockMultipartFile data = new MockMultipartFile(
                    "data", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(validCreateDto())
            );

            when(mapper.toCreateProductCommand(any(), isNull(), eq(1))).thenReturn(mock());
            when(createProduct.create(any())).thenReturn(mockProduct);
            when(mapper.toProductResponseDTO(mockProduct)).thenReturn(sampleProductDto());

            mockMvc.perform(multipart("/api/products").file(data))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idProduct").value(1));

            verify(createProduct, times(1)).create(any());
        }

        @Test
        void shouldReturn400WhenInvalidRequest() throws Exception {
            String invalid = "{\"name\":\"\",\"quantity\":-1,\"price\":0,\"productType\":\"INGREDIENT\"}";
            MockMultipartFile data = new MockMultipartFile(
                    "data", "", MediaType.APPLICATION_JSON_VALUE, invalid.getBytes()
            );

            mockMvc.perform(multipart("/api/products").file(data))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(createProduct);
        }
    }

    @Nested
    class ListProductsTests {
        @Test
        void shouldReturn200WhenListed() throws Exception {
            PageResponse<Product> page = new PageResponse<>(List.of(mockProduct), 0, 10, 1L, 1);
            when(listProducts.list(any(ListProductsQuery.class), any(PaginationRequest.class))).thenReturn(page);
            when(mapper.toProductResponseDTO(mockProduct)).thenReturn(sampleProductDto());

            mockMvc.perform(get("/api/products/list"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items[0].idProduct").value(1));

            verify(listProducts, times(1)).list(any(ListProductsQuery.class), any(PaginationRequest.class));
        }
    }

    @Nested
    class SearchProductsTests {
        @Test
        void shouldReturn200WhenSearching() throws Exception {
            PageResponse<Product> page = new PageResponse<>(List.of(), 0, 10, 0L, 0);
            when(searchProducts.search(any(SearchProductsQuery.class), any(PaginationRequest.class))).thenReturn(page);

            mockMvc.perform(get("/api/products/search").param("q", "carne"))
                    .andExpect(status().isOk());

            verify(searchProducts, times(1)).search(any(SearchProductsQuery.class), any(PaginationRequest.class));
        }
    }

    @Nested
    class GetProductByIdTests {
        @Test
        void shouldReturn200WhenFound() throws Exception {
            when(getProductById.get(any(GetProductByIdQuery.class))).thenReturn(mockProduct);
            when(mapper.toProductResponseDTO(mockProduct)).thenReturn(sampleProductDto());

            mockMvc.perform(get("/api/products/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Carne"));

            verify(getProductById, times(1)).get(argThat(q -> q.id().equals(1)));
        }

        @Test
        void shouldReturn404WhenNotFound() throws Exception {
            when(getProductById.get(any(GetProductByIdQuery.class)))
                    .thenThrow(new ProductNotFoundException(99));

            mockMvc.perform(get("/api/products/99"))
                    .andExpect(status().isNotFound());

            verify(getProductById, times(1)).get(any(GetProductByIdQuery.class));
        }
    }

    @Nested
    class UpdateProductTests {
        @Test
        void shouldReturn200WhenUpdated() throws Exception {
            when(mapper.toUpdateProductCommand(eq(1), any(), eq(1))).thenReturn(mock());
            when(updateProduct.update(any())).thenReturn(mockProduct);
            when(mapper.toProductResponseDTO(mockProduct)).thenReturn(sampleProductDto());

            mockMvc.perform(put("/api/products/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUpdateDto())))
                    .andExpect(status().isOk());

            verify(updateProduct, times(1)).update(any());
        }

        @Test
        void shouldReturn400WhenInvalidBody() throws Exception {
            String bad = "{\"name\":\"\",\"quantity\":null,\"price\":null,\"productType\":null}";
            mockMvc.perform(put("/api/products/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bad))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(updateProduct);
        }
    }

    @Nested
    class UpdateProductImageTests {
        @Test
        void shouldReturn200WhenImageSent() throws Exception {
            MockMultipartFile img = new MockMultipartFile(
                    "productImage", "p.png", MediaType.IMAGE_PNG_VALUE, "bytes".getBytes()
            );

            when(updateProductImage.update(eq(1), any(), eq(1))).thenReturn(mockProduct);
            when(mapper.toProductResponseDTO(mockProduct)).thenReturn(sampleProductDto());

            mockMvc.perform(multipart("/api/products/image/1")
                            .file(img)
                            .with(r -> {
                                r.setMethod("PUT");
                                return r;
                            }))
                    .andExpect(status().isOk());

            verify(updateProductImage, times(1)).update(eq(1), any(), eq(1));
        }

        @Test
        void shouldReturn400WhenImageMissing() throws Exception {
            mockMvc.perform(multipart("/api/products/image/1")
                            .with(r -> {
                                r.setMethod("PUT");
                                return r;
                            }))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(updateProductImage);
        }
    }

    @Nested
    class ChangeAvailabilityTests {
        @Test
        void shouldReturn200WhenToggled() throws Exception {
            when(setProductAvailability.setAvailability(eq(1), eq(false), eq(1))).thenReturn(mockProduct);
            when(mapper.toProductResponseDTO(mockProduct)).thenReturn(sampleProductDto());

            mockMvc.perform(patch("/api/products/1/availability").param("availability", "false"))
                    .andExpect(status().isOk());

            verify(setProductAvailability, times(1)).setAvailability(1, false, 1);
        }
    }

    @Nested
    class AdjustStockTests {
        @Test
        void shouldReturn200WhenAdjusted() throws Exception {
            when(adjustProductStock.adjustStock(eq(1), eq(3), eq(1))).thenReturn(mockProduct);
            when(mapper.toProductResponseDTO(mockProduct)).thenReturn(sampleProductDto());

            mockMvc.perform(patch("/api/products/1/stock").param("delta", "3"))
                    .andExpect(status().isOk());

            verify(adjustProductStock, times(1)).adjustStock(1, 3, 1);
        }
    }

    @Nested
    class DeleteProductTests {
        @Test
        void shouldReturn200WhenDeleted() throws Exception {
            doNothing().when(deleteProduct).delete(1, 1);

            mockMvc.perform(delete("/api/products/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(deleteProduct, times(1)).delete(1, 1);
        }
    }

    @Nested
    class PublicProductsTests {
        @Test
        void shouldReturn200WhenListed() throws Exception {
            PageResponse<Product> page = new PageResponse<>(List.of(mockProduct), 0, 10, 1L, 1);
            when(listPublicProducts.list(eq(ProductType.INGREDIENT), isNull(), any(PaginationRequest.class)))
                    .thenReturn(page);
            when(mapper.toPublicProductResponseDTO(eq(mockProduct), isNull()))
                    .thenReturn(sampleProductDto());

            mockMvc.perform(get("/api/products/public").param("productType", "INGREDIENT"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items[0].idProduct").value(1));

            verify(listPublicProducts, times(1)).list(eq(ProductType.INGREDIENT), isNull(), any(PaginationRequest.class));
        }
    }

    @Nested
    class SecurityTests {

        @Test
        void adminEndpoint_shouldNotCallUseCase_whenNotAuthenticated() throws Exception {
            SecurityContextHolder.clearContext();
            verifyNoInteractions(createProduct);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        void adminEndpoint_shouldReturn403_whenRoleIsClient() throws Exception {
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void adminEndpoint_shouldBeAccessible_whenRoleIsAdmin() throws Exception {
            PageResponse<Product> page = new PageResponse<>(List.of(), 0, 10, 0L, 0);
            when(listProducts.list(any(), any(PaginationRequest.class))).thenReturn(page);
            mockMvc.perform(get("/api/products/list"))
                    .andExpect(status().isOk());
        }
    }
}
