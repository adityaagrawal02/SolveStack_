package exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Centralized exception handling utilities for SolveStack.
 *
 * <p>This {@code final} class cannot be instantiated. All methods are static
 * and fall into two categories:</p>
 * <ul>
 *   <li><b>Specific handlers</b> — typed methods for each custom exception
 *       (e.g. {@link #handleUserNotFound}) that log the error and show a
 *       role-appropriate dialog message.</li>
 *   <li><b>Generic handlers</b> — {@link #handle} and {@link #handleAndShow}
 *       for cases where the exact exception type is not known at the call
 *       site.</li>
 * </ul>
 *
 * <p>All UI dialogs are shown on the JavaFX Application Thread, regardless of
 * which thread calls the handler.</p>
 */
public final class GlobalExceptionHandler {

    /** Timestamp format used when printing errors to the console. */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Prevent instantiation — this is a static utility class only. */
    private GlobalExceptionHandler() {
    }

    // ── Generic handlers ─────────────────────────────────────────────────────

    /**
     * Logs a {@link SolveStackException} to the console (no dialog shown).
     * Use this when the error is purely informational or expected.
     *
     * @param ex The SolveStack-specific exception to log.
     */
    public static void handle(SolveStackException ex) {
        logToConsole(ex); // write error details to System.err
    }

    /**
     * Logs any generic {@link Exception} to the console.
     *
     * @param ex The exception to log.
     */
    public static void handle(Exception ex) {
        logToConsole(ex);
    }

    /**
     * Logs the exception AND shows a dialog to the user.
     * Dispatches to a specific dialog template based on exception type.
     *
     * @param ex     The exception to handle.
     * @param parent Ignored (legacy parameter kept for API compatibility).
     */
    public static void handleAndShow(Exception ex, Object parent) {
        logToConsole(ex);   // always log first
        showDialog(ex, parent); // then show the appropriate dialog
    }

    // ── Specific typed handlers ───────────────────────────────────────────────

    /**
     * Handles a {@link UserNotFoundException}: logs it and shows a WARNING
     * dialog identifying which username/email was not found.
     *
     * @param ex     The user-not-found exception.
     * @param parent Ignored (legacy API compatibility).
     */
    public static void handleUserNotFound(UserNotFoundException ex, Object parent) {
        logToConsole(ex);
        showAlert("User Not Found",
                "No account found for: \"" + ex.getIdentifier() + "\"\nPlease check your username or sign up.",
                Alert.AlertType.WARNING); // WARNING because it may be a user typo
    }

    /**
     * Handles an {@link UnauthorizedAccessException}: logs it and shows an
     * ERROR dialog explaining which action was blocked and why.
     *
     * @param ex     The unauthorized-access exception.
     * @param parent Ignored (legacy API compatibility).
     */
    public static void handleUnauthorized(UnauthorizedAccessException ex, Object parent) {
        logToConsole(ex);
        showAlert("Unauthorized Action",
                "Access Denied.\nYour role ('" + ex.getUserRole() + "') is not permitted to: " + ex.getAttemptedAction(),
                Alert.AlertType.ERROR); // ERROR because it is a permission violation
    }

    /**
     * Handles a {@link SubmissionDeadlineException}: logs it and shows a
     * WARNING dialog with the challenge name and its deadline date.
     *
     * @param ex     The deadline-passed exception.
     * @param parent Ignored (legacy API compatibility).
     */
    public static void handleDeadlinePassed(SubmissionDeadlineException ex, Object parent) {
        logToConsole(ex);
        showAlert("Deadline Passed",
                "Submission failed.\nThe deadline for \"" + ex.getChallengeTitle() + "\" passed on " + ex.getDeadline() + ".",
                Alert.AlertType.WARNING);
    }

    /**
     * Handles a {@link ChallengeNotFoundException}: logs it and shows a
     * WARNING dialog with the missing challenge's ID.
     *
     * @param ex     The challenge-not-found exception.
     * @param parent Ignored (legacy API compatibility).
     */
    public static void handleChallengeNotFound(ChallengeNotFoundException ex, Object parent) {
        logToConsole(ex);
        showAlert("Challenge Not Found",
                "Challenge not found (ID: " + ex.getChallengeId() + ").\nIt may have been removed.",
                Alert.AlertType.WARNING);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Writes a formatted error block to {@code System.err}.
     *
     * <p>Includes a timestamp, the SolveStack error code (if applicable), the
     * simple class name, and the exception message. For non-SolveStack
     * exceptions the full stack trace is also printed.</p>
     *
     * @param ex The exception to log.
     */
    private static void logToConsole(Exception ex) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        // Use the error code if this is a SolveStack exception, otherwise "UNEXPECTED_ERROR".
        String code = (ex instanceof SolveStackException ssEx) ? ssEx.getErrorCode() : "UNEXPECTED_ERROR";

        System.err.println("============================================");
        System.err.println("[SolveStack ERROR] " + timestamp);
        System.err.println("Code    : " + code);
        System.err.println("Type    : " + ex.getClass().getSimpleName());
        System.err.println("Message : " + ex.getMessage());
        System.err.println("============================================");

        // Only print stack trace for unexpected (non-SolveStack) exceptions,
        // since SolveStack exceptions are expected and intentional.
        if (!(ex instanceof SolveStackException)) {
            ex.printStackTrace();
        }
    }

    /**
     * Selects the appropriate dialog title, message, and type based on the
     * concrete exception type using pattern-matching instanceof, then shows the
     * dialog.
     *
     * @param ex     The exception to display.
     * @param parent Ignored (legacy parameter).
     */
    private static void showDialog(Exception ex, Object parent) {
        String title;
        String message;
        Alert.AlertType dialogType;

        // Pattern-match to provide context-specific dialog text.
        if (ex instanceof UserNotFoundException userNotFoundException) {
            title = "User Not Found";
            message = "No account found for: \"" + userNotFoundException.getIdentifier() + "\"";
            dialogType = Alert.AlertType.WARNING;
        } else if (ex instanceof UnauthorizedAccessException unauthorizedAccessException) {
            title = "Access Denied";
            message = "You do not have permission to perform this action.\n(" + unauthorizedAccessException.getAttemptedAction() + ")";
            dialogType = Alert.AlertType.ERROR;
        } else if (ex instanceof SubmissionDeadlineException submissionDeadlineException) {
            title = "Deadline Passed";
            message = "The submission deadline for \"" + submissionDeadlineException.getChallengeTitle() + "\" has passed.";
            dialogType = Alert.AlertType.WARNING;
        } else if (ex instanceof ChallengeNotFoundException challengeNotFoundException) {
            title = "Challenge Not Found";
            message = "Challenge (ID: " + challengeNotFoundException.getChallengeId() + ") could not be found.";
            dialogType = Alert.AlertType.WARNING;
        } else if (ex instanceof SolveStackException solveStackException) {
            // Generic SolveStack error — show the exception's own message.
            title = "Application Error";
            message = solveStackException.getMessage();
            dialogType = Alert.AlertType.ERROR;
        } else {
            // Truly unexpected error — ask the user to restart.
            title = "Unexpected Error";
            message = "Something went wrong. Please restart the application.\n\nDetails: " + ex.getMessage();
            dialogType = Alert.AlertType.ERROR;
        }

        showAlert(title, message, dialogType);
    }

    /**
     * Shows a JavaFX {@link Alert} dialog with the given parameters.
     *
     * <p>JavaFX UI operations must run on the Application Thread. This method
     * uses {@link Platform#isFxApplicationThread()} to decide whether to run
     * the dialog immediately or schedule it with {@link Platform#runLater}.</p>
     *
     * @param title   The dialog header text.
     * @param message The dialog body text.
     * @param type    The alert type (INFO, WARNING, ERROR, etc.).
     */
    private static void showAlert(String title, String message, Alert.AlertType type) {
        // Wrap dialog creation in a Runnable so it can be dispatched to the FX thread.
        Runnable display = () -> {
            Alert alert = new Alert(type, message, ButtonType.OK);
            alert.setHeaderText(title);
            alert.showAndWait(); // block until the user clicks OK
        };

        // Run immediately if already on the FX thread; otherwise schedule it.
        if (Platform.isFxApplicationThread()) {
            display.run();
        } else {
            Platform.runLater(display);
        }
    }
}