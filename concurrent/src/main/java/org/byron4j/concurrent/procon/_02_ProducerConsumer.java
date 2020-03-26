package org.byron4j.concurrent.procon;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 使用wait-notify实现生产者-消费者模式
 * 思路：
 * 1. 缓冲队列可以使用链表实现LinkedList
 * 2. 设置容量
 */
public class _02_ProducerConsumer<E> {
    private Queue<E> queue;
    private int capacity;

    public _02_ProducerConsumer() {
        this(16);
    }

    public _02_ProducerConsumer(int capacity) {
        queue = new LinkedList<>();
        this.capacity = capacity;
    }

    /**
     * 生产：
     *
     * @param e
     */
    public synchronized void pro(E e) throws InterruptedException {
        // 满了则等待
        while (queue.size() == capacity) {
            // 满了则等待；释放锁；等待其他线程notify、notifyAll才唤醒重新获得锁
            this.wait();
        }
        if( queue.size() == 0 ){
            // 唤醒其他线程，在当前线程释放锁之前其他线程只是就绪并不会立马执行
            this.notifyAll();
        }
        queue.add(e);
    }

    /**
     * 消费
     * @return
     * @throws InterruptedException
     */
    public synchronized E con() throws InterruptedException {
        // 空则等待
        while (queue.size() == 0) {
            this.wait();
        }
        if( queue.size() == capacity ){
            this.notifyAll();
        }
        return queue.remove();
    }


}
