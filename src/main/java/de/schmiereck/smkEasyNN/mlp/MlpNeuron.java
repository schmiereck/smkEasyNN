package de.schmiereck.smkEasyNN.mlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MlpNeuron {
    //float[] inputArr;
    //float[] weightArr;
    /**
     * The derivative of how a weight affects the weighted input of a neuron.
     */
    //float[] dweightArr;
    List<MlpSynapse> synapseList;
    float output;
    //float output2;
    float error;

    public MlpNeuron(final int inputSize) {
        //this.weightArr = new float[inputSize];
        //this.dweightArr = new float[inputSize];
        this.synapseList = new ArrayList<>(inputSize + 1);
    }
}
