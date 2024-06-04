package de.schmiereck.smkEasyNN.genEden.service;

public abstract class Part {
    private double[] valueFieldArr;
    private double[] comFieldArr = new double[3];

    public Part(final double[] valueFieldArr) {
        this.valueFieldArr = valueFieldArr;
    }

    public double[] getValueFieldArr() {
        return this.valueFieldArr;
    }

    public double[] getComFieldArr() {
        return this.comFieldArr;
    }
}
