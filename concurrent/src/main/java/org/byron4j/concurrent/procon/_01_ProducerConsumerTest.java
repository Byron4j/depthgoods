package org.byron4j.concurrent.procon;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class _01_ProducerConsumerTest {
    public static void main(String[] args) {
        BlockingQueue blockingQueue = new ArrayBlockingQueue<>(100);
        new Thread(new _01_Producer(blockingQueue)).start();
        new Thread(new _01_Consumer(blockingQueue)).start();
        new Thread(new _01_Producer(blockingQueue)).start();
        new Thread(new _01_Producer(blockingQueue)).start();
        new Thread(new _01_Consumer(blockingQueue)).start();
        new Thread(new _01_Producer(blockingQueue)).start();
    }
}
