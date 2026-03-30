package exceptions;

public class UserNotFoundException extends SolveStackException {
    public UserNotFoundException(String userId) {
        super("User not found with ID: " + userId);
    }
}