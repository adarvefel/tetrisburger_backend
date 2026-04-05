package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.burger.GetFeaturedBurgers;
import com.tetris.tetrisburger_backend.domain.port.in.burger.ListBurgerIngredients;
import com.tetris.tetrisburger_backend.domain.port.in.burger.SearchIngredients;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.UpdateCustomBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.client.CreateCustomBurger;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin.MenuBurgerResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client.BurgerResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client.CreateCustomBurgerRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client.UpdateCustomBurgerRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.BurgerIngredientListDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.BurgerRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.ProductRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserBurgerControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private CreateCustomBurger createCustomBurger;
    @Mock private ListBurgerIngredients listBurgerIngredients;
    @Mock private SearchIngredients searchIngredients;
    @Mock private UpdateCustomBurger updateCustomBurger;
    @Mock private ProductRestDtoMapper productRestDtoMapper;
    @Mock private GetFeaturedBurgers getFeaturedBurgers;
    @Mock private BurgerRestDtoMapper mapper;

    @InjectMocks private UserBurgerController controller;

    private CustomUserDetails mockUserDetails;
    private Burger mockBurger;

    @BeforeEach
    void setUp() {
        mockUserDetails = mock(CustomUserDetails.class);
        lenient().when(mockUserDetails.getId()).thenReturn(1);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterAnnotation(AuthenticationPrincipal.class) != null;
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return mockUserDetails;
                    }
                })
                .build();

        mockBurger = mock(Burger.class);
    }

    @Nested
    class CreateCustomBurgerTests {
        @Test
        void shouldCreateBurgerAndReturn201() throws Exception {
            when(mapper.toCreateCustomBurgerCommand(any(), eq(1))).thenReturn(mock());
            when(createCustomBurger.handle(any())).thenReturn(mockBurger);

            mockMvc.perform(post("/api/burgers/custom")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isCreated());

            verify(createCustomBurger).handle(any());
        }
    }

    @Nested
    class SearchIngredientsTests {
        @Test
        void shouldSearchIngredientsAndReturn200() throws Exception {
            PageResponse<Product> pageResponse = new PageResponse<>(List.of(), 0, 10, 0L, 0);
            BurgerIngredientListDTO responseDTO = new BurgerIngredientListDTO(List.of(), 0, 10, 0L, 0);

            when(searchIngredients.handle(eq("test"), any(PaginationRequest.class))).thenReturn(pageResponse);
            when(productRestDtoMapper.toBurgerIngredientListDTO(pageResponse)).thenReturn(responseDTO);

            mockMvc.perform(get("/api/burgers/ingredients/search").param("name", "test"))
                    .andExpect(status().isOk());

            verify(searchIngredients).handle(any(), any());
        }
    }

    @Nested
    class UpdateCustomBurgerTests {
        @Test
        void shouldUpdateBurgerAndReturn200() throws Exception {
            when(mapper.toUpdateCustomBurgerCommand(eq(1), any(), eq(1))).thenReturn(mock());
            when(updateCustomBurger.handle(any())).thenReturn(mockBurger);

            mockMvc.perform(put("/api/burgers/custom/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isOk());

            verify(updateCustomBurger).handle(any());
        }
    }

    @Nested
    class GetFeaturedBurgersTests {
        @Test
        void shouldReturnFeaturedBurgersAnd200() throws Exception {
            when(getFeaturedBurgers.handle()).thenReturn(List.of(mockBurger));

            mockMvc.perform(get("/api/burgers/featured"))
                    .andExpect(status().isOk());

            verify(getFeaturedBurgers).handle();
        }
    }
}
