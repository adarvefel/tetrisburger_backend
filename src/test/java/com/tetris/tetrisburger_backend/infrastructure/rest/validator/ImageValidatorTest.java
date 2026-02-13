package com.tetris.tetrisburger_backend.infrastructure.rest.validator;

import com.tetris.tetrisburger_backend.domain.exception.ImageUploadException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ImageValidator Tests")
class ImageValidatorTest {

    private final ImageValidator validator = new ImageValidator();

    // Magic bytes to simulate real files
    private static final byte[] JPEG_MAGIC_BYTES = new byte[]{
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0
    };

    private static final byte[] PNG_MAGIC_BYTES = new byte[]{
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    private static final byte[] WEBP_MAGIC_BYTES = new byte[]{
            0x52, 0x49, 0x46, 0x46, // "RIFF"
            0x00, 0x00, 0x00, 0x00, // File size
            0x57, 0x45, 0x42, 0x50  // "WEBP"
    };

    private static final byte[] PDF_MAGIC_BYTES = new byte[]{
            0x25, 0x50, 0x44, 0x46 // "%PDF"
    };

    @Nested
    @DisplayName("Successful Validations")
    class SuccessfulValidationsTests {

        @Test
        @DisplayName("should validate JPEG image correctly")
        void shouldValidateJpegImageCorrectly() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1024L); // 1KB
            when(file.getOriginalFilename()).thenReturn("foto.jpg");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(JPEG_MAGIC_BYTES));

