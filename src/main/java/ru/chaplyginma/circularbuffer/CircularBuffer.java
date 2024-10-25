package ru.chaplyginma.circularbuffer;

import java.util.Arrays;

public class CircularBuffer<T> {

    private final Object[] elements;
    private final int capacity;
    private boolean overwrite = false;

    private int read = 0;
    private int write = 0;
    private boolean full = false;
    private boolean isReadCircleAhead = false;


    public CircularBuffer(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.capacity = capacity;
        elements = new Object[capacity];
    }

    public CircularBuffer(int capacity, boolean overwrite) {
        this(capacity);
        this.overwrite = overwrite;
    }

    public boolean add(T element) {
        if (!overwrite && isFull()) {
            return false;
        }
        elements[write] = element;
        write = (write + 1) % capacity;
        if (isReadCircleAhead) {
            read = write;
        }
        if (write == read) {
            full = true;
            isReadCircleAhead = true;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        if (isEmpty()) {
            return null;
        }
        T element = (T) elements[read];
        elements[read] = null;
        read = (read + 1) % capacity;
        if (read == write) {
            full = false;
        }
        if (read > write) {
            isReadCircleAhead = false;
        }
        return element;
    }

    private boolean isEmpty() {
        return read == write && !full;
    }

    private boolean isFull() {
        return read == write && full;
    }

    @Override
    public String toString() {
        return "CircularBuffer{" +
                "elements=" + Arrays.toString(elements) +
                ", read=" + read +
                ", write=" + write +
                '}';
    }
}
