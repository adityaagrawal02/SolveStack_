package ui;

import db.DBConnection;
import models.*;

import java.sql.*;
import java.util.UUID;

/**
 * JDBC-based User Repository for SolveStack
 * Fully synchronized with MySQL schema.
 */
public class UserRepository {

    private static UserRepository instance;

    private UserRepository() {
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    /* =====================================================
       LOGIN
       ===================================================== */
    public User authenticate(String username, String password) {

        if (username == null || password == null ||
                username.isBlank() || password.isBlank()) {
            return null;
        }

        String sql =
                "SELECT user_id, username, email, password_hash, role " +
                        "FROM users WHERE username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username.trim());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String dbPassword = rs.getString("password_hash");

                if (!dbPassword.equals(password)) {
                    return null;
                }

                String userId = rs.getString("user_id");
                String email = rs.getString("email");
                String role = rs.getString("role");

                switch (role.toUpperCase()) {

                    case "COMPANY":
                        return loadCompany(con, userId, username, email, password);

                    case "DEVELOPER":
                        return loadDeveloper(con, userId, username, email, password);

                    case "EVALUATOR":
                        return loadEvaluator(con, userId, username, email, password);

                    case "ADMIN":
                        return loadAdmin(con, userId, username, email, password);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /* =====================================================
       GET USER ROLE
       ===================================================== */
    public String getUserRole(String username) {

        String sql = "SELECT role FROM users WHERE username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return normalizeRole(rs.getString("role"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /* =====================================================
       REGISTER NORMAL USERS
       ===================================================== */
    public boolean registerUser(String username,
                                String email,
                                String password,
                                String role,
                                String dynamicValue,
                                String securityQuestion,
                                String securityAnswer) {

        String userId = generateId(role);

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            String sql =
                    "INSERT INTO users(user_id,username,email,password_hash,role) " +
                            "VALUES(?,?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, userId);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, role.toUpperCase());

            ps.executeUpdate();

            switch (role.toUpperCase()) {

                case "DEVELOPER":
                    insertDeveloper(con, userId, dynamicValue);
                    break;

                case "EVALUATOR":
                    insertEvaluator(con, userId, dynamicValue);
                    break;

                case "ADMIN":
                    insertAdmin(con, userId, dynamicValue);
                    break;
            }

            con.commit();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /* =====================================================
       REGISTER COMPANY
       ===================================================== */
    public boolean registerCompany(String username,
                                   String email,
                                   String password,
                                   String companyName,
                                   String industry,
                                   String securityQuestion,
                                   String securityAnswer) {

        String userId = generateId("COMPANY");

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            String sql =
                    "INSERT INTO users(user_id,username,email,password_hash,role) " +
                            "VALUES(?,?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, userId);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, "COMPANY");

            ps.executeUpdate();

            String companySql =
                    "INSERT INTO companies(user_id,company_name,industry,registration_number) " +
                            "VALUES(?,?,?,?)";

            PreparedStatement cps = con.prepareStatement(companySql);

            cps.setString(1, userId);
            cps.setString(2, companyName);
            cps.setString(3, industry);
            cps.setString(4, "REG-" + System.currentTimeMillis());

            cps.executeUpdate();

            con.commit();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /* =====================================================
       UPDATE PASSWORD
       ===================================================== */
    public boolean updatePassword(String username, String newPassword) {

        String sql =
                "UPDATE users SET password_hash=? WHERE username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, username);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    /* =====================================================
       SECURITY QUESTION MOCK SUPPORT
       ===================================================== */
    public String getSecurityQuestion(String username) {
        return "What is your pet's name?";
    }

    public boolean verifySecurityAnswer(String username, String answer) {
        return answer != null && answer.equalsIgnoreCase("Fluffy");
    }

    /* =====================================================
       PRIVATE LOADERS
       ===================================================== */

    private Company loadCompany(Connection con,
                                String userId,
                                String username,
                                String email,
                                String password) throws Exception {

        String sql =
                "SELECT company_name, industry, registration_number " +
                        "FROM companies WHERE user_id=?";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, userId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            return new Company(
                    userId,
                    username,
                    email,
                    password,
                    rs.getString("company_name"),
                    rs.getString("industry"),
                    rs.getString("registration_number")
            );
        }

        return null;
    }

    private Developer loadDeveloper(Connection con,
                                    String userId,
                                    String username,
                                    String email,
                                    String password) throws Exception {

        String sql =
                "SELECT skill_set FROM developers WHERE user_id=?";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, userId);

        ResultSet rs = ps.executeQuery();

        String skills = rs.next() ? rs.getString("skill_set") : "";

        return new Developer(userId, username, email, password, skills);
    }

    private Evaluator loadEvaluator(Connection con,
                                    String userId,
                                    String username,
                                    String email,
                                    String password) throws Exception {

        String sql =
                "SELECT expertise FROM evaluators WHERE user_id=?";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, userId);

        ResultSet rs = ps.executeQuery();

        String expertise = rs.next() ? rs.getString("expertise") : "";

        return new Evaluator(userId, username, email, password, expertise);
    }

    private Admin loadAdmin(Connection con,
                            String userId,
                            String username,
                            String email,
                            String password) {

        return new Admin(
                userId,
                username,
                email,
                password,
                "STANDARD"
        );
    }

    /* =====================================================
       INSERT HELPERS
       ===================================================== */

    private void insertDeveloper(Connection con,
                                 String userId,
                                 String skills) throws Exception {

        String sql =
                "INSERT INTO developers(user_id,skill_set) VALUES(?,?)";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, userId);
        ps.setString(2, skills);

        ps.executeUpdate();
    }

    private void insertEvaluator(Connection con,
                                 String userId,
                                 String expertise) throws Exception {

        String sql =
                "INSERT INTO evaluators(user_id,expertise) VALUES(?,?)";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, userId);
        ps.setString(2, expertise);

        ps.executeUpdate();
    }

    private void insertAdmin(Connection con,
                             String userId,
                             String level) throws Exception {

        String sql =
                "INSERT INTO admins(user_id,admin_level) VALUES(?,?)";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, userId);
        ps.setString(2, level == null || level.isBlank()
                ? "STANDARD"
                : level);

        ps.executeUpdate();
    }

    /* =====================================================
       UTILITIES
       ===================================================== */

    private String generateId(String role) {

        String prefix = switch (role.toUpperCase()) {
            case "COMPANY" -> "CO";
            case "DEVELOPER" -> "DEV";
            case "EVALUATOR" -> "EVL";
            case "ADMIN" -> "ADM";
            default -> "USR";
        };

        return prefix + "-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 6)
                        .toUpperCase();
    }

    private String normalizeRole(String role) {

        return switch (role.toUpperCase()) {
            case "COMPANY" -> "Company";
            case "DEVELOPER" -> "Developer";
            case "EVALUATOR" -> "Evaluator";
            case "ADMIN" -> "Admin";
            default -> role;
        };
    }

    public boolean updateUserProfile(String oldUsername,
                                     String newUsername,
                                     String newEmail,
                                     String bio) {

        String sql =
                "UPDATE users " +
                        "SET username=?, email=? " +
                        "WHERE username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, newUsername);
            ps.setString(2, newEmail);
            ps.setString(3, oldUsername);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

