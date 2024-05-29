package de.schmiereck.smkEasyNN.genEden.service;

import java.util.Objects;

public class GeneticPartService {
    private final HexGridService hexGridService;

    public GeneticPartService(final HexGridService hexGridService) {
        this.hexGridService = hexGridService;
    }

    public void initDemoParts() {
        final int partCount = (this.hexGridService.getXGridSize() * this.hexGridService.getYGridSize()) / 28;
        for (int pos = 0; pos < partCount; pos++) {
            final int xPos = HexGridService.rnd.nextInt(this.hexGridService.getXGridSize() / 2);
            final int yPos = HexGridService.rnd.nextInt(this.hexGridService.getYGridSize());
            final GridNode targetGridNode = this.hexGridService.retrieveGridNode(xPos, yPos);
            if (Objects.isNull(targetGridNode.getOutPart())) {
                final Part part = this.createGeneticPart();
                targetGridNode.setOutPart(part);
            }
        }
    }

    public Part createGeneticPart() {
        final GeneticPart part = new GeneticPart();
        part.dirRotate = HexGridService.rnd.nextInt(HexDir.values().length);
        return part;
    }

    public void calcPart(final GridNode sourceGridNode, final GeneticPart part) {
        final GridNode targetGridNode;
        if (false) {
            final HexDir moveDir = HexDir.values()[HexGridService.rnd.nextInt(HexDir.values().length)];
            targetGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), moveDir);
        } else {
            targetGridNode = sourceGridNode;
        }
        if (Objects.isNull(targetGridNode.getInPart())) {
            targetGridNode.setInPart(part);
        }
    }
}
