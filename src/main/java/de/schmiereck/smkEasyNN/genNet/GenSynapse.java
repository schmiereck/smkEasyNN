package de.schmiereck.smkEasyNN.genNet;

public class GenSynapse {
    GenNeuron inGenNeuron;
    float weight;

    public GenSynapse(final GenNeuron inGenNeuron, final float weight) {
        this.inGenNeuron = inGenNeuron;
        this.weight = weight;
    }
}
