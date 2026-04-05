package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tetris.tetrisburger_backend.domain.common.ImageStatus;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.*;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.DeleteUserByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.ListUsersQuery;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.SearchUsersByEmailQuery;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.BurgerExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.GlobalExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ProductExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ValidationExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.UserRestDtoMapper;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock private CreateUserByAdmin createUserByAdmin;
    @Mock private ListUser listUser;
    @Mock private GetUserById getUserById;
    @Mock private UpdateUserByAdmin updateUserByAdmin;
    @Mock private UpdateUserImageByAdmin updateUserImageByAdmin;
    @Mock private DeleteUserByAdmin deleteUserByAdmin;
    @Mock private SearchUsersByEmail searchUsersByEmail;
    @Mock private UserRestDtoMapper mapper;
    @Mock private ImageStoragePort imageStoragePort;
    @Mock private ImageValidator imageValidator;

    @InjectMocks private AdminController controller;

    private CustomUserDetails mockUserDetails;
    private User mockUser;

    private void mockAuthenticatedUser(Long userId) {
        lenient().when(mockUserDetails.getId()).thenReturn(userId.intValue());
        lenient().when(mockUserDetails.getUsername()).thenReturn(userId.toString());
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

        mockUser = mock(User.class);
        lenient().when(mockUser.getUserImageKey()).thenReturn(null);
    }

    private String createUserJsonBody() {
        return """
                {"userName":"Juan Pérez","email":"juan@test.com","password":"secret12","role":"ADMIN","phone":null}
                """;
    }

    @Nested
    class CreateUserTests {
        @Test
        void shouldReturn201WhenCreatedSuccessfully() throws Exception {
            MockMultipartFile dataPart = new MockMultipartFile(
                    "data", "", MediaType.APPLICATION_JSON_VALUE, createUserJsonBody().getBytes()
            );

            when(mapper.toCreateUserByAdminCommand(any(), isNull(), eq(1))).thenReturn(mock());
            when(createUserByAdmin.handle(any())).thenReturn(mockUser);
            when(mapper.toCreateUserByAdminResponseDTO(eq(mockUser), isNull(), eq(ImageStatus.NONE)))
                    .thenReturn(mock(CreateUserByAdminResponseDTO.class));

            mockMvc.perform(multipart("/api/admin/users").file(dataPart))
                    .andExpect(status().isCreated());

            verify(createUserByAdmin, times(1)).handle(any());
            verify(imageValidator, never()).validate(any());
        }

        @Test
        void shouldReturn400WhenInvalidRequest() throws Exception {
            MockMultipartFile dataPart = new MockMultipartFile(
                    "data", "", MediaType.APPLICATION_JSON_VALUE, "{}".getBytes()
            );

            mockMvc.perform(multipart("/api/admin/users").file(dataPart))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(createUserByAdmin);
        }

        @Test
        void shouldReturn201WithImage() throws Exception {
            MockMultipartFile dataPart = new MockMultipartFile(
                    "data", "", MediaType.APPLICATION_JSON_VALUE, createUserJsonBody().getBytes()
            );
            MockMultipartFile img = new MockMultipartFile(
                    "userImage", "a.png", MediaType.IMAGE_PNG_VALUE, "bytes".getBytes()
            );

            doNothing().when(imageValidator).validate(any());
            when(mapper.toCreateUserByAdminCommand(any(), any(), eq(1))).thenReturn(mock());
            when(createUserByAdmin.handle(any())).thenReturn(mockUser);
            when(mapper.toCreateUserByAdminResponseDTO(eq(mockUser), isNull(), eq(ImageStatus.PENDING)))
                    .thenReturn(mock(CreateUserByAdminResponseDTO.class));

            mockMvc.perform(multipart("/api/admin/users").file(dataPart).file(img))
                    .andExpect(status().isCreated());

            verify(imageValidator, times(1)).validate(any());
            verify(createUserByAdmin, times(1)).handle(any());
        }
    }

    @Nested
    class ListUsersTests {
        @Test
        void shouldReturn200WhenListed() throws Exception {
            PageResponse<User> page = new PageResponse<>(List.of(mockUser), 0, 10, 1L, 1);
            when(listUser.execute(any(ListUsersQuery.class))).thenReturn(page);
            when(mapper.toUserResponseDTO(eq(mockUser), isNull(), eq(ImageStatus.NONE)))
                    .thenReturn(mock(UserResponseDTO.class));

            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isOk());

            verify(listUser, times(1)).execute(any(ListUsersQuery.class));
        }
    }

    @Nested
    class GetUserByIdTests {
        @Test
        void shouldReturn200WhenFound() throws Exception {
            when(getUserById.handle(5)).thenReturn(mockUser);
            when(mapper.toUserResponseDTO(mockUser, null, ImageStatus.NONE)).thenReturn(mock(UserResponseDTO.class));

            mockMvc.perform(get("/api/admin/users/5"))
                    .andExpect(status().isOk());

            verify(getUserById, times(1)).handle(5);
        }

        @Test
        void shouldReturn404WhenNotFound() throws Exception {
            when(getUserById.handle(404)).thenThrow(new UserNotFoundException(404));

            mockMvc.perform(get("/api/admin/users/404"))
                    .andExpect(status().isNotFound());

            verify(getUserById, times(1)).handle(404);
        }
    }

    @Nested
    class UpdateUserTests {
        @Test
        void shouldReturn200WhenUpdated() throws Exception {
            UpdateUserByAdminRequestDTO dto = new UpdateUserByAdminRequestDTO(
                    "Nombre Largo", "mail@test.com", null, "ADMIN", null
            );

            when(mapper.toUpdateUserByAdminCommand(eq(1), any(), eq(1))).thenReturn(mock());
            when(updateUserByAdmin.handle(any())).thenReturn(mockUser);
            when(mapper.toUpdateUserByAdminResponseDTO(mockUser, null, ImageStatus.NONE))
                    .thenReturn(mock(UpdateUserByAdminResponseDTO.class));

            mockMvc.perform(put("/api/admin/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());

            verify(updateUserByAdmin, times(1)).handle(any());
        }

        @Test
        void shouldReturn400WhenEmailInvalid() throws Exception {
            String bad = "{\"userName\":\"abc\",\"email\":\"no-email\",\"password\":null,\"role\":null,\"phone\":null}";
            mockMvc.perform(put("/api/admin/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bad))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(updateUserByAdmin);
        }
    }

    @Nested
    class UpdateUserImageTests {
        @Test
        void shouldReturn200WhenImageSent() throws Exception {
            MockMultipartFile img = new MockMultipartFile(
                    "userImage", "f.png", MediaType.IMAGE_PNG_VALUE, "data".getBytes()
            );

            doNothing().when(imageValidator).validate(any());
            when(mapper.toUpdateUserImageByAdminCommand(eq(2), any(), eq(1))).thenReturn(mock());
            when(updateUserImageByAdmin.handle(any())).thenReturn(mockUser);
            when(mapper.toUpdateUserByAdminResponseDTO(mockUser, null, ImageStatus.PENDING))
                    .thenReturn(mock(UpdateUserByAdminResponseDTO.class));

            mockMvc.perform(multipart("/api/admin/users/2/image")
                            .file(img)
                            .with(r -> {
                                r.setMethod("PUT");
                                return r;
                            }))
                    .andExpect(status().isOk());

            verify(updateUserImageByAdmin, times(1)).handle(any());
        }

        @Test
        void shouldReturn400WhenImageMissing() throws Exception {
            mockMvc.perform(multipart("/api/admin/users/1/image")
                            .with(r -> {
                                r.setMethod("PUT");
                                return r;
                            }))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(updateUserImageByAdmin);
        }
    }

    @Nested
    class DeleteUserTests {
        @Test
        void shouldReturn200WhenDeleted() throws Exception {
            when(mapper.toDeleteUserByAdminDTO(3)).thenReturn(mock(DeleteUserByAdminDTO.class));

            mockMvc.perform(delete("/api/admin/users/3"))
                    .andExpect(status().isOk());

            verify(deleteUserByAdmin, times(1)).handle(argThat((DeleteUserByAdminCommand cmd) ->
                    cmd.idUser() == 3 && cmd.deletedBy() == 1));
        }
    }

    @Nested
    class SearchUsersByEmailTests {
        @Test
        void shouldReturn200WhenFound() throws Exception {
            PageResponse<User> page = new PageResponse<>(List.of(mockUser), 0, 10, 1L, 1);
            when(searchUsersByEmail.handle(any(SearchUsersByEmailQuery.class), any(PaginationRequest.class)))
                    .thenReturn(page);
            when(mapper.toUserResponseDTO(any(), any(), any())).thenReturn(mock(UserResponseDTO.class));

            mockMvc.perform(get("/api/admin/users/by-email").param("email", "juan@test.com"))
                    .andExpect(status().isOk());

            verify(searchUsersByEmail, times(1)).handle(any(SearchUsersByEmailQuery.class), any(PaginationRequest.class));
        }

        @Test
        void shouldReturn400WhenEmailParamMissing() throws Exception {
            mockMvc.perform(get("/api/admin/users/by-email"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(searchUsersByEmail);
        }
    }

    @Nested
    class SecurityTests {

        @Test
        void adminEndpoint_shouldNotCallUseCase_whenNotAuthenticated() throws Exception {
            SecurityContextHolder.clearContext();
            verifyNoInteractions(createUserByAdmin);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        void adminEndpoint_shouldReturn403_whenRoleIsClient() throws Exception {
            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void adminEndpoint_shouldBeAccessible_whenRoleIsAdmin() throws Exception {
            PageResponse<User> page = new PageResponse<>(List.of(), 0, 10, 0L, 0);
            when(listUser.execute(any())).thenReturn(page);
            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isOk());
        }
    }
}
