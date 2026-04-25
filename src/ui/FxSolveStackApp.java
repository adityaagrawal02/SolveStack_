package ui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX entrypoint and shared stage owner for scene-based navigation.
 */
public class FxSolveStackApp extends Application {
    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setMinWidth(1080);
        stage.setMinHeight(760);
        stage.setWidth(Theme.WINDOW_WIDTH);
        stage.setHeight(Theme.WINDOW_HEIGHT);
        stage.setTitle("SolveStack - Sign In");
        new LoginUI().show(stage);
    }
}
