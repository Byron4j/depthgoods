package org.byron4j.concurrent.procon;

import java.util.concurrent.BlockingQueue;

/**
 * 生产者-消费者实现模式一——使用内置的阻塞队列
 * BlockingQueue的put、take天然支持阻塞等待、线程安全
 */
public class _01_Producer implements Runnable{

    private BlockingQueue<Object> blockingQueue;

    public _01_Producer(BlockingQueue<Object> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        for(int i=1; i<=1000; i++){
            try {
                blockingQueue.put(Thread.currentThread().getName()+"-" + i);
                System.out.println("生产者线程[" + Thread.currentThread().getName()
                        + "]生产了" + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
