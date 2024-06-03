package de.schmiereck.smkEasyNN.genNet;

import com.fasterxml.jackson.annotation.JsonBackReference;

public class GenSynapse {
    GenNeuron inGenNeuron;
    float weight;

    public GenSynapse(final GenNeuron inGenNeuron, final float weight) {
        this.inGenNeuron = inGenNeuron;
        this.weight = weight;
    }

    public GenNeuron getInGenNeuron() {
        return this.inGenNeuron;
    }

    public float getWeight() {
        return this.weight;
    }

    public void setInGenNeuron(final GenNeuron inGenNeuron) {
        this.inGenNeuron = inGenNeuron;
    }

    public void setWeight(final float weight) {
        this.weight = weight;
    }
}
