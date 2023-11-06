package de.schmiereck.smkEasyNN.mlp.persistent;

public class SynapseData {
    public int inputLayerNr;
    public int inputNeuronNr;
    boolean useLastError;
    boolean useLastInput;
    boolean useTrainLastInput;
    public float weight;
}
