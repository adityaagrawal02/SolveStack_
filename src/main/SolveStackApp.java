package main;

import javafx.application.Application;
import ui.FxSolveStackApp;

/**
 * SolveStack application entry point.
 *
 * This class contains the {@code main} method that the JVM calls first when
 * the program is run. Its sole job is to hand off control to the JavaFX
 * runtime so that the graphical UI can be initialised properly.
 */
public class SolveStackApp {

    /**
     * Application entry point.
     *
     * {@link Application#launch} starts the JavaFX platform, instantiates
     * {@link FxSolveStackApp} (which extends {@link Application}), and calls
     * its {@link FxSolveStackApp#start(javafx.stage.Stage)} method on the
     * JavaFX Application Thread. Any command-line arguments are forwarded as-is.
     *
     * @param args Command-line arguments (not used by SolveStack).
     */
    public static void main(String[] args) {
        // Hand control to JavaFX; FxSolveStackApp.start() will be called next.
        Application.launch(FxSolveStackApp.class, args);
    }
}
