//package com.tetris.tetrisburger_backend.infrastructure.rest.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.tetris.tetrisburger_backend.domain.common.PageResponse;
//import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
//import com.tetris.tetrisburger_backend.domain.exception.BurgerNotFoundException;
//import com.tetris.tetrisburger_backend.domain.model.Burger;
//import com.tetris.tetrisburger_backend.domain.model.Product;
//import com.tetris.tetrisburger_backend.domain.port.in.burger.*;
//import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.*;
//import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateMenuBurgerImageCommand;
//import com.tetris.tetrisburger_backend.domain.port.in.burger.query.SearchMenuBurgersQuery;
//import com.tetris.tetrisburger_backend.infrastructure.rest.advice.BurgerExceptionHandler;
//import com.tetris.tetrisburger_backend.infrastructure.rest.advice.GlobalExceptionHandler;
//import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ProductExceptionHandler;
//import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ValidationExceptionHandler;
//import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.IngredientRequestDTO;
//import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin.*;
//import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.BurgerIngredientListDTO;
//import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.BurgerRestDtoMapper;
//import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.ProductRestDtoMapper;
//import com.tetris.tetrisburger_backend.infrastructure.rest.validator.ImageValidator;
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
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.test.context.support.WithMockUser;
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
//class AdminBurgerControllerTest {
//
//    private MockMvc mockMvc;
//    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
//
//    @Mock private CreateMenuBurger createMenuBurger;
//    @Mock private GetBurgerById getBurgerById;
//    @Mock private ListBurgers listBurgers;
//    @Mock private UpdateMenuBurger updateMenuBurger;
//    @Mock private UpdateMenuBurgerImage updateMenuBurgerImage;
//    @Mock private DeleteMenuBurger deleteMenuBurger;
//    @Mock private SearchMenuBurgers searchMenuBurgers;
//    @Mock private BurgerRestDtoMapper mapper;
//    @Mock private ListBurgerIngredients listBurgerIngredients;
//    @Mock private ProductRestDtoMapper productRestDtoMapper;
//    @Mock private SearchIngredients searchIngredients;
//    @Mock private ImageValidator imageValidator;
//
//    @InjectMocks private AdminBurgerController controller;
//
//    private CustomUserDetails mockUserDetails;
//    private Burger mockBurger;
//
//    private void mockAuthenticatedUser(Long userId) {
//        lenient().when(mockUserDetails.getId()).thenReturn(userId.intValue());
//        lenient().when(mockUserDetails.getUsername()).thenReturn(userId.toString());
//    }
//
//    private MenuBurgerResponseDTO sampleMenuResponse() {
//        return new MenuBurgerResponseDTO(
//                1, "Classic", "Desc", BigDecimal.ZERO, new BigDecimal("15000"), BigDecimal.ZERO, BigDecimal.ZERO,
//                false, true, true, true, null, null, "NONE", List.of(),
//                LocalDateTime.now(), LocalDateTime.now(), 1, 1
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
//    private CreateMenuBurgerRequestDTO validCreateDto() {
//        return new CreateMenuBurgerRequestDTO(
//                "Classic Burger",
//                "Descripción",
//                new BigDecimal("15000"),
//                false,
//                true,
//                List.of(new IngredientRequestDTO(1, 1))
//        );
//    }
//
//    private UpdateMenuBurgerRequestDTO validUpdateDto() {
//        return new UpdateMenuBurgerRequestDTO(
//                "Updated Burger",
//                "Nueva desc",
//                new BigDecimal("16000"),
//                true,
//                false,
//                List.of(new IngredientRequestDTO(1, 2))
//        );
//    }
//
//    @Nested
//    class CreateMenuBurgerTests {
//        @Test
//        void shouldReturn201WhenCreatedSuccessfully() throws Exception {
//            MockMultipartFile dataPart = new MockMultipartFile(
//                    "data", "", MediaType.APPLICATION_JSON_VALUE,
//                    objectMapper.writeValueAsBytes(validCreateDto())
//            );
//
//            when(mapper.toCreateBurgerCommand(any(), isNull(), eq(1))).thenReturn(mock());
//            when(createMenuBurger.handle(any())).thenReturn(mockBurger);
//            when(mapper.toMenuBurgerResponseDTO(mockBurger)).thenReturn(sampleMenuResponse());
//
//            mockMvc.perform(multipart("/api/admin/burgers/menu").file(dataPart))
//                    .andExpect(status().isCreated())
//                    .andExpect(jsonPath("$.idBurger").value(1))
//                    .andExpect(jsonPath("$.name").value("Classic"));
//
//            verify(createMenuBurger, times(1)).handle(any());
//            verify(mapper, times(1)).toMenuBurgerResponseDTO(mockBurger);
//        }
//
//        @Test
//        void shouldReturn400WhenInvalidRequest() throws Exception {
//            String invalidJson = "{\"name\":\"ab\",\"finalPrice\":0,\"availability\":true,\"ingredients\":[]}";
//            MockMultipartFile dataPart = new MockMultipartFile(
//                    "data", "", MediaType.APPLICATION_JSON_VALUE, invalidJson.getBytes()
//            );
//
//            mockMvc.perform(multipart("/api/admin/burgers/menu").file(dataPart))
//                    .andExpect(status().isBadRequest());
//
//            verifyNoInteractions(createMenuBurger);
//        }
//
//        @Test
//        void shouldReturn201WithImage() throws Exception {
//            MockMultipartFile dataPart = new MockMultipartFile(
//                    "data", "", MediaType.APPLICATION_JSON_VALUE,
//                    objectMapper.writeValueAsBytes(validCreateDto())
//            );
//            MockMultipartFile img = new MockMultipartFile(
//                    "burgerImage", "b.png", MediaType.IMAGE_PNG_VALUE, "x".getBytes()
//            );
//
//            doNothing().when(imageValidator).validate(any());
//            when(mapper.toCreateBurgerCommand(any(), any(), eq(1))).thenReturn(mock());
//            when(createMenuBurger.handle(any())).thenReturn(mockBurger);
//            when(mapper.toMenuBurgerResponseDTO(mockBurger)).thenReturn(sampleMenuResponse());
//
//            mockMvc.perform(multipart("/api/admin/burgers/menu").file(dataPart).file(img))
//                    .andExpect(status().isCreated());
//
//            verify(imageValidator, times(1)).validate(any());
//            verify(createMenuBurger, times(1)).handle(any());
//        }
//    }
//
//    @Nested
//    class UpdateMenuBurgerTests {
//        @Test
//        void shouldReturn200WhenUpdated() throws Exception {
//            when(mapper.toUpdateMenuBurgerCommand(eq(1), any(), eq(1))).thenReturn(mock());
//            when(updateMenuBurger.handle(any())).thenReturn(mockBurger);
//            when(mapper.toMenuBurgerResponseDTO(mockBurger)).thenReturn(sampleMenuResponse());
//
//            mockMvc.perform(put("/api/admin/burgers/menu/1")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(validUpdateDto())))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.idBurger").value(1));
//
//            verify(updateMenuBurger, times(1)).handle(any());
//        }
//
//        @Test
//        void shouldReturn400WhenInvalidRequest() throws Exception {
//            String invalid = "{\"name\":\"xx\",\"availability\":null,\"ingredients\":[]}";
//            mockMvc.perform(put("/api/admin/burgers/menu/1")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(invalid))
//                    .andExpect(status().isBadRequest());
//
//            verifyNoInteractions(updateMenuBurger);
//        }
//    }
//
//    @Nested
//    class PatchMenuBurgerImageTests {
//        @Test
//        void shouldReturn200WhenImageUpdated() throws Exception {
//            MockMultipartFile img = new MockMultipartFile(
//                    "burgerImage", "b.png", MediaType.IMAGE_PNG_VALUE, "img".getBytes()
//            );
//
//            doNothing().when(imageValidator).validate(any());
//            when(updateMenuBurgerImage.handle(any(UpdateMenuBurgerImageCommand.class))).thenReturn(mockBurger);
//            when(mapper.toMenuBurgerResponseDTO(mockBurger)).thenReturn(sampleMenuResponse());
//
//            mockMvc.perform(multipart("/api/admin/burgers/menu/1/image")
//                            .file(img)
//                            .with(r -> {
//                                r.setMethod("PATCH");
//                                return r;
//                            }))
//                    .andExpect(status().isOk());
//
//            verify(updateMenuBurgerImage, times(1)).handle(any(UpdateMenuBurgerImageCommand.class));
//        }
//
//        @Test
//        void shouldReturn400WhenImageMissing() throws Exception {
//            mockMvc.perform(multipart("/api/admin/burgers/menu/1/image")
//                            .with(r -> {
//                                r.setMethod("PATCH");
//                                return r;
//                            }))
//                    .andExpect(status().isBadRequest());
//
//            verifyNoInteractions(updateMenuBurgerImage);
//        }
//    }
//
//    @Nested
//    class DeleteMenuBurgerTests {
//        @Test
//        void shouldReturn200WhenDeleted() throws Exception {
//            doNothing().when(deleteMenuBurger).handle(1, 1);
//
//            mockMvc.perform(delete("/api/admin/burgers/menu/1"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.success").value(true));
//
//            verify(deleteMenuBurger, times(1)).handle(1, 1);
//        }
//    }
//
//    @Nested
//    class ListMenuBurgersTests {
//        @Test
//        void shouldReturn200WithPage() throws Exception {
//            PageResponse<Burger> page = new PageResponse<>(List.of(mockBurger), 0, 10, 1L, 1);
//            when(listBurgers.handle(any(PaginationRequest.class))).thenReturn(page);
//            when(mapper.toMenuBurgerPageResponseDTO(page)).thenReturn(
//                    new MenuBurgerPageResponseDTO(List.of(sampleMenuResponse()), 0, 10, 1L, 1)
//            );
//
//            mockMvc.perform(get("/api/admin/burgers/menu"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.totalElements").value(1));
//
//            verify(listBurgers, times(1)).handle(any(PaginationRequest.class));
//        }
//    }
//
//    @Nested
//    class SearchMenuBurgersTests {
//        @Test
//        void shouldReturn200WhenSearching() throws Exception {
//            PageResponse<Burger> page = new PageResponse<>(List.of(), 0, 10, 0L, 0);
//            when(searchMenuBurgers.search(any(SearchMenuBurgersQuery.class), any(PaginationRequest.class)))
//                    .thenReturn(page);
//            when(mapper.toMenuBurgerPageResponseDTO(page)).thenReturn(
//                    new MenuBurgerPageResponseDTO(List.of(), 0, 10, 0L, 0)
//            );
//
//            mockMvc.perform(get("/api/admin/burgers/menu/search").param("name", "Test"))
//                    .andExpect(status().isOk());
//
//            verify(searchMenuBurgers, times(1)).search(any(SearchMenuBurgersQuery.class), any(PaginationRequest.class));
//        }
//
//        @Test
//        void shouldReturn400WhenNameMissing() throws Exception {
//            mockMvc.perform(get("/api/admin/burgers/menu/search"))
//                    .andExpect(status().isBadRequest());
//
//            verifyNoInteractions(searchMenuBurgers);
//        }
//    }
//
//    @Nested
//    class GetBurgerByIdTests {
//        @Test
//        void shouldReturn200WhenFound() throws Exception {
//            when(getBurgerById.execute(1)).thenReturn(mockBurger);
//            when(mapper.toMenuBurgerResponseDTO(mockBurger)).thenReturn(sampleMenuResponse());
//
//            mockMvc.perform(get("/api/admin/burgers/1"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.idBurger").value(1));
//
//            verify(getBurgerById, times(1)).execute(1);
//        }
//
//        @Test
//        void shouldReturn404WhenNotFound() throws Exception {
//            when(getBurgerById.execute(99)).thenThrow(new BurgerNotFoundException(99));
//
//            mockMvc.perform(get("/api/admin/burgers/99"))
//                    .andExpect(status().isNotFound());
//
//            verify(getBurgerById, times(1)).execute(99);
//        }
//    }
//
//    @Nested
//    class ListBurgerIngredientsTests {
//        @Test
//        void shouldReturn200() throws Exception {
//            PageResponse<Product> page = new PageResponse<>(List.of(), 0, 10, 0L, 0);
//            BurgerIngredientListDTO dto = new BurgerIngredientListDTO(List.of(), 0, 10, 0L, 0);
//            when(listBurgerIngredients.handle(isNull(), any(PaginationRequest.class))).thenReturn(page);
//            when(productRestDtoMapper.toBurgerIngredientListDTO(page)).thenReturn(dto);
//
//            mockMvc.perform(get("/api/admin/burgers/ingredients"))
//                    .andExpect(status().isOk());
//
//            verify(listBurgerIngredients, times(1)).handle(isNull(), any(PaginationRequest.class));
//        }
//    }
//
//    @Nested
//    class SearchIngredientsTests {
//        @Test
//        void shouldReturn200() throws Exception {
//            PageResponse<Product> page = new PageResponse<>(List.of(), 0, 10, 0L, 0);
//            when(searchIngredients.handle(eq("queso"), any(PaginationRequest.class))).thenReturn(page);
//            when(productRestDtoMapper.toBurgerIngredientListDTO(page)).thenReturn(
//                    new BurgerIngredientListDTO(List.of(), 0, 10, 0L, 0)
//            );
//
//            mockMvc.perform(get("/api/admin/burgers/ingredients/search").param("name", "queso"))
//                    .andExpect(status().isOk());
//
//            verify(searchIngredients, times(1)).handle(eq("queso"), any(PaginationRequest.class));
//        }
//    }
//
//    @Nested
//    class SecurityTests {
//
//        @Test
//        void adminEndpoint_shouldNotCallUseCase_whenNotAuthenticated() throws Exception {
//            SecurityContextHolder.clearContext();
//            verifyNoInteractions(createMenuBurger);
//        }
//
//        @Test
//        @WithMockUser(roles = "CLIENT")
//        void adminEndpoint_shouldReturn403_whenRoleIsClient() throws Exception {
//            mockMvc.perform(get("/api/admin/burgers/menu"))
//                    .andExpect(status().isForbidden());
//        }
//
//        @Test
//        @WithMockUser(roles = "ADMIN")
//        void adminEndpoint_shouldBeAccessible_whenRoleIsAdmin() throws Exception {
//            PageResponse<Burger> page = new PageResponse<>(List.of(), 0, 10, 0L, 0);
//            when(listBurgers.handle(any(PaginationRequest.class))).thenReturn(page);
//            when(mapper.toMenuBurgerPageResponseDTO(page)).thenReturn(
//                    new MenuBurgerPageResponseDTO(List.of(), 0, 10, 0L, 0));
//            mockMvc.perform(get("/api/admin/burgers/menu"))
//                    .andExpect(status().isOk());
//        }
//    }
//}
