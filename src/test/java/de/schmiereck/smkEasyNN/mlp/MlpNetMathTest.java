package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpochWithTrainSize;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandom;
import static de.schmiereck.smkEasyNN.mlp.MlpService.train;

import java.util.Objects;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class MlpNetMathTest {
    private record Result(float[][] trainInputArrArr, float[][] expectedOutputArrArr) {
    }

    @Test
    void GIVEN_2_value_inputs_THEN_add_output() {
        // Arrange
        final Result result = arrangeAddResult();

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final int[] layerSizeArr = new int[]{ 2, 6, 4, 1 };
        final MlpConfiguration config = new MlpConfiguration(true, false);
        final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);

        final MlpWeightTrainer trainer = new MlpWeightTrainer(6, rnd);
        final int successfulCounterMax = 60;
        int successfulCounter = 0;
        final int epochMax = 7_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = MlpWeightTrainerService.runTrainRandom(net, result.expectedOutputArrArr, result.trainInputArrArr,
                    0.1F, 0.6F, rnd,
                    trainer);

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
        final Result result = arrangeSubResult();
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
        final Result result = arrangeMultResult();
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

    private static Result arrangeAddResult() {
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
        return new Result(trainInputArrArr, expectedOutputArrArr);
    }

    private static Result arrangeSubResult() {
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
        return new Result(trainInputArrArr, expectedOutputArrArr);
    }

    private static Result arrangeMultResult() {
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
        return new Result(trainInputArrArr, expectedOutputArrArr);
    }
}
