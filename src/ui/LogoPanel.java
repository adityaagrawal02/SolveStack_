package ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class LogoPanel extends VBox {

    public LogoPanel() {
        this(false, 24);
    }

    public LogoPanel(boolean iconOnly) {
        this(iconOnly, 24);
    }

    public LogoPanel(boolean iconOnly, double fontSize) {
        setAlignment(Pos.CENTER);
        setSpacing(16);

        javafx.scene.image.ImageView logoView = null;
        try {
            javafx.scene.image.Image logoImage = new javafx.scene.image.Image(getClass().getResourceAsStream("assets/logo.png"));
            if (logoImage != null && !logoImage.isError()) {
                logoView = new javafx.scene.image.ImageView(logoImage);
                logoView.setFitHeight(fontSize * 2.2); // Increased from 1.5
                logoView.setPreserveRatio(true);
                logoView.setSmooth(true);
                logoView.setCache(true);
            }
        } catch (Exception e) {
            // Fallback to CSS logo
        }

        if (logoView != null) {
            getChildren().add(logoView);
        } else {
            StackPane cube = new StackPane();
            cube.getStyleClass().add("logo-cube");
            cube.setMinSize(fontSize, fontSize);
            cube.setPrefSize(fontSize, fontSize);
            getChildren().add(cube);
        }

        if (iconOnly) return;

        Label solve = new Label("Solve");
        solve.getStyleClass().add("logo-solve");
        solve.setStyle("-fx-font-size: " + fontSize + "px;");

        Label stack = new Label("Stack");
        stack.getStyleClass().add("logo-stack");
        stack.setStyle("-fx-font-size: " + fontSize + "px;");

        HBox word = new HBox(0, solve, stack);
        word.setAlignment(Pos.CENTER);

        getChildren().add(word);
    }
}
