package de.schmiereck.smkEasyNN.genEden.service;

public class DemoPart extends Part {
    HexDir moveDir = null;
    int moveCnt = 0;

    public DemoPart(final double[] visibleValueArr) {
        super(visibleValueArr);
    }
}
