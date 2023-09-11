package de.schmiereck.smkEasyNN.mlp;

public class MlpLayerConfig {
    private final int size;

    public MlpLayerConfig(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }
}
