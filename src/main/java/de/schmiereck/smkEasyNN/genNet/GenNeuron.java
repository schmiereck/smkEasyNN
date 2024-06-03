package de.schmiereck.smkEasyNN.genNet;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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

    public NeuronType getNeuronType() {
        return this.neuronType;
    }

    public int getNeuronIndex() {
        return this.neuronIndex;
    }

    public List<GenSynapse> getInputSynapseList() {
        return this.inputSynapseList;
    }

    public float getBias() {
        return this.bias;
    }

    public float getOutputValue() {
        return this.outputValue;
    }

    public void setOutputValue(final float outputValue) {
        this.outputValue = outputValue;
    }

    public void setInputSynapseList(final List<GenSynapse> inputSynapseList) {
        this.inputSynapseList = inputSynapseList;
    }

    public void setNeuronIndex(final int neuronIndex) {
        this.neuronIndex = neuronIndex;
    }

    public void setNeuronType(final NeuronType neuronType) {
        this.neuronType = neuronType;
    }

    public void setBias(final float bias) {
        this.bias = bias;
    }

}
