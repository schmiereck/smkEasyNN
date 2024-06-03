package de.schmiereck.smkEasyNN.genEden.service.persistent;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.schmiereck.smkEasyNN.genNet.GenNeuron;
import de.schmiereck.smkEasyNN.genNet.GenSynapse;

import java.util.List;

public class PerGenNeuron {
    public GenNeuron.NeuronType neuronType;
    public int neuronIndex;
    //@JsonManagedReference
    public List<PerGenSynapse> inputSynapseList;
    public float bias;
    public float outputValue = 0.0F;
}
