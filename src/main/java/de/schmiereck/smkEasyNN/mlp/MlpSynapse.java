package de.schmiereck.smkEasyNN.mlp;

public class MlpSynapse {
    private MlpInputInterface input;
    private MlpInputErrorInterface inputError;
    boolean useLastError = false;
    boolean useLastInput = false;
    boolean useTrainLastInput = false;

    float weight;

    /**
     * The derivative of how a weight affects the weighted input of a neuron.
     */
    float dweight;

    public MlpSynapse(final MlpInputInterface input, final MlpInputErrorInterface inputError, final boolean useLastError, final boolean useLastInput, final boolean useTrainLastInput) {
        this.input = input;
        this.inputError = inputError;
        this.useLastError = useLastError;
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
