package de.schmiereck.smkEasyNN.mlp;

import java.util.Random;

public class MlpLayer {
    MlpNeuron[] neuronArr;

    float[] inputArr;


    boolean isOutputLayer = false;

    public MlpLayer(final int inputSize, final int outputSize, final Random rnd) {
        this.neuronArr = new MlpNeuron[outputSize];

        for (int neuronPos = 0; neuronPos < this.neuronArr.length; neuronPos++) {
            this.neuronArr[neuronPos] = new MlpNeuron(inputSize);
        }
        this.inputArr = new float[inputSize];

        initWeights(rnd);
    }

    public void setOutputLayer(final boolean isSigmoid) {
        this.isOutputLayer = isSigmoid;
    }

    public void initWeights(final Random rnd) {
        for (int neuronPos = 0; neuronPos < this.neuronArr.length; neuronPos++) {
            final MlpNeuron mlpNeuron = this.neuronArr[neuronPos];
            for (int weightPos = 0; weightPos < mlpNeuron.weightArr.length; weightPos++) {
                mlpNeuron.weightArr[weightPos] = (rnd.nextFloat() - 0.5F) * 4.0F;
            }
        }
    }
}
