package de.schmiereck.smkEasyNN.genEden.service;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HexGridFillPopParallelProcessor {
    private static final int NUM_THREADS = Math.min(1, Runtime.getRuntime().availableProcessors() - 2);

    public void fillPopHexGrid(final GeneticPartService geneticPartService, final List<Part> partList, final int partCount) {
        final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        final int partSegmentSize = partCount / NUM_THREADS;

        runWorker(executor, geneticPartService, partSegmentSize, partList, partCount, GeneticPartService::fillPartPopulation);

        executor.shutdown();
    }

    private static void runWorker(final ExecutorService executor, final GeneticPartService geneticPartService, final int partSegmentSize, final List<Part> partList, final int partCount, final HexGridFillPopWorker.WorkInterface workInterface) {
        final CountDownLatch latch = new CountDownLatch(NUM_THREADS);

        for (int threadPos = 0; threadPos < NUM_THREADS; threadPos++) {
            final int startCountPos = threadPos * partSegmentSize;
            final int endCountPos;
            if (threadPos == NUM_THREADS - 1) {
                // Make sure the last segment goes up to the xEndGridPos of the grid
                endCountPos = partCount - 1;
            } else {
                endCountPos = (threadPos + 1) * partSegmentSize;
            }

            final Runnable worker = new HexGridFillPopWorker(workInterface, geneticPartService, partList, startCountPos, endCountPos);

            executor.submit(() -> {
                try {
                    worker.run();
                } finally {
                    latch.countDown();
                }
            });
        }

        //executor.shutdown();

//        try {
//            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//        } catch (final InterruptedException e) {
//            // Handle exception
//        }
        try {
            latch.await();
        } catch (final InterruptedException e) {
            // Handle exception
        }
    }
}
