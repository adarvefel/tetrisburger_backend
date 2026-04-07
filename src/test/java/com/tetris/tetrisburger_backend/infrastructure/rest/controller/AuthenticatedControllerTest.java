package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.exception.UnauthorizedException;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticatedControllerTest {

    private static final class TestAuthenticatedController extends AuthenticatedController {
        public Integer extract(UserDetails userDetails) {
            return getUserId(userDetails);
        }
    }

    @Nested
    class SecurityTests {

        @Test
        void shouldExtractUserIdFromCustomUserDetails() {
            TestAuthenticatedController controller = new TestAuthenticatedController();
            CustomUserDetails userDetails = mock(CustomUserDetails.class);
            when(userDetails.getId()).thenReturn(77);

            Integer userId = controller.extract(userDetails);

            assertThat(userId).isEqualTo(77);
        }

        @Test
        void shouldThrowWhenUserDetailsIsNotCustomUserDetails() {
            TestAuthenticatedController controller = new TestAuthenticatedController();
            UserDetails userDetails = mock(UserDetails.class);

            assertThatThrownBy(() -> controller.extract(userDetails))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("Usuario no autenticado correctamente");
        }

        @Test
        void shouldRejectWhenSecurityContextIsCleared() {
            SecurityContextHolder.clearContext();
            TestAuthenticatedController controller = new TestAuthenticatedController();
            UserDetails userDetails = mock(UserDetails.class);

            assertThatThrownBy(() -> controller.extract(userDetails))
                    .isInstanceOf(UnauthorizedException.class);
        }
    }
}
