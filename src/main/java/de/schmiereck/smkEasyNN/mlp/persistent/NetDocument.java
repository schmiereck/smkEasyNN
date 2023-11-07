package de.schmiereck.smkEasyNN.mlp.persistent;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * List<LayerData>
 *      - List<NeuronData>
 *          - List<SynapseData>
 *              - inputLayerNr
 *              - inputNeuronNr
 *              - weight
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetDocument {
    public List<LayerData> layerDataList;
    public boolean useAdditionalBiasInput;
    public boolean useAdditionalClockInput;
    public List<InternalValueInputData> internalValueInputList;
}
