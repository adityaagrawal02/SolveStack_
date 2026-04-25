package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ui.AdminDashboardUI;
import ui.CompanyDashboardUI;
import ui.DeveloperDashboardUI;
import ui.EvaluatorDashboardUI;
import ui.LoginUI;
import ui.UserSession;

public final class NavigationUtil {

    private NavigationUtil() {
        throw new UnsupportedOperationException("NavigationUtil cannot be instantiated.");
    }

    public static void routeToDashboard(Object currentView, String role) {
        if (role == null) {
            showError("Unknown role", "Role was null. Redirecting to sign in.");
            new LoginUI().setVisible(true);
            return;
        }

        switch (role.toUpperCase()) {
            case Constants.ROLE_ADMIN -> new AdminDashboardUI().setVisible(true);
            case Constants.ROLE_COMPANY -> new CompanyDashboardUI().setVisible(true);
            case Constants.ROLE_DEVELOPER -> new DeveloperDashboardUI().setVisible(true);
            case Constants.ROLE_EVALUATOR -> new EvaluatorDashboardUI().setVisible(true);
            default -> {
                showError("Unknown role", "Unknown role: " + role);
                new LoginUI().setVisible(true);
            }
        }
    }

    public static void logout(Object currentView) {
        UserSession.getInstance().logout();
        new LoginUI().setVisible(true);
    }

    public static void openWindow(Object newView, Object parentView) {
        if (newView instanceof DeveloperDashboardUI dev) {
            dev.setVisible(true);
        } else if (newView instanceof EvaluatorDashboardUI evaluator) {
            evaluator.setVisible(true);
        } else if (newView instanceof CompanyDashboardUI company) {
            company.setVisible(true);
        } else if (newView instanceof AdminDashboardUI admin) {
            admin.setVisible(true);
        } else if (newView instanceof LoginUI login) {
            login.setVisible(true);
        }
    }

    public static void navigateTo(Object newView, Object currentView) {
        openWindow(newView, currentView);
    }

    public static void closeWindow(Object view) {
        // JavaFX windows are stage-managed by each screen class.
    }

    public static void centerRelativeTo(Object child, Object parent) {
        // Stage centering is handled by JavaFX runtime.
    }

    public static void centerOnScreen(Object frame) {
        // Stage centering is handled by JavaFX runtime.
    }

    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(title);
        alert.setContentText(message);
        return alert.showAndWait().filter(button -> button == ButtonType.OK).isPresent();
    }
}
