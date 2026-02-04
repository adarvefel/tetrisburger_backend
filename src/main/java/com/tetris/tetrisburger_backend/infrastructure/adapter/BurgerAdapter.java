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

import java.util.List;
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
        return jpaRepository.findById(idBurger)
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
    // CONSULTAS DE MENÚ - LISTAR
    // ========================================

    @Override
    public PageResponse<Burger> findAllOnMenu(PaginationRequest pagination) {
        Pageable pageable = buildPageable(pagination);
        Page<BurgerEntity> page = jpaRepository.findByIsOnMenuTrueAndDeletedAtIsNull(pageable);
        return mapToPageResponse(page);
    }

    @Override
    public PageResponse<Burger> findAllAvailableOnMenu(PaginationRequest pagination) {
        Pageable pageable = buildPageable(pagination);
        Page<BurgerEntity> page = jpaRepository
                .findAllByIsOnMenuTrueAndAvailabilityTrueAndDeletedAtIsNull(pageable);
        return mapToPageResponse(page);
    }

    @Override
    public PageResponse<Burger> findAllFavoriteMenuBurgers(PaginationRequest pagination) {
        Pageable pageable = buildPageable(pagination);
        Page<BurgerEntity> page = jpaRepository
                .findAllByIsOnMenuTrueAndIsFavoriteTrueAndDeletedAtIsNull(pageable);
        return mapToPageResponse(page);
    }

    @Override
    public List<Burger> findAllMenuBurgers() {
        return jpaRepository.findAllByIsOnMenuTrueAndDeletedAtIsNull()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    // ========================================
    // CONSULTAS DE MENÚ - BUSCAR
    // ========================================

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
    public Optional<Burger> findByNameAndIsOnMenuTrueAndDeletedAtIsNull(String name) {
        return jpaRepository.findByNameAndIsOnMenuTrueAndDeletedAtIsNull(name)
                .map(mapper::toDomain);
    }

    // ========================================
    // CONSULTAS DE MENÚ - BÚSQUEDA Y FILTROS
    // ========================================

    @Override
    public PageResponse<Burger> searchMenuByName(String name, PaginationRequest pagination) {
        Pageable pageable = buildPageable(pagination);
        Page<BurgerEntity> page = jpaRepository.searchMenuByName(name, pageable);
        return mapToPageResponse(page);
    }

    @Override
    public PageResponse<Burger> searchMenuBurgersWithFilters(
            String name,
            Boolean availability,
            Boolean isFavorite,
            PaginationRequest pagination) {

        Pageable pageable = buildPageable(pagination);
        Page<BurgerEntity> page = jpaRepository.searchMenuBurgersWithFilters(
                name, availability, isFavorite, pageable
        );
        return mapToPageResponse(page);
    }

    @Override
    public PageResponse<Burger> findTopOrderedMenuBurgers(PaginationRequest pagination) {
        Pageable pageable = buildPageable(pagination);
        Page<BurgerEntity> page = jpaRepository.findTopOrderedMenuBurgers(pageable);
        return mapToPageResponse(page);
    }

    @Override
    public PageResponse<Burger> findMenuBurgersOrderByPriceAsc(PaginationRequest pagination) {
        Pageable pageable = buildPageable(pagination);
        Page<BurgerEntity> page = jpaRepository.findMenuBurgersOrderByPriceAsc(pageable);
        return mapToPageResponse(page);
    }

    @Override
    public PageResponse<Burger> findMenuBurgersOrderByPriceDesc(PaginationRequest pagination) {
        Pageable pageable = buildPageable(pagination);
        Page<BurgerEntity> page = jpaRepository.findMenuBurgersOrderByPriceDesc(pageable);
        return mapToPageResponse(page);
    }

    // ========================================
    // CONSULTAS DE MENÚ - VALIDACIONES
    // ========================================

    @Override
    public boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(String name) {
        return jpaRepository.existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(name);
    }

    @Override
    public boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNullAndIdBurgerNot(
            String name,
            Integer excludeId) {
        return jpaRepository.existsByNameAndIsOnMenuTrueAndDeletedAtIsNullAndIdBurgerNot(
                name, excludeId
        );
    }

    // ========================================
    // CONSULTAS DE CUSTOM BURGERS - LISTAR
    // ========================================

    @Override
    public PageResponse<Burger> findAllCustomByUserId(Integer idUser, PaginationRequest pagination) {
        Pageable pageable = buildPageable(pagination);
        Page<BurgerEntity> page = jpaRepository
                .findAllByIdUserAndIsCustomTrueAndDeletedAtIsNull(idUser, pageable);
        return mapToPageResponse(page);
    }

    @Override
    public PageResponse<Burger> findAllFavoriteCustomByUserId(Integer idUser, PaginationRequest pagination) {
        Pageable pageable = buildPageable(pagination);
        Page<BurgerEntity> page = jpaRepository
                .findAllByIdUserAndIsCustomTrueAndIsFavoriteTrueAndDeletedAtIsNull(idUser, pageable);
        return mapToPageResponse(page);
    }

    @Override
    public List<Burger> findAllCustomByUserId(Integer idUser) {
        return jpaRepository.findAllByIdUserAndIsCustomTrueAndDeletedAtIsNull(idUser)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    // ========================================
    // CONSULTAS DE CUSTOM BURGERS - BUSCAR
    // ========================================

    @Override
    public Optional<Burger> findCustomByIdAndUser(Integer idBurger, Integer idUser) {
        return jpaRepository
                .findByIdBurgerAndIdUserAndIsCustomTrueAndDeletedAtIsNull(idBurger, idUser)
                .map(mapper::toDomain);
    }

    @Override
    public PageResponse<Burger> searchCustomByName(
            Integer idUser,
            String name,
            PaginationRequest pagination) {

        Pageable pageable = buildPageable(pagination);
        Page<BurgerEntity> page = jpaRepository.searchCustomByName(idUser, name, pageable);
        return mapToPageResponse(page);
    }

    @Override
    public PageResponse<Burger> searchCustomBurgersWithFilters(
            Integer idUser,
            String name,
            Boolean isFavorite,
            PaginationRequest pagination) {

        Pageable pageable = buildPageable(pagination);
        Page<BurgerEntity> page = jpaRepository.searchCustomBurgersWithFilters(
                idUser, name, isFavorite, pageable
        );
        return mapToPageResponse(page);
    }

    @Override
    public PageResponse<Burger> findTopOrderedCustomBurgersByUser(
            Integer idUser,
            PaginationRequest pagination) {

        Pageable pageable = buildPageable(pagination);
        Page<BurgerEntity> page = jpaRepository
                .findTopOrderedCustomBurgersByUser(idUser, pageable);
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
    public long countCustomBurgersByUser(Integer idUser) {
        return jpaRepository.countCustomBurgersByUser(idUser);
    }

    @Override
    public long countFavoriteMenuBurgers() {
        return jpaRepository.countFavoriteMenuBurgers();
    }

    @Override
    public Optional<Burger> findActiveById(Integer idBurger) {
        return jpaRepository.findByIdBurgerAndDeletedAtIsNull(idBurger)
                .map(mapper::toDomain);
    }

    // ========================================
    // HELPERS
    // ========================================

    private Pageable buildPageable(PaginationRequest pagination) {
        if (pagination == null) {
            return PageRequest.of(0, 10, Sort.by("createdAt").descending());
        }

        Sort sort = pagination.getDirection() != null
                && pagination.getDirection().equalsIgnoreCase("DESC")
                ? Sort.by(pagination.getSortBy()).descending()
                : Sort.by(pagination.getSortBy()).ascending();

        return PageRequest.of(
                pagination.getPage(),
                pagination.getSize(),
                sort
        );
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
