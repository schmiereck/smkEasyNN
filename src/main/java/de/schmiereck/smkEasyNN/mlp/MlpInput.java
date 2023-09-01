package de.schmiereck.smkEasyNN.mlp;

public class MlpInput implements MlpInputInterface {
    //float input;
    MlpNeuron neuron;

    public MlpInput(final MlpNeuron neuron) {
        this.neuron = neuron;
    }

    @Override
    public float getInput() {
        return this.neuron.output;
    }

    @Override
    public void addError(final float error) {
        this.neuron.error += error;
    }
}
