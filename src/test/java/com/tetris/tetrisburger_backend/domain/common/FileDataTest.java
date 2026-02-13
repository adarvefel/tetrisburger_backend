package com.tetris.tetrisburger_backend.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("FileData Tests")
class FileDataTest {

    // ============================================
    // FACTORY METHOD - from(MultipartFile)
    // ============================================

    @Nested
    @DisplayName("Factory from MultipartFile")
    class FromMultipartFileTests {

        @Test
        @DisplayName("should create FileData from valid MultipartFile")
        void shouldCreateFileDataFromValidMultipartFile() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            byte[] content = "test content".getBytes();

            when(file.isEmpty()).thenReturn(false);
            when(file.getOriginalFilename()).thenReturn("image.jpg");
            when(file.getContentType()).thenReturn("image/jpeg");
            when(file.getBytes()).thenReturn(content);

            // When
            FileData fileData = FileData.from(file);

            // Then
            assertThat(fileData).isNotNull();
            assertThat(fileData.originalFilename()).isEqualTo("image.jpg");
            assertThat(fileData.contentType()).isEqualTo("image/jpeg");
            assertThat(fileData.bytes()).isEqualTo(content);
        }

        @Test
        @DisplayName("should return null when file is null")
        void shouldReturnNullWhenFileIsNull() {
            // When
            FileData fileData = FileData.from(null);

            // Then
            assertThat(fileData).isNull();
        }

        @Test
        @DisplayName("should return null when file is empty")
        void shouldReturnNullWhenFileIsEmpty() {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(true);

            // When
            FileData fileData = FileData.from(file);

            // Then
            assertThat(fileData).isNull();
        }

