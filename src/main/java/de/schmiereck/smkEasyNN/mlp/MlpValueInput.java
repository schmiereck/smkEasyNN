package de.schmiereck.smkEasyNN.mlp;

public class MlpValueInput implements MlpInputInterface {
    private float value;
    private float error;

    public MlpValueInput(final float value) {
        this.value = value;
    }

    @Override
    public float getInput() {
        return this.value;
    }

    public void setValue(final float value) {
        this.value = value;
    }

    @Override
    public void addError(final float error) {
        this.error += error;
    }
}
