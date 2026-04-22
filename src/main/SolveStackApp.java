package main;

import ui.LoginUI;
import javax.swing.*;

public class SolveStackApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
                System.err.println("Unable to apply system look and feel: " + e.getMessage());
            }
            new LoginUI().setVisible(true);
        });
    }
}
