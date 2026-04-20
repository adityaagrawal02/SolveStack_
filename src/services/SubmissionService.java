package services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import models.Challenge;
import models.Developer;
import models.Submission;

/**
 * ============================================================
 *  SolveStack – Open Innovation Collaboration Platform
 *  File   : SubmissionService.java
 *  Package: services
 *  Role   : Service layer that manages all submission-related
 *           operations — creating, validating, fetching,
 *           updating, and withdrawing submissions across
 *           the entire platform.
 *
 *  OOP Principles Applied:
 *  ─────────────────────────────────────────────────────────
 *  ✔ Encapsulation  – Master submission registry is private;
 *                     all access goes through controlled methods.
 *  ✔ Abstraction    – Callers interact through clean method
 *                     signatures; storage and validation
 *                     details are hidden.
 *  ✔ Single Responsibility – This class only manages
 *                     submissions. Scoring belongs to
 *                     EvaluationService.
 * ============================================================
 */
public class SubmissionService {

    // ─────────────────────────────────────────────────────────
    //  ENCAPSULATION: private master registry of all submissions
    // ─────────────────────────────────────────────────────────

    private final List<Submission> allSubmissions;   // Platform-wide submission registry
    private static int             submissionCounter; // Auto-incrementing ID generator

    // ─────────────────────────────────────────────────────────
    //  CONSTANTS – Submission status labels
    // ─────────────────────────────────────────────────────────

    public static final String STATUS_PENDING    = "SUBMITTED";
    public static final String STATUS_UNDER_REVIEW = "UNDER_REVIEW";
    public static final String STATUS_ACCEPTED   = "ACCEPTED";
    public static final String STATUS_REJECTED   = "REJECTED";
    public static final String STATUS_WITHDRAWN  = "WITHDRAWN";

    // ─────────────────────────────────────────────────────────
    //  CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Initialises the service with an empty submission registry.
     */
    public SubmissionService() {
        this.allSubmissions  = new ArrayList<>();
        submissionCounter    = 2000; // Submission IDs start from SUB-2001
    }

    // ─────────────────────────────────────────────────────────
    //  CORE SERVICE METHODS
    // ─────────────────────────────────────────────────────────

    /**
     * Submits a solution to a challenge on behalf of a Developer.
     *
     * Validates:
     *  - Developer is logged in
     *  - Challenge is OPEN
     *  - Developer has not already submitted to this challenge
     *  - Submission content is not empty
     *
     * On success, the Submission is registered in both the
     * platform-wide registry and the Challenge's own list.
     *
     * @param developer    The Developer submitting the solution.
     * @param challenge    The Challenge being responded to.
     * @param solutionText Description / summary of the solution.
     * @param githubLink   Optional GitHub repository URL.
     * @param documentPath Optional path to an uploaded document.
     * @return The created Submission, or null if validation failed.
     */
    public Submission submitSolution(Developer developer,
                                     Challenge challenge,
                                     String solutionText,
                                     String githubLink,
                                     String documentPath) {

        // ── Validate Developer ──────────────────────────────
        if (developer == null) {
            System.out.println("[SubmissionService] ERROR: Developer reference is null.");
            return null;
        }
        if (!developer.isLoggedIn()) {
            System.out.println("[SubmissionService] ERROR: Developer must be logged in to submit.");
            return null;
        }

        // ── Validate Challenge ──────────────────────────────
        if (challenge == null) {
            System.out.println("[SubmissionService] ERROR: Challenge reference is null.");
            return null;
        }
        if (challenge.getStatus() != Challenge.Status.OPEN) {
            System.out.println("[SubmissionService] ERROR: Challenge '"
                    + challenge.getTitle() + "' is not open for submissions. "
                    + "Current status: " + challenge.getStatus());
            return null;
        }

        // ── Check for duplicate submission ──────────────────
        if (hasAlreadySubmitted(developer.getUsername(), challenge.getChallengeId())) {
            System.out.println("[SubmissionService] ERROR: Developer '"
                    + developer.getUsername()
                    + "' has already submitted to challenge: "
                    + challenge.getTitle());
            return null;
        }

        // ── Validate content ────────────────────────────────
        if (!validateSubmission(solutionText)) {
            return null;
        }

        // ── Create and register the submission ──────────────
        String newId = generateSubmissionId();

        Submission submission = new Submission(
                newId,
                challenge.getChallengeId(),
            developer.getUsername(),
                solutionText
        );

        // Attach optional extras
        if (githubLink != null && !githubLink.isBlank()) {
            submission.attachGithubLink(githubLink);
        }
        if (documentPath != null && !documentPath.isBlank()) {
            submission.uploadDocument(documentPath);
        }

        // Register in platform registry
        allSubmissions.add(submission);

        // Register inside the Challenge object itself
        challenge.addSubmission(submission);

        // Let the Developer track it
        developer.trackSubmission(submission.getSubmissionId());

        System.out.println("[SubmissionService] Submission '" + newId
                + "' received from '" + developer.getUsername()
                + "' for challenge: " + challenge.getTitle());

        return submission;
    }

