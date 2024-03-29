package de.schmiereck.smkEasyNN.mlp;

public class MlpNet {
    MlpLayer[] layerArr;
    final MlpInputInterface biasInput;
    final MlpInputInterface clockInput;
    private MlpValueInput[] valueInputArr;
    private MlpInternalValueInput[] internalValueInputArr;
    private float[] layerOutputArr;
    private final MlpConfiguration config;

    public MlpNet(final MlpConfiguration config) {
        this.config = config;

        this.biasInput = new MlpValueInput(MlpService.INTERNAL_LAYER_NR, MlpService.INTERNAL_BIAS_INPUT_NR, MlpService.BIAS_VALUE);
        this.clockInput = new MlpValueInput(MlpService.INTERNAL_LAYER_NR, MlpService.INTERNAL_CLOCK_INPUT_NR, MlpService.CLOCK_VALUE);
    }

    public void setInputValue(final int inputPos, final float inputValue) {
        this.valueInputArr[inputPos].setValue(inputValue);
    }

    public MlpValueInput[] getValueInputArr() {
        return this.valueInputArr;
    }

    public void setValueInputArr(final MlpValueInput[] valueInputArr) {
        this.valueInputArr = valueInputArr;
    }

    public MlpValueInput getInputValue(final int inputPos) {
        return this.valueInputArr[inputPos];
    }

    public void setInternalInputValue(final int inputPos, final float inputValue) {
        this.internalValueInputArr[inputPos].setValue(inputValue);
    }

    public MlpInternalValueInput[] getInternalValueInputArr() {
        return this.internalValueInputArr;
    }

    public void setInternalValueInputArr(final MlpInternalValueInput[] internalValueInputArr) {
        this.internalValueInputArr = internalValueInputArr;
    }

    public MlpInternalValueInput getInternalInputValue(final int inputPos) {
        return this.internalValueInputArr[inputPos];
    }

    public MlpLayer[] getLayerArr() {
        return this.layerArr;
    }

    public void setLayerArr(final MlpLayer[] layerArr) {
        this.layerArr = layerArr;
    }

    public MlpInputInterface getBiasInput() {
        return this.biasInput;
    }

    public MlpInputInterface getClockInput() {
        return this.clockInput;
    }

    public float[] getLayerOutputArr() {
        return this.layerOutputArr;
    }

    public void setLayerOutputArr(float[] layerOutputArr) {
        this.layerOutputArr = layerOutputArr;
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

    public MlpConfiguration getConfig() {
        return this.config;
    }
}
