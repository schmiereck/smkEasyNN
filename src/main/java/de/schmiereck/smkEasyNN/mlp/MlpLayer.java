package de.schmiereck.smkEasyNN.mlp;

import java.util.List;
import java.util.Random;

public class MlpLayer {
    MlpNeuron[] neuronArr;
    boolean isOutputLayer = false;

    public MlpLayer(final int inputSize, final int layerSize) {
        this.neuronArr = new MlpNeuron[layerSize];

        for (int neuronPos = 0; neuronPos < this.neuronArr.length; neuronPos++) {
            this.neuronArr[neuronPos] = new MlpNeuron(inputSize);
        }
    }

    public void setOutputLayer(final boolean isSigmoid) {
        this.isOutputLayer = isSigmoid;
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

    public static float calcInitWeight2(final float initialWeightValue, final Random rnd) {
        //return (rnd.nextFloat() - 0.5F) * 1.0F;
        //return (rnd.nextFloat() - 0.5F) * 0.1F;
        return calcInitWeight(initialWeightValue, rnd);
    }

    public static float calcInitWeight3(final float initialWeightValue, final Random rnd) {
        //return calcInitWeight(initialWeightValue, rnd) * 0.01F;
        return 0.025F;
    }

    //public void initWeights(final Random rnd) {
    //    for (int neuronPos = 0; neuronPos < this.neuronArr.length; neuronPos++) {
    //        final MlpNeuron mlpNeuron = this.neuronArr[neuronPos];
    //        for (int weightPos = 0; weightPos < mlpNeuron.weightArr.length; weightPos++) {
    //            mlpNeuron.weightArr[weightPos] = (rnd.nextFloat() - 0.5F) * 4.0F;
    //        }
    //    }
    //}
}
