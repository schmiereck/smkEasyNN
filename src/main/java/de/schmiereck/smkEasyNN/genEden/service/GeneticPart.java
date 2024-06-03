package de.schmiereck.smkEasyNN.genEden.service;

import de.schmiereck.smkEasyNN.genNet.GenNet;

public class GeneticPart extends Part {
    HexDir moveDir;
    int size = 0;
    int energie = 0;
    int age = 0;
    GenNet genNet;

    public GeneticPart(final double[] visibleValueArr) {
        super(visibleValueArr);
    }

    public HexDir getMoveDir() {
        return this.moveDir;
    }

    public int getSize() {
        return this.size;
    }

    public int getEnergie() {
        return this.energie;
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

    public void setSize(final int size) {
        this.size = size;
    }

    public void setEnergie(final int energie) {
        this.energie = energie;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public void setGenNet(final GenNet genNet) {
        this.genNet = genNet;
    }
}
