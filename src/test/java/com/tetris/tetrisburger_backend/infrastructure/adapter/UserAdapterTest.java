package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.ListUsersQuery;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.UserEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.UserEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de UserAdapter")
class UserAdapterTest {

    @Mock
    private UserJpaRepository jpa;

    @Mock
    private UserEntityMapper mapper;

    @InjectMocks
    private UserAdapter adapter;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    private User domainUser;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        domainUser = mock(User.class);
        userEntity = mock(UserEntity.class);
    }

    // ── saveUser ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("saveUser")
    class SaveUser {

        @Test
        @DisplayName("debería mapear a entidad, guardar y retornar dominio")
        void shouldMapSaveAndReturnDomain() {
            UserEntity saved = mock(UserEntity.class);
            when(mapper.toEntity(domainUser)).thenReturn(userEntity);
            when(jpa.save(userEntity)).thenReturn(saved);
            when(mapper.toDomain(saved)).thenReturn(domainUser);

            User result = adapter.saveUser(domainUser);

            assertThat(result).isEqualTo(domainUser);
            verify(mapper).toEntity(domainUser);
            verify(jpa).save(userEntity);
            verify(mapper).toDomain(saved);
        }

        @Test
        @DisplayName("debería propagar excepción cuando jpa.save falla")
        void shouldPropagateExceptionWhenSaveFails() {
            when(mapper.toEntity(domainUser)).thenReturn(userEntity);
            when(jpa.save(userEntity)).thenThrow(new RuntimeException("DB error"));

            assertThatThrownBy(() -> adapter.saveUser(domainUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("DB error");
        }
    }

    // ── findUserById ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("findUserById")
    class FindUserById {

        @Test
        @DisplayName("debería retornar usuario mapeado cuando existe")
        void shouldReturnMappedUserWhenExists() {
            when(jpa.findById(1)).thenReturn(Optional.of(userEntity));
            when(mapper.toDomain(userEntity)).thenReturn(domainUser);

            Optional<User> result = adapter.findUserById(1);

            assertThat(result).isPresent().contains(domainUser);
        }

        @Test
        @DisplayName("debería retornar vacío cuando el ID no existe")
        void shouldReturnEmptyWhenIdNotFound() {
            when(jpa.findById(999)).thenReturn(Optional.empty());

            Optional<User> result = adapter.findUserById(999);

            assertThat(result).isEmpty();
            verify(mapper, never()).toDomain(any());
        }

        @Test
        @DisplayName("debería usar findById sin filtro de deletedAt")
        void shouldUseFindByIdWithoutDeletedAtFilter() {
            when(jpa.findById(anyInt())).thenReturn(Optional.empty());

            adapter.findUserById(1);

            verify(jpa).findById(1);
            verify(jpa, never()).findByIdUserAndDeletedAtIsNull(any());
        }
    }

    // ── findUserByEmail (simple) ──────────────────────────────────────────

    @Nested
    @DisplayName("findUserByEmail (Optional)")
    class FindUserByEmail {

        @Test
        @DisplayName("debería retornar usuario cuando email existe y está activo")
        void shouldReturnUserWhenEmailExistsAndActive() {
            when(jpa.findByEmailAndDeletedAtIsNull("user@test.com"))
                    .thenReturn(Optional.of(userEntity));
            when(mapper.toDomain(userEntity)).thenReturn(domainUser);

            Optional<User> result = adapter.findUserByEmail("user@test.com");

            assertThat(result).isPresent().contains(domainUser);
        }

        @Test
        @DisplayName("debería retornar vacío cuando email no existe")
        void shouldReturnEmptyWhenEmailNotFound() {
            when(jpa.findByEmailAndDeletedAtIsNull("ghost@test.com"))
                    .thenReturn(Optional.empty());

            Optional<User> result = adapter.findUserByEmail("ghost@test.com");

            assertThat(result).isEmpty();
            verify(mapper, never()).toDomain(any());
        }
    }

    // ── findUserByEmail (paginado) ────────────────────────────────────────

    @Nested
    @DisplayName("findUserByEmail (PageResponse)")
    class FindUserByEmailPaged {

        @Test
        @DisplayName("debería retornar PageResponse con usuarios mapeados")
        void shouldReturnPageResponseWithMappedUsers() {
            PaginationRequest request = mock(PaginationRequest.class);
            when(request.getPage()).thenReturn(0);
            when(request.getSize()).thenReturn(10);
            when(request.getSortBy()).thenReturn("email");

            Page<UserEntity> page = new PageImpl<>(
                    List.of(userEntity),
                    PageRequest.of(0, 10, Sort.by("email").ascending()),
                    1
            );

            when(jpa.findByEmailContainingIgnoreCaseAndDeletedAtIsNull(
                    eq("test"), any(Pageable.class))).thenReturn(page);
            when(mapper.toDomain(userEntity)).thenReturn(domainUser);

            PageResponse<User> result = adapter.findUserByEmail("test", request);

            assertThat(result.content()).hasSize(1).contains(domainUser);
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(10);
            assertThat(result.totalPages()).isEqualTo(1);
            assertThat(result.totalPages()).isEqualTo(1);
        }

        @Test
        @DisplayName("debería construir Pageable con los parámetros del request")
        void shouldBuildPageableFromRequest() {
            PaginationRequest request = mock(PaginationRequest.class);
            when(request.getPage()).thenReturn(2);
            when(request.getSize()).thenReturn(5);
            when(request.getSortBy()).thenReturn("email");

            Page<UserEntity> emptyPage = new PageImpl<>(
                    List.of(),
                    PageRequest.of(2, 5, Sort.by("email").ascending()),
                    0
            );

            when(jpa.findByEmailContainingIgnoreCaseAndDeletedAtIsNull(
                    anyString(), pageableCaptor.capture())).thenReturn(emptyPage);

            adapter.findUserByEmail("any", request);

            Pageable captured = pageableCaptor.getValue();
            assertThat(captured.getPageNumber()).isEqualTo(2);
            assertThat(captured.getPageSize()).isEqualTo(5);
            assertThat(captured.getSort()).isEqualTo(Sort.by("email").ascending());
        }

        @Test
        @DisplayName("debería retornar PageResponse vacío cuando no hay coincidencias")
        void shouldReturnEmptyPageResponseWhenNoMatches() {
            PaginationRequest request = mock(PaginationRequest.class);
            when(request.getPage()).thenReturn(0);
            when(request.getSize()).thenReturn(10);
            when(request.getSortBy()).thenReturn("email");

            Page<UserEntity> emptyPage = new PageImpl<>(List.of());
            when(jpa.findByEmailContainingIgnoreCaseAndDeletedAtIsNull(
                    anyString(), any(Pageable.class))).thenReturn(emptyPage);

            PageResponse<User> result = adapter.findUserByEmail("xyz", request);

            assertThat(result.content()).isEmpty();
            assertThat(result.totalPages()).isZero();
        }
    }

    // ── findAllUsers ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("findAllUsers")
    class FindAllUsers {

        @Test
        @DisplayName("debería retornar PageResponse con todos los usuarios activos")
        void shouldReturnPageResponseWithAllActiveUsers() {
            ListUsersQuery query = mock(ListUsersQuery.class);
            when(query.page()).thenReturn(0);
            when(query.size()).thenReturn(10);
            when(query.sortBy()).thenReturn("email");

            Page<UserEntity> page = new PageImpl<>(
                    List.of(userEntity),
                    PageRequest.of(0, 10, Sort.by("email").ascending()),
                    1
            );

            when(jpa.findAllByDeletedAtIsNull(any(Pageable.class))).thenReturn(page);
            when(mapper.toDomain(userEntity)).thenReturn(domainUser);

            PageResponse<User> result = adapter.findAllUsers(query);

            assertThat(result.content()).hasSize(1).contains(domainUser);
            assertThat(result.totalPages()).isEqualTo(1);
        }

        @Test
        @DisplayName("debería construir Pageable con los parámetros del query")
        void shouldBuildPageableFromQuery() {
            ListUsersQuery query = mock(ListUsersQuery.class);
            when(query.page()).thenReturn(1);
            when(query.size()).thenReturn(5);
            when(query.sortBy()).thenReturn("email");

            Page<UserEntity> emptyPage = new PageImpl<>(
                    List.of(),
                    PageRequest.of(1, 5, Sort.by("email").ascending()),
                    0
            );

            when(jpa.findAllByDeletedAtIsNull(pageableCaptor.capture()))
                    .thenReturn(emptyPage);

            adapter.findAllUsers(query);

            Pageable captured = pageableCaptor.getValue();
            assertThat(captured.getPageNumber()).isEqualTo(1);
            assertThat(captured.getPageSize()).isEqualTo(5);
            assertThat(captured.getSort()).isEqualTo(Sort.by("email").ascending());
        }

        @Test
        @DisplayName("debería retornar PageResponse vacío cuando no hay usuarios activos")
        void shouldReturnEmptyPageResponseWhenNoActiveUsers() {
            ListUsersQuery query = mock(ListUsersQuery.class);
            when(query.page()).thenReturn(0);
            when(query.size()).thenReturn(10);
            when(query.sortBy()).thenReturn("email");

            when(jpa.findAllByDeletedAtIsNull(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of()));

            PageResponse<User> result = adapter.findAllUsers(query);

            assertThat(result.content()).isEmpty();
            assertThat(result.totalPages()).isZero();
        }
    }

    // ── deleteUserById ────────────────────────────────────────────────────

    @Nested
    @DisplayName("deleteUserById")
    class DeleteUserById {

        @Test
        @DisplayName("debería llamar a jpa.deleteById con el ID correcto")
        void shouldCallDeleteByIdWithCorrectId() {
            adapter.deleteUserById(1);

            verify(jpa).deleteById(1);
        }

        @Test
        @DisplayName("debería propagar excepción cuando jpa.deleteById falla")
        void shouldPropagateExceptionWhenDeleteFails() {
            doThrow(new RuntimeException("Delete failed")).when(jpa).deleteById(1);

            assertThatThrownBy(() -> adapter.deleteUserById(1))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Delete failed");
        }
    }

    // ── softDeleteUser ────────────────────────────────────────────────────

    @Nested
    @DisplayName("softDeleteUser")
    class SoftDeleteUser {

        @Test
        @DisplayName("debería llamar a softDeleteUser cuando el usuario existe")
        void shouldCallSoftDeleteWhenUserExists() {
            when(jpa.existsById(1)).thenReturn(true);

            adapter.softDeleteUser(1, 99);

            verify(jpa).softDeleteUser(eq(1), any(LocalDateTime.class), eq(99));
        }

        @Test
        @DisplayName("debería lanzar IllegalArgumentException cuando el usuario no existe")
        void shouldThrowIllegalArgumentExceptionWhenUserNotFound() {
            when(jpa.existsById(999)).thenReturn(false);

            assertThatThrownBy(() -> adapter.softDeleteUser(999, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Usuario no encontrado");

            verify(jpa, never()).softDeleteUser(any(), any(), any());
        }

        @Test
        @DisplayName("debería pasar LocalDateTime.now() como deletedAt")
        void shouldPassCurrentTimeAsDeletedAt() {
            when(jpa.existsById(1)).thenReturn(true);

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);
            adapter.softDeleteUser(1, 5);
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
            verify(jpa).softDeleteUser(eq(1), captor.capture(), eq(5));

            LocalDateTime captured = captor.getValue();
            assertThat(captured).isAfter(before).isBefore(after);
        }

        @Test
        @DisplayName("debería pasar el deletedBy correcto")
        void shouldPassCorrectDeletedBy() {
            when(jpa.existsById(1)).thenReturn(true);

            adapter.softDeleteUser(1, 42);

            verify(jpa).softDeleteUser(eq(1), any(LocalDateTime.class), eq(42));
        }
    }

    // ── existsByEmail ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("existsByEmail")
    class ExistsByEmail {

        @Test
        @DisplayName("debería retornar true cuando el email existe y está activo")
        void shouldReturnTrueWhenEmailExistsAndActive() {
            when(jpa.existsByEmailAndDeletedAtIsNull("user@test.com")).thenReturn(true);

            assertThat(adapter.existsByEmail("user@test.com")).isTrue();
        }

        @Test
        @DisplayName("debería retornar false cuando el email no existe")
        void shouldReturnFalseWhenEmailNotExists() {
            when(jpa.existsByEmailAndDeletedAtIsNull("ghost@test.com")).thenReturn(false);

            assertThat(adapter.existsByEmail("ghost@test.com")).isFalse();
        }
    }

    // ── existsById ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("existsById")
    class ExistsById {

        @Test
        @DisplayName("debería retornar true cuando el ID existe y el usuario está activo")
        void shouldReturnTrueWhenIdExistsAndActive() {
            when(jpa.existsByIdUserAndDeletedAtIsNull(1)).thenReturn(true);

            assertThat(adapter.existsById(1)).isTrue();
        }

        @Test
        @DisplayName("debería retornar false cuando el ID no existe o está eliminado")
        void shouldReturnFalseWhenIdNotExistsOrDeleted() {
            when(jpa.existsByIdUserAndDeletedAtIsNull(999)).thenReturn(false);

            assertThat(adapter.existsById(999)).isFalse();
        }
    }
}
