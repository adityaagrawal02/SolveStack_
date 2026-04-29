package models;

/**
 * ============================================================
 *  SolveStack – Open Innovation Collaboration Platform
 *  File   : models.Submission.java
 *  Package: models
 *  Role   : Represents a developer's solution to a models.Challenge.
 *
 *  OOP Principles Applied:
 *  ─────────────────────────────────────────────────────────
 *   Encapsulation  – All fields private; mutable only via
 *                     controlled setters / methods.
 *   Abstraction    – Status enum cleanly models lifecycle.
 * ============================================================
 */
public class Submission {

    // ─────────────────────────────────────────────────────────
    //  STATUS ENUM
    // ─────────────────────────────────────────────────────────

    public enum Status { SUBMITTED, UNDER_REVIEW, ACCEPTED, REJECTED, WITHDRAWN }

    // ─────────────────────────────────────────────────────────
    //  PRIVATE FIELDS
    // ─────────────────────────────────────────────────────────

    private final String submissionId;
    private final String challengeId;
    private final String developerUsername;
    private       String solutionSummary;
    private       String githubLink;
    private       String documentPath;       // Path to uploaded document
    private       Status status;
    private       double score;              // Assigned by models.Evaluator (0–100)
    private       String evaluatorFeedback;

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Creates a new models.Submission in SUBMITTED status.
     *
     * @param submissionId      Unique identifier.
     * @param challengeId       The challenge this solves.
     * @param developerUsername The developer who submitted.
     * @param solutionSummary   Brief overview of the solution.
     */
    public Submission(String submissionId,
                      String challengeId,
                      String developerUsername,
                      String solutionSummary) {
        if (submissionId     == null || submissionId.isBlank())
            throw new IllegalArgumentException("submissionId cannot be blank.");
        if (challengeId      == null || challengeId.isBlank())
            throw new IllegalArgumentException("challengeId cannot be blank.");
        if (developerUsername == null || developerUsername.isBlank())
            throw new IllegalArgumentException("developerUsername cannot be blank.");
        if (solutionSummary  == null || solutionSummary.isBlank())
            throw new IllegalArgumentException("solutionSummary cannot be blank.");

        this.submissionId      = submissionId;
        this.challengeId       = challengeId;
        this.developerUsername = developerUsername;
        this.solutionSummary   = solutionSummary;
        this.githubLink        = "";
        this.documentPath      = "";
        this.status            = Status.SUBMITTED;
        this.score             = 0.0;
        this.evaluatorFeedback = "";
    }

    // ─────────────────────────────────────────────────────────
    //  METHODS
    // ─────────────────────────────────────────────────────────

    /**
     * Attaches a GitHub repository link to the submission.
     *
     * @param link GitHub URL.
     */
    public void attachGithubLink(String link) {
        if (link == null || link.isBlank()) {
            System.out.println("[ERROR] GitHub link cannot be blank.");
            return;
        }
        this.githubLink = link;
        System.out.println("[SUBMISSION] GitHub link attached: " + link);
    }

    /**
     * Attaches a document path (e.g., PDF write-up) to the submission.
     *
     * @param path File path / URL of the uploaded document.
     */
    public void uploadDocument(String path) {
        if (path == null || path.isBlank()) {
            System.out.println("[ERROR] Document path cannot be blank.");
            return;
        }
        this.documentPath = path;
        System.out.println("[SUBMISSION] Document uploaded: " + path);
    }

    /**
     * Updates the submission lifecycle status.
     *
     * @param newStatus New Status value.
     */
    public void updateStatus(Status newStatus) {
        if (newStatus == null) {
            System.out.println("[ERROR] Status cannot be null.");
            return;
        }
        Status old = this.status;
        this.status = newStatus;
        System.out.println("[SUBMISSION STATUS] " + submissionId
                + " changed: " + old + " → " + newStatus);
    }

    /**
     * Records the evaluator's score and feedback for this submission.
     *
     * @param score    Score between 0.0 and 100.0.
     * @param feedback Written feedback from the evaluator.
     */
    public void evaluate(double score, String feedback) {
        if (score < 0 || score > 100) {
            System.out.println("[ERROR] Score must be between 0 and 100.");
            return;
        }
        this.score             = score;
        this.evaluatorFeedback = (feedback != null) ? feedback : "";
        System.out.printf("[SCORE] models.Submission %s scored: %.1f/100%n", submissionId, score);
    }

    /**
     * Prints a detailed summary of the submission.
     */
    public void printDetails() {
        System.out.println("============================================");
        System.out.println("  models.Submission ID  : " + submissionId);
        System.out.println("  models.Challenge ID   : " + challengeId);
        System.out.println("  models.Developer      : " + developerUsername);
        System.out.println("  Status         : " + status);
        System.out.printf ("  Score          : %.1f / 100%n", score);
        System.out.println("  GitHub         : " + (githubLink.isBlank()   ? "N/A" : githubLink));
        System.out.println("  Document       : " + (documentPath.isBlank() ? "N/A" : documentPath));
        System.out.println("  Summary        : " + solutionSummary);
        if (!evaluatorFeedback.isBlank()) {
            System.out.println("  Feedback       : " + evaluatorFeedback);
        }
        System.out.println("============================================");
    }

    // ─────────────────────────────────────────────────────────
    //  GETTERS
    // ─────────────────────────────────────────────────────────

    public String getSubmissionId()      { return submissionId;      }
    public String getChallengeId()       { return challengeId;       }
    public String getDeveloperUsername() { return developerUsername; }
    public String getSolutionSummary()   { return solutionSummary;   }
    public String getGithubLink()        { return githubLink;        }
    public String getDocumentPath()      { return documentPath;      }
    public Status getStatus()            { return status;            }
    public double getScore()             { return score;             }
    public String getEvaluatorFeedback() { return evaluatorFeedback; }

    // ─────────────────────────────────────────────────────────
    //  toString
    // ─────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format(
                "models.Submission{id='%s', challenge='%s', developer='%s', status=%s, score=%.1f}",
                submissionId, challengeId, developerUsername, status, score
        );
    }
}