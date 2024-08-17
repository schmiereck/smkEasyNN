package de.schmiereck.smkEasyNN.geniNet;

import java.util.Arrays;
import java.util.Formatter;

public class GeniNetPrintUtils {
    public static void printResultForEpochWithTrainSize(final int epochPos, final int mainOutputMseErrorValue, final int expectedOutputTrainSize) {
        printEpoch(epochPos, mainOutputMseErrorValue, expectedOutputTrainSize);
    }

    public static void printFullResultForEpochWithTrainSize(final GeniNet geniNet,
                                                            final int[][][] trainInputArrArrArr, final int[][][] expectedOutputArrArrArr,
                                                            final int epochPos, final int mainOutputMseErrorValue, final int expectedOutputTrainSize) {
        System.out.println();
        printEpoch(epochPos, mainOutputMseErrorValue, expectedOutputTrainSize);
        for (int pos = 0; pos < trainInputArrArrArr.length; pos++) {
            final int[][] trainInputArrArr = trainInputArrArrArr[pos];
            final int[][] expectedOutputArrArr = expectedOutputArrArrArr[pos];
            printResult(geniNet, trainInputArrArr, expectedOutputArrArr);
        }
    }

    public static void printFullResultForEpochWithTrainSize(final GeniNet geniNet,
                                                            final int[][] trainInputArrArr, final int[][] expectedOutputArrArr,
                                                            final int epochPos, final int mainOutputMseErrorValue) {
        System.out.println();
        printEpoch(epochPos, mainOutputMseErrorValue);
        printResult(geniNet, trainInputArrArr, expectedOutputArrArr);
    }

    public static void printFullResultForEpoch(final GeniNet geniNet,
                                               final int[][][] trainInputArrArrArr, final int[][][] expectedOutputArrArrArr,
                                               final int epochPos, final long mainOutputMseErrorValue) {
        System.out.println();
        printEpoch(epochPos, mainOutputMseErrorValue);
        for (int pos = 0; pos < trainInputArrArrArr.length; pos++) {
            final int[][] trainInputArrArr = trainInputArrArrArr[pos];
            final int[][] expectedOutputArrArr = expectedOutputArrArrArr[pos];
            printResult(geniNet, trainInputArrArr, expectedOutputArrArr);
        }
    }

    public static void printFullResultForEpoch(final GeniNet geniNet,
                                               final int[] trainInputArr, int[] outputArr, final int[] expectedOutputArr,
                                               final int epochPos, final long mainOutputMseErrorValue) {
        printEpochPos(epochPos);
        printResultLine(trainInputArr, outputArr, expectedOutputArr);
        printMse(mainOutputMseErrorValue);
        System.out.println();
    }

    public static void printFullResultForEpoch(final GeniNet geniNet,
                                               final int[][][] trainInputArrArrArr, final int[][][] expectedOutputArrArrArr,
                                               final int epochPos, final int mainOutputMseErrorValue, final int samplesLayerPos) {
        System.out.println();
        printEpoch(epochPos, mainOutputMseErrorValue);
        for (int pos = 0; pos < trainInputArrArrArr.length; pos++) {
            final int[][] trainInputArrArr = trainInputArrArrArr[pos];
            final int[][] expectedOutputArrArr = expectedOutputArrArrArr[pos];
            printResult(geniNet, trainInputArrArr, expectedOutputArrArr);
            printMse(mainOutputMseErrorValue);
            System.out.println();

            //System.out.println("samplesOutput:");
            //printSamplesOutput(geniNet, trainInputArrArr, expectedOutputArrArr, samplesLayerPos);
            //System.out.println();
        }
    }

    private static void printMse(long mainOutputMseErrorValue) {
        System.out.printf(" (mse:%6d)", mainOutputMseErrorValue);
    }

