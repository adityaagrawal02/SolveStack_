package models;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  SolveStack – Open Innovation Collaboration Platform
 *  File   : models.Evaluator.java
 *  Package: models
 *  Role   : Expert user who reviews and scores submissions.
 *
 *  OOP Principles Applied:
 *  ─────────────────────────────────────────────────────────
 *   Inheritance    – Extends models.User.
 *   Interface      – Implements models.Evaluable (evaluation contract).
 *   Polymorphism   – Overrides getRole() and dashboard.
 *   Abstraction    – Implements all abstract models.User methods.
 * ============================================================
 */
public class Evaluator extends User implements Evaluable {

    // ─────────────────────────────────────────────────────────
    //  EVALUATOR-SPECIFIC PRIVATE FIELDS
    // ─────────────────────────────────────────────────────────

    private String       expertise;          // Domain of expertise
    private int          evaluationsCompleted;
    private final List<String> assignedChallengeIds;

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Creates an models.Evaluator user.
     *
     * @param userId    Unique platform identifier.
     * @param username  Display name.
     * @param email     Email address.
     * @param password  Plain-text password.
     * @param expertise Area of domain expertise (e.g., "AI/ML", "Cybersecurity").
     */
    public Evaluator(String userId,
                     String username,
                     String email,
                     String password,
                     String expertise) {
        super(userId, username, email, password);
        this.expertise              = (expertise != null) ? expertise : "General";
        this.evaluationsCompleted   = 0;
        this.assignedChallengeIds   = new ArrayList<>();
    }

    // ─────────────────────────────────────────────────────────
    //  ABSTRACT METHOD IMPLEMENTATIONS
    // ─────────────────────────────────────────────────────────

    @Override
    public String getRole() { return "models.Evaluator"; }

    @Override
    protected void displayRoleDashboard() {
        System.out.println("  Expertise          : " + expertise);
        System.out.println("  Evaluations Done   : " + evaluationsCompleted);
        System.out.println("  Assigned Challenges: " + assignedChallengeIds.size());
        System.out.println("--------------------------------------------");
        System.out.println("  Quick Actions:");
        System.out.println("    1) evaluateSubmission(submission, score, feedback)");
        System.out.println("    2) giveFeedback(submission, feedback)");
        System.out.println("    3) assignScore(submission, score)");
        System.out.println("============================================");
    }

    @Override
    public void onAccountRemoved() {
        System.out.println("[ACCOUNT REMOVED] models.Evaluator '" + getUsername()
                + "' removed from platform. "
                + assignedChallengeIds.size() + " challenge(s) need reassignment.");
        assignedChallengeIds.clear();
    }

    // ─────────────────────────────────────────────────────────
    //  EVALUABLE INTERFACE IMPLEMENTATIONS
    // ─────────────────────────────────────────────────────────

    /**
     * Fully evaluates a submission: sets score, feedback, and marks it
     * as ACCEPTED (score ≥ 50) or REJECTED (score < 50).
     */
    @Override
    public void evaluateSubmission(Submission submission, double score, String feedback) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] models.Evaluator must be logged in.");
            return;
        }
        if (submission == null) {
            System.out.println("[ERROR] models.Submission cannot be null.");
            return;
        }
        if (score < 0 || score > 100) {
            System.out.println("[ERROR] Score must be between 0 and 100.");
            return;
        }

        submission.updateStatus(Submission.Status.UNDER_REVIEW);
        submission.calculateScore(score, feedback);

        Submission.Status result = (score >= 50.0)
                ? Submission.Status.ACCEPTED
                : Submission.Status.REJECTED;
        submission.updateStatus(result);

        evaluationsCompleted++;
        System.out.println("[EVALUATION COMPLETE] " + submission.getSubmissionId()
                + " → " + result + " (Score: " + score + ")");
    }

    /**
     * Provides written feedback without altering the score or status.
     */
    @Override
    public void giveFeedback(Submission submission, String feedback) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] models.Evaluator must be logged in.");
            return;
        }
        if (submission == null || feedback == null || feedback.isBlank()) {
            System.out.println("[ERROR] models.Submission and feedback cannot be null/blank.");
            return;
        }
        submission.calculateScore(submission.getScore(), feedback);
        System.out.println("[FEEDBACK] Written feedback provided for: "
                + submission.getSubmissionId());
    }

    /**
     * Assigns a score without changing the submission's status text.
     */
    @Override
    public void assignScore(Submission submission, double score) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] models.Evaluator must be logged in.");
            return;
        }
        if (submission == null) {
            System.out.println("[ERROR] models.Submission cannot be null.");
            return;
        }
        submission.calculateScore(score, submission.getEvaluatorFeedback());
        System.out.println("[SCORE ASSIGNED] " + submission.getSubmissionId()
                + " → " + score + "/100");
    }

    // ─────────────────────────────────────────────────────────
    //  ADDITIONAL METHODS
    // ─────────────────────────────────────────────────────────

    /**
     * Assigns this evaluator to a challenge (called by models.Admin/ChallengeService).
     *
     * @param challengeId ID of the challenge to be assigned.
     */
    public void assignToChallenge(String challengeId) {
        if (!assignedChallengeIds.contains(challengeId)) {
            assignedChallengeIds.add(challengeId);
            System.out.println("[EVALUATOR] " + getUsername()
                    + " assigned to challenge: " + challengeId);
        }
    }

    // ─────────────────────────────────────────────────────────
    //  GETTERS
    // ─────────────────────────────────────────────────────────

    public String       getExpertise()            { return expertise;            }
    public int          getEvaluationsCompleted() { return evaluationsCompleted; }
    public List<String> getAssignedChallengeIds() { return assignedChallengeIds; }

    public void setExpertise(String expertise) {
        if (expertise != null && !expertise.isBlank()) this.expertise = expertise;
    }

    @Override
    public String toString() {
        return String.format(
                "models.Evaluator{id='%s', username='%s', expertise='%s', evaluationsDone=%d}",
                getUserId(), getUsername(), expertise, evaluationsCompleted
        );
    }
}