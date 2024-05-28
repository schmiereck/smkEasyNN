package de.schmiereck.smkEasyNN.genEden.service;

public class GridNode {
    private final int xPos;
    private final int yPos;
    private Part inPart;
    private Part outPart;
    private Field[] fieldArr = new Field[HexDir.values().length];

    public GridNode(final int xPos, final int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        for (final HexDir hexDir : HexDir.values()) {
            this.fieldArr[hexDir.ordinal()] = new Field();
        }
    }

    public int getXPos() {
        return this.xPos;
    }

    public int getYPos() {
        return this.yPos;
    }

    public void setInPart(final Part inPart) {
        this.inPart = inPart;
    }

    public Part getInPart() {
        return this.inPart;
    }

    public void setOutPart(final Part outPart) {
        this.outPart = outPart;
    }

    public Part getOutPart() {
        return this.outPart;
    }

    public Field getField(final HexDir hexDir) {
        return this.fieldArr[hexDir.ordinal()];
    }
}
