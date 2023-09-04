package de.schmiereck.smkEasyNN.mlp;

public interface MlpInputInterface {
    float getInput();
    float getLastInput();

    void addError(float error);
    void addLastError(float error);

}
