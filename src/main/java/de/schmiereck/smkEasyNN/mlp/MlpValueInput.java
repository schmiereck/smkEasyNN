package de.schmiereck.smkEasyNN.mlp;

public class MlpValueInput implements MlpInputInterface {
    final int layerNr;
    final int neuronNr;
    public boolean internalInput;
    public int inputLayerNr;
    public int inputNeuronNr;
    private float value;

    public MlpValueInput(final int layerNr, final int neuronNr, final float value) {
        this.layerNr = layerNr;
        this.neuronNr = neuronNr;
        this.value = value;
    }

    @Override
    public float getInputValue() {
        return this.value;
    }

    @Override
    public float getLastInputValue() {
        return this.value;
    }

    @Override
    public void setValue(final float value) {
        this.value = value;
    }

    @Override
    public int getLayerNr() {
        return this.layerNr;
    }

    @Override
    public int getNeuronNr() {
        return this.neuronNr;
    }

}
