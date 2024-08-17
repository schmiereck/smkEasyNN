package de.schmiereck.smkEasyNN.engineWorld;

import java.util.*;

public class EngineWorldService {

    public enum PositionType {
        R,
        G,
        B
    }

    final int stateMaxCount = 64;
    final int typeCount = 4;
    final int energyCount = 3;
    final int impulseCount = 3;

    final int locationCount;

    int engineLocationPos = 0;
    int engineTypePos = 0;
    int engineEnergyPos = 0;
    int engineImpulsePos = 0;

    final EwState[] locationEwStateArr;

    final Map<PositionType, Map<Integer, Map<Integer, Map<Integer, RuleEngine>>>> ruleEngineMap = new HashMap<>();
    //final Map<RuleEngineKey, RuleEngine> ruleEngineMap = new HashMap<>();

    final Random rnd = new Random();

    public EngineWorldService(int locationCount) {
        this.locationCount = locationCount;

        this.locationEwStateArr = new EwState[this.locationCount];

        for (int locationPos = 0; locationPos < locationCount; locationPos++) {
            final EwState locationEwState = new EwState(typeCount);
            locationEwState.count = locationPos + 1;

            for (int typePos = 0; typePos < typeCount; typePos++) {
                final EwState typeEwState = new EwState(energyCount);
                typeEwState.count = typePos + 1;

                for (int energyPos = 0; energyPos < energyCount; energyPos++) {
                    final EwState energyEwState = new EwState(impulseCount);
                    energyEwState.count = energyPos + 1;

                    for (int impulsePos = 0; impulsePos < impulseCount; impulsePos++) {
                        final EwState impulseEwState = new EwState(impulseCount);

                        impulseEwState.count = 0;

                        energyEwState.ewStateArr[impulsePos] = impulseEwState;
                    }
                    typeEwState.ewStateArr[energyPos] = energyEwState;
                }
                locationEwState.ewStateArr[typePos] = typeEwState;
            }
            this.locationEwStateArr[locationPos] = locationEwState;
        }
    }

