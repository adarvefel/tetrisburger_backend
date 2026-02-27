package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.UserEntity;

import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(
        excludeAutoConfiguration = {
                FlywayAutoConfiguration.class,
                LiquibaseAutoConfiguration.class
        },
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;NON_KEYWORDS=USER",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.jpa.show-sql=false",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
        }
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Pruebas del repositorio de usuarios")
class UserJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private UserEntity activeUser;
    private UserEntity deletedUser;
    private UserEntity adminUser;

    private UserEntity buildUser(String email, Role role,
                                 LocalDateTime deletedAt, Integer deletedBy) {
        UserEntity user = new UserEntity();
        user.setUserName("Test User");
        user.setEmail(email);
        user.setPassword("password123");
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setDeletedAt(deletedAt);
        user.setDeletedBy(deletedBy);
        return user;
    }

    @BeforeEach
    void setUp() {
        activeUser  = buildUser("active@tetris.com",  Role.CLIENT, null, null);
        deletedUser = buildUser("deleted@tetris.com", Role.CLIENT, LocalDateTime.now().minusDays(1), 1);
        adminUser   = buildUser("admin@tetris.com",   Role.ADMIN,  null, null);

        entityManager.persistAndFlush(activeUser);
        entityManager.persistAndFlush(deletedUser);
        entityManager.persistAndFlush(adminUser);
    }

    // ─────────────────────────────────────────────────────────────
    // findByEmailAndDeletedAtIsNull
    // Usado en: UserAdapter.findUserByEmail(String)
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Pruebas de findByEmailAndDeletedAtIsNull")
    class FindByEmailAndDeletedAtIsNullTests {

        @Test
        @DisplayName("Debe retornar el usuario cuando el email existe y está activo")
        void shouldReturnUserWhenEmailExistsAndIsActive() {
            Optional<UserEntity> result = userJpaRepository
                    .findByEmailAndDeletedAtIsNull("active@tetris.com");

            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("active@tetris.com");
        }

        @Test
        @DisplayName("Debe retornar vacío si el email pertenece a un usuario eliminado")
        void shouldReturnEmptyWhenEmailBelongsToDeletedUser() {
            Optional<UserEntity> result = userJpaRepository
                    .findByEmailAndDeletedAtIsNull("deleted@tetris.com");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Debe retornar vacío cuando el email no existe en la base de datos")
        void shouldReturnEmptyWhenEmailDoesNotExist() {
            Optional<UserEntity> result = userJpaRepository
                    .findByEmailAndDeletedAtIsNull("ghost@tetris.com");

            assertThat(result).isEmpty();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // findByEmailContainingIgnoreCaseAndDeletedAtIsNull (Page + Pageable)
    // Usado en: UserAdapter.findUserByEmail(String, PaginationRequest)
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Pruebas de findByEmailContainingIgnoreCaseAndDeletedAtIsNull")
    class FindByEmailContainingAndNotDeletedTests {

        @Test
        @DisplayName("Debe retornar usuarios activos cuyo email contiene el fragmento")
        void shouldReturnActiveUsersMatchingEmailFragment() {
            Page<UserEntity> result = userJpaRepository
                    .findByEmailContainingIgnoreCaseAndDeletedAtIsNull(
                            "tetris",
                            PageRequest.of(0, 10, Sort.by("email").ascending())
                    );

            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent()).extracting(UserEntity::getEmail)
                    .containsExactlyInAnyOrder("active@tetris.com", "admin@tetris.com");
        }

        @Test
        @DisplayName("Debe ignorar mayúsculas y minúsculas al buscar por email")
        void shouldBeCaseInsensitiveWhenSearchingByEmail() {
            Page<UserEntity> result = userJpaRepository
                    .findByEmailContainingIgnoreCaseAndDeletedAtIsNull(
                            "ACTIVE",
                            PageRequest.of(0, 10, Sort.by("email").ascending())
                    );

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getEmail()).isEqualTo("active@tetris.com");
        }

        @Test
        @DisplayName("No debe incluir usuarios eliminados aunque el email coincida")
        void shouldNotIncludeDeletedUsersEvenIfEmailMatches() {
            Page<UserEntity> result = userJpaRepository
                    .findByEmailContainingIgnoreCaseAndDeletedAtIsNull(
                            "deleted",
                            PageRequest.of(0, 10, Sort.by("email").ascending())
                    );

            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("Debe retornar página vacía si no hay coincidencias entre activos")
        void shouldReturnEmptyPageWhenNoActiveUserMatches() {
            Page<UserEntity> result = userJpaRepository
                    .findByEmailContainingIgnoreCaseAndDeletedAtIsNull(
                            "xyz_nonexistent",
                            PageRequest.of(0, 10, Sort.by("email").ascending())
                    );

            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("Debe respetar el tamaño de página al buscar por email")
        void shouldRespectPageSizeWhenSearchingByEmail() {
            Page<UserEntity> result = userJpaRepository
                    .findByEmailContainingIgnoreCaseAndDeletedAtIsNull(
                            "tetris",
                            PageRequest.of(0, 1, Sort.by("email").ascending())
                    );

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // findAllByDeletedAtIsNull (Page + Pageable)
    // Usado en: UserAdapter.findAllUsers(ListUsersQuery)
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Pruebas de findAllByDeletedAtIsNull")
    class FindAllByDeletedAtIsNullTests {

        @Test
        @DisplayName("Debe retornar solo usuarios activos con paginación")
        void shouldReturnOnlyActiveUsersWithPagination() {
            Page<UserEntity> result = userJpaRepository
                    .findAllByDeletedAtIsNull(PageRequest.of(0, 10));

            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent()).extracting(UserEntity::getEmail)
                    .containsExactlyInAnyOrder("active@tetris.com", "admin@tetris.com");
        }

        @Test
        @DisplayName("Debe excluir usuarios con deletedAt poblado")
        void shouldExcludeUsersWithDeletedAt() {
            Page<UserEntity> result = userJpaRepository
                    .findAllByDeletedAtIsNull(PageRequest.of(0, 10));

            assertThat(result.getContent())
                    .noneMatch(u -> u.getEmail().equals("deleted@tetris.com"));
        }

        @Test
        @DisplayName("Debe respetar el tamaño de página indicado")
        void shouldRespectPageSizeParameter() {
            Page<UserEntity> result = userJpaRepository
                    .findAllByDeletedAtIsNull(PageRequest.of(0, 1));

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("Debe retornar la segunda página correctamente")
        void shouldReturnSecondPageCorrectly() {
            Page<UserEntity> result = userJpaRepository
                    .findAllByDeletedAtIsNull(PageRequest.of(1, 1));

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getNumber()).isEqualTo(1);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // existsByEmailAndDeletedAtIsNull
    // Usado en: UserAdapter.existsByEmail(String)
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Pruebas de existsByEmailAndDeletedAtIsNull")
    class ExistsByEmailTests {

        @Test
        @DisplayName("Debe retornar verdadero cuando el email existe y el usuario está activo")
        void shouldReturnTrueWhenEmailExistsAndUserIsActive() {
            assertThat(userJpaRepository
                    .existsByEmailAndDeletedAtIsNull("active@tetris.com")).isTrue();
        }

        @Test
        @DisplayName("Debe retornar falso cuando el email pertenece a un usuario eliminado")
        void shouldReturnFalseWhenEmailBelongsToDeletedUser() {
            assertThat(userJpaRepository
                    .existsByEmailAndDeletedAtIsNull("deleted@tetris.com")).isFalse();
        }

        @Test
        @DisplayName("Debe retornar falso cuando el email no existe")
        void shouldReturnFalseWhenEmailDoesNotExist() {
            assertThat(userJpaRepository
                    .existsByEmailAndDeletedAtIsNull("ghost@tetris.com")).isFalse();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // existsByIdUserAndDeletedAtIsNull
    // Usado en: UserAdapter.existsById(Integer)
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Pruebas de existsByIdUserAndDeletedAtIsNull")
    class ExistsByIdTests {

        @Test
        @DisplayName("Debe retornar verdadero cuando el ID existe y el usuario está activo")
        void shouldReturnTrueWhenIdExistsAndUserIsActive() {
            assertThat(userJpaRepository
                    .existsByIdUserAndDeletedAtIsNull(activeUser.getIdUser())).isTrue();
        }

        @Test
        @DisplayName("Debe retornar falso cuando el usuario con ese ID fue eliminado")
        void shouldReturnFalseWhenUserWithIdWasDeleted() {
            assertThat(userJpaRepository
                    .existsByIdUserAndDeletedAtIsNull(deletedUser.getIdUser())).isFalse();
        }

        @Test
        @DisplayName("Debe retornar falso cuando el ID no existe en la base de datos")
        void shouldReturnFalseWhenIdDoesNotExist() {
            assertThat(userJpaRepository
                    .existsByIdUserAndDeletedAtIsNull(9999)).isFalse();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // softDeleteUser (@Modifying + @Query)
    // Usado en: UserAdapter.softDeleteUser(Integer, Integer)
    //
    // IMPORTANTE: @DataJpaTest ejecuta cada test en una transacción
    // con rollback automático. Para que el UPDATE de @Modifying sea
    // visible antes del assert, se necesita flush() + clear() [web:47][web:49]
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Pruebas de softDeleteUser")
    class SoftDeleteUserTests {

        @Test
        @DisplayName("Debe marcar deletedAt y deletedBy correctamente al eliminar lógicamente")
        void shouldSetDeletedAtAndDeletedByOnSoftDelete() {
            LocalDateTime deletionTime = LocalDateTime.now();

            userJpaRepository.softDeleteUser(activeUser.getIdUser(), deletionTime, 1);
            entityManager.flush(); // propaga el UPDATE al contexto de H2
            entityManager.clear(); // descarta la caché para forzar re-lectura desde BD

            UserEntity result = entityManager.find(UserEntity.class, activeUser.getIdUser());

            assertThat(result.getDeletedAt()).isNotNull();
            assertThat(result.getDeletedBy()).isEqualTo(1);
        }

        @Test
        @DisplayName("Debe dejar de aparecer en búsquedas filtradas por activos tras el soft delete")
        void shouldNotAppearInFilteredSearchAfterSoftDelete() {
            userJpaRepository.softDeleteUser(activeUser.getIdUser(), LocalDateTime.now(), 1);
            entityManager.flush();
            entityManager.clear();

            Optional<UserEntity> byEmail = userJpaRepository
                    .findByEmailAndDeletedAtIsNull("active@tetris.com");

            assertThat(byEmail).isEmpty();
        }

        @Test
        @DisplayName("Debe seguir existiendo físicamente en la base de datos tras el soft delete")
        void shouldStillExistPhysicallyAfterSoftDelete() {
            userJpaRepository.softDeleteUser(activeUser.getIdUser(), LocalDateTime.now(), 1);
            entityManager.flush();
            entityManager.clear();

            // findById no filtra por deletedAt — confirma que el registro persiste
            Optional<UserEntity> result = userJpaRepository.findById(activeUser.getIdUser());

            assertThat(result).isPresent();
            assertThat(result.get().getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("Debe reducir el conteo de activos en findAllByDeletedAtIsNull tras el soft delete")
        void shouldReduceActiveUserCountAfterSoftDelete() {
            userJpaRepository.softDeleteUser(activeUser.getIdUser(), LocalDateTime.now(), 1);
            entityManager.flush();
            entityManager.clear();

            Page<UserEntity> result = userJpaRepository
                    .findAllByDeletedAtIsNull(PageRequest.of(0, 10));

            // activos eran 2 (active + admin), ahora solo queda admin
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getEmail()).isEqualTo("admin@tetris.com");
        }

        @Test
        @DisplayName("Debe hacer que existsById retorne falso tras el soft delete")
        void shouldMakeExistsByIdReturnFalseAfterSoftDelete() {
            userJpaRepository.softDeleteUser(activeUser.getIdUser(), LocalDateTime.now(), 1);
            entityManager.flush();
            entityManager.clear();

            assertThat(userJpaRepository
                    .existsByIdUserAndDeletedAtIsNull(activeUser.getIdUser())).isFalse();
        }
    }
}
