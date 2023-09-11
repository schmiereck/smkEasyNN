package de.schmiereck.smkEasyNN.mlp;

import java.util.Arrays;
import java.util.Formatter;

public class MlpNetPrintUtils {

    public static void printResultForEpoch(final MlpNet mlpNet, final float[][][] trainInputArrArrArr, final float[][] expectedOutputArrArrArr[], final int epochPos) {
        printEpoch(epochPos);
        for (int pos = 0; pos < trainInputArrArrArr.length; pos++) {
            final float[][] trainInputArrArr = trainInputArrArrArr[pos];
            final float[][] expectedOutputArrArr = expectedOutputArrArrArr[pos];
            printResult(mlpNet, trainInputArrArr, expectedOutputArrArr);
        }
    }

    public static void printResultForEpoch(final MlpNet mlpNet, final float[][][] trainInputArrArrArr, final float[][] expectedOutputArrArrArr[], final int epochPos, final int samplesLayerPos) {
        printEpoch(epochPos);
        for (int pos = 0; pos < trainInputArrArrArr.length; pos++) {
            final float[][] trainInputArrArr = trainInputArrArrArr[pos];
            final float[][] expectedOutputArrArr = expectedOutputArrArrArr[pos];
            printResult(mlpNet, trainInputArrArr, expectedOutputArrArr);

            System.out.println("samplesOutput:");
            printSamplesOutput(mlpNet, trainInputArrArr, expectedOutputArrArr, samplesLayerPos);
            System.out.println();
        }
    }

    public static void printResultForEpoch(final MlpNet mlpNet, final float[][] trainInputArrArr, final float[][] expectedOutputArrArr, final int epochPos) {
        printEpoch(epochPos);
        printResult(mlpNet, trainInputArrArr, expectedOutputArrArr);
    }

    private static void printEpoch(int epochPos) {
        System.out.println();
        System.out.printf("%d epoch\n", epochPos + 1);
    }

    static void printResult(final MlpNet mlpNet, final float[][] inputArrArr, final float[][] expectedOutputArrArr) {
        for (int resultPos = 0; resultPos < expectedOutputArrArr.length; resultPos++) {
            final float[] expectedOutputArr = expectedOutputArrArr[resultPos];
            final float[] trainInputArr = inputArrArr[resultPos];

            final float[] outputArr = MlpService.run(mlpNet, trainInputArr);

            printResultLine(trainInputArr, outputArr, expectedOutputArr);
        }
    }

    static void printSamplesOutput(final MlpNet mlpNet, final float[][] inputArrArr, final float[][] expectedOutputArrArr, final int layerPos) {
        for (int resultPos = 0; resultPos < expectedOutputArrArr.length; resultPos++) {
            final float[] expectedOutputArr = expectedOutputArrArr[resultPos];
            final float[] trainInputArr = inputArrArr[resultPos];

            final float[] outputArr = MlpService.run(mlpNet, trainInputArr);
            final float[] samplesOutputArr = collectSamplesOutputArr(mlpNet, layerPos);

            printResultLine(trainInputArr, samplesOutputArr, expectedOutputArr);
        }
    }

    private static float[] collectSamplesOutputArr(final MlpNet mlpNet, final int layerPos) {
        final MlpLayer mlpLayer = mlpNet.getLayer(layerPos);

        final float[] samplesOutputArr = new float[mlpLayer.neuronArr.length];
        for (int pos = 0; pos < mlpLayer.neuronArr.length; pos++) {
            final MlpNeuron mlpNeuron = mlpLayer.neuronArr[pos];
            samplesOutputArr[pos] = mlpNeuron.outputValue;
        }
        return samplesOutputArr;
    }

    public static void printResultLine(float[] inputArr, float[] outputArr, final float[] expectedOutputArr) {
        System.out.println(formatResultLine(inputArr, outputArr, expectedOutputArr));
    }

    public static String formatResultLine(float[] inputArr, float[] outputArr, final float[] expectedOutputArr) {
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
        formatter.format(" = %s", Arrays.toString(expectedOutputArr));
        return strBuf.toString();
    }
}