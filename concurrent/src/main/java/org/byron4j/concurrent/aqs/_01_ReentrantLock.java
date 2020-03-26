package org.byron4j.concurrent.aqs;

import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入锁ReentrantLock
 */
public class _01_ReentrantLock {

    private final ReentrantLock lock = new ReentrantLock();

    private void printHello(){
        try {
            lock.lock();
            System.out.println(LocalTime.now() + "--hello..." + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            lock.unlock();
        }


    }

    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(10);
        _01_ReentrantLock reentrantLock = new _01_ReentrantLock();
        int i = 10;
        while(i-- > 0){
            es.execute(()->{
                reentrantLock.printHello();
            });
        }
        es.shutdown();
    }


}
