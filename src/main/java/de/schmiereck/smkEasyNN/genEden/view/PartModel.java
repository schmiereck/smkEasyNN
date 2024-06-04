package de.schmiereck.smkEasyNN.genEden.view;

import de.schmiereck.smkEasyNN.genEden.service.HexDir;

public class PartModel {
    public double[] visibleValueArr;
    public HexDir moveDir;

    public PartModel(final double[] visibleValueArr) {
        this.visibleValueArr = visibleValueArr;
    }
}
