package exceptions;

/**
 * Centralized exception handler for SolveStack.
 * In Phase 2 this would map to Spring's @ControllerAdvice / @ExceptionHandler.
 */
public class GlobalExceptionHandler {

    /**
     * Handles any SolveStack-specific exception and prints a formatted error.
     *
     * @param ex The exception to handle.
     */
    public static void handle(SolveStackException ex) {
        System.out.println("============================================");
        System.out.println("[SOLVESTACK ERROR] " + ex.getClass().getSimpleName());
        System.out.println("  Message: " + ex.getMessage());
        System.out.println("============================================");
    }

    /**
     * Handles unexpected runtime exceptions.
     *
     * @param ex The exception to handle.
     */
    public static void handle(Exception ex) {
        System.out.println("============================================");
        System.out.println("[UNEXPECTED ERROR] " + ex.getClass().getSimpleName());
        System.out.println("  Message: " + ex.getMessage());
        System.out.println("  Please contact the platform administrator.");
        System.out.println("============================================");
    }
}