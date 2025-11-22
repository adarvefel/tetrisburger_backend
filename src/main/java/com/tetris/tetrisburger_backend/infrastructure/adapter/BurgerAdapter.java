package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.BurgerEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.BurgerJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BurgerAdapter implements BurgerRepository {

    private static final Logger logger = LoggerFactory.getLogger(BurgerAdapter.class);

    private final BurgerJpaRepository burgerJpaRepository;
    private final BurgerEntityMapper burgerEntityMapper;

    public BurgerAdapter(BurgerJpaRepository burgerJpaRepository,
                         BurgerEntityMapper burgerEntityMapper) {
        this.burgerJpaRepository = burgerJpaRepository;
        this.burgerEntityMapper = burgerEntityMapper;
    }

    @Override
    public Burger save(Burger burger) {
        logger.info("BurgerAdapter.save -> idBurger={}, ingredients.size={}",
                burger.getIdBurger(), burger.getIngredients().size());
        BurgerEntity entity = burgerEntityMapper.toEntity(burger);
        BurgerEntity saved = burgerJpaRepository.save(entity);
        return burgerEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Burger> findById(Integer idBurger) {
        logger.debug("Buscando hamburguesa por ID: {}", idBurger);
        return burgerJpaRepository.findById(idBurger)
                .map(burgerEntityMapper::toDomain);
    }



    @Override
    public PageResponse<Burger> findAllOnMenu(PaginationRequest pagination) {
        logger.debug("Buscando hamburguesas de menú page={}, size={}",
                pagination.getPage(), pagination.getSize());

        Sort sort = Sort.unsorted();
        if (pagination.getSortBy() != null) {
            sort = Sort.by(
                    Sort.Direction.fromString(pagination.getDirection()),
                    pagination.getSortBy()
            );
        }

        PageRequest pageRequest = PageRequest.of(
                pagination.getPage(),
                pagination.getSize(),
                sort
        );

        Page<BurgerEntity> page = burgerJpaRepository
                .findAllByIsOnMenuTrueAndAvailabilityTrueAndDeletedAtIsNull(pageRequest);

        List<Burger> content = page.getContent().stream()
                .map(burgerEntityMapper::toDomain)
                .toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public PageResponse<Burger> findAllCustomByUserId(Integer idUser, PaginationRequest pagination) {
        logger.debug("Buscando hamburguesas custom for user={} page={}, size={}",
                idUser, pagination.getPage(), pagination.getSize());

        Sort sort = Sort.unsorted();
        if (pagination.getSortBy() != null) {
            sort = Sort.by(
                    Sort.Direction.fromString(pagination.getDirection()),
                    pagination.getSortBy()
            );
        }

        PageRequest pageRequest = PageRequest.of(
                pagination.getPage(),
                pagination.getSize(),
                sort
        );

        Page<BurgerEntity> page = burgerJpaRepository
                .findAllByIdUserAndIsCustomTrueAndDeletedAtIsNull(idUser, pageRequest);

        List<Burger> content = page.getContent().stream()
                .map(burgerEntityMapper::toDomain)
                .toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public Optional<Burger> findCustomByIdAndUser(Integer idBurger, Integer idUser) {
        return burgerJpaRepository
                .findByIdBurgerAndIdUserAndIsCustomTrueAndDeletedAtIsNull(idBurger, idUser)
                .map(burgerEntityMapper::toDomain);
    }

    @Override
    public Optional<Burger> findMenuById(Integer idBurger) {
        logger.debug("Buscando hamburguesa de menú por ID: {}", idBurger);
        return burgerJpaRepository
                .findByIdBurgerAndIsOnMenuTrue(idBurger)
                .map(burgerEntityMapper::toDomain);
    }

    @Override
    public Optional<Burger> findActiveMenuById(Integer idBurger) {
        logger.debug("Buscando hamburguesa de menú ACTIVA por ID: {}", idBurger);
        return burgerJpaRepository
                .findByIdBurgerAndIsOnMenuTrueAndDeletedAtIsNull(idBurger)
                .map(burgerEntityMapper::toDomain);
    }


    @Override
    public PageResponse<Burger> searchMenuByName(String name, PaginationRequest pagination) {
        logger.debug("Buscando hamburguesas de menú por nombre='{}' page={}, size={}, sortBy={}",
                name, pagination.getPage(), pagination.getSize(), pagination.getSortBy());

        Sort sort = Sort.unsorted();
        if (pagination.getSortBy() != null) {
            sort = Sort.by(
                    Sort.Direction.fromString(pagination.getDirection()),
                    pagination.getSortBy()
            );
        }

        PageRequest pageRequest = PageRequest.of(
                pagination.getPage(),
                pagination.getSize(),
                sort
        );

        Page<BurgerEntity> page = burgerJpaRepository.searchMenuByName(name, pageRequest);

        List<Burger> content = page.getContent().stream()
                .map(burgerEntityMapper::toDomain)
                .toList();

        logger.info("Encontradas {} hamburguesas de menú con nombre='{}'",
                page.getTotalElements(), name);

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public PageResponse<Burger> searchCustomByName(Integer idUser, String name, PaginationRequest pagination) {
        logger.debug("Buscando hamburguesas custom del usuario={} por nombre='{}' page={}, size={}, sortBy={}",
                idUser, name, pagination.getPage(), pagination.getSize(), pagination.getSortBy());

        Sort sort = Sort.unsorted();
        if (pagination.getSortBy() != null) {
            sort = Sort.by(
                    Sort.Direction.fromString(pagination.getDirection()),
                    pagination.getSortBy()
            );
        }

        PageRequest pageRequest = PageRequest.of(
                pagination.getPage(),
                pagination.getSize(),
                sort
        );

        Page<BurgerEntity> page = burgerJpaRepository.searchCustomByName(idUser, name, pageRequest);

        List<Burger> content = page.getContent().stream()
                .map(burgerEntityMapper::toDomain)
                .toList();

        logger.info("Encontradas {} hamburguesas custom del usuario={} con nombre='{}'",
                page.getTotalElements(), idUser, name);

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }




}
