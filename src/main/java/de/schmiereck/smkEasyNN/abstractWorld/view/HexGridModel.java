package de.schmiereck.smkEasyNN.abstractWorld.view;

import javafx.scene.shape.Circle;

public class HexGridModel {
    //public static final double SCALE = 1.0D;//2.0D;
    public static double SCALE = 0.75D;//2.0D;
    public static double STROKE_WIDTH = 1.0D * SCALE;
    public static double FIELD_STROKE_WIDTH = 2.0D * SCALE;
    public static final double X_SPACE = (2.0D + 1.0D);
    public static final double X_OFFSET_0 = (0.0D);
    public static final double X_OFFSET_1 = (1.5D);
    public static final double Y_SPACE = (Math.sqrt(3) / 2.0D);

    public final int ySize;
    public final int xSize;
    final double size;
    public double xBordOffset;
    public double yBordOffset;

    final HexCellModel grid[][];
    public int stepCount;
    public int partCount;
    public int generationCount;

    public Circle selectionMarkerShape;
    public int selectedPartNr = -1;
    public HexCellModel selectedHexCellModel = null;

    public HexGridModel(final int xSize, final int ySize, final double newScale) {
        this.xSize = xSize;
        this.ySize = ySize;

        HexGridModel.SCALE = newScale;//2.0D;
        HexGridModel.STROKE_WIDTH = 1.0D * HexGridModel.SCALE;
        HexGridModel.FIELD_STROKE_WIDTH = 2.0D * HexGridModel.SCALE;
        this.size = 10.0D * newScale;
        this.xBordOffset = this.size + (STROKE_WIDTH / 2.0D);
        this.yBordOffset = this.size * Y_SPACE + (STROKE_WIDTH / 2.0D);

        this.grid = new HexCellModel[this.xSize][this.ySize];
    }
}
