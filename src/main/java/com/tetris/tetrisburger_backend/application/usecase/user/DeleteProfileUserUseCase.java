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

    private final UserRepository userRepository;


    public DeleteProfileUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public void handle(DeleteProfileUserCommand command) {


            //PRIMERO VALIDAR DE QUE EL USUARIO EXISTA
            userRepository.findUserById(command.idUser())
                    .orElseThrow(()-> new UserNotFoundException("Usurio no encontrado con ID: " + command.idUser()));
            //Respuesta
            userRepository.deleteUserById(command.idUser());

    }
}
