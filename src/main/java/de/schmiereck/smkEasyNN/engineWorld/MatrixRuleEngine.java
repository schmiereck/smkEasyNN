package de.schmiereck.smkEasyNN.engineWorld;

import de.schmiereck.smkEasyNN.engineWorld.EngineWorldService.PositionType;

import java.util.List;

import static de.schmiereck.smkEasyNN.engineWorld.MatrixRuleEngineService.calcCountResult;

public class MatrixRuleEngine extends RuleEngine {

    final List<MatrixOutputState> outputStateList;

    public MatrixRuleEngine(final PositionType inputPositionType, final int inputTypePos, final int inputEnergyPos, final int inputImpulsePos,
                            final List<MatrixOutputState> outputStateList) {
        super(inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos);

        this.outputStateList = outputStateList;
    }

    @Override
    RuleState calc(final EngineWorldService engineWorldService, final RuleState positionRuleState, final EwState positionEwState) {
        final RuleState retPositionRuleState;

        // TODO Idee: positionEwState gleich in der World in Chuncs mit L&R Nachbarn in kleinen [3] Arrays speichen.
        //    könnte nützlich für die Operationen werden.

        // Idee 1: Ergebnis als flaches Array mit den counts als Werte zurückgeben. (Unnötig?)
        // TODO Idee 2: positionEwState ist ja schon die mehrdimensionals Matrix mit den Count-Werten, kann direkt für die Matrix-Berechnung verwendet werden
        //     (Evtl eben mit den Nachbar-Positionen zusammen?)
        final int[][][][] stateCountMatrixArr =
            MatrixRuleEngineService.createStateCountMatrix(engineWorldService, positionRuleState.positionType(), positionEwState);

        // Statt Suche, irgendeine OutputStateList zufällig auswählen.
        // Noch viel mehr mit allen denkbaren OutputStates aufgrund aller möglichen Matrix-Operationen hinzufügen.
        // Fordere Einträge aus sortierter Liste öfter holen: nextInt(nextInt(outputStateList.outputStateList.size()) + 1)
        final int outputStateSelectSize = engineWorldService.rnd.nextInt(this.outputStateList.size());
        final int outputStateSelectSize2 = engineWorldService.rnd.nextInt(outputStateSelectSize + 1);
        final int outputStateSelectPos = engineWorldService.rnd.nextInt(outputStateSelectSize2 + 1);

        // Legt fest, wo der calcedCount-Wert landet.
        final MatrixOutputState selectedOutputState =
                this.outputStateList.get(outputStateSelectPos);
                //outputStateList.outputStateList.peek();

        // Idea: just a hack to prevent the same positionType
        //if (positionRuleState.positionType() == selectedOutputState.positionType)  {
        //    // Blast all to type 0.
        //    return new RuleState(
        //            positionRuleState.positionType(),
        //            0,
        //            0,
        //            0,
        //            positionRuleState.count());
        //}

        //Count Aufgrund des selectedOutputState und der stateCountMatrixArr berechnen.
        //        Matrix-Operation(en) auf stateCountMatrixArr anwenden um einen Ergebnis Wert zu bekommen.
        //        Völlig unklar, wie. (!)
        final int calcedCount = selectedOutputState.calcCount(engineWorldService,
                positionRuleState, stateCountMatrixArr, positionEwState);

        //Die ganze Prüfung weglassen.
        //final int inputResult =
        //        calcCountResult(positionRuleState);
        //
        //final int newInputResult =
        //        calcCountResult(positionRuleState, calcedCount);
        //
        //final int newOutputResult =
        //        calcCountResult(selectedOutputState, calcedCount);
        //
        //if (inputResult == (newInputResult + newOutputResult)) {
        if (calcedCount > 0) {
            retPositionRuleState = new RuleState(
                selectedOutputState.positionType,
                selectedOutputState.typePos,
                selectedOutputState.energyPos,
                selectedOutputState.impulsePos,
                calcedCount);

            //Die Idee mit dem sortieren und dann bevorzugt solche OutputStates zu nehmen,
            //die eher am Anfang stehen, ist nicht schlecht.
            //        Bevorzugt werden OutputStates die insgesammt eine große Anzahl Counts verschieben (Summe über alle Aufrufe)
            //        oder solche die große Count-Blöcke verschieben (Summe durch Anzahl Aufrufe).
            //removeOutputState(engineWorldService, this, selectedOutputState, false);
            //removeOutputState(engineWorldService, this, selectedOutputState, true);

            selectedOutputState.calcCount++;

            // Not really a transfer.
            if ((positionRuleState.positionType() != selectedOutputState.positionType) ||
                (positionRuleState.typePos() != selectedOutputState.typePos) ||
                (positionRuleState.energyPos() != selectedOutputState.energyPos) ||
                (positionRuleState.impulsePos() != selectedOutputState.impulsePos)) {
                selectedOutputState.sumCount += calcedCount;

                // Sort for most transfer per call.
                this.outputStateList.sort((aOutputState, bOutputState) -> (int) ((aOutputState.sumCount / aOutputState.calcCount) - (bOutputState.sumCount / bOutputState.calcCount)));
                // Sort for most transfer over all.
                //this.outputStateList.sort((aOutputState, bOutputState) -> (int) (bOutputState.sumCount - aOutputState.sumCount));
            }
        } else {
        //    //outputStateList.outputStateList.remove(outputStatePos);
        //    removeOutputState(engineWorldService, this, selectedOutputState, false);
            // Nichts braucht auch nicht verschoben werden.
            //retPositionRuleState = null;

            // Blast all to type 0.
            retPositionRuleState = new RuleState(
                    positionRuleState.positionType(),
                    0,
                    0,
                    0,
                    positionRuleState.count());
        }

        return retPositionRuleState;
    }

}
