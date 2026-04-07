package com.tetris.tetrisburger_backend.application.usecase.auth;

import com.tetris.tetrisburger_backend.domain.exception.InvalidRecaptchaException;
import com.tetris.tetrisburger_backend.domain.exception.UserAlreadyExistsException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.RegisterUser;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.RegisterUserCommand;
import com.tetris.tetrisburger_backend.domain.port.out.EmailPort;
import com.tetris.tetrisburger_backend.domain.port.out.RecaptchaPort;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RegisterUserUseCase implements RegisterUser {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailPort emailPort;
    private final RecaptchaPort recaptchaPort;

    public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailPort emailPort, RecaptchaPort recaptchaPort) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailPort = emailPort;
        this.recaptchaPort = recaptchaPort;
    }



    @Override
    public User handle(RegisterUserCommand cmd) {

        boolean isHuman = recaptchaPort.verifyToken(cmd.recaptchaToken(),"register");
        if(!isHuman){
            throw new InvalidRecaptchaException(" Verificación de seguridad falló. Por favor intenta de nuevo.");
        }


        // 1. Verificar que el email no existe
        if (userRepository.existsByEmail(cmd.email())) {
            throw new UserAlreadyExistsException("El email ya está registrado");
        }

        // 2. Hashear contraseña
        String hashedPassword = passwordEncoder.encode(cmd.password());

        // 3. Crear cliente usando factory method del dominio
        User newUser = User.createClient(
                cmd.userName(),
                cmd.email(),
                hashedPassword
        );

        // 4. Guardar usuario
        User savedUser = userRepository.saveUser(newUser);

        // 5. Enviar email de bienvenida (no debe romper el registro si falla)
        try {
            emailPort.sendWelcomeEmail(savedUser.getEmail(), savedUser.getUserName());
        } catch (Exception e) {

        }

        return savedUser;
    }
}
