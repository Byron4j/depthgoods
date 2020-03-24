package org.byron4j.concurrent.magic;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 操作同步
 */
public class UnsafeOptSynchronization {
    private static Unsafe unsafe;

    static {
        try {
            Class clazz = Unsafe.class;
            Field field = clazz.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe)field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sayHello(){
        unsafe.tryMonitorEnter(UnsafeOptSynchronization.class);
        unsafe.monitorEnter(UnsafeOptSynchronization.class);

        System.out.println("hello..." + Thread.currentThread().getName());

    }

    public static void main(String[] args) throws InterruptedException {
        MyThread myThread = new UnsafeOptSynchronization.MyThread();
        MyThread myThread2 = new UnsafeOptSynchronization.MyThread();

        myThread.start();
        myThread.join();
        myThread2.start();
    }

    static class MyThread extends Thread{
        @Override
        public void run() {
            sayHello();
        }
    }

}
