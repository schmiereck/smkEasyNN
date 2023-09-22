package de.schmiereck.smkEasyNN.mlp;

import java.util.ArrayList;
import java.util.List;

public class MlpNeuron implements MlpInputInterface, MlpInputErrorInterface {
    final int layerNr;
    final int neuronNr;
    List<MlpSynapse> synapseList;
    float outputValue;
    float lastOutputValue;
    float errorValue;
    float lastErrorValue;

    public MlpNeuron(final int layerNr, final int neuronNr, final int inputSize) {
        this.layerNr = layerNr;
        this.neuronNr = neuronNr;
        //this.weightArr = new float[inputSize];
        //this.dweightArr = new float[inputSize];
        this.synapseList = new ArrayList<>(inputSize + 1);
    }

    @Override
    public float getInputValue() {
        return this.outputValue;
    }

    @Override
    public float getLastInputValue() {
        return this.lastOutputValue;
    }

    @Override
    public void setValue(final float value) {
        this.outputValue = value;
    }

    @Override
    public int getLayerNr() {
        return this.layerNr;
    }

    @Override
    public int getNeuronNr() {
        return this.neuronNr;
    }

    @Override
    public void addErrorValue(final float errorValue) {
        this.errorValue += errorValue;
    }

    @Override
    public void addLastErrorValue(final float errorValue) {
        this.lastErrorValue += errorValue;
    }
}
