package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpService.CLOCK_VALUE;
import static de.schmiereck.smkEasyNN.mlp.MlpService.NORM_VALUE;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class MlpSaveService {

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
            if (Float.isNaN(outputNeuron.errorValue)) {
                throw new RuntimeException("trainWithOutput outputNeuron.error NaN:");
            }
        }

        trainWithError(mlpNet, learningRate, momentum);
    }

    public static void trainWithError(final MlpNet mlpNet, final float learningRate, final float momentum) {
        for (int layerPos = mlpNet.layerArr.length - 1; layerPos >= 0; layerPos--) {
            final MlpLayer mlpLayer = mlpNet.layerArr[layerPos];
            train(mlpLayer, learningRate, momentum);
            //MlpService.train2(mlpLayer, learningRate, momentum);
        }
        for (int layerPos = mlpNet.layerArr.length - 1; layerPos >= 0; layerPos--) {
            final MlpLayer mlpLayer = mlpNet.layerArr[layerPos];
            //MlpService.train(mlpLayer, learningRate, momentum);
            train2(mlpLayer, learningRate, momentum);
        }
    }

    public static void train(final MlpLayer mlpLayer, final float learningRate, final float momentum) {
        for (int outputPos = 0; outputPos < mlpLayer.neuronArr.length; outputPos++) {
            final MlpNeuron neuron = mlpLayer.neuronArr[outputPos];
            if (Float.isNaN(neuron.errorValue)) {
                throw new RuntimeException("train neuron.error NaN:");
            }
            if (Float.isInfinite(neuron.errorValue)) {
                throw new RuntimeException("train neuron.error Infinite:");
            }

            if (!mlpLayer.isOutputLayer) {
                final float sd = sigmoidDerivative(mlpLayer.neuronArr[outputPos].outputValue);
                final float oe = neuron.errorValue;
                neuron.errorValue *= sd;
                if (Float.isNaN(neuron.errorValue)) {
                    throw new RuntimeException("train neuron.error NaN:" + mlpLayer.neuronArr[outputPos].outputValue + ", " + oe);
                }
                if (Float.isInfinite(neuron.errorValue)) {
                    throw new RuntimeException("train neuron.error Infinite:" + mlpLayer.neuronArr[outputPos].outputValue + ", " + oe);
                }
            }

            for (int inputPos = 0; inputPos < neuron.synapseList.size(); inputPos++) {
                final MlpSynapse synapse = neuron.synapseList.get(inputPos);

                if (synapse.forward) {
                    if (Objects.nonNull(synapse.getInputError())) {
                        synapse.getInputError().addLastErrorValue(synapse.weight * neuron.lastErrorValue);
                        synapse.getInputError().addErrorValue(synapse.weight * neuron.lastErrorValue);
                    }
                } else {
                    if (Objects.nonNull(synapse.getInputError())) {
                        final float e = synapse.weight * neuron.errorValue;
                        if (Float.isInfinite(e)) {
                            throw new RuntimeException("train e Infinite:" + synapse.weight + ", " + neuron.errorValue);
                        }
                        synapse.getInputError().addErrorValue(e);
                    }
                }
                if (Float.isNaN(neuron.errorValue)) {
                    throw new RuntimeException("train neuron.error NaN:" + mlpLayer.neuronArr[outputPos].outputValue + ", ");
                }
            }
        }
    }

    public static void train2(final MlpLayer mlpLayer, final float learningRate, final float momentum) {
        for (int outputPos = 0; outputPos < mlpLayer.neuronArr.length; outputPos++) {
            final MlpNeuron neuron = mlpLayer.neuronArr[outputPos];

            for (int inputPos = 0; inputPos < neuron.synapseList.size(); inputPos++) {
                final MlpSynapse synapse = neuron.synapseList.get(inputPos);

                final float errorValue;
                final float inputValue;
                if (synapse.forward) {
                    errorValue = neuron.errorValue + neuron.lastErrorValue;
                    //errorValue = neuron.lastError;
                    //errorValue = neuron.errorValue;
                    inputValue = synapse.getInput().getLastInputValue();
                } else {
                    errorValue = neuron.errorValue;
                    inputValue = synapse.getInput().getInputValue();
                }

                float dw = inputValue * errorValue * learningRate;

                synapse.weight += (synapse.dweight * momentum) + dw;
                if (Float.isNaN(synapse.weight)) {
                    throw new RuntimeException("train2 synapse.weight NaN:" + synapse.dweight + ", " + momentum + ", " + dw);
                }
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
        final float[] layerOutputArr = new float[outputLayer.neuronArr.length];
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

                final float inputValue = synapse.getInput().getInputValue();

                neuron.outputValue += synapse.weight * inputValue;
                if (Float.isNaN(neuron.outputValue)) {
                    throw new RuntimeException("runLayer neuron.output NaN:" + synapse.weight + ", " + inputValue);
                }
            }
            if (!mlpLayer.isOutputLayer) {
                neuron.outputValue = sigmoid(neuron.outputValue);
            }
        }
    }

    static float sigmoidDerivative(final float x) {
        final float ret = (x * (NORM_VALUE - x));
        if (Float.isInfinite(ret)) {
            throw new RuntimeException("sigmoidDerivative Infinite:" + x);
        }
        return ret;
    }


    private static float sigmoid(final float x) {
        final float ret = (float) (NORM_VALUE / (NORM_VALUE + Math.exp(-x)));
        if (Float.isNaN(ret)) {
            throw new RuntimeException("sigmoid NaN:" + x);
        }
        return ret;
    }
}
