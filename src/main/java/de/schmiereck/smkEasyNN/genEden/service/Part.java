package de.schmiereck.smkEasyNN.genEden.service;

public abstract class Part {
    private double[] visibleValueArr;

    public Part(final double[] visibleValueArr) {
        this.visibleValueArr = visibleValueArr;
    }

    public double[] getVisibleValueArr() {
        return this.visibleValueArr;
    }
}
