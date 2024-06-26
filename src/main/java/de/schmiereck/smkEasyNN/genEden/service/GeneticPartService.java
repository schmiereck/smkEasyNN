package de.schmiereck.smkEasyNN.genEden.service;

import de.schmiereck.smkEasyNN.genNet.GenNetService;
import de.schmiereck.smkEasyNN.genNet.GenNetTrainService;
import de.schmiereck.smkEasyNN.genNet.GenNetTrainService.GenNetMutateConfig;
import de.schmiereck.smkEasyNN.genNet.GenNeuron;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.schmiereck.smkEasyNN.genEden.service.HexDirUtils.calcOppositeDir;
import static de.schmiereck.smkEasyNN.genEden.service.Part.calcNextPartNr;

public class GeneticPartService implements PartServiceInterface {
    private static final int MIN_SIZE = 100;
    private static final int MAX_SIZE = 1200*2;

    /**
     * Higher values gives smaller populations.
     */
    private static final int POPULATION_DIV = 5; //28;

    /**
     * Higher values gives more selection pressure.
     */
    private static int FILL_POPULATION_DIV = 3;

    private final GenEdenHexGridService hexGridService;
    private int targetPopulationPartCount;

    private int generationCount;
    public static boolean threadMode = false;

    public GeneticPartService(final GenEdenHexGridService hexGridService) {
        this.hexGridService = hexGridService;
    }

    @Override
    public List<Part> initParts() {
        final List<Part> partList = new ArrayList<>();

        for (int xPos = 0; xPos < 20; xPos++) {
            this.createBlocker(5 + xPos, 10);
        }
        for (int yPos = 0; yPos < 40; yPos += 4) {
            this.createBlocker(25, 14 + yPos);
        }

        //this.targetPopulationPartCount = (this.hexGridService.getXGridSize() * this.hexGridService.getYGridSize()) / POPULATION_DIV;
        this.targetPopulationPartCount = 900; // 6480 = 30*2 * 27*4, 2160 = 6480 / 3
        for (int pos = 0; pos < this.targetPopulationPartCount; pos++) {
            final int xPos = GenEdenHexGridService.rnd.nextInt(this.hexGridService.getXGridSize() / 2);
            final int yPos = GenEdenHexGridService.rnd.nextInt(this.hexGridService.getYGridSize());
            final GridNode targetGridNode = this.hexGridService.retrieveGridNode(xPos, yPos);
            if (Objects.isNull(targetGridNode.getOutPart())) {
                final Part part = this.createGeneticPart();
                targetGridNode.setOutPart(part);
                partList.add(part);
            }
        }
        return partList;
    }

    private void createBlocker(final int xPos, final int yPos) {
        final GridNode targetGridNode = this.hexGridService.retrieveGridNode(xPos, yPos);
        if (Objects.isNull(targetGridNode.getOutPart())) {
            final Part part = new BlockerPart(calcNextPartNr(), new double[] { 0.5D, 0.5D, 0.5D });
            targetGridNode.setOutPart(part);
        }
    }

    private final int[] genNetLayerSizeArr = new int[]{
            IN_COUNT,
            IN_COUNT + OUT_COUNT,
            IN_COUNT / 2 + OUT_COUNT,
            IN_COUNT / 2 + OUT_COUNT / 2,
            OUT_COUNT };

    public Part createGeneticPart() {
        final double[] visibleValueArr = new double[] {
                GenEdenHexGridService.rnd.nextDouble(0.75D) + 0.25D,
                GenEdenHexGridService.rnd.nextDouble(0.75D) + 0.25D,
                GenEdenHexGridService.rnd.nextDouble(0.75D) + 0.25D
        };
        final GeneticPart part = new GeneticPart(calcNextPartNr(), visibleValueArr);
        part.moveDir = HexDir.values()[GenEdenHexGridService.rnd.nextInt(HexDir.values().length)];
        part.size = GenEdenHexGridService.rnd.nextInt(MIN_SIZE, MAX_SIZE);
        part.energie = GenEdenHexGridService.rnd.nextInt(part.size / 2, part.size / 1);

        part.genNet = GenNetService.createNet(genNetLayerSizeArr, GenEdenHexGridService.rnd);

        return part;
    }

