package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.exception.UnauthorizedException;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;

public abstract class AuthenticatedController {

    protected Integer getUserId(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getId();
        }
        throw new UnauthorizedException("Usuario no autenticado correctamente");
    }
}
