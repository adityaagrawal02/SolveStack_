package exceptions;

import java.time.LocalDate;

/**
 * Thrown when a submission attempt is made after the challenge deadline.
 * Example: challenge closed on Dec 31, developer submits on Jan 5.
 */
public class SubmissionDeadlineException extends SolveStackException {

    private final String challengeTitle;
    private final LocalDate deadline;
    private final LocalDate attemptedOn;

    public SubmissionDeadlineException(String challengeTitle, LocalDate deadline) {
        super(
                "Submission deadline has passed for challenge: '" + challengeTitle + "'. " +
                        "Deadline was: " + deadline,
                "SUBMISSION_DEADLINE_PASSED"
        );
        this.challengeTitle = challengeTitle;
        this.deadline = deadline;
        this.attemptedOn = LocalDate.now();
    }

    public SubmissionDeadlineException(String challengeTitle, LocalDate deadline, LocalDate attemptedOn) {
        super(
                "Submission deadline has passed for challenge: '" + challengeTitle + "'. " +
                        "Deadline was: " + deadline + ", attempted on: " + attemptedOn,
                "SUBMISSION_DEADLINE_PASSED"
        );
        this.challengeTitle = challengeTitle;
        this.deadline = deadline;
        this.attemptedOn = attemptedOn;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public LocalDate getAttemptedOn() {
        return attemptedOn;
    }
}