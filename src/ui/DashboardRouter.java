package ui;

import javafx.scene.Scene;
import javafx.stage.Stage;

public final class DashboardRouter {

    private DashboardRouter() {
        throw new UnsupportedOperationException("DashboardRouter cannot be instantiated.");
    }

    public static String normalizeRole(String role) {
        if (role == null) {
            return "";
        }

        String normalized = role.trim().toLowerCase();
        if (normalized.startsWith("models.")) {
            normalized = normalized.substring("models.".length());
        }

        return switch (normalized) {
            case "developer", "role_developer" -> "Developer";
            case "evaluator", "role_evaluator" -> "Evaluator";
            case "admin", "role_admin" -> "Admin";
            case "company", "role_company" -> "Company";
            default -> role.trim();
        };
    }

    public static Scene createDashboard(String role, Stage stage) {
        String normalizedRole = normalizeRole(role);
        if (normalizedRole.isBlank()) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        return FxDashboardUI.createScene(stage, normalizedRole);
    }

    public static void openDashboard(Stage stage, String role) {
        Stage targetStage = stage != null ? stage : FxSolveStackApp.getPrimaryStage();
        if (targetStage == null) {
            throw new IllegalStateException("Primary stage is not initialized.");
        }

        Scene dashboard = createDashboard(role, targetStage);
        targetStage.setTitle("SolveStack - " + normalizeRole(role) + " Dashboard");
        targetStage.setScene(dashboard);
        targetStage.show();
    }

    /**
     * Compatibility overload for legacy callsites that still pass Swing frames.
     */
    public static void openDashboard(Object ignoredCurrentFrame, String role) {
        openDashboard(FxSolveStackApp.getPrimaryStage(), role);
    }
}
