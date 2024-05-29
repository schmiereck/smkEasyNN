package de.schmiereck.smkEasyNN.genEden.service;

import de.schmiereck.smkEasyNN.genNet.GenNetService;
import de.schmiereck.smkEasyNN.genNet.GenNetTrainService;

import java.util.List;
import java.util.Objects;

import static de.schmiereck.smkEasyNN.genEden.service.HexGridService.calcOppositeDir;
import static de.schmiereck.smkEasyNN.genEden.service.HexGridService.calcRotateDir;

public class GeneticPartService {
    private static final int MIN_SIZE = 100;
    private static final int MAX_SIZE = 3000;
    private final HexGridService hexGridService;
    private int populationPartCount;

    public GeneticPartService(final HexGridService hexGridService) {
        this.hexGridService = hexGridService;
    }

    public void initGeneticParts() {
        this.populationPartCount = (this.hexGridService.getXGridSize() * this.hexGridService.getYGridSize()) / 5;//28;
        for (int pos = 0; pos < this.populationPartCount; pos++) {
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
        final double[] visibleValueArr = new double[] {
                HexGridService.rnd.nextDouble(0.75D) + 0.25D,
                HexGridService.rnd.nextDouble(0.75D) + 0.25D,
                HexGridService.rnd.nextDouble(0.75D) + 0.25D
        };
        final GeneticPart part = new GeneticPart(visibleValueArr);
        part.moveDir = HexDir.values()[HexGridService.rnd.nextInt(HexDir.values().length)];
        part.size = HexGridService.rnd.nextInt(MIN_SIZE, MAX_SIZE);
        part.energie = HexGridService.rnd.nextInt(part.size / 2, part.size);

        final int[] layerSizeArr = new int[]{
                IN_COUNT,
                //IN_COUNT,
                IN_COUNT / 2 + OUT_COUNT / 2,
                IN_COUNT / 2 + OUT_COUNT / 2,
                //OUT_COUNT,
                OUT_COUNT };

        part.genNet = GenNetService.createNet(layerSizeArr, HexGridService.rnd);

        return part;
    }
    public void calc() {
        final int diffPartCount = this.populationPartCount - this.hexGridService.retrievePartCount();

        if (diffPartCount > this.populationPartCount / 4) {
            this.fillPartPopulation(this.populationPartCount / 2);
        }
    }

    public void fillPartPopulation(final int targetPopulationSize) {
        final List<Part> partList = this.hexGridService.retrievePartList();
        final int partCount = targetPopulationSize - partList.size();
        for (int cnt = 0; cnt < partCount; cnt++) {
            final Part newPart;
            if (partList.size() > 0 || HexGridService.rnd.nextInt(10) >= 1) {
                final GeneticPart sourcePart = (GeneticPart) partList.get(HexGridService.rnd.nextInt(partList.size()));
                final int newEnergie = HexGridService.rnd.nextInt(sourcePart.size / 2) + sourcePart.size / 2;
                newPart = this.mutateGeneticPart(sourcePart, newEnergie, 0.014F, 0.15F);
            } else {
                newPart = this.createGeneticPart();
            }
            do {
                final int xPos = HexGridService.rnd.nextInt(this.hexGridService.getXGridSize());
                final int yPos = HexGridService.rnd.nextInt(this.hexGridService.getYGridSize());
                final GridNode targetGridNode = this.hexGridService.retrieveGridNode(xPos, yPos);
                if (Objects.isNull(targetGridNode.getOutPart())) {
                    targetGridNode.setOutPart(newPart);
                    break;
                }
            } while (true);
        }
    }

    public void calcPart(final GridNode sourceGridNode, final GeneticPart part) {
        if (part.energie > 0) {
            this.consumeEnergie(part, 1);
            this.calcPartNet(sourceGridNode, part);

            final int rotateValue = this.calcRotate(part);
            if (rotateValue != 0) {
                part.moveDir = calcRotateDir(part.moveDir, rotateValue);
            }

            final GridNode targetGridNode;
            if (this.calcMove(part)) {
                targetGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), part.moveDir);
                this.consumeEnergie(part, 2);
            } else {
                targetGridNode = sourceGridNode;
            }

            final int attackValue = this.calcAttack(part);
            if (attackValue > 0) {
                final int usedAttackValue = this.consumeEnergie(part, attackValue / 10 + 1);
                final GridNode attackGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), part.moveDir);
                final Part attackPart = attackGridNode.getOutPart();
                if (Objects.nonNull(attackPart) && attackPart instanceof GeneticPart) {
                    final int eatValue = this.consumeEnergie((GeneticPart)attackPart, usedAttackValue);
                    this.addEnergie(part, usedAttackValue + eatValue);
                }
            }

            for (final HexDir hexDir : HexDir.values()) {
                if (this.calcPopulate(part, hexDir)) {
                    final GridNode outGridNode = this.hexGridService.retrieveGridNode(targetGridNode.getXPos(), targetGridNode.getYPos(), hexDir);
                    final Part targetPart = outGridNode.getInPart();
                    if (Objects.isNull(targetPart)) {
                        final GeneticPart newPart = this.populateGeneticPart(part);
                        outGridNode.setInPart(newPart);
                        this.consumeEnergie(part, newPart.energie);
                    } else {
                        this.consumeEnergie(part, 6);
                    }
                }
            }

            if (Objects.isNull(targetGridNode.getInPart())) {
                targetGridNode.setInPart(part);
            } else {
                sourceGridNode.setInPart(part);
            }
        }
    }

    private static final int IN_ENERGIE = 0;
    private static final int IN_SIZE = 1;
    private static final int IN_FIELD_R[] = { 2, 3, 4, 5, 6, 7 };
    private static final int IN_FIELD_G[] = { 8, 9, 10, 11, 12, 13 };
    private static final int IN_FIELD_B[] = { 14, 15, 16, 17, 18, 19 };
    private static final int IN_PART_R[] = { 20, 21, 22, 23, 24, 25 };
    private static final int IN_PART_G[] = { 26, 27, 28, 29, 30, 31};
    private static final int IN_PART_B[] = { 32, 33, 34, 35, 36, 37};
    private static final int IN_PART_ENEGRIE[] = { 38, 39, 40, 41, 42, 43 };
    private static final int IN_THIS_R = 44;
    private static final int IN_THIS_G = 45;
    private static final int IN_THIS_B = 46;

    private static final int IN_THIS_MOVE = 47;
    private static final int IN_THIS_ROTATE = 48;
    private static final int IN_THIS_POPULATE[] = { 49, 50, 51, 52, 53, 54 };
    private static final int IN_THIS_ATTACK = 56;

    private static final int IN_COUNT = 57;

    private void calcPartNet(final GridNode sourceGridNode, final GeneticPart part) {
        GenNetService.submitInputValue(part.genNet, IN_ENERGIE, part.energie);
        GenNetService.submitInputValue(part.genNet, IN_SIZE, part.size);
        GenNetService.submitInputValue(part.genNet, IN_THIS_R, (float) part.getVisibleValueArr()[0]);
        GenNetService.submitInputValue(part.genNet, IN_THIS_G, (float) part.getVisibleValueArr()[1]);
        GenNetService.submitInputValue(part.genNet, IN_THIS_B, (float) part.getVisibleValueArr()[2]);
        GenNetService.submitInputValue(part.genNet, IN_THIS_MOVE, GenNetService.retrieveOutputValue(part.genNet, OUT_MOVE));
        GenNetService.submitInputValue(part.genNet, IN_THIS_ROTATE, GenNetService.retrieveOutputValue(part.genNet, OUT_ROTATE));
        for (final HexDir hexDir : HexDir.values()) {
            GenNetService.submitInputValue(part.genNet, IN_THIS_POPULATE[hexDir.ordinal()],
                    GenNetService.retrieveOutputValue(part.genNet, OUT_POPULATE[hexDir.ordinal()]));
        }
        GenNetService.submitInputValue(part.genNet, IN_THIS_ATTACK, GenNetService.retrieveOutputValue(part.genNet, OUT_ATTACK));

        for (final HexDir hexDir : HexDir.values()) {
            final HexDir inputHexDir = calcRotateDir(part.moveDir, hexDir);
            final GridNode targetGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), inputHexDir);
            final Field inputField = targetGridNode.getField(calcOppositeDir(inputHexDir));
            GenNetService.submitInputValue(part.genNet, IN_FIELD_R[hexDir.ordinal()], (float) inputField.outValueArr[0]);
            GenNetService.submitInputValue(part.genNet, IN_FIELD_G[hexDir.ordinal()], (float) inputField.outValueArr[1]);
            GenNetService.submitInputValue(part.genNet, IN_FIELD_B[hexDir.ordinal()], (float) inputField.outValueArr[2]);
            final Part targetOutPart = targetGridNode.getOutPart();
            if (Objects.nonNull(targetOutPart) && targetOutPart instanceof GeneticPart) {
                final GeneticPart targetGenPart = (GeneticPart) targetOutPart;
                double[] visibleValueArr = targetGenPart.getVisibleValueArr();
                GenNetService.submitInputValue(part.genNet, IN_PART_R[hexDir.ordinal()], (float) visibleValueArr[0]);
                GenNetService.submitInputValue(part.genNet, IN_PART_G[hexDir.ordinal()], (float) visibleValueArr[1]);
                GenNetService.submitInputValue(part.genNet, IN_PART_B[hexDir.ordinal()], (float) visibleValueArr[2]);
                GenNetService.submitInputValue(part.genNet, IN_PART_ENEGRIE[hexDir.ordinal()], (float) targetGenPart.energie);
            } else {
                GenNetService.submitInputValue(part.genNet, IN_PART_R[hexDir.ordinal()], 0.0F);
                GenNetService.submitInputValue(part.genNet, IN_PART_G[hexDir.ordinal()], 0.0F);
                GenNetService.submitInputValue(part.genNet, IN_PART_B[hexDir.ordinal()], 0.0F);
                GenNetService.submitInputValue(part.genNet, IN_PART_ENEGRIE[hexDir.ordinal()], -1.0F);
            }
        }

        GenNetService.calc(part.genNet);
    }

    private GeneticPart populateGeneticPart(final GeneticPart sourcePart) {
        final GeneticPart newPart = this.mutateGeneticPart(sourcePart, sourcePart.energie / 2, 0.014F, 0.05F);
        newPart.energie = sourcePart.energie / 2;
        return newPart;
    }

    private GeneticPart mutateGeneticPart(final GeneticPart sourcePart, final int newEnergie, final float minMutationRate, final float maxMutationRate) {
        final double[] sourceVisibleValueArr = sourcePart.getVisibleValueArr();
        final double[] visibleValueArr = new double[] {
                this.mutateValue(0.0D, 1.0D, 0.01D, sourceVisibleValueArr[0]),
                this.mutateValue(0.0D, 1.0D, 0.01D, sourceVisibleValueArr[1]),
                this.mutateValue(0.0D, 1.0D, 0.01D, sourceVisibleValueArr[2])
        };
        final float mutationRate = GenNetTrainService.calcMutationRate(minMutationRate, maxMutationRate, HexGridService.rnd);
        final GeneticPart newPart = new GeneticPart(visibleValueArr);
        newPart.moveDir = sourcePart.moveDir;
        newPart.size = this.mutateValue(MIN_SIZE, MAX_SIZE, 1, sourcePart.size);
        newPart.energie = newEnergie;
        newPart.genNet = GenNetTrainService.createMutatedNet(sourcePart.genNet, mutationRate, HexGridService.rnd);
        return newPart;
    }

    private int mutateValue(final int minValue, final int maxValue, final int mutateValue, final int value) {
        final int newValue = value + HexGridService.rnd.nextInt(mutateValue * 2) - mutateValue;
        return Math.min(maxValue, Math.max(minValue, newValue));
    }

    private double mutateValue(final double minValue, final double maxValue, final double mutateValue, final double value) {
        final double newValue = value + HexGridService.rnd.nextDouble(mutateValue * 2) - mutateValue;
        return Math.min(maxValue, Math.max(minValue, newValue));
    }

    private int addEnergie(final GeneticPart part, final int value) {
        final int usedValue = Math.min(part.size - part.energie, value);
        part.energie += usedValue;
        return value - usedValue;
    }

    private int consumeEnergie(final GeneticPart part, final int value) {
        final int usedValue = Math.min(part.energie, value);
        part.energie -= usedValue;
        return usedValue;
    }

    private static final int OUT_MOVE = 0;
    private static final int OUT_ROTATE = 1;
    private static final int OUT_POPULATE[] = { 2, 3, 4, 5, 6, 7 };
    private static final int OUT_ATTACK = 8;
    private static final int OUT_COUNT = 9;

    private boolean calcMove(final GeneticPart part) {
        final float outputValue = GenNetService.retrieveOutputValue(part.genNet, OUT_MOVE);
        return outputValue > 0.5F;
    }

    private int calcRotate(final GeneticPart part) {
        final float outputValue = GenNetService.retrieveOutputValue(part.genNet, OUT_ROTATE);
        if (outputValue > 0.5F) {
            return 1;
        } else {
            if (outputValue < -0.5F) {
                return -1;
            } else {
                return 0;
            }
        }
    }
    private boolean calcPopulate(final GeneticPart part, final HexDir hexDir) {
        final float outputValue = GenNetService.retrieveOutputValue(part.genNet, OUT_POPULATE[hexDir.ordinal()]);
        return outputValue > 0.5F;
    }

    private int calcAttack(final GeneticPart part) {
        final float outputValue = GenNetService.retrieveOutputValue(part.genNet, OUT_ATTACK);
        return (int) (outputValue * part.size);
    }

}
