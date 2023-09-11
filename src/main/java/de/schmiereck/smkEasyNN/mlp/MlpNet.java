package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.createFlatLayer;

import java.util.Random;

public class MlpNet {
    MlpLayer[] layers;
    final MlpInputInterface biasInput;
    final MlpInputInterface clockInput;
    private final MlpValueInput[] valueInputArr;

    private final MlpConfiguration config;

    public MlpNet(final MlpConfiguration config, int[] layersSize, final Random rnd) {
        this.config = config;
        this.layers = new MlpLayer[layersSize.length];
         this.biasInput = new MlpValueInput(MlpService.BIAS_VALUE);
        this.clockInput = new MlpValueInput(MlpService.CLOCK_VALUE);

        this.valueInputArr = new MlpValueInput[layersSize[0]];
        for (int neuronPos = 0; neuronPos < this.valueInputArr.length; neuronPos++) {
            this.valueInputArr[neuronPos] = new MlpValueInput(0.0F);
        }

        for (int layerPos = 0; layerPos < layersSize.length; layerPos++) {
            final int inputLayerPos = (layerPos == 0 ? layerPos : layerPos - 1);
            final int inputLayerSize = layersSize[inputLayerPos];
            final int additionalBiasInputSize = (this.config.useAdditionalBiasInput ? 1 : 0);
            final int additionalClockInputSize = (this.config.useAdditionalClockInput ? 1 : 0);
            final int allInputLayerSize = (inputLayerSize + additionalBiasInputSize + additionalClockInputSize);
            final int layerOutputSize = layersSize[layerPos];
            final boolean isOutputLayer = layerPos == (layersSize.length - 1);

            this.layers[layerPos] = createFlatLayer(isOutputLayer, allInputLayerSize, layerOutputSize, layerPos, inputLayerPos, inputLayerSize,
                    this.layers, this.valueInputArr, this.biasInput, this.clockInput,
                    this.config, rnd);
        }
    }

    public void setInputValue(final int inputPos, final float inputValue) {
        this.valueInputArr[inputPos].setValue(inputValue);
    }

    public MlpValueInput[] getValueInputArr() {
        return this.valueInputArr;
    }

    public MlpLayer getLayer(final int layerPos) {
        return this.layers[layerPos];
    }

    public MlpLayer getOutputLayer() {
        return this.layers[this.layers.length - 1];
    }

    public boolean getUseAdditionalBiasInput() {
        return this.config.useAdditionalBiasInput;
    }

    public boolean getUseAdditionalClockInput() {
        return this.config.useAdditionalClockInput;
    }

    public float getInitialWeightValue() {
        return this.config.initialWeightValue;
    }
}
