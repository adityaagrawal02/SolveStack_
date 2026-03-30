package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================
 *  SolveStack – Open Innovation Collaboration Platform
 *  File   : models.Developer.java
 *  Package: models
 *  Role   : Represents a developer or researcher who browses
 *           challenges and submits solutions.
 *
 *  OOP Principles Applied:
 *  ─────────────────────────────────────────────────────────
 *   Inheritance    – Extends models.User.
 *   Encapsulation  – Submissions list is private; exposed
 *                     only as an unmodifiable view.
 *   Polymorphism   – Overrides getRole() and
 *                     displayRoleDashboard().
 *   Abstraction    – Implements all abstract models.User methods.
 * ============================================================
 */
public class Developer extends User {

    // ─────────────────────────────────────────────────────────
    //  DEVELOPER-SPECIFIC PRIVATE FIELDS
    // ─────────────────────────────────────────────────────────

    private String       skillSet;           // Comma-separated skill tags
    private String       portfolioUrl;
    private int          totalPoints;        // Accumulated from scored submissions
    private final List<Submission> mySubmissions;

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Creates a models.Developer user.
     *
     * @param userId   Unique platform identifier.
     * @param username Display / login name.
     * @param email    Email address.
     * @param password Plain-text password (hashed in models.User).
     * @param skillSet Comma-separated skills (e.g., "Java, ML, Cloud").
     */
    public Developer(String userId,
                     String username,
                     String email,
                     String password,
                     String skillSet) {
        super(userId, username, email, password);
        this.skillSet      = (skillSet != null) ? skillSet : "";
        this.portfolioUrl  = "";
        this.totalPoints   = 0;
        this.mySubmissions = new ArrayList<>();
    }

    // ─────────────────────────────────────────────────────────
    //  ABSTRACT METHOD IMPLEMENTATIONS
    // ─────────────────────────────────────────────────────────

    @Override
    public String getRole() { return "models.Developer"; }

    @Override
    protected void displayRoleDashboard() {
        long pending  = mySubmissions.stream()
                .filter(s -> s.getStatus() == Submission.Status.SUBMITTED).count();
        long accepted = mySubmissions.stream()
                .filter(s -> s.getStatus() == Submission.Status.ACCEPTED).count();

        System.out.println("  Skills        : " + (skillSet.isBlank() ? "N/A" : skillSet));
        System.out.println("  Portfolio     : " + (portfolioUrl.isBlank() ? "N/A" : portfolioUrl));
        System.out.println("  Total Points  : " + totalPoints);
        System.out.println("--------------------------------------------");
        System.out.println("  Submissions   : " + mySubmissions.size());
        System.out.println("  Pending Review: " + pending);
        System.out.println("  Accepted      : " + accepted);
        System.out.println("--------------------------------------------");
        System.out.println("  Quick Actions:");
        System.out.println("    1) browseChallenges(allChallenges)");
        System.out.println("    2) submitSolution(challenge, summary)");
        System.out.println("    3) trackSubmission(submissionId)");
        System.out.println("    4) withdrawSubmission(submissionId)");
        System.out.println("============================================");
    }

    @Override
    public void onAccountRemoved() {
        System.out.println("[ACCOUNT REMOVED] models.Developer '" + getUsername()
                + "' removed. Withdrawing pending submissions...");
        for (Submission s : mySubmissions) {
            if (s.getStatus() == Submission.Status.SUBMITTED ||
                    s.getStatus() == Submission.Status.UNDER_REVIEW) {
                s.updateStatus(Submission.Status.WITHDRAWN);
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    //  DEVELOPER METHODS
    // ─────────────────────────────────────────────────────────

    /**
     * Prints all open challenges from a provided list.
     *
     * @param allChallenges The platform's full challenge list.
     */
    public void browseChallenges(List<Challenge> allChallenges) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] Please log in to browse challenges.");
            return;
        }
        System.out.println("============================================");
        System.out.println("  Open Challenges on SolveStack");
        System.out.println("--------------------------------------------");

        boolean found = false;
        for (Challenge c : allChallenges) {
            if (c.getStatus() == Challenge.Status.OPEN) {
                System.out.printf("  [%s] %s | Prize: $%.2f | Deadline: %s%n",
                        c.getChallengeId(), c.getTitle(),
                        c.getPrizeAmount(), c.getDeadline());
                System.out.println("       " + c.getDescription().substring(
                        0, Math.min(80, c.getDescription().length())) + "...");
                found = true;
            }
        }
        if (!found) System.out.println("  No open challenges available at this time.");
        System.out.println("============================================");
    }

