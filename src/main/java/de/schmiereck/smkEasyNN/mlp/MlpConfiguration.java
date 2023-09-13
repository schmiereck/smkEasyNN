package de.schmiereck.smkEasyNN.mlp;

public class MlpConfiguration {
    final boolean useAdditionalBiasInput;
    final boolean useAdditionalClockInput;
    final float initialWeightValue;

    public MlpConfiguration() {
        this(false, false);
    }

    public MlpConfiguration(final boolean useAdditionalBiasInput, final boolean useAdditionalClockInput) {
        this(useAdditionalBiasInput, useAdditionalClockInput, 4.0F);
    }

    public MlpConfiguration(final boolean useAdditionalBiasInput, final boolean useAdditionalClockInput, final float initialWeightValue) {
        this.useAdditionalBiasInput = useAdditionalBiasInput;
        this.useAdditionalClockInput = useAdditionalClockInput;
        this.initialWeightValue = initialWeightValue;
    }

    public float getInitialWeightValue() {
        return this.initialWeightValue;
    }
}
