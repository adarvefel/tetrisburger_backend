package com.tetris.tetrisburger_backend.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ImageStatus Tests")
class ImageStatusTest {

    @Nested
    @DisplayName("Enum Values")
    class EnumValuesTests {

        @Test
        @DisplayName("should have NONE value")
        void shouldHaveNoneValue() {
            // Then
            assertThat(ImageStatus.NONE).isNotNull();
            assertThat(ImageStatus.NONE.name()).isEqualTo("NONE");
        }

        @Test
        @DisplayName("should have PENDING value")
        void shouldHavePendingValue() {
            // Then
            assertThat(ImageStatus.PENDING).isNotNull();
            assertThat(ImageStatus.PENDING.name()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("should have UPLOADED value")
        void shouldHaveUploadedValue() {
            // Then
            assertThat(ImageStatus.UPLOADED).isNotNull();
            assertThat(ImageStatus.UPLOADED.name()).isEqualTo("UPLOADED");
        }

        @Test
        @DisplayName("should have FAILED value")
        void shouldHaveFailedValue() {
            // Then
            assertThat(ImageStatus.FAILED).isNotNull();
            assertThat(ImageStatus.FAILED.name()).isEqualTo("FAILED");
        }

        @Test
        @DisplayName("should have exactly 4 values")
        void shouldHaveExactlyFourValues() {
            // When
            ImageStatus[] values = ImageStatus.values();

            // Then
            assertThat(values).hasSize(4);
            assertThat(values).containsExactly(
                    ImageStatus.NONE,
                    ImageStatus.PENDING,
                    ImageStatus.UPLOADED,
                    ImageStatus.FAILED
            );
        }

        @Test
        @DisplayName("should convert string to enum using valueOf")
        void shouldConvertStringToEnumUsingValueOf() {
            // When & Then
            assertThat(ImageStatus.valueOf("NONE")).isEqualTo(ImageStatus.NONE);
            assertThat(ImageStatus.valueOf("PENDING")).isEqualTo(ImageStatus.PENDING);
            assertThat(ImageStatus.valueOf("UPLOADED")).isEqualTo(ImageStatus.UPLOADED);
            assertThat(ImageStatus.valueOf("FAILED")).isEqualTo(ImageStatus.FAILED);
        }

        @Test
        @DisplayName("should throw exception when converting invalid string")
        void shouldThrowExceptionWhenConvertingInvalidString() {
            // Then
            assertThatThrownBy(() ->
                    ImageStatus.valueOf("INVALID")
            ).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("should maintain declaration order")
        void shouldMaintainDeclarationOrder() {
            // When
            ImageStatus[] values = ImageStatus.values();

            // Then
            assertThat(values[0]).isEqualTo(ImageStatus.NONE);
            assertThat(values[1]).isEqualTo(ImageStatus.PENDING);
            assertThat(values[2]).isEqualTo(ImageStatus.UPLOADED);
            assertThat(values[3]).isEqualTo(ImageStatus.FAILED);
        }

        @Test
        @DisplayName("should have correct ordinal values")
        void shouldHaveCorrectOrdinalValues() {
            // Then
            assertThat(ImageStatus.NONE.ordinal()).isEqualTo(0);
            assertThat(ImageStatus.PENDING.ordinal()).isEqualTo(1);
            assertThat(ImageStatus.UPLOADED.ordinal()).isEqualTo(2);
            assertThat(ImageStatus.FAILED.ordinal()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("Comparisons")
    class ComparisonsTests {

        @Test
        @DisplayName("should compare statuses using equals")
        void shouldCompareStatusesUsingEquals() {
            // Given
            ImageStatus status1 = ImageStatus.UPLOADED;
            ImageStatus status2 = ImageStatus.UPLOADED;
            ImageStatus status3 = ImageStatus.PENDING;

            // Then
            assertThat(status1).isEqualTo(status2);
            assertThat(status1).isNotEqualTo(status3);
        }

        @Test
        @DisplayName("should compare statuses using equality operator")
        void shouldCompareStatusesUsingEqualityOperator() {
            // Given
            ImageStatus status1 = ImageStatus.NONE;
            ImageStatus status2 = ImageStatus.NONE;

            // Then
            assertThat(status1 == status2).isTrue();
        }
    }
}
