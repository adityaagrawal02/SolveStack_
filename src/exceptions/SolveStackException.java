package exceptions;

/**
 * Base exception class for all SolveStack-specific exceptions.
 * All custom exceptions in this project should extend this class.
 */
public class SolveStackException extends RuntimeException {

    private final String errorCode;

    public SolveStackException(String message) {
        super(message);
        this.errorCode = "SOLVESTACK_ERROR";
    }

    public SolveStackException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public SolveStackException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "SOLVESTACK_ERROR";
    }

    public SolveStackException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "[" + errorCode + "] " + getMessage();
    }
}