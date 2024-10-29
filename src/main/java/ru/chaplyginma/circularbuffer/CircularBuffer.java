package ru.chaplyginma.circularbuffer;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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

    public int size() {
        lock.lock();
        try {
            return size;
        } finally {
            lock.unlock();
        }
    }

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