    public void runSimulation() {
        System.out.println("START: Simulating...");

        while (true) {
            this.run();

            try {
                Thread.sleep(1);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        final EwState inputEwState =
                this.locationEwStateArr[this.engineLocationPos].ewStateArr[this.engineTypePos].ewStateArr[this.engineEnergyPos].ewStateArr[this.engineImpulsePos];

        if (inputEwState.count > 0) {
            // PositionType: 0:R, 1:G, 2:B
            final PositionType inputPositionType = PositionType.values()[this.engineLocationPos % 3];

            final Map<Integer, Map<Integer, Map<Integer, RuleEngine>>> engineTypeRuleEngineMap1 = this.ruleEngineMap.get(inputPositionType);

            if (Objects.nonNull(engineTypeRuleEngineMap1)) {
                final Map<Integer, Map<Integer, RuleEngine>> engineTypeRuleEngineMap2 = engineTypeRuleEngineMap1.get(this.engineEnergyPos);

                if (Objects.nonNull(engineTypeRuleEngineMap2)) {
                    final Map<Integer, RuleEngine> engineTypeRuleEngineMap3 = engineTypeRuleEngineMap2.get(this.engineImpulsePos);

                    if (Objects.nonNull(engineTypeRuleEngineMap3)) {

                        final RuleEngine ruleEngine = engineTypeRuleEngineMap3.get(this.engineTypePos);

                        if (Objects.nonNull(ruleEngine)) {
                            RuleEngine.RuleState locationRuleState =
                                    new RuleEngine.RuleState(inputPositionType, this.engineTypePos, this.engineEnergyPos, this.engineImpulsePos, inputEwState.count);

                            final RuleEngine.RuleState outputRuleState =
                                    ruleEngine.calc(locationRuleState, this.locationEwStateArr[this.engineLocationPos]);

                            final int nextLocationPos =
                                    calcNextLocationPos(this.engineLocationPos, inputPositionType, outputRuleState.positionType());

                            final EwState outputEwState =
                                    this.locationEwStateArr[nextLocationPos].
                                            ewStateArr[outputRuleState.typePos()].
                                            ewStateArr[outputRuleState.energyPos()].
                                            ewStateArr[outputRuleState.impulsePos()];

                            outputEwState.count += outputRuleState.count();
                            inputEwState.count -= outputRuleState.count();
                        }
                    }
                }
            }
        }

        this.calcNextPos();
    }

    private int calcNextLocationPos(final int actualLocationPos, final PositionType actualPositionType, final PositionType mextPositionType) {
        return
            switch (actualPositionType) {
                case R ->
                        switch (mextPositionType) {
                            case R -> calcCenterLocationPos(actualLocationPos);
                            case G -> calcRightLocationPos(actualLocationPos);
                            case B -> calcLeftLocationPos(actualLocationPos);
                            //default -> throw new IllegalStateException("Unexpected positionTypePos value: %d".formatted(outputRuleState.positionTypePos()));
                        };
                case G ->
                        switch (mextPositionType) {
                            case R -> calcLeftLocationPos(actualLocationPos);
                            case G -> calcCenterLocationPos(actualLocationPos);
                            case B -> calcRightLocationPos(actualLocationPos);
                            //default -> throw new IllegalStateException("Unexpected positionTypePos value: %d".formatted(outputRuleState.positionTypePos()));
                        };
                case B ->
                        switch (mextPositionType) {
                            case R -> calcRightLocationPos(actualLocationPos);
                            case G -> calcLeftLocationPos(actualLocationPos);
                            case B -> calcCenterLocationPos(actualLocationPos);
                            //default -> throw new IllegalStateException("Unexpected positionTypePos value: %d".formatted(outputRuleState.positionTypePos()));
                        };
            };
    }

    private int calcRightLocationPos(int locationPos) {
        return (locationPos + 1) % this.locationCount;
    }

    private int calcLeftLocationPos(final int locationPos) {
        return ((locationPos - 1) + this.locationCount) % this.locationCount;
    }

    private int calcCenterLocationPos(final int locationPos) {
        return locationPos;
    }

    private void calcNextPos() {
        this.engineImpulsePos = rnd.nextInt(this.impulseCount);
        this.engineEnergyPos = rnd.nextInt(this.energyCount);
        this.engineTypePos = rnd.nextInt(this.typeCount);
        this.engineLocationPos = rnd.nextInt(this.locationCount);
    }

    private void calcNextPos2() {
        if (this.engineImpulsePos >= this.impulseCount - 1) {
            this.engineImpulsePos = 0;
            if (this.engineEnergyPos >= this.energyCount - 1) {
                this.engineEnergyPos = 0;
                if (this.engineTypePos >= this.typeCount - 1) {
                    this.engineTypePos = 0;
                    this.engineLocationPos = (this.engineLocationPos + 1) % this.locationCount;
                } else {
                    this.engineTypePos++;
                }
            } else {
                this.engineEnergyPos++;
            }
        } else {
            this.engineImpulsePos++;
        }
    }

    public void addRuleEngine(final RuleEngine ruleEngine) {
        final Map<Integer, Map<Integer, Map<Integer, RuleEngine>>> usedEngineTypeRuleEngineMap;
        {
            final Map<Integer, Map<Integer, Map<Integer, RuleEngine>>> engineTypeRuleEngineMap = this.ruleEngineMap.get(ruleEngine.inputPositionType);

            if (Objects.isNull(engineTypeRuleEngineMap)) {
                usedEngineTypeRuleEngineMap = new HashMap<>();
                this.ruleEngineMap.put(ruleEngine.inputPositionType, usedEngineTypeRuleEngineMap);
            } else {
                usedEngineTypeRuleEngineMap = engineTypeRuleEngineMap;
            }
        }

        final Map<Integer, Map<Integer, RuleEngine>> usedEngineTypeRuleEngineMap2;
        {
            final Map<Integer, Map<Integer, RuleEngine>> engineTypeRuleEngineMap2 = usedEngineTypeRuleEngineMap.get(ruleEngine.inputEnergyPos);

            if (Objects.isNull(engineTypeRuleEngineMap2)) {
                usedEngineTypeRuleEngineMap2 = new HashMap<>();
                usedEngineTypeRuleEngineMap.put(ruleEngine.inputEnergyPos, usedEngineTypeRuleEngineMap2);
            } else {
                usedEngineTypeRuleEngineMap2 = engineTypeRuleEngineMap2;
            }
        }

        final Map<Integer, RuleEngine> usedEngineTypeRuleEngineMap3;
        {
            final Map<Integer, RuleEngine> engineTypeRuleEngineMap3 = usedEngineTypeRuleEngineMap2.get(ruleEngine.inputImpulsePos);

            if (Objects.isNull(engineTypeRuleEngineMap3)) {
                usedEngineTypeRuleEngineMap3 = new HashMap<>();
                usedEngineTypeRuleEngineMap2.put(ruleEngine.inputImpulsePos, usedEngineTypeRuleEngineMap3);
            } else {
                usedEngineTypeRuleEngineMap3 = engineTypeRuleEngineMap3;
            }
        }

        usedEngineTypeRuleEngineMap3.put(ruleEngine.inputTypePos, ruleEngine);
    }
}
