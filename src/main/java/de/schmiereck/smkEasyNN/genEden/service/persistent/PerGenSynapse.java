package de.schmiereck.smkEasyNN.genEden.service.persistent;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.schmiereck.smkEasyNN.genNet.GenNeuron;

public class PerGenSynapse {
    //@JsonBackReference
    //public PerGenNeuron inGenNeuron;
    public int inGenNeuronIndex;
    public float weight;
}
