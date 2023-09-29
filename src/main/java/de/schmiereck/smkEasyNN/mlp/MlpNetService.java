package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpLayer.calcInitWeight2;
import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.createLayers;
import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.createSynapse;
import static de.schmiereck.smkEasyNN.mlp.MlpService.FIRST_LAYER_NR;
import static de.schmiereck.smkEasyNN.mlp.MlpService.INPUT_LAYER_NR;
import static de.schmiereck.smkEasyNN.mlp.MlpService.INTERNAL_BIAS_INPUT_NR;
import static de.schmiereck.smkEasyNN.mlp.MlpService.INTERNAL_CLOCK_INPUT_NR;
import static de.schmiereck.smkEasyNN.mlp.MlpService.INTERNAL_INPUT_LAYER_NR;
import static de.schmiereck.smkEasyNN.mlp.MlpService.INTERNAL_LAYER_NR;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class MlpNetService {
    private MlpNetService() {}

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
        final MlpNet net = new MlpNet(config);

        final MlpValueInput[] valueInputArr = new MlpValueInput[layerConfigArr[0].getSize()];
        for (int neuronPos = 0; neuronPos < valueInputArr.length; neuronPos++) {
            valueInputArr[neuronPos] = new MlpValueInput(INPUT_LAYER_NR, neuronPos, 0.0F);
        }
        net.setValueInputArr(valueInputArr);

        net.setLayerArr(createLayers(layerConfigArr,
                net.getValueInputArr(), net.getBiasInput(), net.getClockInput(),
                net.getConfig(), true, false, rnd));

        final MlpLayer outputLayer = net.getOutputLayer();
        net.setLayerOutputArr(new float[outputLayer.neuronArr.length]);

        return net;
    }

    public static MlpNet duplicateNet(final MlpNet net) {
        final MlpNet newNet = new MlpNet(net.getConfig());

        newNet.setValueInputArr(duplicateValueInputArr(net.getValueInputArr()));
        newNet.setInternalValueInputArr(duplicateInternalValueInputArr(net.getInternalValueInputArr()));
        newNet.setLayerArr(duplicateNeuronLayers(newNet, net.getLayerArr()));
        duplicateSynapses(newNet, net.getLayerArr());

        final MlpLayer outputLayer = newNet.getOutputLayer();
        newNet.setLayerOutputArr(new float[outputLayer.neuronArr.length]);

        return newNet;
    }

    private static MlpValueInput[] duplicateValueInputArr(MlpValueInput[] valueInputArr) {
        final MlpValueInput[] newValueInputArr = new MlpValueInput[valueInputArr.length];
        for (int neuronPos = 0; neuronPos < valueInputArr.length; neuronPos++) {
            final MlpValueInput valueInput = valueInputArr[neuronPos];
            newValueInputArr[neuronPos] = new MlpValueInput(valueInput.getLayerNr(), valueInput.getNeuronNr(), valueInput.getInputValue());
        }
        return newValueInputArr;
    }

    private static MlpInternalValueInput[] duplicateInternalValueInputArr(MlpInternalValueInput[] valueInputArr) {
        final MlpInternalValueInput[] newValueInputArr = new MlpInternalValueInput[valueInputArr.length];
        for (int neuronPos = 0; neuronPos < valueInputArr.length; neuronPos++) {
            final MlpInternalValueInput valueInput = valueInputArr[neuronPos];
            newValueInputArr[neuronPos] = new MlpInternalValueInput(valueInput.getLayerNr(), valueInput.getNeuronNr(), valueInput.getInputValue());

            newValueInputArr[neuronPos].internalInput = valueInput.internalInput;
            newValueInputArr[neuronPos].inputLayerNr = valueInput.inputLayerNr;
            newValueInputArr[neuronPos].inputNeuronNr = valueInput.inputNeuronNr;
        }
        return newValueInputArr;
    }

    private static MlpLayer[] duplicateNeuronLayers(final MlpNet newNet, final MlpLayer[] layerArr) {
        final MlpLayer[] newLayerArr = new MlpLayer[layerArr.length];

        for (int layerPos = 0; layerPos < layerArr.length; layerPos++) {
            final MlpLayer layer = layerArr[layerPos];
            newLayerArr[layerPos] = duplicateLayer(layer);
        }
        return newLayerArr;
    }

    private static MlpLayer[] duplicateSynapses(final MlpNet newNet, final MlpLayer[] layerArr) {
        final MlpLayer[] newLayerArr = newNet.getLayerArr();

        for (int layerPos = 0; layerPos < layerArr.length; layerPos++) {
            final MlpLayer layer = layerArr[layerPos];
            final MlpLayer newLayer = newLayerArr[layerPos];

            for (int neuronPos = 0; neuronPos < layer.neuronArr.length; neuronPos++) {
                final MlpNeuron neuron = layer.neuronArr[neuronPos];
                final MlpNeuron newNeuron = newLayer.neuronArr[neuronPos];

                final List<MlpSynapse> synapseList = neuron.synapseList;
                for (final MlpSynapse synapse : synapseList) {
                    final MlpSynapse newSynapse = duplicateSynapse(newNet, synapse);
                    newNeuron.synapseList.add(newSynapse);
                }
            }
        }
        return newLayerArr;
    }

    private static MlpSynapse duplicateSynapse(final MlpNet newNet, final MlpSynapse synapse) {
        final MlpInputInterface input = searchInputNeuron(newNet, synapse.getInput().getLayerNr(), synapse.getInput().getNeuronNr());

        final MlpInputErrorInterface inputError;
        if (Objects.nonNull(synapse.getInputError())) {
            inputError = searchErrorNeuron(newNet, synapse.getInputError().getLayerNr(), synapse.getInputError().getNeuronNr());
        } else {
            inputError = null;
        }

        final MlpSynapse newSynapse = new MlpSynapse(input, inputError,
                synapse.useLastError, synapse.useLastInput, synapse.useTrainLastInput);
        newSynapse.weight = synapse.weight;
        newSynapse.dweight = synapse.dweight;
        return newSynapse;
    }

    private static MlpInputInterface searchInputNeuron(final MlpNet newNet, final int layerNr, final int neuronNr) {
        final MlpInputInterface input;
        if (layerNr == INTERNAL_LAYER_NR) {
            input =
                switch (neuronNr) {
                    case INTERNAL_BIAS_INPUT_NR -> newNet.getBiasInput();
                    case INTERNAL_CLOCK_INPUT_NR -> newNet.getClockInput();
                    default -> throw new RuntimeException("Unexpected neuronNr \"%d\".".formatted(neuronNr));
                };
        } else {
            if (layerNr == INPUT_LAYER_NR) {
                input = newNet.getValueInputArr()[neuronNr];
            } else {
                final MlpLayer newInputLayer = newNet.getLayerArr()[layerNr];
                input = newInputLayer.neuronArr[neuronNr];
            }
        }
        return input;
    }

    private static MlpInputErrorInterface searchErrorNeuron(final MlpNet newNet, final int layerNr, final int neuronNr) {
        return newNet.getLayerArr()[layerNr].neuronArr[neuronNr];
    }

    private static MlpLayer duplicateLayer(final MlpLayer layer) {
        final MlpLayer newLayer = new MlpLayer(layer.layerNr, 0, layer.neuronArr.length);
        newLayer.setIsOutputLayer(layer.getIsOutputLayer());

        for (int neuronPos = 0; neuronPos < layer.neuronArr.length; neuronPos++) {
            final MlpNeuron neuron = layer.neuronArr[neuronPos];
            final MlpNeuron newNeuron = newLayer.neuronArr[neuronPos];

            newNeuron.neuronNr = neuron.neuronNr;
            newNeuron.outputValue = neuron.outputValue;
            newNeuron.lastOutputValue = neuron.lastOutputValue;
            newNeuron.errorValue = neuron.errorValue;
            newNeuron.lastErrorValue = neuron.lastErrorValue;
        }

        return newLayer;
    }

    public static void resetNetOutputs(final MlpNet net) {
        final MlpValueInput[] valueInputArr = net.getValueInputArr();
        for (final MlpValueInput valueInput : valueInputArr) {
            valueInput.setValue(0.0F);
        }

        final MlpLayer[] layerArr = net.getLayerArr();

        for (int layerPos = 0; layerPos < layerArr.length; layerPos++) {
            final MlpLayer layer = layerArr[layerPos];

            for (int neuronPos = 0; neuronPos < layer.neuronArr.length; neuronPos++) {
                final MlpNeuron neuron = layer.neuronArr[neuronPos];

                neuron.setOutputValue(0.0F);
                neuron.setLastOutputValue(0.0F);
                neuron.setErrorValue(0.0F);
                neuron.setLastErrorValue(0.0F);
            }
        }

        net.getClockInput().setValue(MlpService.CLOCK_VALUE);
    }

    public static void createInternalInputs(final MlpNet net, final int inputLayerNr, final int firstNeuronPos, final int lastNeuronPos, final Random rnd) {
        final int inputSize = (lastNeuronPos - firstNeuronPos) + 1;
        final MlpInternalValueInput[] internalValueInputArr = new MlpInternalValueInput[inputSize];

        for (int neuronPos = 0; neuronPos < inputSize; neuronPos++) {
            final MlpInternalValueInput valueInput = new MlpInternalValueInput(INTERNAL_INPUT_LAYER_NR, neuronPos, 0.0F);

            valueInput.inputLayerNr = inputLayerNr;
            valueInput.inputNeuronNr = firstNeuronPos + neuronPos;

            internalValueInputArr[neuronPos] = valueInput;
        }
        net.setInternalValueInputArr(internalValueInputArr);

        final MlpLayer layer = net.getLayer(FIRST_LAYER_NR);

        for (int neuronPos = 0; neuronPos < layer.neuronArr.length; neuronPos++) {
            final MlpNeuron neuron = layer.neuronArr[neuronPos];

            for (int inputPos = 0; inputPos < internalValueInputArr.length; inputPos++) {
                final MlpInternalValueInput valueInput = internalValueInputArr[inputPos];

                //final MlpLayer inputLayer = net.getLayer(valueInput.inputLayerNr);
                //final MlpNeuron inputNeuron = inputLayer.neuronArr[valueInput.inputNeuronNr];

                final boolean useLastError = false;
                final boolean useLastInput = false;
                final boolean useTrainLastInput = false;

                final MlpSynapse synapse = MlpLayerService.createSynapse(calcInitWeight2(net.getInitialWeightValue(), rnd),
                        useLastError, useLastInput, useTrainLastInput, valueInput, null);
                neuron.synapseList.add(synapse);
            }
        }
    }
}
