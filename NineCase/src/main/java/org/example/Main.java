package org.example;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Synchronizer synchronizer = new Synchronizer();
        // Создаем несколько потоков, которые будут ждать
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " is waiting");
                    synchronizer.blockUntilDone();
                    System.out.println(Thread.currentThread().getName() + " is released");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }



        // Ждем 2 секунды перед вызовом doOnce()
        Thread.sleep(2000);
        System.out.println("Calling doOnce()");
        synchronizer.doOnce();
    }
}