package de.schmiereck.smkEasyNN.mlp;

public class MlpInput implements MlpInputInterface {
    MlpNeuron neuron;

    public MlpInput(final MlpNeuron neuron) {
        this.neuron = neuron;
    }

    @Override
    public float getInput() {
        return this.neuron.output;
    }

    @Override
    public float getLastInput() {
        return this.neuron.lastOutput;
    }

    @Override
    public void addError(final float error) {
        this.neuron.error += error;
    }

    @Override
    public void addLastError(final float error) {
        this.neuron.lastError += error;
    }
}
