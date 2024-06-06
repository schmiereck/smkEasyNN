package de.schmiereck.smkEasyNN.genEden.service;

public class HexGridCalcWorker implements Runnable {
    final WorkInterface workInterface;
    private final HexGridService hexGridService;
    private final int xStartGridPos;
    private final int yStartGridPos;
    private final int xEndGridPos;
    private final int yEndGridPos;

    @FunctionalInterface
    public interface WorkInterface {
        void work(final HexGridService hexGridService, final int xStartGridPos, final int yStartGridPos, final int xEndGridPos, final int yEndGridPos);
    }

    public HexGridCalcWorker(final WorkInterface workInterface, final HexGridService hexGridService, final int xStartGridPos, final int yStartGridPos, final int xEndGridPos, final int yEndGridPos) {
        this.workInterface = workInterface;
        this.hexGridService = hexGridService;
        this.xStartGridPos = xStartGridPos;
        this.yStartGridPos = yStartGridPos;
        this.xEndGridPos = xEndGridPos;
        this.yEndGridPos = yEndGridPos;
    }

    @Override
    public void run() {
        //this.hexGridService.calcGrid(this.xStartGridPos, this.yStartGridPos, this.xEndGridPos, this.yEndGridPos);
        this.workInterface.work(this.hexGridService, this.xStartGridPos, this.yStartGridPos, this.xEndGridPos, this.yEndGridPos);
    }
}
