package ru.chaplyginma;

import ru.chaplyginma.circularbuffer.CircularBuffer;

public class Main {
    public static void main(String[] args) {
        CircularBuffer<Integer> buffer = new CircularBuffer<>(10, true);

        buffer.offer(1);
        System.out.println(buffer);
        buffer.offer(2);
        System.out.println(buffer);
        buffer.offer(3);
        System.out.println(buffer);
        buffer.offer(4);
        System.out.println(buffer);
        buffer.offer(5);
        System.out.println(buffer);
        buffer.offer(6);
        System.out.println(buffer);
        buffer.offer(7);
        System.out.println(buffer);
        System.out.println("Size: " + buffer.size());
        System.out.println(buffer.take());
        System.out.println(buffer.take());
        System.out.println(buffer.take());
        System.out.println(buffer.take());
        System.out.println(buffer.take());
        System.out.println(buffer.take());
        System.out.println(buffer.take());
        System.out.println(buffer.take());
        System.out.println(buffer.take());
        System.out.println(buffer);
        System.out.println("Size: " + buffer.size());
        buffer.offer(8);

        System.out.println(buffer);
        System.out.println("Size: " + buffer.size());
        System.out.println(buffer.take());
        System.out.println(buffer);
        System.out.println(buffer.take());
        System.out.println(buffer);
        buffer.offer(9);
        buffer.offer(10);
        buffer.offer(11);

        System.out.println(buffer);
        System.out.println("Size: " + buffer.size());
        System.out.println(buffer.take());
        System.out.println(buffer);
        System.out.println(buffer.take());
        System.out.println(buffer);
        System.out.println(buffer.take());
        System.out.println(buffer);
        System.out.println("Size: " + buffer.size());
        System.out.println(buffer.take());
        System.out.println(buffer);
        buffer.offer(12);
        buffer.offer(13);

        System.out.println(buffer);
        System.out.println("Size: " + buffer.size());
        System.out.println(buffer.take());
        System.out.println(buffer);
        System.out.println(buffer.take());
        System.out.println(buffer);
        buffer.offer(14);
        buffer.offer(15);
        buffer.offer(16);
        buffer.offer(17);

        buffer.offer(18);
        buffer.offer(19);
        System.out.println(buffer);
        System.out.println("Size: " + buffer.size());
        buffer.offer(20);
        buffer.offer(21);
        buffer.offer(22);
        buffer.offer(23);
        buffer.offer(24);
        buffer.offer(25);
        buffer.offer(26);
        buffer.offer(27);
        buffer.offer(28);

        System.out.println(buffer);
        System.out.println("Size: " + buffer.size());
        buffer.offer(29);
        System.out.println(buffer);
        System.out.println("Size: " + buffer.size());
        buffer.offer(30);
        buffer.offer(31);
        System.out.println(buffer);
        System.out.println("Size: " + buffer.size());
        System.out.println(buffer.take());
        System.out.println(buffer.take());
        System.out.println(buffer.take());
        System.out.println(buffer.take());
        System.out.println(buffer.take());
        System.out.println(buffer.take());
        System.out.println(buffer.take());
        System.out.println(buffer);
        System.out.println(buffer.take());
        System.out.println(buffer.take());
        System.out.println(buffer.take());
        System.out.println(buffer.take());
        buffer.offer(32);
        System.out.println(buffer);
        System.out.println("Size: " + buffer.size());
        System.out.println(buffer.take());
        buffer.offer(33);
        System.out.println(buffer);
        System.out.println("Size: " + buffer.size());
        System.out.println(buffer.take());
        System.out.println(buffer.take());
    }
}