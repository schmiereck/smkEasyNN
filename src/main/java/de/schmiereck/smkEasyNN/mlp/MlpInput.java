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
}
