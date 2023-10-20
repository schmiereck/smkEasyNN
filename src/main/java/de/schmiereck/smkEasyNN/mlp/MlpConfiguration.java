package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpLayer.calcInitWeightXavier;

import java.util.Random;

public class MlpConfiguration {

    @FunctionalInterface
    public interface CalcInitialWeightValueInterface {
        float calcInitialWeightValue(final int inputSize, final int outputSize, final Random rnd);
    }

    final boolean useAdditionalBiasInput;
    final boolean useAdditionalClockInput;
    final CalcInitialWeightValueInterface calcInitialWeightValueInterface;
    final CalcInitialWeightValueInterface calcInitialBiasWeightValueInterface;

    public MlpConfiguration() {
        this(false, false);
    }

    public MlpConfiguration(final boolean useAdditionalBiasInput, final boolean useAdditionalClockInput) {
        this(useAdditionalBiasInput, useAdditionalClockInput,
                //(inputSize, outputSize, rnd) -> calcInitWeight(initialWeightValue, rnd),
                (inputSize, outputSize, rnd) -> calcInitWeightXavier(inputSize, rnd),
                //(inputSize, outputSize, rnd) -> calcInitWeightNormalizedXavier(inputSize, outputSize, rnd),
                //(inputSize, outputSize, rnd) -> calcInitWeight3(initialBiasWeightValue, rnd));
                (inputSize, outputSize, rnd) -> 0.0F);
    }

    public MlpConfiguration(final boolean useAdditionalBiasInput, final boolean useAdditionalClockInput,
                            final CalcInitialWeightValueInterface calcInitialWeightValueInterface,
                            final CalcInitialWeightValueInterface calcInitialBiasWeightValueInterface) {
        this.useAdditionalBiasInput = useAdditionalBiasInput;
        this.useAdditionalClockInput = useAdditionalClockInput;
        this.calcInitialWeightValueInterface = calcInitialWeightValueInterface;
        this.calcInitialBiasWeightValueInterface = calcInitialBiasWeightValueInterface;
    }

    public CalcInitialWeightValueInterface getCalcInitialWeightValueInterface() {
        return this.calcInitialWeightValueInterface;
    }

    public CalcInitialWeightValueInterface getCalcInitialBiasWeightValueInterface() {
        return this.calcInitialBiasWeightValueInterface;
    }

    public boolean getUseAdditionalBiasInput() {
        return this.useAdditionalBiasInput;
    }
}
