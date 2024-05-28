package de.schmiereck.smkEasyNN.genEden;

public class HexGridModel {
    final int ySize;
    final int xSize;
    final double size;

    final HexCellModel grid[][];
    public int stepCount;
    public int partCount;

    public HexGridModel(final int xSize, final int ySize, final double size) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.size = size;
        this.grid = new HexCellModel[this.xSize][this.ySize];
    }
}
