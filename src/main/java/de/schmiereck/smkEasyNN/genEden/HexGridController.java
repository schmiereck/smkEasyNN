package de.schmiereck.smkEasyNN.genEden;

import de.schmiereck.smkEasyNN.genEden.service.*;
import de.schmiereck.smkEasyNN.genEden.view.PartModel;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;

import java.util.Objects;

public class HexGridController {

    public static final double X_SPACE = (2.0D + 1.0D);
    public static final double X_OFFSET_0 = (0.5D);
    public static final double X_OFFSET_1 = (1.5D);
    public static final double Y_SPACE = (Math.sqrt(3) / 2.0D);

    public static final double SCALE = 1.0D;//2.0D;
    public static final double STROKE_WIDTH = 1.0D * SCALE;
    public static final double FIELD_STROKE_WIDTH = 2.0D * SCALE;
    public static final Color FIELD_DIR_COLOR = Color.color(1.0D, 1.0D, 0.0D);

    private HexGridService hexGridService;
    private LifeService lifeService;

    private HexGridModel hexGridModel;

    private AnimationTimer animationTimer;

    @FXML
    private Label counterText;

    @FXML
    private Pane pane;

    public HexGridController() {
        this.hexGridService = new HexGridService();
        this.lifeService = new LifeService(this.hexGridService);
    }

    @FXML
    public void initialize() {
        final HexGrid hexGrid = this.hexGridService.retrieveHexGrid();

        this.hexGridModel = new HexGridModel(hexGrid.getXSize(), hexGrid.getYSize(), 10.0D * SCALE);
        this.initHexGrid(this.hexGridModel);

        this.updateHexGridModel(this.hexGridModel);

        this.updateView();
    }

    @FXML
    protected void onNextButtonClick() {
        this.hexGridService.calcNext();

        this.updateHexGridModel(this.hexGridModel);

        this.updateView();
    }

    private void updateHexGridModel(final HexGridModel hexGridModel) {
        hexGridModel.stepCount = this.hexGridService.retrieveStepCount();
        hexGridModel.partCount = this.hexGridService.retrievePartCount();

        for (int yPos = 0; yPos < hexGridModel.ySize; yPos++) {
            for (int xPos = 0; xPos < hexGridModel.xSize; xPos++) {
                final GridNode gridNode = this.hexGridService.retrieveGridNode(xPos, yPos);
                final Part outPart = gridNode.getOutPart();
                final HexCellModel hexCellModel = this.hexGridModel.grid[xPos][yPos];
                if (Objects.nonNull(outPart)) {
                    hexCellModel.partModel = new PartModel(outPart.getVisibleValueArr());
                } else {
                    hexCellModel.partModel = null;
                }
                for (final HexDir hexDir : HexDir.values()) {
                    hexCellModel.fieldArrArr[hexDir.ordinal()][0] = gridNode.getField(hexDir).outValueArr[0];
                    hexCellModel.fieldArrArr[hexDir.ordinal()][1] = gridNode.getField(hexDir).outValueArr[1];
                    hexCellModel.fieldArrArr[hexDir.ordinal()][2] = gridNode.getField(hexDir).outValueArr[2];
                }
            }
        }
    }

