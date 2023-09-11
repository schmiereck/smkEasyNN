package de.schmiereck.smkEasyNN.mlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MlpNeuron implements MlpInputInterface, MlpInputErrorInterface {
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

    @Override
    public float getInput() {
        return this.output;
    }

    @Override
    public float getLastInput() {
        return this.lastOutput;
    }

    @Override
    public void addError(final float error) {
        this.error += error;
    }

    @Override
    public void addLastError(final float error) {
        this.lastError += error;
    }
}
