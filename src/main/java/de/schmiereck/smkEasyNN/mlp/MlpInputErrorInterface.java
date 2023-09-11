package de.schmiereck.smkEasyNN.mlp;

public interface MlpInputErrorInterface {
    void addErrorValue(float errorValue);
    void addLastErrorValue(float errorValue);
}
