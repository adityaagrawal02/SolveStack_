package exceptions;

/**
 * Thrown when a user cannot be found in the UserRepository.
 * Example: login attempt with an unregistered username.
 */
public class UserNotFoundException extends SolveStackException {

    private final String identifier;

    public UserNotFoundException(String identifier) {
        super("User not found: '" + identifier + "'", "USER_NOT_FOUND");
        this.identifier = identifier;
    }

    public UserNotFoundException(String identifier, String customMessage) {
        super(customMessage, "USER_NOT_FOUND");
        this.identifier = identifier;
    }

    /**
     * Returns the username or email that was searched for.
     */
    public String getIdentifier() {
        return identifier;
    }
}