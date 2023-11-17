package de.schmiereck.smkEasyNN.mlp.persistent;

import static de.schmiereck.smkEasyNN.mlp.MlpNetService.createValueInputArr;
import static de.schmiereck.smkEasyNN.mlp.MlpNetService.searchErrorNeuron;
import static de.schmiereck.smkEasyNN.mlp.MlpNetService.searchInputNeuron;

import de.schmiereck.smkEasyNN.mlp.MlpConfiguration;
import de.schmiereck.smkEasyNN.mlp.MlpInputErrorInterface;
import de.schmiereck.smkEasyNN.mlp.MlpInputInterface;
import de.schmiereck.smkEasyNN.mlp.MlpInternalValueInput;
import de.schmiereck.smkEasyNN.mlp.MlpLayer;
import de.schmiereck.smkEasyNN.mlp.MlpNet;
import de.schmiereck.smkEasyNN.mlp.MlpNeuron;
import de.schmiereck.smkEasyNN.mlp.MlpSynapse;
import de.schmiereck.smkEasyNN.mlp.MlpValueInput;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MlpPersistentService {

    public static void saveNet(final File file, final MlpNet net) {
        final NetDocument netDocument = new NetDocument();

        //this.biasInput = new MlpValueInput(MlpService.INTERNAL_LAYER_NR, MlpService.INTERNAL_BIAS_INPUT_NR, MlpService.BIAS_VALUE);
        //this.clockInput = new MlpValueInput(MlpService.INTERNAL_LAYER_NR, MlpService.INTERNAL_CLOCK_INPUT_NR, MlpService.CLOCK_VALUE);
        netDocument.useAdditionalBiasInput = net.getUseAdditionalBiasInput();
        netDocument.useAdditionalClockInput = net.getUseAdditionalClockInput();

        netDocument.layerDataList = new ArrayList<>();
        final MlpLayer[] layerArr = net.getLayerArr();
        for (int layerPos = 0; layerPos < layerArr.length; layerPos++) {
            final MlpLayer layer = layerArr[layerPos];
            final LayerData layerData = new LayerData();
            layerData.layerPos = layerPos;
            layerData.isOutputLayer = layer.getIsOutputLayer();

            layerData.neuronDataList = new ArrayList<>();

            final MlpNeuron[] neuronArr = layer.getNeuronArr();
            for (int neuronPos = 0; neuronPos < neuronArr.length; neuronPos++) {
                final MlpNeuron neuron = neuronArr[neuronPos];

                final NeuronData neuronData = new NeuronData();
                neuronData.neuronNr = neuron.getNeuronNr();

                layerData.neuronDataList.add(neuronData);

                neuronData.synapseDataList = new ArrayList<>();
                final List<MlpSynapse> synapseList = neuron.getSynapseList();

                for (int synapePos = 0; synapePos < synapseList.size(); synapePos++) {
                    final MlpSynapse synapse = synapseList.get(synapePos);
                    final SynapseData synapseData = new SynapseData();
                    synapseData.inputLayerNr = synapse.getInput().getLayerNr();
                    synapseData.inputNeuronNr = synapse.getInput().getNeuronNr();
                    synapseData.useLastError = synapse.isUseLastError();
                    synapseData.useLastInput = synapse.isUseLastInput();
                    synapseData.useTrainLastInput = synapse.isUseTrainLastInput();
                    synapseData.weight = synapse.getWeight();
                    neuronData.synapseDataList.add(synapseData);
                }
            }
            netDocument.layerDataList.add(layerData);
        }

        final MlpInternalValueInput[] internalValueInputArr = net.getInternalValueInputArr();
        if (Objects.nonNull(internalValueInputArr)) {
            netDocument.internalValueInputList = new ArrayList<>();
            for (int inputPos = 0; inputPos < internalValueInputArr.length; inputPos++) {
                final MlpInternalValueInput internalValueInput = internalValueInputArr[inputPos];
                final InternalValueInputData internalValueInputData = new InternalValueInputData();

                internalValueInputData.layerNr = internalValueInput.getLayerNr();
                internalValueInputData.neuronNr = internalValueInput.getNeuronNr();
                internalValueInputData.inputLayerNr = internalValueInput.inputLayerNr;
                internalValueInputData.inputNeuronNr = internalValueInput.inputNeuronNr;
                internalValueInputData.value = internalValueInput.getInputValue();

                netDocument.internalValueInputList.add(internalValueInputData);
            }
        }
        var objectMapper = new ObjectMapper();
        //objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

        try {
            // mapper.registerModule(new JavaTimeModule());
            objectMapper.writeValue(file, netDocument);
        } catch (final JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (final JsonGenerationException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static MlpNet loadNet(final File file) {
        final NetDocument netDocument;

        var objectMapper = new ObjectMapper();

        try {
            netDocument = objectMapper.readValue(file, new TypeReference<>() {
            });
        } catch (final JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (final JsonParseException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final MlpConfiguration config = new MlpConfiguration(netDocument.useAdditionalBiasInput,
                netDocument.useAdditionalClockInput);

        final MlpNet net = new MlpNet(config);

        final List<LayerData> layerDataList = netDocument.layerDataList;
        final MlpLayer[] layerArr = new MlpLayer[layerDataList.size()];

        for (int layerPos = 0; layerPos < layerDataList.size(); layerPos++) {
            final LayerData layerData = layerDataList.get(layerPos);
            final MlpLayer layer = new MlpLayer(layerPos, 0, layerData.neuronDataList.size());
            layer.setIsOutputLayer(layerData.isOutputLayer);
            layerArr[layerPos] = layer;
        }

        net.setLayerArr(layerArr);

        final MlpInternalValueInput[] internalValueInputArr;
        final List<InternalValueInputData> internalValueInputList = netDocument.internalValueInputList;
        if (Objects.nonNull(internalValueInputList)) {
            internalValueInputArr = new MlpInternalValueInput[internalValueInputList.size()];
            for (int inputPos = 0; inputPos < internalValueInputList.size(); inputPos++) {
                final InternalValueInputData internalValueInputData = internalValueInputList.get(inputPos);
                final MlpInternalValueInput internalValueInput = new MlpInternalValueInput(internalValueInputData.layerNr,
                        internalValueInputData.neuronNr, internalValueInputData.value);
                internalValueInput.inputLayerNr = internalValueInputData.inputLayerNr;
                internalValueInput.inputNeuronNr = internalValueInputData.inputNeuronNr;

                internalValueInputArr[inputPos] = internalValueInput;
            }
        } else {
            internalValueInputArr = new MlpInternalValueInput[0];
        }
        net.setInternalValueInputArr(internalValueInputArr);

        final MlpLayer inputLayer = layerArr[0];
        final MlpValueInput[] valueInputArr = createValueInputArr(inputLayer.getNeuronArr().length);
        net.setValueInputArr(valueInputArr);

        final MlpLayer outputLayer = layerArr[layerArr.length - 1];
        net.setLayerOutputArr(new float[outputLayer.getNeuronArr().length]);

        for (int layerPos = 0; layerPos < layerArr.length; layerPos++) {
            final LayerData layerData = layerDataList.get(layerPos);
            final MlpLayer layer = layerArr[layerPos];

            final List<NeuronData> neuronDataList = layerData.neuronDataList;
            final MlpNeuron[] neuronArr = layer.getNeuronArr();
            for (int neuronPos = 0; neuronPos < neuronDataList.size(); neuronPos++) {
                final NeuronData neuronData = neuronDataList.get(neuronPos);
                final MlpNeuron neuron = neuronArr[neuronPos];

                final List<SynapseData> synapseDataList = neuronData.synapseDataList;
                for (int synapePos = 0; synapePos < synapseDataList.size(); synapePos++) {
                    final SynapseData synapseData = synapseDataList.get(synapePos);
                    final MlpInputInterface inputNeuron = searchInputNeuron(net, synapseData.inputLayerNr, synapseData.inputNeuronNr);
                    final MlpInputErrorInterface errorNeuron = searchErrorNeuron(net, synapseData.inputLayerNr, synapseData.inputNeuronNr);
                    final MlpSynapse synapse = new MlpSynapse(inputNeuron, errorNeuron,
                            synapseData.useLastError, synapseData.useLastInput, synapseData.useTrainLastInput);
                    synapse.setWeight(synapseData.weight);

                    neuron.addSynapse(synapse);
                }
            }

            layerArr[layerPos] = layer;
        }

        return net;
    }
}
