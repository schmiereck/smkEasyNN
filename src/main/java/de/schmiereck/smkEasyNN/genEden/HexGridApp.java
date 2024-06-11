package de.schmiereck.smkEasyNN.genEden;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

public class HexGridApp extends Application {
    private static final String PREF_USED = "used";
    private static final String PREF_WINDOW_X = "windowX";
    private static final String PREF_WINDOW_Y = "windowY";
    private static final String PREF_WINDOW_WIDTH = "windowWidth";
    private static final String PREF_WINDOW_HEIGHT = "windowHeight";

    private Preferences prefs;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.prefs = Preferences.userNodeForPackage(HexGridApp.class);

        final Parent root = FXMLLoader.load(getClass().getResource("HexGrid.fxml"));
        primaryStage.setTitle("Hex Grid");
        primaryStage.setScene(new Scene(root));

        if (this.prefs.getBoolean(PREF_USED, false)) {
            double windowX = this.prefs.getDouble(PREF_WINDOW_X, 200.0D);
            double windowY = this.prefs.getDouble(PREF_WINDOW_Y, 200.0D);
            final double windowWidth = this.prefs.getDouble(PREF_WINDOW_WIDTH, 800.0D);
            final double windowHeight = this.prefs.getDouble(PREF_WINDOW_HEIGHT, 600.0D);

            // Get the bounds of the visual bounds of the screen
            final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            // Check if the window coordinates are within the screen bounds
            if (windowX < screenBounds.getMinX()) {
                windowX = 0.0D;
            } else if (windowX + windowWidth > screenBounds.getMaxX()) {
                windowX = Math.max(0.0D, screenBounds.getMaxX() - windowWidth);
            }
            if (windowY < screenBounds.getMinY()) {
                windowY = 0.0D;
            } else if (windowY + windowHeight > screenBounds.getMaxY()) {
                windowY = Math.max(0.0D, screenBounds.getMaxY() - windowHeight);
            }

            primaryStage.setX(windowX);
            primaryStage.setY(windowY);
            primaryStage.setWidth(windowWidth);
            primaryStage.setHeight(windowHeight);
        }

        primaryStage.getIcons().add(new Image("file:./icon.png"));

        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            prefs.putBoolean(PREF_USED, true);
            prefs.putDouble(PREF_WINDOW_X, primaryStage.getX());
            prefs.putDouble(PREF_WINDOW_Y, primaryStage.getY());
            prefs.putDouble(PREF_WINDOW_WIDTH, primaryStage.getWidth());
            prefs.putDouble(PREF_WINDOW_HEIGHT, primaryStage.getHeight());
        });
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
