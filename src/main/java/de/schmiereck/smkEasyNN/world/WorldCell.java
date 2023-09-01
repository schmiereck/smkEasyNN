package de.schmiereck.smkEasyNN.world;

public class WorldCell {
    public static enum State {
        /**
         * Identity / Energy
         */
        ri, gi, bi,
        /**
         * State / Energy
         */
        rs, gs, bs
    }
    float[] state = new float[State.values().length];
}
