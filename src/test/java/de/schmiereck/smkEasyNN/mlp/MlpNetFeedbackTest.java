package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.addForwwardInputs;
import static de.schmiereck.smkEasyNN.mlp.MlpNetService.resetNetOutputs;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpService.run;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandomOrder;
import static de.schmiereck.smkEasyNN.mlp.MlpService.train;
import static de.schmiereck.smkEasyNN.mlp.MlpService.trainWithOutput;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MlpNetFeedbackTest {

    @Test
    void GIVEN_xxx_THEN_yyy() {
        // Arrange
        final float[] trainInputArr = new float[4];
        final float[] expectedOutputArr = new float[2];

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpConfiguration mlpConfiguration = new MlpConfiguration(true, false,
                0.5F, 0.0F);
        final MlpLayerConfig[] layerConfigArr = new MlpLayerConfig[4];
        layerConfigArr[0] = new MlpLayerConfig(4);
        layerConfigArr[1] = new MlpLayerConfig(4);
        layerConfigArr[2] = new MlpLayerConfig(4);
        layerConfigArr[3] = new MlpLayerConfig(2);

        final MlpNet net = MlpNetService.createNet(mlpConfiguration, layerConfigArr, rnd);

        //addForwwardInputs2(mlpNet, 2, 1, true, false, true, false, false, rnd);
        //addForwwardInputs(net, 2, 1, false, false, true, true, true, rnd);
        //addAdditionalBiasInputToLayer(mlpNet, 2, false, rnd);

        final int maxMemCount = 1;
        int memCount = 0;
        int lastInputNr = 0;
        final int epochMax = 110_000;
        int epochPos = 0;
        while (true) {

            final int inputNr = rnd.nextInt(2);

            trainInputArr[0] = (inputNr & 0b10) != 0 ? 1.0F : 0.0F;
            trainInputArr[1] = (inputNr & 0b01) != 0 ? 1.0F : 0.0F;

            if (memCount == 0) {
                resetNetOutputs(net);
                trainInputArr[2] = 0.0F;
                trainInputArr[3] = 0.0F;
            } else {
                final MlpLayer[] layerArr = net.getLayerArr();
                final MlpLayer layer = layerArr[2];

                trainInputArr[2] = layer.neuronArr[2].getInputValue();
                trainInputArr[3] = layer.neuronArr[3].getInputValue();
            }

            float[] calcOutputArr = run(net, trainInputArr);

            if (memCount > 0) {
                final int expectedInputNr = inputNr + lastInputNr;
                expectedOutputArr[0] = (expectedInputNr & 0b10) != 0 ? 1.0F : 0.0F;
                expectedOutputArr[1] = (expectedInputNr & 0b01) != 0 ? 1.0F : 0.0F;

                final float mainOutputMseErrorValue = trainWithOutput(net, expectedOutputArr, calcOutputArr, 0.1F, 0.9F);

                if ((epochPos + 1) % 100 == 0) {
                    MlpNetPrintUtils.printFullResultForEpoch(net, trainInputArr, calcOutputArr, expectedOutputArr, epochPos, mainOutputMseErrorValue);
                }
                if (mainOutputMseErrorValue < 0.00001F) {
                    break;
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

        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        {
                                new float[]{0, 0, 0, 0},
                                new float[]{0, 1, 0, 0},
                        },
                        {
                                new float[]{ 0, 1, 0, 0 },
                                new float[]{ 0, 0, 0, 0 },
                        },
                        {
                                new float[]{ 0, 1, 0, 0 },
                                new float[]{ 0, 1, 0, 0 },
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0, 0 },
                                new float[]{ 0, 1 },
                        },
                        {
                                new float[]{ 0, 1 },
                                new float[]{ 0, 0 },
                        },
                        {
                                new float[]{ 0, 1 },
                                new float[]{ 1, 0 },
                        }
                };
        //actAssertExpectedOutput(net, trainInputArrArrArr, expectedOutputArrArrArr, 0.15F, true);
    }
}
