package de.schmiereck.smkEasyNN.geniNet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GeniNetService {
    final static Random rnd = new Random();

    public static int VALUE_0 = 0;
    public static int VALUE_MAX;
    public static int VALUE_MAX_10p;
    public static int VALUE_MAX_90p;
    public static int VALUE_MAX2;

    static {
        initValueRange(100);
    }

    public static void initValueRange(final int valueMax) {
        VALUE_MAX = valueMax;
        VALUE_MAX_10p = (VALUE_MAX * 10 / 100);
        VALUE_MAX_90p = (VALUE_MAX * 90 / 100);
        VALUE_MAX2 = VALUE_MAX / 2;
    }

    public static int calcPercentValue(final float percent) {
        //return (int)((VALUE_MAX * percent) / 100.0F);
        return (int)((VALUE_MAX * percent));
    }

    public static GeniNet createNet(final int[] layerSizeArr, final Random rnd) {
        final GeniNet geniNet = new GeniNet();
        final List<GeniNeuron[]> neuronLayerList = new ArrayList<>(layerSizeArr.length);
        final int initBias = 0;

        // Inputs:
        final int inputSize = layerSizeArr[0];
        final GeniNeuron[] inputNeuronArr = new GeniNeuron[inputSize];
        neuronLayerList.add(inputNeuronArr);
        for (int inputPos = 0; inputPos < inputSize; inputPos++) {
            final GeniNeuron inGeniNeuron = new GeniNeuron(GeniNeuron.NeuronType.Input, initBias);
            GeniNetService.submitNewNeuron(geniNet, inGeniNeuron);
            inputNeuronArr[inputPos] = inGeniNeuron;
        }

        // Hidden:
        final int hiddenLayerSize = layerSizeArr.length - 2;
        for (int hiddenLayerPos = 0; hiddenLayerPos < hiddenLayerSize; hiddenLayerPos++) {
            final int hiddenSize = layerSizeArr[hiddenLayerPos + 1];
            final GeniNeuron[] hiddenNeuronArr = new GeniNeuron[hiddenSize];
            neuronLayerList.add(hiddenNeuronArr);
            for (int hiddenPos = 0; hiddenPos < hiddenSize; hiddenPos++) {
                final GeniNeuron hiddenGeniNeuron = new GeniNeuron(GeniNeuron.NeuronType.Hidden, initBias);
                //final GenNeuron hiddenGenNeuron = new GenNeuron(GenNeuron.NeuronType.Hidden, rnd.nextFloat(-0.25F, 0.25F));
                GeniNetService.submitNewNeuron(geniNet, hiddenGeniNeuron);
                hiddenNeuronArr[hiddenPos] = hiddenGeniNeuron;
            }
        }

        // Outputs:
        final int outputSize = layerSizeArr[layerSizeArr.length - 1];
        final GeniNeuron[] outputNeuronArr = new GeniNeuron[outputSize];
        neuronLayerList.add(outputNeuronArr);
        for (int outputPos = 0; outputPos < outputSize; outputPos++) {
            final GeniNeuron outGeniNeuron = new GeniNeuron(GeniNeuron.NeuronType.Output, initBias);
            GeniNetService.submitNewNeuron(geniNet, outGeniNeuron);
            outputNeuronArr[outputPos] = outGeniNeuron;
        }

        // Synapses:
        for (int layerPos = 0; layerPos < neuronLayerList.size() - 1; layerPos++) {
            final GeniNeuron[] sourceLayerNeuronArr = neuronLayerList.get(layerPos);

            for (int inputPos = 0; inputPos < sourceLayerNeuronArr.length; inputPos++) {
                final GeniNeuron inputNeuron = sourceLayerNeuronArr[inputPos];

                final GeniNeuron[] targetLayerNeuronArr = neuronLayerList.get(layerPos + 1);
                for (int targetPos = 0; targetPos < targetLayerNeuronArr.length; targetPos++) {
                    final GeniNeuron targetNeuron = targetLayerNeuronArr[targetPos];
                    createGeniNetSynapse(inputNeuron, targetNeuron, rnd);
                    //createGeniNetSynapse(inputNeuron, targetNeuron, rnd);
                }
            }
        }
        return geniNet;
    }

    static void createGeniNetSynapse(final GeniNeuron inGeniNeuron, final GeniNeuron outGeniNeuron, final Random rnd) {
        createGeniNetSynapse(inGeniNeuron, outGeniNeuron, rnd.nextInt(VALUE_MAX) - VALUE_MAX2);
    }

    public static void createGeniNetSynapse(final GeniNeuron inGeniNeuron, final GeniNeuron outGeniNeuron, final int weight) {
        final GeniSynapse geniSynapse = new GeniSynapse(inGeniNeuron, weight);
        if (Objects.isNull(outGeniNeuron.inputSynapseList)) {
            outGeniNeuron.inputSynapseList = new ArrayList<>();
        }
        outGeniNeuron.inputSynapseList.add(geniSynapse);
    }

    public static void submitNewNeuron(final GeniNet geniNet, final GeniNeuron geniNeuron) {
        switch (geniNeuron.neuronType) {
            case Input -> geniNet.inputNeuronList.add(geniNeuron);
            case Output -> geniNet.outputNeuronList.add(geniNeuron);
        }
        geniNeuron.neuronIndex = geniNet.neuronList.size();
        geniNet.neuronList.add(geniNeuron);
    }

    public static void submitNewNeuron(final GeniNet geniNet, final int insertNeuronPos, final GeniNeuron geniNeuron) {
        switch (geniNeuron.neuronType) {
            case Input -> geniNet.inputNeuronList.add(geniNeuron);
            case Output -> geniNet.outputNeuronList.add(geniNeuron);
        }
        geniNeuron.neuronIndex = insertNeuronPos;
        geniNet.neuronList.add(insertNeuronPos, geniNeuron);
        for (int neuronPos = insertNeuronPos + 1; neuronPos < geniNet.neuronList.size(); neuronPos++) {
            geniNet.neuronList.get(neuronPos).neuronIndex = neuronPos;
        }
    }

    public static GeniNeuron retrieveNeuron(final GeniNet geniNet, final int pos) {
        return geniNet.neuronList.get(pos);
    }

    public static GeniNeuron retrieveInputNeuron(final GeniNet geniNet, final int pos) {
        return geniNet.inputNeuronList.get(pos);
    }

    public static void submitInputValue(final GeniNet geniNet, final int pos, final int inputValue) {
        geniNet.inputNeuronList.get(pos).outputValue = inputValue;
    }

    public static int retrieveOutputValue(final GeniNet geniNet, final int pos) {
        return geniNet.outputNeuronList.get(pos).outputValue;
    }

    public static int[] run(final GeniNet geniNet, final int[] trainInputArr) {
        for (int inputPos = 0; inputPos < trainInputArr.length; inputPos++) {
            submitInputValue(geniNet, inputPos, trainInputArr[inputPos]);
        }

        calc(geniNet);

        final int[] layerOutputArr = new int[geniNet.outputNeuronList.size()];
        for (int outputPos = 0; outputPos < layerOutputArr.length; outputPos++) {
            layerOutputArr[outputPos] = GeniNetService.retrieveOutputValue(geniNet, outputPos);
        }
        return layerOutputArr;
    }

    public static void calc(final GeniNet geniNet) {
        // for each Neuron
        //   1. add the bias to the current sum
        //   2. apply activation function to this result
        //   3. for each outgoing connection
        //        3.1. multiply this out value with synapse weight and add the result to the sum of destination neuron

        geniNet.neuronList.forEach(neuron -> {
            if (neuron.neuronType != GeniNeuron.NeuronType.Input) {
                neuron.outputValue = neuron.bias;
                if (Objects.nonNull(neuron.inputSynapseList)) {
                    neuron.inputSynapseList.forEach(synapse -> {
                        neuron.outputValue += synapse.inGeniNeuron.outputValue * synapse.weight;
                    });
                }
                neuron.outputValue = calcActivation(neuron.neuronType, neuron.outputValue);
            }
        });
    }

    public static void resetNetOutputs(final GeniNet geniNet) {
        for (final GeniNeuron geniNeuron : geniNet.neuronList) {
            geniNeuron.outputValue = VALUE_0;
        }

        //net.getClockInput().setValue(MlpService.CLOCK_VALUE);
    }

    private static int calcActivation(GeniNeuron.NeuronType neuronType, int value) {
        return
        switch (neuronType) {
            case Input -> value;
            //case Hidden, Output -> hyperbolicTension(value);
            //case Hidden, Output -> calcReLU(value);
            case Hidden, Output -> calcLeakyReLU(value);
        };
    }

    /**
     * ReLU (Rectified Linear Unit)
     */
    private static int calcReLU(final int value) {
        return Math.max(0, value);
    }

    /**
     * Leaky ReLU (Rectified Linear Unit)
     */
    private static int calcLeakyReLU(final int value) {
        //return Math.max(0.01F * value, value);
        //return Math.max(value / VALUE_MAX_10p, value);
        return Math.max(value / VALUE_MAX_90p, value);
    }

    private static int hyperbolicTension(final int value) {
        return (int)(Math.tanh(value / (float)VALUE_MAX) * VALUE_MAX);
    }

    /**
     * f(x)= 1 / (1+exp(−x) )
     */
    private static int calcSigmoid(final int value) {
        return value;
    }

    static GeniNet copyNet(final GeniNet net) {
        final GeniNet newNet = new GeniNet();

        net.neuronList.forEach(neuron -> {
            final GeniNeuron newNeuron = new GeniNeuron(neuron.neuronType, neuron.bias);
            newNeuron.outputValue = neuron.outputValue;
            newNeuron.neuronIndex = neuron.neuronIndex;
            newNet.neuronList.add(newNeuron);
        });

        net.inputNeuronList.forEach(genNeuron -> newNet.inputNeuronList.add(newNet.neuronList.get(genNeuron.neuronIndex)));
        net.outputNeuronList.forEach(genNeuron -> newNet.outputNeuronList.add(newNet.neuronList.get(genNeuron.neuronIndex)));

        for (int neuronPos = 0; neuronPos < newNet.neuronList.size(); neuronPos++) {
            final GeniNeuron neuron = net.neuronList.get(neuronPos);
            final GeniNeuron newNeuron = newNet.neuronList.get(neuronPos);
            if (Objects.nonNull(neuron.inputSynapseList)) {
                newNeuron.inputSynapseList = new ArrayList<>();
                neuron.inputSynapseList.forEach(synapse -> {
                    final GeniNeuron newInputNeuron = newNet.neuronList.get(synapse.inGeniNeuron.neuronIndex);
                    final GeniSynapse newSynapse = new GeniSynapse(newInputNeuron, synapse.weight);
                    newNeuron.inputSynapseList.add(newSynapse);
                });
            }
        }
        return newNet;
    }
}
