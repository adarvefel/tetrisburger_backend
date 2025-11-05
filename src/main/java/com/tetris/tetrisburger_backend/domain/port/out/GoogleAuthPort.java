// domain/port/out/GoogleAuthPort.java

package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.exception.InvalidTokenException;

import java.util.Map;

public interface GoogleAuthPort {
    /**
     * Valida el token de Google y extrae la información del usuario.
     * @param googleToken El JWT emitido por Google (ID Token).
     * @return Mapa que contiene "email" y "userName".
     * @throws InvalidTokenException Si el token es inválido o expirado.
     */
    Map<String, String> validateAndExtractUserInfo(String googleToken) throws InvalidTokenException;
}