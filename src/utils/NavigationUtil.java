package utils;

import ui.LoginUI;
import ui.AdminDashboardUI;
import ui.CompanyDashboardUI;
import ui.DeveloperDashboardUI;
import ui.EvaluatorDashboardUI;

import exceptions.UnauthorizedAccessException;
import exceptions.GlobalExceptionHandler;

import javax.swing.*;
import java.awt.*;

public final class NavigationUtil {

    // Prevent instantiation
    private NavigationUtil() {
        throw new UnsupportedOperationException("NavigationUtil cannot be instantiated.");
    }

    // =========================================================
    // ROLE-BASED DASHBOARD ROUTING
    // =========================================================

    public static void routeToDashboard(JFrame currentFrame, String role) {
        SwingUtilities.invokeLater(() -> {
            try {
                closeWindow(currentFrame);

                switch (role.toUpperCase()) {
                    case Constants.ROLE_ADMIN ->
                            new AdminDashboardUI().setVisible(true);

                    case Constants.ROLE_COMPANY ->
                            new CompanyDashboardUI().setVisible(true);

                    case Constants.ROLE_DEVELOPER ->
                            new DeveloperDashboardUI().setVisible(true);

                    case Constants.ROLE_EVALUATOR ->
                            new EvaluatorDashboardUI().setVisible(true);

                    default -> throw new UnauthorizedAccessException(
                            role, "access any dashboard (unknown role: " + role + ")"
                    );
                }

            } catch (UnauthorizedAccessException e) {
                GlobalExceptionHandler.handleUnauthorized(e, currentFrame);
                new LoginUI().setVisible(true);
            }
        });
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    public static void logout(JFrame currentFrame) {
        SwingUtilities.invokeLater(() -> {

            int choice = JOptionPane.showConfirmDialog(
                    currentFrame,
                    "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                // Clear session (correct method)
                ui.UserSession.getInstance().logout();

                closeWindow(currentFrame);
                new LoginUI().setVisible(true);
            }
        });
    }

    // =========================================================
    // WINDOW MANAGEMENT
    // =========================================================

    public static void openWindow(JFrame newFrame, JFrame parentFrame) {
        SwingUtilities.invokeLater(() -> {
            if (parentFrame != null) {
                centerRelativeTo(newFrame, parentFrame);
            }
            newFrame.setVisible(true);
        });
    }

    public static void navigateTo(JFrame newFrame, JFrame currentFrame) {
        SwingUtilities.invokeLater(() -> {
            closeWindow(currentFrame);
            newFrame.setVisible(true);
        });
    }

    public static void closeWindow(JFrame frame) {
        if (frame != null) {
            frame.dispose();
        }
    }

    public static void centerRelativeTo(Window child, Window parent) {
        if (parent == null) {
            child.setLocationRelativeTo(null);
            return;
        }
        int x = parent.getX() + (parent.getWidth() - child.getWidth()) / 2;
        int y = parent.getY() + (parent.getHeight() - child.getHeight()) / 2;
        child.setLocation(x, y);
    }

    public static void centerOnScreen(Window frame) {
        frame.setLocationRelativeTo(null);
    }

    // =========================================================
    // DIALOG HELPERS
    // =========================================================

    public static void showInfo(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean showConfirmation(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(
                parent,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
}