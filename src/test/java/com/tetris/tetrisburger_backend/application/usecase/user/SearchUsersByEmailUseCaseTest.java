package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.SearchUsersByEmailQuery;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de SearchUsersByEmailUseCase")
class SearchUsersByEmailUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SearchUsersByEmailUseCase useCase;

    // ── handle ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("handle")
    class Handle {

        @Test
        @DisplayName("debería delegar al repositorio y retornar el PageResponse")
        void shouldDelegateToRepositoryAndReturnPageResponse() {
            SearchUsersByEmailQuery query = mock(SearchUsersByEmailQuery.class);
            PaginationRequest request = mock(PaginationRequest.class);
            PageResponse<User> expected = mock(PageResponse.class);

            when(query.email()).thenReturn("user@test.com");
            when(userRepository.findUserByEmail("user@test.com", request)).thenReturn(expected);

            PageResponse<User> result = useCase.handle(query, request);

            assertThat(result).isEqualTo(expected);
            verify(userRepository).findUserByEmail("user@test.com", request);
        }

        @Test
        @DisplayName("debería pasar el email exacto del query al repositorio")
        void shouldPassExactEmailFromQueryToRepository() {
            SearchUsersByEmailQuery query = mock(SearchUsersByEmailQuery.class);
            PaginationRequest request = mock(PaginationRequest.class);

            when(query.email()).thenReturn("specific@email.com");
            when(userRepository.findUserByEmail(anyString(), any())).thenReturn(mock(PageResponse.class));

            useCase.handle(query, request);

            verify(userRepository).findUserByEmail(eq("specific@email.com"), eq(request));
        }

        @Test
        @DisplayName("debería pasar el mismo PaginationRequest al repositorio sin modificarlo")
        void shouldPassSamePaginationRequestToRepository() {
            SearchUsersByEmailQuery query = mock(SearchUsersByEmailQuery.class);
            PaginationRequest request = mock(PaginationRequest.class);

            when(query.email()).thenReturn("user@test.com");
            when(userRepository.findUserByEmail(anyString(), any())).thenReturn(mock(PageResponse.class));

            useCase.handle(query, request);

            verify(userRepository).findUserByEmail(anyString(), eq(request));
        }

        @Test
        @DisplayName("debería retornar PageResponse vacío cuando el repositorio no encuentra coincidencias")
        void shouldReturnEmptyPageResponseWhenNoMatches() {
            SearchUsersByEmailQuery query = mock(SearchUsersByEmailQuery.class);
            PaginationRequest request = mock(PaginationRequest.class);

            PageResponse<User> emptyResponse = mock(PageResponse.class);
            when(emptyResponse.content()).thenReturn(List.of());
            when(emptyResponse.totalElements()).thenReturn(0L);

            when(query.email()).thenReturn("nonexistent@test.com");
            when(userRepository.findUserByEmail("nonexistent@test.com", request))
                    .thenReturn(emptyResponse);

            PageResponse<User> result = useCase.handle(query, request);

            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isZero();
        }

        @Test
        @DisplayName("debería llamar al repositorio exactamente una vez")
        void shouldCallRepositoryExactlyOnce() {
            SearchUsersByEmailQuery query = mock(SearchUsersByEmailQuery.class);
            PaginationRequest request = mock(PaginationRequest.class);

            when(query.email()).thenReturn("user@test.com");
            when(userRepository.findUserByEmail(anyString(), any())).thenReturn(mock(PageResponse.class));

            useCase.handle(query, request);

            verify(userRepository, times(1)).findUserByEmail(anyString(), any());
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("debería propagar excepción cuando el repositorio falla")
        void shouldPropagateExceptionWhenRepositoryFails() {
            SearchUsersByEmailQuery query = mock(SearchUsersByEmailQuery.class);
            PaginationRequest request = mock(PaginationRequest.class);

            when(query.email()).thenReturn("user@test.com");
            when(userRepository.findUserByEmail(anyString(), any()))
                    .thenThrow(new RuntimeException("DB error"));

            assertThatThrownBy(() -> useCase.handle(query, request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("DB error");
        }
    }
}