    public static void printFullResultForEpoch(final GeniNet geniNet,
                                               final int[][] trainInputArrArr, final int[][] expectedOutputArrArr,
                                               final int epochPos, final long mainOutputMseErrorValue) {
        System.out.println();
        printEpoch(epochPos, mainOutputMseErrorValue);
        printResult(geniNet, trainInputArrArr, expectedOutputArrArr);
        System.out.println();
    }

    private static void printEpoch(final int epochPos, final int mainOutputMseErrorValue, final int expectedOutputTrainSize) {
        printEpochPos(epochPos);
        printTrainSize(expectedOutputTrainSize);
        printMse(mainOutputMseErrorValue);
        System.out.println();
    }

    private static void printEpoch(final int epochPos, final long mainOutputMseErrorValue) {
        printEpochPos(epochPos);
        printMse(mainOutputMseErrorValue);
        System.out.println();
    }

    private static void printTrainSize(final int expectedOutputTrainSize) {
        System.out.printf("TrainSize:%d ", expectedOutputTrainSize);
    }

    private static void printEpochPos(final int epochPos) {
        System.out.printf("%7d epoch ", epochPos + 1);
    }

    static void printResult(final GeniNet geniNet, final int[][] inputArrArr, final int[][] expectedOutputArrArr) {
        for (int resultPos = 0; resultPos < expectedOutputArrArr.length; resultPos++) {
            final int[] expectedOutputArr = expectedOutputArrArr[resultPos];
            final int[] trainInputArr = inputArrArr[resultPos];

            final int[] outputArr = GeniNetService.run(geniNet, trainInputArr);

            printResultLine(trainInputArr, outputArr, expectedOutputArr);
            System.out.println();
        }
    }

//    static void printSamplesOutput(final GeniNet geniNet, final int[][] inputArrArr, final int[][] expectedOutputArrArr, final int layerPos) {
//        for (int resultPos = 0; resultPos < expectedOutputArrArr.length; resultPos++) {
//            final int[] expectedOutputArr = expectedOutputArrArr[resultPos];
//            final int[] trainInputArr = inputArrArr[resultPos];
//
//            final int[] outputArr = GeniNetService.run(geniNet, trainInputArr);
//            final int[] samplesOutputArr = collectSamplesOutputArr(geniNet, layerPos);
//
//            printResultLine(trainInputArr, samplesOutputArr, expectedOutputArr);
//            System.out.println();
//        }
//    }

//    private static int[] collectSamplesOutputArr(final GeniNet geniNet, final int layerPos) {
//        final MlpLayer mlpLayer = geniNet.getLayer(layerPos);
//
//        final int[] samplesOutputArr = new int[mlpLayer.neuronArr.length];
//        for (int pos = 0; pos < mlpLayer.neuronArr.length; pos++) {
//            final MlpNeuron mlpNeuron = mlpLayer.neuronArr[pos];
//            samplesOutputArr[pos] = mlpNeuron.outputValue;
//        }
//        return samplesOutputArr;
//    }

    public static void printResultLine(int[] inputArr, int[] outputArr, final int[] expectedOutputArr) {
        System.out.print(formatResultLine(inputArr, outputArr, expectedOutputArr));
    }

    public static String formatResultLine(int[] inputArr, int[] outputArr, final int[] expectedOutputArr) {
        //final StringBuffer strBuf = new StringBuffer();
        final StringBuilder strBuf = new StringBuilder();
        final Formatter formatter = new Formatter(strBuf);

        for (int inputPos = 0; inputPos < inputArr.length; inputPos++) {
            if (inputPos > 0) {
                formatter.format(" | ");
            }
            formatter.format("%3d", inputArr[inputPos]);
        }
        formatter.format(" --> ");
        for (int outputPos = 0; outputPos < outputArr.length; outputPos++) {
            if (outputPos > 0) {
                formatter.format(" | ");
            }
            formatter.format("%-3d", outputArr[outputPos]);
        }
        formatter.format(" = %s", Arrays.toString(expectedOutputArr));
        return strBuf.toString();
    }
}
