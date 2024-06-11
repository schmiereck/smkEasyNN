package de.schmiereck.smkEasyNN.genEden;

import de.schmiereck.smkEasyNN.genEden.service.*;
import de.schmiereck.smkEasyNN.genEden.service.persistent.GeneticPersistentService;
import de.schmiereck.smkEasyNN.genEden.view.HexGridViewParallelProcessor;
import de.schmiereck.smkEasyNN.genEden.view.PartModel;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.Objects;

public class HexGridController {
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

    private static void calcScale(final HexGridModel hexGridModel, final double newScale) {
        HexGridModel.SCALE = newScale;//2.0D;
        HexGridModel.STROKE_WIDTH = 1.0D * HexGridModel.SCALE;
        HexGridModel.FIELD_STROKE_WIDTH = 2.0D * HexGridModel.SCALE;
        hexGridModel.xBordOffset = hexGridModel.size + (HexGridModel.STROKE_WIDTH / 2.0D);
        hexGridModel.yBordOffset = hexGridModel.size * HexGridModel.Y_SPACE + (HexGridModel.STROKE_WIDTH / 2.0D);
    }

    @FXML
    public void initialize() {
        if (HexGridController.demoMode) {
            this.hexGridService.init(50*2, 46*4);
        } else {
            this.hexGridService.init(35*2, 27*4);
            //this.hexGridService.init(40*2, 40*3);
        }

        final HexGrid hexGrid = this.hexGridService.retrieveHexGrid();

        this.hexGridModel = new HexGridModel(hexGrid.getXSize(), hexGrid.getYSize(), 10.0D * HexGridModel.SCALE);
        if (HexGridController.demoMode) {
            calcScale(this.hexGridModel, 0.75D * 0.6D);
        } else {
            calcScale(this.hexGridModel, 0.75D);//2.0D;
        }

        this.initHexGridModel(this.hexGridModel, this.mainPane);

        this.updateHexGridModel(this.hexGridModel);

        this.updateView();

        //--------------------------------------------------------------------------------------------------------------
        final ObjectProperty<Point2D> dragLastMouseCoordinates = new SimpleObjectProperty<>();
        final ObjectProperty<Boolean> dragOperation = new SimpleObjectProperty<>();
        dragOperation.set(Boolean.FALSE);

        this.mainPane.setOnMouseClicked(event -> {
            if (dragOperation.get() == Boolean.FALSE) {
                final Point2D sceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
                final Point2D anchorPaneCoords = mainPane.sceneToLocal(sceneCoords);
                final double x = anchorPaneCoords.getX() - this.hexGridModel.xBordOffset;
                final double y = anchorPaneCoords.getY() - this.hexGridModel.yBordOffset;

                //final double offsetX = yPos % 2 == 0 ? HexGridModel.X_OFFSET_0 : hexGridModel.size * HexGridModel.X_OFFSET_1;
                //hexagon.setLayoutX(xBordOffset + (xPos * (hexGridModel.size * X_SPACE)) + offsetX);
                //hexagon.setLayoutY(yBordOffset + (yPos * (hexGridModel.size * Y_SPACE)));

                final double offsetX = (((int)(y / hexGridModel.size)) % 2) == 0 ? hexGridModel.size * HexGridModel.X_OFFSET_0 : hexGridModel.size * HexGridModel.X_OFFSET_1;
                //final int posX = (int) ((x - hexGridModel.size) / (hexGridModel.size * HexGridModel.X_SPACE));// - ((int)this.hexGridModel.xBordOffset);
                //final int posY = (int) ((y - hexGridModel.size) / (hexGridModel.size * HexGridModel.Y_SPACE));// - ((int)this.hexGridModel.yBordOffset);
                //final int posX = (int) ((x + hexGridModel.size / 2 - ((int)this.hexGridModel.xBordOffset)) / (hexGridModel.size * HexGridModel.X_SPACE));// - ((int)this.hexGridModel.xBordOffset);
                //final int posY = (int) ((y + hexGridModel.size / 2 - ((int)this.hexGridModel.yBordOffset)) / (hexGridModel.size * HexGridModel.Y_SPACE));// - ((int)this.hexGridModel.yBordOffset);
                final int posX = (int) ((x + offsetX) / (hexGridModel.size * HexGridModel.X_SPACE));// - ((int)this.hexGridModel.xBordOffset);
                final int posY = (int) ((y) / (hexGridModel.size * HexGridModel.Y_SPACE));// - ((int)this.hexGridModel.yBordOffset);
                HexCellModel hexCellModel = this.hexGridModel.grid[posX][posY];
                if (hexCellModel.partModel.isPart) {
                    if (this.hexGridModel.selectedHexCellModel == hexCellModel) {
                        this.hexGridModel.selectedHexCellModel = null;
                    } else {
                        this.hexGridModel.selectedHexCellModel = hexCellModel;
                    }
                    this.updateView();
                } else {
                    if (Objects.nonNull(this.hexGridModel.selectedHexCellModel)) {
                        this.hexGridModel.selectedHexCellModel = null;
                        this.updateView();
                    }
                }

                event.consume();
            } else {
                dragOperation.set(Boolean.FALSE);
            }
        });

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

    private void initHexGridModel(final HexGridModel hexGridModel, final Pane mainPane) {
        //final double xBordOffset = hexGridModel.size + (HexGridModel.STROKE_WIDTH / 2.0D);
        //final double yBordOffset = hexGridModel.size * HexGridModel.Y_SPACE + (HexGridModel.STROKE_WIDTH / 2.0D);

        for (int yPos = 0; yPos < hexGridModel.ySize; yPos++) {
            for (int xPos = 0; xPos < hexGridModel.xSize; xPos++) {
                final double offsetX = yPos % 2 == 0 ? hexGridModel.size * HexGridModel.X_OFFSET_0 : hexGridModel.size * HexGridModel.X_OFFSET_1;

                final Polygon hexagon = createHexagon(this.hexGridModel, hexGridModel.xBordOffset, hexGridModel.yBordOffset, offsetX, hexGridModel.size - HexGridModel.STROKE_WIDTH, xPos, yPos);
                mainPane.getChildren().add(hexagon);

                final Line dirArr[] = createDirArr(hexGridModel.xBordOffset, hexGridModel.yBordOffset, offsetX, hexGridModel.size, xPos, yPos);
                for (int edgePos = 0; edgePos < 6; edgePos++) {
                    mainPane.getChildren().add(dirArr[edgePos]);
                }

                final HexCellModel hexCellModel = new HexCellModel(hexagon, dirArr);
                hexCellModel.partModel = new PartModel();
                hexGridModel.grid[xPos][yPos] = hexCellModel;
            }
        }
        final Circle circle = new Circle(hexGridModel.size);
        circle.setFill(null);
        circle.setStroke(Color.DARKRED);
        circle.setCenterX(0.0D);
        circle.setCenterY(0.0D);
        circle.setVisible(false);
        mainPane.getChildren().add(circle);
        hexGridModel.selectionMarkerShape = circle;
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

        final HexCellModel selectedHexCellModel = this.hexGridModel.selectedHexCellModel;
        final Circle selectionMarkerShape = hexGridModel.selectionMarkerShape;
        if (Objects.nonNull(selectedHexCellModel)) {
            selectionMarkerShape.setCenterX(selectedHexCellModel.hexagon.getLayoutX());
            selectionMarkerShape.setCenterY(selectedHexCellModel.hexagon.getLayoutY());
            selectionMarkerShape.setVisible(true);
        } else {
            selectionMarkerShape.setVisible(false);
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

    private Polygon createHexagon(final HexGridModel hexGridModel, final double xBordOffset, final double yBordOffset, final double offsetX,
                                  final double size, final int xPos, final int yPos) {
        final Polygon hexagon = new Polygon();
        for (int edgePos = 0; edgePos < 6; edgePos++) {
            final double x = edgePos * Math.PI / 3;
            hexagon.getPoints().addAll(size * Math.cos(x), size * Math.sin(x));
        }
        hexagon.setLayoutX(xBordOffset + (xPos * (hexGridModel.size * HexGridModel.X_SPACE)) + offsetX);
        hexagon.setLayoutY(yBordOffset + (yPos * (hexGridModel.size * HexGridModel.Y_SPACE)));
        hexagon.setFill(Color.WHITE);
        hexagon.setStroke(Color.gray(0.975D));
        hexagon.setStrokeWidth(HexGridModel.STROKE_WIDTH);
        return hexagon;
    }

    private Line[] createDirArr(final double xBordOffset, final double yBordOffset, final double offsetX,
                                final double size, final int xPos, final int yPos) {
        final double startSize = HexGridModel.FIELD_STROKE_WIDTH;
        final double endSize = size - (HexGridModel.FIELD_STROKE_WIDTH * 1.35D);
        final Line[] dirArr = new Line[6];
        for (int edgePos = 0; edgePos < 6; edgePos++) {
            final int edge = (edgePos - 1);
            final double d = (edge * (Math.PI / 3.0D)) - (Math.PI / 6.0D);
            final double x = Math.cos(d);
            final double y = Math.sin(d);
            dirArr[edgePos] = new Line(startSize * x, startSize * y,
                    endSize * x, endSize * y);
            dirArr[edgePos].setLayoutX(xBordOffset + (xPos * (hexGridModel.size * HexGridModel.X_SPACE)) + offsetX);
            dirArr[edgePos].setLayoutY(yBordOffset + (yPos * (hexGridModel.size * HexGridModel.Y_SPACE)));
            dirArr[edgePos].setStroke(Color.GREEN);
            dirArr[edgePos].setStrokeWidth(HexGridModel.FIELD_STROKE_WIDTH);
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
