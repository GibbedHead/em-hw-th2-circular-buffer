package ru.chaplyginma.circularbuffer;

public class CircularBuffer<T> {

    private final Object[] elements;
    private final int capacity;
    private int read = -1;
    private int write = 0;

    public CircularBuffer(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.capacity = capacity;
        elements = new Object[capacity];

    }

    public void add(T element) {
        elements[write++ % capacity] = element;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) elements[++read];
    }
}
