package exceptions;

/**
 * Base exception for all SolveStack-specific errors.
 */
public class SolveStackException extends RuntimeException {
    public SolveStackException(String message) { super(message); }
    public SolveStackException(String message, Throwable cause) { super(message, cause); }
}
