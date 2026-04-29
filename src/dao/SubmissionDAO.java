// =============================================
// FILE 1: dao/SubmissionDAO.java
// FINAL VERSION
// =============================================

package dao;

import db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data Access Object (DAO) for the {@code submissions} table.
 *
 * <p>Handles all SQL operations related to developer submissions:
 * creating, reading, evaluating, and counting submissions.
 * Each public method opens and closes its own JDBC connection.</p>
 */
public class SubmissionDAO {

    /**
     * Inserts a new submission into the {@code submissions} table.
     *
     * <p>The submission starts with {@code status = 'SUBMITTED'} and
     * {@code score = 0.00}. The evaluator fills in the score and feedback later.</p>
     *
     * @param challengeId     The ID of the challenge being solved.
     * @param developerId     The user_id of the submitting developer.
     * @param solutionSummary A short description of the solution.
     * @param githubLink      Optional GitHub repository link.
     * @param documentPath    Optional path to an uploaded supporting document.
     * @return {@code true} if the INSERT affected at least one row.
     */
    public boolean submitSolution(String challengeId,
                                  String developerId,
                                  String solutionSummary,
                                  String githubLink,
                                  String documentPath) {

        // Column list explicitly named so future schema changes don't break the query.
        String sql =
                "INSERT INTO submissions(" +
                        "submission_id,challenge_id,developer_id," +
                        "solution_summary,github_link,document_path," +
                        "status,score) VALUES(?,?,?,?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, generateSubmissionId()); // e.g. "SUB-A1B2C3D4"
            ps.setString(2, challengeId);
            ps.setString(3, developerId);
            ps.setString(4, solutionSummary);
            ps.setString(5, githubLink);
            ps.setString(6, documentPath);
            ps.setString(7, "SUBMITTED"); // initial lifecycle status
            ps.setDouble(8, 0.00);        // score starts at zero until evaluated

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all submissions from every developer and challenge, ordered by
     * submission timestamp (newest first).
     *
     * <p>Used by the Admin dashboard for platform-wide oversight.</p>
     *
     * @return A list of String arrays:
     *         {@code [submission_id, challenge_id, developer_id,
     *                  solution_summary, status, score, submitted_at]}.
     */
    public List<String[]> getAllSubmissions() {

        List<String[]> list =
                new ArrayList<>();

        // Fetch all columns needed for display; ordered DESC so newest is first.
        String sql =
                "SELECT submission_id,challenge_id,developer_id," +
                        "solution_summary,status,score,submitted_at " +
                        "FROM submissions " +
                        "ORDER BY submitted_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) { // execute without parameters

            // Collect each row as a fixed-size String array.
            while (rs.next()) {

                list.add(new String[]{
                        rs.getString("submission_id"),
                        rs.getString("challenge_id"),
                        rs.getString("developer_id"),
                        rs.getString("solution_summary"),
                        rs.getString("status"),
                        rs.getString("score"),
                        rs.getString("submitted_at")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Retrieves all submissions for a specific challenge.
     *
     * <p>Used by companies and evaluators to see who has submitted to a
     * particular challenge.</p>
     *
     * @param challengeId The ID of the challenge.
     * @return A list of String arrays:
     *         {@code [submission_id, developer_id, solution_summary, status, score]}.
     */
    public List<String[]> getSubmissionsByChallenge(
            String challengeId) {

        List<String[]> list =
                new ArrayList<>();

        String sql =
                "SELECT submission_id,developer_id," +
                        "solution_summary,status,score " +
                        "FROM submissions WHERE challenge_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, challengeId); // filter by the given challenge

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                list.add(new String[]{
                        rs.getString("submission_id"),
                        rs.getString("developer_id"),
                        rs.getString("solution_summary"),
                        rs.getString("status"),
                        rs.getString("score")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Retrieves all submissions made by a specific developer.
     *
     * <p>Used to build the "My Submissions" section on the Developer dashboard.</p>
     *
     * @param developerId The user_id of the developer.
     * @return A list of String arrays:
     *         {@code [submission_id, challenge_id, solution_summary, status, score]}.
     */
    public List<String[]> getSubmissionsByDeveloper(
            String developerId) {

        List<String[]> list =
                new ArrayList<>();

        String sql =
                "SELECT submission_id,challenge_id," +
                        "solution_summary,status,score " +
                        "FROM submissions WHERE developer_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, developerId); // filter by the given developer

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                list.add(new String[]{
                        rs.getString("submission_id"),
                        rs.getString("challenge_id"),
                        rs.getString("solution_summary"),
                        rs.getString("status"),
                        rs.getString("score")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Updates a submission's score, evaluator feedback, and status after
     * an evaluator has reviewed it.
     *
     * @param submissionId The ID of the submission to update.
     * @param score        The numeric score (0–100).
     * @param feedback     Written feedback from the evaluator.
     * @param status       The new status string, e.g. "ACCEPTED" or "REJECTED".
     * @return {@code true} if the UPDATE affected at least one row.
     */
    public boolean evaluateSubmission(
            String submissionId,
            double score,
            String feedback,
            String status) {

        // Update three columns in a single statement for atomicity.
        String sql =
                "UPDATE submissions " +
                        "SET score=?, evaluator_feedback=?, status=? " +
                        "WHERE submission_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, score);        // numeric score
            ps.setString(2, feedback);     // evaluator's written comments
            ps.setString(3, status);       // new lifecycle status
            ps.setString(4, submissionId); // target submission

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks whether a developer has already submitted to a given challenge.
     *
     * <p>Prevents duplicate submissions — a developer may submit only once
     * per challenge.</p>
     *
     * @param challengeId The challenge ID.
     * @param developerId The developer's user_id.
     * @return {@code true} if a matching submission row already exists.
     */
    public boolean hasSubmitted(String challengeId,
                                String developerId) {

        // We only need to know if at least one row exists, not its contents.
        String sql =
                "SELECT submission_id " +
                        "FROM submissions " +
                        "WHERE challenge_id=? AND developer_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, challengeId);
            ps.setString(2, developerId);

            ResultSet rs = ps.executeQuery();

            // rs.next() returns true if at least one row was found.
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the total number of submissions across the entire platform.
     *
     * @return Row count, or 0 on error.
     */
    public int getTotalSubmissions() {

        String sql =
                "SELECT COUNT(*) FROM submissions";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // COUNT(*) always returns exactly one row; get value from column 1.
            if (rs.next())
                return rs.getInt(1);

        } catch (Exception ignored) {
        }

        return 0;
    }

    /**
     * Generates a unique submission ID with prefix {@code "SUB-"} followed by
     * 8 upper-cased hex characters from a random UUID.
     *
     * <p>Example output: {@code "SUB-A1B2C3D4"}.</p>
     *
     * @return A freshly generated, effectively unique submission ID.
     */
    private String generateSubmissionId() {

        return "SUB-" +
                UUID.randomUUID()     // create a random UUID
                        .toString()
                        .substring(0, 8) // take just the first 8 characters
                        .toUpperCase();  // uppercase for consistency
    }
}