package de.schmiereck.smkEasyNN.mlp;

public interface MlpInputErrorInterface extends MlpInputIdentInterface {
    void addErrorValue(float errorValue);
    void addLastErrorValue(float errorValue);
}
