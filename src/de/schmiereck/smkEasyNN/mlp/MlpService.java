package de.schmiereck.smkEasyNN.mlp;

public class MlpService {

    public static final float BIAS_VALUE = 1.0F;
    public static final float NORM_VALUE = 1.0F;

    public static void train(final MlpNet mlpNet, float[] inputArr, float[] targetOutputArr, float learningRate, float momentum) {
        float[] calcOut = run(mlpNet, inputArr);

        float[] errorArr = new float[calcOut.length];

        for (int errorPos = 0; errorPos < errorArr.length; errorPos++) {
            errorArr[errorPos] = targetOutputArr[errorPos] - calcOut[errorPos]; // negative error
        }
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

}
