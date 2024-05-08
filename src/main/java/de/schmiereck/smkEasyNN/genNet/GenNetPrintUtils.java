package de.schmiereck.smkEasyNN.genNet;

import de.schmiereck.smkEasyNN.mlp.MlpLayer;
import de.schmiereck.smkEasyNN.mlp.MlpNet;
import de.schmiereck.smkEasyNN.mlp.MlpNeuron;
import de.schmiereck.smkEasyNN.mlp.MlpService;

import java.util.Arrays;
import java.util.Formatter;

public class GenNetPrintUtils {
    public static void printResultForEpochWithTrainSize(final int epochPos, final float mainOutputMseErrorValue, final int expectedOutputTrainSize) {
        printEpoch(epochPos, mainOutputMseErrorValue, expectedOutputTrainSize);
    }

    public static void printFullResultForEpochWithTrainSize(final GenNet genNet,
                                                            final float[][][] trainInputArrArrArr, final float[][][] expectedOutputArrArrArr,
                                                            final int epochPos, final float mainOutputMseErrorValue, final int expectedOutputTrainSize) {
        System.out.println();
        printEpoch(epochPos, mainOutputMseErrorValue, expectedOutputTrainSize);
        for (int pos = 0; pos < trainInputArrArrArr.length; pos++) {
            final float[][] trainInputArrArr = trainInputArrArrArr[pos];
            final float[][] expectedOutputArrArr = expectedOutputArrArrArr[pos];
            printResult(genNet, trainInputArrArr, expectedOutputArrArr);
        }
    }

    public static void printFullResultForEpochWithTrainSize(final GenNet genNet,
                                                            final float[][] trainInputArrArr, final float[][] expectedOutputArrArr,
                                                            final int epochPos, final float mainOutputMseErrorValue) {
        System.out.println();
        printEpoch(epochPos, mainOutputMseErrorValue);
        printResult(genNet, trainInputArrArr, expectedOutputArrArr);
    }

    public static void printFullResultForEpoch(final GenNet genNet,
                                               final float[][][] trainInputArrArrArr, final float[][][] expectedOutputArrArrArr,
                                               final int epochPos, final float mainOutputMseErrorValue) {
        System.out.println();
        printEpoch(epochPos, mainOutputMseErrorValue);
        for (int pos = 0; pos < trainInputArrArrArr.length; pos++) {
            final float[][] trainInputArrArr = trainInputArrArrArr[pos];
            final float[][] expectedOutputArrArr = expectedOutputArrArrArr[pos];
            printResult(genNet, trainInputArrArr, expectedOutputArrArr);
        }
    }

    public static void printFullResultForEpoch(final GenNet genNet,
                                               final float[] trainInputArr, float[] outputArr, final float[] expectedOutputArr,
                                               final int epochPos, final float mainOutputMseErrorValue) {
        printEpochPos(epochPos);
        printResultLine(trainInputArr, outputArr, expectedOutputArr);
        printMse(mainOutputMseErrorValue);
        System.out.println();
    }

    public static void printFullResultForEpoch(final GenNet genNet,
                                               final float[][][] trainInputArrArrArr, final float[][][] expectedOutputArrArrArr,
                                               final int epochPos, final float mainOutputMseErrorValue, final int samplesLayerPos) {
        System.out.println();
        printEpoch(epochPos, mainOutputMseErrorValue);
        for (int pos = 0; pos < trainInputArrArrArr.length; pos++) {
            final float[][] trainInputArrArr = trainInputArrArrArr[pos];
            final float[][] expectedOutputArrArr = expectedOutputArrArrArr[pos];
            printResult(genNet, trainInputArrArr, expectedOutputArrArr);
            printMse(mainOutputMseErrorValue);
            System.out.println();

            //System.out.println("samplesOutput:");
            //printSamplesOutput(genNet, trainInputArrArr, expectedOutputArrArr, samplesLayerPos);
            //System.out.println();
        }
    }

    private static void printMse(float mainOutputMseErrorValue) {
        System.out.printf(" (mse:%.6f)", mainOutputMseErrorValue);
    }

    public static void printFullResultForEpoch(final GenNet genNet,
                                               final float[][] trainInputArrArr, final float[][] expectedOutputArrArr,
                                               final int epochPos, final float mainOutputMseErrorValue) {
        System.out.println();
        printEpoch(epochPos, mainOutputMseErrorValue);
        printResult(genNet, trainInputArrArr, expectedOutputArrArr);
        System.out.println();
    }

    private static void printEpoch(final int epochPos, final float mainOutputMseErrorValue, final int expectedOutputTrainSize) {
        printEpochPos(epochPos);
        printTrainSize(expectedOutputTrainSize);
        printMse(mainOutputMseErrorValue);
        System.out.println();
    }

    private static void printEpoch(final int epochPos, final float mainOutputMseErrorValue) {
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

    static void printResult(final GenNet genNet, final float[][] inputArrArr, final float[][] expectedOutputArrArr) {
        for (int resultPos = 0; resultPos < expectedOutputArrArr.length; resultPos++) {
            final float[] expectedOutputArr = expectedOutputArrArr[resultPos];
            final float[] trainInputArr = inputArrArr[resultPos];

            final float[] outputArr = GenNetService.run(genNet, trainInputArr);

            printResultLine(trainInputArr, outputArr, expectedOutputArr);
            System.out.println();
        }
    }

//    static void printSamplesOutput(final GenNet genNet, final float[][] inputArrArr, final float[][] expectedOutputArrArr, final int layerPos) {
//        for (int resultPos = 0; resultPos < expectedOutputArrArr.length; resultPos++) {
//            final float[] expectedOutputArr = expectedOutputArrArr[resultPos];
//            final float[] trainInputArr = inputArrArr[resultPos];
//
//            final float[] outputArr = GenNetService.run(genNet, trainInputArr);
//            final float[] samplesOutputArr = collectSamplesOutputArr(genNet, layerPos);
//
//            printResultLine(trainInputArr, samplesOutputArr, expectedOutputArr);
//            System.out.println();
//        }
//    }

//    private static float[] collectSamplesOutputArr(final GenNet genNet, final int layerPos) {
//        final MlpLayer mlpLayer = genNet.getLayer(layerPos);
//
//        final float[] samplesOutputArr = new float[mlpLayer.neuronArr.length];
//        for (int pos = 0; pos < mlpLayer.neuronArr.length; pos++) {
//            final MlpNeuron mlpNeuron = mlpLayer.neuronArr[pos];
//            samplesOutputArr[pos] = mlpNeuron.outputValue;
//        }
//        return samplesOutputArr;
//    }

    public static void printResultLine(float[] inputArr, float[] outputArr, final float[] expectedOutputArr) {
        System.out.print(formatResultLine(inputArr, outputArr, expectedOutputArr));
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
