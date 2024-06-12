package de.schmiereck.smkEasyNN.genEden.service;

public abstract class Part {
    private static int partCnt = 0;
    private final int partNr;
    private double[] valueFieldArr;
    private double[] comFieldArr = new double[3];
    private GridNode gridNode;

    public Part(final int partNr, final double[] valueFieldArr) {
        this.partNr = partNr;
        this.valueFieldArr = valueFieldArr;
    }

    public double[] getValueFieldArr() {
        return this.valueFieldArr;
    }

    public double[] getComFieldArr() {
        return this.comFieldArr;
    }

    public GridNode getGridNode() {
        return this.gridNode;
    }

    public void setGridNode(final GridNode gridNode) {
        this.gridNode = gridNode;
    }

    public int getPartNr() {
        return this.partNr;
    }

    public static int calcNextPartNr() {
        return partCnt++;
    }
}
