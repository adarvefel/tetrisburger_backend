package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.port.in.user.DeleteProfileUser;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.DeleteProfileUserCommand;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class DeleteProfileUserUseCase implements DeleteProfileUser {

    private final Logger logger = LoggerFactory.getLogger(DeleteProfileUserUseCase.class);
    private final UserRepository userRepository;


    public DeleteProfileUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public void handle(DeleteProfileUserCommand command) {

            logger.warn("Solicitando  eliminacion del usuario con el ID:{}",command.idUser());

            //PRIMERO VALIDAR DE QUE EL USUARIO EXISTA
            userRepository.findUserById(command.idUser())
                    .orElseThrow(()->{
                        logger.error("Intentando eliminar usario inexistente - ID {}",command.idUser());
                        return new UserNotFoundException("Usurio no encontrado con ID: " + command.idUser());
                    });
            //Respuesta
            userRepository.deleteUserById(command.idUser());
            logger.warn("Eliminando al usuario con el ID:{}",command.idUser());

    }
}
