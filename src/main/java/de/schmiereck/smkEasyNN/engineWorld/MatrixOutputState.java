package de.schmiereck.smkEasyNN.engineWorld;

class MatrixOutputState {
    final EngineWorldService.PositionType positionType;
    final int typePos;
    final int energyPos;
    final int impulsePos;
    final MatrixOperation[][][][] matrixOperationArr;

    long sumCount;
    long calcCount;

    public MatrixOutputState(final EngineWorldService.PositionType outputPositionType, final int outputTypePos,
                             final int outputEnergyPos, final int outputImpulsePos, final MatrixOperation[][][][] matrixOperationArr) {
        this.positionType = outputPositionType;
        this.typePos = outputTypePos;
        this.energyPos = outputEnergyPos;
        this.impulsePos = outputImpulsePos;
        this.sumCount = 1L;
        this.calcCount = 1L;

        this.matrixOperationArr = matrixOperationArr;
    }

    public int calcCount(final EngineWorldService engineWorldService,
                         final RuleEngine.RuleState positionRuleState, int[][][][] stateCountMatrixArr, EwState positionEwState) {
        // TODO Idee: Count Aufgrund des outputState und der stateCountMatrixArr berechnen.
        //        Matrix-Operation(en) auf stateCountMatrixArr anwenden um einen Ergebnis Wert zu bekommen.
        //        Völlig unklar, wie. (!)

        // Do a matrix operation to this.matrixArr and stateCountMatrixArr to get a result value.
        // Iterate through the elements of matrixArr and stateCountMatrixArr
        int result = 0;

        final int count = positionRuleState.count();
        final int maxCount = EngineWorldService.PositionType.values().length * engineWorldService.typeCount * engineWorldService.energyCount * engineWorldService.impulseCount;

        for (final EngineWorldService.PositionType positionType : EngineWorldService.PositionType.values()) {
            for (int inputTypePos = 0; inputTypePos < engineWorldService.typeCount; inputTypePos++) {
                final EwState inputTypeEwState = positionEwState.ewStateArr[inputTypePos];
                for (int inputEnergyPos = 0; inputEnergyPos < engineWorldService.energyCount; inputEnergyPos++) {
                    final EwState inputEnergyEwState = inputTypeEwState.ewStateArr[inputEnergyPos];
                    for (int inputImpulsePos = 0; inputImpulsePos < engineWorldService.impulseCount; inputImpulsePos++) {
                        final EwState inputImpulseEwState = inputEnergyEwState.ewStateArr[inputImpulsePos];
                        final int positionCount = inputImpulseEwState.count;
                        final int stateCount = stateCountMatrixArr[positionType.ordinal()][inputTypePos][inputEnergyPos][inputImpulsePos];
                        final MatrixOperation matrixOperation = this.matrixOperationArr[positionType.ordinal()][inputTypePos][inputEnergyPos][inputImpulsePos];
                        final int matrixValue = matrixOperation.value;

                        result +=
                                switch (matrixOperation.op) {
                                    case Mul -> (positionCount * stateCount * count) * matrixValue;
                                    case Mul1 -> (positionCount *  count) * matrixValue;
                                    case Mul2 -> (stateCount *  count) * matrixValue;
                                    case Mul3 -> (count) * matrixValue;
                                    case Div -> matrixValue > 0 ? (positionCount * stateCount * count) / matrixValue : 0;
                                    //case Div -> matrixValue > 0 ? (positionCount * count) / matrixValue : 0;
                                };
                    }
                }
            }
        }

        //final int retCount = Math.max(0, Math.min(result / maxCount, positionRuleState.count() - 30));
        //final int retCount = Math.min(result / maxCount, positionRuleState.count() / 2);
        //final int retCount = Math.min(result / (maxCount), positionRuleState.count());
        final int retCount = Math.min(result, positionRuleState.count() / 2);
        //final int retCount = Math.min(result, positionRuleState.count());

        //final int retCount = calcCountResult(positionRuleState, 0) / 2;
        //final int retCount = positionRuleState.count() / 2;
        //final int retCount = positionRuleState.count() / 3;
        //final int retCount = positionRuleState.count() / 4;
        //final int retCount = positionRuleState.count() / 8;
        //final int retCount = (positionRuleState.count() * 75) / 100;
        //final int retCount = positionRuleState.count() / 10;
        //final int retCount = positionRuleState.count();
        return retCount;
    }
}
