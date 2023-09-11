package de.schmiereck.smkEasyNN.mlp;

public class MlpNet {
    MlpLayer[] layerArr;
    final MlpInputInterface biasInput;
    final MlpInputInterface clockInput;
    private final MlpValueInput[] valueInputArr;

    private final MlpConfiguration config;

    public MlpNet(final MlpConfiguration config, final MlpLayerConfig[] layerConfigArr) {
        this.config = config;

        this.biasInput = new MlpValueInput(MlpService.BIAS_VALUE);
        this.clockInput = new MlpValueInput(MlpService.CLOCK_VALUE);

        this.valueInputArr = new MlpValueInput[layerConfigArr[0].getSize()];
        for (int neuronPos = 0; neuronPos < this.valueInputArr.length; neuronPos++) {
            this.valueInputArr[neuronPos] = new MlpValueInput(0.0F);
        }

    }

    public void setInputValue(final int inputPos, final float inputValue) {
        this.valueInputArr[inputPos].setValue(inputValue);
    }

    public MlpValueInput[] getValueInputArr() {
        return this.valueInputArr;
    }

    public MlpLayer[] getLayerArr() {
        return this.layerArr;
    }

    public void setLayerArr(MlpLayer[] layerArr) {
        this.layerArr = layerArr;
    }

    public MlpInputInterface getBiasInput() {
        return this.biasInput;
    }

    public MlpInputInterface getClockInput() {
        return this.clockInput;
    }

    public MlpLayer getLayer(final int layerPos) {
        return this.layerArr[layerPos];
    }

    public MlpLayer getOutputLayer() {
        return this.layerArr[this.layerArr.length - 1];
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

    public MlpConfiguration getConfig() {
        return this.config;
    }
}
