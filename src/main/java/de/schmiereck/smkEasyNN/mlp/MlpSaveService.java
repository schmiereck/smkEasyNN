package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpService.CLOCK_VALUE;
import static de.schmiereck.smkEasyNN.mlp.MlpService.NORM_VALUE;
import static de.schmiereck.smkEasyNN.mlp.MlpService.run;
import static de.schmiereck.smkEasyNN.mlp.MlpService.sigmoidDerivative;

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
        Arrays.stream(mlpNet.layers).forEach(layer -> {
            Arrays.stream(layer.neuronArr).forEach(neuron -> {
                neuron.lastError = 0.0F;
                //neuron.lastError = neuron.error;
                neuron.error = 0.0F;
            });
        });
        Arrays.stream(mlpNet.biasNeuronArr).forEach(neuron -> {
            neuron.lastError = 0.0F;
            //neuron.lastError = neuron.error;
            neuron.error = 0.0F;
        });
        mlpNet.clockNeuron.lastError = 0.0F;
        mlpNet.clockNeuron.error = 0.0F;

        final MlpLayer outputLayer = mlpNet.getOutputLayer();

        for (int outputNeuronPos = 0; outputNeuronPos < outputLayer.neuronArr.length; outputNeuronPos++) {
            final MlpNeuron outputNeuron = outputLayer.neuronArr[outputNeuronPos];
            outputNeuron.error = expectedOutputArr[outputNeuronPos] - outputNeuron.output;
            if (Float.isNaN(outputNeuron.error)) {
                throw new RuntimeException("trainWithOutput outputNeuron.error NaN:");
            }
        }

        trainWithError(mlpNet, learningRate, momentum);
    }

    public static void trainWithError(final MlpNet mlpNet, final float learningRate, final float momentum) {
        for (int layerPos = mlpNet.layers.length - 1; layerPos >= 0; layerPos--) {
            final MlpLayer mlpLayer = mlpNet.layers[layerPos];
            train(mlpLayer, learningRate, momentum);
            //MlpService.train2(mlpLayer, learningRate, momentum);
        }
        for (int layerPos = mlpNet.layers.length - 1; layerPos >= 0; layerPos--) {
            final MlpLayer mlpLayer = mlpNet.layers[layerPos];
            //MlpService.train(mlpLayer, learningRate, momentum);
            train2(mlpLayer, learningRate, momentum);
        }
    }

    public static void train(final MlpLayer mlpLayer, final float learningRate, final float momentum) {
        for (int outputPos = 0; outputPos < mlpLayer.neuronArr.length; outputPos++) {
            final MlpNeuron neuron = mlpLayer.neuronArr[outputPos];
            if (Float.isNaN(neuron.error)) {
                throw new RuntimeException("train neuron.error NaN:");
            }
            if (Float.isInfinite(neuron.error)) {
                throw new RuntimeException("train neuron.error Infinite:");
            }

            if (!mlpLayer.isOutputLayer) {
                final float sd = sigmoidDerivative(mlpLayer.neuronArr[outputPos].output);
                final float oe = neuron.error;
                neuron.error *= sd;
                if (Float.isNaN(neuron.error)) {
                    throw new RuntimeException("train neuron.error NaN:" + mlpLayer.neuronArr[outputPos].output + ", " + oe);
                }
                if (Float.isInfinite(neuron.error)) {
                    throw new RuntimeException("train neuron.error Infinite:" + mlpLayer.neuronArr[outputPos].output + ", " + oe);
                }
            }

            for (int inputPos = 0; inputPos < neuron.synapseList.size(); inputPos++) {
                final MlpSynapse synapse = neuron.synapseList.get(inputPos);

                if (synapse.forward) {
                    if (Objects.nonNull(synapse.inputError)) {
                        synapse.inputError.addLastError(synapse.weight * neuron.lastError);
                        synapse.inputError.addError(synapse.weight * neuron.lastError);
                    }
                } else {
                    if (Objects.nonNull(synapse.inputError)) {
                        final float e = synapse.weight * neuron.error;
                        if (Float.isInfinite(e)) {
                            throw new RuntimeException("train e Infinite:" + synapse.weight + ", " + neuron.error);
                        }
                        synapse.inputError.addError(e);
                    }
                }
                if (Float.isNaN(neuron.error)) {
                    throw new RuntimeException("train neuron.error NaN:" + mlpLayer.neuronArr[outputPos].output + ", ");
                }
            }
        }
    }

    public static void train2(final MlpLayer mlpLayer, final float learningRate, final float momentum) {
        for (int outputPos = 0; outputPos < mlpLayer.neuronArr.length; outputPos++) {
            final MlpNeuron neuron = mlpLayer.neuronArr[outputPos];

            for (int inputPos = 0; inputPos < neuron.synapseList.size(); inputPos++) {
                final MlpSynapse synapse = neuron.synapseList.get(inputPos);

                final float error;
                final float input;
                if (synapse.forward) {
                    error = neuron.error + neuron.lastError;
                    //error = neuron.lastError;
                    //error = neuron.error;
                    input = synapse.input.getLastInput();
                } else {
                    error = neuron.error;
                    input = synapse.input.getInput();
                }

                float dw = input * error * learningRate;

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
            if (mlpNet.clockNeuron.output == CLOCK_VALUE) {
                mlpNet.clockNeuron.output = -CLOCK_VALUE;
            } else {
                mlpNet.clockNeuron.output = CLOCK_VALUE;
            }
        }
        for (int layerPos = 0; layerPos < mlpNet.layers.length; layerPos++) {
            final MlpLayer mlpLayer = mlpNet.layers[layerPos];
            runLayer(mlpLayer);
        }

        final MlpLayer outputLayer = mlpNet.getOutputLayer();
        final float[] layerOutputArr = new float[outputLayer.neuronArr.length];
        for (int outputPos = 0; outputPos < outputLayer.neuronArr.length; outputPos++) {
            layerOutputArr[outputPos] = outputLayer.neuronArr[outputPos].output;
        }
        return layerOutputArr;
    }

    public static void runLayer(final MlpLayer mlpLayer) {
        for (int outputPos = 0; outputPos < mlpLayer.neuronArr.length; outputPos++) {
            final MlpNeuron neuron = mlpLayer.neuronArr[outputPos];
            neuron.lastOutput = neuron.output;
            neuron.output = 0.0F;

            for (int inputPos = 0; inputPos < neuron.synapseList.size(); inputPos++) {
                final MlpSynapse synapse = neuron.synapseList.get(inputPos);

                final float input = synapse.input.getInput();

                neuron.output += synapse.weight * input;
                if (Float.isNaN(neuron.output)) {
                    throw new RuntimeException("runLayer neuron.output NaN:" + synapse.weight + ", " + input);
                }
            }
            if (!mlpLayer.isOutputLayer) {
                neuron.output = sigmoid(neuron.output);
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
