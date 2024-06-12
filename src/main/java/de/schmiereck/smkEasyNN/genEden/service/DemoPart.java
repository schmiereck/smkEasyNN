package de.schmiereck.smkEasyNN.genEden.service;

public class DemoPart extends Part {
    HexDir moveDir = null;
    int moveCnt = 0;

    public DemoPart(final int partNr, final double[] visibleValueArr) {
        super(partNr, visibleValueArr);
    }
}
