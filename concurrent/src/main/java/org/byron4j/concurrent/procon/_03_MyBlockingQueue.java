package org.byron4j.concurrent.procon;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自定义实现BlockingQueue（使用ReentrantLock+Condition通知机制）
 * 一把锁、2个信号通知（condition：notEmpty、notFull）
 *  1. 提供插入、取数据的2个方法
 *  2. 插入方法：
 *      2.1 进来加锁lock
 *      2.2 一个condition等待：
 *          queue满了则notFull等待
 *      2.3 另一个condition发送信号：
 *          queue为空notEmpty发送信号
 *      2.4 放入数据
 *      2.3 最后finally块里面释放锁
 */
public class _03_MyBlockingQueue<E> {
    private Queue<E> queue;
    private int capacity;

    final ReentrantLock lock = new ReentrantLock();
    final Condition notEmpty = lock.newCondition();
    final Condition notFull = lock.newCondition();

    public _03_MyBlockingQueue() {
        this(16);
    }

    public _03_MyBlockingQueue(int capacity) {
        this.queue = new LinkedList();
        this.capacity = capacity;
    }


    // 生产
    public void pro(E e) throws InterruptedException {
        // 可中断锁
        lock.lockInterruptibly();

        try {
            if (queue.size() == capacity) {
                // 锁会自动释放，线程暂停直到该条件（notFull）发出signal、signalAll
                notFull.await();
            }
            if (queue.size() == 0) {
                notEmpty.signal();
            }
            queue.add(e);
        } finally {
            lock.unlock();
        }
    }

    // 消费
    public E con() throws InterruptedException {
        lock.lockInterruptibly();

        try {
            if (queue.size() == 0) {
                notEmpty.await();
            }

            if (queue.size() == capacity) {
                notFull.signal();
            }
            return queue.remove();
        } finally {
            lock.unlock();
        }
    }
}
