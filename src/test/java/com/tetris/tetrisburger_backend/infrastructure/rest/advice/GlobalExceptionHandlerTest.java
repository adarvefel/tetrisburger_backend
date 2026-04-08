package com.tetris.tetrisburger_backend.infrastructure.rest.advice;

import com.tetris.tetrisburger_backend.domain.exception.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    private MockMvc mockMvc;

    // ── Controlador de prueba interno ─────────────────────────────────────

    @RestController
    @RequestMapping("/test")
    static class TestController {

        @GetMapping("/user-exists")
        public void userExists() { throw new UserAlreadyExistsException("ya existe"); }

        @GetMapping("/user-not-found")
        public void userNotFound() { throw new UserNotFoundException("no encontrado"); }

        @GetMapping("/invalid-credentials")
        public void invalidCredentials() { throw new InvalidCredentialsException("credenciales"); }

        @GetMapping("/invalid-token")
        public void invalidToken() { throw new InvalidTokenException("token"); }

        @GetMapping("/invalid-recaptcha")
        public void invalidRecaptcha() { throw new InvalidRecaptchaException("recaptcha"); }

        @GetMapping("/image-upload")
        public void imageUpload() { throw new ImageUploadException("imagen", new RuntimeException()); }

        @GetMapping("/pqrs-deleted")
        public void pqrsDeleted() { throw new PqrsAlreadyDeletedException("pqrs"); }

        @GetMapping("/data-integrity-unique")
        public void dataIntegrityUnique() {
            throw new DataIntegrityViolationException("error",
                    new RuntimeException("UNIQUE constraint failed / duplicate entry uq_email"));
        }

        @GetMapping("/data-integrity-fk")
        public void dataIntegrityFk() {
            throw new DataIntegrityViolationException("error",
                    new RuntimeException("foreign key constraint fails fk_user"));
        }

        @GetMapping("/data-integrity-generic")
        public void dataIntegrityGeneric() {
            throw new DataIntegrityViolationException("Conflicto genérico");
        }

        @GetMapping("/illegal-argument")
        public void illegalArgument() { throw new IllegalArgumentException("argumento inválido"); }

        @GetMapping("/missing-param")
        public void missingParam(@RequestParam String requiredParam) { }

        // Lanza la excepción directamente para evitar problemas con standaloneSetup
        @PostMapping("/media-type-not-supported")
        public void mediaTypeNotSupported() throws HttpMediaTypeNotSupportedException {
            throw new HttpMediaTypeNotSupportedException(
                    new org.springframework.http.MediaType("application", "xml"),
                    List.of(MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA)
            );
        }

        @GetMapping("/access-denied")
        public void accessDenied() { throw new AccessDeniedException("denegado"); }

        @GetMapping("/category-not-found")
        public void categoryNotFound() { throw new ProductCategoryNotFoundException("categoría"); }

        @GetMapping("/entity-not-found-category")
        public void entityNotFoundCategory() {
            throw new EntityNotFoundException("Unable to find ProductCategoryEntity with id 5");
        }

        @GetMapping("/entity-not-found-product")
        public void entityNotFoundProduct() {
            throw new EntityNotFoundException("Unable to find ProductEntity with id 10");
        }

        @GetMapping("/entity-not-found-generic")
        public void entityNotFoundGeneric() {
            throw new EntityNotFoundException("some entity not found");
        }

        @GetMapping("/generic-exception")
        public void genericException() { throw new RuntimeException("error inesperado"); }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    // ── User Exceptions ───────────────────────────────────────────────────

    @Nested
    @DisplayName("User Exceptions")
    class UserExceptions {

        @Test
        @DisplayName("UserAlreadyExistsException → 409 CONFLICT")
        void shouldReturn409WhenUserAlreadyExists() throws Exception {
            mockMvc.perform(get("/test/user-exists"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Usuario ya existe"))
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("UserNotFoundException → 404 NOT FOUND")
        void shouldReturn404WhenUserNotFound() throws Exception {
            mockMvc.perform(get("/test/user-not-found"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Usuario no encontrado"))
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("InvalidCredentialsException → 401 UNAUTHORIZED")
        void shouldReturn401WhenInvalidCredentials() throws Exception {
            mockMvc.perform(get("/test/invalid-credentials"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Credenciales inválidas"))
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("InvalidTokenException → 400 BAD REQUEST")
        void shouldReturn400WhenInvalidToken() throws Exception {
            mockMvc.perform(get("/test/invalid-token"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Token inválido"))
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    // ── Recaptcha Exceptions ──────────────────────────────────────────────

    @Nested
    @DisplayName("Recaptcha Exceptions")
    class RecaptchaExceptions {

        @Test
        @DisplayName("InvalidRecaptchaException → 400 BAD REQUEST")
        void shouldReturn400WhenInvalidRecaptcha() throws Exception {
            mockMvc.perform(get("/test/invalid-recaptcha"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(
                            "Verificación de seguridad falló. Por favor intenta de nuevo."))
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    // ── Image Exceptions ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Image Exceptions")
    class ImageExceptions {

        @Test
        @DisplayName("ImageUploadException → 500 INTERNAL SERVER ERROR")
        void shouldReturn500WhenImageUploadFails() throws Exception {
            mockMvc.perform(get("/test/image-upload"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value(
                            "No se pudo subir la imagen. Intenta de nuevo."))
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    // ── PQRS Exceptions ───────────────────────────────────────────────────

    @Nested
    @DisplayName("PQRS Exceptions")
    class PqrsExceptions {

        @Test
        @DisplayName("PqrsAlreadyDeletedException → 400 BAD REQUEST")
        void shouldReturn400WhenPqrsAlreadyDeleted() throws Exception {
            mockMvc.perform(get("/test/pqrs-deleted"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("PQRS ya eliminada"))
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    // ── Database Exceptions ───────────────────────────────────────────────

    @Nested
    @DisplayName("Database Exceptions")
    class DatabaseExceptions {

        @Test
        @DisplayName("DataIntegrityViolationException con UNIQUE → 409 con mensaje duplicado")
        void shouldReturn409WithDuplicateMessageWhenUniqueViolation() throws Exception {
            mockMvc.perform(get("/test/data-integrity-unique"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(
                            "Registro duplicado: ya existe un recurso con esos datos"))
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("DataIntegrityViolationException con FOREIGN KEY → 409 con mensaje FK")
        void shouldReturn409WithFkMessageWhenFkViolation() throws Exception {
            mockMvc.perform(get("/test/data-integrity-fk"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(
                            "Referencia inválida a datos relacionados"))
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("DataIntegrityViolationException genérica → 409 con mensaje genérico")
        void shouldReturn409WithGenericMessageWhenGenericViolation() throws Exception {
            mockMvc.perform(get("/test/data-integrity-generic"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Conflicto de integridad de datos"))
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    // ── Validation Exceptions ─────────────────────────────────────────────

    @Nested
    @DisplayName("Validation Exceptions")
    class ValidationExceptions {

        @Test
        @DisplayName("IllegalArgumentException → 400 BAD REQUEST con mensaje de la excepción")
        void shouldReturn400WithExceptionMessageWhenIllegalArgument() throws Exception {
            mockMvc.perform(get("/test/illegal-argument"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("argumento inválido"))
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("MissingServletRequestParameterException → 400 BAD REQUEST con nombre del parámetro")
        void shouldReturn400WithParamNameWhenMissingParam() throws Exception {
            mockMvc.perform(get("/test/missing-param"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(
                            "El parámetro 'requiredParam' es requerido"))
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("HttpMediaTypeNotSupportedException → 400 BAD REQUEST")
        void shouldReturn400WhenMediaTypeNotSupported() throws Exception {
            mockMvc.perform(post("/test/media-type-not-supported")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(
                            "Tipo de contenido no soportado. Usa multipart/form-data o application/json"))
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    // ── Security Exceptions ───────────────────────────────────────────────

    @Nested
    @DisplayName("Security Exceptions")
    class SecurityExceptions {

        @Test
        @DisplayName("AccessDeniedException → 403 FORBIDDEN")
        void shouldReturn403WhenAccessDenied() throws Exception {
            mockMvc.perform(get("/test/access-denied"))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(
                            "No tienes permisos para realizar esta acción"))
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    // ── HTTP Exceptions ───────────────────────────────────────────────────

    @Nested
    @DisplayName("HTTP Exceptions")
    class HttpExceptions {

        @Test
        @DisplayName("HttpRequestMethodNotSupportedException → 405 METHOD NOT ALLOWED")
        void shouldReturn405WhenMethodNotSupported() throws Exception {
            mockMvc.perform(delete("/test/user-exists"))
                    .andExpect(status().isMethodNotAllowed())
                    .andExpect(jsonPath("$.message").value(
                            "Método 'DELETE' no permitido para esta ruta"))
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    // ── Product Exceptions ────────────────────────────────────────────────

    @Nested
    @DisplayName("Product Exceptions")
    class ProductExceptions {

        @Test
        @DisplayName("ProductCategoryNotFoundException → 404 con ErrorResponseDTO")
        void shouldReturn404WhenCategoryNotFound() throws Exception {
            mockMvc.perform(get("/test/category-not-found"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value("Categoría no encontrada"))
                    .andExpect(jsonPath("$.path").value("/test/category-not-found"))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }

        @Test
        @DisplayName("EntityNotFoundException con ProductCategoryEntity → 404 con mensaje de categoría")
        void shouldReturn404WithCategoryMessageWhenEntityNotFoundForCategory() throws Exception {
            mockMvc.perform(get("/test/entity-not-found-category"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value(
                            "Error: El producto tiene una categoría inexistente en la base de datos"))
                    .andExpect(jsonPath("$.path").value("/test/entity-not-found-category"));
        }

        @Test
        @DisplayName("EntityNotFoundException con ProductEntity → 404 con mensaje de producto")
        void shouldReturn404WithProductMessageWhenEntityNotFoundForProduct() throws Exception {
            mockMvc.perform(get("/test/entity-not-found-product"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Producto no encontrado"))
                    .andExpect(jsonPath("$.path").value("/test/entity-not-found-product"));
        }

        @Test
        @DisplayName("EntityNotFoundException genérica → 404 con mensaje genérico")
        void shouldReturn404WithGenericMessageWhenEntityNotFoundGeneric() throws Exception {
            mockMvc.perform(get("/test/entity-not-found-generic"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Recurso no encontrado"));
        }
    }

    // ── Fallback ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Fallback Exception")
    class FallbackException {

        @Test
        @DisplayName("Exception genérica → 500 INTERNAL SERVER ERROR")
        void shouldReturn500WhenGenericException() throws Exception {
            mockMvc.perform(get("/test/generic-exception"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("Error interno del servidor"))
                    .andExpect(jsonPath("$.success").value(false));
        }
    }
}
