package de.schmiereck.smkEasyNN.abstractWorld.view;

public class HexGridViewWorker implements Runnable {
    final HexGridViewWorker.WorkInterface workInterface;
    private final HexGridModel hexGridModel;
    private final int xStartGridPos;
    private final int yStartGridPos;
    private final int xEndGridPos;
    private final int yEndGridPos;

    @FunctionalInterface
    public interface WorkInterface {
        void work(final HexGridModel hexGridModel, final int xStartGridPos, final int yStartGridPos, final int xEndGridPos, final int yEndGridPos);
    }

    public HexGridViewWorker(final HexGridViewWorker.WorkInterface workInterface, final HexGridModel hexGridModel, final int xStartGridPos, final int yStartGridPos, final int xEndGridPos, final int yEndGridPos) {
        this.workInterface = workInterface;
        this.hexGridModel = hexGridModel;
        this.xStartGridPos = xStartGridPos;
        this.yStartGridPos = yStartGridPos;
        this.xEndGridPos = xEndGridPos;
        this.yEndGridPos = yEndGridPos;
    }

    @Override
    public void run() {
        //this.hexGridService.calcGrid(this.xStartGridPos, this.yStartGridPos, this.xEndGridPos, this.yEndGridPos);
        this.workInterface.work(this.hexGridModel, this.xStartGridPos, this.yStartGridPos, this.xEndGridPos, this.yEndGridPos);
    }
}
