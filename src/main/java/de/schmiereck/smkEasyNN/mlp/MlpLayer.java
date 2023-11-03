package de.schmiereck.smkEasyNN.mlp;

import java.util.Random;

public class MlpLayer {
    final int layerNr;
    MlpNeuron[] neuronArr;
    boolean isOutputLayer = false;

    public MlpLayer(final int layerNr, final int inputSize, final int layerSize) {
        this.layerNr = layerNr;
        this.neuronArr = new MlpNeuron[layerSize];

        for (int neuronPos = 0; neuronPos < this.neuronArr.length; neuronPos++) {
            this.neuronArr[neuronPos] = new MlpNeuron(this.layerNr, neuronPos, inputSize);
        }
    }

    public void setIsOutputLayer(final boolean isOutputLayer) {
        this.isOutputLayer = isOutputLayer;
    }

    public boolean getIsOutputLayer() {
        return this.isOutputLayer;
    }

    public void initWeights2(final float initialWeightValue, final Random rnd) {
        for (int neuronPos = 0; neuronPos < this.neuronArr.length; neuronPos++) {
            final MlpNeuron mlpNeuron = this.neuronArr[neuronPos];
            for (int weightPos = 0; weightPos < mlpNeuron.synapseList.size(); weightPos++) {
                mlpNeuron.synapseList.get(weightPos).weight = calcInitWeight(initialWeightValue, rnd);
            }
        }
    }

    public static float calcInitWeight(final float initialWeightValue, final Random rnd) {
        return (rnd.nextFloat() - 0.5F) * initialWeightValue;
    }

    public static float calcInitWeight3(final float initialWeightValue, final Random rnd) {
        //return calcInitWeight(initialWeightValue, rnd) * 0.01F;
        return 0.025F;
    }

    /**
     * https://machinehack.com/story/addressing-unstable-training-of-gan-with-xavier-glorot-initialization
     */
    public static float calcInitWeightXavier(final int inputSize, final Random rnd) {
        // Xavier Glorot weight:

        // inputSize: number of nodes in the previous layer
        final int outputSize = 0;
        final float variance = 6.0F / (inputSize + outputSize);

        // calculate the range for the weights
        final float bound = (float)Math.sqrt(variance);
        // generate random number and
        // scale to the desired range
        final float scaled = (rnd.nextFloat(-bound, bound));
        return scaled;
    }

    /**
     * https://machinehack.com/story/addressing-unstable-training-of-gan-with-xavier-glorot-initialization
     */
    public static float calcInitWeightXavierWithFactor(final int inputSize, final float weightFactor, final Random rnd) {
        // Xavier Glorot weight:

        // inputSize: number of nodes in the previous layer
        final int outputSize = 0;
        final float variance = (2.0F * weightFactor) / (inputSize + outputSize);

        // calculate the range for the weights
        final float bound = (float)Math.sqrt(variance);
        // generate random number and
        // scale to the desired range
        final float scaled = (rnd.nextFloat(-bound, bound));
        return scaled;
    }

    /**
     * https://machinelearningmastery.com/weight-initialization-for-deep-learning-neural-networks/
     */
    public static float calcInitWeightXavier2(final int inputSize, final Random rnd) {
        // number of nodes in the previous layer
        // calculate the range for the weights
        final float lower = -(1.0F / (float)Math.sqrt(inputSize));
        final float upper = (1.0F / (float)Math.sqrt(inputSize));
        // generate random number and
        // scale to the desired range
        final float scaled = lower + rnd.nextFloat() * (upper - lower);
        return scaled;
    }

    /**
     * https://machinelearningmastery.com/weight-initialization-for-deep-learning-neural-networks/
     */
    public static float calcInitWeightNormalizedXavier(final int inputSize, final int outputSize, final Random rnd) {
        // number of nodes in the previous layer
        //final int inputSize = (int)(initialWeightValue * 10);
        //final int outputSize = (int)(initialWeightValue * 10);
        // calculate the range for the weights
        final float lower = -((float)Math.sqrt(6.0F) / (float)Math.sqrt(inputSize + outputSize));
        final float upper = ((float)Math.sqrt(6.0F) / (float)Math.sqrt(inputSize + outputSize));
        // generate random number and
        // scale to the desired range
        final float scaled = lower + rnd.nextFloat() * (upper - lower);
        return scaled;
    }

    //public void initWeights(final Random rnd) {
    //    for (int neuronPos = 0; neuronPos < this.neuronArr.length; neuronPos++) {
    //        final MlpNeuron mlpNeuron = this.neuronArr[neuronPos];
    //        for (int weightPos = 0; weightPos < mlpNeuron.weightArr.length; weightPos++) {
    //            mlpNeuron.weightArr[weightPos] = (rnd.nextFloat() - 0.5F) * 4.0F;
    //        }
    //    }
    //}


    public MlpNeuron[] getNeuronArr() {
        return this.neuronArr;
    }
}
