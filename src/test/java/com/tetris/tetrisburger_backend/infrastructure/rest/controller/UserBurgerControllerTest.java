//package com.tetris.tetrisburger_backend.infrastructure.rest.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.tetris.tetrisburger_backend.domain.common.PageResponse;
//import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
//import com.tetris.tetrisburger_backend.domain.exception.BurgerNotFoundException;
//import com.tetris.tetrisburger_backend.domain.model.Burger;
//import com.tetris.tetrisburger_backend.domain.model.Product;
//import com.tetris.tetrisburger_backend.domain.port.in.burger.GetFeaturedBurgers;
//import com.tetris.tetrisburger_backend.domain.port.in.burger.ListBurgerIngredients;
//import com.tetris.tetrisburger_backend.domain.port.in.burger.SearchIngredients;
//import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.UpdateCustomBurger;
//import com.tetris.tetrisburger_backend.domain.port.in.burger.client.CreateCustomBurger;
//import com.tetris.tetrisburger_backend.infrastructure.rest.advice.BurgerExceptionHandler;
//import com.tetris.tetrisburger_backend.infrastructure.rest.advice.GlobalExceptionHandler;
//import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ProductExceptionHandler;
//import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ValidationExceptionHandler;
//import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.IngredientRequestDTO;
//import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin.MenuBurgerResponseDTO;
//import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client.BurgerResponseDTO;
//import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client.CreateCustomBurgerRequestDTO;
//import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client.UpdateCustomBurgerRequestDTO;
//import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.BurgerIngredientListDTO;
//import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.BurgerRestDtoMapper;
//import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.ProductRestDtoMapper;
//import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.core.MethodParameter;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//class UserBurgerControllerTest {
//
//    private MockMvc mockMvc;
//    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
//
//    @Mock private CreateCustomBurger createCustomBurger;
//    @Mock private ListBurgerIngredients listBurgerIngredients;
//    @Mock private SearchIngredients searchIngredients;
//    @Mock private UpdateCustomBurger updateCustomBurger;
//    @Mock private ProductRestDtoMapper productRestDtoMapper;
//    @Mock private GetFeaturedBurgers getFeaturedBurgers;
//    @Mock private BurgerRestDtoMapper mapper;
//
//    @InjectMocks private UserBurgerController controller;
//
//    private CustomUserDetails mockUserDetails;
//    private Burger mockBurger;
//
//    private void mockAuthenticatedUser(Long userId) {
//        lenient().when(mockUserDetails.getId()).thenReturn(userId.intValue());
//        lenient().when(mockUserDetails.getUsername()).thenReturn(userId.toString());
//    }
//
//    private BurgerResponseDTO sampleBurgerResponse() {
//        return new BurgerResponseDTO(1, "Mi Burger", new BigDecimal("20000"), null, List.of());
//    }
//
//    private MenuBurgerResponseDTO sampleMenuBurgerResponse() {
//        return new MenuBurgerResponseDTO(
//                1, "Featured", null, BigDecimal.ZERO, new BigDecimal("18000"), BigDecimal.ZERO, BigDecimal.ZERO,
//                false, true, true, true, null, null, "NONE", List.of(),
//                LocalDateTime.now(), LocalDateTime.now(), null, null
//        );
//    }
//
//    @BeforeEach
//    void setUp() {
//        mockUserDetails = mock(CustomUserDetails.class);
//        mockAuthenticatedUser(1L);
//
//        mockMvc = MockMvcBuilders.standaloneSetup(controller)
//                .setControllerAdvice(
//                        new ValidationExceptionHandler(),
//                        new BurgerExceptionHandler(),
//                        new ProductExceptionHandler(),
//                        new GlobalExceptionHandler()
//                )
//                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
//                    @Override
//                    public boolean supportsParameter(MethodParameter parameter) {
//                        return parameter.getParameterAnnotation(AuthenticationPrincipal.class) != null;
//                    }
//
//                    @Override
//                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
//                                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
//                        return mockUserDetails;
//                    }
//                })
//                .build();
//
//        mockBurger = mock(Burger.class);
//        lenient().when(mockBurger.getIdBurger()).thenReturn(1);
//    }
//
//    @Nested
//    class CreateCustomBurgerTests {
//        @Test
//        void shouldReturn201WhenCreatedSuccessfully() throws Exception {
//            CreateCustomBurgerRequestDTO req = new CreateCustomBurgerRequestDTO(
//                    "Mi Burger", List.of(new IngredientRequestDTO(1, 2))
//            );
//
//            when(mapper.toCreateCustomBurgerCommand(any(), eq(1))).thenReturn(mock());
//            when(createCustomBurger.handle(any())).thenReturn(mockBurger);
//            when(mapper.toBurgerResponseDTO(mockBurger)).thenReturn(sampleBurgerResponse());
//
//            mockMvc.perform(post("/api/burgers/custom")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(req)))
//                    .andExpect(status().isCreated())
//                    .andExpect(jsonPath("$.idBurger").value(1));
//
//            verify(createCustomBurger, times(1)).handle(any());
//        }
//
//        @Test
//        void shouldReturn500WhenBodyMalformed() throws Exception {
//            mockMvc.perform(post("/api/burgers/custom")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content("not-json"))
//                    .andExpect(status().isInternalServerError());
//
//            verifyNoInteractions(createCustomBurger);
//        }
//    }
//
//    @Nested
//    class SearchIngredientsTests {
//        @Test
//        void shouldReturn200WhenSearching() throws Exception {
//            PageResponse<Product> page = new PageResponse<>(List.of(), 0, 10, 0L, 0);
//            BurgerIngredientListDTO dto = new BurgerIngredientListDTO(List.of(), 0, 10, 0L, 0);
//
//            when(searchIngredients.handle(eq("queso"), any(PaginationRequest.class))).thenReturn(page);
//            when(productRestDtoMapper.toBurgerIngredientListDTO(page)).thenReturn(dto);
//
//            mockMvc.perform(get("/api/burgers/ingredients/search").param("name", "queso"))
//                    .andExpect(status().isOk());
//
//            verify(searchIngredients, times(1)).handle(eq("queso"), any(PaginationRequest.class));
//        }
//
//        @Test
//        void shouldReturn200WhenNameOmitted() throws Exception {
//            PageResponse<Product> page = new PageResponse<>(List.of(), 0, 10, 0L, 0);
//            when(searchIngredients.handle(isNull(), any(PaginationRequest.class))).thenReturn(page);
//            when(productRestDtoMapper.toBurgerIngredientListDTO(page)).thenReturn(
//                    new BurgerIngredientListDTO(List.of(), 0, 10, 0L, 0)
//            );
//
//            mockMvc.perform(get("/api/burgers/ingredients/search"))
//                    .andExpect(status().isOk());
//
//            verify(searchIngredients, times(1)).handle(isNull(), any(PaginationRequest.class));
//        }
//    }
//
//    @Nested
//    class UpdateCustomBurgerTests {
//        @Test
//        void shouldReturn200WhenUpdated() throws Exception {
//            UpdateCustomBurgerRequestDTO req = new UpdateCustomBurgerRequestDTO("Nombre", "Desc", List.of(new IngredientRequestDTO(1, 1)));
//
//            when(mapper.toUpdateCustomBurgerCommand(eq(1), any(), eq(1))).thenReturn(mock());
//            when(updateCustomBurger.handle(any())).thenReturn(mockBurger);
//            when(mapper.toBurgerResponseDTO(mockBurger)).thenReturn(sampleBurgerResponse());
//
//            mockMvc.perform(put("/api/burgers/custom/1")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(req)))
//                    .andExpect(status().isOk());
//
//            verify(updateCustomBurger, times(1)).handle(any());
//        }
//
//        @Test
//        void shouldReturn404WhenNotFound() throws Exception {
//            UpdateCustomBurgerRequestDTO req = new UpdateCustomBurgerRequestDTO("Nombre", null, List.of(new IngredientRequestDTO(1, 1)));
//
//            when(mapper.toUpdateCustomBurgerCommand(eq(99), any(), eq(1))).thenReturn(mock());
//            when(updateCustomBurger.handle(any())).thenThrow(new BurgerNotFoundException(99));
//
//            mockMvc.perform(put("/api/burgers/custom/99")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(req)))
//                    .andExpect(status().isNotFound());
//
//            verify(updateCustomBurger, times(1)).handle(any());
//        }
//    }
//
//    @Nested
//    class GetFeaturedBurgersTests {
//        @Test
//        void shouldReturn200WhenListed() throws Exception {
//            when(getFeaturedBurgers.handle()).thenReturn(List.of(mockBurger));
//            when(mapper.toMenuBurgerResponseDTO(mockBurger)).thenReturn(sampleMenuBurgerResponse());
//
//            mockMvc.perform(get("/api/burgers/featured"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$[0].idBurger").value(1));
//
//            verify(getFeaturedBurgers, times(1)).handle();
//        }
//    }
//
//    @Nested
//    class SecurityTests {
//
//        @Test
//        void publicEndpoints_shouldBeAccessibleWithoutAuthentication() throws Exception {
//            SecurityContextHolder.clearContext();
//            when(getFeaturedBurgers.handle()).thenReturn(List.of());
//            mockMvc.perform(get("/api/burgers/featured"))
//                    .andExpect(status().isOk());
//            verifyNoInteractions(createCustomBurger);
//        }
//    }
//}
