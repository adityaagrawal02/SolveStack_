package models;

import java.util.*;

/**
 * ============================================================
 * SolveStack – Open Innovation Collaboration Platform
 * File    : EvaluationService.java
 * Package : models
 * Role    : Service layer that manages the complete evaluation
 *           lifecycle — assigning evaluators, scoring
 *           submissions, computing final scores, generating
 *           leaderboards, and publishing results.
 *
 *           Also contains two static inner classes:
 *           ● EvaluationRecord  – immutable result of one evaluation
 *           ● LeaderboardEntry  – one ranked entry in a leaderboard
 *
 * OOP Principles Applied:
 * ─────────────────────────────────────────────────────────
 * ✔ Encapsulation  – All internal registries are private;
 *                    access goes through controlled methods.
 * ✔ Abstraction    – Scoring weights and formula are hidden
 *                    inside calculateFinalScore().
 * ✔ Single Resp.   – This class only manages evaluations.
 *                    Submission creation belongs to
 *                    SubmissionService.
 * ✔ Polymorphism   – Delegates scoring to Evaluator via the
 *                    Evaluable interface contract.
 * ============================================================
 */
public class EvaluationService {

    // ─────────────────────────────────────────────────────────
    // ENCAPSULATION: private registries
    // ─────────────────────────────────────────────────────────

    /** Maps submissionId → EvaluationRecord */
    private final Map<String, EvaluationRecord> evaluationRecords;

    /** Maps submissionId → evaluatorUsername */
    private final Map<String, String> assignmentMap;

    /** Maps evaluatorUsername → number of submissions assigned */
    private final Map<String, Integer> evaluatorWorkload;

    // ─────────────────────────────────────────────────────────
    // SCORING WEIGHTS — must sum to 1.0
    // ─────────────────────────────────────────────────────────

    private static final double WEIGHT_INNOVATION   = 0.30;
    private static final double WEIGHT_FEASIBILITY  = 0.25;
    private static final double WEIGHT_COMPLETENESS = 0.25;
    private static final double WEIGHT_PRESENTATION = 0.20;

    // ─────────────────────────────────────────────────────────
    // CONSTRUCTOR
    // ─────────────────────────────────────────────────────────

    /**
     * Initialises the EvaluationService with empty registries.
     */
    public EvaluationService() {
        this.evaluationRecords = new LinkedHashMap<>();
        this.assignmentMap     = new HashMap<>();
        this.evaluatorWorkload = new HashMap<>();
    }

    // ─────────────────────────────────────────────────────────
    // CORE SERVICE METHODS
    // ─────────────────────────────────────────────────────────

    /**
     * Assigns an Evaluator to a specific Submission.
     *
     * Validates:
     * - Evaluator is not null and is logged in
     * - Submission is not null
     * - Submission is not already ACCEPTED, REJECTED, or WITHDRAWN
     * - Submission has not already been assigned
     *
     * On success, moves submission status to UNDER_REVIEW.
     *
     * @param evaluator  The Evaluator to assign.
     * @param submission The Submission to be evaluated.
     * @return true if assignment was successful.
     */
    public boolean assignEvaluator(Evaluator evaluator, Submission submission) {

        // ── Validate evaluator ───────────────────────────────
        if (evaluator == null) {
            System.out.println("[EvaluationService] ERROR: Evaluator reference is null.");
            return false;
        }
        if (!evaluator.isLoggedIn()) {
            System.out.println("[EvaluationService] ERROR: Evaluator '"
                    + evaluator.getUsername() + "' must be logged in.");
            return false;
        }

        // ── Validate submission ──────────────────────────────
        if (submission == null) {
            System.out.println("[EvaluationService] ERROR: Submission reference is null.");
            return false;
        }

        String subId             = submission.getSubmissionId();
        Submission.Status status = submission.getStatus();

        if (status == Submission.Status.ACCEPTED
                || status == Submission.Status.REJECTED
                || status == Submission.Status.WITHDRAWN) {
            System.out.println("[EvaluationService] ERROR: Cannot assign evaluator to a '"
                    + status + "' submission.");
            return false;
        }

        // ── Prevent duplicate assignment ─────────────────────
        if (assignmentMap.containsKey(subId)) {
            System.out.println("[EvaluationService] ERROR: Submission '" + subId
                    + "' is already assigned to: " + assignmentMap.get(subId));
            return false;
        }

        // ── Perform assignment ───────────────────────────────
        assignmentMap.put(subId, evaluator.getUsername());
        evaluatorWorkload.merge(evaluator.getUsername(), 1, Integer::sum);
        submission.updateStatus(Submission.Status.UNDER_REVIEW);

        System.out.println("[EvaluationService] Submission '" + subId
                + "' assigned to Evaluator '" + evaluator.getUsername() + "'.");
        return true;
    }

