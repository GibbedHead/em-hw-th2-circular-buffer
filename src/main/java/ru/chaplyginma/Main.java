package ru.chaplyginma;

import ru.chaplyginma.circularbuffer.CircularBuffer;

public class Main {
    public static void main(String[] args) {
        CircularBuffer<Integer> buffer = new CircularBuffer<>(5);

        buffer.add(1);
        buffer.add(2);
        buffer.add(3);

        System.out.println(buffer.get());
        System.out.println(buffer.get());

    }
}