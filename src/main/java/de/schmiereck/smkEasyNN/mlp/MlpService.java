package de.schmiereck.smkEasyNN.mlp;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * Learning Rate:
 *  A traditional default value for the learning rate is 0.1 or 0.01
 *  less than 1 and greater than 10^−6
 *  It is common to grid search learning rates on a log scale from 0.1 to 10^-5 or 10^-6.
 *
 * Momentum:
 *  Momentum is set to a value greater than 0.0 and less than one, where common values such as 0.9 and 0.99 are used in practice.
 *  Common values of [momentum] used in practice include .5, .9, and .99.
 */
public final class MlpService {

    public static final float BIAS_VALUE = 1.0F;
    public static final float CLOCK_VALUE = 1.0F;
    public static final float NORM_VALUE = 1.0F;
    public static final int INPUT_LAYER_NR = -1;
    public static final int INTERNAL_LAYER_NR = -2;
    public static final int INTERNAL_BIAS_INPUT_NR = 0;
    public static final int INTERNAL_CLOCK_INPUT_NR = 1;

    private MlpService() {}

    public static float runTrainOrder(final MlpNet mlpNet, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr, final Random rnd) {
        float mainOutputMseErrorValue = 0.0F;
        int mainOutputCount = 0;
        for (int expectedResultPos = 0; expectedResultPos < expectedOutputArrArr.length; expectedResultPos++) {
            int idx = expectedResultPos;
            mainOutputMseErrorValue += train(mlpNet, trainInputArrArr[idx], expectedOutputArrArr[idx], 0.3F, 0.6F);
            mainOutputCount++;
        }
        return mainOutputMseErrorValue / mainOutputCount;
    }

    public static float runTrainRandom(final MlpNet mlpNet, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr, final Random rnd) {
        float mainOutputMseErrorValue = 0.0F;
        int mainOutputCount = 0;
        for (int expectedResultPos = 0; expectedResultPos < expectedOutputArrArr.length; expectedResultPos++) {
            int idx = rnd.nextInt(expectedOutputArrArr.length);
            //int idx = expectedResultPos;
            mainOutputMseErrorValue += train(mlpNet, trainInputArrArr[idx], expectedOutputArrArr[idx], 0.3F, 0.6F);
            mainOutputCount++;
        }
        return mainOutputMseErrorValue / mainOutputCount;
    }

    public static float runTrainRandomOrder(final MlpNet mlpNet, final float[][][] expectedOutputArrArrArr, final float[][][] trainInputArrArrArr, final Random rnd) {
        return runTrainRandomOrder(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr, 0.3F, 0.6F, rnd);
    }

    public static float runTrainRandomOrder(final MlpNet mlpNet, final float[][][] expectedOutputArrArrArr, final float[][][] trainInputArrArrArr,
                                           final float learningRate, final float momentum, final Random rnd) {
        //final float[][] expectedOutputArrArr = expectedOutputArrArrArr[0];
        //final int expectedOutputTrainSize = expectedOutputArrArr.length;

        return runTrainRandomOrder(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr,
                                    Integer.MAX_VALUE,
                                    learningRate, momentum, rnd);
    }

    public static float runTrainRandomOrder(final MlpNet mlpNet, final float[][][] expectedOutputArrArrArr, final float[][][] trainInputArrArrArr,
                                           final int expectedOutputTrainSize,
                                           final float learningRate, final float momentum, final Random rnd) {
        float mainOutputMseErrorValue = 0.0F;
        int mainOutputCount = 0;
        for (int expectedResultArrPos = 0; expectedResultArrPos < expectedOutputArrArrArr.length; expectedResultArrPos++) {
            final int idx = rnd.nextInt(expectedOutputArrArrArr.length);
            final float[][] trainInputArrArr = trainInputArrArrArr[idx];
            final float[][] expectedOutputArrArr = expectedOutputArrArrArr[idx];
            final int trainSize;
            if (expectedOutputTrainSize > expectedOutputArrArr.length) {
                trainSize = expectedOutputArrArr.length;
            } else {
                trainSize = expectedOutputTrainSize;
            }
            //for (int pos = 0; pos < expectedOutputArrArr.length; pos++) {
            //for (int pos = 0; pos < expectedOutputTrainSize; pos++) {
            for (int pos = 0; pos < trainSize; pos++) {
                mainOutputMseErrorValue += train(mlpNet, trainInputArrArr[pos], expectedOutputArrArr[pos], learningRate, momentum);
                mainOutputCount++;
            }
        }
        return mainOutputMseErrorValue / mainOutputCount;
    }

    public static float train(final MlpNet mlpNet, final float[] trainInputArr, final float[] expectedOutputArr, final float learningRate, final float momentum) {
        float[] calcOutputArr = run(mlpNet, trainInputArr);

        return trainWithOutput(mlpNet, expectedOutputArr, calcOutputArr, learningRate, momentum);
    }

    /**
     * @return mittlerer quadratischen Fehler (MSE).
     */
    public static float trainWithOutput(final MlpNet mlpNet, final float[] expectedOutputArr, final float[] calcOutputArr, final float learningRate, final float momentum) {
        float mainOutputMseErrorValue = 0.0F;

        Arrays.stream(mlpNet.layerArr).forEach(layer -> {
            Arrays.stream(layer.neuronArr).forEach(neuron -> {
                neuron.lastErrorValue = 0.0F;
                //neuron.lastError = neuron.error;
                neuron.errorValue = 0.0F;
            });
        });

        final MlpLayer outputLayer = mlpNet.getOutputLayer();

        for (int outputNeuronPos = 0; outputNeuronPos < outputLayer.neuronArr.length; outputNeuronPos++) {
            final MlpNeuron outputNeuron = outputLayer.neuronArr[outputNeuronPos];
            outputNeuron.errorValue = expectedOutputArr[outputNeuronPos] - outputNeuron.outputValue;
            mainOutputMseErrorValue += (outputNeuron.errorValue * outputNeuron.errorValue);
        }

        trainWithError(mlpNet, learningRate, momentum);

        return mainOutputMseErrorValue / outputLayer.neuronArr.length;
    }

