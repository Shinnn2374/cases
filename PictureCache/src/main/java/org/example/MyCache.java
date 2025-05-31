package org.example;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyCache {
    private final ConcurrentHashMap<Integer, byte[]> memoryCache = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock diskLock = new ReentrantReadWriteLock();
    private final AtomicInteger currentId = new AtomicInteger(0);
    private final AtomicLong currentMemoryUsage = new AtomicLong(0);
    private final long memoryLimit = 100 * 1024 * 1024; // 100 MB лимит памяти

    public byte[] loadFromFile(String filename) {
        // Реализация загрузки из файла (предоставлена)
        return null;
    }

    public void saveToFile(String filename, byte[] data) {
        // Реализация сохранения в файл (предоставлена)
        // Работает 1000 мс
    }

    public int putToCache(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }

        int id = currentId.incrementAndGet();
        long dataSize = data.length;

        // Проверяем, помещаются ли данные в память
        if (currentMemoryUsage.get() + dataSize <= memoryLimit) {
            // Пытаемся добавить в память
            if (memoryCache.putIfAbsent(id, data) == null) {
                currentMemoryUsage.addAndGet(dataSize);
                return id;
            }
        }

        // Если не поместилось в память, сохраняем на диск
        diskLock.writeLock().lock();
        try {
            saveToFile("img_" + id, data);
        } finally {
            diskLock.writeLock().unlock();
        }

        return id;
    }

    public byte[] getFromCache(int id) {
        // Сначала проверяем в памяти
        byte[] data = memoryCache.get(id);
        if (data != null) {
            return data;
        }

        // Если нет в памяти, пробуем загрузить с диска
        diskLock.readLock().lock();
        try {
            return loadFromFile("img_" + id);
        } finally {
            diskLock.readLock().unlock();
        }
    }
}