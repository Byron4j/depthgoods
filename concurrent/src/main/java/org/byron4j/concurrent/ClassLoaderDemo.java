package org.byron4j.concurrent;

public class ClassLoaderDemo {
    public static void main(String[] args) {
        String property = System.getProperty("sun.boot.class.path");
        for (String s : property.split(";")) {
            System.out.println(s);
        }

        //
        System.out.println("-------------------");
        String property2 = System.getProperty("java.ext.dirs");
        for (String s : property2.split(";")) {
            System.out.println(s);
        }
    }
}
