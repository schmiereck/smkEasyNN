package de.schmiereck.smkEasyNN.mlp;

import java.util.Random;

public class MlpNet {
    MlpLayer[] layers;

    private final MlpNeuron biasNeuron;
    private final MlpValueInput[] valueInputArr;

    public MlpNet(int[] layersSize, final boolean useAdditionalBiasInput, final Random rnd) {
        this.layers = new MlpLayer[layersSize.length];
        this.biasNeuron = new MlpNeuron(0);
        this.biasNeuron.output = MlpService.BIAS_VALUE;

        this.valueInputArr = new MlpValueInput[layersSize[0]];
        for (int neuronPos = 0; neuronPos < this.valueInputArr.length; neuronPos++) {
            this.valueInputArr[neuronPos] = new MlpValueInput(0.0F);
        }

        for (int layerPos = 0; layerPos < layersSize.length; layerPos++) {
            final int sizeInputLayerPos = (layerPos == 0 ? layerPos : layerPos - 1);
            final int inputLayerSize = layersSize[sizeInputLayerPos];
            int allInputLayerSize = (useAdditionalBiasInput ? inputLayerSize + 1 : inputLayerSize);
            int layerOutputSize = layersSize[layerPos];

            final MlpInputInterface[] inputArr = new MlpInputInterface[allInputLayerSize];

            this.layers[layerPos] = new MlpLayer(inputArr, layerOutputSize, rnd);

            for (int neuronPos = 0; neuronPos < inputLayerSize; neuronPos++) {
                if (layerPos == 0) {
                    inputArr[neuronPos] = this.valueInputArr[neuronPos];
                } else {
                    inputArr[neuronPos] = new MlpInput(this.layers[sizeInputLayerPos].neuronArr[neuronPos]);
                }
            }
            if (useAdditionalBiasInput) {
                inputArr[allInputLayerSize - 1] = new MlpInput(this.biasNeuron);
            }

            if (layerPos == (layersSize.length - 1)) {
                this.layers[layerPos].setOutputLayer(true);
            }
        }
    }

    public void setInputValue(final int inputPos, final float inputValue) {
        this.valueInputArr[inputPos].setValue(inputValue);
    }

    public MlpLayer getLayer(final int layerPos) {
        return this.layers[layerPos];
    }

    public MlpLayer getOutputLayer() {
        return this.layers[this.layers.length - 1];
    }
}
