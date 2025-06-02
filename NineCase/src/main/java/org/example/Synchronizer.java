package org.example;

public class Synchronizer {
    private boolean isDone = false;
    private final Object lock = new Object();

    public void blockUntilDone() throws InterruptedException {
        synchronized (lock) {
            while (!isDone) {
                lock.wait();
            }
        }
    }
    public void doOnce() {
        synchronized (lock) {
            isDone = true;
            lock.notifyAll();
        }
    }
}