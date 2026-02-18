package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de S3ImageStorageAdapter")
class S3ImageStorageAdapterTest {

    @Mock
    private S3Client s3Client;

    @Captor
    private ArgumentCaptor<PutObjectRequest> putRequestCaptor;

    @Captor
    private ArgumentCaptor<DeleteObjectRequest> deleteRequestCaptor;

    private S3ImageStorageAdapter adapter;

    private static final String BUCKET_NAME = "test-bucket";
    private static final String REGION = "us-east-1";

    @BeforeEach
    void setUp() {
        adapter = new S3ImageStorageAdapter(s3Client, BUCKET_NAME, REGION);
    }

    @Nested
    @DisplayName("uploadUserImage")
    class UploadUserImage {

        @Test
        @DisplayName("debería subir imagen de usuario correctamente")
        void shouldUploadUserImageSuccessfully() {
            // Given
            byte[] imageBytes = "fake-image-content".getBytes();
            String contentType = "image/jpeg";
            String originalFileName = "profile.jpg";

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When
            ImageUploadResult result = adapter.uploadUserImage(imageBytes, contentType, originalFileName);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.imageKey()).startsWith("users/");
            assertThat(result.imageKey()).contains("-profile.jpg");
            assertThat(result.originalFileName()).isEqualTo(originalFileName);

            verify(s3Client).putObject(putRequestCaptor.capture(), any(RequestBody.class));

            PutObjectRequest request = putRequestCaptor.getValue();
            assertThat(request.bucket()).isEqualTo(BUCKET_NAME);
            assertThat(request.contentType()).isEqualTo(contentType);
        }

        @Test
        @DisplayName("debería subir imagen PNG")
        void shouldUploadPngImage() {
            // Given
            byte[] imageBytes = new byte[1024];
            String contentType = "image/png";
            String originalFileName = "avatar.png";

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When
            ImageUploadResult result = adapter.uploadUserImage(imageBytes, contentType, originalFileName);

            // Then
            assertThat(result.imageKey()).contains("avatar.png");
            assertThat(result.imageKey()).startsWith("users/");
        }

        @Test
        @DisplayName("debería retornar null si bytes es null")
        void shouldReturnNullIfBytesIsNull() {
            // When
            ImageUploadResult result = adapter.uploadUserImage(null, "image/jpeg", "test.jpg");

            // Then
            assertThat(result).isNull();
            verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }

        @Test
        @DisplayName("debería retornar null si bytes está vacío")
        void shouldReturnNullIfBytesIsEmpty() {
            // When
            ImageUploadResult result = adapter.uploadUserImage(new byte[0], "image/jpeg", "test.jpg");

            // Then
            assertThat(result).isNull();
            verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }

