package com.tetris.tetrisburger_backend.application.usecase.menu;

import com.tetris.tetrisburger_backend.domain.exception.EntityNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Menu;
import com.tetris.tetrisburger_backend.domain.port.in.menu.GetMenuById;
import com.tetris.tetrisburger_backend.domain.port.out.MenuRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GetMenuByIdUseCase implements GetMenuById {

    private final MenuRepository repository;

    public GetMenuByIdUseCase(MenuRepository repository) {
        this.repository = repository;
    }

    @Override
    public Menu handle(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Menú no encontrado con id: " + id
                ));
    }
}