    public static void trainWithError(final MlpNet mlpNet, final float learningRate, final float momentum) {
        for (int layerPos = mlpNet.layerArr.length - 1; layerPos >= 0; layerPos--) {
            final MlpLayer mlpLayer = mlpNet.layerArr[layerPos];
            MlpService.trainNetErrorValues(mlpLayer, learningRate, momentum);
            //MlpService.train2(mlpLayer, learningRate, momentum);
        }
        for (int layerPos = mlpNet.layerArr.length - 1; layerPos >= 0; layerPos--) {
            final MlpLayer mlpLayer = mlpNet.layerArr[layerPos];
            //MlpService.train(mlpLayer, learningRate, momentum);
            MlpService.trainNetWeights(mlpLayer, learningRate, momentum);
        }
    }

    public static void trainNetErrorValues(final MlpLayer mlpLayer, final float learningRate, final float momentum) {
        for (int outputPos = 0; outputPos < mlpLayer.neuronArr.length; outputPos++) {
            final MlpNeuron neuron = mlpLayer.neuronArr[outputPos];

            if (!mlpLayer.isOutputLayer) {
                neuron.errorValue *= sigmoidDerivative(mlpLayer.neuronArr[outputPos].outputValue);
            }

            for (int inputPos = 0; inputPos < neuron.synapseList.size(); inputPos++) {
                final MlpSynapse synapse = neuron.synapseList.get(inputPos);

                final MlpInputErrorInterface inputError = synapse.getInputError();
                if (synapse.forward) {
                    if (Objects.nonNull(inputError)) {
                        inputError.addLastErrorValue(synapse.weight * neuron.lastErrorValue);
                        inputError.addErrorValue(synapse.weight * neuron.lastErrorValue);
                    }
                } else {
                    if (Objects.nonNull(inputError)) {
                        inputError.addErrorValue(synapse.weight * neuron.errorValue);
                    }
                }
            }
        }
    }

    public static void trainNetWeights(final MlpLayer mlpLayer, final float learningRate, final float momentum) {
        for (int outputPos = 0; outputPos < mlpLayer.neuronArr.length; outputPos++) {
            final MlpNeuron neuron = mlpLayer.neuronArr[outputPos];

            for (int inputPos = 0; inputPos < neuron.synapseList.size(); inputPos++) {
                final MlpSynapse synapse = neuron.synapseList.get(inputPos);

                final float errorValue;
                final float inputValue;
                if (synapse.forward) {
                    errorValue = neuron.errorValue + neuron.lastErrorValue;
                    //errorValue = neuron.lastErrorValue;
                    //errorValue = neuron.errorValue;
                } else {
                    errorValue = neuron.errorValue;
                }
                if (synapse.useTrainLastInput) {
                    inputValue = synapse.getInput().getLastInputValue();
                } else {
                    inputValue = synapse.getInput().getInputValue();
                }

                float dw = inputValue * errorValue * learningRate;

                synapse.weight += (synapse.dweight * momentum) + dw;
                synapse.dweight = dw;
            }
        }
    }

    public static float[] run(final MlpNet mlpNet, final float[] inputArr) {
        for (int inputPos = 0; inputPos < inputArr.length; inputPos++) {
            mlpNet.setInputValue(inputPos, inputArr[inputPos]);
        }
        if (mlpNet.getUseAdditionalClockInput()) {
            if (mlpNet.clockInput.getInputValue() == CLOCK_VALUE) {
                mlpNet.clockInput.setValue(-CLOCK_VALUE);
            } else {
                mlpNet.clockInput.setValue(CLOCK_VALUE);
            }
        }
        for (int layerPos = 0; layerPos < mlpNet.layerArr.length; layerPos++) {
            final MlpLayer mlpLayer = mlpNet.layerArr[layerPos];
            runLayer(mlpLayer);
        }

        final MlpLayer outputLayer = mlpNet.getOutputLayer();
        final float[] layerOutputArr = mlpNet.getLayerOutputArr();
        for (int outputPos = 0; outputPos < outputLayer.neuronArr.length; outputPos++) {
            layerOutputArr[outputPos] = outputLayer.neuronArr[outputPos].outputValue;
        }
        return layerOutputArr;
    }

    public static void runLayer(final MlpLayer mlpLayer) {
        for (int outputPos = 0; outputPos < mlpLayer.neuronArr.length; outputPos++) {
            final MlpNeuron neuron = mlpLayer.neuronArr[outputPos];
            neuron.lastOutputValue = neuron.outputValue;
            neuron.outputValue = 0.0F;

            for (int inputPos = 0; inputPos < neuron.synapseList.size(); inputPos++) {
                final MlpSynapse synapse = neuron.synapseList.get(inputPos);

                final float inputValue;
                if (synapse.useLastInput) {
                    inputValue = synapse.getInput().getLastInputValue();
                } else {
                    inputValue = synapse.getInput().getInputValue();
                }
                neuron.outputValue += synapse.weight * inputValue;
            }
            if (!mlpLayer.isOutputLayer) {
                neuron.outputValue = sigmoid(neuron.outputValue);
            }
        }
    }

    static float sigmoidDerivative(final float x) {
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
