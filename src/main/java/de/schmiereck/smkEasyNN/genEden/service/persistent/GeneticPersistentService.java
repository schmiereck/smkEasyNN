package de.schmiereck.smkEasyNN.genEden.service.persistent;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.schmiereck.smkEasyNN.genEden.service.GeneticPart;
import de.schmiereck.smkEasyNN.genEden.service.Part;
import de.schmiereck.smkEasyNN.genNet.GenNet;
import de.schmiereck.smkEasyNN.genNet.GenNeuron;
import de.schmiereck.smkEasyNN.genNet.GenSynapse;
import de.schmiereck.smkEasyNN.mlp.persistent.NetDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GeneticPersistentService {
    public static void saveNet(final File file, final List<GeneticPart> geneticPartList) {
        final GeneticDocument doc = new GeneticDocument();

        doc.geneticPartList = geneticPartList.stream().map(geneticPart -> {
            final PerGeneticPart perGeneticPart = new PerGeneticPart();
            perGeneticPart.visibleValueArr = geneticPart.getVisibleValueArr();
            perGeneticPart.moveDir = geneticPart.getMoveDir();
            perGeneticPart.size = geneticPart.getSize();
            perGeneticPart.energie = geneticPart.getEnergie();
            perGeneticPart.age = geneticPart.getAge();
            perGeneticPart.genNet = createPerGenNet(geneticPart.getGenNet());
            return perGeneticPart;
        }).toList();
        var objectMapper = new ObjectMapper();
        //objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

        try {
            // mapper.registerModule(new JavaTimeModule());
            objectMapper.writeValue(file, doc);
        } catch (final JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (final JsonGenerationException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static PerGenNet createPerGenNet(final GenNet genNet) {
        final PerGenNet perGenNet = new PerGenNet();

        perGenNet.error = genNet.getError();
        //perGenNet.inputNeuronList = genNet.getInputNeuronList();
        perGenNet.neuronList = createPerNeuronList(genNet.getNeuronList());
        //perGenNet.outputNeuronList = genNet.getOutputNeuronList();

        return perGenNet;
    }

    private static List<PerGenNeuron> createPerNeuronList(final List<GenNeuron> neuronList) {
        final List<PerGenNeuron> perGenNeuronList = neuronList.stream().map(genNeuron -> {
            final PerGenNeuron perGenNeuron = new PerGenNeuron();
            perGenNeuron.neuronType = genNeuron.getNeuronType();
            perGenNeuron.neuronIndex = genNeuron.getNeuronIndex();
            perGenNeuron.inputSynapseList = createPerGenSynapseList(genNeuron.getInputSynapseList());
            perGenNeuron.bias = genNeuron.getBias();
            perGenNeuron.outputValue = genNeuron.getOutputValue();
            return perGenNeuron;
        }).toList();
        return perGenNeuronList;
    }

    private static List<PerGenSynapse> createPerGenSynapseList(final List<GenSynapse> inputSynapseList) {
        final List<PerGenSynapse> perGenSynapseList;
        if (Objects.nonNull(inputSynapseList)) {
            perGenSynapseList = inputSynapseList.stream().map(genSynapse -> {
                final PerGenSynapse perGenSynapse = new PerGenSynapse();
                perGenSynapse.inGenNeuronIndex = genSynapse.getInGenNeuron().getNeuronIndex();
                perGenSynapse.weight = genSynapse.getWeight();
                return perGenSynapse;
            }).toList();
        } else {
            perGenSynapseList = null;
        }
        return perGenSynapseList;
    }

    public static List<GeneticPart> loadNet(final File file) {
        final GeneticDocument doc;

        var objectMapper = new ObjectMapper();

        try {
            doc = objectMapper.readValue(file, new TypeReference<>() {
            });
        } catch (final JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (final JsonParseException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final List<GeneticPart> geneticPartList = doc.geneticPartList.stream().map(perGeneticPart -> {
            final GeneticPart geneticPart = new GeneticPart(perGeneticPart.visibleValueArr);
            geneticPart.setMoveDir(perGeneticPart.moveDir);
            geneticPart.setSize(perGeneticPart.size);
            geneticPart.setEnergie(perGeneticPart.energie);
            geneticPart.setAge(perGeneticPart.age);
            geneticPart.setGenNet(createGenNet(perGeneticPart.genNet));
            return geneticPart;
        }).toList();

        return geneticPartList;
    }

    private static GenNet createGenNet(final PerGenNet perGenNet) {
        final GenNet genNet = new GenNet();

        genNet.setError(perGenNet.error);

        perGenNet.neuronList.forEach(perGenNeuron -> {
            final GenNeuron genNeuron = new GenNeuron(perGenNeuron.neuronType, perGenNeuron.bias);
            genNeuron.setNeuronIndex(perGenNeuron.neuronIndex);
            genNeuron.setInputSynapseList(createGenSynapseList(genNet, perGenNeuron.inputSynapseList));
            genNeuron.setOutputValue(perGenNeuron.outputValue);
            genNet.getNeuronList().add(genNeuron);
            if (genNeuron.getNeuronType() == GenNeuron.NeuronType.Input) {
                genNet.getInputNeuronList().add(genNeuron);
            }
            if (genNeuron.getNeuronType() == GenNeuron.NeuronType.Output) {
                genNet.getOutputNeuronList().add(genNeuron);
            }
        });

        perGenNet.neuronList.forEach(perGenNeuron -> {
            final GenNeuron genNeuron = searchGenNeuron(genNet, perGenNeuron.neuronIndex);

            genNeuron.setInputSynapseList(createGenSynapseList(genNet, perGenNeuron.inputSynapseList));
        });

        return genNet;
    }

    private static GenNeuron searchGenNeuron(final GenNet genNet, final int neuronIndex) {
        final GenNeuron retGenNeuron = genNet.getNeuronList().
                stream().
                filter(genNeuron -> genNeuron.getNeuronIndex() == neuronIndex).
                findFirst().orElseThrow();

        return retGenNeuron;
    }

    private static List<GenSynapse> createGenSynapseList(final GenNet genNet, final List<PerGenSynapse> inputSynapseList) {
        final List<GenSynapse> genSynapseList;
        if (Objects.nonNull(inputSynapseList)) {
            genSynapseList = new ArrayList<>();
            inputSynapseList.forEach(perGenSynapse -> {
                final GenNeuron inGenNeuron = searchGenNeuron(genNet, perGenSynapse.inGenNeuronIndex);
                final GenSynapse genSynapse = new GenSynapse(inGenNeuron, perGenSynapse.weight);
                genSynapseList.add(genSynapse);
            });
        } else {
            genSynapseList = null;
        }
        return genSynapseList;
    }

}
