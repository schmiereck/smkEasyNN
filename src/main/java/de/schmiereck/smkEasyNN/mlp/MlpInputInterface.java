package de.schmiereck.smkEasyNN.mlp;

public interface MlpInputInterface {
    float getInput();

    void addError(float error);
}
