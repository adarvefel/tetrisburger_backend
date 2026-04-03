package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.ListUser;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.ListUsersQuery;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ListUserUseCase implements ListUser {

    private final UserRepository userRepository;

    public ListUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public PageResponse<User> execute(ListUsersQuery query) {

        PageResponse<User> users = userRepository.findAllUsers(query);

        return users;
    }
}
