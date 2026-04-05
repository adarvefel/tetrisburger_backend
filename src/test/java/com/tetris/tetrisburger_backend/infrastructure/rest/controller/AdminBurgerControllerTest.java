package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.*;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.*;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateMenuBurgerImageCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.query.SearchMenuBurgersQuery;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.BurgerRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.ProductRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.validator.ImageValidator;
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
class AdminBurgerControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private CreateMenuBurger createMenuBurger;
    @Mock private GetBurgerById getBurgerById;
    @Mock private ListBurgers listBurgers;
    @Mock private UpdateMenuBurger updateMenuBurger;
    @Mock private UpdateMenuBurgerImage updateMenuBurgerImage;
    @Mock private DeleteMenuBurger deleteMenuBurger;
    @Mock private SearchMenuBurgers searchMenuBurgers;
    @Mock private BurgerRestDtoMapper mapper;
    @Mock private ListBurgerIngredients listBurgerIngredients;
    @Mock private ProductRestDtoMapper productRestDtoMapper;
    @Mock private SearchIngredients searchIngredients;
    @Mock private ImageValidator imageValidator;

    @InjectMocks private AdminBurgerController controller;

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
    class CreateMenuBurgerTests {
        @Test
        void shouldCreateBurgerAndReturn201() throws Exception {
            MockMultipartFile dataPart = new MockMultipartFile("data", "", MediaType.APPLICATION_JSON_VALUE, "{}".getBytes());
            
            when(mapper.toCreateBurgerCommand(any(), any(), eq(1))).thenReturn(mock());
            when(createMenuBurger.handle(any())).thenReturn(mockBurger);

            mockMvc.perform(multipart("/api/admin/burgers/menu")
                    .file(dataPart))
                    .andExpect(status().isCreated());

            verify(createMenuBurger).handle(any());
        }
    }

    @Nested
    class UpdateMenuBurgerTests {
        @Test
        void shouldUpdateBurgerAndReturn200() throws Exception {
            when(mapper.toUpdateMenuBurgerCommand(eq(1), any(), eq(1))).thenReturn(mock());
            when(updateMenuBurger.handle(any())).thenReturn(mockBurger);

            mockMvc.perform(put("/api/admin/burgers/menu/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isOk());

            verify(updateMenuBurger).handle(any());
        }
    }

    @Nested
    class UpdateMenuBurgerImageTests {
        @Test
        void shouldUpdateImageAndReturn200() throws Exception {
            MockMultipartFile imagePart = new MockMultipartFile("burgerImage", "img.png", MediaType.IMAGE_PNG_VALUE, "img".getBytes());

            when(updateMenuBurgerImage.handle(any(UpdateMenuBurgerImageCommand.class))).thenReturn(mockBurger);

            mockMvc.perform(multipart("/api/admin/burgers/menu/1/image")
                    .file(imagePart)
                    .with(req -> { req.setMethod("PATCH"); return req; }))
                    .andExpect(status().isOk());

            verify(updateMenuBurgerImage).handle(any());
        }
    }

    @Nested
    class DeleteMenuBurgerTests {
        @Test
        void shouldDeleteBurgerAndReturn200() throws Exception {
            mockMvc.perform(delete("/api/admin/burgers/menu/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(deleteMenuBurger).handle(1, 1);
        }
    }

    @Nested
    class ListMenuBurgersTests {
        @Test
        void shouldListBurgersAndReturn200() throws Exception {
            PageResponse<Burger> pageResponse = new PageResponse<>(List.of(mockBurger), 0, 10, 1L, 1);

            when(listBurgers.handle(any(PaginationRequest.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/api/admin/burgers/menu"))
                    .andExpect(status().isOk());

            verify(listBurgers).handle(any());
        }
    }

    @Nested
    class SearchMenuBurgersTests {
        @Test
        void shouldSearchBurgersAndReturn200() throws Exception {
            PageResponse<Burger> pageResponse = new PageResponse<>(List.of(mockBurger), 0, 10, 1L, 1);

            when(searchMenuBurgers.search(any(SearchMenuBurgersQuery.class), any(PaginationRequest.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/api/admin/burgers/menu/search").param("name", "Test"))
                    .andExpect(status().isOk());

            verify(searchMenuBurgers).search(any(), any());
        }
    }

    @Nested
    class GetByIdTests {
        @Test
        void shouldReturnBurgerById() throws Exception {
            when(getBurgerById.execute(1)).thenReturn(mockBurger);

            mockMvc.perform(get("/api/admin/burgers/1"))
                    .andExpect(status().isOk());

            verify(getBurgerById).execute(1);
        }
    }
}
