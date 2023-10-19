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
    public static final int FIRST_LAYER_NR = 0;
    public static final int INTERNAL_LAYER_NR = -2;
    public static final int INTERNAL_INPUT_LAYER_NR = -3;
    public static final int INTERNAL_BIAS_INPUT_NR = 0;
    public static final int INTERNAL_CLOCK_INPUT_NR = 1;

    private MlpService() {}

    public static float[] run(final MlpNet net, final float[] inputArr) {
        //for (int inputPos = 0; inputPos < inputArr.length; inputPos++) {
        for (int inputPos = 0; inputPos < net.getValueInputArr().length; inputPos++) {
            net.setInputValue(inputPos, inputArr[inputPos]);
        }

        final MlpInternalValueInput[] internalValueInputArr = net.getInternalValueInputArr();
        if (Objects.nonNull(internalValueInputArr)) {
            for (int inputPos = 0; inputPos < internalValueInputArr.length; inputPos++) {
                final MlpInternalValueInput valueInput = net.getInternalInputValue(inputPos);
                final MlpNeuron neuron = getNeuron(net, valueInput.inputLayerNr, valueInput.inputNeuronNr);
                net.setInternalInputValue(inputPos, neuron.getInputValue());
            }
        }
        if (net.getUseAdditionalClockInput()) {
            if (net.clockInput.getInputValue() == CLOCK_VALUE) {
                net.clockInput.setValue(-CLOCK_VALUE);
            } else {
                net.clockInput.setValue(CLOCK_VALUE);
            }
        }
        for (int layerPos = 0; layerPos < net.layerArr.length; layerPos++) {
            final MlpLayer layer = net.layerArr[layerPos];
            runLayer(layer);
        }

        final MlpLayer outputLayer = net.getOutputLayer();
        final float[] layerOutputArr = net.getLayerOutputArr();
        for (int outputPos = 0; outputPos < outputLayer.neuronArr.length; outputPos++) {
            layerOutputArr[outputPos] = outputLayer.neuronArr[outputPos].outputValue;
        }
        return layerOutputArr;
    }

    public static void runLayer(final MlpLayer layer) {
        for (int outputPos = 0; outputPos < layer.neuronArr.length; outputPos++) {
            final MlpNeuron neuron = layer.neuronArr[outputPos];
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
            if (!layer.isOutputLayer) {
                neuron.outputValue = sigmoid(neuron.outputValue);
            }
        }
    }

    public static float runTrainOrder(final MlpNet net, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr, final Random rnd) {
        float mainOutputMseErrorValue = 0.0F;
        int mainOutputCount = 0;
        for (int expectedResultPos = 0; expectedResultPos < expectedOutputArrArr.length; expectedResultPos++) {
            final int idx = expectedResultPos;
            mainOutputMseErrorValue += train(net, trainInputArrArr[idx], expectedOutputArrArr[idx], 0.3F, 0.6F);
            mainOutputCount++;
        }
        return mainOutputMseErrorValue / mainOutputCount;
    }

    public static float runTrainRandom(final MlpNet net, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr, final Random rnd) {
        return runTrainRandom(net, expectedOutputArrArr, trainInputArrArr, 0.3F, 0.6F, rnd);
    }

    public static float runTrainRandom(final MlpNet net, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                                       final float learningRate, final float momentum, final Random rnd) {
        float mainOutputMseErrorValue = 0.0F;
        int mainOutputCount = 0;
        for (int expectedResultPos = 0; expectedResultPos < expectedOutputArrArr.length; expectedResultPos++) {
            int idx = rnd.nextInt(expectedOutputArrArr.length);
            //int idx = expectedResultPos;
            mainOutputMseErrorValue += train(net, trainInputArrArr[idx], expectedOutputArrArr[idx], learningRate, momentum);
            mainOutputCount++;
        }
        return mainOutputMseErrorValue / mainOutputCount;
    }

    public static float runTrainRandomOrder(final MlpNet net, final float[][][] expectedOutputArrArrArr, final float[][][] trainInputArrArrArr, final Random rnd) {
        return runTrainRandomOrder(net, expectedOutputArrArrArr, trainInputArrArrArr, 0.3F, 0.6F, rnd);
    }

    public static float runTrainRandomOrder(final MlpNet net, final float[][][] expectedOutputArrArrArr, final float[][][] trainInputArrArrArr,
                                           final float learningRate, final float momentum, final Random rnd) {
        //final float[][] expectedOutputArrArr = expectedOutputArrArrArr[0];
        //final int expectedOutputTrainSize = expectedOutputArrArr.length;

        return runTrainRandomOrder(net, expectedOutputArrArrArr, trainInputArrArrArr,
                                    Integer.MAX_VALUE,
                                    learningRate, momentum, rnd);
    }

    public static float runTrainRandomOrder(final MlpNet net, final float[][][] expectedOutputArrArrArr, final float[][][] trainInputArrArrArr,
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
            //resetNetOutputs(net);
            //for (int pos = 0; pos < expectedOutputArrArr.length; pos++) {
            //for (int pos = 0; pos < expectedOutputTrainSize; pos++) {
            for (int pos = 0; pos < trainSize; pos++) {
                mainOutputMseErrorValue += train(net, trainInputArrArr[pos], expectedOutputArrArr[pos], learningRate, momentum);
                mainOutputCount++;
            }
        }
        return mainOutputMseErrorValue / mainOutputCount;
    }

    public static float train(final MlpNet net, final float[] trainInputArr, final float[] expectedOutputArr, final float learningRate, final float momentum) {
        final float[] calcOutputArr = run(net, trainInputArr);

        return trainWithOutput(net, expectedOutputArr, calcOutputArr, learningRate, momentum);
    }

    public static float trainWithOutput(final MlpNet net, final float[] expectedOutputArr, final float[] calcOutputArr,
                                        final float learningRate, final float momentum) {

        final float mse = trainNetErrorValues(net, learningRate, momentum, expectedOutputArr);

        trainNetWeights(net, learningRate, momentum);

        return mse;
    }

    /**
     * @return mittlerer quadratischen Fehler (MSE).
     */
    static float trainNetErrorValues(final MlpNet net, final float learningRate, final float momentum,
                                     final float[] expectedOutputArr) {
        float mainOutputMseErrorValue = 0.0F;

        Arrays.stream(net.layerArr).forEach(layer -> {
            Arrays.stream(layer.neuronArr).forEach(neuron -> {
                neuron.lastErrorValue = 0.0F;
                //neuron.lastError = neuron.error;
                neuron.errorValue = 0.0F;
            });
        });

        final MlpLayer outputLayer = net.getOutputLayer();

        for (int outputNeuronPos = 0; outputNeuronPos < outputLayer.neuronArr.length; outputNeuronPos++) {
            final MlpNeuron outputNeuron = outputLayer.neuronArr[outputNeuronPos];
            outputNeuron.errorValue = expectedOutputArr[outputNeuronPos] - outputNeuron.outputValue;
            mainOutputMseErrorValue += (outputNeuron.errorValue * outputNeuron.errorValue);
        }

        for (int layerPos = net.layerArr.length - 1; layerPos >= 0; layerPos--) {
            final MlpLayer layer = net.layerArr[layerPos];
            MlpService.trainLayerErrorValues(layer, learningRate, momentum);
        }

        return mainOutputMseErrorValue / outputLayer.neuronArr.length;
    }

    public static void trainLayerErrorValues(final MlpLayer layer, final float learningRate, final float momentum) {
        for (int outputPos = 0; outputPos < layer.neuronArr.length; outputPos++) {
            final MlpNeuron neuron = layer.neuronArr[outputPos];

            if (!layer.isOutputLayer) {
                neuron.errorValue *= sigmoidDerivative(layer.neuronArr[outputPos].outputValue);
            }

            for (int inputPos = 0; inputPos < neuron.synapseList.size(); inputPos++) {
                final MlpSynapse synapse = neuron.synapseList.get(inputPos);

                final MlpInputErrorInterface inputError = synapse.getInputError();
                if (synapse.useLastError) {
                    if (Objects.nonNull(inputError)) {
                        inputError.addLastErrorValue(synapse.weight * neuron.lastErrorValue);
                        inputError.addErrorValue(synapse.weight * neuron.errorValue);
                    }
                } else {
                    if (Objects.nonNull(inputError)) {
                        inputError.addErrorValue(synapse.weight * neuron.errorValue);
                    }
                }
            }
        }
    }

    static void trainNetWeights(final MlpNet net, final float learningRate, final float momentum) {
        for (int layerPos = net.layerArr.length - 1; layerPos >= 0; layerPos--) {
            final MlpLayer layer = net.layerArr[layerPos];
            trainLayerWeights(layer, learningRate, momentum);
        }
    }

    public static void trainLayerWeights(final MlpLayer layer, final float learningRate, final float momentum) {
        for (int outputPos = 0; outputPos < layer.neuronArr.length; outputPos++) {
            final MlpNeuron neuron = layer.neuronArr[outputPos];

            for (int inputPos = 0; inputPos < neuron.synapseList.size(); inputPos++) {
                final MlpSynapse synapse = neuron.synapseList.get(inputPos);

                final float errorValue;
                final float inputValue;
                if (synapse.useLastError) {
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

    private static MlpNeuron getNeuron(final MlpNet net, final int inputLayerNr, final int inputNeuronNr) {
        return net.getLayerArr()[inputLayerNr].neuronArr[inputNeuronNr];
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
