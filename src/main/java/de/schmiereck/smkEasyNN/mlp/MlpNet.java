package de.schmiereck.smkEasyNN.mlp;

import java.util.Random;

public class MlpNet {
    MlpLayer[] layers;

    private final boolean useAdditionalBiasInput;

    public MlpNet(int[] layersSize, final boolean useAdditionalBiasInput, final Random rnd) {
        this.layers = new MlpLayer[layersSize.length];
        this.useAdditionalBiasInput = useAdditionalBiasInput;

        //final Random rnd = new Random(1234);

        for (int layerPos = 0; layerPos < layersSize.length; layerPos++) {
            final int sizeLayerPos = (layerPos == 0 ? layerPos : layerPos - 1);
            int layerInputSize = (useAdditionalBiasInput ? layersSize[sizeLayerPos] + 1 : layersSize[sizeLayerPos]);
            int layerOutputSize = layersSize[layerPos];

            this.layers[layerPos] = new MlpLayer(layerInputSize, layerOutputSize, rnd);

            if (layerPos == (layersSize.length - 1)) {
                this.layers[layerPos].setOutputLayer(true);
            }
        }
    }

    public MlpLayer getLayer(final int layerPos) {
        return this.layers[layerPos];
    }

    public boolean getUseAdditionalBiasInput() {
        return useAdditionalBiasInput;
    }
}
