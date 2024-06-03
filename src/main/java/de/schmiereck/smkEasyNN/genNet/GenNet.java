package de.schmiereck.smkEasyNN.genNet;

import java.util.ArrayList;
import java.util.List;

public class GenNet {
    float error;
    List<GenNeuron> inputNeuronList = new ArrayList<>();
    List<GenNeuron> outputNeuronList = new ArrayList<>();
    List<GenNeuron> neuronList = new ArrayList<>();

    public float getError() {
        return error;
    }

    public List<GenNeuron> getInputNeuronList() {
        return inputNeuronList;
    }

    public List<GenNeuron> getOutputNeuronList() {
        return outputNeuronList;
    }

    public List<GenNeuron> getNeuronList() {
        return neuronList;
    }

    public void setError(final float error) {
        this.error = error;
    }


}
