package de.schmiereck.smkEasyNN.world;

import de.schmiereck.smkEasyNN.mlp.MlpNet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldNet {
    final float ri, gi, bi;
    final MlpNet mlpNet;

    List<WorldNode> worldNodeList = new ArrayList<>();

    public static final int CellOutputSize = WorldCell.State.values().length * WorldCell.State.values().length;
    public static final int CellOutput2Size = CellOutputSize * 2;

    public WorldNet(final float ri, final float gi, final float bi) {
        this.ri = ri;
        this.gi = gi;
        this.bi = bi;
        final Random rnd = new Random();
        final int[] layerSizeArr = new int[] {
                (3 + 2*3*3),
                (2*3*3),
                (2*3*3),
                (CellOutput2Size)
        };
        this.mlpNet = new MlpNet(layerSizeArr, true, rnd);
    }

    public void addWorldNode(WorldNode worldNode) {
        this.worldNodeList.add(worldNode);
    }
}