    @FXML
    public void onStartRunButtonClick(final ActionEvent actionEvent) {
        if (Objects.isNull(this.animationTimer)) {
            this.animationTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    HexGridController.this.runLife();
                }
            };
            this.animationTimer.start();
        }
    }

    @FXML
    public void onStopRunButtonClick(final ActionEvent actionEvent) {
        if (Objects.nonNull(this.animationTimer)) {
            this.animationTimer.stop();
            this.animationTimer = null;
        }
    }

    private void runLife() {
        this.lifeService.runLife();

        this.updateHexGridModel(this.hexGridModel);

        this.updateView();
    }

    public void updateView() {
        this.counterText.setText(String.format("Step: %d (Parts: %,d)", this.hexGridModel.stepCount, this.hexGridModel.partCount));

        this.updateHexGrid(this.hexGridModel);
    }

    private void initHexGrid(final HexGridModel hexGridModel) {
        final double xBordOffset = hexGridModel.size + (STROKE_WIDTH / 2.0D);
        final double yBordOffset = hexGridModel.size * Y_SPACE + (STROKE_WIDTH / 2.0D);

        for (int yPos = 0; yPos < hexGridModel.ySize; yPos++) {
            for (int xPos = 0; xPos < hexGridModel.xSize; xPos++) {
                final double offsetX = yPos % 2 == 0 ? X_OFFSET_0 : hexGridModel.size * X_OFFSET_1;

                final Polygon hexagon = createHexagon(xBordOffset, yBordOffset, offsetX, hexGridModel.size - STROKE_WIDTH, xPos, yPos);
                pane.getChildren().add(hexagon);

                final Line dirArr[] = createDirArr(xBordOffset, yBordOffset, offsetX, hexGridModel.size, xPos, yPos);
                for (int edgePos = 0; edgePos < 6; edgePos++) {
                    pane.getChildren().add(dirArr[edgePos]);
                }

                hexGridModel.grid[xPos][yPos] = new HexCellModel(hexagon, dirArr);
            }
        }
    }

    private void updateHexGrid(final HexGridModel hexGridModel) {
        for (int yPos = 0; yPos < hexGridModel.ySize; yPos++) {
            for (int xPos = 0; xPos < hexGridModel.xSize; xPos++) {
                final HexCellModel hexCellModel = hexGridModel.grid[xPos][yPos];
                final Polygon hexagon = hexCellModel.hexagon;
                final Color partColor;
                final PartModel partModel = hexGridModel.grid[xPos][yPos].partModel;
                if (Objects.nonNull(partModel)) {
                    //partColor = Color.CORAL;
                    partColor = Color.color(
                            1.0D - (partModel.visibleValueArr[0]),
                            1.0D - (partModel.visibleValueArr[1]),
                            1.0D - (partModel.visibleValueArr[2]));
                    for (final HexDir hexDir : HexDir.values()) {
                        hexCellModel.dirArr[hexDir.ordinal()].setStroke(FIELD_DIR_COLOR);
                    }
                } else {
                    for (final HexDir hexDir : HexDir.values()) {
                        final double[] fieldArr = hexCellModel.fieldArrArr[hexDir.ordinal()];
                        //if (field > 0.0D) {
                        //    hexCellModel.dirArr[inDir.ordinal()].setStroke(Color.BLUE);//color(0.0D, 0.0D, field / 2.0D));
                        //} else {
                        //    hexCellModel.dirArr[inDir.ordinal()].setStroke(Color.BLACK);
                        //}
                        hexCellModel.dirArr[hexDir.ordinal()].setStroke(Color.color(
                                1.0D - (fieldArr[0]),
                                1.0D - (fieldArr[1]),
                                1.0D - (fieldArr[2])));
                        //1.0D - Math.tanh(fieldArr[0]),
                        //1.0D - Math.tanh(fieldArr[1]),
                        //1.0D - Math.tanh(fieldArr[2])));
                    }
                    //partColor = Color.gray(fieldSum);
                    //partColor = Color.color(fieldSum, 0, 0);
                    partColor = Color.WHITE;
                }
                hexagon.setFill(partColor);
            }
        }
    }

    private Polygon createHexagon(final double xBordOffset, final double yBordOffset, final double offsetX,
                                  final double size, final int xPos, final int yPos) {
        final Polygon hexagon = new Polygon();
        for (int edgePos = 0; edgePos < 6; edgePos++) {
            final double x = edgePos * Math.PI / 3;
            hexagon.getPoints().addAll(size * Math.cos(x), size * Math.sin(x));
        }
        hexagon.setLayoutX(xBordOffset + (xPos * (hexGridModel.size * X_SPACE)) + offsetX);
        hexagon.setLayoutY(yBordOffset + (yPos * (hexGridModel.size * Y_SPACE)));
        hexagon.setFill(Color.WHITE);
        hexagon.setStroke(Color.gray(0.975D));
        hexagon.setStrokeWidth(STROKE_WIDTH);
        return hexagon;
    }

    private Line[] createDirArr(final double xBordOffset, final double yBordOffset, final double offsetX,
                                final double size, final int xPos, final int yPos) {
        final double startSize = FIELD_STROKE_WIDTH;
        final double endSize = size - (FIELD_STROKE_WIDTH * 1.35D);
        final Line[] dirArr = new Line[6];
        for (int edgePos = 0; edgePos < 6; edgePos++) {
            final int edge = (edgePos - 1);
            final double d = (edge * (Math.PI / 3.0D)) - (Math.PI / 6.0D);
            final double x = Math.cos(d);
            final double y = Math.sin(d);
            dirArr[edgePos] = new Line(startSize * x, startSize * y,
                    endSize * x, endSize * y);
            dirArr[edgePos].setLayoutX(xBordOffset + (xPos * (hexGridModel.size * X_SPACE)) + offsetX);
            dirArr[edgePos].setLayoutY(yBordOffset + (yPos * (hexGridModel.size * Y_SPACE)));
            dirArr[edgePos].setStroke(Color.GREEN);
            dirArr[edgePos].setStrokeWidth(FIELD_STROKE_WIDTH);
            dirArr[edgePos].setStrokeLineCap(StrokeLineCap.BUTT);
        }
        return dirArr;
    }

}
