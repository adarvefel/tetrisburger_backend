package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.*;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

        mockUser = mock(User.class);
    }

    @Nested
    class GetProfileTests {
        @Test
        void shouldGetProfileAndReturn200() throws Exception {
            when(getUserProfile.execute(any())).thenReturn(mockUser);

            mockMvc.perform(get("/api/profile"))
                    .andExpect(status().isOk());

            verify(getUserProfile).execute(any());
        }
    }

    @Nested
    class UpdateProfileTests {
        @Test
        void shouldUpdateProfileAndReturn200() throws Exception {
            when(mapper.toUpdateProfileUserCommand(eq(1), any())).thenReturn(mock());
            when(updateProfileUser.handle(eq(1), any())).thenReturn(mockUser);

            mockMvc.perform(put("/api/profile")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isOk());

            verify(updateProfileUser).handle(eq(1), any());
        }
    }

    @Nested
    class UpdateProfileImageTests {
        @Test
        void shouldUpdateImageAndReturn200() throws Exception {
            MockMultipartFile imagePart = new MockMultipartFile("profileImage", "img.png", MediaType.IMAGE_PNG_VALUE, "img".getBytes());

            when(updateProfileImage.handle(eq(1), any())).thenReturn(mockUser);

            mockMvc.perform(multipart("/api/profile/image")
                    .file(imagePart)
                    .with(req -> { req.setMethod("PATCH"); return req; }))
                    .andExpect(status().isOk());

            verify(updateProfileImage).handle(eq(1), any());
        }
    }

    @Nested
    class DeleteProfileTests {
        @Test
        void shouldDeleteProfileAndReturn200() throws Exception {
            mockMvc.perform(delete("/api/profile")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"password\": \"password\"}"))
                    .andExpect(status().isOk());

            verify(deleteProfileUser).handle(any());
        }
    }
}
