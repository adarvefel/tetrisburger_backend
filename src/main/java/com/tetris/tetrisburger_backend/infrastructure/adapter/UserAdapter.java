package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.ListUsersQuery;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.UserEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.UserEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.UserJpaRepository;

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

    private final UserJpaRepository jpa;
    private final UserEntityMapper mapper;

    public UserAdapter(UserJpaRepository jpa, UserEntityMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public User saveUser(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findUserById(Integer id) {
        return jpa.findById(id).map(mapper::toDomain);


    }



    @Override
    public Optional<User> findUserByEmail(String email) {
        return jpa.findByEmailAndDeletedAtIsNull(email)
                .map(mapper::toDomain);
    }

    @Override
    public PageResponse<User> findUserByEmail(String email, PaginationRequest request) {

        PageRequest pageRequest = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(request.getSortBy()).ascending()
        );

        Page<UserEntity> page = jpa.findByEmailContainingIgnoreCaseAndDeletedAtIsNull(email,pageRequest);

        List<User> users = page.getContent().stream()
                .map(mapper::toDomain)
                .toList();
                return  new PageResponse<>(
                        users,
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages()
                );
    }


    @Override
    public PageResponse<User> findAllUsers(ListUsersQuery query) {

        Pageable pageable = PageRequest.of(
                query.page(),
                query.size(),
                Sort.by(query.sortBy()).ascending()
        );

        Page<UserEntity> page = jpa.findAllByDeletedAtIsNull(pageable);

        var users = page.getContent().stream()
                .map(mapper::toDomain)
                .toList();


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
            jpa.deleteById(idUser);
    }



    @Override
    public void softDeleteUser(Integer idUser, Integer deletedBy) {
            // Verificar que existe
            if (!jpa.existsById(idUser)) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }

            jpa.softDeleteUser(idUser, LocalDateTime.now(), deletedBy);

    }


    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmailAndDeletedAtIsNull(email);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpa.existsByIdUserAndDeletedAtIsNull(id);
    }






}
