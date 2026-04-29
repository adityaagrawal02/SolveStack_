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

/**
 * Utility class providing static helpers for navigating between Swing/JavaFX
 * windows, managing session logout, and centering/positioning frames.
 *
 * <p>This class cannot be instantiated (private constructor + exception).
 * All methods are called statically.</p>
 *
 * <p>Note: Most UI code has migrated to JavaFX. This class retains Swing
 * helpers for backward compatibility.</p>
 */
public final class NavigationUtil {

    /** Prevent instantiation — this is a static-only utility class. */
    private NavigationUtil() {
        throw new UnsupportedOperationException("NavigationUtil cannot be instantiated.");
    }

    // =========================================================
    // ROLE-BASED DASHBOARD ROUTING
    // =========================================================

    /**
     * Closes the current window and opens the dashboard appropriate for the
     * given user role.
     *
     * <p>The switch uses the role constants from {@link Constants} to decide
     * which dashboard class to instantiate. If the role is unrecognized, an
     * {@link UnauthorizedAccessException} is thrown, caught, and the user is
     * redirected back to the login screen.</p>
     *
     * <p>The entire operation runs on the Swing Event Dispatch Thread via
     * {@link SwingUtilities#invokeLater(Runnable)} to keep UI operations
     * thread-safe.</p>
     *
     * @param currentFrame The currently visible frame to close before navigating.
     * @param role         The role string returned by the authentication layer.
     */
    public static void routeToDashboard(JFrame currentFrame, String role) {
        SwingUtilities.invokeLater(() -> {
            try {
                closeWindow(currentFrame); // dismiss the current screen first

                // Choose and show the correct dashboard based on role.
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
                // Show an error dialog and fall back to the login screen.
                GlobalExceptionHandler.handleUnauthorized(e, currentFrame);
                new LoginUI().setVisible(true);
            }
        });
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    /**
     * Prompts the user with a YES/NO confirmation dialog. If confirmed, clears
     * the current session and navigates back to the login screen.
     *
     * @param currentFrame The frame from which the logout was triggered.
     */
    public static void logout(JFrame currentFrame) {
        SwingUtilities.invokeLater(() -> {

            // Show a confirmation dialog to prevent accidental logouts.
            int choice = JOptionPane.showConfirmDialog(
                    currentFrame,
                    "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                // Invalidate the singleton session so no cached user data leaks.
                ui.UserSession.getInstance().logout();

                closeWindow(currentFrame);       // dismiss the current dashboard
                new LoginUI().setVisible(true);  // show the login screen
            }
        });
    }

    // =========================================================
    // WINDOW MANAGEMENT
    // =========================================================

    /**
     * Makes a new frame visible, optionally centring it relative to a parent
     * frame. Runs on the Swing EDT.
     *
     * @param newFrame    The frame to display.
     * @param parentFrame If non-null, the new frame is centred over this parent.
     */
    public static void openWindow(JFrame newFrame, JFrame parentFrame) {
        SwingUtilities.invokeLater(() -> {
            if (parentFrame != null) {
                centerRelativeTo(newFrame, parentFrame); // position first
            }
            newFrame.setVisible(true); // then show
        });
    }

    /**
     * Closes the current window and shows a new one. Useful for full-screen
     * navigation where only one window should be visible at a time.
     *
     * @param newFrame     The next window to show.
     * @param currentFrame The window to close.
     */
    public static void navigateTo(JFrame newFrame, JFrame currentFrame) {
        SwingUtilities.invokeLater(() -> {
            closeWindow(currentFrame); // close old window
            newFrame.setVisible(true); // show new window
        });
    }

    /**
     * Disposes (destroys) the given frame, freeing its OS resources.
     * A {@code null} frame is silently ignored.
     *
     * @param frame The frame to close; may be {@code null}.
     */
    public static void closeWindow(JFrame frame) {
        if (frame != null) {
            frame.dispose(); // dispose releases native windowing resources
        }
    }

    /**
     * Centres {@code child} over {@code parent} by computing the top-left
     * corner that places the child at the centre of the parent.
     *
     * <p>If {@code parent} is {@code null}, the child is centred on the screen.</p>
     *
     * @param child  The window to position.
     * @param parent The reference window (may be {@code null}).
     */
    public static void centerRelativeTo(Window child, Window parent) {
        if (parent == null) {
            child.setLocationRelativeTo(null); // centre on screen
            return;
        }
        // Calculate top-left so the centres of both windows align.
        int x = parent.getX() + (parent.getWidth() - child.getWidth()) / 2;
        int y = parent.getY() + (parent.getHeight() - child.getHeight()) / 2;
        child.setLocation(x, y);
    }

    /**
     * Centres a window on the screen (equivalent to
     * {@code frame.setLocationRelativeTo(null)}).
     *
     * @param frame The window to centre.
     */
    public static void centerOnScreen(Window frame) {
        frame.setLocationRelativeTo(null);
    }

    // =========================================================
    // DIALOG HELPERS
    // =========================================================

    /**
     * Shows a modal INFORMATION dialog.
     *
     * @param parent  The parent component (used for dialog positioning).
     * @param message The text to display.
     * @param title   The dialog title.
     */
    public static void showInfo(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a modal ERROR dialog.
     *
     * @param parent  The parent component.
     * @param message The error message to display.
     * @param title   The dialog title.
     */
    public static void showError(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a modal YES/NO confirmation dialog.
     *
     * @param parent  The parent component.
     * @param message The question to ask the user.
     * @param title   The dialog title.
     * @return {@code true} if the user clicked YES.
     */
    public static boolean showConfirmation(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(
                parent,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        // YES_OPTION == 0; compare explicitly for clarity.
        return result == JOptionPane.YES_OPTION;
    }
}