package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.BurgerEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.BurgerJpaRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.util.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BurgerAdapter implements BurgerRepository {

    private final BurgerJpaRepository jpaRepository;
    private final BurgerEntityMapper mapper;

    public BurgerAdapter(BurgerJpaRepository jpaRepository, BurgerEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    // ========================================
    // OPERACIONES BÁSICAS
    // ========================================

    @Override
    public Burger save(Burger burger) {
        BurgerEntity entity = mapper.toEntity(burger);
        BurgerEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Burger> findById(Integer idBurger) {
        return jpaRepository.findByIdWithProductsAndDeletedAtIsNull(idBurger)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(Integer idBurger) {
        jpaRepository.deleteById(idBurger);
    }

    @Override
    public boolean existsById(Integer idBurger) {
        return jpaRepository.existsById(idBurger);
    }


    @Override
    public void deleteActiveDraftsByUser(Integer idUser) {
        jpaRepository.softDeleteAllActiveDrafts(idUser);
    }

    // ========================================
    // MENÚ - LISTAR
    // ========================================

    @Override
    public PageResponse<Burger> findAllOnMenu(PaginationRequest pagination) {
        Page<BurgerEntity> page = jpaRepository
                .findByIsOnMenuTrueAndDeletedAtIsNull(buildPageable(pagination));
        return mapToPageResponse(page);
    }


    // ========================================
    // MENÚ - BUSCAR
    // ========================================



    @Override
    public Optional<Burger> findActiveMenuById(Integer idBurger) {
        return jpaRepository.findActiveMenuByIdWithProducts(idBurger)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Burger> findActiveById(Integer idBurger) {
        return jpaRepository.findByIdWithProductsAndDeletedAtIsNull(idBurger)
                .map(mapper::toDomain);
    }


    // ========================================
    // MENÚ - FILTROS Y BÚSQUEDA
    // ========================================

    @Override
    public PageResponse<Burger> searchMenuByName(String name, PaginationRequest pagination) {
        Page<BurgerEntity> page = jpaRepository.searchMenuByName(name, buildPageable(pagination));
        return mapToPageResponse(page);
    }



    // ========================================
    // MENÚ - VALIDACIONES
    // ========================================

    @Override
    public boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(String name) {
        return jpaRepository.existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(name);
    }



    // ========================================
    // CUSTOM (isCustom) - BUSCAR
    // ========================================

    @Override
    public Optional<Burger> findCustomByIdAndUser(Integer idBurger, Integer idUser) {
        return jpaRepository.findCustomByIdAndUser(idBurger, idUser)
                .map(mapper::toDomain);
    }



    // ========================================
    // ESTADÍSTICAS
    // ========================================




    @Override
    public List<Burger> findAllByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return jpaRepository.findAllByIds(ids)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    // ========================================
    // HELPERS
    // ========================================

    private Pageable buildPageable(PaginationRequest pagination) {
        if (pagination == null) {
            return PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        return PageRequest.of(pagination.getPage(), pagination.getSize(), SortBuilder.build(pagination));
    }

    private PageResponse<Burger> mapToPageResponse(Page<BurgerEntity> page) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public List<Burger> findAllByIngredientProductId(Integer idProduct) {
        return jpaRepository.findAllByIngredientProductId(idProduct)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }


    @Override
    public List<Burger> findAllFeaturedAndAvailable() {
        return jpaRepository.findAllFeaturedAndAvailable()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}