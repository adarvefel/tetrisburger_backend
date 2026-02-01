package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.BurgerEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.BurgerJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BurgerAdapter implements BurgerRepository {

    private final BurgerJpaRepository jpaRepository;
    private final BurgerEntityMapper mapper;

    public BurgerAdapter(BurgerJpaRepository jpaRepository,
                                   BurgerEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Burger save(Burger burger) {
        BurgerEntity entity = mapper.toEntity(burger);
        BurgerEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Burger> findById(Integer idBurger) {
        return jpaRepository.findById(idBurger)
                .map(mapper::toDomain);
    }

    // ========================================
    // CONSULTAS DE MENÚ
    // ========================================

    @Override
    public PageResponse<Burger> findAllOnMenu(PaginationRequest pagination) {
        Pageable pageable = buildPageable(pagination);
        Page<BurgerEntity> page = jpaRepository.findByIsOnMenuTrueAndDeletedAtIsNull(pageable);
        return mapToPageResponse(page);
    }

    @Override
    public Optional<Burger> findMenuById(Integer idBurger) {
        return jpaRepository.findByIdBurgerAndIsOnMenuTrue(idBurger)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Burger> findActiveMenuById(Integer idBurger) {
        return jpaRepository.findByIdBurgerAndIsOnMenuTrueAndDeletedAtIsNull(idBurger)
                .map(mapper::toDomain);
    }

    @Override
    public PageResponse<Burger> searchMenuByName(String name, PaginationRequest pagination) {
        Pageable pageable = buildPageable(pagination);
        Page<BurgerEntity> page = jpaRepository.searchMenuByName(name, pageable);
        return mapToPageResponse(page);
    }

    @Override
    public boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(String name) {
        return jpaRepository.existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(name);
    }

    // ========================================
    // CONSULTAS DE CUSTOM BURGERS
    // ========================================

    @Override
    public PageResponse<Burger> findAllCustomByUserId(Integer idUser, PaginationRequest pagination) {
        Pageable pageable = buildPageable(pagination);
        Page<BurgerEntity> page = jpaRepository.findAllByIdUserAndIsCustomTrueAndDeletedAtIsNull(idUser, pageable);
        return mapToPageResponse(page);
    }

    @Override
    public Optional<Burger> findCustomByIdAndUser(Integer idBurger, Integer idUser) {
        return jpaRepository.findByIdBurgerAndIdUserAndIsCustomTrueAndDeletedAtIsNull(idBurger, idUser)
                .map(mapper::toDomain);
    }

    @Override
    public PageResponse<Burger> searchCustomByName(Integer idUser, String name, PaginationRequest pagination) {
        Pageable pageable = buildPageable(pagination);
        Page<BurgerEntity> page = jpaRepository.searchCustomByName(idUser, name, pageable);
        return mapToPageResponse(page);
    }

    // ========================================
    // HELPERS
    // ========================================

    private Pageable buildPageable(PaginationRequest pagination) {
        Sort sort = pagination.getDirection().equalsIgnoreCase("DESC")
                ? Sort.by(pagination.getSortBy()).descending()
                : Sort.by(pagination.getSortBy()).ascending();
        return PageRequest.of(pagination.getPage(), pagination.getSize(), sort);
    }

    private PageResponse<Burger> mapToPageResponse(Page<BurgerEntity> page) {
        return new PageResponse<>(
                page.getContent().stream()
                        .map(mapper::toDomain)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
