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

public class ChallengeDAO {

    /* =====================================================
       CREATE CHALLENGE
       ===================================================== */
    public boolean addChallenge(String title,
                                String description,
                                String companyId,
                                double reward) {

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

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, generateChallengeId());
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, companyId);
            ps.setDouble(5, reward);
            ps.setDate(6, Date.valueOf(LocalDate.now()));
            ps.setDate(7, Date.valueOf(LocalDate.now().plusDays(30)));
            ps.setString(8, "OPEN");

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* =====================================================
       GET ALL OPEN CHALLENGES
       ===================================================== */
    public List<String[]> getAllOpenChallenges() {

        List<String[]> list = new ArrayList<>();

        String sql =
                "SELECT challenge_id,title,company_id,prize_amount,deadline " +
                        "FROM challenges WHERE status='OPEN' " +
                        "ORDER BY created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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
    public List<String[]> getChallengesByCompany(String companyId) {

        List<String[]> list = new ArrayList<>();

        String sql =
                "SELECT challenge_id,title,status,prize_amount,deadline " +
                        "FROM challenges WHERE company_id=? " +
                        "ORDER BY created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, companyId);

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
    public boolean closeChallenge(String challengeId) {

        String sql =
                "UPDATE challenges SET status='CLOSED' " +
                        "WHERE challenge_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, challengeId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    /* =====================================================
       ASSIGN EVALUATOR
       ===================================================== */
    public boolean assignEvaluator(String challengeId,
                                   String evaluatorId) {

        String sql =
                "UPDATE challenges " +
                        "SET assigned_evaluator_id=?, status='UNDER_REVIEW' " +
                        "WHERE challenge_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, evaluatorId);
            ps.setString(2, challengeId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    /* =====================================================
       SEARCH CHALLENGES
       ===================================================== */
    public List<String[]> searchChallenges(String keyword) {

        List<String[]> list = new ArrayList<>();

        String sql =
                "SELECT challenge_id,title,company_id,prize_amount " +
                        "FROM challenges " +
                        "WHERE title LIKE ? OR description LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String search = "%" + keyword + "%";

            ps.setString(1, search);
            ps.setString(2, search);

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
    public int getTotalChallenges() {

        String sql = "SELECT COUNT(*) FROM challenges";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (Exception ignored) {
        }

        return 0;
    }

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
    private String generateChallengeId() {

        return "CH-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }
}