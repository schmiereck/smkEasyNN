package de.schmiereck.smkEasyNN.genEden.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HexGridCalcParallelProcessor {
    private static final int NUM_THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() - 2);

    public void processHexGrid(final HexGridService hexGridService) {
        final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        final int xGridSize = hexGridService.getXGridSize();
        final int yGridSize = hexGridService.getYGridSize();
        final int ySegmentSize = yGridSize / NUM_THREADS;

        runWorker(executor, hexGridService, ySegmentSize, xGridSize, yGridSize, HexGridService::calcGridPartNetInput);
        //hexGridService.calcGridPartNetInput(0, 0, xGridSize - 1, yGridSize - 1);

        runWorker(executor, hexGridService, ySegmentSize, xGridSize, yGridSize, HexGridService::calcGridFieldOutToIn);
        //hexGridService.calcGridFieldOutToIn(0, 0, xGridSize - 1, yGridSize - 1);

        runWorker(executor, hexGridService, ySegmentSize, xGridSize, yGridSize, HexGridService::calcGridFieldInToOut);
        //hexGridService.calcGridFieldInToOut(0, 0, xGridSize - 1, yGridSize - 1);

        runWorker(executor, hexGridService, ySegmentSize, xGridSize, yGridSize, HexGridService::calcGridPartOutIn);
        //hexGridService.calcGridPartOutIn(0, 0, xGridSize - 1, yGridSize - 1);

        runWorker(executor, hexGridService, ySegmentSize, xGridSize, yGridSize, HexGridService::calcGridOut);
        //hexGridService.calcGridOut(0, 0, xGridSize - 1, yGridSize - 1);

        executor.shutdown();
    }

    private static void runWorker(final ExecutorService executor, final HexGridService hexGridService, final int ySegmentSize, final int xGridSize, final int yGridSize, final HexGridCalcWorker.WorkInterface workInterface) {
        final CountDownLatch latch = new CountDownLatch(NUM_THREADS);

        for (int threadPos = 0; threadPos < NUM_THREADS; threadPos++) {
            final int xStartGridPos = 0;
            final int yStartGridPos = threadPos * ySegmentSize;
            final int xEndGridPos = xGridSize - 1;
            final int yEndGridPos;
            if (threadPos == NUM_THREADS - 1) {
                // Make sure the last segment goes up to the xEndGridPos of the grid
                yEndGridPos = yGridSize - 1;
            } else {
                yEndGridPos = ((threadPos + 1) * ySegmentSize) - 1;
            }

            final Runnable worker = new HexGridCalcWorker(workInterface, hexGridService, xStartGridPos, yStartGridPos, xEndGridPos, yEndGridPos);

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
            throw new RuntimeException("Unexpected InterruptedException.", e);
        }
    }
}
