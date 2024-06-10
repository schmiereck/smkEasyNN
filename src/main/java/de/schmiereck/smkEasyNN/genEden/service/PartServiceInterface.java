package de.schmiereck.smkEasyNN.genEden.service;

import java.util.List;

public interface PartServiceInterface {
    List<Part> initParts();

    /**
     * 0. Part: Out -> Net-Input
     */
    void calcPartInput(final GridNode sourceGridNode, final Part outPart);
    /**
     * 3. Part: Out -> In
     */
    void calcPartOutToIn(final GridNode sourceGridNode, final Part outPart);
    void calcParts();
    void calcBeginNext();

    int retrieveGenerationCount();
    void submitGenerationCount(final int generationCount);
}
