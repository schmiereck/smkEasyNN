package de.schmiereck.smkEasyNN.fieldWorld;

import de.schmiereck.smkEasyNN.abstractWorld.BaseWorldApp;
import de.schmiereck.smkEasyNN.genEden.HexGridApp;

import java.net.URL;

public class FieldWorldApp extends BaseWorldApp {
    @Override
    protected URL getFXMLResource() {
        return getClass().getResource("HexGrid.fxml");
    }

    @Override
    protected Class<?> getPreferencesClass() {
        return FieldWorldApp.class;
    }

    @Override
    protected String getTitle() {
        return "Field World";
    }
}