    private int diffToTargetPartSize = 500;

    @Override
    public void calcParts() {
        final int diffToTargetPartCount = this.targetPopulationPartCount - this.hexGridService.retrievePartCount();

        //if (diffToTargetPartCount > (this.targetPopulationPartCount - (this.targetPopulationPartCount / FILL_POPULATION_DIV))) {
        if (diffToTargetPartCount > diffToTargetPartSize) {
            if (diffToTargetPartSize > 10) {
                diffToTargetPartSize--;
            }
            this.fillPartPopulation(this.targetPopulationPartCount);
        }
    }

    private final HexGridFillPopParallelProcessor fillPopProc = new HexGridFillPopParallelProcessor();

    public void fillPartPopulation(final int targetPopulationSize) {
        final List<Part> partList = this.hexGridService.retrievePartList().
                stream().
                filter(part -> part instanceof GeneticPart).collect(Collectors.toList());

        partList.sort((part1, part2) -> {
            final GeneticPart aGeneticPart = (GeneticPart) part1;
            final GeneticPart bGeneticPart = (GeneticPart) part2;
            //return Integer.compare(bGeneticPart.energie * bGeneticPart.age, aGeneticPart.energie * aGeneticPart.age);
            return Integer.compare(
                    bGeneticPart.energie + bGeneticPart.age / 10,
                    aGeneticPart.energie + aGeneticPart.age / 10);
        });

        // TODO Thread this.

        final int partCount = targetPopulationSize - partList.size();
        final int startCountPos = 0;
        final int endCountPos = partCount - 1;

        if (threadMode) {
            fillPopProc.fillPopHexGrid(this, partList, partCount);
        } else {
            this.fillPartPopulation(partList, startCountPos, endCountPos);
        }

        this.generationCount++;
    }

    public void fillPartPopulation(final List<Part> partList, final int startCountPos, final int endCountPos) {
        for (int cnt = startCountPos; cnt <= endCountPos; cnt++) {
            final Part newPart;
            final int maxXPos;
            final int maxYPos;
            int xPos;
            int yPos;
            if (!partList.isEmpty()) {
                if (GenEdenHexGridService.rnd.nextInt(10) >= 1) {
                    final GeneticPart sourcePart;
                    if (GenEdenHexGridService.rnd.nextInt(10) >= 1) {
                        sourcePart = (GeneticPart) partList.get(GenEdenHexGridService.rnd.nextInt(Math.min(cnt + 1, partList.size())));
                    } else {
                        sourcePart = (GeneticPart) partList.get(GenEdenHexGridService.rnd.nextInt(partList.size()));
                    }
                    if (GenEdenHexGridService.rnd.nextBoolean()) {
                        //final int newEnergie = HexGridService.rnd.nextInt(sourcePart.size / 6, sourcePart.size / 4);
                        //final int newEnergie = HexGridService.rnd.nextInt(sourcePart.size / 2, sourcePart.size / 1);
                        final int newEnergie = sourcePart.getEnergie();
                        newPart = this.mutateGeneticPart(sourcePart, newEnergie, 0.014F, 0.15F);
                    } else {
                        newPart = this.copyGeneticPart(sourcePart);
                    }
                    maxXPos = this.hexGridService.getXGridSize() - (this.hexGridService.getXGridSize() / 8);
                    maxYPos = this.hexGridService.getYGridSize() - (this.hexGridService.getYGridSize() / 5);
                    xPos = sourcePart.getGridNode().getXPos();
                    yPos = sourcePart.getGridNode().getYPos();
                } else {
                    newPart = this.createGeneticPart();
                    maxXPos = this.hexGridService.getXGridSize();
                    maxYPos = this.hexGridService.getYGridSize();
                    xPos = GenEdenHexGridService.rnd.nextInt(maxXPos / 1) * 1;
                    yPos = GenEdenHexGridService.rnd.nextInt(maxYPos / 3) * 3;
                }
            } else {
                newPart = this.createGeneticPart();
                maxXPos = this.hexGridService.getXGridSize();
                maxYPos = this.hexGridService.getYGridSize();
                xPos = GenEdenHexGridService.rnd.nextInt(maxXPos / 1) * 1;
                yPos = GenEdenHexGridService.rnd.nextInt(maxYPos / 3) * 3;
            }
            do {
                final GridNode targetGridNode = this.hexGridService.retrieveGridNode(xPos, yPos);
                if (Objects.isNull(targetGridNode.getOutPart())) {
                    targetGridNode.setOutPart(newPart);
                    break;
                }
                xPos = GenEdenHexGridService.rnd.nextInt(maxXPos / 1) * 1;
                yPos = GenEdenHexGridService.rnd.nextInt(maxYPos / 3) * 3;
            } while (true);
        }
    }

