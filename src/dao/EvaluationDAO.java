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

public class EvaluationDAO {

    /* ==========================================
       EVALUATE SUBMISSION
       ========================================== */

    public boolean evaluateSubmission(String submissionId,
                                      double score,
                                      String feedback) {

        String sql =
                "UPDATE submissions " +
                        "SET score=?, " +
                        "evaluator_feedback=?, " +
                        "status=? " +
                        "WHERE submission_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setDouble(1, score);
            ps.setString(2, feedback);
            ps.setString(3, "ACCEPTED");
            ps.setString(4, submissionId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ==========================================
       REJECT SUBMISSION
       ========================================== */

    public boolean rejectSubmission(String submissionId,
                                    String feedback) {

        String sql =
                "UPDATE submissions " +
                        "SET evaluator_feedback=?, " +
                        "status=? " +
                        "WHERE submission_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, feedback);
            ps.setString(2, "REJECTED");
            ps.setString(3, submissionId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    /* ==========================================
       GET PENDING SUBMISSIONS
       Only for evaluator's assigned challenges
       ========================================== */

    public List<String[]> getPendingSubmissions(
            String evaluatorId
    ) {

        List<String[]> list = new ArrayList<>();

        String sql =
                "SELECT s.submission_id, " +
                        "s.challenge_id, " +
                        "s.developer_id, " +
                        "s.solution_summary, " +
                        "s.github_link, " +
                        "s.submitted_at " +
                        "FROM submissions s " +
                        "JOIN challenges c " +
                        "ON s.challenge_id = c.challenge_id " +
                        "WHERE c.assigned_evaluator_id=? " +
                        "AND s.status IN " +
                        "('SUBMITTED','UNDER_REVIEW') " +
                        "ORDER BY s.submitted_at ASC";

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

    public List<String[]> getReviewedSubmissions(
            String evaluatorId
    ) {

        List<String[]> list = new ArrayList<>();

        String sql =
                "SELECT s.submission_id, " +
                        "s.challenge_id, " +
                        "s.developer_id, " +
                        "s.status, " +
                        "s.score, " +
                        "s.evaluator_feedback " +
                        "FROM submissions s " +
                        "JOIN challenges c " +
                        "ON s.challenge_id = c.challenge_id " +
                        "WHERE c.assigned_evaluator_id=? " +
                        "AND s.status IN " +
                        "('ACCEPTED','REJECTED') " +
                        "ORDER BY s.updated_at DESC";

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
                        "WHERE submission_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, submissionId);

            ResultSet rs = ps.executeQuery();

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

        return null;
    }

    /* ==========================================
       TOTAL REVIEWS COUNT
       ========================================== */

    public int getTotalReviewed(
            String evaluatorId
    ) {

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

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception ignored) {
        }

        return 0;
    }
}