package dao;

import db.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 FULL JDBC CHALLENGE DAO
 Synced with current SQL schema
*/

/**
 * Data Access Object (DAO) for the {@code challenges} table.
 *
 * <p>All database reads and writes for Challenge records go through this class.
 * Each method opens its own connection via try-with-resources so connections
 * are always closed even when exceptions occur.</p>
 */
public class ChallengeDAO {

    /* =====================================================
       CREATE CHALLENGE
       ===================================================== */

    /**
     * Inserts a new challenge row into the {@code challenges} table.
     *
     * <p>The challenge is automatically given:
     * <ul>
     *   <li>A UUID-based ID (see {@link #generateChallengeId()}).</li>
     *   <li>{@code posted_date} = today.</li>
     *   <li>{@code deadline} = 30 days from today.</li>
     *   <li>{@code status} = "OPEN".</li>
     * </ul>
     * </p>
     *
     * @param title       Short challenge title.
     * @param description Full problem statement.
     * @param companyId   The user_id of the posting company.
     * @param reward      Prize amount in the platform currency.
     * @return {@code true} if the INSERT affected at least one row.
     */
    public boolean addChallenge(String title,
                                String description,
                                String companyId,
                                double reward) {

        // Build the INSERT statement with named column list for clarity.
        String sql =
                "INSERT INTO challenges(" +
                        "challenge_id," +
                        "title," +
                        "description," +
                        "company_id," +
                        "prize_amount," +
                        "posted_date," +
                        "deadline," +
                        "status" +
                        ") VALUES(?,?,?,?,?,?,?,?)";

        // try-with-resources: connection and statement are closed automatically.
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, generateChallengeId()); // unique ID like "CH-A1B2C3D4"
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, companyId);
            ps.setDouble(5, reward);
            ps.setDate(6, Date.valueOf(LocalDate.now()));            // today
            ps.setDate(7, Date.valueOf(LocalDate.now().plusDays(30))); // +30 days
            ps.setString(8, "OPEN"); // new challenges start as OPEN

            // executeUpdate() returns affected-row count; > 0 means success.
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* =====================================================
       GET ALL OPEN CHALLENGES
       ===================================================== */

    /**
     * Retrieves every challenge with {@code status = 'OPEN'}, ordered by the
     * most recently created first.
     *
     * @return A list of String arrays, each containing:
     *         {@code [challenge_id, title, company_id, prize_amount, deadline]}.
     */
    public List<String[]> getAllOpenChallenges() {

        List<String[]> list = new ArrayList<>();

        // Only return OPEN challenges, sorted newest first.
        String sql =
                "SELECT challenge_id,title,company_id,prize_amount,deadline " +
                        "FROM challenges WHERE status='OPEN' " +
                        "ORDER BY created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) { // execute query and get results

            // Iterate over every returned row and collect it as a String array.
            while (rs.next()) {

                list.add(new String[]{
                        rs.getString("challenge_id"),
                        rs.getString("title"),
                        rs.getString("company_id"),
                        rs.getString("prize_amount"),
                        rs.getString("deadline")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /* =====================================================
       GET CHALLENGES BY COMPANY
       ===================================================== */

    /**
     * Retrieves all challenges posted by a specific company (any status).
     *
     * @param companyId The user_id of the company.
     * @return A list of String arrays:
     *         {@code [challenge_id, title, status, prize_amount, deadline]}.
     */
    public List<String[]> getChallengesByCompany(String companyId) {

        List<String[]> list = new ArrayList<>();

        // Filter by company_id; include all statuses so the company can see
        // its own closed/under-review challenges too.
        String sql =
                "SELECT challenge_id,title,status,prize_amount,deadline " +
                        "FROM challenges WHERE company_id=? " +
                        "ORDER BY created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, companyId); // bind the company ID parameter

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                list.add(new String[]{
                        rs.getString("challenge_id"),
                        rs.getString("title"),
                        rs.getString("status"),
                        rs.getString("prize_amount"),
                        rs.getString("deadline")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /* =====================================================
       CLOSE CHALLENGE
       ===================================================== */

    /**
     * Sets the status of a challenge to {@code 'CLOSED'}, preventing further
     * submission.
     *
     * @param challengeId The unique ID of the challenge to close.
     * @return {@code true} if a row was updated (i.e., the ID existed).
     */
    public boolean closeChallenge(String challengeId) {

        String sql =
                "UPDATE challenges SET status='CLOSED' " +
                        "WHERE challenge_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, challengeId);

            // Returns the number of rows changed; > 0 means the challenge was found.
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    /* =====================================================
       ASSIGN EVALUATOR
       ===================================================== */

    /**
     * Assigns an evaluator to a challenge and transitions its status to
     * {@code 'UNDER_REVIEW'}.
     *
     * <p>This is called by the Admin when submissions are ready for scoring.</p>
     *
     * @param challengeId ID of the challenge to assign.
     * @param evaluatorId user_id of the evaluator.
     * @return {@code true} if the update succeeded.
     */
    public boolean assignEvaluator(String challengeId,
                                   String evaluatorId) {

        // Update both the evaluator column AND the status in one statement.
        String sql =
                "UPDATE challenges " +
                        "SET assigned_evaluator_id=?, status='UNDER_REVIEW' " +
                        "WHERE challenge_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, evaluatorId);  // the new evaluator
            ps.setString(2, challengeId);  // the target challenge

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    /* =====================================================
       SEARCH CHALLENGES
       ===================================================== */

    /**
     * Performs a case-insensitive keyword search across challenge titles and
     * descriptions using SQL {@code LIKE} patterns.
     *
     * @param keyword The search term entered by the user.
     * @return A list of String arrays:
     *         {@code [challenge_id, title, company_id, prize_amount]}.
     */
    public List<String[]> searchChallenges(String keyword) {

        List<String[]> list = new ArrayList<>();

        // LIKE with % wildcards matches anywhere in title or description.
        String sql =
                "SELECT challenge_id,title,company_id,prize_amount " +
                        "FROM challenges " +
                        "WHERE title LIKE ? OR description LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Wrap keyword with % so it matches anywhere in the string.
            String search = "%" + keyword + "%";

            ps.setString(1, search); // applied to title column
            ps.setString(2, search); // applied to description column

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                list.add(new String[]{
                        rs.getString("challenge_id"),
                        rs.getString("title"),
                        rs.getString("company_id"),
                        rs.getString("prize_amount")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /* =====================================================
       COUNT METHODS
       ===================================================== */

    /**
     * Returns the total number of challenge rows across all statuses.
     *
     * @return Row count, or 0 if the query fails.
     */
    public int getTotalChallenges() {

        String sql = "SELECT COUNT(*) FROM challenges"; // aggregate over all rows

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // COUNT(*) always returns exactly one row; column index 1 holds the value.
            if (rs.next()) return rs.getInt(1);

        } catch (Exception ignored) {
        }

        return 0; // default safe value
    }

    /**
     * Returns the number of currently open challenges.
     *
     * @return Count of challenges with {@code status = 'OPEN'}, or 0 on error.
     */
    public int getOpenChallengesCount() {

        String sql =
                "SELECT COUNT(*) FROM challenges WHERE status='OPEN'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (Exception ignored) {
        }

        return 0;
    }

    /* =====================================================
       UTIL
       ===================================================== */

    /**
     * Generates a unique challenge ID with the prefix {@code "CH-"} followed
     * by the first 8 characters of a random UUID (upper-cased).
     *
     * <p>Example output: {@code "CH-A1B2C3D4"}.</p>
     *
     * @return A newly generated, effectively unique challenge ID string.
     */
    private String generateChallengeId() {

        return "CH-" +
                UUID.randomUUID()       // generate a random UUID, e.g. "a1b2c3d4-..."
                        .toString()
                        .substring(0, 8) // take only the first 8 hex characters
                        .toUpperCase();  // convert to uppercase for readability
    }
}