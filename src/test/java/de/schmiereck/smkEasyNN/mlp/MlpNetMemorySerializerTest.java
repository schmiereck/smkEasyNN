package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpLayer.calcInitWeight;
import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.addShortTermMemoryInputs;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.addForwwardInputs;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.runTrainWithGrowingTrainSize;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandomOrder;

import java.util.Random;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MlpNetMemorySerializerTest {

    @Test
    void GIVEN_digital_input_THEN_output_serialized_digit_1_memory_1() {
        // Arrange
        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0,  0, 0, 0 }, // 000
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 1 }, // 001
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  0, 0, 0 }, // 010
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 1 }, // 011
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  0, 0, 0 }, // 100
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 1 }, // 101
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  0, 0, 0 }, // 110
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 1 }, // 111
                                new float[]{ 1,  0, 0, 0 },
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0 }, // 000
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 0 }, // 001
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 0 }, // 010
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 0 }, // 011
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 0 }, // 100
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 0 }, // 101
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 0 }, // 110
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 0 }, // 111
                                new float[]{ 1 },
                        },
                };
        final int[] layerSizeArr = new int[]{ 1+3,  20, 20, 10, 10, 10,  1 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpConfiguration config = new MlpConfiguration(true, true,
                (inputSize, outputSize, rnd2) -> calcInitWeight(4.0F, rnd),
                //(inputSize, outputSize, rnd2) -> calcInitWeightXavier(inputSize, rnd2),
                //(inputSize, outputSize, rnd) -> calcInitWeightNormalizedXavier(inputSize, outputSize, rnd),
                //(inputSize, outputSize, rnd) -> calcInitWeight3(initialBiasWeightValue, rnd));
                (inputSize, outputSize, rnd2) -> 0.0F);
        final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);

        addForwwardInputs(net, 4, 2, true, false, true, false, true, rnd);
        //addAdditionalBiasInputToLayer(net, 2, true, rnd);
        addShortTermMemoryInputs(net, 2, 5, 9, false, true, true, rnd);

        final int epochMax = 25_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = runTrainRandomOrder(net, expectedOutputArrArrArr, trainInputArrArrArr, 0.3F, 0.6F, rnd);

            if ((epochPos + 1) % 100 == 0) {
                MlpNetPrintUtils.printFullResultForEpoch(net, trainInputArrArrArr, expectedOutputArrArrArr, epochPos, mainOutputMseErrorValue);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(net, trainInputArrArrArr, expectedOutputArrArrArr, 0.05F);
    }

    @Test
    void GIVEN_digital_input_THEN_output_serialized_digit_1_memory_2() {
        // Arrange
        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0,  0, 0, 0 }, // 000
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 1 }, // 001
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  0, 0, 0 }, // 010
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 1 }, // 011
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  0, 0, 0 }, // 100
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 1 }, // 101
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  0, 0, 0 }, // 110
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 1 }, // 111
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0 }, // 000
                                new float[]{ 0 },
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 1 }, // 001
                                new float[]{ 1 },
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 0 }, // 010
                                new float[]{ 0 },
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 1 }, // 011
                                new float[]{ 1 },
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 0 }, // 100
                                new float[]{ 0 },
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 1 }, // 101
                                new float[]{ 1 },
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 0 }, // 110
                                new float[]{ 0 },
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 1 }, // 111
                                new float[]{ 1 },
                                new float[]{ 1 },
                        },
                };
        final int[] layerSizeArr = new int[]{ 1+3,  20, 20, 10, 10, 10, 20, 20,  1 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpConfiguration config = new MlpConfiguration(true, false,
                (inputSize, outputSize, rnd2) -> calcInitWeight(3.0F, rnd),
                //(inputSize, outputSize, rnd2) -> calcInitWeightXavier(inputSize, rnd2),
                //(inputSize, outputSize, rnd) -> calcInitWeightNormalizedXavier(inputSize, outputSize, rnd),
                //(inputSize, outputSize, rnd) -> calcInitWeight3(initialBiasWeightValue, rnd));
                (inputSize, outputSize, rnd2) -> 0.0F);
        final MlpNet mlpNet = MlpNetService.createNet(config, layerSizeArr, rnd);

        //addForwwardInputs(mlpNet, 3, 3, rnd);
        //addForwwardInputs(mlpNet, 4, 4, rnd);
        //addForwwardInputs(mlpNet, 5, 5, rnd);

        //addForwwardInputs(mlpNet, 3, 2, rnd);
        //addForwwardInputs(mlpNet, 4, 2, rnd);
        //addForwwardInputs(mlpNet, 5, 2, rnd);

        //addForwwardInputs(mlpNet, 2, 1, rnd);

        //addForwwardInputs(mlpNet, 5, 4, rnd);
        //addForwwardInputs(mlpNet, 6, 4, rnd);
        addForwwardInputs(mlpNet, 5, 4, true, false, false, rnd);
        addForwwardInputs(mlpNet, 6, 4, true, false, false, rnd);

        //addForwwardInputs(mlpNet, 5, 2, rnd);
        //addForwwardInputs(mlpNet, 4, 3, rnd);
        //addForwwardInputs(mlpNet, 5, 3, rnd);
        //addForwwardInputs(mlpNet, 6, 3, rnd);
        //addForwwardInputs(mlpNet, 5, 3, rnd);

        final int successfulCounterMax = 10;
        final int epochMax = 20_000;
        final float learningRate = 0.3F;
        final float momentum = 0.6F;
        runTrainWithGrowingTrainSize(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr,
                epochMax, successfulCounterMax, true,
                learningRate, momentum, rnd);

        // Act & Assert
        actAssertExpectedOutput(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, 0.05F);
    }

    @Test
    @Disabled
    void GIVEN_digital_input_THEN_output_serialized_digits() {
        // Arrange
        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0,  0, 0, 0 }, // 000
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  0, 0, 1 }, // 001
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  0, 1, 0 }, // 010
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  0, 1, 1 }, // 011
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 0, 0 }, // 100
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 0, 1 }, // 101
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 0 }, // 110
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 1 }, // 111
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0 }, // 000
                                new float[]{ 0 },
                                new float[]{ 0 },
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 1 }, // 001
                                new float[]{ 1 },
                                new float[]{ 0 },
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 0 }, // 010
                                new float[]{ 0 },
                                new float[]{ 1 },
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 1 }, // 011
                                new float[]{ 1 },
                                new float[]{ 1 },
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 0 }, // 100
                                new float[]{ 0 },
                                new float[]{ 0 },
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 1 }, // 101
                                new float[]{ 1 },
                                new float[]{ 0 },
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 0 }, // 110
                                new float[]{ 0 },
                                new float[]{ 1 },
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 1 }, // 111
                                new float[]{ 1 },
                                new float[]{ 1 },
                                new float[]{ 1 },
                        },
                };
        //                                    0    (1)   2   3   4   (5)   6     7   8   (9)   10    11  12  13
        final int[] layerSizeArr = new int[]{ 1+3, 12*2, 12, 12, 12, 12*2, 12*2, 12, 12, 12*2, 12*1, 12, 12, 1 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpConfiguration configuration = new MlpConfiguration(true, false);
        final MlpNet mlpNet = MlpNetService.createNet(configuration, layerSizeArr, rnd);

        //addForwwardInputs(mlpNet, 13, 1, false, false, true, rnd);
        //addForwwardInputs(mlpNet, 3, 2, false, false, true, rnd);
        //addForwwardInputs(mlpNet, 4, 3, false, false, true, rnd);
        //addForwwardInputs(mlpNet, 5, 6, false, false, true, rnd);

        addForwwardInputs(mlpNet, 13, 1, false, false, true, rnd);
        addForwwardInputs(mlpNet, 3, 2, false, false, true, rnd);
        addForwwardInputs(mlpNet, 4, 3, false, false, true, rnd);

        addForwwardInputs(mlpNet, 13, 5, false, false, true, rnd);
        addForwwardInputs(mlpNet, 7, 6, false, false, true, rnd);
        addForwwardInputs(mlpNet, 8, 7, false, false, true, rnd);

        addForwwardInputs(mlpNet, 13, 9, false, false, true, rnd);
        addForwwardInputs(mlpNet, 11, 10, false, false, true, rnd);
        addForwwardInputs(mlpNet, 12, 11, false, false, true, rnd);

        //addForwwardInputs(mlpNet, 2, 2, false, false, true, rnd);
        //addForwwardInputs(mlpNet, 6, 6, false, false, true, rnd);
        //addForwwardInputs(mlpNet, 10, 10, false, false, true, rnd);

        //addForwwardInputs(mlpNet, 1, 0, false, false, true, rnd);
        //addForwwardInputs(mlpNet, 5, 4, false, false, true, rnd);
        //addForwwardInputs(mlpNet, 10, 9, false, false, true, rnd);

        final int successfulCounterMax = 6150;
        final int epochMax = 250_000;
        final float learningRate = 0.3F;
        final float momentum = 0.9F;
        runTrainWithGrowingTrainSize(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr,
                epochMax, successfulCounterMax, false,
                learningRate, momentum, rnd);

        // Act & Assert
        actAssertExpectedOutput(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, 0.05F);
    }
}
