package exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Centralized exception handling utilities for SolveStack.
 */
public final class GlobalExceptionHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private GlobalExceptionHandler() {
    }

    public static void handle(SolveStackException ex) {
        logToConsole(ex);
    }

    public static void handle(Exception ex) {
        logToConsole(ex);
    }

    public static void handleAndShow(Exception ex, Object parent) {
        logToConsole(ex);
        showDialog(ex, parent);
    }

    public static void handleUserNotFound(UserNotFoundException ex, Object parent) {
        logToConsole(ex);
        showAlert("User Not Found",
                "No account found for: \"" + ex.getIdentifier() + "\"\nPlease check your username or sign up.",
                Alert.AlertType.WARNING);
    }

    public static void handleUnauthorized(UnauthorizedAccessException ex, Object parent) {
        logToConsole(ex);
        showAlert("Unauthorized Action",
                "Access Denied.\nYour role ('" + ex.getUserRole() + "') is not permitted to: " + ex.getAttemptedAction(),
                Alert.AlertType.ERROR);
    }

    public static void handleDeadlinePassed(SubmissionDeadlineException ex, Object parent) {
        logToConsole(ex);
        showAlert("Deadline Passed",
                "Submission failed.\nThe deadline for \"" + ex.getChallengeTitle() + "\" passed on " + ex.getDeadline() + ".",
                Alert.AlertType.WARNING);
    }

    public static void handleChallengeNotFound(ChallengeNotFoundException ex, Object parent) {
        logToConsole(ex);
        showAlert("Challenge Not Found",
                "Challenge not found (ID: " + ex.getChallengeId() + ").\nIt may have been removed.",
                Alert.AlertType.WARNING);
    }

    private static void logToConsole(Exception ex) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String code = (ex instanceof SolveStackException ssEx) ? ssEx.getErrorCode() : "UNEXPECTED_ERROR";

        System.err.println("============================================");
        System.err.println("[SolveStack ERROR] " + timestamp);
        System.err.println("Code    : " + code);
        System.err.println("Type    : " + ex.getClass().getSimpleName());
        System.err.println("Message : " + ex.getMessage());
        System.err.println("============================================");

        if (!(ex instanceof SolveStackException)) {
            ex.printStackTrace();
        }
    }

    private static void showDialog(Exception ex, Object parent) {
        String title;
        String message;
        Alert.AlertType dialogType;

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
            title = "Application Error";
            message = solveStackException.getMessage();
            dialogType = Alert.AlertType.ERROR;
        } else {
            title = "Unexpected Error";
            message = "Something went wrong. Please restart the application.\n\nDetails: " + ex.getMessage();
            dialogType = Alert.AlertType.ERROR;
        }

        showAlert(title, message, dialogType);
    }

    private static void showAlert(String title, String message, Alert.AlertType type) {
        Runnable display = () -> {
            Alert alert = new Alert(type, message, ButtonType.OK);
            alert.setHeaderText(title);
            alert.showAndWait();
        };

        if (Platform.isFxApplicationThread()) {
            display.run();
        } else {
            Platform.runLater(display);
        }
    }
}