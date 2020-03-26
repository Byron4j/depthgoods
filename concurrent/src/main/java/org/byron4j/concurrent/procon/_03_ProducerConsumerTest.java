package org.byron4j.concurrent.procon;

import java.util.Random;

public class _03_ProducerConsumerTest {
    public static void main(String[] args)  {
        _03_MyBlockingQueue blockingQueue = new _03_MyBlockingQueue<>();
        new Thread(()->{
            while (true) {
                int nextInt = new Random().nextInt(1000);
                try {
                    blockingQueue.pro(nextInt);
                    System.out.println("生产：" + nextInt);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(()->{
            while (true) {
                try {
                    Object con = blockingQueue.con();
                    System.out.println("消费：" + con);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
