/**File 6: GlobalExceptionHandler.java
        Central handler — catches and logs all exceptions in one place, shows user-friendly Swing dialogs.*/

package exceptions;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Centralized exception handler for SolveStack.
 * In Phase 2 this would map to Spring's @ControllerAdvice / @ExceptionHandler.
 *
 * Responsibilities:
 *  - Log all exceptions with timestamps to the console.
 *  - Show user-friendly JOptionPane dialogs for UI-layer errors.
 *  - Differentiate between known SolveStack errors and unexpected system errors.
 *
 * Usage:
 *  GlobalExceptionHandler.handle(e);               // silent log
 *  GlobalExceptionHandler.handleAndShow(e, frame); // log + show dialog
 */
public class GlobalExceptionHandler {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // -------------------------------------------------------------------------
    // Core Handlers
    // -------------------------------------------------------------------------

    /**
     * Handles any SolveStack-specific exception and prints a formatted error.
     *
     * @param ex The exception to handle.
     * Logs the exception to the console without showing any UI dialog.
     * Use this in service/model layers where no UI is available.
     */
    public static void handle(SolveStackException ex) {
        System.out.println("============================================");
        System.out.println("[SOLVESTACK ERROR] " + ex.getClass().getSimpleName());
        System.out.println("  Message: " + ex.getMessage());
        System.out.println("============================================");
    public static void handle(Exception e) {
        logToConsole(e);
    }

    /**
     * Handles unexpected runtime exceptions.
     * Logs the exception AND shows a user-friendly dialog on screen.
     * Use this in UI layers (LoginUI, DashboardUI, etc.)
     *
     * @param ex The exception to handle.
     * @param e       The exception that occurred.
     * @param parent  The parent Swing component for the dialog (can be null).
     */
    public static void handle(Exception ex) {
        System.out.println("============================================");
        System.out.println("[UNEXPECTED ERROR] " + ex.getClass().getSimpleName());
        System.out.println("  Message: " + ex.getMessage());
        System.out.println("  Please contact the platform administrator.");
        System.out.println("============================================");
    public static void handleAndShow(Exception e, java.awt.Component parent) {
        logToConsole(e);
        showDialog(e, parent);
    }

    // -------------------------------------------------------------------------
    // Specific Exception Shortcuts
    // -------------------------------------------------------------------------

    public static void handleUserNotFound(UserNotFoundException e, java.awt.Component parent) {
        logToConsole(e);
        JOptionPane.showMessageDialog(
                parent,
                "No account found for: \"" + e.getIdentifier() + "\"\nPlease check your username or sign up.",
                "User Not Found",
                JOptionPane.WARNING_MESSAGE
        );
    }

    public static void handleUnauthorized(UnauthorizedAccessException e, java.awt.Component parent) {
        logToConsole(e);
        JOptionPane.showMessageDialog(
                parent,
                "Access Denied.\nYour role ('" + e.getUserRole() + "') is not permitted to: " + e.getAttemptedAction(),
                "Unauthorized Action",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void handleDeadlinePassed(SubmissionDeadlineException e, java.awt.Component parent) {
        logToConsole(e);
        JOptionPane.showMessageDialog(
                parent,
                "Submission Failed!\nThe deadline for \"" + e.getChallengeTitle() + "\" passed on " + e.getDeadline() + ".",
                "Deadline Passed",
                JOptionPane.WARNING_MESSAGE
        );
    }

    public static void handleChallengeNotFound(ChallengeNotFoundException e, java.awt.Component parent) {
        logToConsole(e);
        JOptionPane.showMessageDialog(
                parent,
                "Challenge not found (ID: " + e.getChallengeId() + ").\nIt may have been removed.",
                "Challenge Not Found",
                JOptionPane.WARNING_MESSAGE
        );
    }

    // -------------------------------------------------------------------------
    // Internal Helpers
    // -------------------------------------------------------------------------

    private static void logToConsole(Exception e) {
        String timestamp = LocalDateTime.now().format(FORMATTER);

        System.err.println("╔══════════════════════════════════════════════╗");
        System.err.println("  [SolveStack ERROR] " + timestamp);

        if (e instanceof SolveStackException se) {
            System.err.println("  Code    : " + se.getErrorCode());
        } else {
            System.err.println("  Code    : UNEXPECTED_ERROR");
        }

        System.err.println("  Type    : " + e.getClass().getSimpleName());
        System.err.println("  Message : " + e.getMessage());
        System.err.println("╚══════════════════════════════════════════════╝");

        // Print full stack trace for unexpected errors
        if (!(e instanceof SolveStackException)) {
            e.printStackTrace();
        }
    }

    private static void showDialog(Exception e, java.awt.Component parent) {
        String title;
        String message;
        int dialogType;

        // Show specific friendly messages for known exceptions
        if (e instanceof UserNotFoundException ue) {
            title = "User Not Found";
            message = "No account found for: \"" + ue.getIdentifier() + "\"";
            dialogType = JOptionPane.WARNING_MESSAGE;

        } else if (e instanceof UnauthorizedAccessException ue) {
            title = "Access Denied";
            message = "You don't have permission to perform this action.\n(" + ue.getAttemptedAction() + ")";
            dialogType = JOptionPane.ERROR_MESSAGE;

        } else if (e instanceof SubmissionDeadlineException de) {
            title = "Deadline Passed";
            message = "The submission deadline for \"" + de.getChallengeTitle() + "\" has passed.";
            dialogType = JOptionPane.WARNING_MESSAGE;

        } else if (e instanceof ChallengeNotFoundException ce) {
            title = "Challenge Not Found";
            message = "Challenge (ID: " + ce.getChallengeId() + ") could not be found.";
            dialogType = JOptionPane.WARNING_MESSAGE;

        } else if (e instanceof SolveStackException se) {
            title = "Application Error";
            message = se.getMessage();
            dialogType = JOptionPane.ERROR_MESSAGE;

        } else {
            // Completely unexpected error
            title = "Unexpected Error";
            message = "Something went wrong. Please restart the application.\n\nDetails: " + e.getMessage();
            dialogType = JOptionPane.ERROR_MESSAGE;
        }

        JOptionPane.showMessageDialog(parent, message, title, dialogType);
    }
}