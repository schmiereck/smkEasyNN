package de.schmiereck.smkEasyNN.mlp;

public class MlpSynapse {
    private MlpInputInterface input;
    private MlpInputErrorInterface inputError;
    float weight;

    /**
     * The derivative of how a weight affects the weighted input of a neuron.
     */
    float dweight;
    boolean forward = false;

    public MlpSynapse(final MlpInputInterface input, final MlpInputErrorInterface inputError, final boolean forward) {
        this.input = input;
        this.inputError = inputError;
        this.forward = forward;
    }

    public MlpInputInterface getInput() {
        return this.input;
    }

    public MlpInputErrorInterface getInputError() {
        return this.inputError;
    }
}
