package de.schmiereck.smkEasyNN.engineWorld;

import java.util.*;

public class EngineWorldService {
    final int stateMaxCount = 64;
    final int locationCount = 8;
    final int typeCount = 4;
    final int energyCount = 3;
    final int impulseCount = 3;

    EwState[] locationEwStateArr = new EwState[locationCount];

    final Map<Integer, RuleEngine> ruleEngineMap = new HashMap<>();

    public void init() {
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

    int enginLocationPos = 0;
    int enginTypePos = 0;
    int enginEnergyPos = 0;
    int enginImpulsePos = 0;

    public void run() {
        final EwState inputEwState =
                this.locationEwStateArr[this.enginLocationPos].ewStateArr[this.enginTypePos].ewStateArr[this.enginEnergyPos].ewStateArr[this.enginImpulsePos];

        if (inputEwState.count > 0) {
            final RuleEngine ruleEngine = this.ruleEngineMap.get(enginTypePos);
            if (Objects.nonNull(ruleEngine)) {
                // R, G, B
                final int locationTypePos = this.enginLocationPos % 3;

                RuleEngine.RuleState locationRuleState =
                        new RuleEngine.RuleState(locationTypePos, enginTypePos, enginEnergyPos, enginImpulsePos, inputEwState.count);

                final RuleEngine.RuleState outputRuleState =
                        ruleEngine.calc(locationRuleState, this.locationEwStateArr[enginLocationPos]);

                final int locationPos =
                switch (outputRuleState.positionTypePos()) {
                    case 0 -> ((this.enginLocationPos - 1) + this.locationCount) % this.locationCount;
                    case 1 -> this.enginLocationPos;
                    case 2 -> (this.enginLocationPos + 1) % this.locationCount;
                    default -> throw new IllegalStateException("Unexpected positionTypePos value: %d".formatted(outputRuleState.positionTypePos()));
                };

                final EwState outputEwState =
                        this.locationEwStateArr[locationPos].
                        ewStateArr[outputRuleState.typePos()].
                        ewStateArr[outputRuleState.energyPos()].
                        ewStateArr[outputRuleState.impulsePos()];

                outputEwState.count += outputRuleState.count();
                inputEwState.count -= outputRuleState.count();
            }
        }

        if (this.enginImpulsePos >= this.impulseCount - 1) {
            this.enginImpulsePos = 0;
            if (this.enginEnergyPos >= this.energyCount - 1) {
                this.enginEnergyPos = 0;
                if (this.enginTypePos >= this.typeCount - 1) {
                    this.enginTypePos = 0;
                    this.enginLocationPos = (this.enginLocationPos + 1) % this.locationCount;
                } else {
                    this.enginTypePos++;
                }
            } else {
                this.enginEnergyPos++;
            }
        } else {
            this.enginImpulsePos++;
        }
    }

    public void addRuleEngine(final RuleEngine ruleEngine) {
        this.ruleEngineMap.put(ruleEngine.inputTypePos, ruleEngine);
    }
}
