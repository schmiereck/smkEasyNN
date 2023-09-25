package de.schmiereck.smkEasyNN.mlp;

public class MlpConfiguration {
    final boolean useAdditionalBiasInput;
    final boolean useAdditionalClockInput;
    final float initialWeightValue;
    final float initialBiasWeightValue;

    public MlpConfiguration() {
        this(false, false);
    }

    public MlpConfiguration(final boolean useAdditionalBiasInput, final boolean useAdditionalClockInput) {
        this(useAdditionalBiasInput, useAdditionalClockInput, 4.0F);
    }

    public MlpConfiguration(final boolean useAdditionalBiasInput, final boolean useAdditionalClockInput, final float initialWeightValue) {
        this(useAdditionalBiasInput, useAdditionalClockInput, initialWeightValue, 0.0F);
    }

    public MlpConfiguration(final boolean useAdditionalBiasInput, final boolean useAdditionalClockInput, final float initialWeightValue, final float initialBiasWeightValue) {
        this.useAdditionalBiasInput = useAdditionalBiasInput;
        this.useAdditionalClockInput = useAdditionalClockInput;
        this.initialWeightValue = initialWeightValue;
        this.initialBiasWeightValue = initialBiasWeightValue;
    }

    public float getInitialWeightValue() {
        return this.initialWeightValue;
    }

    public float getInitialBiasWeightValue() {
        return this.initialBiasWeightValue;
    }
}
