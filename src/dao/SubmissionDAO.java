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

public class SubmissionDAO {

    public boolean submitSolution(String challengeId,
                                  String developerId,
                                  String solutionSummary,
                                  String githubLink,
                                  String documentPath) {

        String sql =
                "INSERT INTO submissions(" +
                        "submission_id,challenge_id,developer_id," +
                        "solution_summary,github_link,document_path," +
                        "status,score) VALUES(?,?,?,?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, generateSubmissionId());
            ps.setString(2, challengeId);
            ps.setString(3, developerId);
            ps.setString(4, solutionSummary);
            ps.setString(5, githubLink);
            ps.setString(6, documentPath);
            ps.setString(7, "SUBMITTED");
            ps.setDouble(8, 0.00);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String[]> getAllSubmissions() {

        List<String[]> list =
                new ArrayList<>();

        String sql =
                "SELECT submission_id,challenge_id,developer_id," +
                        "solution_summary,status,score,submitted_at " +
                        "FROM submissions " +
                        "ORDER BY submitted_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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

            ps.setString(1, challengeId);

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

            ps.setString(1, developerId);

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

    public boolean evaluateSubmission(
            String submissionId,
            double score,
            String feedback,
            String status) {

        String sql =
                "UPDATE submissions " +
                        "SET score=?, evaluator_feedback=?, status=? " +
                        "WHERE submission_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, score);
            ps.setString(2, feedback);
            ps.setString(3, status);
            ps.setString(4, submissionId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }
    public boolean hasSubmitted(String challengeId,
                                String developerId) {

        String sql =
                "SELECT submission_id " +
                        "FROM submissions " +
                        "WHERE challenge_id=? AND developer_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, challengeId);
            ps.setString(2, developerId);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getTotalSubmissions() {

        String sql =
                "SELECT COUNT(*) FROM submissions";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next())
                return rs.getInt(1);

        } catch (Exception ignored) {
        }

        return 0;
    }

    private String generateSubmissionId() {

        return "SUB-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }
}