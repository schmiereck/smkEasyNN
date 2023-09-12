package de.schmiereck.smkEasyNN.mlp;

public class MlpLayerConfig {
    private final int size;
    private boolean isArray = false;

    public MlpLayerConfig(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    public boolean getIsArray() {
        return this.isArray;
    }

    public void setIsArray(boolean isArray) {
        this.isArray = isArray;
    }
}
