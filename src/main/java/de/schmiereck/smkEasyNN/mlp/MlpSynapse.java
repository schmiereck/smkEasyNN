package de.schmiereck.smkEasyNN.mlp;

public class MlpSynapse {
    private MlpInputInterface input;
    private MlpInputErrorInterface inputError;
    boolean forward = false;
    boolean useLastInput = false;
    boolean useTrainLastInput = false;

    float weight;

    /**
     * The derivative of how a weight affects the weighted input of a neuron.
     */
    float dweight;

    public MlpSynapse(final MlpInputInterface input, final MlpInputErrorInterface inputError, final boolean forward, final boolean useLastInput) {
        this(input, inputError, forward, useLastInput, useLastInput);
    }

    public MlpSynapse(final MlpInputInterface input, final MlpInputErrorInterface inputError, final boolean forward, final boolean useLastInput, final boolean useTrainLastInput) {
        this.input = input;
        this.inputError = inputError;
        this.forward = forward;
        this.useLastInput = useLastInput;
        this.useTrainLastInput = useTrainLastInput;
    }

    public MlpInputInterface getInput() {
        return this.input;
    }

    public MlpInputErrorInterface getInputError() {
        return this.inputError;
    }

}
