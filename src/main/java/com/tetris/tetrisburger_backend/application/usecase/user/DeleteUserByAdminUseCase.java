package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.DeleteUserByAdmin;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.DeleteUserByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@Transactional
public class DeleteUserByAdminUseCase implements DeleteUserByAdmin {

    private static final Logger logger = LoggerFactory.getLogger(DeleteUserByAdminUseCase.class);
    private final UserRepository userRepository;

    public DeleteUserByAdminUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(DeleteUserByAdminCommand command) {
        logger.info("Eliminando usuario con ID: {} por admin: {}",
                command.idUser(), command.deletedBy());

        // 1. Buscar usuario
        User user = userRepository.findUserById(command.idUser())
                .orElseThrow(() -> new RuntimeException(
                        "Usuario no encontrado con ID: " + command.idUser()));

        // 2. SOFT DELETE: Marcar como eliminado
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(command.deletedBy());

        // 3. Guardar
        userRepository.saveUser(user);

        logger.info("Usuario {} marcado como eliminado por admin {}",
                command.idUser(), command.deletedBy());
    }

}
