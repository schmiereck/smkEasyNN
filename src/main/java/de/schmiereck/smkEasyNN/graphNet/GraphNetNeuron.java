package de.schmiereck.smkEasyNN.graphNet;

import java.util.List;

public class GraphNetNeuron {
    public enum NeuronType {
        Input, Output, Hidden
    }
    NeuronType neuronType;
    int neuronIndex;
    List<GraphNetSynapse> inputSynapseList;
    float bias;
    float outputValue = 0.0F;

    public GraphNetNeuron(final NeuronType neuronType, final float bias) {
        this.neuronType = neuronType;
        this.bias = bias;
    }
}
