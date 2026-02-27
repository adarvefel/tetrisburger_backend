package com.tetris.tetrisburger_backend.domain.port.in.user;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.SearchUsersByEmailQuery;

import java.util.List;

public interface SearchUsersByEmail {
    PageResponse<User> handle(SearchUsersByEmailQuery query, PaginationRequest request);

}