    /**
     * Evaluates a submission using four scoring dimensions and
     * stores the result as an EvaluationRecord.
     *
     * Scoring weights:
     *   Innovation (30%) + Feasibility (25%) + Completeness (25%) + Presentation (20%)
     *
     * Delegates the final score and feedback to
     * evaluator.evaluateSubmission() via the Evaluable interface,
     * which sets ACCEPTED (score ≥ 50) or REJECTED on the submission.
     *
     * @param evaluator     The Evaluator performing the review.
     * @param submission    The Submission being evaluated.
     * @param innovation    Originality / creative approach   (0–100).
     * @param feasibility   Practical viability               (0–100).
     * @param completeness  How fully the problem is solved    (0–100).
     * @param presentation  Clarity and quality of write-up    (0–100).
     * @param feedback      Mandatory written feedback.
     * @return The computed final score, or -1 if validation failed.
     */
    public double evaluateSubmission(Evaluator evaluator,
                                     Submission submission,
                                     double innovation,
                                     double feasibility,
                                     double completeness,
                                     double presentation,
                                     String feedback) {

        // ── Validate evaluator ───────────────────────────────
        if (evaluator == null || !evaluator.isLoggedIn()) {
            System.out.println("[EvaluationService] ERROR: Evaluator must be logged in to evaluate.");
            return -1;
        }

        // ── Validate submission ──────────────────────────────
        if (submission == null) {
            System.out.println("[EvaluationService] ERROR: Submission cannot be null.");
            return -1;
        }
        if (submission.getStatus() == Submission.Status.WITHDRAWN) {
            System.out.println("[EvaluationService] ERROR: Cannot evaluate a WITHDRAWN submission.");
            return -1;
        }

        // ── Validate all dimension scores ────────────────────
        if (!isValidScore(innovation) || !isValidScore(feasibility)
                || !isValidScore(completeness) || !isValidScore(presentation)) {
            System.out.println("[EvaluationService] ERROR: All scores must be between 0 and 100.");
            return -1;
        }

        // ── Validate feedback ────────────────────────────────
        if (feedback == null || feedback.isBlank()) {
            System.out.println("[EvaluationService] ERROR: Feedback cannot be empty.");
            return -1;
        }

        // ── Compute weighted final score ─────────────────────
        double finalScore = calculateFinalScore(innovation, feasibility, completeness, presentation);

        // ── Delegate to Evaluator via Evaluable interface ────
        // This calls submission.calculateScore() and sets ACCEPTED / REJECTED
        evaluator.evaluateSubmission(submission, finalScore, feedback);

        // ── Store the evaluation record ──────────────────────
        String subId = submission.getSubmissionId();
        evaluationRecords.put(subId, new EvaluationRecord(
                subId,
                evaluator.getUsername(),
                innovation,
                feasibility,
                completeness,
                presentation,
                finalScore,
                feedback
        ));

        System.out.println("[EvaluationService] Evaluation complete for submission '" + subId + "'.");
        System.out.println("  ├─ Innovation   : " + innovation);
        System.out.println("  ├─ Feasibility  : " + feasibility);
        System.out.println("  ├─ Completeness : " + completeness);
        System.out.println("  ├─ Presentation : " + presentation);
        System.out.println("  └─ Final Score  : "
                + String.format("%.2f", finalScore) + " / 100 → " + submission.getStatus());

        return finalScore;
    }

