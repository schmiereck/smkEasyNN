package de.schmiereck.smkEasyNN.genEden.service;

public class GridNode {
    private final int xPos;
    private final int yPos;
    private Part part;
    private Field[] inFieldArr = new Field[InDir.values().length];

    public GridNode(final int xPos, final int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        for (final InDir inDir : InDir.values()) {
            this.inFieldArr[inDir.ordinal()] = new Field();
        }
    }

    public int getXPos() {
        return this.xPos;
    }

    public int getYPos() {
        return this.yPos;
    }

    public void setPart(final Part part) {
        this.part = part;
    }

    public Part getPart() {
        return this.part;
    }

    public Field getInField(final InDir inDir) {
        return this.inFieldArr[inDir.ordinal()];
    }
}
