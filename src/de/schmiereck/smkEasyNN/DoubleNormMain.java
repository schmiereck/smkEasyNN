package de.schmiereck.smkEasyNN;

import java.util.Arrays;

public class DoubleNormMain {

    public static void main(String[] args) {
	    System.out.println("smkEasyNN Double-Norm V1.0.0");

	    int[] layers = new int[4];
        layers[0] = 4;
        layers[1] = 4;
        layers[2] = 4;
        layers[3] = 4;
        DoubleNormNeuralNet neuralNet = new DoubleNormNeuralNet(layers);

        trainNeuralNet(neuralNet);

        // Frage das Netzwerk nach dem Ausgabewert für einen gegebenen Eingabewert
        predict(neuralNet, new int[] {0, 0, 0, 0});
        predict(neuralNet, new int[] {255, 0, 0, 0});
        predict(neuralNet, new int[] {0, 255, 0, 0});
        predict(neuralNet, new int[] {0, 0, 255, 0});
        predict(neuralNet, new int[] {0, 0, 0, 255});
        predict(neuralNet, new int[] {255, 255, 255, 255});
        predict(neuralNet, new int[] {0, 255, 255, 0});
        predict(neuralNet, new int[] {255, 0, 0, 255});
        predict(neuralNet, new int[] {255, 0, 255, 0});
        predict(neuralNet, new int[] {0, 255, 0, 255});
    }

    /**
     Hier ist eine Trainingsfunktion, die das Netzwerk auf die Beispieldaten 2:4, 6:12, 9:18 und 16:32 abbildet:
     */
    public static void trainNeuralNet(DoubleNormNeuralNet neuralNet) {
        // Integer.MAX_VALUE = 2_147_483_647
        //int numIterations = 500_000_000; // BEST
        //int numIterations = 5_000_000; //
        int numIterations = 100_000; //

        double learningRate = 0.1D;//0.1D;

        //int[][] trainInput = {{0, 0, 0, 2}, {0, 0, 0, 6}, {0, 0, 0, 9}, {0, 0, 0, 16}};
        //int[][] expectedOutput = {{0, 0, 0, 4}, {0, 0, 0, 12}, {0, 0, 0, 18}, {0, 0, 0, 32}};
        int[][] trainInput  = {{0, 0, 0, 0}, {0, 0, 0, 255}, {0, 0, 255, 0}, {0, 255, 0, 0}, {255, 0, 0, 0}, {255, 255, 255, 255}};
        int[][] expectedOutput = {{0, 0, 0, 0}, {0, 0, 0, 255}, {0, 0, 255, 0}, {0, 255, 0, 0}, {255, 0, 0, 0}, {255, 255, 255, 255}};

        for (int i = 0; i < numIterations; i++) {
            double mseSum = 0;
            for (int j = 0; j < trainInput.length; j++) {
                final int[] inputArr = trainInput[j];
                double[] normInputArr = makeNormArr(inputArr);
                final double[] outputResult = neuralNet.forward(normInputArr);

                final int[] expectedOutputArr = expectedOutput[j];
                double[] expectedNormOutputArr = makeNormArr(expectedOutputArr);
                neuralNet.backward(expectedNormOutputArr, learningRate);

                final double mse = neuralNet.calculateMSE(normInputArr, outputResult, expectedNormOutputArr);
                mseSum += mse;
            }
            System.out.printf("%,11d:\t%8.6f\r", i, mseSum / trainInput.length);
        }
        System.out.println();
    }

    private static void predict(final DoubleNormNeuralNet neuralNet, final int[] inputArr) {
        double[] normInputArr = makeNormArr(inputArr);

        double[] outputArr = neuralNet.predict(normInputArr);

        int[] resultArr = makeRenormArr(outputArr);

        // Gib das Ergebnis aus
        System.out.println("Das Netzwerk gibt für die Eingabe " + Arrays.toString(inputArr) + " den Ausgabewert " + Arrays.toString(resultArr) + " aus.");
    }

    private static double[] makeNormArr(final int[] inputArr) {
        double[] normInputArr = new double[inputArr.length];
        for (int i2 = 0; i2 < inputArr.length; i2++) {
            normInputArr[i2] = inputArr[i2] / 255.0D; // Skalieren der Eingabe auf den Bereich [0, 1]
        }
        return normInputArr;
    }

    private static int[] makeRenormArr(final double[] inputArr) {
        int[] result = new int[inputArr.length];
        for (int i = 0; i < inputArr.length; i++) {
            result[i] = (int) (inputArr[i] * 255.0D); // Umkehrung der Skalierung des Ausgangs
        }
        return result;
    }
}
