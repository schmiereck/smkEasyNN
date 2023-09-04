package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpLayer.calcInitWeight;
import static de.schmiereck.smkEasyNN.mlp.MlpLayer.calcInitWeight2;

import java.util.Arrays;
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

        Arrays.stream(mlpNet.layers).forEach(layer -> {
            Arrays.stream(layer.neuronArr).forEach(neuron -> neuron.error = 0.0F);
        });
        Arrays.stream(mlpNet.biasNeuronArr).forEach(neuron -> neuron.error = 0.0F);

        final MlpLayer outputLayer = mlpNet.getOutputLayer();

        for (int outputNeuronPos = 0; outputNeuronPos < outputLayer.neuronArr.length; outputNeuronPos++) {
            final MlpNeuron outputNeuron = outputLayer.neuronArr[outputNeuronPos];
            outputNeuron.error = expectedOutputArr[outputNeuronPos] - outputNeuron.output;
        }

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

    public static float[] train(final MlpLayer mlpLayer, final float[] errorArr, final float learningRate, final float momentum) {
        float[] nextError = new float[mlpLayer.inputArr.size()];

        for (int outputPos = 0; outputPos < mlpLayer.neuronArr.length; outputPos++) {
            final MlpNeuron neuron = mlpLayer.neuronArr[outputPos];
            //float error = errorArr[outputPos];
            float error2 = neuron.error;

            if (!mlpLayer.isOutputLayer) {
                //error *= sigmoidDerivative(mlpLayer.neuronArr[outputPos].output);
                error2 *= sigmoidDerivative(mlpLayer.neuronArr[outputPos].output);
            }

            for (int inputPos = 0; inputPos < mlpLayer.inputArr.size(); inputPos++) {
                final MlpSynapse synapse = neuron.synapseList.get(inputPos);

                //nextError[inputPos] += neuron.weightArr[inputPos] * error;
                synapse.input.addError(synapse.weight * error2);

                //float dw = mlpLayer.inputArr.get(inputPos).getInput() * error * learningRate;
                float dw2 = synapse.input.getInput() * error2 * learningRate;

                //neuron.weightArr[inputPos] += neuron.dweightArr[inputPos] * momentum + dw;
                //neuron.dweightArr[inputPos] = dw;
                synapse.weight += synapse.dweight * momentum + dw2;
                if (Float.isNaN(synapse.weight))
                    throw new RuntimeException();
                synapse.dweight = dw2;
            }
        }
        return nextError;
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
            final MlpNeuron neuron = mlpLayer.neuronArr[outputPos];
            neuron.output = 0;
            //neuron.output2 = 0;

            for (int inputPos = 0; inputPos < mlpLayer.inputArr.size(); inputPos++) {
                //neuron.output += neuron.weightArr[inputPos] * mlpLayer.inputArr.get(inputPos).getInput();
                final MlpSynapse synapse = neuron.synapseList.get(inputPos);
                neuron.output += synapse.weight * synapse.input.getInput();
                if (Float.isNaN(neuron.output))
                    throw new RuntimeException();
            }
            if (!mlpLayer.isOutputLayer) {
                //neuron.output = sigmoid(neuron.output);
                neuron.output = sigmoid(neuron.output);
            }
        }
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
                final MlpSynapse synapse = new MlpSynapse();
                synapse.input = new MlpInput(fromNeuron);
                synapse.weight = calcInitWeight2(rnd);
                toNeuron.synapseList.add(synapse);
            });
            if (mlpNet.getUseAdditionalBiasInput()) {
                final MlpSynapse synapse = new MlpSynapse();
                synapse.input = new MlpInput(mlpNet.biasNeuronArr[toLayerPos]);
                synapse.weight = calcInitWeight2(rnd);
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

                final MlpSynapse synapse = new MlpSynapse();
                synapse.input = new MlpInput(fromNeuron);
                synapse.weight = calcInitWeight2(rnd);
                toNeuron.synapseList.add(synapse);
            }

            if (mlpNet.getUseAdditionalBiasInput()) {
                final MlpSynapse synapse = new MlpSynapse();
                synapse.input = new MlpInput(mlpNet.biasNeuronArr[layerPos]);
                synapse.weight = calcInitWeight2(rnd);
                toNeuron.synapseList.add(synapse);
            }
        }
    }

}