            // When & Then
            assertThatCode(() -> validator.validate(file))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should validate PNG image correctly")
        void shouldValidatePngImageCorrectly() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(2048L); // 2KB
            when(file.getOriginalFilename()).thenReturn("logo.png");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(PNG_MAGIC_BYTES));

            // When & Then
            assertThatCode(() -> validator.validate(file))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should validate WebP image correctly")
        void shouldValidateWebpImageCorrectly() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1500L);
            when(file.getOriginalFilename()).thenReturn("imagen.webp");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(WEBP_MAGIC_BYTES));

            // When & Then
            assertThatCode(() -> validator.validate(file))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should validate image at 5MB limit")
        void shouldValidateImageAtFiveMBLimit() throws IOException {
            // Given
            long fiveMB = 5 * 1024 * 1024;
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(fiveMB);
            when(file.getOriginalFilename()).thenReturn("grande.jpg");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(JPEG_MAGIC_BYTES));

            // When & Then
            assertThatCode(() -> validator.validate(file))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should validate image without extension in filename")
        void shouldValidateImageWithoutExtension() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1024L);
            when(file.getOriginalFilename()).thenReturn("imagen_sin_extension");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(JPEG_MAGIC_BYTES));

            // When & Then
            assertThatCode(() -> validator.validate(file))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Null or Empty File Validation")
    class NullOrEmptyFileTests {

        @Test
        @DisplayName("should throw exception when file is null")
        void shouldThrowExceptionWhenFileIsNull() {
            // When & Then
            assertThatThrownBy(() -> validator.validate(null))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessage("La imagen es requerida");
        }

        @Test
        @DisplayName("should throw exception when file is empty")
        void shouldThrowExceptionWhenFileIsEmpty() {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessage("La imagen es requerida");
        }
    }

    @Nested
    @DisplayName("Size Validation")
    class SizeValidationTests {

        @Test
        @DisplayName("should reject image larger than 5MB")
        void shouldRejectImageLargerThanFiveMB() throws IOException {
            // Given
            long sixMB = 6 * 1024 * 1024;
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(sixMB);
            when(file.getOriginalFilename()).thenReturn("muy-grande.jpg");

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessageContaining("Imagen muy grande")
                    .hasMessageContaining("6.00 MB")
                    .hasMessageContaining("Máximo 5MB");
        }

        @Test
        @DisplayName("should reject 10MB image")
        void shouldRejectTenMBImage() throws IOException {
            // Given
            long tenMB = 10 * 1024 * 1024;
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(tenMB);
            when(file.getOriginalFilename()).thenReturn("enorme.png");

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessageContaining("10.00 MB")
                    .hasMessageContaining("Máximo 5MB");
        }

        @Test
        @DisplayName("should reject image that exceeds limit by 1 byte")
        void shouldRejectImageThatExceedsLimitByOneByte() throws IOException {
            // Given
            long fiveMBPlusOneByte = (5 * 1024 * 1024) + 1;
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(fiveMBPlusOneByte);
            when(file.getOriginalFilename()).thenReturn("limite-excedido.jpg");

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessageContaining("Imagen muy grande")
                    .hasMessageContaining("Máximo 5MB");
        }
    }

    @Nested
    @DisplayName(".jfif Files Validation")
    class JfifFilesValidationTests {

        @Test
        @DisplayName("should reject file with lowercase .jfif extension")
        void shouldRejectFileWithLowercaseJfifExtension() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1024L);
            when(file.getOriginalFilename()).thenReturn("foto.jfif");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(JPEG_MAGIC_BYTES));

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessage("Archivos .jfif no permitidos. Convierta a JPG/PNG primero");
        }

        @Test
        @DisplayName("should reject file with uppercase .JFIF extension")
        void shouldRejectFileWithUppercaseJfifExtension() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1024L);
            when(file.getOriginalFilename()).thenReturn("FOTO.JFIF");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(JPEG_MAGIC_BYTES));

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessage("Archivos .jfif no permitidos. Convierta a JPG/PNG primero");
        }

        @Test
        @DisplayName("should reject file with mixed case .JfIf extension")
        void shouldRejectFileWithMixedCaseJfifExtension() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1024L);
            when(file.getOriginalFilename()).thenReturn("imagen.JfIf");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(JPEG_MAGIC_BYTES));

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessage("Archivos .jfif no permitidos. Convierta a JPG/PNG primero");
        }

        @Test
        @DisplayName("should reject .jfif file even with valid JPEG magic bytes")
        void shouldRejectJfifFileEvenWithValidJpegMagicBytes() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(2048L);
            when(file.getOriginalFilename()).thenReturn("valido-pero-jfif.jfif");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(JPEG_MAGIC_BYTES));

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessageContaining(".jfif no permitidos");
        }
    }

    @Nested
    @DisplayName("MIME Type Validation")
    class MimeTypeValidationTests {

        @Test
        @DisplayName("should reject PDF file")
        void shouldRejectPdfFile() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1024L);
            when(file.getOriginalFilename()).thenReturn("documento.pdf");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(PDF_MAGIC_BYTES));

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessageContaining("Tipo de archivo no permitido")
                    .hasMessageContaining("application/pdf")
                    .hasMessageContaining("Use: JPG, PNG o WebP");
        }

        @Test
        @DisplayName("should reject text file")
        void shouldRejectTextFile() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            byte[] plainText = "Esto es un archivo de texto".getBytes();

            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1024L);
            when(file.getOriginalFilename()).thenReturn("archivo.txt");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(plainText));

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessageContaining("Tipo de archivo no permitido")
                    .hasMessageContaining("Use: JPG, PNG o WebP");
        }

        @Test
        @DisplayName("should reject HTML file")
        void shouldRejectHtmlFile() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            byte[] html = "<html><body>Test</body></html>".getBytes();

            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1024L);
            when(file.getOriginalFilename()).thenReturn("pagina.html");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(html));

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessageContaining("Tipo de archivo no permitido")
                    .hasMessageContaining("Use: JPG, PNG o WebP");
        }

        @Test
        @DisplayName("should reject file with .jpg extension but PDF content")
        void shouldRejectFileWithFakeExtension() throws IOException {
            // Given - file with .jpg extension but PDF content
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1024L);
            when(file.getOriginalFilename()).thenReturn("fake.jpg");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(PDF_MAGIC_BYTES));

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessageContaining("Tipo de archivo no permitido")
                    .hasMessageContaining("application/pdf");
        }

        @Test
        @DisplayName("should reject unsupported GIF image")
        void shouldRejectUnsupportedGifImage() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            byte[] gifMagicBytes = new byte[]{0x47, 0x49, 0x46, 0x38, 0x39, 0x61}; // GIF89a

            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1024L);
            when(file.getOriginalFilename()).thenReturn("animacion.gif");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(gifMagicBytes));

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessageContaining("Tipo de archivo no permitido")
                    .hasMessageContaining("image/gif")
                    .hasMessageContaining("Use: JPG, PNG o WebP");
        }
    }

    @Nested
    @DisplayName("IOException Handling")
    class IOExceptionHandlingTests {

        @Test
        @DisplayName("should throw exception when file read fails")
        void shouldThrowExceptionWhenFileReadFails() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1024L);
            when(file.getOriginalFilename()).thenReturn("corrupto.jpg");
            when(file.getInputStream()).thenThrow(new IOException("Error de lectura del disco"));

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessageContaining("Error al validar archivo")
                    .hasMessageContaining("Error de lectura del disco");
        }

        @Test
        @DisplayName("should throw exception when InputStream is null")
        void shouldThrowExceptionWhenInputStreamIsNull() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1024L);
            when(file.getOriginalFilename()).thenReturn("archivo.jpg");
            when(file.getInputStream()).thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessageContaining("Error al validar archivo");
        }

        @Test
        @DisplayName("should throw exception when InputStream throws error on read")
        void shouldThrowExceptionWhenInputStreamThrowsErrorOnRead() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            InputStream mockInputStream = mock(InputStream.class);

            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1024L);
            when(file.getOriginalFilename()).thenReturn("error.jpg");
            when(file.getInputStream()).thenReturn(mockInputStream);
            when(mockInputStream.read()).thenThrow(new IOException("Stream corrupto"));

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessageContaining("Error al validar archivo");
        }
    }

    @Nested
    @DisplayName("Edge and Special Cases")
    class EdgeAndSpecialCasesTests {

        @Test
        @DisplayName("should validate file with null filename but valid MIME type")
        void shouldValidateFileWithNullFilenameButValidMimeType() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1024L);
            when(file.getOriginalFilename()).thenReturn(null);
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(JPEG_MAGIC_BYTES));

            // When & Then
            assertThatCode(() -> validator.validate(file))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should validate very small file of 1 byte")
        void shouldValidateVerySmallFileOfOneByte() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1L);
            when(file.getOriginalFilename()).thenReturn("tiny.jpg");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(JPEG_MAGIC_BYTES));

            // When & Then
            assertThatCode(() -> validator.validate(file))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should validate file with .jfif in middle of filename")
        void shouldValidateFileWithJfifInMiddleOfFilename() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1024L);
            when(file.getOriginalFilename()).thenReturn("archivo.jfif.jpg"); // ends in .jpg
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(JPEG_MAGIC_BYTES));

            // When & Then - should NOT reject because it doesn't end in .jfif
            assertThatCode(() -> validator.validate(file))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should validate JPEG image with Spanish filename and special characters")
        void shouldValidateJpegImageWithSpanishFilenameAndSpecialCharacters() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(2048L);
            when(file.getOriginalFilename()).thenReturn("hamburguesa-súper-deliciosa-2026.jpg");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(JPEG_MAGIC_BYTES));

            // When & Then
            assertThatCode(() -> validator.validate(file))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Integration with Real Use Cases")
    class RealUseCasesIntegrationTests {

        @Test
        @DisplayName("should validate typical burger photo")
        void shouldValidateTypicalBurgerPhoto() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(3 * 1024 * 1024L); // 3MB
            when(file.getOriginalFilename()).thenReturn("burger-deluxe.jpg");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(JPEG_MAGIC_BYTES));

            // When & Then
            assertThatCode(() -> validator.validate(file))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should reject overly large product image")
        void shouldRejectOverlyLargeProductImage() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(8 * 1024 * 1024L); // 8MB
            when(file.getOriginalFilename()).thenReturn("producto-alta-resolucion.png");

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessageContaining("8.00 MB")
                    .hasMessageContaining("Máximo 5MB");
        }

        @Test
        @DisplayName("should reject user attempting to upload malicious script")
        void shouldRejectUserAttemptingToUploadMaliciousScript() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            byte[] script = "<script>alert('xss')</script>".getBytes();

            when(file.isEmpty()).thenReturn(false);
            when(file.getSize()).thenReturn(1024L);
            when(file.getOriginalFilename()).thenReturn("hack.jpg");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(script));

            // When & Then
            assertThatThrownBy(() -> validator.validate(file))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessageContaining("Tipo de archivo no permitido");
        }
    }
}
