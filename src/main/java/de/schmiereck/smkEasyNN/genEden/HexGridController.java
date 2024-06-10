package de.schmiereck.smkEasyNN.genEden;

import de.schmiereck.smkEasyNN.genEden.service.*;
import de.schmiereck.smkEasyNN.genEden.service.persistent.GeneticPersistentService;
import de.schmiereck.smkEasyNN.genEden.view.HexGridViewParallelProcessor;
import de.schmiereck.smkEasyNN.genEden.view.PartModel;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.Objects;

public class HexGridController {

    public static final double X_SPACE = (2.0D + 1.0D);
    public static final double X_OFFSET_0 = (0.5D);
    public static final double X_OFFSET_1 = (1.5D);
    public static final double Y_SPACE = (Math.sqrt(3) / 2.0D);

    //public static final double SCALE = 1.0D;//2.0D;
    public static double SCALE = 0.75D;//2.0D;
    public static double STROKE_WIDTH = 1.0D * SCALE;
    public static double FIELD_STROKE_WIDTH = 2.0D * SCALE;
    public static final Color FIELD_DIR_COLOR = Color.color(1.0D, 1.0D, 0.0D);

    private HexGridService hexGridService;

    private HexGridModel hexGridModel;

    private AnimationTimer animationTimer;

    @FXML
    private Label counterText;

    @FXML
    private Pane mainPane;

    private final FileChooser fileChooser = new FileChooser();
    private final String directoryName = "./data/";
    private File lastFile = new File("%sgenetic-a.genEden.json".formatted(directoryName));

    public static boolean demoMode = false;
    private final ServiceContext serviceContext;
    public static boolean threadMode = true;

    public HexGridController() {
        this.serviceContext = new ServiceContext();
        this.hexGridService = new HexGridService(this.serviceContext);
        final PartServiceInterface partService;
        if (HexGridController.demoMode) {
            partService = new DemoPartService(this.hexGridService);
        } else {
            partService = new GeneticPartService(this.hexGridService);
        }
        this.serviceContext.setPartService(partService);
    }

