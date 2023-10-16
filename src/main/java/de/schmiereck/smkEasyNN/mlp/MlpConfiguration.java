package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpLayer.calcInitWeight;

import java.util.Random;

public class MlpConfiguration {
    @FunctionalInterface
    public interface CalcInitialWeightValueInterface {
        float calcInitialWeightValue(final int inputSize, final int outputSize, final Random rnd);
    }

    final boolean useAdditionalBiasInput;
    final boolean useAdditionalClockInput;
    final float initialWeightValue;
    final float initialBiasWeightValue;
    final CalcInitialWeightValueInterface calcInitialWeightValueInterface;
    final CalcInitialWeightValueInterface calcInitialBiasWeightValueInterface;

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
        this(useAdditionalBiasInput, useAdditionalClockInput,
                initialWeightValue,
                (inputSize, outputSize, rnd) -> calcInitWeight(initialWeightValue, rnd),
                initialBiasWeightValue,
                //(inputSize, outputSize, rnd) -> calcInitWeight3(initialBiasWeightValue, rnd));
                (inputSize, outputSize, rnd) -> initialBiasWeightValue);
    }

    public MlpConfiguration(final boolean useAdditionalBiasInput, final boolean useAdditionalClockInput,
                            final float initialWeightValue,
                            final CalcInitialWeightValueInterface calcInitialWeightValueInterface,
                            final float initialBiasWeightValue,
                            final CalcInitialWeightValueInterface calcInitialBiasWeightValueInterface) {
        this.useAdditionalBiasInput = useAdditionalBiasInput;
        this.useAdditionalClockInput = useAdditionalClockInput;
        this.initialWeightValue = initialWeightValue;
        this.initialBiasWeightValue = initialBiasWeightValue;
        this.calcInitialWeightValueInterface = calcInitialWeightValueInterface;
        this.calcInitialBiasWeightValueInterface = calcInitialBiasWeightValueInterface;
    }

    public float getInitialWeightValue() {
        return this.initialWeightValue;
    }

    public float getInitialBiasWeightValue() {
        return this.initialBiasWeightValue;
    }

    public CalcInitialWeightValueInterface getCalcInitialWeightValueInterface() {
        return this.calcInitialWeightValueInterface;
    }

    public CalcInitialWeightValueInterface getCalcInitialBiasWeightValueInterface() {
        return this.calcInitialBiasWeightValueInterface;
    }
}
