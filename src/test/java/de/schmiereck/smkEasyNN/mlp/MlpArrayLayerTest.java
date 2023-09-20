package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.addForwwardInputs;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandomOrder;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class MlpArrayLayerTest {

    @Test
    void GIVEN_value_input_moves_THEN_direction_is_output() {
        // Arrange
        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        {       // A1
                                new float[]{
                                        0, 0, 0, 0,     // stay
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 0, 1, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 0, 0, 1,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 1,     // up
                                        0, 0, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 1, 0,     // left
                                        0, 0, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 1, 0, 0,     // left
                                        0, 0, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // down
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                        },
                        {       // A2
                                new float[]{
                                        0, 0, 0, 0,     // stay
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 1, 0, 0,     // up
                                        0, 0, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        1, 0, 0, 0,     // left
                                        0, 0, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // down
                                        1, 0, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // down
                                        0, 0, 0, 0,
                                        1, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 0, 0, 0,
                                        0, 1, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // up
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                        },
                        {       // A3
                                new float[]{
                                        0, 0, 0, 0,     // stay
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // down
                                        0, 0, 0, 0,
                                        0, 1, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 0, 0, 0,
                                        0, 0, 1, 0},
                                new float[]{
                                        0, 0, 0, 0,     // up
                                        0, 0, 1, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // left
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                        },
                        {       // A4
                                new float[]{
                                        0, 0, 0, 0,     // stay
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 0, 1, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // left
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // left
                                        1, 0, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                        },
                        {       // A5
                                new float[]{
                                        0, 0, 0, 0,     // stay
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // down
                                        0, 0, 0, 0,
                                        0, 1, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // left
                                        0, 0, 0, 0,
                                        1, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 0, 0, 0,
                                        0, 1, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 0, 0, 0,
                                        0, 0, 1, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 0, 0, 0,
                                        0, 0, 0, 1},
                                new float[]{
                                        0, 0, 0, 0,     // left
                                        0, 0, 0, 0,
                                        0, 0, 1, 0},
                                new float[]{
                                        0, 0, 0, 0,     // left
                                        0, 0, 0, 0,
                                        0, 1, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // up
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        //              stay, left, right, up, down
                        {       // A1
                                new float[]{ 1, 0, 0, 0, 0 },     // stay
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                                new float[]{ 0, 0, 0, 1, 0 },     // up
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                                new float[]{ 0, 0, 0, 0, 1 },     // down
                        },
                        {       // A2
                                new float[]{ 1, 0, 0, 0, 0 },     // stay
                                new float[]{ 0, 0, 0, 1, 0 },     // up
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                                new float[]{ 0, 0, 0, 0, 1 },     // down
                                new float[]{ 0, 0, 0, 0, 1 },     // down
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                                new float[]{ 0, 0, 0, 1, 0 },     // up
                        },
                        //              stay, left, right, up, down
                        {       // A3
                                new float[]{ 1, 0, 0, 0, 0 },     // stay
                                new float[]{ 0, 0, 0, 0, 1 },     // down
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                                new float[]{ 0, 0, 0, 1, 0 },     // up
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                        },
                        {       // A4
                                new float[]{ 1, 0, 0, 0, 0 },     // stay
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                        },
                        {       // A5
                                new float[]{ 1, 0, 0, 0, 0 },     // stay
                                new float[]{ 0, 0, 0, 0, 1 },     // down
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                                new float[]{ 0, 0, 0, 1, 0 },     // up
                        },
                };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpConfiguration mlpConfiguration = new MlpConfiguration(true, false, 1.25F);
        final MlpLayerConfig[] layerConfigArr = new MlpLayerConfig[5];
        layerConfigArr[0] = new MlpLayerConfig(12);
        layerConfigArr[1] = new MlpLayerConfig(24);
        layerConfigArr[2] = new MlpLayerConfig(12);
        layerConfigArr[3] = new MlpLayerConfig(8);
        layerConfigArr[4] = new MlpLayerConfig(5);

        layerConfigArr[0].setIsArray(true);
        layerConfigArr[1].setIsArray(true);

        final MlpNet mlpNet = MlpNetService.createNet(mlpConfiguration, layerConfigArr, rnd);

        addForwwardInputs(mlpNet, 2, 1, rnd);

        //final int epochMax = 25_000; // Flat
        final int epochMax = 7_800; // Array
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = runTrainRandomOrder(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                MlpNetPrintUtils.printFullResultForEpoch(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, epochPos, mainOutputMseErrorValue);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, 0.075F);
        /*
        final float[][] inputArrArr = new float[][]
                {
                        new float[]{ 0, 0, 0, 0, 0, 1, 1 }, // 1 + 2 = 3
                        new float[]{ 0, 0, 0, 1, 0, 0, 1 }, // 1 + 4 = 5
                        new float[]{ 0, 0, 0, 1, 0, 1, 0 }, // 2 + 4 = 6
                        new float[]{ 0, 0, 0, 1, 1, 0, 0 }, // 3 + 4 = 7
                };
        final float[][] unexpectedOutputArrArr = new float[][]
                {
                        new float[]{0, 1, 1}, // 3
                        new float[]{1, 0, 1}, // 5
                        new float[]{1, 1, 0}, // 6
                        new float[]{1, 1, 1}, // 7
                };

        actAssertExpectedOutput(mlpNet, inputArrArr, unexpectedOutputArrArr, 0.6F);
        System.out.println();
        System.out.println("unexpectedOutput:");
        printResult(mlpNet, inputArrArr, unexpectedOutputArrArr);
        */
    }
}
