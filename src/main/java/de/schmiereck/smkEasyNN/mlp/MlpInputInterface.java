package de.schmiereck.smkEasyNN.mlp;

public interface MlpInputInterface extends MlpInputIdentInterface {
    float getInputValue();
    float getLastInputValue();
    void setValue(final float value);
}
