package de.schmiereck.smkEasyNN.mlp;

import java.util.Formatter;

import org.junit.jupiter.api.Assertions;

public class MlpNetTestUtils {

    public static void printResultForEpoch(final MlpNet mlpNet, final float[][] trainInputArrArr, final float[][] expectedOutputArrArr, final int epochPos) {
        System.out.println();
        System.out.printf("%d epoch\n", epochPos + 1);
        printResult(mlpNet, trainInputArrArr, expectedOutputArrArr);
    }

    static void printResult(final MlpNet mlpNet, final float[][] inputArrArr, final float[][] expectedOutputArrArr) {
        for (int resultPos = 0; resultPos < expectedOutputArrArr.length; resultPos++) {
            final float[] expectedOutputArr = expectedOutputArrArr[resultPos];
            final float[] trainInputArr = inputArrArr[resultPos];

            final float[] outputArr = MlpService.run(mlpNet, trainInputArr);

            printResultLine(trainInputArr, outputArr);
        }
    }

    static void printSamplesOutput(final MlpNet mlpNet, final float[][] inputArrArr, final float[][] expectedOutputArrArr, final int layerPos) {
        for (int resultPos = 0; resultPos < expectedOutputArrArr.length; resultPos++) {
            final float[] expectedOutputArr = expectedOutputArrArr[resultPos];
            final float[] trainInputArr = inputArrArr[resultPos];

            final float[] outputArr = MlpService.run(mlpNet, trainInputArr);
            final float[] samplesOutputArr = collectSamplesOutputArr(mlpNet, layerPos);

            printResultLine(trainInputArr, samplesOutputArr);
        }
    }

    private static float[] collectSamplesOutputArr(final MlpNet mlpNet, final int layerPos) {
        final MlpLayer mlpLayer = mlpNet.getLayer(layerPos);

        final float[] samplesOutputArr = new float[mlpLayer.neuronArr.length];
        for (int pos = 0; pos < mlpLayer.neuronArr.length; pos++) {
            final MlpNeuron mlpNeuron = mlpLayer.neuronArr[pos];
            samplesOutputArr[pos] = mlpNeuron.output;
        }
        return samplesOutputArr;
    }

    public static void printResultLine(float[] inputArr, float[] outputArr) {
        System.out.println(formatResultLine(inputArr, outputArr));
    }

    public static String formatResultLine(float[] inputArr, float[] outputArr) {
        //final StringBuffer strBuf = new StringBuffer();
        final StringBuilder strBuf = new StringBuilder();
        final Formatter formatter = new Formatter(strBuf);

        for (int inputPos = 0; inputPos < inputArr.length; inputPos++) {
            if (inputPos > 0) {
                formatter.format(" | ");
            }
            formatter.format("%.1f", inputArr[inputPos]);
        }
        formatter.format(" --> ");
        for (int outputPos = 0; outputPos < outputArr.length; outputPos++) {
            if (outputPos > 0) {
                formatter.format(" | ");
            }
            formatter.format("%6.3f", outputArr[outputPos]);
        }
        return strBuf.toString();
    }

    static void actAssertExpectedOutput(final MlpNet mlpNet, final float[][] inputArrArr, final float[][] expectedOutputArrArr, final float delta) {
        for (int resultPos = 0; resultPos < expectedOutputArrArr.length; resultPos++) {
            final float[] expectedOutputArr = expectedOutputArrArr[resultPos];
            final float[] inputArr = inputArrArr[resultPos];

            final float[] outputArr = MlpService.run(mlpNet, inputArr);

            for (int expectedOutputPos = 0; expectedOutputPos < expectedOutputArr.length; expectedOutputPos++) {
                Assertions.assertEquals(expectedOutputArr[expectedOutputPos], outputArr[expectedOutputPos], delta,
                        "expectedOutput line %d: expectedOutputPos %d\n%s".formatted(resultPos, expectedOutputPos,
                                formatResultLine(inputArr, outputArr)));
            }
        }
    }

}
