package de.schmiereck.smkEasyNN.graphNet;

public class GraphNetSynapse {
    GraphNetNeuron inGraphNetNeuron;
    float weight;

    public GraphNetSynapse(final GraphNetNeuron inGraphNetNeuron, final float weight) {
        this.inGraphNetNeuron = inGraphNetNeuron;
        this.weight = weight;
    }
}
