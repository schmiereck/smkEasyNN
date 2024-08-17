package de.schmiereck.smkEasyNN.geniNet;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import static de.schmiereck.smkEasyNN.geniNet.GeniNetTestUtils.calcToValueMax2;

public class GeniNetValueToValueBottleneckTest {

    record GeniNetConfig(GeniNet geniNet, float minMutationRate, int populationSize, float copyPercent, float maxMutationRate) {
    }

    @Test
    void GIVEN_7_value_inputs_THEN_value_output_after_bottleneck() throws ExecutionException, InterruptedException {
        // Arrange
        //GeniNetService.initValueRange(1000);
        //GeniNetService.initValueRange(100);
        GeniNetService.initValueRange(64);
        //GeniNetService.initValueRange(24);
        //GeniNetService.initValueRange(8);

        final int[][] trainInputArrArr = calcToValueMax2(new int[][]
                {
                        new int[]{ 0, 0, 0, 0, 0, 0, 0 },
                        new int[]{ 0, 0, 0, 0, 0, 0, 1 },
                        new int[]{ 0, 0, 0, 0, 0, 1, 0 },
                        new int[]{ 0, 0, 0, 0, 1, 0, 0 },

                        new int[]{ 0, 0, 0, 1, 0, 0, 0 },
                        new int[]{ 0, 0, 1, 0, 0, 0, 0 },
                        new int[]{ 0, 1, 0, 0, 0, 0, 0 },
                        new int[]{ 1, 0, 0, 0, 0, 0, 0 },
                });
        final int[][] expectedOutputArrArr = calcToValueMax2(new int[][]
                {
                        new int[]{ 0, 0, 0, 0, 0, 0, 0 },
                        new int[]{ 0, 0, 0, 0, 0, 0, 1 },
                        new int[]{ 0, 0, 0, 0, 0, 1, 0 },
                        new int[]{ 0, 0, 0, 0, 1, 0, 0 },

                        new int[]{ 0, 0, 0, 1, 0, 0, 0 },
                        new int[]{ 0, 0, 1, 0, 0, 0, 0 },
                        new int[]{ 0, 1, 0, 0, 0, 0, 0 },
                        new int[]{ 1, 0, 0, 0, 0, 0, 0 },
                });
        final int[] layerSizeArr = new int[]{ 7, 7, 7, 3, 7, 7, 7 };
        //final int[] layerSizeArr = new int[]{ 7, 7, 3, 7, 7 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        //final GeniNet geniNet = GeniNetService.createNet(layerSizeArr, rnd);

        //final int bottleneckLayerPos = 3;
        //mlpNet.getLayer(bottleneckLayerPos).setIsOutputLayer(true);

        // Act & Assert
        // better Results: lower mutationRate (/10), bigger population size (*10), bigger epoche size (*10)
        final float minMutationRate = 0.014F;
        final float minMutationRateArr[] = { 0.012F, 0.013F, 0.014F, 0.016F, 0.017F, 0.018F };
        final float maxMutationRate = 0.18F;
        //final float maxMutationRateArr[] = { 0.4F };
        //final float maxMutationRateArr[] = { 0.02F, 0.1F, 0.18F, 0.2F, 0.3F, 0.4F , 0.4F };
        //final float maxMutationRateArr[] = { 0.1F,  0.12F,  0.18F, 0.25F };
        final float maxMutationRateArr[] = { 0.15F, 0.25F, 0.35F, 0.45F, 0.55F }; // 0.45F best!
        final int populationSize = 3_600;
        final int populationSizeArr[] = { 400, 800, 1_200, 1_600, 1_800, 1_900 };
        final int epocheSize = 32_000;//1_000;//16_000;
        final float copyPercent = 0.011F;
        final float copyPercentArr[] = { 0.0F, 0.001F, 0.0025F, 0.003F, 0.005F, 0.008F, 0.009F, 0.01F, 0.011F, 0.012F, 0.05F, 0.1F };
        final int epochMax = 8000;

        // Erstellen Sie einen ExecutorService mit einer festen Thread-Pool-Größe
        final ExecutorService executor = Executors.newFixedThreadPool(maxMutationRateArr.length);

        // Erstellen Sie eine Liste, um die Future-Objekte zu speichern
        final List<Future<GeniNetConfig>> futureList = new ArrayList<>();

        //for (final float minMutationRateValue : minMutationRateArr) {
        //for (final int populationSizeValue : populationSizeArr) {
        //for (final float copyPercentValue : copyPercentArr) {
        for (final float maxMutationRateValue : maxMutationRateArr) {
            System.out.println("Create task for maxMutationRateValue %.3f".formatted(maxMutationRateValue));
            // Erstellen Sie eine Callable-Aufgabe für jeden Wert
            final Callable<GeniNetConfig> task = () -> {
                // Führen Sie hier Ihre Berechnung durch
                System.out.println("runTrainNet for maxMutationRateValue %f".formatted(maxMutationRateValue));
                final GeniNet trainedGeniNet = GeniNetTrainService.runTrainNet(layerSizeArr,
                        minMutationRate, maxMutationRateValue, populationSize, epocheSize, copyPercent,
                        GeniNetTrainService.DefaultGeniNetMutateConfig,
                        expectedOutputArrArr, trainInputArrArr,
                        //(epochPos, geniNetList) -> {},
                        //(epochePos, geniNetList) -> System.out.printf("Epoch %8d:", epochePos),
                        (epochePos, geniNetList) -> System.out.printf("Epoch %8d for maxMutationRateValue %.3f: mse:%d\n", epochePos, maxMutationRateValue, geniNetList.get(0).error),
                        (error) -> {},
                        //(error) -> System.out.printf(" %3d", error),
                        () -> { },
                        //() -> { System.out.println(); },
                        rnd);
                final GeniNetConfig geniNetConfig = new GeniNetConfig(trainedGeniNet, minMutationRate, populationSize, copyPercent, maxMutationRateValue);
                return geniNetConfig; // Beispiel: Quadrat des Werts berechnen
            };

            // Übergeben Sie die Aufgabe an den ExecutorService
            final Future<GeniNetConfig> future = executor.submit(task);
            futureList.add(future);
        }

        // Schließen Sie den ExecutorService
        executor.shutdown();

        // Warten Sie, bis alle Aufgaben abgeschlossen sind
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        // Drucken Sie die Ergebnisse
        System.out.println();
        for (final Future<GeniNetConfig> future : futureList) {
            final GeniNetConfig geniNetConfig = future.get();
            final GeniNet trainedGeniNet = geniNetConfig.geniNet;
            final float netMinMutationRate = geniNetConfig.minMutationRate;
            final float netMaxMutationRate = geniNetConfig.maxMutationRate;
            final int netPopulationSize = geniNetConfig.populationSize;
            final float netCopyPercent = geniNetConfig.copyPercent;
            System.out.printf("minMutationRate: %.3f, maxMutationRate: %.3f, populationSize: %d, copyPercent: %.3f, error: %d\n",
                    netMinMutationRate, netMaxMutationRate, netPopulationSize, netCopyPercent, trainedGeniNet.error);
            GeniNetPrintUtils.printFullResultForEpoch(trainedGeniNet, trainInputArrArr, expectedOutputArrArr, epocheSize, trainedGeniNet.error);
            //GeniNetTestUtils.actAssertExpectedOutput(trainedGeniNet, trainInputArrArr, expectedOutputArrArr, 0.05F);
        }
    }
}
