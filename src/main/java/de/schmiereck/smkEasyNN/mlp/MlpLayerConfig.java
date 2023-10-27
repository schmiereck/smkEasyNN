package de.schmiereck.smkEasyNN.mlp;

public class MlpLayerConfig {
    private final int size;
    private boolean isArray = false;
    private boolean isLimited = false;
    private boolean useBoundings = false;
    private int limitedInputSize;

    private int xArrayCellSize;// = 1;
    private int yArrayCellSize;// = 1;
    private int xArraySize;// = 4;
    private int yArraySize;// = 3;
    private int extraArraySize;// = 0;

    public MlpLayerConfig(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    public boolean getIsArray() {
        return this.isArray;
    }

   public boolean getIsLimited() {
        return this.isLimited;
    }

    public void setIsArray(final boolean isArray) {
        this.isArray = isArray;
    }

    public void setIsArray(final boolean isArray,
                           final int xArrayCellSize, final int yArrayCellSize,
                           final int xArraySize, final int yArraySize,
                           final int extraArraySize) {
        this.isArray = isArray;
        this.xArrayCellSize = xArrayCellSize;
        this.yArrayCellSize = yArrayCellSize;
        this.xArraySize = xArraySize;
        this.yArraySize = yArraySize;
        this.extraArraySize = extraArraySize;
    }

    public void setIsLimited(final int limitedInputSize, final boolean useBoundings) {
        this.isLimited = true;
        this.useBoundings = useBoundings;
        this.limitedInputSize = limitedInputSize;
    }

    public int getXArrayCellSize() {
        return this.xArrayCellSize;
    }

    public int getYArrayCellSize() {
        return this.yArrayCellSize;
    }

    public int getXArraySize() {
        return this.xArraySize;
    }

    public int getYArraySize() {
        return this.yArraySize;
    }

    public int getExtraArraySize() {
        return this.extraArraySize;
    }

    public int getLimitedInputSize() {
        return this.limitedInputSize;
    }

    public boolean getUseBoundings() {
        return this.useBoundings;
    }
}
