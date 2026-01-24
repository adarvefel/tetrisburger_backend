package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.FilterUsersByRole;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.FilterUsersByRoleQuery;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FilterUsersByRoleUseCase implements FilterUsersByRole {

    private final UserRepository userRepository;

    public FilterUsersByRoleUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public PageResponse<User> handle(FilterUsersByRoleQuery query) {
        Pageable pageable = PageRequest.of(
                query.page(),
                query.size(),
                Sort.by(query.sortBy()).ascending()
        );

        return userRepository.findByRole(query.role(), pageable);
    }


}
