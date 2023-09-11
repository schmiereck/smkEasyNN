package de.schmiereck.smkEasyNN.mlp;

public interface MlpInputInterface {
    float getInputValue();
    float getLastInputValue();
    void setValue(final float value);
}