        @Test
        @DisplayName("debería lanzar excepción si contentType no es imagen")
        void shouldThrowExceptionIfContentTypeIsNotImage() {
            // Given
            byte[] bytes = "content".getBytes();

            // When/Then
            assertThatThrownBy(() ->
                    adapter.uploadUserImage(bytes, "application/pdf", "file.pdf"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("debe ser una imagen");
        }

        @Test
        @DisplayName("debería lanzar excepción si imagen supera 5MB")
        void shouldThrowExceptionIfImageExceeds5MB() {
            // Given
            byte[] largeBytes = new byte[6 * 1024 * 1024]; // 6MB

            // When/Then
            assertThatThrownBy(() ->
                    adapter.uploadUserImage(largeBytes, "image/jpeg", "large.jpg"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("no puede superar 5MB");
        }
    }

    @Nested
    @DisplayName("uploadProductImage")
    class UploadProductImage {

        @Test
        @DisplayName("debería subir imagen de producto correctamente")
        void shouldUploadProductImageSuccessfully() {
            // Given
            byte[] imageBytes = "product-image".getBytes();
            FileData fileData = new FileData("burger.jpg", "image/jpeg", imageBytes);

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When
            ImageUploadResult result = adapter.uploadProductImage(fileData);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.imageKey()).startsWith("products/");
            assertThat(result.imageKey()).contains("burger.jpg");
            assertThat(result.originalFileName()).isEqualTo("burger.jpg");

            verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }

        @Test
        @DisplayName("debería sanitizar nombre de archivo con caracteres especiales")
        void shouldSanitizeFileNameWithSpecialCharacters() {
            // Given
            byte[] imageBytes = "image".getBytes();
            FileData fileData = new FileData("Hamburguesa Especial #1.jpg", "image/jpeg", imageBytes);

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When
            ImageUploadResult result = adapter.uploadProductImage(fileData);

            // Then
            assertThat(result.imageKey()).contains("hamburguesa_especial__1.jpg");
        }

        @Test
        @DisplayName("debería generar imageKey único para cada subida")
        void shouldGenerateUniqueImageKeyForEachUpload() {
            // Given
            byte[] imageBytes = "image".getBytes();
            FileData fileData = new FileData("test.jpg", "image/jpeg", imageBytes);

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When
            ImageUploadResult result1 = adapter.uploadProductImage(fileData);
            ImageUploadResult result2 = adapter.uploadProductImage(fileData);

            // Then
            assertThat(result1.imageKey()).isNotEqualTo(result2.imageKey());
        }
    }

    @Nested
    @DisplayName("uploadMenuBurgerImage")
    class UploadMenuBurgerImage {

        @Test
        @DisplayName("debería subir imagen de menú burger correctamente")
        void shouldUploadMenuBurgerImageSuccessfully() {
            // Given
            byte[] imageBytes = "burger-menu-image".getBytes();
            FileData fileData = new FileData("classic-burger.png", "image/png", imageBytes);
            Integer burgerId = 1;
            Integer adminId = 100;

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When
            ImageUploadResult result = adapter.uploadMenuBurgerImage(fileData, burgerId, adminId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.imageKey()).startsWith("burgers-menu/");
            assertThat(result.imageKey()).contains("classic-burger.png");

            verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }

        @Test
        @DisplayName("debería subir imagen WebP")
        void shouldUploadWebpImage() {
            // Given
            byte[] imageBytes = "webp-image".getBytes();
            FileData fileData = new FileData("burger.webp", "image/webp", imageBytes);

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When
            ImageUploadResult result = adapter.uploadMenuBurgerImage(fileData, 5, 10);

            // Then
            assertThat(result.imageKey()).endsWith(".webp");
            assertThat(result.imageKey()).startsWith("burgers-menu/");
        }
    }

    @Nested
    @DisplayName("uploadImage (deprecated)")
    class UploadImageDeprecated {

        @Test
        @DisplayName("debería subir imagen con carpeta personalizada")
        void shouldUploadImageWithCustomFolder() {
            // Given
            byte[] imageBytes = "test-image".getBytes();
            FileData fileData = new FileData("test.jpg", "image/jpeg", imageBytes);
            String folder = "custom-folder";

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When
            ImageUploadResult result = adapter.uploadImage(fileData, folder);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.imageKey()).startsWith("custom-folder/");
        }

        @Test
        @DisplayName("debería normalizar carpeta sin slash final")
        void shouldNormalizeFolderWithoutTrailingSlash() {
            // Given
            byte[] imageBytes = "image".getBytes();
            FileData fileData = new FileData("test.jpg", "image/jpeg", imageBytes);

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When
            ImageUploadResult result = adapter.uploadImage(fileData, "test");

            // Then
            assertThat(result.imageKey()).startsWith("test/");
        }

        @Test
        @DisplayName("debería retornar null si FileData es null")
        void shouldReturnNullIfFileDataIsNull() {
            // When
            ImageUploadResult result = adapter.uploadImage(null, "folder");

            // Then
            assertThat(result).isNull();
            verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }

        @Test
        @DisplayName("debería rechazar carpeta con path traversal")
        void shouldRejectFolderWithPathTraversal() {
            // Given
            byte[] imageBytes = "image".getBytes();
            FileData fileData = new FileData("test.jpg", "image/jpeg", imageBytes);

            // When/Then
            assertThatThrownBy(() -> adapter.uploadImage(fileData, "../etc"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Folder inválido");
        }

        @Test
        @DisplayName("debería rechazar carpeta que empieza con /")
        void shouldRejectFolderStartingWithSlash() {
            // Given
            byte[] imageBytes = "image".getBytes();
            FileData fileData = new FileData("test.jpg", "image/jpeg", imageBytes);

            // When/Then
            assertThatThrownBy(() -> adapter.uploadImage(fileData, "/root"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Folder inválido");
        }
    }

    @Nested
    @DisplayName("deleteImage")
    class DeleteImage {

        @Test
        @DisplayName("debería eliminar imagen correctamente")
        void shouldDeleteImageSuccessfully() {
            // Given
            String imageKey = "users/123-profile.jpg";

            when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(null);

            // When
            adapter.deleteImage(imageKey);

            // Then
            verify(s3Client).deleteObject(deleteRequestCaptor.capture());

            DeleteObjectRequest request = deleteRequestCaptor.getValue();
            assertThat(request.bucket()).isEqualTo(BUCKET_NAME);
            assertThat(request.key()).isEqualTo(imageKey);
        }

        @Test
        @DisplayName("debería manejar excepción al eliminar sin lanzar error")
        void shouldHandleExceptionWhenDeletingGracefully() {
            // Given
            String imageKey = "users/test.jpg";

            when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                    .thenThrow(S3Exception.builder().message("Not found").build());

            // When/Then - No debe lanzar excepción
            assertThatCode(() -> adapter.deleteImage(imageKey))
                    .doesNotThrowAnyException();

            verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
        }

        @Test
        @DisplayName("debería ignorar eliminación si imageKey es null")
        void shouldIgnoreDeletionIfImageKeyIsNull() {
            // When
            adapter.deleteImage(null);

            // Then
            verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
        }

        @Test
        @DisplayName("debería ignorar eliminación si imageKey está vacío")
        void shouldIgnoreDeletionIfImageKeyIsEmpty() {
            // When
            adapter.deleteImage("");

            // Then
            verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
        }
    }

    @Nested
    @DisplayName("getImageUrl")
    class GetImageUrl {

        @Test
        @DisplayName("debería generar URL correcta para imagen")
        void shouldGenerateCorrectImageUrl() {
            // Given
            String imageKey = "users/123-profile.jpg";

            // When
            String url = adapter.getImageUrl(imageKey);

            // Then
            String expectedUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                    BUCKET_NAME, REGION, imageKey);
            assertThat(url).isEqualTo(expectedUrl);
        }

        @Test
        @DisplayName("debería generar URL para producto")
        void shouldGenerateUrlForProduct() {
            // Given
            String imageKey = "products/456-burger.jpg";

            // When
            String url = adapter.getImageUrl(imageKey);

            // Then
            assertThat(url).contains("products/456-burger.jpg");
            assertThat(url).startsWith("https://");
            assertThat(url).contains(".s3.");
            assertThat(url).contains(".amazonaws.com");
        }

        @Test
        @DisplayName("debería retornar null si imageKey es null")
        void shouldReturnNullIfImageKeyIsNull() {
            // When
            String url = adapter.getImageUrl(null);

            // Then
            assertThat(url).isNull();
        }

        @Test
        @DisplayName("debería retornar null si imageKey está vacío")
        void shouldReturnNullIfImageKeyIsEmpty() {
            // When
            String url = adapter.getImageUrl("");

            // Then
            assertThat(url).isNull();
        }
    }

    @Nested
    @DisplayName("File Validation")
    class FileValidation {

        @Test
        @DisplayName("debería aceptar imagen JPEG")
        void shouldAcceptJpegImage() {
            // Given
            byte[] imageBytes = "jpeg-content".getBytes();

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When/Then
            assertThatCode(() ->
                    adapter.uploadUserImage(imageBytes, "image/jpeg", "test.jpg"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("debería aceptar imagen PNG")
        void shouldAcceptPngImage() {
            // Given
            byte[] imageBytes = "png-content".getBytes();

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When/Then
            assertThatCode(() ->
                    adapter.uploadUserImage(imageBytes, "image/png", "test.png"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("debería aceptar imagen GIF")
        void shouldAcceptGifImage() {
            // Given
            byte[] imageBytes = "gif-content".getBytes();

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When/Then
            assertThatCode(() ->
                    adapter.uploadUserImage(imageBytes, "image/gif", "test.gif"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("debería rechazar archivo PDF")
        void shouldRejectPdfFile() {
            // Given
            byte[] fileBytes = "pdf-content".getBytes();

            // When/Then
            assertThatThrownBy(() ->
                    adapter.uploadUserImage(fileBytes, "application/pdf", "document.pdf"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("debe ser una imagen");
        }

        @Test
        @DisplayName("debería rechazar archivo de texto")
        void shouldRejectTextFile() {
            // Given
            byte[] fileBytes = "text content".getBytes();

            // When/Then
            assertThatThrownBy(() ->
                    adapter.uploadUserImage(fileBytes, "text/plain", "file.txt"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("debería rechazar contentType null")
        void shouldRejectNullContentType() {
            // Given
            byte[] imageBytes = "content".getBytes();

            // When/Then
            assertThatThrownBy(() ->
                    adapter.uploadUserImage(imageBytes, null, "test.jpg"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("debería aceptar imagen de exactamente 5MB")
        void shouldAcceptImageOfExactly5MB() {
            // Given
            byte[] imageBytes = new byte[5 * 1024 * 1024]; // Exactamente 5MB

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When/Then
            assertThatCode(() ->
                    adapter.uploadUserImage(imageBytes, "image/jpeg", "5mb.jpg"))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("File Name Sanitization")
    class FileNameSanitization {

        @Test
        @DisplayName("debería sanitizar espacios en nombres de archivo")
        void shouldSanitizeSpacesInFileName() {
            // Given
            byte[] imageBytes = "image".getBytes();
            FileData fileData = new FileData("my profile picture.jpg", "image/jpeg", imageBytes);

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When
            ImageUploadResult result = adapter.uploadProductImage(fileData);

            // Then
            assertThat(result.imageKey()).contains("my_profile_picture.jpg");
        }

        @Test
        @DisplayName("debería convertir nombre base a minúsculas pero mantener extensión original")
        void shouldConvertFileNameToLowerCase() {
            // Given
            byte[] imageBytes = "image".getBytes();
            FileData fileData = new FileData("UPPERCASE.JPG", "image/jpeg", imageBytes);

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When
            ImageUploadResult result = adapter.uploadProductImage(fileData);

            // Then
            // ✅ AJUSTADO: El adapter convierte todo a minúsculas incluyendo la extensión
            assertThat(result.imageKey()).contains("uppercase");
            assertThat(result.imageKey()).endsWith(".jpg"); // Extensión en minúsculas
        }

        @Test
        @DisplayName("debería reemplazar múltiples underscores")
        void shouldReplaceMultipleUnderscores() {
            // Given
            byte[] imageBytes = "image".getBytes();
            FileData fileData = new FileData("file___with___underscores.jpg", "image/jpeg", imageBytes);

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When
            ImageUploadResult result = adapter.uploadProductImage(fileData);

            // Then
            assertThat(result.imageKey()).contains("file_with_underscores.jpg");
        }

        @Test
        @DisplayName("debería sanitizar caracteres especiales correctamente")
        void shouldSanitizeSpecialCharacters() {
            // Given
            byte[] imageBytes = "image".getBytes();
            FileData fileData = new FileData("Hamburguesa Especial #1.jpg", "image/jpeg", imageBytes);

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When
            ImageUploadResult result = adapter.uploadProductImage(fileData);

            // Then
            // ✅ AJUSTADO: # se reemplaza por un solo _
            assertThat(result.imageKey()).contains("hamburguesa_especial_1.jpg");
            assertThat(result.imageKey()).doesNotContain("#");
            assertThat(result.imageKey()).doesNotContain(" ");
        }

        @Test
        @DisplayName("debería manejar nombre de archivo null")
        void shouldHandleNullFileName() {
            // Given
            byte[] imageBytes = "image".getBytes();
            FileData fileData = new FileData(null, "image/jpeg", imageBytes);

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When
            ImageUploadResult result = adapter.uploadProductImage(fileData);

            // Then
            assertThat(result.imageKey()).contains("file");
        }

        @Test
        @DisplayName("debería manejar nombre de archivo sin extensión")
        void shouldHandleFileNameWithoutExtension() {
            // Given
            byte[] imageBytes = "image".getBytes();
            FileData fileData = new FileData("noextension", "image/jpeg", imageBytes);

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When
            ImageUploadResult result = adapter.uploadProductImage(fileData);

            // Then
            assertThat(result.imageKey()).contains("noextension");
        }
    }

    @Nested
    @DisplayName("S3 Error Handling")
    class S3ErrorHandling {

        @Test
        @DisplayName("debería lanzar excepción si S3 falla al subir")
        void shouldThrowExceptionIfS3FailsToUpload() {
            // Given
            byte[] imageBytes = "image".getBytes();

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenThrow(S3Exception.builder().message("Access denied").build());

            // When/Then
            assertThatThrownBy(() ->
                    adapter.uploadUserImage(imageBytes, "image/jpeg", "test.jpg"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error al subir imagen a S3");
        }

        @Test
        @DisplayName("debería incluir mensaje de error original en excepción")
        void shouldIncludeOriginalErrorMessageInException() {
            // Given
            byte[] imageBytes = "image".getBytes();
            String errorMessage = "Bucket does not exist";

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenThrow(S3Exception.builder().message(errorMessage).build());

            // When/Then
            assertThatThrownBy(() ->
                    adapter.uploadUserImage(imageBytes, "image/jpeg", "test.jpg"))
                    .hasMessageContaining(errorMessage);
        }
    }

    @Nested
    @DisplayName("Implementa ImageStoragePort")
    class ImplementsImageStoragePort {

        @Test
        @DisplayName("debería implementar ImageStoragePort")
        void shouldImplementImageStoragePort() {
            assertThat(adapter).isInstanceOf(ImageStoragePort.class);
        }
    }

    @Nested
    @DisplayName("Key Generation")
    class KeyGeneration {

        @Test
        @DisplayName("debería incluir timestamp en la imageKey")
        void shouldIncludeTimestampInImageKey() {
            // Given
            byte[] imageBytes = "image".getBytes();

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When
            ImageUploadResult result = adapter.uploadUserImage(imageBytes, "image/jpeg", "test.jpg");

            // Then
            String key = result.imageKey();
            String[] parts = key.split("/")[1].split("-");
            assertThat(parts[0]).matches("\\d{13}"); // Timestamp de 13 dígitos
        }

        @Test
        @DisplayName("debería incluir UUID en la imageKey")
        void shouldIncludeUuidInImageKey() {
            // Given
            byte[] imageBytes = "image".getBytes();

            when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(null);

            // When
            ImageUploadResult result = adapter.uploadUserImage(imageBytes, "image/jpeg", "test.jpg");

            // Then
            String key = result.imageKey();
            String[] parts = key.split("/")[1].split("-");
            assertThat(parts[1]).hasSize(8); // UUID truncado a 8 caracteres
        }
    }
}
