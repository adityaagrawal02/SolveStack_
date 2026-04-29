package exceptions;

/**
 * Thrown when a user lookup fails because no account with the given
 * username or email exists in the system.
 *
 * <p>Examples of when this is thrown:</p>
 * <ul>
 *   <li>A login attempt with an unknown username.</li>
 *   <li>A profile lookup for a user who has been deleted.</li>
 * </ul>
 *
 * <p>Extends {@link SolveStackException} so that
 * {@link GlobalExceptionHandler} can handle it with a dedicated
 * {@code handleUserNotFound} method.</p>
 */
public class UserNotFoundException extends SolveStackException {

    /**
     * The username or email address that was searched for but not found.
     * Stored so callers can display it in a meaningful error message.
     */
    private final String identifier;

    /**
     * Creates an exception with a default message of
     * {@code "User not found: '<identifier>'"}.
     *
     * @param identifier The username or email that could not be found.
     */
    public UserNotFoundException(String identifier) {
        // Build a readable default message and assign the error code.
        super("User not found: '" + identifier + "'", "USER_NOT_FOUND");
        this.identifier = identifier;
    }

    /**
     * Creates an exception with a custom error message (e.g. "Invalid
     * username or password" for security reasons).
     *
     * @param identifier    The username or email that could not be found.
     * @param customMessage A caller-provided message to display instead of the default.
     */
    public UserNotFoundException(String identifier, String customMessage) {
        super(customMessage, "USER_NOT_FOUND");
        this.identifier = identifier;
    }

    /**
     * Returns the identifier (username or email) that triggered this exception.
     *
     * @return The searched-for username or email.
     */
    public String getIdentifier() {
        return identifier;
    }
}