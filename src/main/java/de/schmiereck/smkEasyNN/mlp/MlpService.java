package de.schmiereck.smkEasyNN.mlp;

import java.util.Random;

public class MlpService {

    public static final float BIAS_VALUE = 1.0F;
    public static final float NORM_VALUE = 1.0F;

    public static void runTrainOrder(final MlpNet mlpNet, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr, final Random rnd) {
        for (int expectedResultPos = 0; expectedResultPos < expectedOutputArrArr.length; expectedResultPos++) {
            int idx = expectedResultPos;
            train(mlpNet, trainInputArrArr[idx], expectedOutputArrArr[idx], 0.3F, 0.6F);
        }
    }

    public static void runTrainRandom(final MlpNet mlpNet, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr, final Random rnd) {
        for (int expectedResultPos = 0; expectedResultPos < expectedOutputArrArr.length; expectedResultPos++) {
            int idx = rnd.nextInt(expectedOutputArrArr.length);
            //int idx = expectedResultPos;
            train(mlpNet, trainInputArrArr[idx], expectedOutputArrArr[idx], 0.3F, 0.6F);
        }
    }

    public static void runTrainRandomOrder(final MlpNet mlpNet, final float[][][] expectedOutputArrArrArr, final float[][][] trainInputArrArrArr, final Random rnd) {
        for (int expectedResultArrPos = 0; expectedResultArrPos < expectedOutputArrArrArr.length; expectedResultArrPos++) {
            final int idx = rnd.nextInt(expectedOutputArrArrArr.length);
            final float[][] trainInputArrArr = trainInputArrArrArr[idx];
            final float[][] expectedOutputArrArr = expectedOutputArrArrArr[idx];
            for (int pos = 0; pos < expectedOutputArrArr.length; pos++) {
                train(mlpNet, trainInputArrArr[pos], expectedOutputArrArr[pos], 0.3F, 0.6F);
            }
        }
    }

    public static void train(final MlpNet mlpNet, final float[] trainInputArr, final float[] expectedOutputArr, final float learningRate, final float momentum) {
        float[] calcOutputArr = run(mlpNet, trainInputArr);

        trainWithOutput(mlpNet, expectedOutputArr, calcOutputArr, learningRate, momentum);
    }

    public static void trainWithOutput(final MlpNet mlpNet, final float[] expectedOutputArr, final float[] calcOutputArr, final float learningRate, final float momentum) {
        float[] errorArr = new float[calcOutputArr.length];

        for (int errorPos = 0; errorPos < errorArr.length; errorPos++) {
            errorArr[errorPos] = expectedOutputArr[errorPos] - calcOutputArr[errorPos]; // negative error
        }
        trainWithError(mlpNet, errorArr, learningRate, momentum);
    }

    public static void trainWithError(final MlpNet mlpNet, final float[] errorArr, final float learningRate, final float momentum) {
        float[] actErrorArr = errorArr;
        for (int layerPos = mlpNet.layers.length - 1; layerPos >= 0; layerPos--) {
            final MlpLayer mlpLayer = mlpNet.layers[layerPos];
            actErrorArr = MlpService.train(mlpLayer, actErrorArr, learningRate, momentum);
        }
    }

    public static float[] run(final MlpNet mlpNet, final float[] inputArr) {
        for (int inputPos = 0; inputPos < inputArr.length; inputPos++) {
            mlpNet.setInputValue(inputPos, inputArr[inputPos]);
        }

        for (int layerPos = 0; layerPos < mlpNet.layers.length; layerPos++) {
            final MlpLayer mlpLayer = mlpNet.layers[layerPos];
            run(mlpLayer);
        }

        final MlpLayer outputLayer = mlpNet.getOutputLayer();
        final float[] layerOutputArr = new float[outputLayer.neuronArr.length];
        for (int outputPos = 0; outputPos < outputLayer.neuronArr.length; outputPos++) {
            layerOutputArr[outputPos] = outputLayer.neuronArr[outputPos].output;
        }
        return layerOutputArr;
    }

    public static void run(final MlpLayer mlpLayer) {
        for (int outputPos = 0; outputPos < mlpLayer.neuronArr.length; outputPos++) {
            final MlpNeuron mlpNeuron = mlpLayer.neuronArr[outputPos];
            mlpNeuron.output = 0;

            for (int inputPos = 0; inputPos < mlpLayer.inputArr.length; inputPos++) {
                mlpNeuron.output += mlpNeuron.weightArr[inputPos] * mlpLayer.inputArr[inputPos].getInput();
            }
            if (!mlpLayer.isOutputLayer) {
                mlpNeuron.output = sigmoid(mlpNeuron.output);
            }
        }
    }

    public static float[] train(final MlpLayer mlpLayer, final float[] errorArr, final float learningRate, final float momentum) {
        float[] nextError = new float[mlpLayer.inputArr.length];
        for (int outputPos = 0; outputPos < mlpLayer.neuronArr.length; outputPos++) {
            final MlpNeuron mlpNeuron = mlpLayer.neuronArr[outputPos];
            float error = errorArr[outputPos];

            if (!mlpLayer.isOutputLayer) {
                error *= sigmoidDerivative(mlpLayer.neuronArr[outputPos].output);
            }

            for (int inputPos = 0; inputPos < mlpLayer.inputArr.length; inputPos++) {
                nextError[inputPos] += mlpNeuron.weightArr[inputPos] * error;
                float dw = mlpLayer.inputArr[inputPos].getInput() * error * learningRate;
                mlpNeuron.weightArr[inputPos] += mlpNeuron.dweightArr[inputPos] * momentum + dw;
                mlpNeuron.dweightArr[inputPos] = dw;
            }
        }
        return nextError;
    }

    private static float sigmoidDerivative(final float x) {
        return (x * (NORM_VALUE - x));
    }

    private static float sigmoid(final float x) {
        return (float) (NORM_VALUE / (NORM_VALUE + Math.exp(-x)));
    }

    private static float digital(final float x) {
        return x >= 0.0D ? NORM_VALUE : -NORM_VALUE;
        //return x >= 0.5D ? NORM_VALUE : 0.0F;
        //return x >= 0.0D ? NORM_VALUE : 0.0F;
        //return x * 0.5F;
    }

}
