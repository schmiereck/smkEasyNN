package de.schmiereck.smkEasyNN;

import java.util.Arrays;

public class IntegerNorm2Main {

    public static void main(String[] args) {
        System.out.println("smkEasyNN Integer-Norm2 V1.0.0");

        int[] layers = new int[4];
        layers[0] = 4;
        layers[1] = 4;
        layers[2] = 4;
        layers[3] = 4;
        IntegerNorm2NeuralNet neuralNet = new IntegerNorm2NeuralNet(layers);

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
    public static void trainNeuralNet(IntegerNorm2NeuralNet neuralNet) {
        // Integer.MAX_VALUE = 2_147_483_647;
        //int numIterations = 500_000_000; //  BEST mse(1):
        //int numIterations = 100_000_000; //  BEST mse(1): 2.400
        //int numIterations = 50_000_000; //      mse(1): 500
        //int numIterations = 5_000_000; //  GOOD mse(1): 6.000,
        //int numIterations = 1_000_000; //  GOOD mse(1): 6.000,
        int numIterations = 500_000; //  GOOD mse(1): 6.000,
        //int numIterations = 50_000; //  GOOD mse(1): 1,

        //double learningRate = 1.0D / 255; // = 1
        //double learningRate = 1.0D / 127; // = 2
        //double learningRate = 1.0D / 63; // = 4
        //double learningRate = 1.0D / 25; // = 10
        //double learningRate = 1.0D / 10; // = 25
        double learningRate = 1.0D / 4; // = 63
        //double learningRate = 1.0D / 2; // = 126
        //double learningRate = 1.0D / 1; // = 255

        //int[][] trainInput = {{0, 0, 0, 2}, {0, 0, 0, 6}, {0, 0, 0, 9}, {0, 0, 0, 16}};
        //int[][] expectedOutput = {{0, 0, 0, 4}, {0, 0, 0, 12}, {0, 0, 0, 18}, {0, 0, 0, 32}};
        int[][] trainInput  = {{0, 0, 0, 0}, {0, 0, 0, 255}, {0, 0, 255, 0}, {0, 255, 0, 0}, {255, 0, 0, 0}, {255, 255, 255, 255}};
        int[][] expectedOutput = {{0, 0, 0, 0}, {0, 0, 0, 255}, {0, 0, 255, 0}, {0, 255, 0, 0}, {255, 0, 0, 0}, {255, 255, 255, 255}};

        for (int i = 0; i < numIterations; i++) {
            //learningRate = 1.0D / (255 - (numIterations % 200));
            int mseSum = 0;
            for (int j = 0; j < trainInput.length; j++) {
                final int[] inputArr = trainInput[j];
                final int[] normInputArr = makeNormArr(inputArr);
                //final int[] outputResult = neuralNet.forward(inputArr);
                final int[] outputResult = neuralNet.forward(normInputArr);

                final int[] expectedOutputArr = expectedOutput[j];
                final int[] expectedNormOutputArr = makeNormArr(expectedOutputArr);
                //neuralNet.backward(expectedOutputArr, learningRate);
                neuralNet.backward(expectedNormOutputArr, learningRate);

                //final int mse = neuralNet.calculateMSE(inputArr, outputResult, expectedOutputArr);
                final int mse = neuralNet.calculateMSE(normInputArr, outputResult, expectedNormOutputArr);
                mseSum += mse;
            }
            System.out.printf("%,11d:\t%,8d\r", i, mseSum / trainInput.length);
        }
        System.out.println();
    }

    private static void predict(final IntegerNorm2NeuralNet neuralNet, final int[] inputArr) {
        int[] normInputArr = makeNormArr(inputArr);

        //int[] outputArr = neuralNet.predict(inputArr);
        int[] outputArr = neuralNet.predict(normInputArr);

        int[] resultArr = makeRenormArr(outputArr);

        // Gib das Ergebnis aus
        System.out.println("Das Netzwerk gibt für die Eingabe " + Arrays.toString(inputArr) + " den Ausgabewert " + Arrays.toString(resultArr) + " aus.");
    }

    private static int[] makeNormArr(final int[] inputArr) {
        int[] normInputArr = new int[inputArr.length];
        for (int i2 = 0; i2 < inputArr.length; i2++) {
            normInputArr[i2] = inputArr[i2] * IntegerNorm2NeuralNet.MAX_NEURON_PREC; // Skalieren der Eingabe auf den Bereich [0, 1]
        }
        return normInputArr;
    }

    private static int[] makeRenormArr(final int[] inputArr) {
        int[] result = new int[inputArr.length];
        for (int i = 0; i < inputArr.length; i++) {
            result[i] = (int) (inputArr[i] / IntegerNorm2NeuralNet.MAX_NEURON_PREC); // Umkehrung der Skalierung des Ausgangs
        }
        return result;
    }
}