    /**
     * Computes the weighted final score from four evaluation dimensions.
     *
     * Formula:
     *   finalScore = (innovation × 0.30) + (feasibility × 0.25)
     *              + (completeness × 0.25) + (presentation × 0.20)
     *
     * Result rounded to 2 decimal places.
     * Public so it can be called standalone to preview a score.
     *
     * @param innovation    Originality score   (0–100).
     * @param feasibility   Feasibility score   (0–100).
     * @param completeness  Completeness score  (0–100).
     * @param presentation  Presentation score  (0–100).
     * @return Weighted final score (0.00–100.00).
     */
    public double calculateFinalScore(double innovation,
                                      double feasibility,
                                      double completeness,
                                      double presentation) {

        double raw = (innovation   * WEIGHT_INNOVATION)
                + (feasibility  * WEIGHT_FEASIBILITY)
                + (completeness * WEIGHT_COMPLETENESS)
                + (presentation * WEIGHT_PRESENTATION);

        return Math.round(raw * 100.0) / 100.0;
    }

    /**
     * Generates a ranked leaderboard for a specific challenge.
     *
     * Only submissions with a completed EvaluationRecord are included.
     * Sorted by final score descending; ranks assigned from 1.
     *
     * @param challengeId The ID of the challenge to rank.
     * @param submissions Submissions for that challenge
     *                    — from Challenge.getSubmissions().
     * @return Ordered list of LeaderboardEntry objects (rank 1 = best).
     */
    public List<LeaderboardEntry> generateLeaderboard(String challengeId,
                                                      List<Submission> submissions) {

        if (challengeId == null || challengeId.isBlank()) {
            System.out.println("[EvaluationService] ERROR: Challenge ID cannot be blank.");
            return Collections.emptyList();
        }

        if (submissions == null || submissions.isEmpty()) {
            System.out.println("[EvaluationService] No submissions found for challenge: " + challengeId);
            return Collections.emptyList();
        }

        List<LeaderboardEntry> leaderboard = new ArrayList<>();

        for (Submission s : submissions) {
            if (!s.getChallengeId().equals(challengeId)) continue;

            EvaluationRecord record = evaluationRecords.get(s.getSubmissionId());
            if (record == null) continue; // not yet evaluated — skip

            leaderboard.add(new LeaderboardEntry(
                    s.getSubmissionId(),
                    s.getDeveloperUsername(),
                    record.getFinalScore()
            ));
        }

        // Sort descending by final score
        leaderboard.sort((a, b) -> Double.compare(b.getFinalScore(), a.getFinalScore()));

        // Assign 1-based ranks
        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).setRank(i + 1);
        }

        System.out.println("[EvaluationService] Leaderboard generated for challenge '"
                + challengeId + "' — " + leaderboard.size() + " entry(ies) ranked.");

        return leaderboard;
    }

    /**
     * Publishes final results of a challenge by printing the
     * ranked leaderboard to the console.
     *
     * In Phase 2+, this would trigger email notifications and
     * persist results to a database.
     *
     * @param challengeId The challenge whose results are published.
     * @param leaderboard Sorted leaderboard from generateLeaderboard().
     */
    public void publishResults(String challengeId, List<LeaderboardEntry> leaderboard) {

        System.out.println("============================================================");
        System.out.println("  SolveStack – FINAL RESULTS");
        System.out.println("  Challenge ID : " + challengeId);
        System.out.println("------------------------------------------------------------");

        if (leaderboard == null || leaderboard.isEmpty()) {
            System.out.println("  No evaluated submissions to publish for this challenge.");
        } else {
            System.out.printf("  %-6s %-14s %-22s %s%n",
                    "Rank", "Submission ID", "Developer", "Final Score");
            System.out.println("  " + "-".repeat(58));
            for (LeaderboardEntry entry : leaderboard) {
                System.out.printf("  %-6d %-14s %-22s %.2f / 100%n",
                        entry.getRank(),
                        entry.getSubmissionId(),
                        entry.getDeveloperUsername(),
                        entry.getFinalScore());
            }
        }

        System.out.println("============================================================");
        System.out.println("[EvaluationService] Results published for challenge: " + challengeId);
    }

    // ─────────────────────────────────────────────────────────
    // QUERY / UTILITY METHODS
    // ─────────────────────────────────────────────────────────

    /**
     * Retrieves the EvaluationRecord for a specific submission.
     *
     * @param submissionId The submission's unique ID.
     * @return The EvaluationRecord, or null if not yet evaluated.
     */
    public EvaluationRecord getEvaluationRecord(String submissionId) {
        EvaluationRecord record = evaluationRecords.get(submissionId);
        if (record == null) {
            System.out.println("[EvaluationService] No evaluation record found for: " + submissionId);
        }
        return record;
    }

    /**
     * Returns the total number of evaluations completed.
     *
     * @return Count of all stored EvaluationRecords.
     */
    public int getTotalEvaluationsCount() {
        return evaluationRecords.size();
    }

    /**
     * Returns how many submissions have been assigned to a given evaluator.
     *
     * @param evaluatorUsername The evaluator's username.
     * @return Number of submissions assigned.
     */
    public int getEvaluatorWorkload(String evaluatorUsername) {
        return evaluatorWorkload.getOrDefault(evaluatorUsername, 0);
    }

    /**
     * Checks whether a submission has already been evaluated.
     *
     * @param submissionId The submission's unique ID.
     * @return true if an EvaluationRecord exists for it.
     */
    public boolean isEvaluated(String submissionId) {
        return evaluationRecords.containsKey(submissionId);
    }

    /**
     * Prints a formatted summary of all evaluation records.
     * Useful for Admin oversight and platform-wide reporting.
     */
    public void printAllEvaluations() {
        System.out.println("============================================================");
        System.out.println("  SolveStack – All Evaluations (" + evaluationRecords.size() + " total)");
        System.out.println("------------------------------------------------------------");

        if (evaluationRecords.isEmpty()) {
            System.out.println("  No evaluations have been completed yet.");
        } else {
            for (EvaluationRecord r : evaluationRecords.values()) {
                System.out.println("  Submission   : " + r.getSubmissionId());
                System.out.println("  Evaluator    : " + r.getEvaluatorUsername());
                System.out.println("  Innovation   : " + r.getInnovationScore());
                System.out.println("  Feasibility  : " + r.getFeasibilityScore());
                System.out.println("  Completeness : " + r.getCompletenessScore());
                System.out.println("  Presentation : " + r.getPresentationScore());
                System.out.println("  Final Score  : " + r.getFinalScore() + " / 100");
                System.out.println("  Feedback     : " + r.getFeedback());
                System.out.println("  " + "-".repeat(56));
            }
        }
        System.out.println("============================================================");
    }

    // ─────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────

    /**
     * Validates that a score is within the allowed range [0, 100].
     *
     * @param score The score to check.
     * @return true if between 0 and 100 inclusive.
     */
    private boolean isValidScore(double score) {
        return score >= 0 && score <= 100;
    }

    // =========================================================
    // STATIC INNER CLASS: EvaluationRecord
    // =========================================================

    /**
     * ============================================================
     * Immutable data record that stores the complete result of
     * one evaluation — all four dimension scores, the weighted
     * final score, and the evaluator's written feedback.
     *
     * OOP Principles Applied:
     * ─────────────────────────────────────────────────────────
     * ✔ Encapsulation – All fields are private and final.
     *                   No setters — once created, cannot change.
     * ============================================================
     */
    public static class EvaluationRecord {

        // ── Private final fields ─────────────────────────────

        private final String submissionId;
        private final String evaluatorUsername;
        private final double innovationScore;
        private final double feasibilityScore;
        private final double completenessScore;
        private final double presentationScore;
        private final double finalScore;
        private final String feedback;

        // ── Constructor ──────────────────────────────────────

        /**
         * Creates an immutable EvaluationRecord.
         * Called only by EvaluationService.evaluateSubmission().
         *
         * @param submissionId       ID of the evaluated submission.
         * @param evaluatorUsername  Username of the evaluator.
         * @param innovationScore    Originality score    (0–100).
         * @param feasibilityScore   Feasibility score    (0–100).
         * @param completenessScore  Completeness score   (0–100).
         * @param presentationScore  Presentation score   (0–100).
         * @param finalScore         Weighted final score (0.00–100.00).
         * @param feedback           Written evaluator feedback.
         */
        public EvaluationRecord(String submissionId,
                                String evaluatorUsername,
                                double innovationScore,
                                double feasibilityScore,
                                double completenessScore,
                                double presentationScore,
                                double finalScore,
                                String feedback) {

            this.submissionId      = submissionId;
            this.evaluatorUsername = evaluatorUsername;
            this.innovationScore   = innovationScore;
            this.feasibilityScore  = feasibilityScore;
            this.completenessScore = completenessScore;
            this.presentationScore = presentationScore;
            this.finalScore        = finalScore;
            this.feedback          = feedback;
        }

        // ── Getters (no setters — immutable) ─────────────────

        public String getSubmissionId()       { return submissionId;      }
        public String getEvaluatorUsername()  { return evaluatorUsername; }
        public double getInnovationScore()    { return innovationScore;   }
        public double getFeasibilityScore()   { return feasibilityScore;  }
        public double getCompletenessScore()  { return completenessScore; }
        public double getPresentationScore()  { return presentationScore; }
        public double getFinalScore()         { return finalScore;        }
        public String getFeedback()           { return feedback;          }

        // ── toString ─────────────────────────────────────────

        @Override
        public String toString() {
            return String.format(
                    "EvaluationRecord{submission='%s', evaluator='%s', "
                            + "innovation=%.1f, feasibility=%.1f, completeness=%.1f, "
                            + "presentation=%.1f, finalScore=%.2f}",
                    submissionId, evaluatorUsername,
                    innovationScore, feasibilityScore,
                    completenessScore, presentationScore,
                    finalScore
            );
        }
    }

    // =========================================================
    // STATIC INNER CLASS: LeaderboardEntry
    // =========================================================

    /**
     * ============================================================
     * Represents one ranked entry in a challenge leaderboard.
     * Holds the submission ID, developer username, final score,
     * and rank position (assigned after sorting by score).
     *
     * OOP Principles Applied:
     * ─────────────────────────────────────────────────────────
     * ✔ Encapsulation – All fields private. submissionId,
     *                   developerUsername, and finalScore are
     *                   final. rank has a setter because it is
     *                   assigned after the list is sorted.
     * ============================================================
     */
    public static class LeaderboardEntry {

        // ── Fields ───────────────────────────────────────────

        private int    rank;
        private final String submissionId;
        private final String developerUsername;
        private final double finalScore;

        // ── Constructor ──────────────────────────────────────

        /**
         * Creates a LeaderboardEntry without a rank.
         * Rank is set later by generateLeaderboard() after sorting.
         *
         * @param submissionId       ID of the evaluated submission.
         * @param developerUsername  Username of the developer.
         * @param finalScore         Final weighted score (0.00–100.00).
         */
        public LeaderboardEntry(String submissionId,
                                String developerUsername,
                                double finalScore) {
            this.submissionId      = submissionId;
            this.developerUsername = developerUsername;
            this.finalScore        = finalScore;
            this.rank              = 0; // Unranked until generateLeaderboard() sets it
        }

        // ── Getters & Setters ─────────────────────────────────

        public int    getRank()              { return rank;              }
        public String getSubmissionId()      { return submissionId;      }
        public String getDeveloperUsername() { return developerUsername; }
        public double getFinalScore()        { return finalScore;        }

        /**
         * Assigns the rank after sorting. Called only by
         * EvaluationService.generateLeaderboard().
         *
         * @param rank 1-based rank (1 = highest score).
         */
        public void setRank(int rank) { this.rank = rank; }

        // ── toString ─────────────────────────────────────────

        @Override
        public String toString() {
            return String.format(
                    "LeaderboardEntry{rank=%d, submission='%s', developer='%s', score=%.2f}",
                    rank, submissionId, developerUsername, finalScore
            );
        }
    }
}