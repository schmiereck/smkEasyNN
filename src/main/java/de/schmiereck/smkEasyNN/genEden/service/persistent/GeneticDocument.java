package de.schmiereck.smkEasyNN.genEden.service.persistent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.schmiereck.smkEasyNN.genEden.service.GeneticPart;
import de.schmiereck.smkEasyNN.genEden.service.Part;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneticDocument {
    public List<PerGeneticPart> geneticPartList;
}
