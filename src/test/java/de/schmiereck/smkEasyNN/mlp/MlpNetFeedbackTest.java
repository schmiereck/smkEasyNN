package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.addForwwardInputs;
import static de.schmiereck.smkEasyNN.mlp.MlpNetService.resetNetOutputs;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.assertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpService.run;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandomOrder;
import static de.schmiereck.smkEasyNN.mlp.MlpService.trainWithOutput;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MlpNetFeedbackTest {

    @Test
    void GIVEN_digital_numbers_in_sequenz_THEN_add_them_to_output() {
        // Arrange
        final int maxMemCount = 1; // 0, 1
        //final int maxMemCount = 2; // 0, 1, 2
        //final int maxMemCount = 3; // 0, 1, 2, 3

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpConfiguration mlpConfiguration;
        final MlpLayerConfig[] layerConfigArr;
        final int epochMax;
        final float learningRate;
        final float momentum;
        final int inputLayerNr;
        switch (maxMemCount) {
            case 1, 2 -> {
                mlpConfiguration = new MlpConfiguration(true, false,
                        0.5F, 0.0F);
                layerConfigArr = new MlpLayerConfig[5];
                layerConfigArr[0] = new MlpLayerConfig(4);
                layerConfigArr[1] = new MlpLayerConfig(8 * 3);
                layerConfigArr[2] = new MlpLayerConfig(8 * 2);
                layerConfigArr[3] = new MlpLayerConfig(8);
                layerConfigArr[4] = new MlpLayerConfig(4);
                epochMax = 6_500_000;
                learningRate = 0.1F;
                momentum = 1.1F;
                inputLayerNr = 3;
            }
            case 3 -> {
                mlpConfiguration = new MlpConfiguration(true, false,
                        0.5F, 0.0F);
                layerConfigArr = new MlpLayerConfig[6];
                layerConfigArr[0] = new MlpLayerConfig(4);
                layerConfigArr[1] = new MlpLayerConfig(8 * 3);
                layerConfigArr[2] = new MlpLayerConfig(8 * 3);
                layerConfigArr[3] = new MlpLayerConfig(8 * 2);
                layerConfigArr[4] = new MlpLayerConfig(8);
                layerConfigArr[5] = new MlpLayerConfig(4);
                epochMax = 6_500_000;
                learningRate = 0.1F;
                momentum = 1.1F;
                inputLayerNr = 4;
            }
            default -> {
                throw new RuntimeException("Not supportet.");
            }
        }
        final MlpNet net = MlpNetService.createNet(mlpConfiguration, layerConfigArr, rnd);

        MlpNetService.createInternalInputs(net, inputLayerNr, 4, 7, rnd);

        //runTraining(rnd, 1, net, learningRate, momentum, epochMax);
        //runTraining(rnd, 2, net, learningRate, momentum, 6_500_000);
        final int epochPos = runTraining(rnd, maxMemCount, net, learningRate, momentum, epochMax);

        // Act & Assert
        Assertions.assertTrue(epochPos < epochMax);

        System.out.println();
        System.out.println("Assert:");
        final float[] trainInputArr = new float[4];
        final float[] assertExpectedOutputArr = new float[4];

        for (int assertPos = 0; assertPos < 1000; assertPos++) {
            System.out.printf("assertPos: %d\n", assertPos);
            int sumInputNr = 0;
            resetNetOutputs(net);
            float[] lastCalcOutputArr = null;
            for (int pos = 0; pos <= maxMemCount; pos++) {
                final int inputNr = rnd.nextInt(maxMemCount + 1);
                sumInputNr += inputNr;

                calcBinaryInput(trainInputArr, inputNr, 4);
                calcBinaryInput(assertExpectedOutputArr, sumInputNr, 4);

                lastCalcOutputArr = run(net, trainInputArr);

                MlpNetPrintUtils.printResultLine(trainInputArr, lastCalcOutputArr, assertExpectedOutputArr);
                System.out.println();
            }

            assertExpectedOutput("assertPos: %d ".formatted(assertPos), 0.15F, trainInputArr, assertExpectedOutputArr, lastCalcOutputArr);
        }
    }

    private int runTraining(Random rnd, int maxMemCount, final MlpNet net, float learningRate, float momentum, int epochMax) {
        final float[][] trainInputArrArr = new float[8][maxMemCount];
        final float[][] expectedOutputArrArr = new float[4][maxMemCount];
        final float[] trainInputArr = new float[4];
        final float[] expectedOutputArr = new float[4];

        int epochPos = 0;
        {
            int mseSucessCounter = 0;
            int memCount = 0;
            int sumInputNr = 0;
            while (true) {
                final int inputNr = rnd.nextInt(maxMemCount + 1);

                calcBinaryInput(trainInputArr, inputNr, 4);

                if (memCount == 0) {
                    resetNetOutputs(net);
                }

                float[] calcOutputArr = run(net, trainInputArr);

                sumInputNr += inputNr;

                if (memCount == maxMemCount) {
                    //final int expectedInputNr = inputNr + sumInputNr;
                    //calcBinaryInput(expectedOutputArr, expectedInputNr, 4);
                    calcBinaryInput(expectedOutputArr, sumInputNr, 4);

                    final float mainOutputMseErrorValue = trainWithOutput(net, expectedOutputArr, calcOutputArr, learningRate, momentum);

                    if ((epochPos + 1) % 100 == 0) {
                        MlpNetPrintUtils.printFullResultForEpoch(net, trainInputArr, calcOutputArr, expectedOutputArr, epochPos, mainOutputMseErrorValue);
                    }
                    if (mainOutputMseErrorValue < 0.00001F) {
                        mseSucessCounter++;
                        if (mseSucessCounter > 24) {
                            break;
                        }
                    } else {
                        mseSucessCounter = 0;
                    }
                    sumInputNr = 0;
                    memCount = 0;
                    epochPos++;
                } else {
                    memCount++;
                }
                //sumInputNr = inputNr;

                if (epochPos > epochMax) {
                    break;
                }
            }
        }
        return epochPos;
    }

    private void calcBinaryInput(final float[] trainInputArr, final int inputNr, final int size) {
        for (int bitPos = 0; bitPos < size; bitPos++) {
            trainInputArr[(size - 1) - bitPos] = (inputNr & (0b1 << bitPos)) != 0 ? 1.0F : 0.0F;
        }
    }
}
