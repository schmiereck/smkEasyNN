package de.schmiereck.smkEasyNN.fieldWorld;

import de.schmiereck.smkEasyNN.abstractWorld.view.BaseWorldController;
import de.schmiereck.smkEasyNN.abstractWorld.view.HexCellModel;
import de.schmiereck.smkEasyNN.abstractWorld.view.HexGridModel;
import de.schmiereck.smkEasyNN.fieldWorld.service.FieldWorlHexGridService;
import de.schmiereck.smkEasyNN.fieldWorld.service.FieldsWorldServiceContext;
import de.schmiereck.smkEasyNN.genEden.service.*;

public class FieldWorldController extends BaseWorldController<FieldWorlHexGridService, FieldsWorldServiceContext> {

    @Override
    public FieldWorlHexGridService createHexGridService(final FieldsWorldServiceContext serviceContext) {
        final FieldWorlHexGridService hexGridService = new FieldWorlHexGridService(serviceContext);
        return hexGridService;
    }

    @Override
    public FieldsWorldServiceContext createServiceContext() {
        return new FieldsWorldServiceContext();
    }

    @Override
    public void initHexGridService(final FieldWorlHexGridService hexGridService) {
        hexGridService.init(16*2, 16*4);
    }

    @Override
    public HexGridModel createHexGridModel(final HexGrid hexGrid) {
        final HexGridModel hexGridModel = new HexGridModel(hexGrid.getXSize(), hexGrid.getYSize(), 1.25D);
        //calcScale(hexGridModel, 1.6D);
        return hexGridModel;
    }

    @Override
    protected void updateHexGridFieldModel(final HexCellModel hexCellModel, final HexDir hexDir, final Field field) {
        hexCellModel.fieldArrArr[hexDir.ordinal()][0] = field.outValueArr[0];
        hexCellModel.fieldArrArr[hexDir.ordinal()][1] = field.outValueArr[1];
        hexCellModel.fieldArrArr[hexDir.ordinal()][2] = field.outValueArr[2];
    }
}
