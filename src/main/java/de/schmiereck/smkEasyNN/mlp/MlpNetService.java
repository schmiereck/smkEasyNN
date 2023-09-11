package de.schmiereck.smkEasyNN.mlp;

import java.util.Random;

public class MlpNetService {
    public static MlpNet createNet(final int[] layersSize, final boolean useAdditionalBiasInput, final Random rnd) {
        return createNet(layersSize, useAdditionalBiasInput, false, rnd);
    }

    public static MlpNet createNet(final int[] layersSize, final boolean useAdditionalBiasInput, final boolean useAdditionalClockInput, final Random rnd) {
        return createNet(new MlpConfiguration(useAdditionalBiasInput, useAdditionalClockInput), layersSize, rnd);
    }

    public static MlpNet createNet(final MlpConfiguration config, int[] layersSize, final Random rnd) {
        final MlpNet net = new MlpNet(config, layersSize, rnd);
        return net;
    }
}
