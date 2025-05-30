package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.locks.ReentrantLock;

@AllArgsConstructor
@Getter
public class Account {
    private long money;
    private final String accNumber;
    private final ReentrantLock lock;
    private boolean isBlocked;
    

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void block() {
        isBlocked = true;
    }
}