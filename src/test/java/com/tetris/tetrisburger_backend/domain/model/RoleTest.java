package com.tetris.tetrisburger_backend.domain.model;

import com.tetris.tetrisburger_backend.domain.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Pruebas del Enum Role")
class RoleTest {

    @Nested
    @DisplayName("Búsqueda por ID")
    class FromIdRolTests {

        @Test
        @DisplayName("debería encontrar ADMIN por ID")
        void shouldFindAdminById() {
            // When
            Optional<Role> role = Role.fromIdRol(1);

            // Then
            assertThat(role).isPresent();
            assertThat(role.get()).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("debería encontrar CLIENT por ID")
        void shouldFindClientById() {
            // When
            Optional<Role> role = Role.fromIdRol(2);

            // Then
            assertThat(role).isPresent();
            assertThat(role.get()).isEqualTo(Role.CLIENT);
        }

        @Test
        @DisplayName("debería encontrar EMPLOYEE por ID")
        void shouldFindEmployeeById() {
            // When
            Optional<Role> role = Role.fromIdRol(3);

            // Then
            assertThat(role).isPresent();
            assertThat(role.get()).isEqualTo(Role.EMPLOYEE);
        }

        @Test
        @DisplayName("debería retornar vacío para ID no válido")
        void shouldReturnEmptyForInvalidId() {
            // When
            Optional<Role> role = Role.fromIdRol(999);

            // Then
            assertThat(role).isEmpty();
        }

        @Test
        @DisplayName("debería retornar vacío cuando ID es null")
        void shouldReturnEmptyWhenIdIsNull() {
            // When
            Optional<Role> role = Role.fromIdRol(null);

            // Then
            assertThat(role).isEmpty();
        }
    }

    @Nested
    @DisplayName("Búsqueda por Nombre")
    class FromNameRolTests {

        @Test
        @DisplayName("debería encontrar ADMIN por nombre")
        void shouldFindAdminByName() {
            // When
            Optional<Role> role = Role.fromNameRol("ADMIN");

            // Then
            assertThat(role).isPresent();
            assertThat(role.get()).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("debería encontrar CLIENT por nombre")
        void shouldFindClientByName() {
            // When
            Optional<Role> role = Role.fromNameRol("CLIENTE");

            // Then
            assertThat(role).isPresent();
            assertThat(role.get()).isEqualTo(Role.CLIENT);
        }

        @Test
        @DisplayName("debería encontrar EMPLOYEE por nombre")
        void shouldFindEmployeeByName() {
            // When
            Optional<Role> role = Role.fromNameRol("EMPLEADO");

            // Then
            assertThat(role).isPresent();
            assertThat(role.get()).isEqualTo(Role.EMPLOYEE);
        }

        @Test
        @DisplayName("debería encontrar rol ignorando mayúsculas/minúsculas")
        void shouldFindRoleCaseInsensitive() {
            // Then
            assertThat(Role.fromNameRol("admin")).contains(Role.ADMIN);
            assertThat(Role.fromNameRol("Admin")).contains(Role.ADMIN);
            assertThat(Role.fromNameRol("ADMIN")).contains(Role.ADMIN);
            assertThat(Role.fromNameRol("cliente")).contains(Role.CLIENT);
            assertThat(Role.fromNameRol("empleado")).contains(Role.EMPLOYEE);
        }

        @Test
        @DisplayName("debería retornar vacío para nombre no válido")
        void shouldReturnEmptyForInvalidName() {
            // When
            Optional<Role> role = Role.fromNameRol("INVALID_ROLE");

            // Then
            assertThat(role).isEmpty();
        }

        @Test
        @DisplayName("debería retornar vacío cuando nombre es null")
        void shouldReturnEmptyWhenNameIsNull() {
            // When
            Optional<Role> role = Role.fromNameRol(null);

            // Then
            assertThat(role).isEmpty();
        }

        @Test
        @DisplayName("debería retornar vacío cuando nombre está vacío")
        void shouldReturnEmptyWhenNameIsEmpty() {
            // When
            Optional<Role> role = Role.fromNameRol("");

            // Then
            assertThat(role).isEmpty();
        }
    }

    @Nested
    @DisplayName("Jerarquía de Roles")
    class HierarchyTests {

        @Test
        @DisplayName("ADMIN tiene jerarquía sobre todos los roles")
        void adminHasHierarchyOverAll() {
            // Then
            assertThat(Role.ADMIN.hasEqualOrHigherHierarchy(Role.ADMIN)).isTrue();
            assertThat(Role.ADMIN.hasEqualOrHigherHierarchy(Role.EMPLOYEE)).isTrue();
            assertThat(Role.ADMIN.hasEqualOrHigherHierarchy(Role.CLIENT)).isTrue();
        }

