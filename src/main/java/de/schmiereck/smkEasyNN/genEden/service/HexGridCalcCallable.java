package de.schmiereck.smkEasyNN.genEden.service;

import java.util.concurrent.Callable;

public class HexGridCalcCallable implements Callable<Integer> {
    final WorkInterface workInterface;
    private final HexGridService hexGridService;
    private final int xStartGridPos;
    private final int yStartGridPos;
    private final int xEndGridPos;
    private final int yEndGridPos;

    @FunctionalInterface
    public interface WorkInterface {
        Integer work(final HexGridService hexGridService, final int xStartGridPos, final int yStartGridPos, final int xEndGridPos, final int yEndGridPos);
    }

    public HexGridCalcCallable(final WorkInterface workInterface, final HexGridService hexGridService, final int xStartGridPos, final int yStartGridPos, final int xEndGridPos, final int yEndGridPos) {
        this.workInterface = workInterface;
        this.hexGridService = hexGridService;
        this.xStartGridPos = xStartGridPos;
        this.yStartGridPos = yStartGridPos;
        this.xEndGridPos = xEndGridPos;
        this.yEndGridPos = yEndGridPos;
    }

    @Override
    public Integer call() {
        //this.hexGridService.calcGrid(this.xStartGridPos, this.yStartGridPos, this.xEndGridPos, this.yEndGridPos);
        return this.workInterface.work(this.hexGridService, this.xStartGridPos, this.yStartGridPos, this.xEndGridPos, this.yEndGridPos);
    }
}
