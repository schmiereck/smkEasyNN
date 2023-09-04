package de.schmiereck.smkEasyNN.mlp;

import java.util.List;
import java.util.Random;

public class MlpLayer {
    //List<MlpInputInterface> inputArr;
    MlpNeuron[] neuronArr;
    boolean isOutputLayer = false;

    public MlpLayer(final int inputSize, final int outputSize, final Random rnd) {
        this.neuronArr = new MlpNeuron[outputSize];

        for (int neuronPos = 0; neuronPos < this.neuronArr.length; neuronPos++) {
            this.neuronArr[neuronPos] = new MlpNeuron(inputSize);
        }
    }

    public void setOutputLayer(final boolean isSigmoid) {
        this.isOutputLayer = isSigmoid;
    }

    public void initWeights2(final Random rnd) {
        for (int neuronPos = 0; neuronPos < this.neuronArr.length; neuronPos++) {
            final MlpNeuron mlpNeuron = this.neuronArr[neuronPos];
            for (int weightPos = 0; weightPos < mlpNeuron.synapseList.size(); weightPos++) {
                mlpNeuron.synapseList.get(weightPos).weight = calcInitWeight(rnd);
            }
        }
    }

    public static float calcInitWeight(Random rnd) {
        return (rnd.nextFloat() - 0.5F) * 4.0F;
    }

    public static float calcInitWeight2(Random rnd) {
        //return (rnd.nextFloat() - 0.5F) * 1.0F;
        //return (rnd.nextFloat() - 0.5F) * 0.1F;
        return calcInitWeight(rnd);
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
