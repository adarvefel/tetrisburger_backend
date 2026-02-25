package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
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
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"  // ← sobrescribe MySQLDialect
        }
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // ← nuevo
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
        user.setCreatedAt(LocalDateTime.now());   // nullable = false → manual
        user.setUpdatedAt(LocalDateTime.now());   // nullable = false → manual
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

    // ── findByIdUserAndDeletedAtIsNull ────────────────────────────────────

    @Test
    @DisplayName("Debe retornar el usuario cuando existe y no está eliminado")
    void shouldReturnUserWhenExistsAndNotDeleted() {
        Optional<UserEntity> result = userJpaRepository
                .findByIdUserAndDeletedAtIsNull(activeUser.getIdUser());

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("active@tetris.com");
    }

    @Test
    @DisplayName("Debe retornar vacío cuando el usuario fue eliminado lógicamente")
    void shouldReturnEmptyWhenUserIsSoftDeleted() {
        Optional<UserEntity> result = userJpaRepository
                .findByIdUserAndDeletedAtIsNull(deletedUser.getIdUser());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar vacío cuando el ID no existe en la base de datos")
    void shouldReturnEmptyWhenIdDoesNotExist() {
        Optional<UserEntity> result = userJpaRepository
                .findByIdUserAndDeletedAtIsNull(9999);

        assertThat(result).isEmpty();
    }

    // ── findByEmailAndDeletedAtIsNull ─────────────────────────────────────

    @Test
    @DisplayName("Debe retornar el usuario cuando el email existe y está activo")
    void shouldReturnUserWhenEmailExistsAndIsActive() {
        Optional<UserEntity> result = userJpaRepository
                .findByEmailAndDeletedAtIsNull("active@tetris.com");

        assertThat(result).isPresent();
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

    // ── findByEmailContainingIgnoreCaseAndDeletedAtIsNull ─────────────────

    @Test
    @DisplayName("Debe retornar usuarios activos cuyo email contiene el fragmento")
    void shouldReturnActiveUsersMatchingEmailFragment() {
        List<UserEntity> result = userJpaRepository
                .findByEmailContainingIgnoreCaseAndDeletedAtIsNull("tetris");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserEntity::getEmail)
                .containsExactlyInAnyOrder("active@tetris.com", "admin@tetris.com");
    }

    @Test
    @DisplayName("Debe ignorar mayúsculas y minúsculas al buscar por email")
    void shouldBeCaseInsensitiveWhenSearchingByEmail() {
        List<UserEntity> result = userJpaRepository
                .findByEmailContainingIgnoreCaseAndDeletedAtIsNull("ACTIVE");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("active@tetris.com");
    }

    @Test
    @DisplayName("No debe incluir usuarios eliminados aunque el email coincida")
    void shouldNotIncludeDeletedUsersEvenIfEmailMatches() {
        List<UserEntity> result = userJpaRepository
                .findByEmailContainingIgnoreCaseAndDeletedAtIsNull("deleted");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar lista vacía si no hay coincidencias entre activos")
    void shouldReturnEmptyListWhenNoActiveUserMatches() {
        List<UserEntity> result = userJpaRepository
                .findByEmailContainingIgnoreCaseAndDeletedAtIsNull("xyz_nonexistent");

        assertThat(result).isEmpty();
    }

    // ── findByEmailContainingIgnoreCase ───────────────────────────────────

    @Test
    @DisplayName("Debe retornar todos los usuarios incluyendo los eliminados")
    void shouldReturnAllUsersIncludingDeleted() {
        List<UserEntity> result = userJpaRepository
                .findByEmailContainingIgnoreCase("tetris");

        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("Debe incluir al usuario eliminado cuando el email coincide")
    void shouldIncludeDeletedUserWhenEmailMatches() {
        List<UserEntity> result = userJpaRepository
                .findByEmailContainingIgnoreCase("deleted");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDeletedAt()).isNotNull();
    }

    // ── findAllByDeletedAtIsNull ──────────────────────────────────────────

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
    @DisplayName("Debe respetar el tamaño de página indicado")
    void shouldRespectPageSizeParameter() {
        Page<UserEntity> result = userJpaRepository
                .findAllByDeletedAtIsNull(PageRequest.of(0, 1));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Debe retornar todos los usuarios activos sin paginación")
    void shouldReturnAllActiveUsersWithoutPagination() {
        List<UserEntity> result = userJpaRepository.findAllByDeletedAtIsNull();

        assertThat(result).hasSize(2);
        assertThat(result).noneMatch(u -> u.getDeletedAt() != null);
    }

    // ── existsByEmailAndDeletedAtIsNull ───────────────────────────────────

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

    // ── existsByIdUserAndDeletedAtIsNull ──────────────────────────────────

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

    // ── softDeleteUser ────────────────────────────────────────────────────

    @Test
    @DisplayName("Debe marcar deletedAt y deletedBy correctamente al eliminar")
    void shouldSetDeletedAtAndDeletedByOnSoftDelete() {
        LocalDateTime deletionTime = LocalDateTime.now();
        userJpaRepository.softDeleteUser(activeUser.getIdUser(), deletionTime, 1);
        entityManager.clear();

        UserEntity result = entityManager.find(UserEntity.class, activeUser.getIdUser());
        assertThat(result.getDeletedAt()).isNotNull();
        assertThat(result.getDeletedBy()).isEqualTo(1);
    }

    @Test
    @DisplayName("Debe dejar de aparecer en búsquedas de activos tras la eliminación lógica")
    void shouldNotAppearInActiveSearchesAfterSoftDelete() {
        userJpaRepository.softDeleteUser(activeUser.getIdUser(), LocalDateTime.now(), 1);
        entityManager.clear();

        Optional<UserEntity> result = userJpaRepository
                .findByIdUserAndDeletedAtIsNull(activeUser.getIdUser());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe seguir apareciendo en búsquedas sin filtro tras la eliminación lógica")
    void shouldStillExistInUnfilteredSearchAfterSoftDelete() {
        userJpaRepository.softDeleteUser(activeUser.getIdUser(), LocalDateTime.now(), 1);
        entityManager.clear();

        List<UserEntity> result = userJpaRepository
                .findByEmailContainingIgnoreCase("active");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDeletedAt()).isNotNull();
    }

    // ── findByRoleAndDeletedAtIsNull ──────────────────────────────────────

    @Test
    @DisplayName("Debe retornar solo los usuarios activos con rol CLIENT")
    void shouldReturnOnlyActiveUsersWithClientRole() {
        Page<UserEntity> result = userJpaRepository
                .findByRoleAndDeletedAtIsNull(Role.CLIENT, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("active@tetris.com");
    }

    @Test
    @DisplayName("Debe excluir usuarios eliminados aunque tengan el rol correcto")
    void shouldExcludeDeletedUsersEvenIfRoleMatches() {
        Page<UserEntity> result = userJpaRepository
                .findByRoleAndDeletedAtIsNull(Role.CLIENT, PageRequest.of(0, 10));

        assertThat(result.getContent())
                .noneMatch(u -> u.getEmail().equals("deleted@tetris.com"));
    }

    @Test
    @DisplayName("Debe retornar solo el admin entre los usuarios activos con rol ADMIN")
    void shouldReturnOnlyActiveUsersWithAdminRole() {
        Page<UserEntity> result = userJpaRepository
                .findByRoleAndDeletedAtIsNull(Role.ADMIN, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("admin@tetris.com");
    }
}
