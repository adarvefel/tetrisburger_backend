//package com.tetris.tetrisburger_backend.infrastructure.rest.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.tetris.tetrisburger_backend.domain.common.PageResponse;
//import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
//import com.tetris.tetrisburger_backend.domain.model.Product;
//import com.tetris.tetrisburger_backend.domain.port.in.product.*;
//import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
//import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.*;
//import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.ProductRestDtoMapper;
//import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.core.MethodParameter;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//class ProductControllerTest {
//
//    private MockMvc mockMvc;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Mock private CreateProduct createProduct;
//    @Mock private ListProducts listProducts;
//    @Mock private GetProductById getProductById;
//    @Mock private UpdateProduct updateProduct;
//    @Mock private UpdateProductImage updateProductImage;
//    @Mock private DeleteProduct deleteProduct;
//    @Mock private ProductRestDtoMapper mapper;
//    @Mock private ImageStoragePort imageStoragePort;
//    @Mock private SearchProducts searchProducts;
//    @Mock private SetProductAvailability setProductAvailability;
//
//    @InjectMocks private ProductController controller;
//
//    private CustomUserDetails mockUserDetails;
//    private Product mockProduct;
//
//    @BeforeEach
//    void setUp() {
//        mockUserDetails = mock(CustomUserDetails.class);
//        lenient().when(mockUserDetails.getId()).thenReturn(1);
//
//        mockMvc = MockMvcBuilders.standaloneSetup(controller)
//                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
//                    @Override
//                    public boolean supportsParameter(MethodParameter parameter) {
//                        return parameter.getParameterAnnotation(AuthenticationPrincipal.class) != null;
//                    }
//                    @Override
//                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
//                        return mockUserDetails;
//                    }
//                })
//                .build();
//
//        mockProduct = mock(Product.class);
//    }
//
//    @Nested
//    class CreateProductTests {
//        @Test
//        void shouldCreateProductAndReturn201() throws Exception {
//            MockMultipartFile dataPart = new MockMultipartFile("data", "", MediaType.APPLICATION_JSON_VALUE, "{}".getBytes());
//
//            when(mapper.toCreateProductCommand(any(), any(), eq(1))).thenReturn(mock());
//            when(createProduct.create(any())).thenReturn(mockProduct);
//
//            mockMvc.perform(multipart("/api/products")
//                    .file(dataPart))
//                    .andExpect(status().isCreated());
//
//            verify(createProduct).create(any());
//        }
//    }
//
//    @Nested
//    class GetProductByIdTests {
//        @Test
//        void shouldGetProductByIdAndReturn200() throws Exception {
//            ProductResponseDTO mockDto = mock(ProductResponseDTO.class);
//            when(getProductById.get(any())).thenReturn(mockProduct);
//            when(mapper.toProductResponseDTO(any(), anyBoolean())).thenReturn(mockDto);
//
//            mockMvc.perform(get("/api/products/1"))
//                    .andExpect(status().isOk());
//
//            verify(getProductById).get(any());
//        }
//    }
//
//    @Nested
//    class ListProductsTests {
//        @Test
//        void shouldListProductsAndReturn200() throws Exception {
//            PageResponse<Product> pageResponse = new PageResponse<>(List.of(mockProduct), 0, 10, 1L, 1);
//
//            when(listProducts.list(any(), any())).thenReturn(pageResponse);
//
//            mockMvc.perform(get("/api/products"))
//                    .andExpect(status().isOk());
//
//            verify(listProducts).list(any(), any());
//        }
//    }
//
//    @Nested
//    class DeleteProductTests {
//        @Test
//        void shouldDeleteProductAndReturn200() throws Exception {
//            mockMvc.perform(delete("/api/products/1"))
//                    .andExpect(status().isOk());
//
//            verify(deleteProduct).delete(eq(1), eq(1));
//        }
//    }
//}
