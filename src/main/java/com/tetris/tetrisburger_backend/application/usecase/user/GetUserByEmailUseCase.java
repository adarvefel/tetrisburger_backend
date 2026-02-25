package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.GetUserByEmail;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GetUserByEmailUseCase implements GetUserByEmail {

    private final UserRepository userRepository;

    public GetUserByEmailUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User handle(String email) {

        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
    }
}