        @Test
        @DisplayName("EMPLOYEE tiene jerarquía sobre EMPLOYEE y CLIENT")
        void employeeHasHierarchyOverEmployeeAndClient() {
            // Then
            assertThat(Role.EMPLOYEE.hasEqualOrHigherHierarchy(Role.ADMIN)).isFalse();
            assertThat(Role.EMPLOYEE.hasEqualOrHigherHierarchy(Role.EMPLOYEE)).isTrue();
            assertThat(Role.EMPLOYEE.hasEqualOrHigherHierarchy(Role.CLIENT)).isTrue();
        }

        @Test
        @DisplayName("CLIENT solo tiene jerarquía sobre CLIENT")
        void clientHasHierarchyOverClientOnly() {
            // Then
            assertThat(Role.CLIENT.hasEqualOrHigherHierarchy(Role.ADMIN)).isFalse();
            assertThat(Role.CLIENT.hasEqualOrHigherHierarchy(Role.EMPLOYEE)).isFalse();
            assertThat(Role.CLIENT.hasEqualOrHigherHierarchy(Role.CLIENT)).isTrue();
        }

        @Test
        @DisplayName("debería validar jerarquía correctamente para múltiples comparaciones")
        void shouldValidateHierarchyForMultipleComparisons() {
            // ADMIN sobre todos
            assertThat(Role.ADMIN.hasEqualOrHigherHierarchy(Role.ADMIN)).isTrue();
            assertThat(Role.ADMIN.hasEqualOrHigherHierarchy(Role.EMPLOYEE)).isTrue();
            assertThat(Role.ADMIN.hasEqualOrHigherHierarchy(Role.CLIENT)).isTrue();

            // EMPLOYEE sobre sí mismo y CLIENT
            assertThat(Role.EMPLOYEE.hasEqualOrHigherHierarchy(Role.EMPLOYEE)).isTrue();
            assertThat(Role.EMPLOYEE.hasEqualOrHigherHierarchy(Role.CLIENT)).isTrue();

            // CLIENT solo sobre sí mismo
            assertThat(Role.CLIENT.hasEqualOrHigherHierarchy(Role.CLIENT)).isTrue();
        }
    }

    @Nested
    @DisplayName("Roles Administrativos")
    class AdministrativeTests {

        @Test
        @DisplayName("ADMIN es rol administrativo")
        void adminIsAdministrative() {
            // Then
            assertThat(Role.ADMIN.isAdministrativeRol()).isTrue();
        }

        @Test
        @DisplayName("EMPLOYEE es rol administrativo")
        void employeeIsAdministrative() {
            // Then
            assertThat(Role.EMPLOYEE.isAdministrativeRol()).isTrue();
        }

        @Test
        @DisplayName("CLIENT no es rol administrativo")
        void clientIsNotAdministrative() {
            // Then
            assertThat(Role.CLIENT.isAdministrativeRol()).isFalse();
        }

        @Test
        @DisplayName("debería identificar correctamente todos los roles administrativos")
        void shouldIdentifyAllAdministrativeRoles() {
            // Then
            assertThat(Role.ADMIN.isAdministrativeRol()).isTrue();
            assertThat(Role.EMPLOYEE.isAdministrativeRol()).isTrue();
            assertThat(Role.CLIENT.isAdministrativeRol()).isFalse();
        }
    }

    @Nested
    @DisplayName("Getters y Propiedades")
    class GettersTests {

        @Test
        @DisplayName("debería retornar valores correctos para ADMIN")
        void shouldReturnCorrectValuesForAdmin() {
            // Then
            assertThat(Role.ADMIN.getIdRol()).isEqualTo(1);
            assertThat(Role.ADMIN.getNameRol()).isEqualTo("ADMIN");
            assertThat(Role.ADMIN.getDescription()).isEqualTo("Administrador del sistema");
        }

        @Test
        @DisplayName("debería retornar valores correctos para CLIENT")
        void shouldReturnCorrectValuesForClient() {
            // Then
            assertThat(Role.CLIENT.getIdRol()).isEqualTo(2);
            assertThat(Role.CLIENT.getNameRol()).isEqualTo("CLIENTE");
            assertThat(Role.CLIENT.getDescription()).isEqualTo("Cliente de la aplicación");
        }

        @Test
        @DisplayName("debería retornar valores correctos para EMPLOYEE")
        void shouldReturnCorrectValuesForEmployee() {
            // Then
            assertThat(Role.EMPLOYEE.getIdRol()).isEqualTo(3);
            assertThat(Role.EMPLOYEE.getNameRol()).isEqualTo("EMPLEADO");
            assertThat(Role.EMPLOYEE.getDescription()).isEqualTo("Empleado del restaurante");
        }

        @Test
        @DisplayName("todos los roles deberían tener IDs únicos")
        void allRolesShouldHaveUniqueIds() {
            // Given
            Integer adminId = Role.ADMIN.getIdRol();
            Integer clientId = Role.CLIENT.getIdRol();
            Integer employeeId = Role.EMPLOYEE.getIdRol();

            // Then
            assertThat(adminId).isNotEqualTo(clientId);
            assertThat(adminId).isNotEqualTo(employeeId);
            assertThat(clientId).isNotEqualTo(employeeId);
        }

