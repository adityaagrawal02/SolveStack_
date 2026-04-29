package dao;

import db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 FULL JDBC EVALUATION DAO
 Synced with current SolveStack schema

 Uses:
 - challenges.assigned_evaluator_id
 - submissions.score
 - submissions.evaluator_feedback
 - submissions.status
*/

/**
 * Data Access Object (DAO) that handles all evaluation-related SQL operations.
 *
 * <p>An evaluator is assigned to a challenge via
 * {@link ChallengeDAO#assignEvaluator(String, String)}. After that, the
 * evaluator works through this DAO to accept, reject, or review submissions
 * that belong to their assigned challenges.</p>
 */
public class EvaluationDAO {

    /* ==========================================
       EVALUATE SUBMISSION
       ========================================== */

    /**
     * Records a positive evaluation for a submission by setting its score,
     * feedback, and status to {@code 'ACCEPTED'}.
     *
     * <p>This method always marks the submission as ACCEPTED. Use
     * {@link #rejectSubmission(String, String)} to reject.</p>
     *
     * @param submissionId The unique ID of the submission to accept.
     * @param score        Numeric score assigned by the evaluator (0–100).
     * @param feedback     Written evaluator feedback for the developer.
     * @return {@code true} if the UPDATE affected at least one row.
     */
    public boolean evaluateSubmission(String submissionId,
                                      double score,
                                      String feedback) {

        // Update score, feedback, and hard-code status to ACCEPTED.
        String sql =
                "UPDATE submissions " +
                        "SET score=?, " +
                        "evaluator_feedback=?, " +
                        "status=? " +
                        "WHERE submission_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setDouble(1, score);        // evaluator's numeric score
            ps.setString(2, feedback);     // evaluator's written comment
            ps.setString(3, "ACCEPTED");   // hard-coded acceptance status
            ps.setString(4, submissionId); // which submission to update

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ==========================================
       REJECT SUBMISSION
       ========================================== */

    /**
     * Marks a submission as {@code 'REJECTED'} and records the evaluator's
     * feedback. No score is set during rejection.
     *
     * @param submissionId The unique ID of the submission to reject.
     * @param feedback     Reason for rejection written by the evaluator.
     * @return {@code true} if the UPDATE affected at least one row.
     */
    public boolean rejectSubmission(String submissionId,
                                    String feedback) {

        // Only update the feedback and status; score is left unchanged.
        String sql =
                "UPDATE submissions " +
                        "SET evaluator_feedback=?, " +
                        "status=? " +
                        "WHERE submission_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, feedback);     // rejection reason
            ps.setString(2, "REJECTED");   // hard-coded rejection status
            ps.setString(3, submissionId); // target submission

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    /* ==========================================
       GET PENDING SUBMISSIONS
       Only for evaluator's assigned challenges
       ========================================== */

    /**
     * Returns all submissions that are waiting to be evaluated by the given
     * evaluator.
     *
     * <p>A submission is "pending" when its status is either
     * {@code 'SUBMITTED'} or {@code 'UNDER_REVIEW'} AND the parent challenge
     * has been assigned to this evaluator. The JOIN on the {@code challenges}
     * table filters out submissions that belong to other evaluators.</p>
     *
     * @param evaluatorId The user_id of the evaluator.
     * @return A list of String arrays:
     *         {@code [submission_id, challenge_id, developer_id,
     *                  solution_summary, github_link, submitted_at]}.
     */
    public List<String[]> getPendingSubmissions(
            String evaluatorId
    ) {

        List<String[]> list = new ArrayList<>();

        // JOIN challenges so we can filter by assigned_evaluator_id.
        // Status IN (...) picks up both freshly submitted and partially reviewed entries.
        String sql =
                "SELECT s.submission_id, " +
                        "s.challenge_id, " +
                        "s.developer_id, " +
                        "s.solution_summary, " +
                        "s.github_link, " +
                        "s.submitted_at " +
                        "FROM submissions s " +
                        "JOIN challenges c " +
                        "ON s.challenge_id = c.challenge_id " +  // link submission → challenge
                        "WHERE c.assigned_evaluator_id=? " +      // only this evaluator's challenges
                        "AND s.status IN " +
                        "('SUBMITTED','UNDER_REVIEW') " +          // not yet finalized
                        "ORDER BY s.submitted_at ASC";             // oldest first (FIFO review)

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, evaluatorId); // bind the evaluator's ID

            ResultSet rs = ps.executeQuery();

            // Map each row to a String array and add it to the result list.
            while (rs.next()) {

                list.add(new String[]{
                        rs.getString("submission_id"),
                        rs.getString("challenge_id"),
                        rs.getString("developer_id"),
                        rs.getString("solution_summary"),
                        rs.getString("github_link"),
                        rs.getString("submitted_at")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /* ==========================================
       GET ALL EVALUATED SUBMISSIONS
       ========================================== */

    /**
     * Returns all submissions that this evaluator has already finalized
     * (ACCEPTED or REJECTED), ordered by the most recently updated first.
     *
     * @param evaluatorId The user_id of the evaluator.
     * @return A list of String arrays:
     *         {@code [submission_id, challenge_id, developer_id,
     *                  status, score, evaluator_feedback]}.
     */
    public List<String[]> getReviewedSubmissions(
            String evaluatorId
    ) {

        List<String[]> list = new ArrayList<>();

        // Similar JOIN structure; status IN filters to finalized submissions only.
        String sql =
                "SELECT s.submission_id, " +
                        "s.challenge_id, " +
                        "s.developer_id, " +
                        "s.status, " +
                        "s.score, " +
                        "s.evaluator_feedback " +
                        "FROM submissions s " +
                        "JOIN challenges c " +
                        "ON s.challenge_id = c.challenge_id " + // submission ↔ challenge link
                        "WHERE c.assigned_evaluator_id=? " +     // this evaluator's challenges
                        "AND s.status IN " +
                        "('ACCEPTED','REJECTED') " +              // only completed reviews
                        "ORDER BY s.updated_at DESC";             // newest review first

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, evaluatorId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                list.add(new String[]{
                        rs.getString("submission_id"),
                        rs.getString("challenge_id"),
                        rs.getString("developer_id"),
                        rs.getString("status"),
                        rs.getString("score"),
                        rs.getString("evaluator_feedback")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /* ==========================================
       GET SINGLE SUBMISSION DETAILS
       ========================================== */

    /**
     * Fetches the full details of a single submission by its ID.
     *
     * <p>Used by the evaluation UI to pre-populate the review form with the
     * submission's current state before the evaluator makes changes.</p>
     *
     * @param submissionId The unique ID of the submission.
     * @return A String array {@code [submission_id, challenge_id, developer_id,
     *         solution_summary, github_link, status, score]}, or {@code null}
     *         if no matching row is found.
     */
    public String[] getSubmissionById(
            String submissionId
    ) {

        String sql =
                "SELECT submission_id, " +
                        "challenge_id, " +
                        "developer_id, " +
                        "solution_summary, " +
                        "github_link, " +
                        "status, score " +
                        "FROM submissions " +
                        "WHERE submission_id=?"; // exact match by primary key

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, submissionId);

            ResultSet rs = ps.executeQuery();

            // If a row exists, extract all columns into a String array.
            if (rs.next()) {

                return new String[]{
                        rs.getString("submission_id"),
                        rs.getString("challenge_id"),
                        rs.getString("developer_id"),
                        rs.getString("solution_summary"),
                        rs.getString("github_link"),
                        rs.getString("status"),
                        rs.getString("score")
                };
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // submission not found
    }

    /* ==========================================
       TOTAL REVIEWS COUNT
       ========================================== */

    /**
     * Returns the total number of submissions this evaluator has finalized
     * (ACCEPTED + REJECTED combined).
     *
     * <p>Used to show a "reviews completed" statistic on the evaluator's
     * dashboard.</p>
     *
     * @param evaluatorId The user_id of the evaluator.
     * @return The count of completed reviews, or 0 on error.
     */
    public int getTotalReviewed(
            String evaluatorId
    ) {

        // COUNT the rows where the evaluator has finalized the review.
        String sql =
                "SELECT COUNT(*) " +
                        "FROM submissions s " +
                        "JOIN challenges c " +
                        "ON s.challenge_id = c.challenge_id " +
                        "WHERE c.assigned_evaluator_id=? " +
                        "AND s.status IN " +
                        "('ACCEPTED','REJECTED')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, evaluatorId);

            ResultSet rs = ps.executeQuery();

            // COUNT(*) returns exactly one row; read the integer from column 1.
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception ignored) {
        }

        return 0; // safe fallback
    }
}