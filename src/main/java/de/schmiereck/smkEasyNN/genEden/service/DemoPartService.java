package de.schmiereck.smkEasyNN.genEden.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DemoPartService {
    private static int MAX_MOVE_CNT = 4;

    private final HexGridService hexGridService;

    public DemoPartService(final HexGridService hexGridService) {
        this.hexGridService = hexGridService;
    }

    public List<Part> initDemoParts() {
        final List<Part> partList = new ArrayList<>();
        this.hexGridService.retrieveGridNode(2, 7).setOutPart(this.createDemoPart());
        this.hexGridService.retrieveGridNode(12, 14).setOutPart(this.createDemoPart());

        final int partCount = (this.hexGridService.getXGridSize() * this.hexGridService.getYGridSize()) / 28;
        for (int pos = 0; pos < partCount; pos++) {
            final int xPos = HexGridService.rnd.nextInt(this.hexGridService.getXGridSize() / 3);
            final int yPos = HexGridService.rnd.nextInt(this.hexGridService.getYGridSize());
            final GridNode targetGridNode = this.hexGridService.retrieveGridNode(xPos, yPos);
            if (Objects.isNull(targetGridNode.getOutPart())) {
                final Part part = this.createDemoPart();
                targetGridNode.setOutPart(part);
                partList.add(part);
            }
        }
        return partList;
    }

    public Part createDemoPart() {
        final double[] visibleValueArr = new double[] {
                0.5D,
                0.5D,
                //HexGridService.rnd.nextDouble(0.25D) + 0.75D,
                //HexGridService.rnd.nextDouble(0.25D) + 0.75D
                1.0D
        };
        final DemoPart part = new DemoPart(visibleValueArr);
        part.moveCnt = HexGridService.rnd.nextInt(MAX_MOVE_CNT);
        return part;
    }

    public void calcPart(final GridNode sourceGridNode, final DemoPart part) {
        final GridNode targetGridNode;
        if (part.moveCnt >= MAX_MOVE_CNT) {
            //double targetFieldValue = 0.0D;//Double.MAX_VALUE;
            double targetFieldValue = Double.MAX_VALUE;
            HexDir targetDir = null;
            final int startDirOrdinal;
            if (Objects.isNull(part.lastDir)) {
                startDirOrdinal = HexGridService.rnd.nextInt(HexDir.values().length);
            } else {
                startDirOrdinal = part.lastDir.ordinal();
            }
            for (final HexDir posHexDir : HexDir.values()) {
                final HexDir hexDir = HexDir.values()[(startDirOrdinal + posHexDir.ordinal()) % HexDir.values().length];
                final GridNode moveGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), hexDir);
                if (Objects.isNull(moveGridNode.getInPart()) && Objects.isNull(moveGridNode.getOutPart())) {
                    final HexDir oppositeHexDir = HexDirUtils.calcOppositeDir(hexDir);
                    final double dirFieldValue = sumFieldArr(moveGridNode.getField(oppositeHexDir).outValueArr);
                    if (dirFieldValue < targetFieldValue) {
                        targetFieldValue = dirFieldValue;
                        targetDir = hexDir;
                    }
                }
            }
            final HexDir moveDir;
            if (Objects.isNull(targetDir)) {
                moveDir = HexDir.values()[HexGridService.rnd.nextInt(HexDir.values().length)];
            } else {
                moveDir = targetDir;
            }

            part.lastDir = moveDir;
            part.moveCnt = 0;
            targetGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), moveDir);
        } else {
            part.moveCnt++;
            targetGridNode = sourceGridNode;
        }

        if (Objects.isNull(targetGridNode.getInPart())) {
            targetGridNode.setInPart(part);
        }
    }

    private static double sumFieldArr(final double[] outValueArr) {
        return outValueArr[0] + outValueArr[1] + outValueArr[2];
    }
}
