package dao;

import db.DBConnection;
import java.sql.*;

/**
 * Data Access Object (DAO) for the {@code users} table.
 *
 * <p>Provides the basic CRUD operations needed for authentication and
 * role-resolution. Higher-level business logic lives in the service layer
 * ({@link ui.UserRepository}); this class only speaks SQL.</p>
 */
public class UserDAO {

    /**
     * Inserts a new user record into the {@code users} table.
     *
     * @param name     The user's display name.
     * @param email    The user's email address.
     * @param password The user's password (plain text — hashed at a higher layer).
     * @param role     The role string, e.g. "DEVELOPER", "COMPANY".
     * @return {@code true} if the INSERT succeeded and at least one row was affected.
     */
    public boolean register(String name, String email, String password, String role) {
        try {
            // Open a connection; this is auto-closed by the try-with-resources block
            // in the caller, but here we manage it manually for simplicity.
            Connection con = DBConnection.getConnection();

            // Parameterised query prevents SQL injection.
            String q = "INSERT INTO users(name,email,password,role) VALUES(?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(q);
            // Bind each '?' placeholder in order.
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);

            // executeUpdate() returns the number of rows affected.
            // A return value > 0 means the insert was successful.
            return ps.executeUpdate() > 0;

        } catch(Exception e) {
            // Print the full stack trace for debugging; return false to signal failure.
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks whether a user with the given credentials exists in the database.
     *
     * <p>This is a simple credential verification: it queries for a row that
     * matches both the email AND the password. If such a row exists, login
     * succeeds.</p>
     *
     * @param email    The user's email address.
     * @param password The password to verify.
     * @return {@code true} if credentials match an existing record.
     */
    public boolean login(String email, String password) {
        try {
            Connection con = DBConnection.getConnection();

            // Select all columns for the matching user; we only need to know
            // whether at least one row exists.
            String q = "SELECT * FROM users WHERE email=? AND password=?";

            PreparedStatement ps = con.prepareStatement(q);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            // rs.next() moves to the first result row and returns true if one exists.
            return rs.next();

        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the role assigned to the user with the given email.
     *
     * <p>Used after a successful login to determine which dashboard to load.</p>
     *
     * @param email The user's email address.
     * @return The role string (e.g. "DEVELOPER"), or an empty string if not found.
     */
    public String getRole(String email) {
        try {
            Connection con = DBConnection.getConnection();

            // Retrieve only the 'role' column to minimise data transfer.
            String q = "SELECT role FROM users WHERE email=?";
            PreparedStatement ps = con.prepareStatement(q);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            // If a row is found, extract and return the role string.
            if(rs.next())
                return rs.getString("role");

        } catch(Exception e) {
            e.printStackTrace();
        }
        // Return empty string instead of null to avoid NullPointerExceptions upstream.
        return "";
    }
}