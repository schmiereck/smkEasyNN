package de.schmiereck.smkEasyNN.genEden;

import de.schmiereck.smkEasyNN.genEden.service.HexGridService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HexGridMain {
    public static void main(String[] args) {
        HexGridService.demoMode = false;
        HexGridApp.main(args);
    }
}
