/**File 5: UnauthorizedAccessException.java
Thrown when a user tries to do something their role doesn't permit. */

package exceptions;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
/**
 * Thrown when a user attempts an action they are not authorized to perform.
 * Example: a Developer trying to approve a challenge (Admin-only action).
 */
public class UnauthorizedAccessException extends SolveStackException {

    private final String userRole;
    private final String attemptedAction;

    public UnauthorizedAccessException(String userRole, String attemptedAction) {
        super(
                "Unauthorized: Role '" + userRole + "' cannot perform action: '" + attemptedAction + "'",
                "UNAUTHORIZED_ACCESS"
        );
        this.userRole = userRole;
        this.attemptedAction = attemptedAction;
    }

    public UnauthorizedAccessException(String customMessage) {
        super(customMessage, "UNAUTHORIZED_ACCESS");
        this.userRole = "UNKNOWN";
        this.attemptedAction = "UNKNOWN";
    }

    /**
     * Returns the role of the user who attempted the action.
     */
    public String getUserRole() {
        return userRole;
    }

    /**
     * Returns the action that was attempted without permission.
     */
    public String getAttemptedAction() {
        return attemptedAction;
    }
}