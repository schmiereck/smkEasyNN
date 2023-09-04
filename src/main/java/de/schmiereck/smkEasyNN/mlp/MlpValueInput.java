package de.schmiereck.smkEasyNN.mlp;

public class MlpValueInput implements MlpInputInterface {
    private float value;
    private float lastValue;
    private float error;
    private float lastError;

    public MlpValueInput(final float value) {
        this.value = value;
    }

    @Override
    public float getInput() {
        return this.value;
    }

    @Override
    public float getLastInput() {
        return this.lastValue;
    }

    public void setValue(final float value) {
        this.lastValue = this.value;
        this.value = value;
    }

    @Override
    public void addError(final float error) {
        this.error += error;
    }

    @Override
    public void addLastError(final float error) {
        this.lastError += error;
    }

    public void setError(final float error) {
        this.error = error;
        this.lastError = error;
    }
}
