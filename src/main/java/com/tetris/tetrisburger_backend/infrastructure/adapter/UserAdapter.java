package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.ListUsersQuery;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.UserEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.UserEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.UserJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class UserAdapter implements UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserAdapter.class);
    private final UserJpaRepository jpa;
    private final UserEntityMapper mapper;

    public UserAdapter(UserJpaRepository jpa, UserEntityMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public User saveUser(User user) {
        logger.debug("Guardando usuario con el correo: {}", user.getEmail());
        try {
            UserEntity entity = mapper.toEntity(user);
            UserEntity saved = jpa.save(entity);
            logger.info("Usuario guardado exitosamente con ID: {}", saved.getIdUser());
            return mapper.toDomain(saved);
        } catch (Exception e) {
            logger.error("Error al guardar usuario: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<User> findUserById(Integer id) {
        logger.debug("Buscando usuario con el ID: {}", id);
        Optional<User> userOptional = jpa.findById(id).map(mapper::toDomain);

        if (userOptional.isPresent()) {
            logger.debug("Usuario encontrado: {}", userOptional.get().getIdUser());
        } else {
            logger.warn("No existe ningún usuario con este ID: {}", id);
        }
        return userOptional;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        logger.debug("Buscando usuario activo con email: {}", email);
        // ✅ CAMBIO: findByEmailAndDeletedAtIsNull()
        Optional<User> userOpt = jpa.findByEmailAndDeletedAtIsNull(email)
                .map(mapper::toDomain);

        if (userOpt.isPresent()) {
            logger.debug("Usuario encontrado: {}", userOpt.get().getEmail());
        } else {
            logger.warn("No se encontró usuario activo con email: {}", email);
        }
        return userOpt;
    }


    @Override
    public PageResponse<User> findAllUsers(ListUsersQuery query) {
        logger.debug("Consultando usuarios activos - página: {}, tamaño: {}",
                query.page(), query.size());

        Pageable pageable = PageRequest.of(
                query.page(),
                query.size(),
                Sort.by(query.sortBy()).ascending()
        );

        Page<UserEntity> page = jpa.findAllByDeletedAtIsNull(pageable);

        var users = page.getContent().stream()
                .map(mapper::toDomain)
                .toList();

        logger.info("Usuarios activos recuperados: {} de {} (página {}/{})",
                users.size(), page.getTotalElements(),
                page.getNumber() + 1, page.getTotalPages());

        return new PageResponse<>(
                users,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public void deleteUserById(Integer idUser) {
        logger.info("Eliminando usuario con el ID: {}", idUser);
        try {
            jpa.deleteById(idUser);
            logger.info("Usuario eliminado exitosamente con ID: {}", idUser);
        } catch (Exception e) {
            logger.error("Error al eliminar usuario: {}", e.getMessage(), e);
            throw e;
        }
    }



    @Override
    public void softDeleteUser(Integer idUser, Integer deletedBy) {
        logger.info("Marcando usuario como eliminado (soft delete) - ID: {}, eliminado por: {}",
                idUser, deletedBy);
        try {
            // Verificar que existe
            if (!jpa.existsById(idUser)) {
                logger.warn("Usuario no encontrado para soft delete: {}", idUser);
                throw new IllegalArgumentException("Usuario no encontrado");
            }

            jpa.softDeleteUser(idUser, LocalDateTime.now(), deletedBy);
            logger.info("Usuario {} marcado como eliminado por admin {}", idUser, deletedBy);
        } catch (Exception e) {
            logger.error("Error en soft delete del usuario: {}", e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public boolean existsByEmail(String email) {
        // ✅ CAMBIO: Con filtro de soft delete
        return jpa.existsByEmailAndDeletedAtIsNull(email);
    }

    @Override
    public boolean existsById(Integer id) {
        // ✅ CAMBIO: Con filtro de soft delete
        return jpa.existsByIdUserAndDeletedAtIsNull(id);
    }

    @Override
    public List<User> searchUsersByEmail(String emailPart) {
        logger.debug("Buscando usuarios activos por email parcial: {}", emailPart);

        String term = (emailPart == null) ? "" : emailPart.trim();

        return jpa.findByEmailContainingIgnoreCaseAndDeletedAtIsNull(term)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public PageResponse<User> findByRole(Role role, Pageable pageable) {
        logger.debug("Consultando usuarios activos con rol {} - página: {}, tamaño: {}",
                role, pageable.getPageNumber(), pageable.getPageSize());

        Page<UserEntity> page = jpa.findByRoleAndDeletedAtIsNull(role, pageable);

        List<User> users = page.getContent().stream()
                .map(mapper::toDomain)
                .toList();

        logger.info("Usuarios con rol {} recuperados: {} de {} (página {}/{})",
                role, users.size(), page.getTotalElements(),
                page.getNumber() + 1, page.getTotalPages());

        return new PageResponse<>(
                users,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }



}
