package de.schmiereck.smkEasyNN.genEden;

import de.schmiereck.smkEasyNN.genEden.service.HexDir;
import de.schmiereck.smkEasyNN.genEden.view.PartModel;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class HexCellModel {
    final Polygon hexagon;
    final Line dirArr[];
    PartModel partModel;
    double fieldArr[] = new double[HexDir.values().length];

    public HexCellModel(final Polygon hexagon, final Line dirArr[]) {
        this.hexagon = hexagon;
        this.dirArr = dirArr;
    }
}