    /**
     * Validates the content of a submission before accepting it.
     *
     * Rules:
     * - Solution text cannot be null or blank
     * - Solution text must be at least 20 characters long
     *
     * @param solutionText The submission content to validate.
     * @return true if the submission content passes all checks.
     */
    public boolean validateSubmission(String solutionText) {
        if (solutionText == null || solutionText.isBlank()) {
            System.out.println("[SubmissionService] VALIDATION FAILED: Solution text cannot be empty.");
            return false;
        }
        if (solutionText.trim().length() < 20) {
            System.out.println("[SubmissionService] VALIDATION FAILED: Solution must be at least 20 characters. "
                    + "Current length: " + solutionText.trim().length());
            return false;
        }
        System.out.println("[SubmissionService] Submission content validated successfully.");
        return true;
    }

    /**
     * Withdraws a submission if the Developer chooses to retract it.
     *
     * Rules:
     * - Only the submitting Developer can withdraw their own submission
     * - Only PENDING submissions can be withdrawn (not ones under review)
     *
     * @param submissionId  ID of the submission to withdraw.
     * @param developerId   ID of the Developer requesting withdrawal.
     * @return true if withdrawal was successful.
     */
    public boolean withdrawSubmission(String submissionId, String developerId) {
        Submission target = findById(submissionId);

        if (target == null) {
            System.out.println("[SubmissionService] ERROR: Submission '" + submissionId + "' not found.");
            return false;
        }
        if (!target.getDeveloperUsername().equals(developerId)) {
            System.out.println("[SubmissionService] UNAUTHORIZED: Only the submitting developer can withdraw this submission.");
            return false;
        }
        if (!statusEquals(target, STATUS_PENDING)) {
            System.out.println("[SubmissionService] ERROR: Only PENDING submissions can be withdrawn. "
                    + "Current status: " + target.getStatus());
            return false;
        }

        target.updateStatus(toStatus(STATUS_WITHDRAWN));
        System.out.println("[SubmissionService] Submission '" + submissionId
                + "' has been withdrawn by developer ID: " + developerId);
        return true;
    }

    /**
     * Fetches all submissions for a specific challenge.
     * Used by Companies to review incoming solutions and by
     * Evaluators to begin the scoring process.
     *
     * @param challengeId The ID of the challenge.
     * @return List of all submissions for that challenge.
     */
    public List<Submission> fetchSubmissionsByChallenge(String challengeId) {
        if (challengeId == null || challengeId.isBlank()) {
            System.out.println("[SubmissionService] ERROR: Challenge ID cannot be blank.");
            return Collections.emptyList();
        }

        List<Submission> results = new ArrayList<>();
        for (Submission s : allSubmissions) {
            if (s.getChallengeId().equals(challengeId)) {
                results.add(s);
            }
        }

        System.out.println("[SubmissionService] Found " + results.size()
                + " submission(s) for challenge ID: " + challengeId);
        return results;
    }

    /**
     * Fetches all submissions made by a specific Developer.
     * Used to build a Developer's submission history dashboard.
     *
     * @param developerId The Developer's unique user ID.
     * @return List of all submissions by that developer.
     */
    public List<Submission> fetchSubmissionsByDeveloper(String developerId) {
        if (developerId == null || developerId.isBlank()) {
            System.out.println("[SubmissionService] ERROR: Developer ID cannot be blank.");
            return Collections.emptyList();
        }

        List<Submission> results = new ArrayList<>();
        for (Submission s : allSubmissions) {
            if (s.getDeveloperUsername().equals(developerId)) {
                results.add(s);
            }
        }

        System.out.println("[SubmissionService] Found " + results.size()
                + " submission(s) for developer ID: " + developerId);
        return results;
    }

    /**
     * Updates the status of a submission.
     * Typically called by EvaluationService during the review process.
     *
     * Valid transitions:
     *  PENDING → UNDER_REVIEW → ACCEPTED or REJECTED
     *
     * @param submissionId ID of the submission to update.
     * @param newStatus    The new status string.
     * @return true if the status was updated successfully.
     */
    public boolean updateSubmissionStatus(String submissionId, String newStatus) {
        if (!isValidStatus(newStatus)) {
            System.out.println("[SubmissionService] ERROR: Invalid status value: '" + newStatus + "'. "
                    + "Allowed: PENDING, UNDER_REVIEW, ACCEPTED, REJECTED, WITHDRAWN");
            return false;
        }

        Submission target = findById(submissionId);
        if (target == null) {
            System.out.println("[SubmissionService] ERROR: Submission '" + submissionId + "' not found.");
            return false;
        }
        if (statusEquals(target, STATUS_WITHDRAWN)) {
            System.out.println("[SubmissionService] ERROR: Cannot update a WITHDRAWN submission.");
            return false;
        }

        String previousStatus = target.getStatus().name();
        target.updateStatus(toStatus(newStatus));
        System.out.println("[SubmissionService] Submission '" + submissionId
                + "' status changed: " + previousStatus + " → " + newStatus.toUpperCase());
        return true;
    }

