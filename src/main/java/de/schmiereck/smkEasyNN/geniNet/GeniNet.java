package de.schmiereck.smkEasyNN.geniNet;

import java.util.ArrayList;
import java.util.List;

public class GeniNet {
    long error;
    List<GeniNeuron> inputNeuronList = new ArrayList<>();
    List<GeniNeuron> outputNeuronList = new ArrayList<>();
    List<GeniNeuron> neuronList = new ArrayList<>();

    public long getError() {
        return error;
    }

    public List<GeniNeuron> getInputNeuronList() {
        return inputNeuronList;
    }

    public List<GeniNeuron> getOutputNeuronList() {
        return outputNeuronList;
    }

    public List<GeniNeuron> getNeuronList() {
        return neuronList;
    }

    public void setError(final int error) {
        this.error = error;
    }


}