    /**
     * Submits a solution to an open challenge.
     *
     * @param challenge       The target challenge.
     * @param solutionSummary Brief description of the solution.
     * @return The created models.Submission, or null on failure.
     */
    public Submission submitSolution(Challenge challenge, String solutionSummary) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] You must be logged in to submit.");
            return null;
        }
        if (!isVerified()) {
            System.out.println("[ERROR] Your account must be verified before submitting.");
            return null;
        }
        if (challenge == null) {
            System.out.println("[ERROR] models.Challenge cannot be null.");
            return null;
        }
        if (challenge.getStatus() != Challenge.Status.OPEN) {
            System.out.println("[ERROR] models.Challenge is not open for submissions.");
            return null;
        }
        if (solutionSummary == null || solutionSummary.isBlank()) {
            System.out.println("[ERROR] Solution summary cannot be blank.");
            return null;
        }

        String submissionId = "SUB-" + getUserId() + "-" + challenge.getChallengeId()
                + "-" + (mySubmissions.size() + 1);
        Submission submission = new Submission(submissionId,
                challenge.getChallengeId(),
                getUsername(),
                solutionSummary);

        boolean added = challenge.addSubmission(submission);
        if (added) {
            mySubmissions.add(submission);
            System.out.println("[SUBMIT SUCCESS] models.Submission ID: " + submissionId);
        }
        return added ? submission : null;
    }

    /**
     * Displays the current status of a specific submission.
     *
     * @param submissionId ID of the submission to track.
     */
    public void trackSubmission(String submissionId) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] Please log in.");
            return;
        }
        Submission found = findSubmission(submissionId);
        if (found != null) {
            found.printDetails();
        }
    }

    /**
     * Withdraws a submission that is still pending or under review.
     *
     * @param submissionId ID of the submission to withdraw.
     */
    public void withdrawSubmission(String submissionId) {
        if (!isLoggedIn()) {
            System.out.println("[ERROR] Please log in.");
            return;
        }
        Submission submission = findSubmission(submissionId);
        if (submission == null) return;

        if (submission.getStatus() == Submission.Status.ACCEPTED ||
                submission.getStatus() == Submission.Status.REJECTED) {
            System.out.println("[ERROR] Cannot withdraw an already evaluated submission.");
            return;
        }
        if (submission.getStatus() == Submission.Status.WITHDRAWN) {
            System.out.println("[INFO] models.Submission already withdrawn.");
            return;
        }
        submission.updateStatus(Submission.Status.WITHDRAWN);
        System.out.println("[WITHDRAWN] models.Submission " + submissionId + " has been withdrawn.");
    }

    /**
     * Adds points to the developer's total (called by EvaluationService).
     *
     * @param points Points to add.
     */
    public void addPoints(int points) {
        if (points > 0) {
            this.totalPoints += points;
            System.out.println("[POINTS] " + getUsername() + " earned " + points
                    + " points. Total: " + totalPoints);
        }
    }

    // ─────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────

    private Submission findSubmission(String submissionId) {
        for (Submission s : mySubmissions) {
            if (s.getSubmissionId().equals(submissionId)) return s;
        }
        System.out.println("[ERROR] models.Submission not found: " + submissionId);
        return null;
    }

    // ─────────────────────────────────────────────────────────
    //  GETTERS & SETTERS
    // ─────────────────────────────────────────────────────────

    public String          getSkillSet()      { return skillSet;      }
    public String          getPortfolioUrl()  { return portfolioUrl;  }
    public int             getTotalPoints()   { return totalPoints;   }
    public List<Submission> getMySubmissions() {
        return Collections.unmodifiableList(mySubmissions);
    }

    public void setSkillSet(String skillSet)         { if (skillSet != null)       this.skillSet = skillSet; }
    public void setPortfolioUrl(String portfolioUrl) { if (portfolioUrl != null)   this.portfolioUrl = portfolioUrl; }

    @Override
    public String toString() {
        return String.format(
                "models.Developer{id='%s', username='%s', skills='%s', points=%d, submissions=%d}",
                getUserId(), getUsername(), skillSet, totalPoints, mySubmissions.size()
        );
    }
}