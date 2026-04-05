package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tetris.tetrisburger_backend.domain.common.ImageStatus;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.exception.EntityNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Menu;
import com.tetris.tetrisburger_backend.domain.port.in.menu.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.BurgerExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.GlobalExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ProductExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ValidationExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu.CreateMenuRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu.MenuResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu.UpdateMenuRequestDTO;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

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

    private void mockAuthenticatedUser(Long userId) {
        lenient().when(mockUserDetails.getId()).thenReturn(userId.intValue());
        lenient().when(mockUserDetails.getUsername()).thenReturn(userId.toString());
    }

    private MenuResponseDTO sampleMenuDto() {
        return new MenuResponseDTO(
                1, "Menú", "Desc", true, null, ImageStatus.NONE,
                null, List.of(), LocalDateTime.now(), LocalDateTime.now(), 1, 1
        );
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

        mockMenu = mock(Menu.class);
        lenient().when(mockMenu.getIdMenu()).thenReturn(1);
        lenient().when(mockMenu.getName()).thenReturn("Menú");
    }

    @Nested
    class CreateMenuTests {
        @Test
        void shouldReturn201WhenCreatedSuccessfully() throws Exception {
            CreateMenuRequestDTO dto = new CreateMenuRequestDTO("Lunch", "Desc", true, 1, List.of());
            MockMultipartFile dataPart = new MockMultipartFile(
                    "data", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(dto)
            );

            when(mapper.toCreateCommand(any(), isNull(), eq(1))).thenReturn(mock());
            when(createMenu.handle(any())).thenReturn(mockMenu);
            when(mapper.toResponseDTO(mockMenu)).thenReturn(sampleMenuDto());

            mockMvc.perform(multipart("/api/menu").file(dataPart))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idMenu").value(1));

            verify(createMenu, times(1)).handle(any());
        }

        @Test
        void shouldReturn400WhenDataPartMissing() throws Exception {
            mockMvc.perform(multipart("/api/menu"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(createMenu);
        }
    }

    @Nested
    class UpdateMenuTests {
        @Test
        void shouldReturn200WhenUpdated() throws Exception {
            UpdateMenuRequestDTO dto = new UpdateMenuRequestDTO("Nuevo", "D", false, 1, List.of());
            when(mapper.toUpdateCommand(eq(1), any(), eq(1))).thenReturn(mock());
            when(updateMenu.handle(any())).thenReturn(mockMenu);
            when(mapper.toResponseDTO(mockMenu)).thenReturn(sampleMenuDto());

            mockMvc.perform(put("/api/menu/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());

            verify(updateMenu, times(1)).handle(any());
        }

        @Test
        void shouldReturn400WhenWrongContentType() throws Exception {
            mockMvc.perform(put("/api/menu/1")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("x"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(updateMenu);
        }
    }

    @Nested
    class UpdateMenuImageTests {
        @Test
        void shouldReturn200WhenImageSent() throws Exception {
            MockMultipartFile img = new MockMultipartFile(
                    "image", "m.png", MediaType.IMAGE_PNG_VALUE, "bytes".getBytes()
            );

            when(mapper.toUpdateImageCommand(eq(1), any(), eq(1))).thenReturn(mock());
            when(updateMenuImage.handle(any())).thenReturn(mockMenu);
            when(mapper.toResponseDTO(mockMenu)).thenReturn(sampleMenuDto());

            mockMvc.perform(multipart("/api/menu/1/image")
                            .file(img)
                            .with(r -> {
                                r.setMethod("PATCH");
                                return r;
                            }))
                    .andExpect(status().isOk());

            verify(updateMenuImage, times(1)).handle(any());
        }

        @Test
        void shouldReturn400WhenImageMissing() throws Exception {
            mockMvc.perform(multipart("/api/menu/1/image")
                            .with(r -> {
                                r.setMethod("PATCH");
                                return r;
                            }))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(updateMenuImage);
        }
    }

    @Nested
    class DeleteMenuTests {
        @Test
        void shouldReturn200WhenDeleted() throws Exception {
            when(deleteMenu.handle(1, 1)).thenReturn(mockMenu);

            mockMvc.perform(delete("/api/menu/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1));

            verify(deleteMenu, times(1)).handle(1, 1);
        }
    }

    @Nested
    class GetMenuByIdTests {
        @Test
        void shouldReturn200WhenFound() throws Exception {
            when(getMenuById.handle(1)).thenReturn(mockMenu);
            when(mapper.toResponseDTO(mockMenu)).thenReturn(sampleMenuDto());

            mockMvc.perform(get("/api/menu/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idMenu").value(1));

            verify(getMenuById, times(1)).handle(1);
        }

        @Test
        void shouldReturn404WhenNotFound() throws Exception {
            when(getMenuById.handle(99)).thenThrow(new EntityNotFoundException("Menú no encontrado con id: 99"));

            mockMvc.perform(get("/api/menu/99"))
                    .andExpect(status().isNotFound());

            verify(getMenuById, times(1)).handle(99);
        }
    }

    @Nested
    class ListMenuTests {
        @Test
        void shouldReturn200WithPage() throws Exception {
            PageResponse<Menu> page = new PageResponse<>(List.of(mockMenu), 0, 12, 1L, 1);
            when(listMenu.handle(any(PaginationRequest.class))).thenReturn(page);
            when(mapper.toResponseDTO(mockMenu)).thenReturn(sampleMenuDto());

            mockMvc.perform(get("/api/menu"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].idMenu").value(1));

            verify(listMenu, times(1)).handle(any(PaginationRequest.class));
        }
    }

    @Nested
    class SecurityTests {

        @Test
        void adminEndpoint_shouldNotCallUseCase_whenNotAuthenticated() throws Exception {
            SecurityContextHolder.clearContext();
            verifyNoInteractions(createMenu);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        void adminEndpoint_shouldReturn403_whenRoleIsClient() throws Exception {
            mockMvc.perform(post("/api/menu")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void adminEndpoint_shouldBeAccessible_whenRoleIsAdmin() throws Exception {
            PageResponse<Menu> page = new PageResponse<>(List.of(), 0, 12, 0L, 0);
            when(listMenu.handle(any(PaginationRequest.class))).thenReturn(page);
            mockMvc.perform(get("/api/menu"))
                    .andExpect(status().isOk());
        }
    }
}