    public void calcPartNetInput(final GridNode sourceGridNode, final GeneticPart part) {
        if (part.energie > 0) {
            this.consumeEnergie(part, 1); // TODO Depend on size of GenNet.
            //this.checkPartConfiguration(part); // TODO Move to Persistence.
            this.calcPartNet(sourceGridNode, part);
        }
    }

    /**
     * 3. Part: Out -> In
     */
    @Override
    public void calcPartOutToIn(final GridNode sourceGridNode, final Part outPart) {
        if (outPart instanceof GeneticPart geneticPart) {
            this.calcGeneticPart(sourceGridNode, geneticPart);
        } else {
            if (outPart instanceof EnergyPart energyPart) {
                if (energyPart.energie > 0) {
                    this.consumeEnergie(energyPart, 1);
                    sourceGridNode.setInPart(outPart);
                }
            } else {
                if (outPart instanceof BlockerPart) {
                    sourceGridNode.setInPart(outPart);
                } else {
                    throw new RuntimeException("Unexpected Part-Type \"%s\".".formatted(outPart.getClass().getSimpleName()));
                }
            }
        }
    }

    public void calcGeneticPart(final GridNode sourceGridNode, final GeneticPart part) {
        //boolean printed = false;
        if (part.energie > 0) {
            //this.consumeEnergie(part, 1);
            //this.checkPartConfiguration(part);
            //this.calcPartNet(sourceGridNode, part);

            final int rotateValue = this.calcRotate(part);
            if (rotateValue != 0) {
                part.moveDir = HexDirUtils.calcRotateDir(part.moveDir, rotateValue);
                this.consumeEnergie(part, 1);
                //if (printed == false) System.out.println();
                //System.out.printf("\tRotate: %,d", rotateValue);
                //printed = true;
            }

            final GridNode targetGridNode;
            if (this.calcMove(part)) {
                targetGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), part.moveDir);
                this.consumeEnergie(part, 1);
                //if (printed == false) System.out.println();
                //System.out.printf("\tMove: %s", part.moveDir.name());
                //printed = true;
            } else {
                targetGridNode = sourceGridNode;
            }

