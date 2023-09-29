package de.schmiereck.smkEasyNN.mlp;

public class MlpInternalValueInput extends MlpValueInput {
    public boolean internalInput;
    public int inputLayerNr;
    public int inputNeuronNr;

    public MlpInternalValueInput(final int layerNr, final int neuronNr, final float value) {
        super(layerNr, neuronNr, value);
    }
}
