package de.schmiereck.smkEasyNN.genEden;

import de.schmiereck.smkEasyNN.abstractWorld.view.*;
import de.schmiereck.smkEasyNN.genEden.service.*;
import de.schmiereck.smkEasyNN.genEden.service.persistent.GeneticPersistentService;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class GenEdenController extends BaseWorldController<GenEdenHexGridService, GenEdenServiceContext> {
    private final FileChooser fileChooser = new FileChooser();
    private final String directoryName = "./data/";
    private File lastFile = new File("%sgenetic-a.genEden.json".formatted(directoryName));

    public static boolean demoMode = false;

    @Override
    public GenEdenServiceContext createServiceContext() {
        return new GenEdenServiceContext();
    }

    @Override
    public GenEdenHexGridService createHexGridService(final GenEdenServiceContext serviceContext) {
        final GenEdenHexGridService hexGridService = new GenEdenHexGridService(serviceContext);
        final PartServiceInterface partService;
        if (GenEdenController.demoMode) {
            partService = new DemoPartService(hexGridService);
        } else {
            partService = new GeneticPartService(hexGridService);
        }
        serviceContext.setPartService(partService);
        return hexGridService;
    }

    @Override
    public void initHexGridService(final GenEdenHexGridService hexGridService) {
        if (GenEdenController.demoMode) {
            hexGridService.init(33*2, 30*4);
        } else {
            hexGridService.init(35*2, 27*4);
            //this.hexGridService.init(40*2, 40*3);
        }
    }

    @Override
    public HexGridModel createHexGridModel(final HexGrid hexGrid) {
        final HexGridModel hexGridModel;
        if (GenEdenController.demoMode) {
            hexGridModel = new HexGridModel(hexGrid.getXSize(), hexGrid.getYSize(),  0.75D);
            //calcScale(hexGridModel, 0.75D * 0.6D);
        } else {
            hexGridModel = new HexGridModel(hexGrid.getXSize(), hexGrid.getYSize(), 0.7D);//2.0D;
            //calcScale(hexGridModel, 0.75D);//2.0D;
        }
        return hexGridModel;
    }

    @FXML
    protected void onSaveButtonClick() {
        final Window window = this.getMainPane().getScene().getWindow();

        this.fileChooser.setInitialDirectory(this.lastFile.getAbsoluteFile().getParentFile());
        this.fileChooser.setInitialFileName(this.lastFile.getAbsoluteFile().getName());

        final File file = this.fileChooser.showSaveDialog(window);
        if (file != null) {
            this.lastFile = file;

            GeneticPersistentService.saveNet(this.lastFile, this.getHexGridService().retrievePartList().
                    stream().
                    filter(part -> part instanceof GeneticPart).
                    map(part -> (GeneticPart) part).toList(),
                    this.getHexGridService().retrieveStepCount(),
                    this.getServiceContext().getPartService().retrieveGenerationCount());
        }
    }

    @FXML
    protected void onLoadButtonClick() {
        final Window window = this.getMainPane().getScene().getWindow();

        this.fileChooser.setInitialDirectory(this.lastFile.getAbsoluteFile().getParentFile());
        this.fileChooser.setInitialFileName(this.lastFile.getAbsoluteFile().getName());

        final File file = this.fileChooser.showOpenDialog(window);
        if (file != null) {
            this.lastFile = file;

            final GeneticPersistentService.LoadNetResult loadNetResult = GeneticPersistentService.loadNet(this.lastFile);

            this.getHexGridService().submitPartList(loadNetResult.geneticPartList().stream().map(part -> (Part) part).toList());
            this.getHexGridService().submitStepCount(loadNetResult.stepCount());
            this.getServiceContext().getPartService().submitGenerationCount(loadNetResult.generationCount());

            this.updateHexGridModel(this.getHexGridModel());
            this.updateView();
        }
    }

    @Override
    protected void updateHexGridFieldModel(final HexCellModel hexCellModel, final HexDir hexDir, final Field field) {
        if (GenEdenController.demoMode) {
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
