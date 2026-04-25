package ui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public abstract class FxModalWindow {
    private final String title;
    private final double width;
    private final double height;
    private final Window owner;
    private Stage stage;

    protected FxModalWindow(String title, double width, double height, Object parent) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.owner = resolveOwner(parent);
    }

    protected abstract Parent buildContent();

    public void setVisible(boolean visible) {
        if (!visible) {
            dispose();
            return;
        }

        FxComponents.runFx(() -> {
            if (stage == null) {
                stage = new Stage();
                stage.setTitle(title);
                stage.initModality(Modality.WINDOW_MODAL);
                if (owner != null) {
                    stage.initOwner(owner);
                }
            }

            Scene scene = new Scene(buildContent(), width, height);
            FxStyles.apply(scene);
            stage.setScene(scene);
            stage.show();
            stage.toFront();
        });
    }

    public void dispose() {
        FxComponents.runFx(() -> {
            if (stage != null) {
                stage.close();
            }
        });
    }

    protected Stage getStage() {
        return stage;
    }

    private Window resolveOwner(Object parent) {
        if (parent instanceof Window window) {
            return window;
        }
        return FxSolveStackApp.getPrimaryStage();
    }
}