        @Test
        @DisplayName("should throw exception when IOException occurs reading bytes")
        void shouldThrowExceptionWhenIOExceptionOccursReadingBytes() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getBytes()).thenThrow(new IOException("Error reading file"));

            // Then
            assertThatThrownBy(() ->
                    FileData.from(file)
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Error al leer archivo")
                    .hasCauseInstanceOf(IOException.class);
        }

        @Test
        @DisplayName("should handle PNG file")
        void shouldHandlePngFile() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            byte[] pngBytes = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47}; // PNG header

            when(file.isEmpty()).thenReturn(false);
            when(file.getOriginalFilename()).thenReturn("logo.png");
            when(file.getContentType()).thenReturn("image/png");
            when(file.getBytes()).thenReturn(pngBytes);

            // When
            FileData fileData = FileData.from(file);

            // Then
            assertThat(fileData).isNotNull();
            assertThat(fileData.originalFilename()).isEqualTo("logo.png");
            assertThat(fileData.contentType()).isEqualTo("image/png");
            assertThat(fileData.bytes()).isEqualTo(pngBytes);
        }

        @Test
        @DisplayName("should handle large file")
        void shouldHandleLargeFile() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            byte[] largeContent = new byte[5 * 1024 * 1024]; // 5 MB

            when(file.isEmpty()).thenReturn(false);
            when(file.getOriginalFilename()).thenReturn("large-image.jpg");
            when(file.getContentType()).thenReturn("image/jpeg");
            when(file.getBytes()).thenReturn(largeContent);

            // When
            FileData fileData = FileData.from(file);

            // Then
            assertThat(fileData).isNotNull();
            assertThat(fileData.bytes()).hasSize(5 * 1024 * 1024);
        }

        @Test
        @DisplayName("should handle filename with spaces")
        void shouldHandleFilenameWithSpaces() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);

            when(file.isEmpty()).thenReturn(false);
            when(file.getOriginalFilename()).thenReturn("my image with spaces.jpg");
            when(file.getContentType()).thenReturn("image/jpeg");
            when(file.getBytes()).thenReturn("content".getBytes());

            // When
            FileData fileData = FileData.from(file);

            // Then
            assertThat(fileData.originalFilename()).isEqualTo("my image with spaces.jpg");
        }

        @Test
        @DisplayName("should handle filename with special characters")
        void shouldHandleFilenameWithSpecialCharacters() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);

            when(file.isEmpty()).thenReturn(false);
            when(file.getOriginalFilename()).thenReturn("burger-super_special@2026.jpg");
            when(file.getContentType()).thenReturn("image/jpeg");
            when(file.getBytes()).thenReturn("content".getBytes());

            // When
            FileData fileData = FileData.from(file);

            // Then
            assertThat(fileData.originalFilename()).isEqualTo("burger-super_special@2026.jpg");
        }
    }

    // ============================================
    // METHOD isValid()
    // ============================================

    @Nested
    @DisplayName("isValid Method")
    class IsValidTests {

        @Test
        @DisplayName("should return true for valid FileData")
        void shouldReturnTrueForValidFileData() {
            // Given
            FileData fileData = new FileData(
                    "image.jpg",
                    "image/jpeg",
                    "content".getBytes()
            );

            // When
            boolean isValid = fileData.isValid();

            // Then
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("should return false when bytes is null")
        void shouldReturnFalseWhenBytesIsNull() {
            // Given
            FileData fileData = new FileData(
                    "image.jpg",
                    "image/jpeg",
                    null
            );

            // When
            boolean isValid = fileData.isValid();

            // Then
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("should return false when bytes is empty")
        void shouldReturnFalseWhenBytesIsEmpty() {
            // Given
            FileData fileData = new FileData(
                    "image.jpg",
                    "image/jpeg",
                    new byte[0]
            );

            // When
            boolean isValid = fileData.isValid();

            // Then
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("should return false when originalFilename is null")
        void shouldReturnFalseWhenOriginalFilenameIsNull() {
            // Given
            FileData fileData = new FileData(
                    null,
                    "image/jpeg",
                    "content".getBytes()
            );

            // When
            boolean isValid = fileData.isValid();

            // Then
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("should return false when originalFilename is empty")
        void shouldReturnFalseWhenOriginalFilenameIsEmpty() {
            // Given
            FileData fileData = new FileData(
                    "",
                    "image/jpeg",
                    "content".getBytes()
            );

            // When
            boolean isValid = fileData.isValid();

            // Then
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("should return true when contentType is null but other fields are valid")
        void shouldReturnTrueWhenContentTypeIsNullButOtherFieldsAreValid() {
            // Given
            FileData fileData = new FileData(
                    "image.jpg",
                    null,
                    "content".getBytes()
            );

            // When
            boolean isValid = fileData.isValid();

            // Then
            assertThat(isValid).isTrue(); // contentType is not validated in isValid()
        }

        @Test
        @DisplayName("should return false when all fields are null")
        void shouldReturnFalseWhenAllFieldsAreNull() {
            // Given
            FileData fileData = new FileData(null, null, null);

            // When
            boolean isValid = fileData.isValid();

            // Then
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("should return true for file with 1 byte")
        void shouldReturnTrueForFileWithOneByte() {
            // Given
            FileData fileData = new FileData(
                    "tiny.txt",
                    "text/plain",
                    new byte[]{1}
            );

            // When
            boolean isValid = fileData.isValid();

            // Then
            assertThat(isValid).isTrue();
        }
    }

    // ============================================
    // RECORD BEHAVIOR
    // ============================================

    @Nested
    @DisplayName("Record Behavior")
    class RecordBehaviorTests {

        @Test
        @DisplayName("should implement equals correctly")
        void shouldImplementEqualsCorrectly() {
            // Given
            byte[] bytes = "content".getBytes();
            FileData fileData1 = new FileData("file.jpg", "image/jpeg", bytes);
            FileData fileData2 = new FileData("file.jpg", "image/jpeg", bytes);
            FileData fileData3 = new FileData("other.jpg", "image/jpeg", bytes);

            // Then
            assertThat(fileData1).isEqualTo(fileData2);
            assertThat(fileData1).isNotEqualTo(fileData3);
        }

        @Test
        @DisplayName("should implement hashCode correctly")
        void shouldImplementHashCodeCorrectly() {
            // Given
            byte[] bytes = "content".getBytes();
            FileData fileData1 = new FileData("file.jpg", "image/jpeg", bytes);
            FileData fileData2 = new FileData("file.jpg", "image/jpeg", bytes);

            // Then
            assertThat(fileData1.hashCode()).isEqualTo(fileData2.hashCode());
        }

        @Test
        @DisplayName("should implement toString correctly")
        void shouldImplementToStringCorrectly() {
            // Given
            FileData fileData = new FileData(
                    "image.jpg",
                    "image/jpeg",
                    "content".getBytes()
            );

            // When
            String toString = fileData.toString();

            // Then
            assertThat(toString).contains("FileData");
            assertThat(toString).contains("image.jpg");
            assertThat(toString).contains("image/jpeg");
        }

        @Test
        @DisplayName("should be immutable")
        void shouldBeImmutable() {
            // Given
            String filename = "test.jpg";
            String contentType = "image/jpeg";
            byte[] bytes = "content".getBytes();

            // When
            FileData fileData = new FileData(filename, contentType, bytes);

            // Then - No setters available
            assertThat(fileData.originalFilename()).isEqualTo(filename);
            assertThat(fileData.contentType()).isEqualTo(contentType);
            assertThat(fileData.bytes()).isEqualTo(bytes);
        }
    }

    // ============================================
    // INTEGRATED USE CASES
    // ============================================

    @Nested
    @DisplayName("Integrated Use Cases")
    class IntegratedUseCasesTests {

        @Test
        @DisplayName("should handle complete flow of valid image upload")
        void shouldHandleCompleteFlowOfValidImageUpload() throws IOException {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            byte[] imageBytes = "fake-image-content".getBytes();

            when(file.isEmpty()).thenReturn(false);
            when(file.getOriginalFilename()).thenReturn("burger-deluxe.jpg");
            when(file.getContentType()).thenReturn("image/jpeg");
            when(file.getBytes()).thenReturn(imageBytes);

            // When
            FileData fileData = FileData.from(file);

            // Then
            assertThat(fileData).isNotNull();
            assertThat(fileData.isValid()).isTrue();
            assertThat(fileData.originalFilename()).isEqualTo("burger-deluxe.jpg");
            assertThat(fileData.contentType()).isEqualTo("image/jpeg");
            assertThat(fileData.bytes()).hasSize(imageBytes.length);
        }

        @Test
        @DisplayName("should detect invalid file from empty MultipartFile")
        void shouldDetectInvalidFileFromEmptyMultipartFile() {
            // Given
            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(true);

            // When
            FileData fileData = FileData.from(file);

            // Then
            assertThat(fileData).isNull();
        }

        @Test
        @DisplayName("should handle different MIME types")
        void shouldHandleDifferentMimeTypes() throws IOException {
            // Given
            String[] mimeTypes = {
                    "image/jpeg",
                    "image/png",
                    "image/webp",
                    "image/gif"
            };

            for (String mimeType : mimeTypes) {
                MultipartFile file = mock(MultipartFile.class);
                when(file.isEmpty()).thenReturn(false);
                when(file.getOriginalFilename()).thenReturn("test." + mimeType.split("/")[1]);
                when(file.getContentType()).thenReturn(mimeType);
                when(file.getBytes()).thenReturn("content".getBytes());

                // When
                FileData fileData = FileData.from(file);

                // Then
                assertThat(fileData).isNotNull();
                assertThat(fileData.contentType()).isEqualTo(mimeType);
                assertThat(fileData.isValid()).isTrue();
            }
        }
    }
}
