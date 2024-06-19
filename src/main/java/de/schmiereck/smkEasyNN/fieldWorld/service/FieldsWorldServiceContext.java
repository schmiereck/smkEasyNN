package de.schmiereck.smkEasyNN.fieldWorld.service;

import de.schmiereck.smkEasyNN.abstractWorld.service.BaseServiceContext;
import de.schmiereck.smkEasyNN.genEden.service.GridNode;
import de.schmiereck.smkEasyNN.genEden.service.Part;
import de.schmiereck.smkEasyNN.genEden.service.PartServiceInterface;

import java.util.List;

public class FieldsWorldServiceContext extends BaseServiceContext {
    private PartServiceInterface partService = new PartServiceInterface() {
        @Override
        public List<Part> initParts() {
            return List.of();
        }

        @Override
        public void calcPartInput(GridNode sourceGridNode, Part outPart) {

        }

        @Override
        public void calcPartOutToIn(GridNode sourceGridNode, Part outPart) {

        }

        @Override
        public void calcParts() {

        }

        @Override
        public void calcBeginNext() {

        }

        @Override
        public int retrieveGenerationCount() {
            return 0;
        }

        @Override
        public void submitGenerationCount(int generationCount) {

        }
    };

    @Override
    public PartServiceInterface getPartService() {
        return this.partService;
    }
}
