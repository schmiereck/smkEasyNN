package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpWeightTrainerService.InArrNeuronSize;
import static de.schmiereck.smkEasyNN.mlp.MlpWeightTrainerService.InArrSynapseSize;
import static de.schmiereck.smkEasyNN.mlp.MlpWeightTrainerService.OutArrSynapseSize;

import java.util.Random;

public class MlpWeightTrainer {
    final int trainSize;
    final MlpNet trainNet;

    public float trainerMse;

    public boolean useWeightDiff = false;

    public enum TrainLayerSizeEnum {
        Mini,
        Small,
        Deeper0Small,
        Deeper1Small,
        Deeper2,
        Normal,
        Big,
        Bigger,
    }

    public MlpWeightTrainer(final int trainSize, final int additionalNeuronSize, final Random rnd,
                            final TrainLayerSizeEnum trainLayerSizeEnum) {
        this.trainSize = trainSize;
        final int[] trainLayerSizeArr =
                switch (trainLayerSizeEnum) {
                    case Mini -> new int[]
                            {
                                    InArrNeuronSize + (InArrSynapseSize * (trainSize + additionalNeuronSize)),
                                    6 * trainSize,
                                    5 * trainSize,
                                    5 * trainSize,
                                    5 * trainSize,
                                    3 * trainSize,
                                    3 * trainSize,
                                    OutArrSynapseSize * (trainSize + additionalNeuronSize)
                            };
                    case Small -> new int[]
                            {
                                    InArrNeuronSize + (InArrSynapseSize * (trainSize + additionalNeuronSize)),
                                    8 * trainSize,
                                    6 * trainSize,
                                    4 * trainSize,
                                    OutArrSynapseSize * (trainSize + additionalNeuronSize)
                            };
                    case Deeper0Small -> new int[]
                            {
                                    InArrNeuronSize + (InArrSynapseSize * (trainSize + additionalNeuronSize)),
                                    8 * trainSize,
                                    6 * trainSize,
                                    4 * trainSize,
                                    4 * trainSize,
                                    OutArrSynapseSize * (trainSize + additionalNeuronSize)
                            };
                    case Deeper1Small -> new int[]
                            {
                                    InArrNeuronSize + (InArrSynapseSize * (trainSize + additionalNeuronSize)),
                                    8 * trainSize,
                                    6 * trainSize,
                                    6 * trainSize,
                                    4 * trainSize,
                                    4 * trainSize,
                                    OutArrSynapseSize * (trainSize + additionalNeuronSize)
                            };
                    case Deeper2 -> new int[]
                            {
                                    InArrNeuronSize + (InArrSynapseSize * (trainSize + additionalNeuronSize)),
                                    16 * trainSize,
                                    12 * trainSize,
                                    12 * trainSize,
                                    8 * trainSize,
                                    8 * trainSize,
                                    OutArrSynapseSize * (trainSize + additionalNeuronSize)
                            };
                    case Normal -> new int[]
                            {
                                    InArrNeuronSize + (InArrSynapseSize * (trainSize + additionalNeuronSize)),
                                    18 * trainSize,
                                    32 * trainSize,
                                    18 * trainSize,
                                    18 * trainSize,
                                    OutArrSynapseSize * (trainSize + additionalNeuronSize)
                            };
                    case Big -> new int[]
                            {
                                    InArrNeuronSize + (InArrSynapseSize * (trainSize + additionalNeuronSize)),
                                    18 * trainSize,
                                    18 * trainSize,
                                    18 * trainSize,
                                    18 * trainSize,
                                    18 * trainSize,
                                    18 * trainSize,
                                    OutArrSynapseSize * (trainSize + additionalNeuronSize)
                            };
                    case Bigger ->new int[]
                            {
                                    InArrNeuronSize + (InArrSynapseSize * (trainSize + additionalNeuronSize)),
                                    28 * trainSize,
                                    28 * trainSize,
                                    14 * trainSize,
                                    14 * trainSize,
                                    8 * trainSize,
                                    8 * trainSize,
                                    8 * trainSize,
                                    8 * trainSize,
                                    8 * trainSize,
                                    8 * trainSize,
                                    OutArrSynapseSize * (trainSize + additionalNeuronSize)
                            };
                };
        final MlpConfiguration trainConfig = new MlpConfiguration(true, false);
        this.trainNet = MlpNetService.createNet(trainConfig, trainLayerSizeArr, rnd);
    }
}
