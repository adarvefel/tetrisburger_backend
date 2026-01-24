package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity,Integer> {
    Optional<UserEntity> findByIdUserAndDeletedAtIsNull(Integer id);

    Optional<UserEntity> findByEmailAndDeletedAtIsNull(String email);

    List<UserEntity> findByEmailContainingIgnoreCaseAndDeletedAtIsNull(String email);


    List<UserEntity> findByEmailContainingIgnoreCase(String email);


    Page<UserEntity> findAllByDeletedAtIsNull(Pageable pageable);

    List<UserEntity> findAllByDeletedAtIsNull();

    boolean existsByEmailAndDeletedAtIsNull(String email);

    boolean existsByIdUserAndDeletedAtIsNull(Integer id);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.deletedAt = :deletedAt, u.deletedBy = :deletedBy WHERE u.idUser = :idUser")
    void softDeleteUser(@Param("idUser") Integer idUser,
                        @Param("deletedAt") LocalDateTime deletedAt,
                        @Param("deletedBy") Integer deletedBy);

    Page<UserEntity> findByRoleAndDeletedAtIsNull(Role role, Pageable pageable);


}
