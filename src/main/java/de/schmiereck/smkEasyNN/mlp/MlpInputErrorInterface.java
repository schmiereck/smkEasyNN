package de.schmiereck.smkEasyNN.mlp;

public interface MlpInputErrorInterface {
    void addError(float error);
    void addLastError(float error);
}
