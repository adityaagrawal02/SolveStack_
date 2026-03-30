package exceptions;

public class SubmissionDeadlineException extends SolveStackException {
    public SubmissionDeadlineException(String challengeId) {
        super("models.Submission deadline has passed for challenge: " + challengeId);
    }
}