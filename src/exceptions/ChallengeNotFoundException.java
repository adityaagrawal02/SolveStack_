package exceptions;

/**
 * Thrown when a challenge cannot be found in the ChallengeService registry.
 * Example: a developer tries to submit to a challenge ID that no longer exists.
 */
public class ChallengeNotFoundException extends SolveStackException {

    private final String challengeId;

    public ChallengeNotFoundException(String challengeId, String customMessage) {
        super(customMessage, "CHALLENGE_NOT_FOUND");
        this.challengeId = challengeId;
    }

    /**
     * Returns the challenge ID that was searched for.
     */
    public String getChallengeId() {
        return challengeId;
    }
}