
package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.tetris.tetrisburger_backend.domain.exception.InvalidTokenException;
import com.tetris.tetrisburger_backend.domain.port.out.GoogleAuthPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class GoogleAuthAdapter implements GoogleAuthPort {

    private final GoogleIdTokenVerifier verifier;

    // Inyecta el CLIENT ID de la configuración
    public GoogleAuthAdapter(@Value("${google.oauth.client-id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                // Define tu Client ID como la audiencia válida
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    @Override
    public Map<String, String> validateAndExtractUserInfo(String googleToken) throws InvalidTokenException {
        try {
            GoogleIdToken idToken = verifier.verify(googleToken);

            if (idToken == null) {
                throw new InvalidTokenException("Token de Google inválido o expirado.");
            }

            // El token es válido, extraemos la carga útil
            GoogleIdToken.Payload payload = idToken.getPayload();

            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("email", payload.getEmail());
            // El campo 'name' generalmente contiene el nombre completo
            userInfo.put("userName", (String) payload.get("name"));

            return userInfo;

        } catch (GeneralSecurityException | IOException | IllegalArgumentException e) {
            // Captura errores de seguridad, I/O o formato inválido de token
            throw new InvalidTokenException("Fallo en la validación del token de Google: " + e.getMessage());
        }
    }
}