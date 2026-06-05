package com.vivaldi_spring.HilosVirtuales;

import org.junit.jupiter.api.Test;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReentrantLockTests {

    private final ReentrantLock lock = new ReentrantLock();
    private int counter = 0;

    @Test
    void testReentrantLockWithVirtualThreads() throws InterruptedException {
        int numberOfThreads = 1000;
        int incrementsPerThread = 100;

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < numberOfThreads; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        lock.lock();
                        try {
                            counter++;
                        } finally {
                            lock.unlock();
                        }
                    }
                });
            }
        }

        assertEquals(numberOfThreads * incrementsPerThread, counter, "The final counter should be equal to the total number of increments");
    }

    @Test
    void testReentrancy() {
        lock.lock();
        try {
            // The same thread should be able to lock it again
            lock.lock();
            try {
                counter++;
            } finally {
                lock.unlock();
            }
        } finally {
            lock.unlock();
        }
        assertEquals(1, counter);
    }
}
