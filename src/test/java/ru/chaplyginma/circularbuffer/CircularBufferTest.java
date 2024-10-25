package ru.chaplyginma.circularbuffer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CircularBufferTest {

    @Test
    @DisplayName("Creating new buffer with negative capacity throw exception")
    void givenNegativeCapacity_whenCreate_ThrowException() {
        assertThatThrownBy(() -> new CircularBuffer<String>(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Creating new buffer with zero capacity throw exception")
    void givenZeroCapacity_whenCreate_ThrowException() {
        assertThatThrownBy(() -> new CircularBuffer<String>(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("2 inserts and 3 reads. Should return 2 inserted values and null")
    void givenTwoInserts_when3Reads_thenReturn2AddedValuesAnd3rdIsNull() {
        CircularBuffer<String> buffer = new CircularBuffer<>(10);
        buffer.offer("String1");
        buffer.offer("String2");

        String string1 = buffer.take();
        String string2 = buffer.take();
        String string3 = buffer.take();

        assertThat(string1).isEqualTo("String1");
        assertThat(string2).isEqualTo("String2");
        assertThat(string3).isNull();
    }

    @Test
    @DisplayName("Adding 3rd value to buffer capacity 2 and no overwrite return false")
    void givenBufferCapacity2NoOverwrite_when3Inserts_then3rdInsertReturnFalse() {
        CircularBuffer<String> buffer = new CircularBuffer<>(2);

        buffer.offer("String1");
        buffer.offer("String2");

        boolean inserted = buffer.offer("String3");

        assertThat(inserted).isFalse();
    }

    @Test
    @DisplayName("New buffer has size 0")
    void givenNewBuffer_whenSize_thenReturnZero() {
        CircularBuffer<String> buffer = new CircularBuffer<>(2);

        assertThat(buffer.size()).isZero();
    }

    @Test
    @DisplayName("New buffer with 1 value added has size 1")
    void givenNewBufferAnd1Added_whenSize_thenReturnOne() {
        CircularBuffer<String> buffer = new CircularBuffer<>(2);
        buffer.offer("String1");

        assertThat(buffer.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Adding 2 and removing 2 then size is 0")
    void givenAdding2AndRemoving_whenSize_thenReturn0() {
        CircularBuffer<String> buffer = new CircularBuffer<>(2);
        buffer.offer("String1");
        buffer.offer("String2");

        buffer.take();
        buffer.take();

        assertThat(buffer.size()).isZero();
    }

    @Test
    @DisplayName("Adding max, removing 2, adding 2 then size is capacity")
    void givenAddingMaxRemoving2Adding2_whenSize_thenReturnCapacity() {
        int capacity = 10;

        CircularBuffer<Integer> buffer = new CircularBuffer<>(capacity);

        int i;
        for (i = 0; i < capacity; i++) {
            buffer.offer(i);
        }

        buffer.take();
        buffer.take();

        buffer.offer(i);
        buffer.offer(++i);

        assertThat(buffer.size()).isEqualTo(capacity);
    }

    @Test
    @DisplayName("When overwrite is on, adding after capacity is successful")
    void givenOverwriteAndMaxOffered_whenOffer_thenReturnTrue() {
        int capacity = 10;

        CircularBuffer<Integer> buffer = new CircularBuffer<>(capacity, true);

        int i;
        for (i = 0; i < capacity; i++) {
            buffer.offer(i);
        }

        boolean offerResult = buffer.offer(i);

        assertThat(offerResult).isTrue();
    }

    @Test
    @DisplayName("When overwrite is on, adding after capacity and size is capacity")
    void givenOverwriteAndMaxOfferedAnd1MoreOffered_whenSize_thenReturnCapacity() {
        int capacity = 10;

        CircularBuffer<Integer> buffer = new CircularBuffer<>(capacity, true);

        int i;
        for (i = 0; i < capacity; i++) {
            buffer.offer(i);
        }

        buffer.offer(i);
        buffer.offer(++i);

        assertThat(buffer.size()).isEqualTo(capacity);
    }

    @Test
    @DisplayName("When overwrite is on and capacity 10, adding 15, first read should be 5")
    void givenOverwriteAnd10CapacityAnd15Offered_whenRead_thenReturnOldest5() {
        CircularBuffer<Integer> buffer = new CircularBuffer<>(10, true);

        int i;
        for (i = 0; i < 15; i++) {
            buffer.offer(i);
        }

        Integer r = buffer.take();

        assertThat(r).isEqualTo(5);
    }
}