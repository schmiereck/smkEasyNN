package de.schmiereck.smkEasyNN.abstractWorld.view;

import de.schmiereck.smkEasyNN.genEden.service.HexDir;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class HexCellModel {
    final Polygon hexagon;
    final Line dirArr[];
    PartModel partModel;
    public double[][] fieldArrArr = new double[HexDir.values().length][3];

    public HexCellModel(final Polygon hexagon, final Line dirArr[]) {
        this.hexagon = hexagon;
        this.dirArr = dirArr;
    }
}
