package de.schmiereck.smkEasyNN.geniNet;

public class GeniSynapse {
    GeniNeuron inGeniNeuron;
    int weight;

    public GeniSynapse(final GeniNeuron inGeniNeuron, final int weight) {
        this.inGeniNeuron = inGeniNeuron;
        this.weight = weight;
    }

    public GeniNeuron getInGenNeuron() {
        return this.inGeniNeuron;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setInGenNeuron(final GeniNeuron inGeniNeuron) {
        this.inGeniNeuron = inGeniNeuron;
    }

    public void setWeight(final int weight) {
        this.weight = weight;
    }
}
