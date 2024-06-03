package de.schmiereck.smkEasyNN.genEden;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HexGridApp extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final Parent root = FXMLLoader.load(getClass().getResource("HexGrid.fxml"));
        primaryStage.setTitle("Hex Grid");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
