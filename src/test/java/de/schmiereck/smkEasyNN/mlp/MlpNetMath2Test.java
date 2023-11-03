package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpochWithTrainSize;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpWeightTrainerService.calcAdditionalNeuronSize;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MlpNetMath2Test {
    private record Result(float[][] trainInputArrArr, float[][] expectedOutputArrArr) {
    }

    @Test
    @Disabled
    void GIVEN_2_value_inputs_with_trainer_for_every_layer_THEN_add_output_test_1() {
        final boolean useWeightDiff = false;

        final int trainTheTrainerEpochMax = 200;
        //final int trainTheTrainerMaxTrainPos = 1200;

        final int trainTheTrainerMaxTrainPos = 120;
        //final int trainTheTrainerMaxTrainPos = 20;

        final int trainTheTrainerDataSize = 100;

        //final int trainTheNetEpochMax = 27_000;
        final int trainTheNetEpochMax = 1_000;
        //final int trainTheNetEpochMax = 100;

        final MlpWeightTrainer.TrainLayerSizeEnum trainLayerSizeEnum = MlpWeightTrainer.TrainLayerSizeEnum.Small;

        GIVEN_2_value_inputs_with_trainer_for_every_layer_THEN_add_output(trainLayerSizeEnum, useWeightDiff,
                trainTheTrainerMaxTrainPos, trainTheTrainerEpochMax, trainTheTrainerDataSize,
                trainTheNetEpochMax, 4,
                0.01F, 0.6F);
    }

    @Test
    @Disabled
    void GIVEN_2_value_inputs_with_trainer_for_every_layer_THEN_add_output_test_2() {
        final boolean useWeightDiff = false;

        final int trainTheTrainerEpochMax = 200;
        //final int trainTheTrainerEpochMax = 2000;

        // How many Epoches used for every Net-Slots to train.
        //final int trainTheTrainerMaxTrainPos = 2200;
        final int trainTheTrainerMaxTrainPos = 800;
        //final int trainTheTrainerMaxTrainPos = 200;
        //final int trainTheTrainerMaxTrainPos = 120;
        //final int trainTheTrainerMaxTrainPos = 20;
        //final int trainTheTrainerMaxTrainPos = 20;

        // How many Net-Slots trained in parallel.
        //final int trainTheTrainerDataSize = 2000;
        final int trainTheTrainerDataSize = 600;
        //final int trainTheTrainerDataSize = 200;
        //final int trainTheTrainerDataSize = 100;
        //final int trainTheTrainerDataSize = 40;
        //final int trainTheTrainerDataSize = 20;

        //final int trainTheNetEpochMax = 27_000;
        final int trainTheNetEpochMax = 5_000;
        //final int trainTheNetEpochMax = 1_000;
        //final int trainTheNetEpochMax = 100;

        //final MlpWeightTrainer.TrainLayerSizeEnum trainLayerSizeEnum = MlpWeightTrainer.TrainLayerSizeEnum.Small;
        //final MlpWeightTrainer.TrainLayerSizeEnum trainLayerSizeEnum = MlpWeightTrainer.TrainLayerSizeEnum.Deeper0Small;
        //final MlpWeightTrainer.TrainLayerSizeEnum trainLayerSizeEnum = MlpWeightTrainer.TrainLayerSizeEnum.Deeper1Small;
        final MlpWeightTrainer.TrainLayerSizeEnum trainLayerSizeEnum = MlpWeightTrainer.TrainLayerSizeEnum.Deeper2;
        //final MlpWeightTrainer.TrainLayerSizeEnum trainLayerSizeEnum = MlpWeightTrainer.TrainLayerSizeEnum.Normal;
        //final MlpWeightTrainer.TrainLayerSizeEnum trainLayerSizeEnum = MlpWeightTrainer.TrainLayerSizeEnum.Big;

        GIVEN_2_value_inputs_with_trainer_for_every_layer_THEN_add_output(trainLayerSizeEnum, useWeightDiff,
                trainTheTrainerMaxTrainPos, trainTheTrainerEpochMax, trainTheTrainerDataSize,
                trainTheNetEpochMax, 4,
                0.01F, 0.6F);
    }

    @Test
    @Disabled
    void GIVEN_2_value_inputs_with_trainer_for_every_layer_THEN_add_output_test_3() {
        final boolean useWeightDiff = false;

        final int trainTheTrainerEpochMax = 200;
        //final int trainTheTrainerEpochMax = 2000;

        // How many Epoches used for every Net-Slots to train.
        //final int trainTheTrainerMaxTrainPos = 2000;
        //final int trainTheTrainerMaxTrainPos = 1000;
        final int trainTheTrainerMaxTrainPos = 800;
        //final int trainTheTrainerMaxTrainPos = 500;
        //final int trainTheTrainerMaxTrainPos = 200;
        //final int trainTheTrainerMaxTrainPos = 100;
        //final int trainTheTrainerMaxTrainPos = 20;

        // How many Net-Slots trained in parallel.
        //final int trainTheTrainerDataSize = 2000;
        final int trainTheTrainerDataSize = 600;
        //final int trainTheTrainerDataSize = 400;
        //final int trainTheTrainerDataSize = 200;
        //final int trainTheTrainerDataSize = 100;
        //final int trainTheTrainerDataSize = 40;
        //final int trainTheTrainerDataSize = 20;

        //final int trainTheNetEpochMax = 27_000;
        final int trainTheNetEpochMax = 15_000;
        //final int trainTheNetEpochMax = 5_000;
        //final int trainTheNetEpochMax = 1_000;
        //final int trainTheNetEpochMax = 100;

        //final MlpWeightTrainer.TrainLayerSizeEnum trainLayerSizeEnum = MlpWeightTrainer.TrainLayerSizeEnum.Mini;
        //final MlpWeightTrainer.TrainLayerSizeEnum trainLayerSizeEnum = MlpWeightTrainer.TrainLayerSizeEnum.Small;
        //final MlpWeightTrainer.TrainLayerSizeEnum trainLayerSizeEnum = MlpWeightTrainer.TrainLayerSizeEnum.Deeper0Small;
        //final MlpWeightTrainer.TrainLayerSizeEnum trainLayerSizeEnum = MlpWeightTrainer.TrainLayerSizeEnum.Deeper1Small;
        final MlpWeightTrainer.TrainLayerSizeEnum trainLayerSizeEnum = MlpWeightTrainer.TrainLayerSizeEnum.Deeper2;
        //final MlpWeightTrainer.TrainLayerSizeEnum trainLayerSizeEnum = MlpWeightTrainer.TrainLayerSizeEnum.Normal;
        //final MlpWeightTrainer.TrainLayerSizeEnum trainLayerSizeEnum = MlpWeightTrainer.TrainLayerSizeEnum.Big;
        //final MlpWeightTrainer.TrainLayerSizeEnum trainLayerSizeEnum = MlpWeightTrainer.TrainLayerSizeEnum.Bigger;

        GIVEN_2_value_inputs_with_trainer_for_every_layer_THEN_add_output(trainLayerSizeEnum, useWeightDiff,
                trainTheTrainerMaxTrainPos, trainTheTrainerEpochMax, trainTheTrainerDataSize,
                trainTheNetEpochMax, 4,
                //0.01F, 0.6F);
                0.05F, 0.6F);
    }

    /**
     * Train one Trainer-Net with all Neurons in the output Layers.
     */
    private void GIVEN_2_value_inputs_with_trainer_for_every_layer_THEN_add_output(MlpWeightTrainer.TrainLayerSizeEnum trainLayerSizeEnum,
                                                                                   boolean useWeightDiff,
                                                                                   int trainTheTrainerMaxTrainPos,
                                                                                   int trainTheTrainerEpochMax,
                                                                                   final int trainTheTrainerDataSize,
                                                                                   int trainTheNetEpochMax,
                                                                                   final int trainerTrainSize,
                                                                                   final float trainerLearningRate, final float trainerMomentum) {
        // Arrange
        final MlpNetMath2Test.Result result = arrangeAddResult4();
        //final Result result = arrangeAddResult();
        //final Result result = arrangeMultResult();

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        // Use Netzwork-Layouts with a fixed Input-Synapse size (only connect to the nearest input neurons). Like Array-Layers.
        //final int[] layerSizeArr = new int[]{ 2, 6, 4, 1 };
        final MlpConfiguration config = new MlpConfiguration(true, false);
        final MlpLayerConfig[] layerConfigArr = new MlpLayerConfig[4];
        layerConfigArr[0] = new MlpLayerConfig(4);
        layerConfigArr[1] = new MlpLayerConfig(6);
        layerConfigArr[2] = new MlpLayerConfig(4);
        layerConfigArr[3] = new MlpLayerConfig(1);

        layerConfigArr[0].setIsLimited(4, true);
        layerConfigArr[1].setIsLimited(4, true);
        layerConfigArr[2].setIsLimited(4, true);
        layerConfigArr[3].setIsLimited(4, true);

        final int additionalNeuronSize = calcAdditionalNeuronSize(config);
        final MlpWeightTrainer[] weightTrainerArr = new MlpWeightTrainer[layerConfigArr.length];
        for (int trainerPos = 0; trainerPos < layerConfigArr.length; trainerPos++) {
            weightTrainerArr[trainerPos] = new MlpWeightTrainer(trainerTrainSize, additionalNeuronSize, rnd, trainLayerSizeEnum);
            weightTrainerArr[trainerPos].useWeightDiff = useWeightDiff;
        }

        // Train the Trainer and Net:
        {
            // Train all "Net"-Slots in random Order:

            final TrainData[] trainDataArr = new TrainData[trainTheTrainerDataSize];
            for (int trainDataPos = 0; trainDataPos < trainDataArr.length; trainDataPos++) {
                final MlpNet net = MlpNetService.createNet(config, layerConfigArr, rnd);
                trainDataArr[trainDataPos] = new TrainData(net);
            }

            final int successfulCounterMax = 10;

            for (int trainPos = 0; trainPos < (trainTheTrainerMaxTrainPos * trainTheTrainerDataSize); trainPos++) {
                final int trainDataPos = rnd.nextInt((trainPos % (trainDataArr.length)) + 1);
                final TrainData trainData = trainDataArr[trainDataPos];

                trainTheTrainerArr(trainData, rnd, result, weightTrainerArr,
                        0.1F, 0.6F,
                trainerLearningRate, trainerMomentum);

                if ((trainPos) % 100 == 0) {
                    printTrainerMse(trainPos, weightTrainerArr);
                }

                if (trainData.epochPos > trainTheTrainerEpochMax) {
                    // Not Successfully trained: Remove net.
                    final MlpNet net = MlpNetService.createNet(config, layerConfigArr, rnd);
                    trainDataArr[trainDataPos] = new TrainData(net);
                    System.out.printf("trainPos: %d, trainDataPos: %d - Not Successfully trained\n", trainPos, trainDataPos);
                } else {
                    if (trainData.successfulCounter > successfulCounterMax) {
                        //printFullResultForEpochWithTrainSize(trainData.net, result.trainInputArrArr, result.expectedOutputArrArr, trainData.epochPos, trainData.mainOutputMseErrorValue);
                        // Successfully trained: Remove net.
                        final MlpNet net = MlpNetService.createNet(config, layerConfigArr, rnd);
                        trainDataArr[trainDataPos] = new TrainData(net);
                        System.out.printf("trainPos: %d, trainDataPos: %d - Successfully trained\n", trainPos, trainDataPos);
                    } else {
                        if ((trainData.epochPos + 1) % 100 == 0) {
                            //printFullResultForEpoch(trainData.net, result.trainInputArrArr, result.expectedOutputArrArr, trainData.epochPos, trainData.mainOutputMseErrorValue);
                        }
                    }
                }
            }
        }

        // Train the Net with Trainer:
        System.out.println("---- Train the Net with Trainer: --------------------------------------------------------");
        {
            final MlpNet net = MlpNetService.createNet(config, layerConfigArr, rnd);

            final int successfulCounterMax = 60;
            trainTheNetWithTrainerArr(net, rnd, trainTheNetEpochMax, result, weightTrainerArr, successfulCounterMax);

            // Act & Assert
            System.out.println("Act & Assert");
            actAssertExpectedOutput(net, result.trainInputArrArr, result.expectedOutputArrArr, 0.075F);
        }
    }

    /**
     * Train one Trainer-Net with all Neurons in the output Layers.
     */
    @Test
    @Disabled
    void GIVEN_2_value_inputs_with_trainer_for_output_layer_THEN_add_output() {
        // Arrange
        final MlpNetMath2Test.Result result = arrangeAddResult();
        //final Result result = arrangeMultResult();

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final int[] layerSizeArr = new int[]{ 2, 6, 4, 1 };
        final MlpConfiguration config = new MlpConfiguration(true, false);

        final int additionalNeuronSize = calcAdditionalNeuronSize(config);
        final MlpWeightTrainer weightTrainer = new MlpWeightTrainer(6, additionalNeuronSize, rnd, MlpWeightTrainer.TrainLayerSizeEnum.Small);
        final int trainLayerPos = layerSizeArr.length - 1;

        //weightTrainer.useWeightDiff = true;

        // Train the Trainer and Net:
        {
            final int epochMax = 200;
            //for (int trainPos = 0; trainPos < 1200; trainPos++) {
            for (int trainPos = 0; trainPos < 120; trainPos++) {
                trainTheTrainer(config, layerSizeArr, rnd, result, weightTrainer, trainLayerPos, epochMax);
                System.out.printf("trainPos: %d\n", trainPos);
            }
        }

        // Train the Net with Trainer:
        {
            final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);

            final int successfulCounterMax = 60;
            final int epochMax = 27_000;
            trainTheNetWithTrainer(net, config, layerSizeArr, rnd, epochMax, result, weightTrainer, trainLayerPos, successfulCounterMax);

            // Act & Assert
            System.out.println("Act & Assert");
            actAssertExpectedOutput(net, result.trainInputArrArr, result.expectedOutputArrArr, 0.075F);
        }
    }

    /**
     * Train one Trainer-Net with all Neurons in all Layers.
     */
    @Test
    @Disabled
    void GIVEN_2_value_inputs_with_trainer_THEN_add_output() {
        // Arrange
        final MlpNetMath2Test.Result result = arrangeAddResult();
        //final Result result = arrangeMultResult();

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final int[] layerSizeArr = new int[]{ 2, 6, 4, 1 };
        final MlpConfiguration config = new MlpConfiguration(true, false);

        final int additionalNeuronSize = calcAdditionalNeuronSize(config);
        final MlpWeightTrainer weightTrainer = new MlpWeightTrainer(6, additionalNeuronSize, rnd, MlpWeightTrainer.TrainLayerSizeEnum.Small);
        final int trainLayerPos = -1;

        //weightTrainer.useWeightDiff = true;

        // Train the Trainer and Net:
        {
            final int epochMax = 200;
            for (int trainPos = 0; trainPos < 1200; trainPos++) {
                //for (int trainPos = 0; trainPos < 20; trainPos++) {
                trainTheTrainer(config, layerSizeArr, rnd, result, weightTrainer, trainLayerPos, epochMax);
                System.out.printf("trainPos: %d\n", trainPos);
            }
        }

        // Train the Net with Trainer:
        {
            final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);

            final int successfulCounterMax = 60;
            final int epochMax = 27_000;
            trainTheNetWithTrainer(net, config, layerSizeArr, rnd, epochMax, result, weightTrainer, trainLayerPos, successfulCounterMax);

            // Act & Assert
            System.out.println("Act & Assert");
            actAssertExpectedOutput(net, result.trainInputArrArr, result.expectedOutputArrArr, 0.075F);
        }
    }

    @Test
    void GIVEN_2_value_inputs_THEN_add_output() {
        // Arrange
        final MlpNetMath2Test.Result result = arrangeAddResult();

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final int[] layerSizeArr = new int[]{ 2, 6, 4, 1 };
        final MlpConfiguration config = new MlpConfiguration(true, false);
        final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);

        final int successfulCounterMax = 60;
        int successfulCounter = 0;
        final int epochMax = 7_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = MlpService.runTrainRandom(net,
                    result.expectedOutputArrArr, result.trainInputArrArr,
                    0.1F, 0.6F, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printFullResultForEpoch(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
            }
            if (mainOutputMseErrorValue < 0.0001F) {
                successfulCounter++;
                if (successfulCounter > successfulCounterMax) {
                    printFullResultForEpochWithTrainSize(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
                    break;
                }
            } else {
                successfulCounter = 0;
            }
        }

        // Act & Assert
        System.out.println("Act & Assert");
        actAssertExpectedOutput(net, result.trainInputArrArr, result.expectedOutputArrArr, 0.01F);
    }

    @Test
    void GIVEN_2_value_inputs_THEN_sub_output() {
        // Arrange
        final MlpNetMath2Test.Result result = arrangeSubResult();
        final int[] layerSizeArr = new int[]{ 2, 6, 4, 1 };

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final MlpConfiguration config = new MlpConfiguration(true, false);
        final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);

        final int successfulCounterMax = 60;
        int successfulCounter = 0;
        final int epochMax = 7_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = MlpService.runTrainRandom(net, result.expectedOutputArrArr, result.trainInputArrArr,
                    0.1F, 0.6F, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printFullResultForEpoch(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
            }
            if (mainOutputMseErrorValue < 0.0001F) {
                successfulCounter++;
                if (successfulCounter > successfulCounterMax) {
                    printFullResultForEpochWithTrainSize(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
                    break;
                }
            } else {
                successfulCounter = 0;
            }
        }

        // Act & Assert
        System.out.println("Act & Assert");
        actAssertExpectedOutput(net, result.trainInputArrArr, result.expectedOutputArrArr, 0.01F);
    }

    @Test
    void GIVEN_2_value_inputs_THEN_mult_output() {
        // Arrange
        final MlpNetMath2Test.Result result = arrangeMultResult();
        final int[] layerSizeArr = new int[]{ 2, 4, 8, 16, 4, 1 };

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final MlpConfiguration config = new MlpConfiguration(true, false);
        final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);

        final int successfulCounterMax = 80;
        int successfulCounter = 0;
        final int epochMax = 247_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = MlpService.runTrainRandom(net, result.expectedOutputArrArr, result.trainInputArrArr,
                    0.01F, 0.8F, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printFullResultForEpoch(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
            }
            if (mainOutputMseErrorValue < 0.0001F) {
                successfulCounter++;
                if (successfulCounter > successfulCounterMax) {
                    printFullResultForEpochWithTrainSize(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
                    break;
                }
            } else {
                successfulCounter = 0;
            }
        }

        // Act & Assert
        System.out.println("Act & Assert");
        actAssertExpectedOutput(net, result.trainInputArrArr, result.expectedOutputArrArr, 0.02F);
    }

    private class TrainData {
        MlpNet net;
        int epochPos;
        int successfulCounter;
        float mainOutputMseErrorValue;

        public TrainData(final MlpNet net) {
            this.net = net;
            this.epochPos = 0;
            this.successfulCounter = 0;
        }
    }

    private void trainTheTrainerArr(final TrainData trainData, final Random rnd, final MlpNetMath2Test.Result result,
                                    final MlpWeightTrainer[] weightTrainerArr,
                                    final float learningRate, final float momentum,
                                    final float trainerLearningRate, final float trainerMomentum) {
        trainData.mainOutputMseErrorValue =
                MlpWeightTrainerService.runTrainRandomNetAndTrainerArr(trainData.net,
                                                                       result.expectedOutputArrArr, result.trainInputArrArr,
                                                                       learningRate, momentum, rnd,
                                                                       weightTrainerArr,
                        trainerLearningRate, trainerMomentum);

        //if (trainData.mainOutputMseErrorValue < 0.0001F) {
        if (trainData.mainOutputMseErrorValue < 0.05F) {
            trainData.successfulCounter++;
        } else {
            trainData.successfulCounter = 0;
        }
        trainData.epochPos++;
    }

    private void printTrainerMse(final int trainPos, final MlpWeightTrainer[] weightTrainerArr) {
        final String trainerMseStr = Arrays.stream(weightTrainerArr).
                map(weightTrainer -> String.format("%.8f", weightTrainer.trainerMse)).
                collect(Collectors.joining(", ", "[", "]"));
        System.out.printf("trainPos: %d, trainerMse: %s\n", trainPos, trainerMseStr);
    }

    private void printTrainerMse(final MlpWeightTrainer[] weightTrainerArr) {
        final String trainerMseStr = Arrays.stream(weightTrainerArr).
                map(weightTrainer -> String.format("%.8f", weightTrainer.trainerMse)).
                collect(Collectors.joining(", ", "[", "]"));
        System.out.printf("trainerMse: %s\n", trainerMseStr);
    }

    private void trainTheTrainer(final MlpConfiguration config, final int[] layerSizeArr, final Random rnd, final MlpNetMath2Test.Result result,
                                 final MlpWeightTrainer weightTrainer, final int trainLayerPos, final int epochMax) {
        final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);

        final int successfulCounterMax = 10;
        int successfulCounter = 0;
        //final int epochMax = 27_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = MlpWeightTrainerService.runTrainRandomNetAndTrainer(net,
                    result.expectedOutputArrArr, result.trainInputArrArr,
                    0.1F, 0.6F, rnd,
                    weightTrainer, trainLayerPos);

            if ((epochPos + 1) % 100 == 0) {
                printFullResultForEpoch(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
                System.out.printf("trainerMse: %.8f\n", weightTrainer.trainerMse);
            }
            if (mainOutputMseErrorValue < 0.0001F) {
                successfulCounter++;
                if (successfulCounter > successfulCounterMax) {
                    printFullResultForEpochWithTrainSize(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
                    System.out.printf("trainerMse: %.8f\n", weightTrainer.trainerMse);
                    break;
                }
            } else {
                successfulCounter = 0;
            }
        }

        // Act & Assert
        System.out.println("Act & Assert");
        //actAssertExpectedOutput(net, result.trainInputArrArr, result.expectedOutputArrArr, 0.075F);
    }

    private void trainTheNetWithTrainerArr(final MlpNet net,
                                           Random rnd, int epochMax, MlpNetMath2Test.Result result, MlpWeightTrainer[] weightTrainerArr, int successfulCounterMax) {
        int successfulCounter = 0;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = MlpWeightTrainerService.runTrainRandomWithTrainerArr(net,
                    result.expectedOutputArrArr, result.trainInputArrArr,
                    0.1F, 0.6F, rnd,
                    weightTrainerArr);

            if ((epochPos) % 100 == 0) {
                printFullResultForEpoch(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
            }
            if (mainOutputMseErrorValue < 0.0001F) {
                successfulCounter++;
                if (successfulCounter > successfulCounterMax) {
                    printFullResultForEpochWithTrainSize(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
                    break;
                }
            } else {
                successfulCounter = 0;
            }
        }
    }

    private void trainTheNetWithTrainer(final MlpNet net, MlpConfiguration config, int[] layerSizeArr, Random rnd, int epochMax, MlpNetMath2Test.Result result, MlpWeightTrainer weightTrainer, int trainLayerPos, int successfulCounterMax) {
        int successfulCounter = 0;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = MlpWeightTrainerService.runTrainRandomWithTrainer(net,
                    result.expectedOutputArrArr, result.trainInputArrArr,
                    0.1F, 0.6F, rnd,
                    weightTrainer, trainLayerPos);

            if ((epochPos + 1) % 100 == 0) {
                printFullResultForEpoch(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
            }
            if (mainOutputMseErrorValue < 0.0001F) {
                successfulCounter++;
                if (successfulCounter > successfulCounterMax) {
                    printFullResultForEpochWithTrainSize(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
                    break;
                }
            } else {
                successfulCounter = 0;
            }
        }
    }

    private static MlpNetMath2Test.Result arrangeAddResult() {
        final float[][] trainInputArrArr = new float[][]
                {
                        new float[]{0, 0},

                        new float[]{0, 0},
                        new float[]{0, 1},
                        new float[]{0, 2},
                        new float[]{0, 3},

                        new float[]{1, 0},
                        new float[]{1, 1},
                        new float[]{1, 2},
                        new float[]{1, 3},

                        new float[]{2, 0},
                        new float[]{2, 1},
                        new float[]{2, 2},
                        new float[]{2, 3},

                        new float[]{3, 0},
                        new float[]{3, 1},
                        new float[]{3, 2},
                        new float[]{3, 3},
                };
        final float[][] expectedOutputArrArr = new float[][]
                {
                        new float[]{0},

                        new float[]{0},
                        new float[]{1},
                        new float[]{2},
                        new float[]{3},

                        new float[]{1},
                        new float[]{2},
                        new float[]{3},
                        new float[]{4},

                        new float[]{2},
                        new float[]{3},
                        new float[]{4},
                        new float[]{5},

                        new float[]{3},
                        new float[]{4},
                        new float[]{5},
                        new float[]{6},
                };
        return new MlpNetMath2Test.Result(trainInputArrArr, expectedOutputArrArr);
    }

    private static MlpNetMath2Test.Result arrangeAddResult4() {
        final float[][] trainInputArrArr = new float[][]
                {
                        new float[]{ 0, 0, 0, 0},

                        new float[]{ 0, 0, 0, 0},
                        new float[]{ 0, 0, 0, 1},
                        new float[]{ 0, 0, 0, 2},
                        new float[]{ 0, 0, 0, 3},

                        new float[]{ 0, 0, 1, 0},
                        new float[]{ 0, 0, 1, 1},
                        new float[]{ 0, 0, 1, 2},
                        new float[]{ 0, 0, 1, 3},

                        new float[]{ 0, 0, 2, 0},
                        new float[]{ 0, 0, 2, 1},
                        new float[]{ 0, 0, 2, 2},
                        new float[]{ 0, 0, 2, 3},

                        new float[]{ 0, 0, 3, 0},
                        new float[]{ 0, 0, 3, 1},
                        new float[]{ 0, 0, 3, 2},
                        new float[]{ 0, 0, 3, 3},
                };
        final float[][] expectedOutputArrArr = new float[][]
                {
                        new float[]{0},

                        new float[]{0},
                        new float[]{1},
                        new float[]{2},
                        new float[]{3},

                        new float[]{1},
                        new float[]{2},
                        new float[]{3},
                        new float[]{4},

                        new float[]{2},
                        new float[]{3},
                        new float[]{4},
                        new float[]{5},

                        new float[]{3},
                        new float[]{4},
                        new float[]{5},
                        new float[]{6},
                };
        return new MlpNetMath2Test.Result(trainInputArrArr, expectedOutputArrArr);
    }

    private static MlpNetMath2Test.Result arrangeSubResult() {
        final float[][] trainInputArrArr = new float[][]
                {
                        new float[]{0, 0},

                        new float[]{0, 0},
                        new float[]{0, 1},
                        new float[]{0, 2},
                        new float[]{0, 3},

                        new float[]{1, 0},
                        new float[]{1, 1},
                        new float[]{1, 2},
                        new float[]{1, 3},

                        new float[]{2, 0},
                        new float[]{2, 1},
                        new float[]{2, 2},
                        new float[]{2, 3},

                        new float[]{3, 0},
                        new float[]{3, 1},
                        new float[]{3, 2},
                        new float[]{3, 3},
                };
        final float[][] expectedOutputArrArr = new float[][]
                {
                        new float[]{0},

                        new float[]{0},
                        new float[]{-1},
                        new float[]{-2},
                        new float[]{-3},

                        new float[]{1},
                        new float[]{0},
                        new float[]{-1},
                        new float[]{-2},

                        new float[]{2},
                        new float[]{1},
                        new float[]{0},
                        new float[]{-1},

                        new float[]{3},
                        new float[]{2},
                        new float[]{1},
                        new float[]{0},
                };
        return new MlpNetMath2Test.Result(trainInputArrArr, expectedOutputArrArr);
    }

    private static MlpNetMath2Test.Result arrangeMultResult() {
        final float[][] trainInputArrArr = new float[][]
                {
                        new float[]{0, 0},

                        new float[]{0, 0},
                        new float[]{0, 1},
                        new float[]{0, 2},
                        new float[]{0, 3},

                        new float[]{1, 0},
                        new float[]{1, 1},
                        new float[]{1, 2},
                        new float[]{1, 3},

                        new float[]{2, 0},
                        new float[]{2, 1},
                        new float[]{2, 2},
                        new float[]{2, 3},

                        new float[]{3, 0},
                        new float[]{3, 1},
                        new float[]{3, 2},
                        new float[]{3, 3},
                };
        final float[][] expectedOutputArrArr = new float[][]
                {
                        new float[]{0},

                        new float[]{0},
                        new float[]{0},
                        new float[]{0},
                        new float[]{0},

                        new float[]{0},
                        new float[]{1},
                        new float[]{2},
                        new float[]{3},

                        new float[]{0},
                        new float[]{2},
                        new float[]{4},
                        new float[]{6},

                        new float[]{0},
                        new float[]{3},
                        new float[]{6},
                        new float[]{9},
                };
        return new MlpNetMath2Test.Result(trainInputArrArr, expectedOutputArrArr);
    }
}
