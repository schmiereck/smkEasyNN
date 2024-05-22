package de.schmiereck.smkEasyNN.graphNet;

import java.util.ArrayList;
import java.util.List;

public class GraphNetBrain {
    public float error;
    List<GraphNetNeuron> inputNeuronList = new ArrayList<>();
    List<GraphNetNeuron> outputNeuronList = new ArrayList<>();
    List<GraphNetNeuron> neuronList = new ArrayList<>();
}
