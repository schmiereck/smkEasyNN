package de.schmiereck.smkEasyNN.world;

import static de.schmiereck.smkEasyNN.world.WorldNet.CellOutputSize;

import de.schmiereck.smkEasyNN.mlp.MlpService;
import de.schmiereck.smkEasyNN.world.WorldCell.State;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WorldMain {
    public static void main(String[] args) {
        final WorldCell[] cellArr = new WorldCell[9];

        for (int cellPos = 0; cellPos < cellArr.length; cellPos++) {
            cellArr[cellPos] = new WorldCell();
        }

        WorldNet worldNet = new WorldNet(0.25F, 0.75F, 0.0F);

        int cellPos = 4;
        final WorldCell worldCell = cellArr[cellPos];
        worldCell.state[State.ri.ordinal()] = worldNet.ri;
        worldCell.state[State.gi.ordinal()] = worldNet.gi;
        worldCell.state[State.bi.ordinal()] = worldNet.bi;
        worldCell.state[State.rs.ordinal()] = 1.0F;
        worldCell.state[State.gs.ordinal()] = 1.0F;
        worldCell.state[State.bs.ordinal()] = 1.0F;

        WorldNode worldNode = new WorldNode(4);

        worldNet.addWorldNode(worldNode);

        showWorld(cellArr, worldNet);

        for (int cnt = 0; cnt < 500000; cnt++) {
            runWorld(cellArr, worldNet);
            showWorld(cellArr, worldNet);
        }
    }

    private static void showWorld(WorldCell[] cellArr, WorldNet worldNet) {
        System.out.print("worldNodeList: ");
        worldNet.worldNodeList.stream().forEach(worldNode -> System.out.printf("[%d]", worldNode.cellPos));
        System.out.println();

        System.out.print("ident: ");
        for (int cellPos = 0; cellPos < cellArr.length; cellPos++) {
            final WorldCell worldCell = cellArr[cellPos];
            System.out.printf(" %d:[%.2f|%.2f|%.2f]",
                    cellPos,
                    worldCell.state[State.ri.ordinal()],
                    worldCell.state[State.gi.ordinal()],
                    worldCell.state[State.bi.ordinal()]);
        }
        System.out.println();
        System.out.print("state: ");
        for (int cellPos = 0; cellPos < cellArr.length; cellPos++) {
            final WorldCell worldCell = cellArr[cellPos];
            System.out.printf(" %d:[%.2f|%.2f|%.2f]",
                    cellPos,
                    worldCell.state[State.rs.ordinal()],
                    worldCell.state[State.gs.ordinal()],
                    worldCell.state[State.bs.ordinal()]);
        }
        System.out.println();
    }

    private static void runWorld(final WorldCell[] cellArr, final WorldNet worldNet) {
        List<WorldNode> nextWorldNodeList = new ArrayList<>();

        worldNet.worldNodeList.forEach(worldNode -> {
            final WorldCell w0WorldCell = cellArr[worldNode.cellPos];
            final int w1CellPos = calcWorldCellPos(cellArr, worldNode.cellPos, +1);
            final WorldCell w1WorldCell = cellArr[w1CellPos];
            final int w2CellPos = calcWorldCellPos(cellArr, worldNode.cellPos, -1);
            final WorldCell w2WorldCell = cellArr[w2CellPos];
            float[] inputArr = {
                    worldNet.ri, worldNet.gi, worldNet.bi,
                    w0WorldCell.state[State.ri.ordinal()], w0WorldCell.state[State.gi.ordinal()], w0WorldCell.state[State.bi.ordinal()],
                    w0WorldCell.state[State.rs.ordinal()], w0WorldCell.state[State.gs.ordinal()], w0WorldCell.state[State.bs.ordinal()],
                    w1WorldCell.state[State.ri.ordinal()], w1WorldCell.state[State.gi.ordinal()], w1WorldCell.state[State.bi.ordinal()],
                    w1WorldCell.state[State.rs.ordinal()], w1WorldCell.state[State.gs.ordinal()], w1WorldCell.state[State.bs.ordinal()],
                    w2WorldCell.state[State.ri.ordinal()], w2WorldCell.state[State.gi.ordinal()], w2WorldCell.state[State.bi.ordinal()],
                    w2WorldCell.state[State.rs.ordinal()], w2WorldCell.state[State.gs.ordinal()], w2WorldCell.state[State.bs.ordinal()],
            };

            // Layout: (3 + 2*3*3),
            // w0WorldCell: ident(r,g,b), state(r,g,b),
            // w1WorldCell: ident(r,g,b), state(r,g,b),
            // w2WorldCell: ident(r,g,b), state(r,g,b)
            float[] calcOutputArr = MlpService.run(worldNet.mlpNet, inputArr);

            calcTransfer(w0WorldCell, w1WorldCell, calcOutputArr, 0);
            calcTransfer(w0WorldCell, w2WorldCell, calcOutputArr, CellOutputSize);

            // Error:

            float[] errorArr = new float[WorldNet.CellOutput2Size];
            // Error between w0WorldCell cell-ident and net-ident.
            // Differenz is the error value for: w0WorldCell.state[WorldCell.State.xi]
            calcErrorCellIdentAndNetIdent(errorArr, 0 * 2*3, w0WorldCell, worldNet);
            //calcErrorCellIdentAndNetIdent(errorArr, 1 * 2*3, w1WorldCell, worldNet);
            //calcErrorCellIdentAndNetIdent(errorArr, 2 * 2*3, w2WorldCell, worldNet);

            // Error between w0WorldCell cell-state and cell-ident.
            // Differenz is the error value for: w0WorldCell.state[WorldCell.State.xs]

            MlpService.trainWithError(worldNet.mlpNet, errorArr, 0.3F, 0.6F);

            // TODO calculate costs of life step

            // TODO extend/ shrink WorldNet
            //calcIdentState(cellArr, worldNet, worldNode);
            //if (worldNode.cellPos != -1) {
                nextWorldNodeList.add(worldNode);
            //}

            if (calcStateDiff(worldNet, w1WorldCell) < 0.1F) {
            //    nextWorldNodeList.add(new WorldNode(w1CellPos));
            }

            if (calcStateDiff(worldNet, w2WorldCell) < 0.1F) {
            //    nextWorldNodeList.add(new WorldNode(w2CellPos));
            }
        });

        worldNet.worldNodeList = nextWorldNodeList;

        // TODO calculate short/ middle / long term changes for the net (better or worse)
        // Error: difference between net-ident and cell-ident
        // Error: difference between cell-ident and cell-state
        // Error: differences on meta-state
        // Differenz zum Ziel Vorher nachher vergleichen, und wenn verbesserung/ verschlechterung das als Error melden.

        float globalError = 0.1F;

        float[] errorArr = new float[WorldNet.CellOutput2Size];
        for (int errorPos = 0; errorPos < WorldNet.CellOutput2Size; errorPos++) {
            errorArr[errorPos] = -globalError; // negative error
        }

        //MlpService.trainWithError(worldNet.mlpNet, errorArr, 0.3F, 0.6F);
    }

    private static void calcErrorCellIdentAndNetIdent(final float[] errorArr, final int startErrorArrPos, final WorldCell wWorldCell, final WorldNet worldNet) {
        errorArr[startErrorArrPos + State.ri.ordinal()] = -(worldNet.ri - wWorldCell.state[State.ri.ordinal()]);
        errorArr[startErrorArrPos + State.gi.ordinal()] = -(worldNet.gi - wWorldCell.state[State.gi.ordinal()]);
        errorArr[startErrorArrPos + State.bi.ordinal()] = -(worldNet.bi - wWorldCell.state[State.bi.ordinal()]);
    }

    private static void calcIdentState(final WorldCell[] cellArr, final WorldNet worldNet, final WorldNode worldNode) {
        final WorldCell w0WorldCell = cellArr[worldNode.cellPos];

        final float stateDiff = calcStateDiff(worldNet, w0WorldCell);

        if (stateDiff > 0.1F) {
            worldNode.cellPos = -1;
        }
    }

    private static float calcStateDiff(final WorldNet worldNet, final WorldCell w0WorldCell) {
        return  Math.abs(w0WorldCell.state[State.ri.ordinal()] - worldNet.ri) +
                Math.abs(w0WorldCell.state[State.gi.ordinal()] - worldNet.gi) +
                Math.abs(w0WorldCell.state[State.bi.ordinal()] - worldNet.bi);
    }

    private static void calcTransfer(final WorldCell w0WorldCell, final WorldCell w1WorldCell, final float[] calcOutputArr, final int calcOutputOffset) {
        final State[] states = State.values();

        for (int w0StatePos = 0; w0StatePos < states.length; w0StatePos++) {
            for (int w1StatePos = 0; w1StatePos < states.length; w1StatePos++) {
                calcTransfer(w0WorldCell, states[w0StatePos],
                             w1WorldCell, states[w1StatePos],
                             calcOutputArr[calcOutputOffset + (w0StatePos * states.length) + w1StatePos]);
            }
        }
    }

    private static void calcTransfer(final WorldCell w0WorldCell, final State w0State, final WorldCell w1WorldCell, final State w1State, final float calcOutput) {
        final float factor;
        if (calcOutput > 1.0F) {
            factor = 1.0F;
        } else {
            if (calcOutput < -1.0F) {
                factor = -1.0F;
            } else {
                factor = calcOutput;
            }
        }
        final float diff = (w0WorldCell.state[w0State.ordinal()] - w1WorldCell.state[w1State.ordinal()]) * factor;
        final float diff2;
        if (diff >= 0.0F) {
            final float w0Value = w0WorldCell.state[w0State.ordinal()];
            diff2 = calcDiff2(diff, w0Value);
        } else {
            final float w1Value = w1WorldCell.state[w1State.ordinal()];
            diff2 = calcDiff2(diff, w1Value);
        }

        w0WorldCell.state[w0State.ordinal()] -= diff2;
        w1WorldCell.state[w1State.ordinal()] += diff2;
    }

    public static float calcDiff2(float diff, float w0Value) {
        final float diff2;
        if (diff > 0.0F) {
            if (diff > w0Value) {
                diff2 = w0Value;
            } else {
                diff2 = diff;
            }
        } else {
            if (-diff > w0Value) {
                diff2 = -w0Value;
            } else {
                diff2 = diff;
            }
        }
        return diff2;
    }

    private static int calcWorldCellPos(final WorldCell[] cellArr, final int cellPos, final int cellOffset) {
        final int pos = cellPos + cellOffset;
        if (pos < 0) {
            return cellArr.length - pos;
        } else {
            if (pos >= cellArr.length) {
                return pos % cellArr.length;
            } else {
                return pos;
            }
        }
    }
}
