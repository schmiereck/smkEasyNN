package de.schmiereck.smkEasyNN;

import java.util.Arrays;

public class IntegerNormMain {

    public static void main(String[] args) {
        System.out.println("smkEasyNN Integer-Norm V1.0.0");

        int[] layers = new int[4];
        layers[0] = 4;
        layers[1] = 4;
        layers[2] = 4;
        layers[3] = 4;
        IntegerNormNeuralNet neuralNet = new IntegerNormNeuralNet(layers);

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
     Hier ist eine Trainingsfunktion, die das Netzwerk auf die Beispieldaten abbildet:
     */
    public static void trainNeuralNet(IntegerNormNeuralNet neuralNet) {
        // Integer.MAX_VALUE = 2_147_483_647;
        //int numIterations = 500_000_000; //  BEST mse(1):
        //int numIterations = 100_000_000; //  BEST mse(1): 100
        //int numIterations = 50_000_000; //      mse(1): 1.300, mse(adapt): 100
        int numIterations = 5_000_000; //  GOOD mse(1): 2.100/1.200, mse(2): 2.900, mse(4): 3.200, mse(adapt127): 2.500, mse(adapt16): 3.600, mse(adapt3): 3.200
        //double learningRate = 0.1D;//0.1D;
        double learningRate = 1.0D / 255; // = 1
        //double learningRate = 1.0D / 127; // = 2
        //double learningRate = 1.0D / 63; // = 4

        //int[][] trainInput = {{0, 0, 0, 2}, {0, 0, 0, 6}, {0, 0, 0, 9}, {0, 0, 0, 16}};
        //int[][] expectedOutput = {{0, 0, 0, 4}, {0, 0, 0, 12}, {0, 0, 0, 18}, {0, 0, 0, 32}};
        int[][] trainInput  = {{0, 0, 0, 0}, {0, 0, 0, 255}, {0, 0, 255, 0}, {0, 255, 0, 0}, {255, 0, 0, 0}, {255, 255, 255, 255}};
        int[][] expectedOutput = {{0, 0, 0, 0}, {0, 0, 0, 255}, {0, 0, 255, 0}, {0, 255, 0, 0}, {255, 0, 0, 0}, {255, 255, 255, 255}};

        for (int i = 0; i < numIterations; i++) {
            //learningRate = 1.0D / (255 - (numIterations % 3));
            int mseSum = 0;
            for (int j = 0; j < trainInput.length; j++) {
                final int[] inputArr = trainInput[j];
                int[] normInputArr = makeNormArr(inputArr);
                final int[] outputResult = neuralNet.forward(normInputArr);

                final int[] expectedOutputArr = expectedOutput[j];
                int[] expectedNormOutputArr = makeNormArr(expectedOutputArr);
                neuralNet.backward(expectedNormOutputArr, learningRate);

                final int mse = neuralNet.calculateMSE(inputArr, outputResult, expectedNormOutputArr);
                mseSum += mse;
            }
            System.out.printf("%,11d:\t%,8d\r", i, mseSum / trainInput.length);
        }
        System.out.println();
    }

    private static void predict(final IntegerNormNeuralNet neuralNet, final int[] input) {
        int[] normInputArr = makeNormArr(input);

        int[] output = neuralNet.predict(normInputArr);

        int[] result = makeRenormArr(output);

        // Gib das Ergebnis aus
        System.out.println("Das Netzwerk gibt für die Eingabe " + Arrays.toString(input) + " den Ausgabewert " + Arrays.toString(result) + " aus.");
    }

    private static int[] makeNormArr(final int[] inputArr) {
        int[] normInputArr = new int[inputArr.length];
        for (int i2 = 0; i2 < inputArr.length; i2++) {
            normInputArr[i2] = inputArr[i2]; // Skalieren der Eingabe auf den Bereich [0, 1]
        }
        return normInputArr;
    }

    private static int[] makeRenormArr(final int[] inputArr) {
        int[] result = new int[inputArr.length];
        for (int i = 0; i < inputArr.length; i++) {
            result[i] = (int) (inputArr[i]); // Umkehrung der Skalierung des Ausgangs
        }
        return result;
    }
}