    /**
     * Returns an unmodifiable view of all submissions on the platform.
     *
     * @return Read-only list of all submissions.
     */
    public List<Submission> getAllSubmissions() {
        return Collections.unmodifiableList(allSubmissions);
    }

    /**
     * Returns only submissions currently in PENDING status.
     * Used by Evaluators to find work that needs reviewing.
     *
     * @return List of pending submissions.
     */
    public List<Submission> getPendingSubmissions() {
        List<Submission> pending = new ArrayList<>();
        for (Submission s : allSubmissions) {
            if (statusEquals(s, STATUS_PENDING)) {
                pending.add(s);
            }
        }
        System.out.println("[SubmissionService] " + pending.size() + " pending submission(s) found.");
        return pending;
    }

    /**
     * Retrieves a single submission by its unique ID.
     *
     * @param submissionId The ID to look up.
     * @return The matching Submission, or null if not found.
     */
    public Submission getSubmissionById(String submissionId) {
        Submission result = findById(submissionId);
        if (result == null) {
            System.out.println("[SubmissionService] No submission found with ID: " + submissionId);
        }
        return result;
    }

    /**
     * Returns the total number of submissions on the platform.
     *
     * @return Count of all submissions (all statuses).
     */
    public int getTotalSubmissionCount() {
        return allSubmissions.size();
    }

    /**
     * Prints a formatted summary of all submissions.
     * Useful for Admin oversight and platform reporting.
     */
    public void printAllSubmissions() {
        System.out.println("============================================");
        System.out.println("  SolveStack – All Submissions (" + allSubmissions.size() + " total)");
        System.out.println("--------------------------------------------");

        if (allSubmissions.isEmpty()) {
            System.out.println("  No submissions have been made yet.");
        } else {
            for (Submission s : allSubmissions) {
                System.out.println("  ID          : " + s.getSubmissionId());
                System.out.println("  Developer   : " + s.getDeveloperUsername());
                System.out.println("  Challenge   : " + s.getChallengeId());
                System.out.println("  Status      : " + s.getStatus());
                System.out.println("  Score       : " + (s.getScore() == 0 ? "Not yet scored" : s.getScore()));
                System.out.println("  GitHub Link : " + (s.getGithubLink() == null || s.getGithubLink().isBlank()
                        ? "Not provided" : s.getGithubLink()));
                System.out.println("  ----------------------------------------");
            }
        }
        System.out.println("============================================");
    }

    // ─────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────

    /**
     * Internal lookup by submission ID.
     *
     * @param submissionId The ID to search for.
     * @return Matching Submission or null.
     */
    private Submission findById(String submissionId) {
        if (submissionId == null || submissionId.isBlank()) return null;
        for (Submission s : allSubmissions) {
            if (s.getSubmissionId().equals(submissionId)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Checks if a Developer has already submitted to a given challenge.
     * Prevents duplicate submissions from the same Developer.
     *
     * @param developerId The Developer's user ID.
     * @param challengeId The Challenge's ID.
     * @return true if a submission already exists.
     */
    private boolean hasAlreadySubmitted(String developerId, String challengeId) {
        for (Submission s : allSubmissions) {
            if (s.getDeveloperUsername().equals(developerId)
                    && s.getChallengeId().equals(challengeId)
                    && !statusEquals(s, STATUS_WITHDRAWN)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates that a given status string is one of the accepted values.
     *
     * @param status The status string to validate.
     * @return true if valid.
     */
    private boolean isValidStatus(String status) {
        return status != null && (
                status.equalsIgnoreCase(STATUS_PENDING)       ||
                status.equalsIgnoreCase(STATUS_UNDER_REVIEW)  ||
                status.equalsIgnoreCase(STATUS_ACCEPTED)      ||
                status.equalsIgnoreCase(STATUS_REJECTED)      ||
                status.equalsIgnoreCase(STATUS_WITHDRAWN)
        );
    }

    private boolean statusEquals(Submission submission, String status) {
        return submission.getStatus().name().equalsIgnoreCase(status);
    }

    private Submission.Status toStatus(String status) {
        return switch (status.toUpperCase()) {
            case STATUS_PENDING -> Submission.Status.SUBMITTED;
            case STATUS_UNDER_REVIEW -> Submission.Status.UNDER_REVIEW;
            case STATUS_ACCEPTED -> Submission.Status.ACCEPTED;
            case STATUS_REJECTED -> Submission.Status.REJECTED;
            case STATUS_WITHDRAWN -> Submission.Status.WITHDRAWN;
            default -> throw new IllegalArgumentException("Unsupported submission status: " + status);
        };
    }

    /**
     * Generates a unique, auto-incrementing submission ID.
     * Format: SUB-2001, SUB-2002, ...
     *
     * @return New unique submission ID string.
     */
    private static String generateSubmissionId() {
        return "SUB-" + (++submissionCounter);
    }
}
