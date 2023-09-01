package de.schmiereck.smkEasyNN.mlp;

public class MlpSynapse {
    MlpInputInterface input;
    float weight;
    /**
     * The derivative of how a weight affects the weighted input of a neuron.
     */
    float dweight;
}
