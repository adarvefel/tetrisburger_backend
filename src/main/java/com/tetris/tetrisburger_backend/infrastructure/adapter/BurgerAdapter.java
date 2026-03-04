package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.BurgerEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.BurgerJpaRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.util.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
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

    // ========================================
    // MENÚ - LISTAR
    // ========================================

    @Override
    public PageResponse<Burger> findAllOnMenu(PaginationRequest pagination) {
        Page<BurgerEntity> page = jpaRepository
                .findByIsOnMenuTrueAndDeletedAtIsNull(buildPageable(pagination));
        return mapToPageResponse(page);
    }

    @Override
    public PageResponse<Burger> findAllAvailableOnMenu(PaginationRequest pagination) {
        Page<BurgerEntity> page = jpaRepository
                .findAllByIsOnMenuTrueAndAvailabilityTrueAndDeletedAtIsNull(buildPageable(pagination));
        return mapToPageResponse(page);
    }

    @Override
    public PageResponse<Burger> findAllFeaturedMenuBurgers(PaginationRequest pagination) {
        Page<BurgerEntity> page = jpaRepository
                .findAllByIsOnMenuTrueAndIsFeaturedTrueAndDeletedAtIsNull(buildPageable(pagination));
        return mapToPageResponse(page);
    }

    @Override
    public List<Burger> findAllMenuBurgers() {
        return jpaRepository.findAllByIsOnMenuTrueAndDeletedAtIsNull()
                .stream().map(mapper::toDomain).toList();
    }

    // ========================================
    // MENÚ - BUSCAR
    // ========================================

    @Override
    public Optional<Burger> findMenuById(Integer idBurger) {
        return jpaRepository.findByIdOnMenuWithProducts(idBurger)
                .map(mapper::toDomain);
    }

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

    @Override
    public Optional<Burger> findByNameAndIsOnMenuTrueAndDeletedAtIsNull(String name) {
        return jpaRepository.findByNameAndIsOnMenuTrueAndDeletedAtIsNull(name)
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

    @Override
    public PageResponse<Burger> searchMenuBurgersWithFilters(
            String name, Boolean availability, Boolean isFeatured, PaginationRequest pagination) {
        Page<BurgerEntity> page = jpaRepository
                .searchMenuBurgersWithFilters(name, availability, isFeatured, buildPageable(pagination));
        return mapToPageResponse(page);
    }

    @Override
    public PageResponse<Burger> findTopOrderedMenuBurgers(PaginationRequest pagination) {
        Page<BurgerEntity> page = jpaRepository.findTopOrderedMenuBurgers(buildPageable(pagination));
        return mapToPageResponse(page);
    }

    @Override
    public PageResponse<Burger> findMenuBurgersOrderByPriceAsc(PaginationRequest pagination) {
        Page<BurgerEntity> page = jpaRepository.findMenuBurgersOrderByPriceAsc(buildPageable(pagination));
        return mapToPageResponse(page);
    }

    @Override
    public PageResponse<Burger> findMenuBurgersOrderByPriceDesc(PaginationRequest pagination) {
        Page<BurgerEntity> page = jpaRepository.findMenuBurgersOrderByPriceDesc(buildPageable(pagination));
        return mapToPageResponse(page);
    }

    // ========================================
    // MENÚ - VALIDACIONES
    // ========================================

    @Override
    public boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(String name) {
        return jpaRepository.existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(name);
    }

    @Override
    public boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNullAndIdBurgerNot(
            String name, Integer excludeId) {
        return jpaRepository.existsByNameAndIsOnMenuTrueAndDeletedAtIsNullAndIdBurgerNot(name, excludeId);
    }

    // ========================================
    // CUSTOM (isSaved) - LISTAR
    // ========================================

    @Override
    public PageResponse<Burger> findAllSavedByUserId(Integer idUser, PaginationRequest pagination) {
        Page<BurgerEntity> page = jpaRepository
                .findAllByIdUserAndIsSavedTrueAndDeletedAtIsNull(idUser, buildPageable(pagination));
        return mapToPageResponse(page);
    }

    @Override
    public PageResponse<Burger> findAllFeaturedSavedByUserId(Integer idUser, PaginationRequest pagination) {
        Page<BurgerEntity> page = jpaRepository
                .findAllByIdUserAndIsSavedTrueAndIsFeaturedTrueAndDeletedAtIsNull(idUser, buildPageable(pagination));
        return mapToPageResponse(page);
    }

    @Override
    public List<Burger> findAllSavedByUserId(Integer idUser) {
        return jpaRepository.findAllByIdUserAndIsSavedTrueAndDeletedAtIsNull(idUser)
                .stream().map(mapper::toDomain).toList();
    }

    // ========================================
    // CUSTOM (isSaved) - BUSCAR
    // ========================================

    @Override
    public Optional<Burger> findSavedByIdAndUser(Integer idBurger, Integer idUser) {
        return jpaRepository.findSavedByIdAndUserWithProducts(idBurger, idUser)
                .map(mapper::toDomain);
    }

    @Override
    public PageResponse<Burger> searchSavedByName(
            Integer idUser, String name, PaginationRequest pagination) {
        Page<BurgerEntity> page = jpaRepository.searchCustomByName(idUser, name, buildPageable(pagination));
        return mapToPageResponse(page);
    }

    @Override
    public PageResponse<Burger> searchSavedBurgersWithFilters(
            Integer idUser, String name, Boolean isFeatured, PaginationRequest pagination) {
        Page<BurgerEntity> page = jpaRepository
                .searchCustomBurgersWithFilters(idUser, name, isFeatured, buildPageable(pagination));
        return mapToPageResponse(page);
    }

    @Override
    public PageResponse<Burger> findTopOrderedSavedBurgersByUser(
            Integer idUser, PaginationRequest pagination) {
        Page<BurgerEntity> page = jpaRepository
                .findTopOrderedCustomBurgersByUser(idUser, buildPageable(pagination));
        return mapToPageResponse(page);
    }

    // ========================================
    // ESTADÍSTICAS
    // ========================================

    @Override
    public long countActiveMenuBurgers() {
        return jpaRepository.countActiveMenuBurgers();
    }

    @Override
    public long countSavedBurgersByUser(Integer idUser) {
        return jpaRepository.countCustomBurgersByUser(idUser);
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
}