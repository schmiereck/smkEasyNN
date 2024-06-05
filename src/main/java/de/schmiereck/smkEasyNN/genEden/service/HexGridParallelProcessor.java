package de.schmiereck.smkEasyNN.genEden.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HexGridParallelProcessor {
    private static final int NUM_THREADS = Math.min(1, Runtime.getRuntime().availableProcessors() - 2);

    public void processHexGrid(final HexGridService hexGridService) {
        final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        final int xGridSize = hexGridService.getXGridSize();
        final int yGridSize = hexGridService.getYGridSize();
        final int xSegmentSize = xGridSize / NUM_THREADS;

        runWorker(executor, hexGridService, xSegmentSize, xGridSize, yGridSize, HexGridService::calcGridFieldOutToIn);
        runWorker(executor, hexGridService, xSegmentSize, xGridSize, yGridSize, HexGridService::calcGridFieldInToOut);
        runWorker(executor, hexGridService, xSegmentSize, xGridSize, yGridSize, HexGridService::calcGridPartOutIn);
        runWorker(executor, hexGridService, xSegmentSize, xGridSize, yGridSize, HexGridService::calcGridOut);

        executor.shutdown();
    }

    private static void runWorker(final ExecutorService executor, final HexGridService hexGridService, final int xSegmentSize, final int xGridSize, final int yGridSize, final HexGridWorker.WorkInterface workInterface) {
        final CountDownLatch latch = new CountDownLatch(NUM_THREADS);

        for (int threadPos = 0; threadPos < NUM_THREADS; threadPos++) {
            final int xStartGridPos = threadPos * xSegmentSize;
            final int yStartGridPos = 0;
            final int xEndGridPos;
            if (threadPos == NUM_THREADS - 1) {
                // Make sure the last segment goes up to the xEndGridPos of the grid
                xEndGridPos = xGridSize - 1;
            } else {
                xEndGridPos = (threadPos + 1) * xSegmentSize;
            }
            final int yEndGridPos = yGridSize - 1;

            final Runnable worker = new HexGridWorker(workInterface, hexGridService, xStartGridPos, yStartGridPos, xEndGridPos, yEndGridPos);

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
