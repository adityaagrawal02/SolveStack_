package ui;

import java.net.URL;
import javafx.scene.Scene;

public final class FxStyles {

    private FxStyles() {
    }

    public static void apply(Scene scene) {
        URL cssUrl = FxStyles.class.getResource("solvestack.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }
}
