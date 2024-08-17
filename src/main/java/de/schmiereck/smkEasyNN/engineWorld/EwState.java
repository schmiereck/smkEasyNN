package de.schmiereck.smkEasyNN.engineWorld;

public class EwState {
    final EwState[] ewStateArr;
    int count;

    public EwState(final int stateCount) {
        this.ewStateArr = new EwState[stateCount];
    }
}
