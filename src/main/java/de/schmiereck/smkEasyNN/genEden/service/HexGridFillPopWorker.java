package de.schmiereck.smkEasyNN.genEden.service;

import java.util.List;

public class HexGridFillPopWorker implements Runnable {
    final WorkInterface workInterface;
    private final GeneticPartService geneticPartService;
    private final List<Part> partList;
    private final int startCountPos;
    private final int endCountPos;

    @FunctionalInterface
    public interface WorkInterface {
        void work(final GeneticPartService geneticPartService, final List<Part> partList, final int startCountPos, final int endCountPos);
    }

    public HexGridFillPopWorker(final WorkInterface workInterface, final GeneticPartService geneticPartService, final List<Part> partList, final int startCountPos, final int endCountPos) {
        this.workInterface = workInterface;
        this.geneticPartService = geneticPartService;
        this.partList = partList;
        this.startCountPos = startCountPos;
        this.endCountPos = endCountPos;
    }

    @Override
    public void run() {
        this.workInterface.work(this.geneticPartService, this.partList, this.startCountPos, this.endCountPos);
    }
}
