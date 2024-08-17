package de.schmiereck.smkEasyNN.genNet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GenNetService {
    final static Random rnd = new Random();

    public static GenNet createNet(final int[] layerSizeArr, final Random rnd) {
        final GenNet genNet = new GenNet();
        final List<GenNeuron[]> neuronLayerList = new ArrayList<>(layerSizeArr.length);
        final float initBias = 0.0F;

        // Inputs:
        final int inputSize = layerSizeArr[0];
        final GenNeuron[] inputNeuronArr = new GenNeuron[inputSize];
        neuronLayerList.add(inputNeuronArr);
        for (int inputPos = 0; inputPos < inputSize; inputPos++) {
            final GenNeuron inGenNeuron = new GenNeuron(GenNeuron.NeuronType.Input, initBias);
            GenNetService.submitNewNeuron(genNet, inGenNeuron);
            inputNeuronArr[inputPos] = inGenNeuron;
        }

        // Hidden:
        final int hiddenLayerSize = layerSizeArr.length - 2;
        for (int hiddenLayerPos = 0; hiddenLayerPos < hiddenLayerSize; hiddenLayerPos++) {
            final int hiddenSize = layerSizeArr[hiddenLayerPos + 1];
            final GenNeuron[] hiddenNeuronArr = new GenNeuron[hiddenSize];
            neuronLayerList.add(hiddenNeuronArr);
            for (int hiddenPos = 0; hiddenPos < hiddenSize; hiddenPos++) {
                final GenNeuron hiddenGenNeuron = new GenNeuron(GenNeuron.NeuronType.Hidden, initBias);
                //final GenNeuron hiddenGenNeuron = new GenNeuron(GenNeuron.NeuronType.Hidden, rnd.nextFloat(-0.25F, 0.25F));
                GenNetService.submitNewNeuron(genNet, hiddenGenNeuron);
                hiddenNeuronArr[hiddenPos] = hiddenGenNeuron;
            }
        }

        // Outputs:
        final int outputSize = layerSizeArr[layerSizeArr.length - 1];
        final GenNeuron[] outputNeuronArr = new GenNeuron[outputSize];
        neuronLayerList.add(outputNeuronArr);
        for (int outputPos = 0; outputPos < outputSize; outputPos++) {
            final GenNeuron outGenNeuron = new GenNeuron(GenNeuron.NeuronType.Output, initBias);
            GenNetService.submitNewNeuron(genNet, outGenNeuron);
            outputNeuronArr[outputPos] = outGenNeuron;
        }

        // Synapses:
        for (int layerPos = 0; layerPos < neuronLayerList.size() - 1; layerPos++) {
            final GenNeuron[] sourceLayerNeuronArr = neuronLayerList.get(layerPos);

            for (int inputPos = 0; inputPos < sourceLayerNeuronArr.length; inputPos++) {
                final GenNeuron inputNeuron = sourceLayerNeuronArr[inputPos];

                final GenNeuron[] targetLayerNeuronArr = neuronLayerList.get(layerPos + 1);
                for (int targetPos = 0; targetPos < targetLayerNeuronArr.length; targetPos++) {
                    final GenNeuron targetNeuron = targetLayerNeuronArr[targetPos];
                    createGenNetSynapse(inputNeuron, targetNeuron, rnd);
                    //createGenNetSynapse(inputNeuron, targetNeuron, rnd);
                }
            }
        }
        return genNet;
    }

    static void createGenNetSynapse(final GenNeuron inGenNeuron, final GenNeuron outGenNeuron, final Random rnd) {
        createGenNetSynapse(inGenNeuron, outGenNeuron, rnd.nextFloat(1.0F) - 0.5F);
    }

    public static void createGenNetSynapse(final GenNeuron inGenNeuron, final GenNeuron outGenNeuron, final float weight) {
        final GenSynapse genSynapse = new GenSynapse(inGenNeuron, weight);
        if (Objects.isNull(outGenNeuron.inputSynapseList)) {
            outGenNeuron.inputSynapseList = new ArrayList<>();
        }
        outGenNeuron.inputSynapseList.add(genSynapse);
    }

    public static void submitNewNeuron(final GenNet genNet, final GenNeuron genNeuron) {
        switch (genNeuron.neuronType) {
            case Input -> genNet.inputNeuronList.add(genNeuron);
            case Output -> genNet.outputNeuronList.add(genNeuron);
        }
        genNeuron.neuronIndex = genNet.neuronList.size();
        genNet.neuronList.add(genNeuron);
    }

    public static void submitNewNeuron(final GenNet genNet, final int insertNeuronPos, final GenNeuron genNeuron) {
        switch (genNeuron.neuronType) {
            case Input -> genNet.inputNeuronList.add(genNeuron);
            case Output -> genNet.outputNeuronList.add(genNeuron);
        }
        genNeuron.neuronIndex = insertNeuronPos;
        genNet.neuronList.add(insertNeuronPos, genNeuron);
        for (int neuronPos = insertNeuronPos + 1; neuronPos < genNet.neuronList.size(); neuronPos++) {
            genNet.neuronList.get(neuronPos).neuronIndex = neuronPos;
        }
    }

    public static GenNeuron retrieveNeuron(final GenNet genNet, final int pos) {
        return genNet.neuronList.get(pos);
    }

    public static GenNeuron retrieveInputNeuron(final GenNet genNet, final int pos) {
        return genNet.inputNeuronList.get(pos);
    }

    public static void submitInputValue(final GenNet genNet, final int pos, final float inputValue) {
        genNet.inputNeuronList.get(pos).outputValue = inputValue;
    }

    public static float retrieveOutputValue(final GenNet genNet, final int pos) {
        return genNet.outputNeuronList.get(pos).outputValue;
    }

    public static float[] run(final GenNet genNet, final float[] trainInputArr) {
        for (int inputPos = 0; inputPos < trainInputArr.length; inputPos++) {
            submitInputValue(genNet, inputPos, trainInputArr[inputPos]);
        }

        calc(genNet);

        final float[] layerOutputArr = new float[genNet.outputNeuronList.size()];
        for (int outputPos = 0; outputPos < layerOutputArr.length; outputPos++) {
            layerOutputArr[outputPos] = GenNetService.retrieveOutputValue(genNet, outputPos);
        }
        return layerOutputArr;
    }

    public static void calc(final GenNet genNet) {
        // for each Neuron
        //   1. add the bias to the current sum
        //   2. apply activation function to this result
        //   3. for each outgoing connection
        //        3.1. multiply this out value with synapse weight and add the result to the sum of destination neuron

        genNet.neuronList.forEach(neuron -> {
            if (neuron.neuronType != GenNeuron.NeuronType.Input) {
                neuron.outputValue = neuron.bias;
                if (Objects.nonNull(neuron.inputSynapseList)) {
                    neuron.inputSynapseList.forEach(synapse -> {
                        neuron.outputValue += synapse.inGenNeuron.outputValue * synapse.weight;
                    });
                }
                neuron.outputValue = calcActivation(neuron.neuronType, neuron.outputValue);
            }
        });
    }

    public static void resetNetOutputs(final GenNet genNet) {
        for (final GenNeuron genNeuron : genNet.neuronList) {
            genNeuron.outputValue = 0.0F;
        }

        //net.getClockInput().setValue(MlpService.CLOCK_VALUE);
    }

    private static float calcActivation(GenNeuron.NeuronType neuronType, float value) {
        return
        switch (neuronType) {
            case Input -> value;
            case Hidden, Output -> hyperbolicTension(value);
            //case Hidden, Output -> calcReLU(value);
            //case Hidden, Output -> calcLeakyReLU(value);
        };
    }

    /**
     * ReLU (Rectified Linear Unit)
     */
    private static float calcReLU(final float value) {
        return Math.max(0, value);
    }

    /**
     * Leaky ReLU (Rectified Linear Unit)
     */
    private static float calcLeakyReLU(final float value) {
        return Math.max(0.01F * value, value);
    }

    private static float hyperbolicTension(final float value) {
        return (float)Math.tanh(value);
    }

    static GenNet copyNet(final GenNet net) {
        final GenNet newNet = new GenNet();

        net.neuronList.forEach(neuron -> {
            final GenNeuron newNeuron = new GenNeuron(neuron.neuronType, neuron.bias);
            newNeuron.outputValue = neuron.outputValue;
            newNeuron.neuronIndex = neuron.neuronIndex;
            newNet.neuronList.add(newNeuron);
        });

        net.inputNeuronList.forEach(genNeuron -> newNet.inputNeuronList.add(newNet.neuronList.get(genNeuron.neuronIndex)));
        net.outputNeuronList.forEach(genNeuron -> newNet.outputNeuronList.add(newNet.neuronList.get(genNeuron.neuronIndex)));

        for (int neuronPos = 0; neuronPos < newNet.neuronList.size(); neuronPos++) {
            final GenNeuron neuron = net.neuronList.get(neuronPos);
            final GenNeuron newNeuron = newNet.neuronList.get(neuronPos);
            if (Objects.nonNull(neuron.inputSynapseList)) {
                newNeuron.inputSynapseList = new ArrayList<>();
                neuron.inputSynapseList.forEach(synapse -> {
                    final GenNeuron newInputNeuron = newNet.neuronList.get(synapse.inGenNeuron.neuronIndex);
                    final GenSynapse newSynapse = new GenSynapse(newInputNeuron, synapse.weight);
                    newNeuron.inputSynapseList.add(newSynapse);
                });
            }
        }
        return newNet;
    }
}
