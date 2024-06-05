package de.schmiereck.smkEasyNN.genEden.service;

import de.schmiereck.smkEasyNN.genNet.GenNet;

public class GeneticPart extends EnergyPart {
    HexDir moveDir;
    int age = 0;
    GenNet genNet;

    public GeneticPart(final double[] visibleValueArr) {
        super(visibleValueArr);
    }

    public HexDir getMoveDir() {
        return this.moveDir;
    }

    public int getAge() {
        return this.age;
    }

    public GenNet getGenNet() {
        return this.genNet;
    }

    public void setMoveDir(final HexDir moveDir) {
        this.moveDir = moveDir;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public void setGenNet(final GenNet genNet) {
        this.genNet = genNet;
    }
}
