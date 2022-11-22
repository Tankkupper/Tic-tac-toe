package com.test;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName() + "Hello");
                }
                System.out.println(Thread.currentThread().getName() + "END");
            }
        });
        a.start();

        Thread.sleep(5000);
        a.interrupt();
    }
}
