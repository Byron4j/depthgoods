package org.byron4j.concurrent.jvm;

public class JavaThreadStack {

    private int cal(){
        int a = 10;
        int b = 20;
        int c = (a+b) * 10;
        return c;
    }

    public static void main(String[] args) {
        JavaThreadStack jts = new JavaThreadStack();
        jts.cal();
        System.out.println("Bye.");
    }
}
