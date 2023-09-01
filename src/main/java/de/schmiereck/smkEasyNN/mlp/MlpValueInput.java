package de.schmiereck.smkEasyNN.mlp;

public class MlpValueInput implements MlpInputInterface {
    private float value;

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
}
