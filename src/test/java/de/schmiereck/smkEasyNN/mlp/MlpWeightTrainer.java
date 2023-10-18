package de.schmiereck.smkEasyNN.mlp;

import java.util.Random;

public class MlpWeightTrainer {
    final int trainSize;
    final MlpNet trainNet;

    public MlpWeightTrainer(final int trainSize, final Random rnd) {
        this.trainSize = trainSize;
        final int[] trainLayerSizeArr = new int[]{ 3 + 3 * trainSize, 6 * trainSize, 4 * trainSize, 2 * trainSize };
        final MlpConfiguration trainConfig = new MlpConfiguration(true, false);
        this.trainNet = MlpNetService.createNet(trainConfig, trainLayerSizeArr, rnd);
    }
}
