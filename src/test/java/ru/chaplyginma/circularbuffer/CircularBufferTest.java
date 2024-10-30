package ru.chaplyginma.circularbuffer;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CircularBufferTest {

    @Test
    @DisplayName("Single thread: Creating new buffer with negative capacity throw exception")
    void givenNegativeCapacity_whenCreate_ThrowException() {
        assertThatThrownBy(() -> new CircularBuffer<String>(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Single thread: Creating new buffer with zero capacity throw exception")
    void givenZeroCapacity_whenCreate_ThrowException() {
        assertThatThrownBy(() -> new CircularBuffer<String>(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Single thread: Adding null element should throw NullPointerException")
    void givenNullElement_whenOffer_thenThrowNullPointerException() {
        CircularBuffer<String> buffer = new CircularBuffer<>(10);

        assertThatThrownBy(() -> buffer.offer(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Null elements are not permitted");
    }


    @Test
    @DisplayName("Single thread: 2 inserts and 3 reads. Should return 2 inserted values and null")
    void givenTwoInserts_when3Reads_thenReturn2AddedValuesAnd3rdIsNull() {
        CircularBuffer<String> buffer = new CircularBuffer<>(10);
        buffer.offer("String1");
        buffer.offer("String2");

        String string1 = buffer.poll();
        String string2 = buffer.poll();
        String string3 = buffer.poll();

        assertThat(string1).isEqualTo("String1");
        assertThat(string2).isEqualTo("String2");
        assertThat(string3).isNull();
    }

    @Test
    @DisplayName("Single thread: Adding 3rd value to buffer capacity 2 and no overwrite return false")
    void givenBufferCapacity2NoOverwrite_when3Inserts_then3rdInsertReturnFalse() {
        CircularBuffer<String> buffer = new CircularBuffer<>(2);

        buffer.offer("String1");
        buffer.offer("String2");

        boolean inserted = buffer.offer("String3");

        assertThat(inserted).isFalse();
    }

    @Test
    @DisplayName("Single thread: New buffer has size 0")
    void givenNewBuffer_whenSize_thenReturnZero() {
        CircularBuffer<String> buffer = new CircularBuffer<>(2);

        assertThat(buffer.size()).isZero();
    }

    @Test
    @DisplayName("Single thread: New buffer with 1 value added has size 1")
    void givenNewBufferAnd1Added_whenSize_thenReturnOne() {
        CircularBuffer<String> buffer = new CircularBuffer<>(2);
        buffer.offer("String1");

        assertThat(buffer.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Single thread: Adding 2 and removing 2 then size is 0")
    void givenAdding2AndRemoving_whenSize_thenReturn0() {
        CircularBuffer<String> buffer = new CircularBuffer<>(2);
        buffer.offer("String1");
        buffer.offer("String2");

        buffer.poll();
        buffer.poll();

        assertThat(buffer.size()).isZero();
    }

    @Test
    @DisplayName("Single thread: Adding max, removing 2, adding 2 then size is capacity")
    void givenAddingMaxRemoving2Adding2_whenSize_thenReturnCapacity() {
        int capacity = 10;

        CircularBuffer<Integer> buffer = new CircularBuffer<>(capacity);

        int i;
        for (i = 0; i < capacity; i++) {
            buffer.offer(i);
        }

        buffer.poll();
        buffer.poll();

        buffer.offer(i);
        buffer.offer(++i);

        assertThat(buffer.size()).isEqualTo(capacity);
    }

    @Test
    @DisplayName("Single thread: When overwrite is on, adding after capacity is successful")
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
    @DisplayName("Single thread: When overwrite is on, adding after capacity and size is capacity")
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
    @DisplayName("Single thread: When overwrite is on and capacity 10, adding 15, first read should be 5")
    void givenOverwriteAnd10CapacityAnd15Offered_whenRead_thenReturnOldest5() {
        CircularBuffer<Integer> buffer = new CircularBuffer<>(10, true);

        int i;
        for (i = 0; i < 15; i++) {
            buffer.offer(i);
        }

        Integer r = buffer.poll();

        assertThat(r).isEqualTo(5);
    }

    @Test
    @DisplayName("Multi thread: empty buffer poll")
    void givenEmptyBuffer_whenPoll_thenReturnFalse() {
        CircularBuffer<Integer> buffer = new CircularBuffer<>(2);

        Integer r = buffer.poll();

        assertThat(r).isNull();
    }

    @Test
    @DisplayName("Multi thread: empty buffer take wait for buffer fill")
    void givenEmptyBuffer_whenTake_thenThreadWait() throws InterruptedException {
        CircularBuffer<Integer> buffer = new CircularBuffer<>(1);

        Thread consumerThread = new Thread(() -> {
            try {
                buffer.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        consumerThread.start();

        Awaitility.await()
                .timeout(ofSeconds(5))
                .pollInterval(ofMillis(100))
                .until(() -> consumerThread.getState() == Thread.State.WAITING);

        assertThat(consumerThread.getState())
                .withFailMessage("Consumer thread should be waiting on empty buffer")
                .isEqualTo(Thread.State.WAITING);

        consumerThread.interrupt();
        consumerThread.join();
    }

    @Test
    @DisplayName("Multi thread: full buffer put wait for buffer empty")
    void givenFullBuffer_whenPut_thenThreadWait() throws InterruptedException {
        CircularBuffer<Integer> buffer = new CircularBuffer<>(1);

        buffer.put(1);

        Thread producerThread = new Thread(() -> {
            try {
                buffer.put(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producerThread.start();

        Awaitility.await()
                .timeout(ofSeconds(5))
                .pollInterval(ofMillis(100))
                .until(() -> producerThread.getState() == Thread.State.WAITING);

        assertThat(producerThread.getState())
                .withFailMessage("Producer thread should be waiting on full buffer")
                .isEqualTo(Thread.State.WAITING);

        producerThread.interrupt();
        producerThread.join();
    }

    @Test
    @DisplayName("Multi thread: Adding/removing same amount of times should return size 0")
    void givenAddingAndRemovingSameTimes_whenSize_thenReturnZero() {
        int capacity = 10;
        int iterations = 100_000;
        int threads = 5;

        CircularBuffer<String> buffer = new CircularBuffer<>(capacity);

        CountDownLatch countDownLatch = new CountDownLatch(2 * iterations);

        Runnable provider = () -> {
            try {
                buffer.put(Thread.currentThread().getName());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                countDownLatch.countDown();
            }

        };
        Runnable consumer = () -> {
            try {
                buffer.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                countDownLatch.countDown();
            }
        };

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            for (int i = 0; i < iterations; i++) {
                pool.submit(provider);
                pool.submit(consumer);
            }
            pool.shutdown();
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.printf("Latch count: %,d%n", countDownLatch.getCount());
        System.out.println(buffer);

        assertThat(buffer.size()).isZero();
    }

    @Test
    @DisplayName("Multi thread: When overwrite is on, oldest element should be overwritten")
    void givenOverwriteOn_whenAddingInMultipleThreads_thenOldestElementShouldBeOverwritten() {
        int capacity = 10;
        int elementsPut = 15;
        CircularBuffer<Integer> buffer = new CircularBuffer<>(capacity, true);

        List<Integer> oldestElements = new ArrayList<>();

        Runnable producerThread = () -> {
            for (int i = 0; i < elementsPut; i++) {
                try {
                    buffer.put(i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        Runnable consumerThread = () -> {
            for (int i = 0; i < 5; i++) {
                try {
                    oldestElements.add(buffer.take());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            pool.submit(producerThread);
            pool.submit(consumerThread);

            pool.shutdown();

            Awaitility.await()
                    .timeout(ofSeconds(5))
                    .pollInterval(ofMillis(100))
                    .until(pool::isTerminated);
        } finally {
            pool.shutdownNow();
        }

        List<Integer> expectedValues = List.of(5, 6, 7, 8, 9);

        assertThat(oldestElements).containsExactlyInAnyOrder(expectedValues.toArray(Integer[]::new));
    }

}