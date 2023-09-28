package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.addForwwardInputs;
import static de.schmiereck.smkEasyNN.mlp.MlpNetService.resetNetOutputs;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpService.run;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandomOrder;
import static de.schmiereck.smkEasyNN.mlp.MlpService.train;
import static de.schmiereck.smkEasyNN.mlp.MlpService.trainWithOutput;
import static java.lang.Float.NaN;

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
        final MlpLayerConfig[] layerConfigArr = new MlpLayerConfig[4];
        layerConfigArr[0] = new MlpLayerConfig(8);
        layerConfigArr[1] = new MlpLayerConfig(8 * maxMemCount);
        layerConfigArr[2] = new MlpLayerConfig(8 * maxMemCount);
        layerConfigArr[3] = new MlpLayerConfig(4);

        final MlpNet net = MlpNetService.createNet(mlpConfiguration, layerConfigArr, rnd);

        MlpNetService.makeInternalInput(net, 4, 2, 4);
        MlpNetService.makeInternalInput(net, 5, 2, 5);
        MlpNetService.makeInternalInput(net, 6, 2, 2);//6
        MlpNetService.makeInternalInput(net, 7, 2, 3);//7

        int mseSucessCounter = 0;
        int memCount = 0;
        int lastInputNr = 0;
        final int epochMax = 2_000_000;
        int epochPos = 0;
        while (true) {

            final int inputNr = rnd.nextInt(maxMemCount + 1);

            calcBinaryInput(trainInputArr, inputNr, 4);

            if (memCount == 0) {
                resetNetOutputs(net);
            }

            float[] calcOutputArr = run(net, trainInputArr);

            if (memCount == maxMemCount) {
                final int expectedInputNr = inputNr + lastInputNr;
                calcBinaryInput(expectedOutputArr, expectedInputNr, 4);

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
                epochPos++;
            }
            if (memCount == maxMemCount) {
                memCount = 0;
            } else {
                memCount++;
            }
            lastInputNr = inputNr;

            if (epochPos > epochMax) {
                break;
            }
        }

        // Act & Assert
        Assertions.assertTrue(epochPos < epochMax);

        final float[][][] a1trainInputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0, 0, 0, 0, 0, 0, 0, 0 },
                                new float[]{ 0, 0, 0, 1, 0, 0, 0, 0 },
                        },
                        {
                                new float[]{ 0, 0, 0, 1, 0, 0, 0, 0 },
                                new float[]{ 0, 0, 0, 0, 0, 0, 0, 0 },
                        },
                        {
                                new float[]{ 0, 0, 0, 1, 0, 0, 0, 0 },
                                new float[]{ 0, 0, 0, 1, 0, 0, 0, 0 },
                        },
                };
        final float[][][] a1expectedOutputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0, 0, 0, 0 },
                                new float[]{ 0, 0, 0, 1 },
                        },
                        {
                                new float[]{ 0, 0, 0, 1 },
                                new float[]{ 0, 0, 0, 1 },
                        },
                        {
                                new float[]{ 0, 0, 0, 1 },
                                new float[]{ 0, 0, 1, 0 },
                        }
                };
        final float[][][] a2trainInputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0, 0, 0, 0, 0, 0, 0, 0 },
                                new float[]{ 0, 0, 0, 1, 0, 0, 0, 0 },
                        },
                        {
                                new float[]{ 0, 0, 0, 0, 0, 0, 0, 0 },
                                new float[]{ 0, 0, 1, 0, 0, 0, 0, 0 },
                        },
                        {
                                new float[]{ 0, 0, 0, 1, 0, 0, 0, 0 },
                                new float[]{ 0, 0, 0, 0, 0, 0, 0, 0 },
                        },
                        {
                                new float[]{ 0, 0, 0, 1, 0, 0, 0, 0 },
                                new float[]{ 0, 0, 0, 1, 0, 0, 0, 0 },
                        },
                };
        final float[][][] a2expectedOutputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0, 0, 0, 0 },
                                new float[]{ 0, 0, 0, 1 },
                        },
                        {
                                new float[]{ 0, 0, 0, 0 },
                                new float[]{ 0, 0, 1, 0 },
                        },
                        {
                                new float[]{ 0, 0, 0, 1 },
                                new float[]{ 0, 0, 0, 1 },
                        },
                        {
                                new float[]{ 0, 0, 0, 1 },
                                new float[]{ 0, 0, 1, 0 },
                        }
                };
        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0, 0, 0, 0, NaN, NaN, NaN, NaN },
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0, 0, 0, 0 },
                        },
                };
        resetNetOutputs(net);
        actAssertExpectedOutput(net, trainInputArrArrArr, expectedOutputArrArrArr, 0.15F, true);
    }

    private void calcBinaryInput(float[] trainInputArr, int inputNr, int size) {
        for (int bitPos = 0; bitPos < size; bitPos++) {
            trainInputArr[(size - 1) - bitPos] = (inputNr & (0b1 << bitPos)) != 0 ? 1.0F : 0.0F;
        }
    }
}
