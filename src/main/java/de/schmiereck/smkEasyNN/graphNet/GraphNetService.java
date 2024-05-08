package de.schmiereck.smkEasyNN.graphNet;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class GraphNetService {
    final static Random rnd = new Random();

    public static void submitInputValue(GraphNetBrain graphNetBrain, int pos, float inputValue) {
        graphNetBrain.inputNeuronList.get(pos).outputValue = inputValue;
    }

    public static float retrieveOutputValue(GraphNetBrain graphNetBrain, int pos) {
        return graphNetBrain.outputNeuronList.get(pos).outputValue;
    }

    public static void submitNewNeuron(GraphNetBrain graphNetBrain, GraphNetNeuron graphNetNeuron) {
        switch (graphNetNeuron.neuronType) {
            case Input -> graphNetBrain.inputNeuronList.add(graphNetNeuron);
            case Output -> graphNetBrain.outputNeuronList.add(graphNetNeuron);
        }
        graphNetNeuron.neuronIndex = graphNetBrain.neuronList.size();
        graphNetBrain.neuronList.add(graphNetNeuron);
    }

    public static void calc(final GraphNetBrain graphNetBrain) {
        // for each Neuron
        //   1. add the bias to the current sum
        //   2. apply activation function to this result
        //   3. for each outgoing connection
        //        3.1. multiply this out value with synapse weight and add the result to the sum of destination neuron

        graphNetBrain.neuronList.forEach(neuron -> {
            if (neuron.neuronType != GraphNetNeuron.NeuronType.Input) {
                neuron.outputValue = neuron.bias;
                if (Objects.nonNull(neuron.inputSynapseList)) {
                    neuron.inputSynapseList.forEach(synapse -> {
                        neuron.outputValue += synapse.inGraphNetNeuron.outputValue * synapse.weight;
                    });
                }
                neuron.outputValue = calcActivation(neuron.neuronType, neuron.outputValue);
            }
        });
    }

    private static float calcActivation(GraphNetNeuron.NeuronType neuronType, float value) {
        return
        switch (neuronType) {
            case Input -> value;
            case Hidden, Output -> hyperbolicTension(value);
        };
    }

    private static float hyperbolicTension(final float value) {
        return (float)Math.tanh(value);
    }

    public static GraphNetBrain createMutatedBrain(final GraphNetBrain brain, final float mutationRate) {
        final GraphNetBrain mutatedBrain = copyBrain(brain);
        switch (rnd.nextInt(5)) {
            // Nothing
            case 0 -> {}
            // New Synapse
            case 1 -> mutateNewSynapse(mutatedBrain);
            // New Neuron
            case 2 -> mutateNewNeuron(mutatedBrain);
            // Weight modification
            case 3 -> mutateWeight(mutatedBrain, mutationRate);
            // Bias modification
            case 4 -> mutateBias(mutatedBrain, mutationRate);
        }
        return mutatedBrain;
    }

    private static void mutateBias(final GraphNetBrain mutatedBrain, final float mutationRate) {
        final GraphNetNeuron neuron = mutatedBrain.neuronList.get(rnd.nextInt(mutatedBrain.inputNeuronList.size()));
        if (neuron.neuronType != GraphNetNeuron.NeuronType.Input) {
            neuron.bias += rnd.nextFloat(0.1F) - 0.05F;
            neuron.bias += rnd.nextFloat(mutationRate) - (mutationRate / 2.0F);
        }
    }

    private static void mutateWeight(final GraphNetBrain mutatedBrain, final float mutationRate) {
        final GraphNetNeuron neuron = mutatedBrain.neuronList.get(rnd.nextInt(mutatedBrain.neuronList.size()));
        if (Objects.nonNull(neuron.inputSynapseList)) {
            final GraphNetSynapse synapse = neuron.inputSynapseList.get(rnd.nextInt(neuron.inputSynapseList.size()));
            //synapse.weight += rnd.nextFloat(1.0F) - 0.5F;
            //synapse.weight += rnd.nextFloat(0.5F) - 0.25F;
            //synapse.weight += rnd.nextFloat(0.1F) - 0.05F;
            //synapse.weight += rnd.nextFloat(2.0F) - 1.0F;
            synapse.weight += rnd.nextFloat(mutationRate) - (mutationRate / 2.0F);
        }
    }

    private static void mutateNewNeuron(final GraphNetBrain mutatedBrain) {

    }

    private static void mutateNewSynapse(final GraphNetBrain mutatedBrain) {

    }

    private static GraphNetBrain copyBrain(final GraphNetBrain brain) {
        final GraphNetBrain newBrain = new GraphNetBrain();

        brain.neuronList.forEach(neuron -> {
            final GraphNetNeuron newNeuron = new GraphNetNeuron(neuron.neuronType, neuron.bias);
            newNeuron.outputValue = neuron.outputValue;
            newNeuron.neuronIndex = neuron.neuronIndex;
            newBrain.neuronList.add(newNeuron);
        });

        brain.inputNeuronList.forEach(graphNetNeuron -> newBrain.inputNeuronList.add(newBrain.neuronList.get(graphNetNeuron.neuronIndex)));
        brain.outputNeuronList.forEach(graphNetNeuron -> newBrain.outputNeuronList.add(newBrain.neuronList.get(graphNetNeuron.neuronIndex)));

        for (int neuronPos = 0; neuronPos < newBrain.neuronList.size(); neuronPos++) {
            final GraphNetNeuron neuron = brain.neuronList.get(neuronPos);
            final GraphNetNeuron newNeuron = newBrain.neuronList.get(neuronPos);
            if (Objects.nonNull(neuron.inputSynapseList)) {
                newNeuron.inputSynapseList = new ArrayList<>();
                neuron.inputSynapseList.forEach(synapse -> {
                    final GraphNetNeuron newInputNeuron = newBrain.neuronList.get(synapse.inGraphNetNeuron.neuronIndex);
                    final GraphNetSynapse newSynapse = new GraphNetSynapse(newInputNeuron, synapse.weight);
                    newNeuron.inputSynapseList.add(newSynapse);
                });
            }
        }
        return newBrain;
    }
}
