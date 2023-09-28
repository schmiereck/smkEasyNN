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
    void GIVEN_xxx_THEN_yyy() {
        // Arrange
        //final int maxMemCount = 1;
        final int maxMemCount = 2;

        final float[][] trainInputArrArr = new float[8][maxMemCount];
        final float[][] expectedOutputArrArr = new float[4][maxMemCount];
        final float[] trainInputArr = new float[8];
        final float[] expectedOutputArr = new float[4];

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpConfiguration mlpConfiguration = new MlpConfiguration(true, false,
                0.5F, 0.0F);
        final MlpLayerConfig[] layerConfigArr = new MlpLayerConfig[5];
        layerConfigArr[0] = new MlpLayerConfig(8);
        layerConfigArr[1] = new MlpLayerConfig(8 * 3);
        layerConfigArr[2] = new MlpLayerConfig(8 * 2);
        layerConfigArr[3] = new MlpLayerConfig(8);
        layerConfigArr[4] = new MlpLayerConfig(4);

        final MlpNet net = MlpNetService.createNet(mlpConfiguration, layerConfigArr, rnd);

        MlpNetService.makeInternalInput(net, 4, 3, 4);
        MlpNetService.makeInternalInput(net, 5, 3, 5);
        MlpNetService.makeInternalInput(net, 6, 3, 6);//6, 2
        MlpNetService.makeInternalInput(net, 7, 3, 7);//7, 3

        final int epochMax = 3_500_000;
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

                    final float mainOutputMseErrorValue = trainWithOutput(net, expectedOutputArr, calcOutputArr, 0.1F, 1.1F);

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
        // Act & Assert
        Assertions.assertTrue(epochPos < epochMax);

        System.out.println();
        System.out.println("Assert:");
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

    private void calcBinaryInput(final float[] trainInputArr, final int inputNr, final int size) {
        for (int bitPos = 0; bitPos < size; bitPos++) {
            trainInputArr[(size - 1) - bitPos] = (inputNr & (0b1 << bitPos)) != 0 ? 1.0F : 0.0F;
        }
    }
}
