package de.schmiereck.smkEasyNN.mlp;

public interface MlpInputErrorInterface extends MlpInputIdentInterface {
    void addErrorValue(float errorValue);
    float getErrorValue();
    void addLastErrorValue(float errorValue);
}
