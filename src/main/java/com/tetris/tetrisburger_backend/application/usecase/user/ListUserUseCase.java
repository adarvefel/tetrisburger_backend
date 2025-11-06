package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.ListUser;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.ListUsersQuery;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ListUserUseCase implements ListUser {

    private static final Logger logger = LoggerFactory.getLogger(ListUserUseCase.class);
    private final UserRepository userRepository;

    public ListUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public PageResponse<User> execute(ListUsersQuery query) {
        logger.debug("Listando usuarios - página: {}, tamaño: {}, ordenado por: {}",
                query.page(),
                query.size(),
                query.sortBy());

        PageResponse<User> users = userRepository.findAllUsers(query);

        logger.debug("Se encontraron {} usuario(s) en total, mostrando página {}/{}",
                users.totalElements(),
                users.page() + 1,
                users.totalPages());

        return users;
    }
}
