package de.schmiereck.smkEasyNN.genEden.view;

import de.schmiereck.smkEasyNN.genEden.HexGridController;
import de.schmiereck.smkEasyNN.genEden.HexGridModel;
import de.schmiereck.smkEasyNN.genEden.service.HexGridCalcWorker;
import de.schmiereck.smkEasyNN.genEden.service.HexGridService;
import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HexGridViewParallelProcessor {
    private static final int NUM_THREADS = Math.max(1, (Runtime.getRuntime().availableProcessors() - 2) / 4);

    public void processHexGrid(final HexGridController hexGridController, final HexGridModel hexGridModel) {
        final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        final int xGridSize = hexGridModel.xSize;
        final int yGridSize = hexGridModel.ySize;
        final int ySegmentSize = xGridSize / NUM_THREADS;

        runWorker(executor, hexGridModel, ySegmentSize, xGridSize, yGridSize, hexGridController::updateHexGridView);

        executor.shutdown();
    }

    private static void runWorker(final ExecutorService executor, final HexGridModel hexGridModel, final int ySegmentSize, final int xGridSize, final int yGridSize, final HexGridViewWorker.WorkInterface workInterface) {
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
                yEndGridPos = (threadPos + 1) * ySegmentSize;
            }

            final Runnable worker = new HexGridViewWorker(workInterface, hexGridModel, xStartGridPos, yStartGridPos, xEndGridPos, yEndGridPos);

            executor.submit(() -> {
                try {
                    Platform.runLater(() -> {
                        worker.run();
                    });
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
