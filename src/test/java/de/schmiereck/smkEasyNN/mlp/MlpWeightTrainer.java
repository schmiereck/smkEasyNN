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

    public MlpWeightTrainer(final int trainSize, final int additionalNeuronSize, final Random rnd) {
        this.trainSize = trainSize;
        final int[] trainLayerSizeArr = new int[]
                {
                        InArrNeuronSize + (InArrSynapseSize * (trainSize + additionalNeuronSize)),

                        // Small
                        /*
                        */
                        8 * trainSize,
                        6 * trainSize,
                        4 * trainSize,

                        // normal
                        /*
                        18 * trainSize,
                        32 * trainSize,
                        18 * trainSize,
                        18 * trainSize,
                        */

                        // Big
                        /*
                        18 * trainSize,
                        18 * trainSize,
                        18 * trainSize,
                        18 * trainSize,
                        18 * trainSize,
                        18 * trainSize,
                        */

                        // Bigger
                        /*
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
                        */

                        OutArrSynapseSize * (trainSize + additionalNeuronSize)
                };
        final MlpConfiguration trainConfig = new MlpConfiguration(true, false);
        this.trainNet = MlpNetService.createNet(trainConfig, trainLayerSizeArr, rnd);
    }
}
