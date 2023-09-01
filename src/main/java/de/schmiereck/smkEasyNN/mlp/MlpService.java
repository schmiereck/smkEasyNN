package de.schmiereck.smkEasyNN.mlp;

import java.util.Random;

public class MlpService {

    public static final float BIAS_VALUE = 1.0F;
    public static final float NORM_VALUE = 1.0F;

    public static void runTrain(final MlpNet mlpNet, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr, final Random rnd) {
        for (int expectedResultPos = 0; expectedResultPos < expectedOutputArrArr.length; expectedResultPos++) {
            int idx = rnd.nextInt(expectedOutputArrArr.length);
            //int idx = expectedResultPos;
            train(mlpNet, trainInputArrArr[idx], expectedOutputArrArr[idx], 0.3F, 0.6F);
        }
    }

    public static void train(final MlpNet mlpNet, float[] trainInputArr, float[] expectedOutputArr, float learningRate, float momentum) {
        float[] calcOutputArr = run(mlpNet, trainInputArr);

        trainWithOutput(mlpNet, expectedOutputArr, calcOutputArr, learningRate, momentum);
    }

    public static void trainWithOutput(final MlpNet mlpNet, float[] expectedOutputArr, float[] calcOutputArr, float learningRate, float momentum) {
        float[] errorArr = new float[calcOutputArr.length];

        for (int errorPos = 0; errorPos < errorArr.length; errorPos++) {
            errorArr[errorPos] = expectedOutputArr[errorPos] - calcOutputArr[errorPos]; // negative error
        }
        trainWithError(mlpNet, errorArr, learningRate, momentum);
    }

    public static void trainWithError(final MlpNet mlpNet, float[] errorArr, float learningRate, float momentum) {
        for (int layerPos = mlpNet.layers.length - 1; layerPos >= 0; layerPos--) {
            final MlpLayer mlpLayer = mlpNet.layers[layerPos];
            errorArr = MlpService.train(mlpLayer, errorArr, learningRate, momentum);
        }
    }

    public static float[] run(final MlpNet mlpNet, float[] inputArr) {
        float[] actInputArr = inputArr;
        for (int layerPos = 0; layerPos < mlpNet.layers.length; layerPos++) {
            final MlpLayer mlpLayer = mlpNet.layers[layerPos];
            actInputArr = run(mlpLayer, actInputArr, mlpNet.getUseAdditionalBiasInput());
        }
        return actInputArr;
    }

    public static float[] run(final MlpLayer mlpLayer, final float[] parentLayerInputArr, final boolean useAdditionalBiasInput) {
        //System.arraycopy(parentLayerInputArr, 0, mlpLayer.inputArr, 0, parentLayerInputArr.length);
        for (int inputPos = 0; inputPos < parentLayerInputArr.length; inputPos++) {
            mlpLayer.inputArr[inputPos] = parentLayerInputArr[inputPos];
        }

        if (useAdditionalBiasInput) {
            mlpLayer.inputArr[mlpLayer.inputArr.length - 1] = BIAS_VALUE;
        }

        for (int outputPos = 0; outputPos < mlpLayer.neuronArr.length; outputPos++) {
            final MlpNeuron mlpNeuron = mlpLayer.neuronArr[outputPos];
            mlpNeuron.output = 0;

            for (int inputPos = 0; inputPos < mlpLayer.inputArr.length; inputPos++) {
                mlpNeuron.output += mlpNeuron.weightArr[inputPos] * mlpLayer.inputArr[inputPos];
            }
            if (!mlpLayer.isOutputLayer) {
                mlpNeuron.output = sigmoid(mlpNeuron.output);
            }
        }

        final float[] layerOutputArr = new float[mlpLayer.neuronArr.length];
        for (int outputPos = 0; outputPos < mlpLayer.neuronArr.length; outputPos++) {
            layerOutputArr[outputPos] = mlpLayer.neuronArr[outputPos].output;
        }
        return layerOutputArr;
    }

    public static float[] train(final MlpLayer mlpLayer, float[] errorArr, float learningRate, float momentum) {
        float[] nextError = new float[mlpLayer.inputArr.length];
        for (int outputPos = 0; outputPos < mlpLayer.neuronArr.length; outputPos++) {
            final MlpNeuron mlpNeuron = mlpLayer.neuronArr[outputPos];
            float error = errorArr[outputPos];

            if (!mlpLayer.isOutputLayer) {
                error *= sigmoidDerivative(mlpLayer.neuronArr[outputPos].output);
            }

            for (int inputPos = 0; inputPos < mlpLayer.inputArr.length; inputPos++) {
                nextError[inputPos] += mlpNeuron.weightArr[inputPos] * error;
                float dw = mlpLayer.inputArr[inputPos] * error * learningRate;
                mlpNeuron.weightArr[inputPos] += mlpNeuron.dweightArr[inputPos] * momentum + dw;
                mlpNeuron.dweightArr[inputPos] = dw;
            }
        }
        return nextError;
    }

    private static float sigmoidDerivative(float x) {
        return (x * (NORM_VALUE - x));
    }

    private static float sigmoid(float x) {
        return (float) (NORM_VALUE / (NORM_VALUE + Math.exp(-x)));
    }

    private static float digital(float x) {
        return x >= 0.0D ? NORM_VALUE : -NORM_VALUE;
        //return x >= 0.5D ? NORM_VALUE : 0.0F;
        //return x >= 0.0D ? NORM_VALUE : 0.0F;
        //return x * 0.5F;
    }

}
