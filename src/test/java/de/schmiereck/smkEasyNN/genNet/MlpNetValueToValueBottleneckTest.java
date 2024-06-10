package de.schmiereck.smkEasyNN.genNet;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class MlpNetValueToValueBottleneckTest {

    record GenNetConfig(GenNet genNet, float minMutationRate, int populationSize, float copyPercent, float maxMutationRate) {
    }

    @Test
    void GIVEN_7_value_inputs_THEN_value_output_after_bottleneck() throws ExecutionException, InterruptedException {
        // Arrange
        final float[][] trainInputArrArr = new float[][]
                {
                        new float[]{ 0, 0, 0, 0, 0, 0, 0 },
                        new float[]{ 0, 0, 0, 0, 0, 0, 1 },
                        new float[]{ 0, 0, 0, 0, 0, 1, 0 },
                        new float[]{ 0, 0, 0, 0, 1, 0, 0 },

                        new float[]{ 0, 0, 0, 1, 0, 0, 0 },
                        new float[]{ 0, 0, 1, 0, 0, 0, 0 },
                        new float[]{ 0, 1, 0, 0, 0, 0, 0 },
                        new float[]{ 1, 0, 0, 0, 0, 0, 0 },
                };
        final float[][] expectedOutputArrArr = new float[][]
                {
                        new float[]{ 0, 0, 0, 0, 0, 0, 0 },
                        new float[]{ 0, 0, 0, 0, 0, 0, 1 },
                        new float[]{ 0, 0, 0, 0, 0, 1, 0 },
                        new float[]{ 0, 0, 0, 0, 1, 0, 0 },

                        new float[]{ 0, 0, 0, 1, 0, 0, 0 },
                        new float[]{ 0, 0, 1, 0, 0, 0, 0 },
                        new float[]{ 0, 1, 0, 0, 0, 0, 0 },
                        new float[]{ 1, 0, 0, 0, 0, 0, 0 },
                };
        //final int[] layerSizeArr = new int[]{ 7, 7, 7, 3, 7, 7, 7 };
        final int[] layerSizeArr = new int[]{ 7, 7, 3, 7, 7 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        //final GenNet genNet = GenNetService.createNet(layerSizeArr, rnd);

        //final int bottleneckLayerPos = 3;
        //mlpNet.getLayer(bottleneckLayerPos).setIsOutputLayer(true);

        // Act & Assert
        // better Results: lower mutationRate (/10), bigger population size (*10), bigger epoche size (*10)
        final float minMutationRate = 0.014F;
        final float minMutationRateArr[] = { 0.012F, 0.013F, 0.014F, 0.016F, 0.017F, 0.018F };
        final float maxMutationRate = 0.18F;
        final float maxMutationRateArr[] = { 0.02F, 0.1F, 0.18F, 0.2F, 0.3F, 0.4F };
        final int populationSize = 3_600;
        final int populationSizeArr[] = { 400, 800, 1_200, 1_600, 1_800, 1_900 };
        final int epocheSize = 16_000;
        final float copyPercent = 0.011F;
        final float copyPercentArr[] = { 0.0F, 0.001F, 0.0025F, 0.003F, 0.005F, 0.008F, 0.009F, 0.01F, 0.011F, 0.012F, 0.05F, 0.1F };
        final int epochMax = 8000;

        // Erstellen Sie einen ExecutorService mit einer festen Thread-Pool-Größe
        final ExecutorService executor = Executors.newFixedThreadPool(maxMutationRateArr.length);

        // Erstellen Sie eine Liste, um die Future-Objekte zu speichern
        final List<Future<GenNetConfig>> futureList = new ArrayList<>();

        //for (final float minMutationRateValue : minMutationRateArr) {
        //for (final int populationSizeValue : populationSizeArr) {
        //for (final float copyPercentValue : copyPercentArr) {
        for (final float maxMutationRateValue : maxMutationRateArr) {
            // Erstellen Sie eine Callable-Aufgabe für jeden Wert
            final Callable<GenNetConfig> task = () -> {
                // Führen Sie hier Ihre Berechnung durch
                final GenNet trainedGenNet = GenNetTrainService.runTrainNet(layerSizeArr,
                        minMutationRate, maxMutationRateValue, populationSize, epocheSize, copyPercent,
                        GenNetTrainService.DefaultGenNetMutateConfig,
                        expectedOutputArrArr, trainInputArrArr,
                        (epochPos) -> {},
                        (error) -> {},
                        () -> {},
                        rnd);
                final GenNetConfig genNetConfig = new GenNetConfig(trainedGenNet, minMutationRate, populationSize, copyPercent, maxMutationRateValue);
                return genNetConfig; // Beispiel: Quadrat des Werts berechnen
            };

            // Übergeben Sie die Aufgabe an den ExecutorService
            final Future<GenNetConfig> future = executor.submit(task);
            futureList.add(future);
        }

        // Schließen Sie den ExecutorService
        executor.shutdown();

        // Warten Sie, bis alle Aufgaben abgeschlossen sind
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        // Drucken Sie die Ergebnisse
        System.out.println();
        for (final Future<GenNetConfig> future : futureList) {
            final GenNetConfig genNetConfig = future.get();
            final GenNet trainedGenNet = genNetConfig.genNet;
            final float netMinMutationRate = genNetConfig.minMutationRate;
            final float netMaxMutationRate = genNetConfig.maxMutationRate;
            final int netPopulationSize = genNetConfig.populationSize;
            final float netCopyPercent = genNetConfig.copyPercent;
            System.out.printf("minMutationRate: %.3f, maxMutationRate: %.3f, populationSize: %d, copyPercent: %.3f, error: %.3f\n",
                    netMinMutationRate, netMaxMutationRate, netPopulationSize, netCopyPercent, trainedGenNet.error);
            GenNetPrintUtils.printFullResultForEpoch(trainedGenNet, trainInputArrArr, expectedOutputArrArr, epocheSize, trainedGenNet.error);
            //GenNetTestUtils.actAssertExpectedOutput(trainedGenNet, trainInputArrArr, expectedOutputArrArr, 0.05F);
        }
    }
}
