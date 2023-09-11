package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpLayer.calcInitWeight2;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class MlpService {

    public static final float BIAS_VALUE = 1.0F;
    public static final float CLOCK_VALUE = 1.0F;
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
        Arrays.stream(mlpNet.layers).forEach(layer -> {
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
        }

        trainWithError(mlpNet, learningRate, momentum);
    }

    public static void trainWithError(final MlpNet mlpNet, final float learningRate, final float momentum) {
        for (int layerPos = mlpNet.layers.length - 1; layerPos >= 0; layerPos--) {
            final MlpLayer mlpLayer = mlpNet.layers[layerPos];
            MlpService.train(mlpLayer, learningRate, momentum);
            //MlpService.train2(mlpLayer, learningRate, momentum);
        }
        for (int layerPos = mlpNet.layers.length - 1; layerPos >= 0; layerPos--) {
            final MlpLayer mlpLayer = mlpNet.layers[layerPos];
            //MlpService.train(mlpLayer, learningRate, momentum);
            MlpService.train2(mlpLayer, learningRate, momentum);
        }
    }

    public static void train(final MlpLayer mlpLayer, final float learningRate, final float momentum) {
        for (int outputPos = 0; outputPos < mlpLayer.neuronArr.length; outputPos++) {
            final MlpNeuron neuron = mlpLayer.neuronArr[outputPos];

            if (!mlpLayer.isOutputLayer) {
                neuron.errorValue *= sigmoidDerivative(mlpLayer.neuronArr[outputPos].outputValue);
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
                        synapse.getInputError().addErrorValue(synapse.weight * neuron.errorValue);
                    }
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
        for (int layerPos = 0; layerPos < mlpNet.layers.length; layerPos++) {
            final MlpLayer mlpLayer = mlpNet.layers[layerPos];
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

    /**
     * RNN
     *
     * Neuronales Netz · Seite · HOOU
     * https://www.hoou.de/projects/neuronale-netze-kurz-erklart/pages/neuronales-netz
     *
     * Rekurrentes neuronales Netz – Wikipedia
     * https://de.wikipedia.org/wiki/Rekurrentes_neuronales_Netz
     *
     * Long Short-Term Memory Units (kurz: LSTMs)
     *
     * Aufbau einer LSTM-Zelle
     * https://www.bigdata-insider.de/was-ist-ein-long-short-term-memory-a-774848/
     *
     * // 0
     * // 1 to   <---,
     * // 2 from ----'
     */
    public static void addForwwardInputs(final MlpNet mlpNet, final int fromLayerPos, final int toLayerPos, final Random rnd) {
        final MlpLayer fromLayer = mlpNet.getLayer(fromLayerPos);
        final MlpLayer toLayer = mlpNet.getLayer(toLayerPos);

        Arrays.stream(toLayer.neuronArr).forEach(toNeuron -> {
            Arrays.stream(fromLayer.neuronArr).forEach(fromNeuron -> {
                final MlpSynapse synapse = new MlpSynapse(fromNeuron, fromNeuron, true);
                synapse.weight = calcInitWeight2(mlpNet.getInitialWeightValue(), rnd);
                toNeuron.synapseList.add(synapse);
            });
            if (mlpNet.getUseAdditionalBiasInput()) {
                final MlpInputInterface biasInput = mlpNet.biasInputArr[toLayerPos];
                final MlpSynapse synapse = new MlpSynapse(biasInput, null, true);
                synapse.weight = calcInitWeight2(mlpNet.getInitialWeightValue(), rnd);
                toNeuron.synapseList.add(synapse);
            }
        });
    }

    public static void addInternalInputs(final MlpNet mlpNet, final int layerPos, final Random rnd) {
        final MlpLayer toLayer = mlpNet.getLayer(layerPos);

        for (int toNeuronPos = 0; toNeuronPos < toLayer.neuronArr.length; toNeuronPos++) {
            final MlpNeuron toNeuron = toLayer.neuronArr[toNeuronPos];

            for (int fromNeuronPos = toNeuronPos; fromNeuronPos < toLayer.neuronArr.length; fromNeuronPos++) {
                final MlpNeuron fromNeuron = toLayer.neuronArr[fromNeuronPos];

                final MlpSynapse synapse = new MlpSynapse(fromNeuron, fromNeuron, true);
                synapse.weight = calcInitWeight2(mlpNet.getInitialWeightValue(), rnd);
                toNeuron.synapseList.add(synapse);
            }

            if (mlpNet.getUseAdditionalBiasInput()) {
                final MlpInputInterface biasInput = mlpNet.biasInputArr[layerPos];
                final MlpSynapse synapse = new MlpSynapse(biasInput, null, true);
                synapse.weight = calcInitWeight2(mlpNet.getInitialWeightValue(), rnd);
                toNeuron.synapseList.add(synapse);
            }
        }
    }

}