            final int attackValue = this.calcAttack(part);
            if (attackValue > 0) {
                final int costAttackValue = this.consumeEnergie(part, attackValue / 25 + 1);
                final GridNode attackedGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), part.moveDir);
                final Part attackedPart = attackedGridNode.getOutPart();
                if (Objects.nonNull(attackedPart) && attackedPart instanceof EnergyPart attackedEnergyPart) {
                    //final int eatValue = this.consumeEnergie(attackedEnergyPart, Math.min((attackedEnergyPart.energie / 2) + 1, attackValue)); // 10 is temporary
                    final int eatValue = this.consumeEnergie(attackedEnergyPart, attackValue);
                    final int notEatValue = this.addEnergie(part, costAttackValue + eatValue);
                    this.addEnergie(attackedEnergyPart, notEatValue);
                    //if (printed == false) System.out.println();
                    //System.out.printf("\tAttack: %3d\tEat: %3d", attackValue, eatValue);
                    //printed = true;
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
                        //if (printed == false) System.out.println();
                        //System.out.printf("\tPop: %s", hexDir.name());
                        //printed = true;
                    } else {
                        this.consumeEnergie(part, 6);
                    }
                }
            }

            part.age++;

            if (Objects.isNull(targetGridNode.getInPart())) {
                targetGridNode.setInPart(part);
            } else {
                sourceGridNode.setInPart(part);
            }
        }
        //if (printed) System.out.println(); else System.out.print("-");
    }

    private static final int IN_THIS_ENERGIE = 0;
    private static final int IN_THIS_SIZE = 1;

    private static final int IN_FIELD_R[] = { 2, 3, 4, 5, 6, 7 };
    private static final int IN_FIELD_G[] = { 8, 9, 10, 11, 12, 13 };
    private static final int IN_FIELD_B[] = { 14, 15, 16, 17, 18, 19 };
    private static final int IN_PART_R[] = { 20, 21, 22, 23, 24, 25 };

    private static final int IN_PART_G[] = { 26, 27, 28, 29, 30, 31 };
    private static final int IN_PART_B[] = { 32, 33, 34, 35, 36, 37 };
    private static final int IN_PART_ENEGRIE[] = { 38, 39, 40, 41, 42, 43 };

    private static final int IN_THIS_R = 44;
    private static final int IN_THIS_G = 45;
    private static final int IN_THIS_B = 46;

    private static final int IN_THIS_MOVE = 47;
    private static final int IN_THIS_ROTATE = 48;
    private static final int IN_THIS_POPULATE[] = { 49, 50, 51, 52, 53, 54 };
    private static final int IN_THIS_ATTACK = 56;

    private static final int IN_PART_ROTATE[] = { 57, 58, 59, 60, 61, 62 };

    private static final int IN_PART_COM_R[] = { 63, 64, 65, 66, 67, 68 };
    private static final int IN_PART_COM_G[] = { 69, 70, 71, 72, 73, 74 };
    private static final int IN_PART_COM_B[] = { 75, 76, 77, 78, 79, 80 };

    private static final int IN_THIS_COM_R[] = { 81, 82, 83, 84, 85, 86 };
    private static final int IN_THIS_COM_G[] = { 89, 90, 91, 92, 93, 94 };
    private static final int IN_THIS_COM_B[] = { 95, 96, 97, 98, 99, 100 };

    private static final int IN_COUNT = 101;

    private void calcPartNet(final GridNode sourceGridNode, final GeneticPart part) {
        GenNetService.submitInputValue(part.genNet, IN_THIS_ENERGIE, part.energie);
        GenNetService.submitInputValue(part.genNet, IN_THIS_SIZE, part.size);
        GenNetService.submitInputValue(part.genNet, IN_THIS_R, (float) part.getValueFieldArr()[0]);
        GenNetService.submitInputValue(part.genNet, IN_THIS_G, (float) part.getValueFieldArr()[1]);
        GenNetService.submitInputValue(part.genNet, IN_THIS_B, (float) part.getValueFieldArr()[2]);
        GenNetService.submitInputValue(part.genNet, IN_THIS_MOVE, GenNetService.retrieveOutputValue(part.genNet, OUT_MOVE));
        GenNetService.submitInputValue(part.genNet, IN_THIS_ROTATE, GenNetService.retrieveOutputValue(part.genNet, OUT_ROTATE));
        for (final HexDir hexDir : HexDir.values()) {
            GenNetService.submitInputValue(part.genNet, IN_THIS_POPULATE[hexDir.ordinal()], GenNetService.retrieveOutputValue(part.genNet, OUT_POPULATE[hexDir.ordinal()]));

            GenNetService.submitInputValue(part.genNet, IN_THIS_COM_R[hexDir.ordinal()], GenNetService.retrieveOutputValue(part.genNet, OUT_COM_R[0]));
            GenNetService.submitInputValue(part.genNet, IN_THIS_COM_G[hexDir.ordinal()], GenNetService.retrieveOutputValue(part.genNet, OUT_COM_R[1]));
            GenNetService.submitInputValue(part.genNet, IN_THIS_COM_B[hexDir.ordinal()], GenNetService.retrieveOutputValue(part.genNet, OUT_COM_R[2]));
        }
        GenNetService.submitInputValue(part.genNet, IN_THIS_ATTACK, GenNetService.retrieveOutputValue(part.genNet, OUT_ATTACK));

        for (final HexDir hexDir : HexDir.values()) {
            final HexDir inputHexDir = HexDirUtils.calcRotateDir(part.moveDir, hexDir);
            final GridNode targetGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), inputHexDir);
            final Field inputField = targetGridNode.getField(calcOppositeDir(inputHexDir));
            GenNetService.submitInputValue(part.genNet, IN_FIELD_R[hexDir.ordinal()], (float) inputField.outValueArr[0]);
            GenNetService.submitInputValue(part.genNet, IN_FIELD_G[hexDir.ordinal()], (float) inputField.outValueArr[1]);
            GenNetService.submitInputValue(part.genNet, IN_FIELD_B[hexDir.ordinal()], (float) inputField.outValueArr[2]);

            GenNetService.submitInputValue(part.genNet, IN_PART_COM_R[hexDir.ordinal()], (float) inputField.outComArr[0]);
            GenNetService.submitInputValue(part.genNet, IN_PART_COM_G[hexDir.ordinal()], (float) inputField.outComArr[1]);
            GenNetService.submitInputValue(part.genNet, IN_PART_COM_B[hexDir.ordinal()], (float) inputField.outComArr[2]);

            final Part targetOutPart = targetGridNode.getOutPart();
            if (Objects.nonNull(targetOutPart)) {
                double[] visibleValueArr = targetOutPart.getValueFieldArr();
                GenNetService.submitInputValue(part.genNet, IN_PART_R[hexDir.ordinal()], (float) visibleValueArr[0]);
                GenNetService.submitInputValue(part.genNet, IN_PART_G[hexDir.ordinal()], (float) visibleValueArr[1]);
                GenNetService.submitInputValue(part.genNet, IN_PART_B[hexDir.ordinal()], (float) visibleValueArr[2]);

                if (targetOutPart instanceof EnergyPart targetEnergyPart) {
                    GenNetService.submitInputValue(part.genNet, IN_PART_ENEGRIE[hexDir.ordinal()], (float) targetEnergyPart.energie);

                    if (targetOutPart instanceof GeneticPart targetGenPart) {
                        final int dirDiff = HexDirUtils.calcAngelDiff(part.moveDir.ordinal(), targetGenPart.moveDir.ordinal());
                        GenNetService.submitInputValue(part.genNet, IN_PART_ROTATE[hexDir.ordinal()], dirDiff / ((float) HexDirUtils.MAX_ANGEL_DIFF));
                    }
                } else {
                    GenNetService.submitInputValue(part.genNet, IN_PART_ENEGRIE[hexDir.ordinal()], -1.0F);
                    GenNetService.submitInputValue(part.genNet, IN_PART_ROTATE[hexDir.ordinal()], 0.0F);
                }
            } else {
                GenNetService.submitInputValue(part.genNet, IN_PART_R[hexDir.ordinal()], 0.0F);
                GenNetService.submitInputValue(part.genNet, IN_PART_G[hexDir.ordinal()], 0.0F);
                GenNetService.submitInputValue(part.genNet, IN_PART_B[hexDir.ordinal()], 0.0F);
                GenNetService.submitInputValue(part.genNet, IN_PART_ENEGRIE[hexDir.ordinal()], -1.0F);
                GenNetService.submitInputValue(part.genNet, IN_PART_ROTATE[hexDir.ordinal()], 0.0F);
            }
        }

        GenNetService.calc(part.genNet);

        final double[] comFieldArr = part.getComFieldArr();
        comFieldArr[0] = GenNetService.retrieveOutputValue(part.genNet, OUT_COM_R[0]);
        comFieldArr[1] = GenNetService.retrieveOutputValue(part.genNet, OUT_COM_G[1]);
        comFieldArr[2] = GenNetService.retrieveOutputValue(part.genNet, OUT_COM_B[2]);
    }

    private void checkPartConfiguration(final GeneticPart part) {
        final float initBias = 0.0F;
        if (part.genNet.getInputNeuronList().size() < IN_COUNT) {
            for (int neuronPos = part.genNet.getInputNeuronList().size(); neuronPos < IN_COUNT; neuronPos++) {
                final GenNeuron inGenNeuron = new GenNeuron(GenNeuron.NeuronType.Input, initBias);
                GenNetService.submitNewNeuron(part.genNet, neuronPos, inGenNeuron);
                for (int targetNeuronPos = 0; targetNeuronPos < genNetLayerSizeArr[1]; targetNeuronPos++) {
                    final GenNeuron targetGenNeuron = GenNetService.retrieveNeuron(part.genNet, part.genNet.getInputNeuronList().size() + targetNeuronPos);
                    GenNetService.createGenNetSynapse(targetGenNeuron, inGenNeuron, 0.0F);
                }
            }
        }
    }

    private GeneticPart populateGeneticPart(final GeneticPart sourcePart) {
        final GeneticPart newPart = this.mutateGeneticPart(sourcePart, sourcePart.energie / 2, 0.014F, 0.05F);
        newPart.age = sourcePart.age / 10 + 1;
        newPart.energie = sourcePart.energie / 2;
        return newPart;
    }

    private GeneticPart copyGeneticPart(final GeneticPart sourcePart) {
        final double[] sourceVisibleValueArr = sourcePart.getValueFieldArr();
        final double[] visibleValueArr = new double[] {
                sourceVisibleValueArr[0],
                sourceVisibleValueArr[1],
                sourceVisibleValueArr[2]
        };
        final GeneticPart newPart = new GeneticPart(calcNextPartNr(), visibleValueArr);
        newPart.age = sourcePart.age;
        newPart.moveDir = sourcePart.moveDir;
        newPart.size = sourcePart.size;
        newPart.energie = sourcePart.energie;
        newPart.genNet = GenNetTrainService.createCopyNet(sourcePart.genNet);
        return newPart;
    }

    private static final GenNetMutateConfig genNetMutateConfig = new GenNetMutateConfig(true);

    private GeneticPart mutateGeneticPart(final GeneticPart sourcePart, final int newEnergie, final float minMutationRate, final float maxMutationRate) {
        final double[] sourceVisibleValueArr = sourcePart.getValueFieldArr();
        final double[] visibleValueArr = new double[] {
                this.mutateValue(0.0D, 1.0D, 0.01D, sourceVisibleValueArr[0]),
                this.mutateValue(0.0D, 1.0D, 0.01D, sourceVisibleValueArr[1]),
                this.mutateValue(0.0D, 1.0D, 0.01D, sourceVisibleValueArr[2])
        };
        final float mutationRate = GenNetTrainService.calcMutationRate(minMutationRate, maxMutationRate, GenEdenHexGridService.rnd);
        final GeneticPart newPart = new GeneticPart(calcNextPartNr(), visibleValueArr);
        newPart.age = sourcePart.age / 2;
        newPart.moveDir = sourcePart.moveDir;
        newPart.size = this.mutateValue(MIN_SIZE, MAX_SIZE, 1, sourcePart.size);
        newPart.energie = newEnergie;
        newPart.genNet = GenNetTrainService.createMutatedNet(sourcePart.genNet, mutationRate, genNetMutateConfig, GenEdenHexGridService.rnd);
        return newPart;
    }

    private int mutateValue(final int minValue, final int maxValue, final int mutateValue, final int value) {
        final int newValue = value + GenEdenHexGridService.rnd.nextInt(mutateValue * 2 + 1) - mutateValue;
        return Math.min(maxValue, Math.max(minValue, newValue));
    }

    private double mutateValue(final double minValue, final double maxValue, final double mutateValue, final double value) {
        final double newValue = value + GenEdenHexGridService.rnd.nextDouble(mutateValue * 2) - mutateValue;
        return Math.min(maxValue, Math.max(minValue, newValue));
    }

    int addEnergie(final EnergyPart part, final int value) {
        final int usedValue = Math.min(part.size - part.energie, value);
        part.energie += usedValue;
        return value - usedValue;
    }

    int consumeEnergie(final EnergyPart part, final int value) {
        final int usedValue = Math.min(part.energie, value);
        part.energie -= usedValue;
        return usedValue;
    }

    private static final int OUT_MOVE = 0;
    private static final int OUT_ROTATE = 1;
    private static final int OUT_POPULATE[] = { 2, 3, 4, 5, 6, 7 };
    private static final int OUT_ATTACK = 8;

    private static final int OUT_COM_R[] = { 9, 10, 11, 12, 13, 14 };
    private static final int OUT_COM_G[] = { 15, 16, 17, 18, 19, 20 };
    private static final int OUT_COM_B[] = { 21, 22, 23, 24, 25, 26 };

    private static final int OUT_COUNT = 27;

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

    private static final int ADULT_AGE = 100;

    private boolean calcPopulate(final GeneticPart part, final HexDir hexDir) {
        final boolean ret;
        if (part.energie > (part.size - (part.size / 2))) {
            if (part.age > ADULT_AGE) {
                final float outputValue = GenNetService.retrieveOutputValue(part.genNet, OUT_POPULATE[hexDir.ordinal()]);
                ret = outputValue > 0.5F;
            } else {
                ret = false;
            }
        } else {
            ret = false;
        }
        return ret;
    }

    private int calcAttack(final GeneticPart part) {
        final float outputValue = GenNetService.retrieveOutputValue(part.genNet, OUT_ATTACK);
        return (int) (outputValue * part.size);
    }

    @Override
    public int retrieveGenerationCount() {
        return this.generationCount;
    }

    @Override
    public void submitGenerationCount(final int generationCount) {
        this.generationCount = generationCount;
    }

    @Override
    public void calcBeginNext() {
        for (int pos = 0; pos < 3; pos++) {
            final GridNode outGridNode = this.hexGridService.retrieveGridNode(
                    GenEdenHexGridService.rnd.nextInt(this.hexGridService.getXGridSize()),
                    this.hexGridService.getYGridSize() - GenEdenHexGridService.rnd.nextInt(this.hexGridService.getYGridSize() / 3));
            final Part targetPart = outGridNode.getOutPart();
            if (Objects.isNull(targetPart)) {
                final EnergyPart newEnergyPart = new EnergyPart(calcNextPartNr(), new double[] { 0.0D, 0.5D, 0.5D });
                newEnergyPart.setEnergie(32*2);
                outGridNode.setOutPart(newEnergyPart);
            }
        }
    }

    @Override
    public void calcPartInput(final GridNode sourceGridNode, final Part part) {
        if (part instanceof GeneticPart geneticPart) {
            this.calcPartNetInput(sourceGridNode, geneticPart);
        }
    }

}
