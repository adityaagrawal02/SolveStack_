package utils;

/**
 * ============================================================
 *  NavigationUtil.java — SolveStack Screen Navigation Utility
 * ============================================================
 *
 *  PURPOSE:
 *  Centralizes all screen-switching logic for the SolveStack
 *  Swing GUI. Instead of each UI class directly instantiating
 *  other UI classes (tight coupling), they call NavigationUtil
 *  methods to navigate, keeping UI files clean and decoupled.
 *
 *  WHY THIS EXISTS:
 *  Without this utility, every dashboard, login screen, and
 *  dialog would need to know about every other screen. This
 *  creates a tangled web of dependencies. NavigationUtil acts
 *  as a central "router" — similar to a Router in web frameworks.
 *
 *  KEY RESPONSIBILITIES:
 *  1. Route users to the correct dashboard based on their role.
 *  2. Handle window closing and opening transitions cleanly.
 *  3. Provide a logout method that clears session and returns
 *     the user to the LoginUI.
 *  4. Open secondary windows (dialogs, sub-screens) safely.
 *  5. Handle unauthorized role access via GlobalExceptionHandler.
 *
 *  USAGE EXAMPLE:
 *  // After login, route to the right dashboard:
 *  NavigationUtil.routeToDashboard(currentFrame, "DEVELOPER");
 *
 *  // From any screen, log out:
 *  NavigationUtil.logout(currentFrame);
 *
 *  // Open a sub-screen without closing the parent:
 *  NavigationUtil.openWindow(new SubmitSolutionUI(), parentFrame);
 * ============================================================
 */

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

    // Prevent instantiation — this is a pure static utility class
    private NavigationUtil() {
        throw new UnsupportedOperationException("NavigationUtil cannot be instantiated.");
    }

    // =========================================================
    // ROLE-BASED DASHBOARD ROUTING
    // =========================================================

    /**
     * Routes the logged-in user to their correct dashboard
     * based on their role string.
     *
     * Closes the current window before opening the new dashboard
     * to avoid stacking multiple frames in memory.
     *
     * If the role is unrecognized, throws UnauthorizedAccessException
     * which is caught and displayed via GlobalExceptionHandler.
     *
     * @param currentFrame  The currently open JFrame to close.
     * @param role          The role string from user.getRole().
     */
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
                // Log + show dialog, then fall back to login screen
                GlobalExceptionHandler.handleUnauthorized(e, currentFrame);
                new LoginUI().setVisible(true);
            }
        });
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    /**
     * Logs the current user out of their session.
     *
     * Steps:
     *  1. Shows a confirmation dialog to prevent accidental logout.
     *  2. Clears the global UserSession via getInstance().clearSession().
     *  3. Closes the current dashboard window.
     *  4. Opens a fresh LoginUI for re-authentication.
     *
     * @param currentFrame  The dashboard frame to close on logout.
     */
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
                // Clear the active session using current implementation pattern
                ui.UserSession.getInstance().clearSession();

                closeWindow(currentFrame);
                new LoginUI().setVisible(true);
            }
        });
    }

    // =========================================================
    // WINDOW MANAGEMENT HELPERS
    // =========================================================

    /**
     * Opens a new JFrame window while keeping the parent open.
     * Centers the new window relative to the parent if provided.
     * Use this for sub-screens like SubmitSolutionUI or EvaluationUI.
     *
     * @param newFrame     The new JFrame to open.
     * @param parentFrame  The parent frame for positioning. Can be null.
     */
    public static void openWindow(JFrame newFrame, JFrame parentFrame) {
        SwingUtilities.invokeLater(() -> {
            if (parentFrame != null) {
                centerRelativeTo(newFrame, parentFrame);
            }
            newFrame.setVisible(true);
        });
    }

    /**
     * Opens a new JFrame and closes the current one.
     * Use this for full screen transitions (e.g., Login → Dashboard).
     *
     * @param newFrame      The new JFrame to open.
     * @param currentFrame  The current JFrame to close.
     */
    public static void navigateTo(JFrame newFrame, JFrame currentFrame) {
        SwingUtilities.invokeLater(() -> {
            closeWindow(currentFrame);
            newFrame.setVisible(true);
        });
    }

    /**
     * Safely disposes a JFrame if it is not null.
     *
     * @param frame  The frame to close/dispose.
     */
    public static void closeWindow(JFrame frame) {
        if (frame != null) {
            frame.dispose();
        }
    }

    /**
     * Centers a child window relative to its parent window.
     * Falls back to centering on screen if parent is null.
     *
     * @param child   The window to center.
     * @param parent  The reference window to center relative to.
     */
    public static void centerRelativeTo(Window child, Window parent) {
        if (parent == null) {
            child.setLocationRelativeTo(null);
            return;
        }
        int x = parent.getX() + (parent.getWidth() - child.getWidth()) / 2;
        int y = parent.getY() + (parent.getHeight() - child.getHeight()) / 2;
        child.setLocation(x, y);
    }

    /**
     * Centers a window directly on the screen with no parent reference.
     *
     * @param frame  The frame to center on screen.
     */
    public static void centerOnScreen(Window frame) {
        frame.setLocationRelativeTo(null);
    }

    // =========================================================
    // DIALOG HELPERS
    // =========================================================

    /**
     * Shows a simple informational message dialog.
     *
     * @param parent   Parent component for the dialog.
     * @param message  Message to display.
     * @param title    Title of the dialog window.
     */
    public static void showInfo(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows an error dialog with a red error icon.
     *
     * @param parent   Parent component for the dialog.
     * @param message  Error message to display.
     * @param title    Title of the dialog window.
     */
    public static void showError(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a YES/NO confirmation dialog.
     * Returns true only if the user explicitly clicked YES.
     *
     * @param parent   Parent component for the dialog.
     * @param message  Confirmation question to display.
     * @param title    Title of the dialog window.
     * @return         true if user confirmed, false otherwise.
     */
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