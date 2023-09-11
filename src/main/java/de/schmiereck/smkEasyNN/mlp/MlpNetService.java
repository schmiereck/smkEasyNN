package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.createLayers;

import java.util.Random;

public class MlpNetService {
    public static MlpNet createNet(final int[] layersSize, final boolean useAdditionalBiasInput, final Random rnd) {
        return createNet(layersSize, useAdditionalBiasInput, false, rnd);
    }

    public static MlpNet createNet(final int[] layersSize, final boolean useAdditionalBiasInput, final boolean useAdditionalClockInput, final Random rnd) {
        return createNet(new MlpConfiguration(useAdditionalBiasInput, useAdditionalClockInput), layersSize, rnd);
    }

    public static MlpNet createNet(final MlpConfiguration config, int[] layersSize, final Random rnd) {
        final MlpLayerConfig[] layerConfigArr = new MlpLayerConfig[layersSize.length];

        for (int layerPos = 0; layerPos < layerConfigArr.length; layerPos++) {
            layerConfigArr[layerPos] = new MlpLayerConfig(layersSize[layerPos]);
        }

        return createNet(config, layerConfigArr, rnd);
    }

    public static MlpNet createNet(final MlpConfiguration config, final MlpLayerConfig[] layerConfigArr, final Random rnd) {
        final MlpNet net = new MlpNet(config, layerConfigArr);

        net.setLayerArr(createLayers(layerConfigArr,
                net.getValueInputArr(), net.getBiasInput(), net.getClockInput(),
                net.getConfig(), rnd));

        return net;
    }
}
