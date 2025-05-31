package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class LargeFileSorter {
    private static final int MAX_MEMORY_SIZE = 100 * 1024 * 1024; // 100 MB (пример)
    private static final int THREAD_POOL_SIZE = 4;

    public static void sortLargeFile(String inputFile, String outputFile) throws IOException {
        // Этап 1: Разделение и сортировка частей
        List<String> sortedChunks = splitAndSort(inputFile);

        // Этап 2: Многофазное слияние
        mergeFiles(sortedChunks, outputFile);

        // Удаление временных файлов
        cleanup(sortedChunks);
    }

    private static List<String> splitAndSort(String inputFile) throws IOException {
        List<String> tempFiles = new ArrayList<>();
        BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile));
        List<Integer> chunk = new ArrayList<>(MAX_MEMORY_SIZE / 4); // Примерный размер

        String line;
        while ((line = reader.readLine()) != null) {
            chunk.add(Integer.parseInt(line));

            // Если достигли лимита памяти - сортируем и сохраняем
            if (chunk.size() * 4 >= MAX_MEMORY_SIZE) {
                tempFiles.add(sortAndSave(chunk));
                chunk.clear();
            }
        }

        // Остаток данных
        if (!chunk.isEmpty()) {
            tempFiles.add(sortAndSave(chunk));
        }

        reader.close();
        return tempFiles;
    }

    private static String sortAndSave(List<Integer> chunk) throws IOException {
        // Сортировка в памяти
        Collections.sort(chunk);

        // Сохранение во временный файл
        String tempFileName = "temp_" + UUID.randomUUID() + ".tmp";
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(tempFileName))) {
            for (int num : chunk) {
                writer.write(Integer.toString(num));
                writer.newLine();
            }
        }

        return tempFileName;
    }

    private static void mergeFiles(List<String> files, String outputFile) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Future<String>> futures = new ArrayList<>();

        // Первоначальное слияние пар файлов
        List<String> currentFiles = new ArrayList<>(files);

        while (currentFiles.size() > 1) {
            List<String> nextFiles = new ArrayList<>();

            // Сливаем файлы парами
            for (int i = 0; i < currentFiles.size(); i += 2) {
                if (i + 1 < currentFiles.size()) {
                    String file1 = currentFiles.get(i);
                    String file2 = currentFiles.get(i + 1);
                    String mergedFile = "merge_" + UUID.randomUUID() + ".tmp";

                    Future<String> future = executor.submit(() -> mergeTwoFiles(file1, file2, mergedFile));
                    futures.add(future);
                    nextFiles.add(mergedFile);
                } else {
                    // Нечетное количество файлов - последний просто переносим
                    nextFiles.add(currentFiles.get(i));
                }
            }

            // Ждем завершения всех задач
            for (Future<String> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new IOException("Merge failed", e);
                }
            }
            futures.clear();

            currentFiles = nextFiles;
        }

        executor.shutdown();

        // Переименовываем последний временный файл в итоговый
        if (!currentFiles.isEmpty()) {
            Files.move(Paths.get(currentFiles.get(0)), Paths.get(outputFile), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String mergeTwoFiles(String file1, String file2, String outputFile) throws IOException {
        try (BufferedReader reader1 = Files.newBufferedReader(Paths.get(file1));
             BufferedReader reader2 = Files.newBufferedReader(Paths.get(file2));
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile))) {

            String line1 = reader1.readLine();
            String line2 = reader2.readLine();

            while (line1 != null || line2 != null) {
                int value1 = line1 != null ? Integer.parseInt(line1) : Integer.MAX_VALUE;
                int value2 = line2 != null ? Integer.parseInt(line2) : Integer.MAX_VALUE;

                if (value1 <= value2) {
                    writer.write(line1);
                    writer.newLine();
                    line1 = reader1.readLine();
                } else {
                    writer.write(line2);
                    writer.newLine();
                    line2 = reader2.readLine();
                }
            }
        }

        // Удаляем исходные файлы после слияния
        Files.deleteIfExists(Paths.get(file1));
        Files.deleteIfExists(Paths.get(file2));

        return outputFile;
    }

    private static void cleanup(List<String> tempFiles) throws IOException {
        for (String file : tempFiles) {
            Files.deleteIfExists(Paths.get(file));
        }
    }
}