    @FXML
    public void initialize() {
        if (HexGridController.demoMode) {
            this.hexGridService.init(50*2, 46*4);
            SCALE = 0.75D * 0.6D;
        } else {
            this.hexGridService.init(35*2, 27*4);
            //this.hexGridService.init(40*2, 40*3);
            SCALE = 0.75D;//2.0D;
        }
        STROKE_WIDTH = 1.0D * SCALE;
        FIELD_STROKE_WIDTH = 2.0D * SCALE;

        final HexGrid hexGrid = this.hexGridService.retrieveHexGrid();

        this.hexGridModel = new HexGridModel(hexGrid.getXSize(), hexGrid.getYSize(), 10.0D * SCALE);
        this.initHexGridModel(this.hexGridModel);

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
        hexGridModel.generationCount = this.serviceContext.getPartService().retrieveGenerationCount();

        for (int yPos = 0; yPos < hexGridModel.ySize; yPos++) {
            for (int xPos = 0; xPos < hexGridModel.xSize; xPos++) {
                final GridNode gridNode = this.hexGridService.retrieveGridNode(xPos, yPos);
                final Part outPart = gridNode.getOutPart();
                final HexCellModel hexCellModel = this.hexGridModel.grid[xPos][yPos];
                if (Objects.nonNull(outPart)) {
                    hexCellModel.partModel.isPart = true;
                    hexCellModel.partModel.visibleValueArr = outPart.getValueFieldArr();
                    if (outPart instanceof GeneticPart) {
                        final GeneticPart geneticPart = (GeneticPart) outPart;
                        hexCellModel.partModel.moveDir = geneticPart.getMoveDir();
                    }
                } else {
                    hexCellModel.partModel.isPart = false;
                    hexCellModel.partModel.visibleValueArr = null;
                }
                for (final HexDir hexDir : HexDir.values()) {
                    final Field field = gridNode.getField(hexDir);
                    if (HexGridController.demoMode) {
                        hexCellModel.fieldArrArr[hexDir.ordinal()][0] = field.outValueArr[0];
                        hexCellModel.fieldArrArr[hexDir.ordinal()][1] = field.outValueArr[1];
                        hexCellModel.fieldArrArr[hexDir.ordinal()][2] = field.outValueArr[2];
                    } else {
                        hexCellModel.fieldArrArr[hexDir.ordinal()][0] = Math.abs(Math.tanh(field.outComArr[0]));
                        hexCellModel.fieldArrArr[hexDir.ordinal()][1] = Math.abs(Math.tanh(field.outComArr[1]));
                        hexCellModel.fieldArrArr[hexDir.ordinal()][2] = Math.abs(Math.tanh(field.outComArr[2]));
                    }
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
        this.hexGridService.calcNext();

        this.updateHexGridModel(this.hexGridModel);

        this.updateView();
    }

    private final HexGridViewParallelProcessor hexGridViewProc = new HexGridViewParallelProcessor();

    public void updateView() {
        this.counterText.setText(String.format("Step: %d (Parts: %,d), Generation: %d",
                this.hexGridModel.stepCount, this.hexGridModel.partCount, this.hexGridModel.generationCount));

        if (threadMode) {
            this.hexGridViewProc.processHexGrid(this, this.hexGridModel);
        } else {
            this.updateHexGridView(this.hexGridModel, 0, 0, hexGridModel.xSize - 1, hexGridModel.ySize - 1);
        }
    }

    private void initHexGridModel(final HexGridModel hexGridModel) {
        final double xBordOffset = hexGridModel.size + (STROKE_WIDTH / 2.0D);
        final double yBordOffset = hexGridModel.size * Y_SPACE + (STROKE_WIDTH / 2.0D);

        for (int yPos = 0; yPos < hexGridModel.ySize; yPos++) {
            for (int xPos = 0; xPos < hexGridModel.xSize; xPos++) {
                final double offsetX = yPos % 2 == 0 ? X_OFFSET_0 : hexGridModel.size * X_OFFSET_1;

                final Polygon hexagon = createHexagon(xBordOffset, yBordOffset, offsetX, hexGridModel.size - STROKE_WIDTH, xPos, yPos);
                this.mainPane.getChildren().add(hexagon);

                final Line dirArr[] = createDirArr(xBordOffset, yBordOffset, offsetX, hexGridModel.size, xPos, yPos);
                for (int edgePos = 0; edgePos < 6; edgePos++) {
                    this.mainPane.getChildren().add(dirArr[edgePos]);
                }

                final HexCellModel hexCellModel = new HexCellModel(hexagon, dirArr);
                hexCellModel.partModel = new PartModel();
                hexGridModel.grid[xPos][yPos] = hexCellModel;
            }
        }
    }

    private final static ColorCache colorCache = new ColorCache();

    public void updateHexGridView(final HexGridModel hexGridModel, final int xStartPos, final int yStartPos, final int xEndPos, final int yEndPos) {
        for (int yPos = yStartPos; yPos <= yEndPos; yPos++) {
            for (int xPos = xStartPos; xPos <= xEndPos; xPos++) {
                final HexCellModel hexCellModel = hexGridModel.grid[xPos][yPos];
                final Polygon hexagon = hexCellModel.hexagon;
                final Color partColor;
                final PartModel partModel = hexGridModel.grid[xPos][yPos].partModel;
                if (partModel.isPart) {
                    //partColor = Color.CORAL;
                    //partColor = Color.color(
                    partColor = colorCache.retrieveColor(
                            1.0D - (partModel.visibleValueArr[0]),
                            1.0D - (partModel.visibleValueArr[1]),
                            1.0D - (partModel.visibleValueArr[2]));
                    for (final HexDir hexDir : HexDir.values()) {
                        final Color strokeColor;
                        if (hexDir == partModel.moveDir) {
                            strokeColor = partColor;
                        } else  {
                            strokeColor = FIELD_DIR_COLOR;
                        }
                        hexCellModel.dirArr[hexDir.ordinal()].setStroke(strokeColor);
                    }
                } else {
                    for (final HexDir hexDir : HexDir.values()) {
                        final double[] fieldArr = hexCellModel.fieldArrArr[hexDir.ordinal()];
                        //if (field > 0.0D) {
                        //    hexCellModel.dirArr[inDir.ordinal()].setStroke(Color.BLUE);//color(0.0D, 0.0D, field / 2.0D));
                        //} else {
                        //    hexCellModel.dirArr[inDir.ordinal()].setStroke(Color.BLACK);
                        //}
                        hexCellModel.dirArr[hexDir.ordinal()].setStroke(
                                //Color.color(
                                colorCache.retrieveColor(
                                        Math.abs(1.0D - (fieldArr[0])),
                                        Math.abs(1.0D - (fieldArr[1])),
                                        Math.abs(1.0D - (fieldArr[2]))));
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

    @FXML
    protected void onSaveButtonClick() {
        final Window window = this.mainPane.getScene().getWindow();

        this.fileChooser.setInitialDirectory(this.lastFile.getAbsoluteFile().getParentFile());
        this.fileChooser.setInitialFileName(this.lastFile.getAbsoluteFile().getName());

        final File file = this.fileChooser.showSaveDialog(window);
        if (file != null) {
            this.lastFile = file;

            GeneticPersistentService.saveNet(this.lastFile, this.hexGridService.retrievePartList().
                    stream().
                    filter(part -> part instanceof GeneticPart).
                    map(part -> (GeneticPart) part).toList(),
                    this.hexGridService.retrieveStepCount(),
                    this.serviceContext.getPartService().retrieveGenerationCount());
        }
    }

    @FXML
    protected void onLoadButtonClick() {
        final Window window = this.mainPane.getScene().getWindow();

        this.fileChooser.setInitialDirectory(this.lastFile.getAbsoluteFile().getParentFile());
        this.fileChooser.setInitialFileName(this.lastFile.getAbsoluteFile().getName());

        final File file = this.fileChooser.showOpenDialog(window);
        if (file != null) {
            this.lastFile = file;

            final GeneticPersistentService.LoadNetResult loadNetResult = GeneticPersistentService.loadNet(this.lastFile);

            this.hexGridService.submitPartList(loadNetResult.geneticPartList().stream().map(part -> (Part) part).toList());
            this.hexGridService.submitStepCount(loadNetResult.stepCount());
            this.serviceContext.getPartService().submitGenerationCount(loadNetResult.generationCount());

            this.updateHexGridModel(this.hexGridModel);
            this.updateView();
        }
    }

}
