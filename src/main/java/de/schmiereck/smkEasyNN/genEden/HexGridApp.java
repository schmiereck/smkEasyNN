package de.schmiereck.smkEasyNN.genEden;

import de.schmiereck.smkEasyNN.abstractWorld.BaseWorldApp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;
import java.util.prefs.Preferences;

public class HexGridApp extends BaseWorldApp {

    @Override
    protected String getTitle() {
        return "Hex Grid";
    }

    @Override
    protected URL getFXMLResource() {
        return getClass().getResource("HexGrid.fxml");
    }

    @Override
    protected Class<?> getPreferencesClass() {
        return HexGridApp.class;
    }
}
