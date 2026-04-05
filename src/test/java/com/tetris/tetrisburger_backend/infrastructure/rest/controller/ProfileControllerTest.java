package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tetris.tetrisburger_backend.domain.common.ImageStatus;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.*;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.DeleteProfileUserCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.GetUserProfileQuery;
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

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock private GetUserProfile getUserProfile;
    @Mock private UpdateProfileUser updateProfileUser;
    @Mock private UpdateProfileImage updateProfileImage;
    @Mock private DeleteProfileUser deleteProfileUser;
    @Mock private ImageStoragePort imageStoragePort;
    @Mock private UserRestDtoMapper mapper;
    @Mock private ImageValidator imageValidator;

    @InjectMocks private ProfileController controller;

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

    @Nested
    class GetProfileTests {
        @Test
        void shouldReturn200WhenFound() throws Exception {
            when(getUserProfile.execute(any(GetUserProfileQuery.class))).thenReturn(mockUser);
            when(mapper.toGetUserProfileResponseDTO(eq(mockUser), isNull(), eq(ImageStatus.NONE)))
                    .thenReturn(new GetUserProfileResponseDTO(
                            1, "user1", "u@test.com", null, ImageStatus.NONE, "CLIENT", null, LocalDateTime.now()
                    ));

            mockMvc.perform(get("/api/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idUser").value(1));

            verify(getUserProfile, times(1)).execute(any(GetUserProfileQuery.class));
        }

        @Test
        void shouldReturn404WhenUserNotFound() throws Exception {
            when(getUserProfile.execute(any(GetUserProfileQuery.class)))
                    .thenThrow(new UserNotFoundException("Usuario no encontrado"));

            mockMvc.perform(get("/api/profile"))
                    .andExpect(status().isNotFound());

            verify(getUserProfile, times(1)).execute(any(GetUserProfileQuery.class));
        }
    }

    @Nested
    class UpdateProfileTests {
        @Test
        void shouldReturn200WhenUpdated() throws Exception {
            UpdateProfileUserRequestDTO req = new UpdateProfileUserRequestDTO("Nuevo Nombre", null, "3001234567");

            when(mapper.toUpdateProfileUserCommand(eq(1), any())).thenReturn(mock());
            when(updateProfileUser.handle(eq(1), any())).thenReturn(mockUser);
            when(mapper.toUpdateProfileUserResponseDTO(eq(mockUser), isNull(), eq(ImageStatus.NONE)))
                    .thenReturn(mock(UpdateProfileUserResponseDTO.class));

            mockMvc.perform(patch("/api/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());

            verify(updateProfileUser, times(1)).handle(eq(1), any());
        }

        @Test
        void shouldReturn400WhenUserNameTooShort() throws Exception {
            UpdateProfileUserRequestDTO req = new UpdateProfileUserRequestDTO("ab", null, null);

            mockMvc.perform(patch("/api/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(updateProfileUser);
        }
    }

    @Nested
    class UpdateProfileImageTests {
        @Test
        void shouldReturn200WhenImageSent() throws Exception {
            MockMultipartFile img = new MockMultipartFile(
                    "userImage", "p.png", MediaType.IMAGE_PNG_VALUE, "bytes".getBytes()
            );

            doNothing().when(imageValidator).validate(any());
            when(mapper.toUpdateProfileImageCommand(eq(1), any())).thenReturn(mock());
            when(updateProfileImage.handle(eq(1), any())).thenReturn(mockUser);
            when(mapper.toUpdateProfileUserResponseDTO(eq(mockUser), isNull(), eq(ImageStatus.PENDING)))
                    .thenReturn(mock(UpdateProfileUserResponseDTO.class));

            mockMvc.perform(multipart("/api/profile/image")
                            .file(img)
                            .with(r -> {
                                r.setMethod("PUT");
                                return r;
                            }))
                    .andExpect(status().isOk());

            verify(updateProfileImage, times(1)).handle(eq(1), any());
            verify(imageValidator, times(1)).validate(any());
        }

        @Test
        void shouldReturn400WhenImageMissing() throws Exception {
            mockMvc.perform(multipart("/api/profile/image")
                            .with(r -> {
                                r.setMethod("PUT");
                                return r;
                            }))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(updateProfileImage);
        }
    }

    @Nested
    class DeleteProfileTests {
        @Test
        void shouldReturn200WhenDeleted() throws Exception {
            doNothing().when(deleteProfileUser).handle(any(DeleteProfileUserCommand.class));
            when(mapper.toDeleteProfileUserDTO(1)).thenReturn(mock(DeleteProfileUserDTO.class));

            mockMvc.perform(delete("/api/profile"))
                    .andExpect(status().isOk());

            verify(deleteProfileUser, times(1)).handle(argThat(cmd -> cmd.idUser().equals(1)));
        }
    }

    @Nested
    class SecurityTests {

        @Test
        void endpoint_shouldNotCallUseCase_whenNotAuthenticated() throws Exception {
            SecurityContextHolder.clearContext();
            verifyNoInteractions(getUserProfile);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        void endpoint_shouldBeAccessible_whenRoleIsClient() throws Exception {
            when(getUserProfile.execute(any())).thenReturn(mockUser);
            when(mapper.toGetUserProfileResponseDTO(any(), any(), any()))
                    .thenReturn(mock(GetUserProfileResponseDTO.class));
            mockMvc.perform(get("/api/profile"))
                    .andExpect(status().isOk());
        }
    }
}
