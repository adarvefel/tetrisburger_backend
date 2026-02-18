package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de UserAdapter")
class UserAdapterTest {

    @Mock
    private UserJpaRepository jpaRepository;

    @Mock
    private UserEntityMapper mapper;

    @InjectMocks
    private UserAdapter userAdapter;

    private User testUser;
    private UserEntity testUserEntity;

    @BeforeEach
    void setUp() {
        testUser = User.createClient("Test User", "test@example.com", "hashedPassword123");
        testUser.setIdUser(1);

        testUserEntity = new UserEntity();
        testUserEntity.setIdUser(1);
        testUserEntity.setEmail("test@example.com");
        testUserEntity.setUserName("Test User");
        testUserEntity.setPassword("hashedPassword123");
        testUserEntity.setRole(Role.CLIENT);
        testUserEntity.setCreatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Pruebas de Guardar Usuario")
    class SaveUserTests {

        @Test
        @DisplayName("Debería guardar el usuario exitosamente")
        void shouldSaveUserSuccessfully() {
            // Given
            when(mapper.toEntity(testUser)).thenReturn(testUserEntity);
            when(jpaRepository.save(testUserEntity)).thenReturn(testUserEntity);
            when(mapper.toDomain(testUserEntity)).thenReturn(testUser);

            // When
            User savedUser = userAdapter.saveUser(testUser);

            // Then
            assertThat(savedUser).isNotNull();
            assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
            assertThat(savedUser.getUserName()).isEqualTo("Test User");
            verify(mapper).toEntity(testUser);
            verify(jpaRepository).save(testUserEntity);
            verify(mapper).toDomain(testUserEntity);
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando falla al guardar usuario")
        void shouldThrowExceptionWhenSaveUserFails() {
            // Given
            when(mapper.toEntity(testUser)).thenReturn(testUserEntity);
            when(jpaRepository.save(testUserEntity))
                    .thenThrow(new RuntimeException("Database connection error"));

            // When & Then
            assertThatThrownBy(() -> userAdapter.saveUser(testUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database connection error");

            verify(mapper).toEntity(testUser);
            verify(jpaRepository).save(testUserEntity);
            verify(mapper, never()).toDomain(any());
        }

        @Test
        @DisplayName("Debería guardar usuario con todos los campos poblados")
        void shouldSaveUserWithAllFieldsPopulated() {
            // Given
            User adminUser = User.createByAdmin(
                    "Admin User", "admin@example.com", "hashedPass",
                    Role.ADMIN, "+573001234567", "img-key", "img.jpg", 1
            );
            adminUser.setIdUser(2);

            UserEntity adminEntity = new UserEntity();
            adminEntity.setIdUser(2);
            adminEntity.setEmail("admin@example.com");
            adminEntity.setUserName("Admin User");
            adminEntity.setRole(Role.ADMIN);

            when(mapper.toEntity(adminUser)).thenReturn(adminEntity);
            when(jpaRepository.save(adminEntity)).thenReturn(adminEntity);
            when(mapper.toDomain(adminEntity)).thenReturn(adminUser);

            // When
            User savedUser = userAdapter.saveUser(adminUser);

            // Then
            assertThat(savedUser).isNotNull();
            assertThat(savedUser.getRole()).isEqualTo(Role.ADMIN);
            verify(jpaRepository).save(adminEntity);
        }
    }

    @Nested
    @DisplayName("Pruebas de Buscar Usuario")
    class FindUserTests {

        @Test
        @DisplayName("Debería encontrar usuario por ID exitosamente")
        void shouldFindUserByIdSuccessfully() {
            // Given
            Integer userId = 1;
            when(jpaRepository.findById(userId)).thenReturn(Optional.of(testUserEntity));
            when(mapper.toDomain(testUserEntity)).thenReturn(testUser);

            // When
            Optional<User> foundUser = userAdapter.findUserById(userId);

            // Then
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getIdUser()).isEqualTo(userId);
            assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
            verify(jpaRepository).findById(userId);
            verify(mapper).toDomain(testUserEntity);
        }

        @Test
        @DisplayName("Debería retornar vacío cuando no se encuentra usuario por ID")
        void shouldReturnEmptyWhenUserNotFoundById() {
            // Given
            Integer userId = 999;
            when(jpaRepository.findById(userId)).thenReturn(Optional.empty());

            // When
            Optional<User> foundUser = userAdapter.findUserById(userId);

            // Then
            assertThat(foundUser).isEmpty();
            verify(jpaRepository).findById(userId);
            verify(mapper, never()).toDomain(any());
        }

        @Test
        @DisplayName("Debería encontrar usuario por email exitosamente")
        void shouldFindUserByEmailSuccessfully() {
            // Given
            String email = "test@example.com";
            when(jpaRepository.findByEmailAndDeletedAtIsNull(email))
                    .thenReturn(Optional.of(testUserEntity));
            when(mapper.toDomain(testUserEntity)).thenReturn(testUser);

            // When
            Optional<User> foundUser = userAdapter.findUserByEmail(email);

            // Then
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getEmail()).isEqualTo(email);
            verify(jpaRepository).findByEmailAndDeletedAtIsNull(email);
            verify(mapper).toDomain(testUserEntity);
        }

        @Test
        @DisplayName("Debería retornar vacío cuando no se encuentra usuario por email")
        void shouldReturnEmptyWhenUserNotFoundByEmail() {
            // Given
            String email = "nonexistent@example.com";
            when(jpaRepository.findByEmailAndDeletedAtIsNull(email))
                    .thenReturn(Optional.empty());

            // When
            Optional<User> foundUser = userAdapter.findUserByEmail(email);

            // Then
            assertThat(foundUser).isEmpty();
            verify(jpaRepository).findByEmailAndDeletedAtIsNull(email);
            verify(mapper, never()).toDomain(any());
        }

        @Test
        @DisplayName("No debería encontrar usuario eliminado lógicamente por email")
        void shouldNotFindSoftDeletedUserByEmail() {
            // Given
            String email = "deleted@example.com";
            when(jpaRepository.findByEmailAndDeletedAtIsNull(email))
                    .thenReturn(Optional.empty());

            // When
            Optional<User> foundUser = userAdapter.findUserByEmail(email);

            // Then
            assertThat(foundUser).isEmpty();
            verify(jpaRepository).findByEmailAndDeletedAtIsNull(email);
        }
    }

    @Nested
    @DisplayName("Pruebas de Buscar Todos los Usuarios")
    class FindAllUsersTests {

        @Test
        @DisplayName("Debería encontrar todos los usuarios con paginación")
        void shouldFindAllUsersWithPagination() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 10, "email");
            Pageable pageable = PageRequest.of(0, 10, Sort.by("email").ascending());

            List<UserEntity> entities = List.of(testUserEntity);
            Page<UserEntity> page = new PageImpl<>(entities, pageable, 1);

            when(jpaRepository.findAllByDeletedAtIsNull(pageable)).thenReturn(page);
            when(mapper.toDomain(testUserEntity)).thenReturn(testUser);

            // When
            PageResponse<User> result = userAdapter.findAllUsers(query);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.totalElements()).isEqualTo(1);
            assertThat(result.totalPages()).isEqualTo(1);
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.size()).isEqualTo(10);
            verify(jpaRepository).findAllByDeletedAtIsNull(pageable);
            verify(mapper).toDomain(testUserEntity);
        }

        @Test
        @DisplayName("Debería retornar página vacía cuando no se encuentran usuarios")
        void shouldReturnEmptyPageWhenNoUsersFound() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 10, "email");
            Pageable pageable = PageRequest.of(0, 10, Sort.by("email").ascending());
            Page<UserEntity> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(jpaRepository.findAllByDeletedAtIsNull(pageable)).thenReturn(emptyPage);

            // When
            PageResponse<User> result = userAdapter.findAllUsers(query);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isZero();
            assertThat(result.totalPages()).isZero();
            verify(jpaRepository).findAllByDeletedAtIsNull(pageable);
            verify(mapper, never()).toDomain(any());
        }

        @Test
        @DisplayName("Debería encontrar usuarios en la segunda página")
        void shouldFindUsersOnSecondPage() {
            // Given
            ListUsersQuery query = new ListUsersQuery(1, 5, "userName");
            Pageable pageable = PageRequest.of(1, 5, Sort.by("userName").ascending());

            List<UserEntity> entities = List.of(testUserEntity);
            Page<UserEntity> page = new PageImpl<>(entities, pageable, 10);

            when(jpaRepository.findAllByDeletedAtIsNull(pageable)).thenReturn(page);
            when(mapper.toDomain(testUserEntity)).thenReturn(testUser);

            // When
            PageResponse<User> result = userAdapter.findAllUsers(query);

            // Then
            assertThat(result.page()).isEqualTo(1);
            assertThat(result.size()).isEqualTo(5);
            assertThat(result.totalElements()).isEqualTo(10);
            assertThat(result.totalPages()).isEqualTo(2);
            verify(jpaRepository).findAllByDeletedAtIsNull(pageable);
        }

        @Test
        @DisplayName("Debería retornar solo usuarios activos excluyendo los eliminados lógicamente")
        void shouldOnlyReturnActiveUsersExcludingSoftDeleted() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 10, "email");
            Pageable pageable = PageRequest.of(0, 10, Sort.by("email").ascending());

            List<UserEntity> activeEntities = List.of(testUserEntity);
            Page<UserEntity> page = new PageImpl<>(activeEntities, pageable, 1);

            when(jpaRepository.findAllByDeletedAtIsNull(pageable)).thenReturn(page);
            when(mapper.toDomain(testUserEntity)).thenReturn(testUser);

            // When
            PageResponse<User> result = userAdapter.findAllUsers(query);

            // Then
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).isActive()).isTrue();
            verify(jpaRepository).findAllByDeletedAtIsNull(pageable);
        }
    }

    @Nested
    @DisplayName("Pruebas de Eliminar Usuario")
    class DeleteUserTests {

        @Test
        @DisplayName("Debería eliminar usuario por ID exitosamente")
        void shouldDeleteUserByIdSuccessfully() {
            // Given
            Integer userId = 1;
            doNothing().when(jpaRepository).deleteById(userId);

            // When
            userAdapter.deleteUserById(userId);

            // Then
            verify(jpaRepository).deleteById(userId);
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando falla al eliminar usuario")
        void shouldThrowExceptionWhenDeleteUserFails() {
            // Given
            Integer userId = 1;
            doThrow(new RuntimeException("Cannot delete user"))
                    .when(jpaRepository).deleteById(userId);

            // When & Then
            assertThatThrownBy(() -> userAdapter.deleteUserById(userId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Cannot delete user");

            verify(jpaRepository).deleteById(userId);
        }
    }

    @Nested
    @DisplayName("Pruebas de Eliminación Lógica de Usuario")
    class SoftDeleteUserTests {

        @Test
        @DisplayName("Debería eliminar usuario lógicamente exitosamente")
        void shouldSoftDeleteUserSuccessfully() {
            // Given
            Integer userId = 1;
            Integer deletedBy = 2;
            when(jpaRepository.existsById(userId)).thenReturn(true);
            doNothing().when(jpaRepository)
                    .softDeleteUser(eq(userId), any(LocalDateTime.class), eq(deletedBy));

            // When
            userAdapter.softDeleteUser(userId, deletedBy);

            // Then
            verify(jpaRepository).existsById(userId);
            verify(jpaRepository).softDeleteUser(eq(userId), any(LocalDateTime.class), eq(deletedBy));
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando usuario no existe para eliminación lógica")
        void shouldThrowExceptionWhenSoftDeleteUserNotFound() {
            // Given
            Integer userId = 999;
            Integer deletedBy = 2;
            when(jpaRepository.existsById(userId)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> userAdapter.softDeleteUser(userId, deletedBy))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Usuario no encontrado");

            verify(jpaRepository).existsById(userId);
            verify(jpaRepository, never()).softDeleteUser(anyInt(), any(), anyInt());
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando falla la eliminación lógica")
        void shouldThrowExceptionWhenSoftDeleteFails() {
            // Given
            Integer userId = 1;
            Integer deletedBy = 2;
            when(jpaRepository.existsById(userId)).thenReturn(true);
            doThrow(new RuntimeException("Database error"))
                    .when(jpaRepository).softDeleteUser(eq(userId), any(LocalDateTime.class), eq(deletedBy));

            // When & Then
            assertThatThrownBy(() -> userAdapter.softDeleteUser(userId, deletedBy))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database error");

            verify(jpaRepository).existsById(userId);
            verify(jpaRepository).softDeleteUser(eq(userId), any(LocalDateTime.class), eq(deletedBy));
        }
    }

    @Nested
    @DisplayName("Pruebas de Existencia")
    class ExistsTests {

        @Test
        @DisplayName("Debería retornar verdadero cuando usuario existe por email")
        void shouldReturnTrueWhenUserExistsByEmail() {
            // Given
            String email = "test@example.com";
            when(jpaRepository.existsByEmailAndDeletedAtIsNull(email)).thenReturn(true);

            // When
            boolean exists = userAdapter.existsByEmail(email);

            // Then
            assertThat(exists).isTrue();
            verify(jpaRepository).existsByEmailAndDeletedAtIsNull(email);
        }

        @Test
        @DisplayName("Debería retornar falso cuando usuario no existe por email")
        void shouldReturnFalseWhenUserDoesNotExistByEmail() {
            // Given
            String email = "nonexistent@example.com";
            when(jpaRepository.existsByEmailAndDeletedAtIsNull(email)).thenReturn(false);

            // When
            boolean exists = userAdapter.existsByEmail(email);

            // Then
            assertThat(exists).isFalse();
            verify(jpaRepository).existsByEmailAndDeletedAtIsNull(email);
        }

        @Test
        @DisplayName("Debería retornar falso cuando usuario existe pero está eliminado lógicamente")
        void shouldReturnFalseWhenUserExistsButIsSoftDeleted() {
            // Given
            String email = "deleted@example.com";
            when(jpaRepository.existsByEmailAndDeletedAtIsNull(email)).thenReturn(false);

            // When
            boolean exists = userAdapter.existsByEmail(email);

            // Then
            assertThat(exists).isFalse();
            verify(jpaRepository).existsByEmailAndDeletedAtIsNull(email);
        }

        @Test
        @DisplayName("Debería retornar verdadero cuando usuario existe por ID")
        void shouldReturnTrueWhenUserExistsById() {
            // Given
            Integer userId = 1;
            when(jpaRepository.existsByIdUserAndDeletedAtIsNull(userId)).thenReturn(true);

            // When
            boolean exists = userAdapter.existsById(userId);

            // Then
            assertThat(exists).isTrue();
            verify(jpaRepository).existsByIdUserAndDeletedAtIsNull(userId);
        }

        @Test
        @DisplayName("Debería retornar falso cuando usuario no existe por ID")
        void shouldReturnFalseWhenUserDoesNotExistById() {
            // Given
            Integer userId = 999;
            when(jpaRepository.existsByIdUserAndDeletedAtIsNull(userId)).thenReturn(false);

            // When
            boolean exists = userAdapter.existsById(userId);

            // Then
            assertThat(exists).isFalse();
            verify(jpaRepository).existsByIdUserAndDeletedAtIsNull(userId);
        }
    }

    @Nested
    @DisplayName("Pruebas de Búsqueda de Usuarios")
    class SearchUsersTests {

        @Test
        @DisplayName("Debería buscar usuarios por parte del email exitosamente")
        void shouldSearchUsersByEmailPartSuccessfully() {
            // Given
            String emailPart = "test";
            List<UserEntity> entities = List.of(testUserEntity);
            when(jpaRepository.findByEmailContainingIgnoreCaseAndDeletedAtIsNull(emailPart))
                    .thenReturn(entities);
            when(mapper.toDomain(testUserEntity)).thenReturn(testUser);

            // When
            List<User> users = userAdapter.searchUsersByEmail(emailPart);

            // Then
            assertThat(users).hasSize(1);
            assertThat(users.get(0).getEmail()).contains("test");
            verify(jpaRepository).findByEmailContainingIgnoreCaseAndDeletedAtIsNull(emailPart);
            verify(mapper).toDomain(testUserEntity);
        }

        @Test
        @DisplayName("Debería buscar usuarios con cadena vacía cuando parte del email es null")
        void shouldSearchUsersWithEmptyStringWhenEmailPartIsNull() {
            // Given
            when(jpaRepository.findByEmailContainingIgnoreCaseAndDeletedAtIsNull(""))
                    .thenReturn(List.of());

            // When
            List<User> users = userAdapter.searchUsersByEmail(null);

            // Then
            assertThat(users).isEmpty();
            verify(jpaRepository).findByEmailContainingIgnoreCaseAndDeletedAtIsNull("");
        }

        @Test
        @DisplayName("Debería recortar espacios de la parte del email antes de buscar")
        void shouldTrimEmailPartBeforeSearching() {
            // Given
            String emailPart = "  test  ";
            when(jpaRepository.findByEmailContainingIgnoreCaseAndDeletedAtIsNull("test"))
                    .thenReturn(List.of(testUserEntity));
            when(mapper.toDomain(testUserEntity)).thenReturn(testUser);

            // When
            List<User> users = userAdapter.searchUsersByEmail(emailPart);

            // Then
            assertThat(users).hasSize(1);
            verify(jpaRepository).findByEmailContainingIgnoreCaseAndDeletedAtIsNull("test");
        }

        @Test
        @DisplayName("Debería retornar lista vacía cuando ningún usuario coincide con la búsqueda")
        void shouldReturnEmptyListWhenNoUsersMatchSearch() {
            // Given
            String emailPart = "nonexistent";
            when(jpaRepository.findByEmailContainingIgnoreCaseAndDeletedAtIsNull(emailPart))
                    .thenReturn(List.of());

            // When
            List<User> users = userAdapter.searchUsersByEmail(emailPart);

            // Then
            assertThat(users).isEmpty();
            verify(jpaRepository).findByEmailContainingIgnoreCaseAndDeletedAtIsNull(emailPart);
        }

        @Test
        @DisplayName("Debería retornar solo usuarios activos en resultados de búsqueda")
        void shouldOnlyReturnActiveUsersInSearchResults() {
            // Given
            String emailPart = "test";
            List<UserEntity> activeEntities = List.of(testUserEntity);
            when(jpaRepository.findByEmailContainingIgnoreCaseAndDeletedAtIsNull(emailPart))
                    .thenReturn(activeEntities);
            when(mapper.toDomain(testUserEntity)).thenReturn(testUser);

            // When
            List<User> users = userAdapter.searchUsersByEmail(emailPart);

            // Then
            assertThat(users).hasSize(1);
            assertThat(users.get(0).isActive()).isTrue();
            verify(jpaRepository).findByEmailContainingIgnoreCaseAndDeletedAtIsNull(emailPart);
        }
    }

    @Nested
    @DisplayName("Pruebas de Buscar por Rol")
    class FindByRoleTests {

        @Test
        @DisplayName("Debería encontrar usuarios por rol con paginación")
        void shouldFindUsersByRoleWithPagination() {
            // Given
            Role role = Role.CLIENT;
            Pageable pageable = PageRequest.of(0, 10);
            List<UserEntity> entities = List.of(testUserEntity);
            Page<UserEntity> page = new PageImpl<>(entities, pageable, 1);

            when(jpaRepository.findByRoleAndDeletedAtIsNull(role, pageable)).thenReturn(page);
            when(mapper.toDomain(testUserEntity)).thenReturn(testUser);

            // When
            PageResponse<User> result = userAdapter.findByRole(role, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).getRole()).isEqualTo(Role.CLIENT);
            assertThat(result.totalElements()).isEqualTo(1);
            verify(jpaRepository).findByRoleAndDeletedAtIsNull(role, pageable);
            verify(mapper).toDomain(testUserEntity);
        }

        @Test
        @DisplayName("Debería retornar página vacía cuando no se encuentran usuarios por rol")
        void shouldReturnEmptyPageWhenNoUsersFoundByRole() {
            // Given
            Role role = Role.ADMIN;
            Pageable pageable = PageRequest.of(0, 10);
            Page<UserEntity> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(jpaRepository.findByRoleAndDeletedAtIsNull(role, pageable)).thenReturn(emptyPage);

            // When
            PageResponse<User> result = userAdapter.findByRole(role, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isZero();
            verify(jpaRepository).findByRoleAndDeletedAtIsNull(role, pageable);
            verify(mapper, never()).toDomain(any());
        }

        @Test
        @DisplayName("Debería encontrar múltiples usuarios con el mismo rol")
        void shouldFindMultipleUsersWithSameRole() {
            // Given
            Role role = Role.EMPLOYEE;
            Pageable pageable = PageRequest.of(0, 10);

            UserEntity entity1 = new UserEntity();
            entity1.setIdUser(1);
            entity1.setRole(Role.EMPLOYEE);

            UserEntity entity2 = new UserEntity();
            entity2.setIdUser(2);
            entity2.setRole(Role.EMPLOYEE);

            List<UserEntity> entities = List.of(entity1, entity2);
            Page<UserEntity> page = new PageImpl<>(entities, pageable, 2);

            User user1 = User.createByAdmin("Employee 1", "emp1@example.com", "hash",
                    Role.EMPLOYEE, null, null, null, 1);
            user1.setIdUser(1);

            User user2 = User.createByAdmin("Employee 2", "emp2@example.com", "hash",
                    Role.EMPLOYEE, null, null, null, 1);
            user2.setIdUser(2);

            when(jpaRepository.findByRoleAndDeletedAtIsNull(role, pageable)).thenReturn(page);
            when(mapper.toDomain(entity1)).thenReturn(user1);
            when(mapper.toDomain(entity2)).thenReturn(user2);

            // When
            PageResponse<User> result = userAdapter.findByRole(role, pageable);

            // Then
            assertThat(result.content()).hasSize(2);
            assertThat(result.totalElements()).isEqualTo(2);
            assertThat(result.content()).allMatch(user -> user.getRole() == Role.EMPLOYEE);
            verify(jpaRepository).findByRoleAndDeletedAtIsNull(role, pageable);
        }

        @Test
        @DisplayName("Debería retornar solo usuarios activos al buscar por rol")
        void shouldOnlyReturnActiveUsersWhenFindingByRole() {
            // Given
            Role role = Role.CLIENT;
            Pageable pageable = PageRequest.of(0, 10);
            List<UserEntity> activeEntities = List.of(testUserEntity);
            Page<UserEntity> page = new PageImpl<>(activeEntities, pageable, 1);

            when(jpaRepository.findByRoleAndDeletedAtIsNull(role, pageable)).thenReturn(page);
            when(mapper.toDomain(testUserEntity)).thenReturn(testUser);

            // When
            PageResponse<User> result = userAdapter.findByRole(role, pageable);

            // Then
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).isActive()).isTrue();
            verify(jpaRepository).findByRoleAndDeletedAtIsNull(role, pageable);
        }
    }
}
