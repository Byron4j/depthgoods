package org.byron4j.concurrent.procon;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class _02_ProducerConsumerTest {
    public static void main(String[] args) {

        _02_ProducerConsumer<Integer> producerConsumer = new _02_ProducerConsumer();

        ExecutorService es = Executors.newFixedThreadPool(2);


        es.execute(()->{
            while (true) {
                try {
                    int nextInt = new Random().nextInt(1000);
                    System.out.println("生产者[" + Thread.currentThread().getName()
                            + "]生产：" + nextInt);
                    producerConsumer.pro(nextInt);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        es.execute(()->{
            while (true) {
                try {
                    Integer integer = producerConsumer.con();
                    System.out.println("消费者["+Thread.currentThread().getName()
                            +"]获得：" + integer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
