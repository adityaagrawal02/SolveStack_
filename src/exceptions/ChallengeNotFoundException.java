package exceptions;

public class ChallengeNotFoundException extends exceptions.SolveStackException {
    public ChallengeNotFoundException(String challengeId) {
        super("models.Challenge not found with ID: " + challengeId);
    }
}