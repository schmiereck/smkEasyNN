package de.schmiereck.smkEasyNN.genEden.service;

import de.schmiereck.smkEasyNN.genNet.GenNet;

public class GeneticPart extends Part {
    HexDir moveDir;
    int size = 0;
    int energie = 0;
    GenNet genNet;

    public GeneticPart(final double[] visibleValueArr) {
        super(visibleValueArr);
    }
}
