package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.DeleteUserByAdmin;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.DeleteUserByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DeleteUserByAdminUseCase implements DeleteUserByAdmin {

    private final UserRepository userRepository;

    public DeleteUserByAdminUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(DeleteUserByAdminCommand command) {

        // 1. Buscar usuario
        User user = userRepository.findUserById(command.idUser())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + command.idUser()));

        // 2. Soft delete usando método de dominio
        user.markAsDeleted(command.deletedBy());


        // 3. Guardar
        userRepository.saveUser(user);
    }
}
