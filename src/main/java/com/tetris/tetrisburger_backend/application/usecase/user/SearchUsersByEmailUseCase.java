package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.SearchUsersByEmail;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SearchUsersByEmailUseCase implements SearchUsersByEmail {

    private final UserRepository userRepository;

    public SearchUsersByEmailUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> handle(String emailPart) {
        return userRepository.searchUsersByEmail(emailPart);
    }
}
