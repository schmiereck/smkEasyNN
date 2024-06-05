package de.schmiereck.smkEasyNN.genEden.service;

public class EnergyPart extends Part {
    int size = 0;
    int energie = 0;

    public EnergyPart(final double[] valueFieldArr) {
        super(valueFieldArr);
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(final int size) {
        this.size = size;
    }

    public int getEnergie() {
        return this.energie;
    }

    public void setEnergie(final int energie) {
        this.energie = energie;
    }
}
