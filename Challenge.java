package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================
 *  SolveStack – Open Innovation Collaboration Platform
 *  File   : Challenge.java
 *  Package: models
 *  Role   : Represents a problem/challenge posted by a Company.
 *
 *  OOP Principles Applied:
 *  ─────────────────────────────────────────────────────────
 *  ✔ Encapsulation  – All fields private; state changes only
 *                     via controlled methods.
 *  ✔ Abstraction    – Status enum hides raw state strings.
 * ============================================================
 */
public class Challenge {

    // ─────────────────────────────────────────────────────────
    //  STATUS ENUM
    // ─────────────────────────────────────────────────────────

    public enum Status { OPEN, UNDER_REVIEW, CLOSED }

    // ─────────────────────────────────────────────────────────
    //  PRIVATE FIELDS
    // ─────────────────────────────────────────────────────────

    private final String      challengeId;
    private       String      title;
    private       String      description;
    private final Company     postedBy;
    private       double      prizeAmount;
    private final LocalDate   postedDate;
    private       LocalDate   deadline;
    private       Status      status;
    private final List<Submission> submissions;
    private       String      assignedEvaluatorId; // Set by Admin / ChallengeService

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Creates a new Challenge in OPEN status.
     *
     * @param challengeId  Unique identifier.
     * @param title        Short problem title.
     * @param description  Full problem statement.
     * @param postedBy     The Company that created this challenge.
     * @param prizeAmount  Monetary reward for the winning solution.
     * @param durationDays How many days the challenge remains open.
     */
    public Challenge(String challengeId,
                     String title,
                     String description,
                     Company postedBy,
                     double prizeAmount,
                     int durationDays) {
        if (challengeId == null || challengeId.isBlank())
            throw new IllegalArgumentException("challengeId cannot be blank.");
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("title cannot be blank.");
        if (postedBy == null)
            throw new IllegalArgumentException("postedBy company cannot be null.");
        if (prizeAmount <= 0)
            throw new IllegalArgumentException("prizeAmount must be positive.");
        if (durationDays <= 0)
            throw new IllegalArgumentException("durationDays must be positive.");

        this.challengeId  = challengeId;
        this.title        = title;
        this.description  = description;
        this.postedBy     = postedBy;
        this.prizeAmount  = prizeAmount;
        this.postedDate   = LocalDate.now();
        this.deadline     = postedDate.plusDays(durationDays);
        this.status       = Status.OPEN;
        this.submissions  = new ArrayList<>();
    }

    // ─────────────────────────────────────────────────────────
    //  CORE METHODS
    // ─────────────────────────────────────────────────────────

    /**
     * Reopens a closed challenge.
     */
    public void openChallenge() {
        if (status == Status.OPEN) {
            System.out.println("[CHALLENGE] Already OPEN: " + challengeId);
            return;
        }
        this.status = Status.OPEN;
        System.out.println("[CHALLENGE OPENED] " + title + " is now accepting submissions.");
    }

    /**
     * Closes the challenge, preventing new submissions.
     * Moves status to CLOSED.
     */
    public void closeChallenge() {
        if (status == Status.CLOSED) {
            System.out.println("[CHALLENGE] Already CLOSED: " + challengeId);
            return;
        }
        this.status = Status.CLOSED;
        System.out.println("[CHALLENGE CLOSED] " + title);
    }

    /**
     * Moves status to UNDER_REVIEW when evaluation begins.
     */
    public void markUnderReview() {
        this.status = Status.UNDER_REVIEW;
        System.out.println("[CHALLENGE] '" + title + "' is now UNDER REVIEW.");
    }

    /**
     * Extends the submission deadline by the given number of days.
     *
     * @param extraDays Number of days to add.
     */
    public void updateDeadline(int extraDays) {
        if (status == Status.CLOSED) {
            System.out.println("[ERROR] Cannot extend deadline of a CLOSED challenge.");
            return;
        }
        if (extraDays <= 0) {
            System.out.println("[ERROR] Extra days must be positive.");
            return;
        }
        this.deadline = this.deadline.plusDays(extraDays);
        System.out.println("[DEADLINE] Extended by " + extraDays
                + " day(s). New deadline: " + deadline);
    }

    /**
     * Adds a validated Submission to this challenge.
     *
     * @param submission The Submission to attach.
     * @return true if added; false if the challenge is not OPEN.
     */
    public boolean addSubmission(Submission submission) {
        if (status != Status.OPEN) {
            System.out.println("[ERROR] Submissions are only accepted for OPEN challenges.");
            return false;
        }
        if (submission == null) {
            System.out.println("[ERROR] Submission cannot be null.");
            return false;
        }
        // Prevent duplicate submissions from the same developer
        for (Submission s : submissions) {
            if (s.getDeveloperUsername().equals(submission.getDeveloperUsername())) {
                System.out.println("[ERROR] Developer " + submission.getDeveloperUsername()
                        + " has already submitted to this challenge.");
                return false;
            }
        }
        submissions.add(submission);
        System.out.println("[SUBMISSION ADDED] " + submission.getSubmissionId()
                + " added to challenge: " + title);
        return true;
    }

    /**
     * Prints a summary of the challenge.
     */
    public void printDetails() {
        System.out.println("============================================");
        System.out.println("  Challenge   : " + title);
        System.out.println("  ID          : " + challengeId);
        System.out.println("  Company     : " + postedBy.getCompanyName());
        System.out.println("  Status      : " + status);
        System.out.println("  Prize       : $" + prizeAmount);
        System.out.println("  Posted      : " + postedDate);
        System.out.println("  Deadline    : " + deadline);
        System.out.println("  Submissions : " + submissions.size());
        System.out.println("--------------------------------------------");
        System.out.println("  Description : " + description);
        System.out.println("============================================");
    }

    // ─────────────────────────────────────────────────────────
    //  GETTERS
    // ─────────────────────────────────────────────────────────

    public String          getChallengeId()         { return challengeId;          }
    public String          getTitle()               { return title;                }
    public String          getDescription()         { return description;          }
    public Company         getPostedBy()            { return postedBy;             }
    public double          getPrizeAmount()         { return prizeAmount;          }
    public LocalDate       getPostedDate()          { return postedDate;           }
    public LocalDate       getDeadline()            { return deadline;             }
    public Status          getStatus()              { return status;               }
    public String          getAssignedEvaluatorId() { return assignedEvaluatorId;  }
    public List<Submission> getSubmissions() {
        return Collections.unmodifiableList(submissions);
    }

    // ─────────────────────────────────────────────────────────
    //  PACKAGE-LEVEL / PROTECTED SETTERS
    // ─────────────────────────────────────────────────────────

    void setTitle(String title)               { if (title != null && !title.isBlank()) this.title = title; }
    void setDescription(String description)   { if (description != null)              this.description = description; }
    void setPrizeAmount(double prizeAmount)   { if (prizeAmount > 0)                  this.prizeAmount = prizeAmount; }
    public void setAssignedEvaluatorId(String id) { this.assignedEvaluatorId = id; }

    // ─────────────────────────────────────────────────────────
    //  toString
    // ─────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format(
                "Challenge{id='%s', title='%s', company='%s', status=%s, prize=%.2f, submissions=%d}",
                challengeId, title, postedBy.getCompanyName(), status, prizeAmount, submissions.size()
        );
    }
}