        @Test
        @DisplayName("todos los roles deberían tener nombres únicos")
        void allRolesShouldHaveUniqueNames() {
            // Given
            String adminName = Role.ADMIN.getNameRol();
            String clientName = Role.CLIENT.getNameRol();
            String employeeName = Role.EMPLOYEE.getNameRol();

            // Then
            assertThat(adminName).isNotEqualTo(clientName);
            assertThat(adminName).isNotEqualTo(employeeName);
            assertThat(clientName).isNotEqualTo(employeeName);
        }

        @Test
        @DisplayName("todos los roles deberían tener descripciones no nulas")
        void allRolesShouldHaveNonNullDescriptions() {
            // Then
            assertThat(Role.ADMIN.getDescription()).isNotNull().isNotBlank();
            assertThat(Role.CLIENT.getDescription()).isNotNull().isNotBlank();
            assertThat(Role.EMPLOYEE.getDescription()).isNotNull().isNotBlank();
        }
    }

    @Nested
    @DisplayName("Validación de Enum")
    class EnumValidationTests {

        @Test
        @DisplayName("debería tener exactamente 3 roles definidos")
        void shouldHaveExactlyThreeRoles() {
            // Then
            assertThat(Role.values()).hasSize(3);
        }

        @Test
        @DisplayName("debería contener todos los roles esperados")
        void shouldContainAllExpectedRoles() {
            // Then
            assertThat(Role.values()).containsExactlyInAnyOrder(
                    Role.ADMIN,
                    Role.CLIENT,
                    Role.EMPLOYEE
            );
        }

        @Test
        @DisplayName("valueOf debería funcionar correctamente")
        void valueOfShouldWorkCorrectly() {
            // Then
            assertThat(Role.valueOf("ADMIN")).isEqualTo(Role.ADMIN);
            assertThat(Role.valueOf("CLIENT")).isEqualTo(Role.CLIENT);
            assertThat(Role.valueOf("EMPLOYEE")).isEqualTo(Role.EMPLOYEE);
        }

        @Test
        @DisplayName("valueOf debería lanzar excepción para nombre inválido")
        void valueOfShouldThrowForInvalidName() {
            // Then
            assertThatThrownBy(() -> Role.valueOf("INVALID"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Casos de Uso Reales")
    class RealWorldUseCasesTests {

        @Test
        @DisplayName("admin puede editar usuario con cualquier rol")
        void adminCanEditUserWithAnyRole() {
            // Given
            Role adminRole = Role.ADMIN;

            // Then - Admin puede editar todos
            assertThat(adminRole.hasEqualOrHigherHierarchy(Role.ADMIN)).isTrue();
            assertThat(adminRole.hasEqualOrHigherHierarchy(Role.EMPLOYEE)).isTrue();
            assertThat(adminRole.hasEqualOrHigherHierarchy(Role.CLIENT)).isTrue();
        }

        @Test
        @DisplayName("employee puede editar solo employee y client")
        void employeeCanEditOnlyEmployeeAndClient() {
            // Given
            Role employeeRole = Role.EMPLOYEE;

            // Then
            assertThat(employeeRole.hasEqualOrHigherHierarchy(Role.ADMIN)).isFalse();
            assertThat(employeeRole.hasEqualOrHigherHierarchy(Role.EMPLOYEE)).isTrue();
            assertThat(employeeRole.hasEqualOrHigherHierarchy(Role.CLIENT)).isTrue();
        }

        @Test
        @DisplayName("client solo puede editar su propio perfil")
        void clientCanOnlyEditOwnProfile() {
            // Given
            Role clientRole = Role.CLIENT;

            // Then
            assertThat(clientRole.hasEqualOrHigherHierarchy(Role.ADMIN)).isFalse();
            assertThat(clientRole.hasEqualOrHigherHierarchy(Role.EMPLOYEE)).isFalse();
            assertThat(clientRole.hasEqualOrHigherHierarchy(Role.CLIENT)).isTrue();
        }

        @Test
        @DisplayName("debería identificar correctamente roles con acceso al panel administrativo")
        void shouldIdentifyRolesWithAdminPanelAccess() {
            // Then - Solo ADMIN y EMPLOYEE tienen acceso
            assertThat(Role.ADMIN.isAdministrativeRol()).isTrue();
            assertThat(Role.EMPLOYEE.isAdministrativeRol()).isTrue();
            assertThat(Role.CLIENT.isAdministrativeRol()).isFalse();
        }

        @Test
        @DisplayName("debería poder buscar rol por ID desde base de datos")
        void shouldBeAbleToFindRoleByIdFromDatabase() {
            // When - Simulando lectura desde BD
            Optional<Role> role = Role.fromIdRol(1);

            // Then
            assertThat(role).isPresent();
            assertThat(role.get()).isEqualTo(Role.ADMIN);
            assertThat(role.get().getNameRol()).isEqualTo("ADMIN");
        }
    }
}
