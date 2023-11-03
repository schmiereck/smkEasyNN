package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandom;

import de.schmiereck.smkEasyNN.mlp.persistent.MlpPersistentService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class MlpPersistentServiceTest {

    @Test
    void persistentServiceTest() throws IOException {
        // Arrange
        final float[][] trainInputArrArr = new float[][]
                {
                        new float[]{0, 0},
                        new float[]{0, 1},
                        new float[]{1, 0},
                        new float[]{1, 1}
                };
        final float[][] expectedOutputArrArr = new float[][]
                {
                        new float[]{0},
                        new float[]{1},
                        new float[]{1},
                        new float[]{0}
                };
        final int[] layerSizeArr = new int[]{ 2, 1 };

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final MlpConfiguration config = new MlpConfiguration(true, false);
        final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);

        final int epochMax = 1500;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = runTrainRandom(net, expectedOutputArrArr, trainInputArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printFullResultForEpoch(net, trainInputArrArr, expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
            }
        }
        final Path temp = Files.createTempFile(null, null);
        System.out.println("Temp file : " + temp);
        final File file = temp.toFile();
        MlpPersistentService.saveNet(file, net);
        final MlpNet net2 = MlpPersistentService.loadNet(file);

        // Act & Assert
        System.out.println("Act & Assert 1");
        printFullResultForEpoch(net, trainInputArrArr, expectedOutputArrArr, 1, 2);
        actAssertExpectedOutput(net, trainInputArrArr, expectedOutputArrArr, 0.05F);

        System.out.println("Act & Assert 2");
        printFullResultForEpoch(net2, trainInputArrArr, expectedOutputArrArr, 2, 2);
        actAssertExpectedOutput(net2, trainInputArrArr, expectedOutputArrArr, 0.05F);

    }
}
