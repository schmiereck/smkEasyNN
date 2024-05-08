package de.schmiereck.smkEasyNN.genNet;

import java.util.List;

public class GenNeuron {
    public enum NeuronType {
        Input, Output, Hidden
    }
    NeuronType neuronType;
    int neuronIndex;
    List<GenSynapse> inputSynapseList;
    float bias;
    float outputValue = 0.0F;

    public GenNeuron(final NeuronType neuronType, final float bias) {
        this.neuronType = neuronType;
        this.bias = bias;
    }
}
