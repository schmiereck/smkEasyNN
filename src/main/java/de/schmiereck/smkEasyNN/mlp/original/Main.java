package de.schmiereck.smkEasyNN.mlp.original;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println("smkEasyNN Integer-Norm Numbers V1.0.0");

        int[] layers = new int[5];
        layers[0] = 4;
        layers[1] = 5;
        layers[2] = 6;
        layers[3] = 5;
        layers[4] = 5;
        IntegerNormNeuralNet neuralNet = new IntegerNormNeuralNet(layers);

        trainNeuralNet(neuralNet);

        // Frage das Netzwerk nach dem Ausgabewert für einen gegebenen Eingabewert
        predict(neuralNet, new int[]{0, 0, 0, 0});
        predict(neuralNet, new int[]{0, 255, 0, 0});
        predict(neuralNet, new int[]{255, 255, 0, 0});
        predict(neuralNet, new int[]{0, 0, 255, 0});
        predict(neuralNet, new int[]{255, 0, 255, 0});
        predict(neuralNet, new int[]{0, 255, 255, 0});
        predict(neuralNet, new int[]{0, 0, 0, 255});
        predict(neuralNet, new int[]{255, 0, 0, 255});
        predict(neuralNet, new int[]{0, 255, 0, 255});
        predict(neuralNet, new int[]{255, 255, 255, 255});
    }
    /**
     Hier ist eine Trainingsfunktion, die das Netzwerk auf die Beispieldaten abbildet:
     */
    public static void trainNeuralNet(IntegerNormNeuralNet neuralNet) {
        // Integer.MAX_VALUE = 2_147_483_647;
        int numIterations = 500_000_000; //  BEST mse(adapt):
        //int numIterations = 25_000_000; //          mse(1): 3.500
        //int numIterations = 10_000_000; //     mse(adapt): 5.500
        //int numIterations = 5_000_000; //  GOOD   mse(1): 6.300, mse(adapt): 6.300, mse(adapt127): 6.300
        //double learningRate = 0.1D;//0.1D;
        double learningRate = 1.0D / 255; // = 1
        //double learningRate = 1.0D / 127; // = 2
        //double learningRate = 1.0D / 63; // = 4

        int[][] trainInput  = new int[16][4];
        int[][] expectedOutput = new int[16][5];

        for (int nr = 0; nr < 16; nr++) {
            for (int bitPos = 0; bitPos < 4; bitPos++) {
                int bitMask = 1 << bitPos;
                trainInput[nr][bitPos] = (nr & bitMask) != 0 ? 255 : 0;
                expectedOutput[nr][bitPos] = (nr & bitMask) != 0 ? 255 : 0;
            }
            expectedOutput[nr][4] = nr * 10;
        }

        for (int i = 0; i < numIterations; i++) {
            learningRate = 1.0D / (255 - (numIterations % 127));
            int mseSum = 0;
            for (int j = 0; j < trainInput.length; j++) {
                final int[] inputArr = trainInput[j];
                final int[] outputResult = neuralNet.forward(inputArr);

                final int[] expectedOutputArr = expectedOutput[j];
                neuralNet.backward(expectedOutputArr, learningRate);

                final int mse = neuralNet.calculateMSE(inputArr, outputResult, expectedOutputArr);
                mseSum += mse;
            }
            System.out.printf("%,11d:\t%,8d\r", i, mseSum / trainInput.length);
        }
        System.out.println();
    }

    private static void predict(final IntegerNormNeuralNet neuralNet, final int[] input) {
        int[] output = neuralNet.predict(input);

        // Gib das Ergebnis aus
        System.out.println("Input:" + Arrays.toString(input) + " \t Output: " + Arrays.toString(output) + " aus.");
    }
}
