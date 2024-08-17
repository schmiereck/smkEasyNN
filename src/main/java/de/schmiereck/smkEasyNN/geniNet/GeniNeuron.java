package de.schmiereck.smkEasyNN.geniNet;

import java.util.List;

public class GeniNeuron {
    public enum NeuronType {
        Input, Output, Hidden
    }
    NeuronType neuronType;
    int neuronIndex;
    List<GeniSynapse> inputSynapseList;
    int bias;
    int outputValue = 0;

    public GeniNeuron(final NeuronType neuronType, final int bias) {
        this.neuronType = neuronType;
        this.bias = bias;
    }

    public NeuronType getNeuronType() {
        return this.neuronType;
    }

    public int getNeuronIndex() {
        return this.neuronIndex;
    }

    public List<GeniSynapse> getInputSynapseList() {
        return this.inputSynapseList;
    }

    public int getBias() {
        return this.bias;
    }

    public int getOutputValue() {
        return this.outputValue;
    }

    public void setOutputValue(final int outputValue) {
        this.outputValue = outputValue;
    }

    public void setInputSynapseList(final List<GeniSynapse> inputSynapseList) {
        this.inputSynapseList = inputSynapseList;
    }

    public void setNeuronIndex(final int neuronIndex) {
        this.neuronIndex = neuronIndex;
    }

    public void setNeuronType(final NeuronType neuronType) {
        this.neuronType = neuronType;
    }

    public void setBias(final int bias) {
        this.bias = bias;
    }

}
