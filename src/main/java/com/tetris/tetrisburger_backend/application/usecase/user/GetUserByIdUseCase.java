package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.GetUserById;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.GetUserByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.slf4j.Logger;


@Service
@Transactional
public class GetUserByIdUseCase implements GetUserById {

    private final UserRepository userRepository;

    public GetUserByIdUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User handle(Integer idUser) {
        return userRepository.findUserById(idUser)
                .orElseThrow(() ->{
                    return new UserNotFoundException("Usuario no encontrado con ID:"+ idUser);


                });

    }





}

