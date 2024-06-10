package de.schmiereck.smkEasyNN.genEden.service;

import java.util.Objects;

import static de.schmiereck.smkEasyNN.genEden.service.FieldUtils.*;
import static de.schmiereck.smkEasyNN.genEden.service.FieldUtils.resetInField;

public class FieldService {

    /**
     * 1. Field: Out -> In
     */
    public void calcGridNodeFieldOutToIn(final HexGridService hexGridService, final GridNode gridNode, final int xPos, final int yPos) {
        //final InDir inDir = InDir.InDir5; {
        for (final HexDir hexDir : HexDir.values()) {
            final Field field = gridNode.getField(hexDir);

            final double[] outValueFieldArr = field.outValueArr;
            double outValueFieldSum = outValueFieldArr[0] + outValueFieldArr[1] + outValueFieldArr[2];
            //field.outValue = 0.0D;
            //if (outValueFieldSum > 0.021D) {
            //if (outValueFieldSum > 0.042D) {
            //if (outValueFieldSum > (0.035D * 7.0D * 3)) {
            if (outValueFieldSum > (0.01D * 7.0D * 3)) {
                //if (outValueFieldSum > 0.028D) {
                {
                    final GridNode outGridNode = hexGridService.retrieveGridNode(xPos, yPos, hexDir);
                    if (Objects.isNull(outGridNode.getOutPart())) {
                        calcInValueField(outGridNode, hexDir, field, 1.0D / 7.0D - 0.05D);
                    }
                }
                {
                    final HexDir outLHexDir = HexDirUtils.calcOffDir(hexDir, +1);
                    final GridNode outLGridNode = hexGridService.retrieveGridNode(xPos, yPos, outLHexDir);
                    if (Objects.isNull(outLGridNode.getOutPart())) {
                        calcInValueField(outLGridNode, hexDir, field, 3.0D / 7.0D - 0.05D);
                    }
                }
                {
                    final HexDir outRHexDir = HexDirUtils.calcOffDir(hexDir, -1);
                    final GridNode outRGridNode = hexGridService.retrieveGridNode(xPos, yPos, outRHexDir);
                    if (Objects.isNull(outRGridNode.getOutPart())) {
                        calcInValueField(outRGridNode, hexDir, field, 3.0D / 7.0D - 0.05D);
                    }
                }
            }

            final double[] outComFieldArr = field.outComArr;
            double outComFieldSum = outComFieldArr[0] + outComFieldArr[1] + outComFieldArr[2];
            if (outComFieldSum > (0.01D * 7.0D * 3)) {
                {
                    final GridNode outGridNode = hexGridService.retrieveGridNode(xPos, yPos, hexDir);
                    if (Objects.isNull(outGridNode.getOutPart())) {
                        calcInComField(outGridNode, hexDir, field, 1.0D / 7.0D - 0.05D);
                    }
                }
                {
                    final HexDir outLHexDir = HexDirUtils.calcOffDir(hexDir, +1);
                    final GridNode outLGridNode = hexGridService.retrieveGridNode(xPos, yPos, outLHexDir);
                    if (Objects.isNull(outLGridNode.getOutPart())) {
                        calcInComField(outLGridNode, hexDir, field, 3.0D / 7.0D - 0.05D);
                    }
                }
                {
                    final HexDir outRHexDir = HexDirUtils.calcOffDir(hexDir, -1);
                    final GridNode outRGridNode = hexGridService.retrieveGridNode(xPos, yPos, outRHexDir);
                    if (Objects.isNull(outRGridNode.getOutPart())) {
                        calcInComField(outRGridNode, hexDir, field, 3.0D / 7.0D - 0.05D);
                    }
                }
            }
        }
    }

    /**
     * 2. Field: In -> Out
     */
    public void calcGridNodeFieldInToOut(final GridNode gridNode, final HexDir hexDir) {
        final Field field = gridNode.getField(hexDir);
        transferFieldInToOut(field);
        resetInField(field);
    }

    public void calcGridNodeOutField(final GridNode gridNode) {
        final Part outPart = gridNode.getOutPart();

        if (Objects.nonNull(outPart)) {
            for (final HexDir hexDir : HexDir.values()) {
                //final GridNode outGridNode = this.retrieveGridNode(xPos, yPos, hexDir);
                //final Field field = outGridNode.getField(hexDir);
                final Field field = gridNode.getField(hexDir);

                final double[] visibleValueArr = outPart.getValueFieldArr();
                field.outValueArr[0] = visibleValueArr[0];
                field.outValueArr[1] = visibleValueArr[1];
                field.outValueArr[2] = visibleValueArr[2];

                final double[] comFieldArr = outPart.getComFieldArr();
                field.outComArr[0] = comFieldArr[0];
                field.outComArr[1] = comFieldArr[1];
                field.outComArr[2] = comFieldArr[2];
            }
        }
    }

    private static void calcInValueField(final GridNode outGridNode, final HexDir hexDir, final Field outField, final double factor) {
        if (Objects.isNull(outGridNode.getOutPart())) {
            final Field inField = outGridNode.getField(hexDir);
            transferValueFieldOutToIn(outField, inField, factor);
        }
    }

    private static void calcInComField(final GridNode outGridNode, final HexDir hexDir, final Field outField, final double factor) {
        if (Objects.isNull(outGridNode.getOutPart())) {
            final Field inField = outGridNode.getField(hexDir);
            transferComFieldOutToIn(outField, inField, factor);
        }
    }
}
