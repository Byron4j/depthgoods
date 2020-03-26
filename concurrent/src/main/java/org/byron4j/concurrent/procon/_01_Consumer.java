package org.byron4j.concurrent.procon;

import java.util.concurrent.BlockingQueue;

public class _01_Consumer implements Runnable {

    private BlockingQueue blockingQueue;

    public _01_Consumer(BlockingQueue blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        for(int i=1; i<=1000; i++){
            try {
                System.out.println("消费者[" + Thread.currentThread().getName()
                        + "]消费了:" + blockingQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
