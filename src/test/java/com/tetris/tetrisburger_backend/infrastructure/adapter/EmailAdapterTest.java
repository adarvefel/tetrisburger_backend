package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.port.out.EmailPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de EmailAdapter")
class EmailAdapterTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailAdapter emailAdapter;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    private static final String FROM_EMAIL = "tetrisburger8@gmail.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailAdapter, "fromEmail", FROM_EMAIL);
    }

    @Nested
    @DisplayName("sendEmail básico")
    class SendEmailBasic {

        @Test
        @DisplayName("debería enviar email correctamente con parámetros válidos")
        void shouldSendEmailSuccessfully() {
            // Given
            String to = "user@example.com";
            String subject = "Test Subject";
            String body = "Test Body";

            doNothing().when(mailSender).send(any(SimpleMailMessage.class));

            // When
            emailAdapter.sendEmail(to, subject, body);

            // Then
            verify(mailSender).send(messageCaptor.capture());

            SimpleMailMessage sentMessage = messageCaptor.getValue();
            assertThat(sentMessage.getFrom()).isEqualTo(FROM_EMAIL);
            assertThat(sentMessage.getTo()).containsExactly(to);
            assertThat(sentMessage.getSubject()).isEqualTo(subject);
            assertThat(sentMessage.getText()).isEqualTo(body);
        }

        @Test
        @DisplayName("debería manejar MailException sin lanzar excepción")
        void shouldHandleMailExceptionGracefully() {
            // Given
            String to = "user@example.com";
            String subject = "Test";
            String body = "Body";

            doThrow(new MailException("SMTP error") {})
                    .when(mailSender).send(any(SimpleMailMessage.class));

            // When/Then - No debe lanzar excepción (fail-safe)
            assertThatCode(() -> emailAdapter.sendEmail(to, subject, body))
                    .doesNotThrowAnyException();

            verify(mailSender).send(any(SimpleMailMessage.class));
        }

        @Test
        @DisplayName("debería enviar email con caracteres especiales")
        void shouldSendEmailWithSpecialCharacters() {
            // Given
            String to = "user@example.com";
            String subject = "Asunto con ñ y tildes á é í ó ú";
            String body = "Cuerpo con emoji 🍔 y símbolos €$£";

            doNothing().when(mailSender).send(any(SimpleMailMessage.class));

            // When
            emailAdapter.sendEmail(to, subject, body);

            // Then
            verify(mailSender).send(messageCaptor.capture());

            SimpleMailMessage sentMessage = messageCaptor.getValue();
            assertThat(sentMessage.getSubject()).isEqualTo(subject);
            assertThat(sentMessage.getText()).isEqualTo(body);
        }
    }

    @Nested
    @DisplayName("sendEmail con template")
    class SendEmailWithTemplate {

        @Test
        @DisplayName("debería enviar email con template y variables")
        void shouldSendEmailWithTemplateAndVariables() {
            // Given
            String to = "user@example.com";
            String subject = "Welcome";
            String template = "Hello {name}, your code is {code}";
            Map<String, Object> vars = new HashMap<>();
            vars.put("name", "John");
            vars.put("code", "123456");

            doNothing().when(mailSender).send(any(SimpleMailMessage.class));

            // When
            emailAdapter.sendEmail(to, subject, template, vars);

            // Then
            verify(mailSender).send(messageCaptor.capture());

            SimpleMailMessage sentMessage = messageCaptor.getValue();
            assertThat(sentMessage.getTo()).containsExactly(to);
            assertThat(sentMessage.getSubject()).isEqualTo(subject);
            assertThat(sentMessage.getText()).isEqualTo("Hello John, your code is 123456");
        }

        @Test
        @DisplayName("debería enviar template sin variables")
        void shouldSendTemplateWithoutVariables() {
            // Given
            String to = "user@example.com";
            String subject = "Test";
            String template = "Simple message";
            Map<String, Object> vars = new HashMap<>();

            doNothing().when(mailSender).send(any(SimpleMailMessage.class));

            // When
            emailAdapter.sendEmail(to, subject, template, vars);

            // Then
            verify(mailSender).send(messageCaptor.capture());

            SimpleMailMessage sentMessage = messageCaptor.getValue();
            assertThat(sentMessage.getText()).isEqualTo("Simple message");
        }

        @Test
        @DisplayName("debería enviar template con variables null")
        void shouldSendTemplateWithNullVariables() {
            // Given
            String to = "user@example.com";
            String subject = "Test";
            String template = "Message with {placeholder}";

            doNothing().when(mailSender).send(any(SimpleMailMessage.class));

            // When
            emailAdapter.sendEmail(to, subject, template, null);

            // Then
            verify(mailSender).send(messageCaptor.capture());

            SimpleMailMessage sentMessage = messageCaptor.getValue();
            assertThat(sentMessage.getText()).isEqualTo("Message with {placeholder}");
        }
    }

    @Nested
    @DisplayName("sendWelcomeEmail")
    class SendWelcomeEmail {

        @Test
        @DisplayName("debería enviar email de bienvenida correctamente")
        void shouldSendWelcomeEmail() {
            // Given
            String to = "newuser@example.com";
            String userName = "John Doe";

            doNothing().when(mailSender).send(any(SimpleMailMessage.class));

            // When
            emailAdapter.sendWelcomeEmail(to, userName);

            // Then
            verify(mailSender).send(messageCaptor.capture());

            SimpleMailMessage sentMessage = messageCaptor.getValue();
            assertThat(sentMessage.getTo()).containsExactly(to);
            assertThat(sentMessage.getSubject()).contains("Bienvenido");
            assertThat(sentMessage.getText()).contains(userName);
            assertThat(sentMessage.getText()).contains("TetrisBurger");
        }

        @Test
        @DisplayName("debería incluir información relevante en el email de bienvenida")
        void shouldIncludeRelevantInformationInWelcomeEmail() {
            // Given
            String to = "test@example.com";
            String userName = "Jane Smith";

            doNothing().when(mailSender).send(any(SimpleMailMessage.class));

            // When
            emailAdapter.sendWelcomeEmail(to, userName);

            // Then
            verify(mailSender).send(messageCaptor.capture());

            SimpleMailMessage sentMessage = messageCaptor.getValue();
            String emailBody = sentMessage.getText();

            assertThat(emailBody).contains("Hola " + userName);
            assertThat(emailBody).contains("cuenta ha sido creada");
            assertThat(emailBody).contains("support@tetrisburger.com");
        }
    }

    @Nested
    @DisplayName("sendPasswordResetEmail")
    class SendPasswordResetEmail {

        @Test
        @DisplayName("debería enviar email de recuperación de contraseña")
        void shouldSendPasswordResetEmail() {
            // Given
            String to = "user@example.com";
            String userName = "John Doe";
            String resetLink = "https://tetrisburger.com/reset?token=abc123";

            doNothing().when(mailSender).send(any(SimpleMailMessage.class));

            // When
            emailAdapter.sendPasswordResetEmail(to, userName, resetLink);

            // Then
            verify(mailSender).send(messageCaptor.capture());

            SimpleMailMessage sentMessage = messageCaptor.getValue();
            assertThat(sentMessage.getTo()).containsExactly(to);
            assertThat(sentMessage.getSubject()).contains("Recuperación");
            assertThat(sentMessage.getText()).contains(userName);
            assertThat(sentMessage.getText()).contains(resetLink);
        }

        @Test
        @DisplayName("debería incluir advertencia de expiración en email de recuperación")
        void shouldIncludeExpirationWarning() {
            // Given
            String to = "user@example.com";
            String userName = "Jane";
            String resetLink = "https://tetrisburger.com/reset?token=xyz";

            doNothing().when(mailSender).send(any(SimpleMailMessage.class));

            // When
            emailAdapter.sendPasswordResetEmail(to, userName, resetLink);

            // Then
            verify(mailSender).send(messageCaptor.capture());

            SimpleMailMessage sentMessage = messageCaptor.getValue();
            String emailBody = sentMessage.getText();

            assertThat(emailBody).contains("expira");
            assertThat(emailBody).contains("1 hora");
            assertThat(emailBody).contains(resetLink);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("debería manejar email largo")
        void shouldHandleLongEmail() {
            // Given
            String to = "user@example.com";
            String subject = "Test";
            String body = "A".repeat(10000); // 10k caracteres

            doNothing().when(mailSender).send(any(SimpleMailMessage.class));

            // When
            emailAdapter.sendEmail(to, subject, body);

            // Then
            verify(mailSender).send(messageCaptor.capture());
            assertThat(messageCaptor.getValue().getText()).hasSize(10000);
        }

        @Test
        @DisplayName("debería renderizar template con múltiples variables")
        void shouldRenderTemplateWithMultipleVariables() {
            // Given
            String template = "Hi {name}, your order {orderId} total is {total} for {items} items";
            Map<String, Object> vars = new HashMap<>();
            vars.put("name", "John");
            vars.put("orderId", 12345);
            vars.put("total", 49.99);
            vars.put("items", 3);

            doNothing().when(mailSender).send(any(SimpleMailMessage.class));

            // When
            emailAdapter.sendEmail("test@example.com", "Order", template, vars);

            // Then
            verify(mailSender).send(messageCaptor.capture());

            String result = messageCaptor.getValue().getText();
            assertThat(result).isEqualTo("Hi John, your order 12345 total is 49.99 for 3 items");
        }
    }

    @Nested
    @DisplayName("Implementa EmailPort")
    class ImplementsEmailPort {

        @Test
        @DisplayName("debería implementar EmailPort")
        void shouldImplementEmailPort() {
            assertThat(emailAdapter).isInstanceOf(EmailPort.class);
        }
    }
}
