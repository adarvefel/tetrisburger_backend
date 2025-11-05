package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.GetUserProfile;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.GetUserProfileQuery;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import com.tetris.tetrisburger_backend.infrastructure.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetUserProfileUseCase implements GetUserProfile {

    private static final Logger logger = LoggerFactory.getLogger(GetUserProfileUseCase.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public GetUserProfileUseCase(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User execute(GetUserProfileQuery query) {
        logger.debug("Obteniendo perfil del usuario autenticado");

        try {
            // Obtener ID del usuario desde el contexto de seguridad (JWT)
            Integer idUser = jwtUtil.getUserIdFromContext();

            logger.debug("Buscando usuario con ID: {}", idUser);

            User user = userRepository.findUserById(idUser)
                    .orElseThrow(() -> {
                        logger.warn("Usuario no encontrado: {}", idUser);
                        return new UserNotFoundException("Usuario no encontrado");
                    });

            logger.info("Perfil obtenido exitosamente para usuario: {}", user.getEmail());
            return user;

        } catch (Exception e) {
            logger.error("Error al obtener perfil del usuario: {}", e.getMessage(), e);
            throw e;
        }
    }
}
