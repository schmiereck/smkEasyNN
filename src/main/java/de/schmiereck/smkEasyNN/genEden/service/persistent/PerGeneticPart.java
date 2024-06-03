package de.schmiereck.smkEasyNN.genEden.service.persistent;

import de.schmiereck.smkEasyNN.genEden.service.HexDir;
import de.schmiereck.smkEasyNN.genNet.GenNet;

public class PerGeneticPart {
    public double[] visibleValueArr;

    public HexDir moveDir;
    public int size = 0;
    public int energie = 0;
    public int age = 0;
    public PerGenNet genNet;
}
