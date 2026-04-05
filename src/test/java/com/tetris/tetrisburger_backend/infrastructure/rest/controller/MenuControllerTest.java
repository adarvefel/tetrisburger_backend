package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Menu;
import com.tetris.tetrisburger_backend.domain.port.in.menu.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.MenuRestDtoMapper;
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
class MenuControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private CreateMenu createMenu;
    @Mock private UpdateMenu updateMenu;
    @Mock private DeleteMenu deleteMenu;
    @Mock private GetMenuById getMenuById;
    @Mock private ListMenu listMenu;
    @Mock private UpdateMenuImage updateMenuImage;
    @Mock private MenuRestDtoMapper mapper;

    @InjectMocks private MenuController controller;

    private CustomUserDetails mockUserDetails;
    private Menu mockMenu;

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

        mockMenu = mock(Menu.class);
    }

    @Nested
    class CreateMenuTests {
        @Test
        void shouldCreateMenuAndReturn201() throws Exception {
            MockMultipartFile dataPart = new MockMultipartFile("data", "", MediaType.APPLICATION_JSON_VALUE, "{}".getBytes());

            when(mapper.toCreateCommand(any(), any(), eq(1))).thenReturn(mock());
            when(createMenu.handle(any())).thenReturn(mockMenu);

            mockMvc.perform(multipart("/api/menu")
                    .file(dataPart))
                    .andExpect(status().isCreated());

            verify(createMenu).handle(any());
        }
    }

    @Nested
    class UpdateMenuTests {
        @Test
        void shouldUpdateMenuAndReturn200() throws Exception {
            when(mapper.toUpdateCommand(eq(1), any(), eq(1))).thenReturn(mock());
            when(updateMenu.handle(any())).thenReturn(mockMenu);

            mockMvc.perform(put("/api/menu/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isOk());

            verify(updateMenu).handle(any());
        }
    }

    @Nested
    class UpdateMenuImageTests {
        @Test
        void shouldUpdateImageAndReturn200() throws Exception {
            MockMultipartFile imagePart = new MockMultipartFile("image", "img.png", MediaType.IMAGE_PNG_VALUE, "img".getBytes());

            when(mapper.toUpdateImageCommand(eq(1), any(), eq(1))).thenReturn(mock());
            when(updateMenuImage.handle(any())).thenReturn(mockMenu);

            mockMvc.perform(multipart("/api/menu/1/image")
                    .file(imagePart)
                    .with(req -> { req.setMethod("PATCH"); return req; }))
                    .andExpect(status().isOk());

            verify(updateMenuImage).handle(any());
        }
    }

    @Nested
    class DeleteMenuTests {
        @Test
        void shouldDeleteMenuAndReturn200() throws Exception {
            when(deleteMenu.handle(1, 1)).thenReturn(mockMenu);

            mockMvc.perform(delete("/api/menu/1"))
                    .andExpect(status().isOk());

            verify(deleteMenu).handle(1, 1);
        }
    }

    @Nested
    class GetByIdTests {
        @Test
        void shouldReturnMenuByIdAnd200() throws Exception {
            when(getMenuById.handle(1)).thenReturn(mockMenu);

            mockMvc.perform(get("/api/menu/1"))
                    .andExpect(status().isOk());

            verify(getMenuById).handle(1);
        }
    }

    @Nested
    class ListMenuTests {
        @Test
        void shouldListMenuAndReturn200() throws Exception {
            PageResponse<Menu> pageResponse = new PageResponse<>(List.of(mockMenu), 0, 10, 1L, 1);

            when(listMenu.handle(any(PaginationRequest.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/api/menu"))
                    .andExpect(status().isOk());

            verify(listMenu).handle(any());
        }
    }
}
