package de.schmiereck.smkEasyNN.mlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MlpNeuron {
    List<MlpSynapse> synapseList;
    float output;
    float lastOutput;
    float error;
    float lastError;

    public MlpNeuron(final int inputSize) {
        //this.weightArr = new float[inputSize];
        //this.dweightArr = new float[inputSize];
        this.synapseList = new ArrayList<>(inputSize + 1);
    }
}
