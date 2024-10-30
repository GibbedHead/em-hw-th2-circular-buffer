package ru.chaplyginma.circularbuffer;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A thread-safe circular buffer (ring buffer) that allows elements to be added
 * and removed in a first-in-first-out (FIFO) manner. The buffer can be configured
 * to either overwrite old elements when full or to block until there is space
 * available.
 *
 * @param <T> the type of elements held in this buffer
 */
public class CircularBuffer<T> {

    private final Object[] elements;
    private final int capacity;
    private boolean overwrite = false;

    private int readIndex = 0;
    private int writeIndex = 0;
    private int size = 0;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition isFull = lock.newCondition();
    private final Condition isEmpty = lock.newCondition();

    /**
     * Creates a CircularBuffer with the specified capacity.
     *
     * @param capacity the maximum number of elements the buffer can hold
     * @throws IllegalArgumentException if the capacity is less than 1
     */
    public CircularBuffer(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.capacity = capacity;
        elements = new Object[capacity];
    }

    /**
     * Creates a CircularBuffer with the specified capacity and overwrite policy.
     *
     * @param capacity the maximum number of elements the buffer can hold
     * @param overwrite whether to overwrite existing elements when the buffer is full
     * @throws IllegalArgumentException if the capacity is less than 1
     */
    public CircularBuffer(int capacity, boolean overwrite) {
        this(capacity);
        this.overwrite = overwrite;
    }

    /**
     * Returns the current number of elements in the buffer.
     *
     * @return the number of elements currently in the buffer
     */
    public int size() {
        lock.lock();
        try {
            return size;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Attempts to add an element to the buffer without blocking.
     * If the buffer is full and overwrite is false, the method will return false.
     *
     * @param element the element to be added
     * @return true if the element was added, false if the buffer was full
     * @throws NullPointerException if the element is null
     */
    public boolean offer(T element) {
        if (element == null) {
            throw new NullPointerException("Null elements are not permitted");
        }
        lock.lock();
        try {
            if (!overwrite && isFull()) {
                return false;
            }
            return addElement(element);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Adds an element to the buffer, blocking if necessary until space is available.
     *
     * @param element the element to be added
     * @throws InterruptedException if the current thread is interrupted while waiting
     * @throws NullPointerException if the element is null
     */
    public void put(T element) throws InterruptedException {
        if (element == null) {
            throw new NullPointerException("Null elements are not permitted");
        }
        lock.lockInterruptibly();
        try {
            while (!overwrite && isFull()) {
                isFull.await();
            }
            addElement(element);
        } finally {
            lock.unlock();
        }

    }

    /**
     * Retrieves and removes the head of the buffer, or returns null if the buffer is empty.
     *
     * @return the head of the buffer or null if it is empty
     */
    public T poll() {
        lock.lock();
        try {
            if (isEmpty()) {
                return null;
            }
            return getElement();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Retrieves and removes the head of the buffer, blocking if necessary until an element is available.
     *
     * @return the head of the buffer
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public T take() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (isEmpty()) {
                isEmpty.await();
            }
            return getElement();
        } finally {
            lock.unlock();
        }
    }

    private boolean isEmpty() {
        return size == 0;
    }

    private boolean isFull() {
        return size == capacity;
    }

    private boolean addElement(T element) {
        elements[writeIndex] = element;
        size = size == capacity ? size : size + 1;
        writeIndex = (writeIndex + 1) % capacity;
        if (isFull() && writeIndex >= readIndex) {
            readIndex = writeIndex;
        }
        isEmpty.signal();
        return true;
    }

    @SuppressWarnings("unchecked")
    private T getElement() {
        T element = (T) elements[readIndex];
        elements[readIndex] = null;
        size--;
        readIndex = (readIndex + 1) % capacity;
        isFull.signal();
        return element;
    }

    @Override
    public String toString() {
        return "CircularBuffer{" +
                "elements=" + Arrays.toString(elements) +
                ", readIndex=" + readIndex +
                ", writeIndex=" + writeIndex +
                ", size=" + size +
                '}';
    }
}
