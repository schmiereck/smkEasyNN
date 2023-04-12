package de.schmiereck.smkEasyNN.mlp;

import java.util.Random;

public class MlpNeuron {
    float output;
    //float[] inputArr;
    float[] weightArr;
    /**
     * The derivative of how a weight affects the weighted input of a neuron.
     */
    float[] dweightArr;

    public MlpNeuron(final int inputSize) {
        this.weightArr = new float[inputSize];
        this.dweightArr = new float[inputSize];
    }
}
