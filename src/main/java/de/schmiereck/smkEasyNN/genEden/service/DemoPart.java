package de.schmiereck.smkEasyNN.genEden.service;

public class DemoPart extends Part {
    HexDir lastDir = null;
    int moveCnt = 0;

    public DemoPart(final double[] visibleValueArr) {
        super(visibleValueArr);
    }
}
