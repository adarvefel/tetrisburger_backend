package com.tetris.tetrisburger_backend.domain.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Role {
    ADMIN( 1, "ADMIN", "Administrador del sistema"),
    CLIENT( 2, "CLIENTE", "Cliente de la aplicación"),
    EMPLOYEE( 3, "EMPLEADO", "Empleado del restaurante");

    private final Integer idRol;
    private final String nameRol;
    private final String description;

    // Maps for fast lookups (cache)
    private static final Map<Integer, Role> IDROL_TO_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(Role::getIdRol, Function.identity()));

    private static final Map<String, Role> NAMEROL_TO_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(Role::getNameRol, Function.identity()));

    Role(Integer idRol, String nameRol, String description) {
        this.idRol = idRol;
        this.nameRol = nameRol;
        this.description = description;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public String getNameRol() {
        return nameRol;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Finds a role by its ID.
     *
     * @param idRol The role ID.
     * @return An Optional containing the role if found, or empty otherwise.
     */
    public static Optional<Role> fromIdRol(Integer idRol) {
        return Optional.ofNullable(IDROL_TO_MAP.get(idRol));
    }

    /**
     * Finds a role by its name, ignoring case.
     *
     * @param nameRol The name of the role.
     * @return An Optional containing the role if found, or empty otherwise.
     */
    public static Optional<Role> fromNameRol(String nameRol) {
        return Optional.ofNullable(nameRol)
                .map(String::toUpperCase)
                .map(NAMEROL_TO_MAP::get);
    }

    /**
     * Checks if this role has an equal or higher hierarchy than another.
     * The hierarchy is: ADMIN > EMPLOYEE > CLIENT.
     *
     * @param otherRole The role to compare against.
     * @return true if this role has equal or higher hierarchy, false otherwise.
     */
    public boolean hasEqualOrHigherHierarchy(Role otherRole) {
        return switch (this) {
            case ADMIN -> true;
            case EMPLOYEE -> otherRole == EMPLOYEE || otherRole == CLIENT;
            case CLIENT -> otherRole == CLIENT;
        };
    }

    /**
     * Checks if the current role is administrative (ADMIN or EMPLOYEE).
     *
     * @return true if it is an administrative role, false otherwise.
     */
    public boolean isAdministrativeRol() {
        return this == ADMIN || this == EMPLOYEE;
    }
}