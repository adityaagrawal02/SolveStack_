package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class that manages JDBC connections to the SolveStack MySQL database.
 *
 * <p>This is a <em>static utility class</em>: it cannot be instantiated and
 * every method is {@code static}. All DAO classes call
 * {@link #getConnection()} to obtain a fresh {@link Connection} and are
 * responsible for closing it (preferably in a try-with-resources block).</p>
 *
 * <p>Connection parameters are hard-coded here for the academic prototype.
 * In a production system these values would be read from environment variables
 * or a configuration file.</p>
 */
public final class DBConnection {

    // ── Connection parameters ──────────────────────────────────────────────
    private static final String HOST     = "localhost"; // MySQL server host
    private static final String PORT     = "3306";      // Default MySQL port
    private static final String DATABASE = "solvestack"; // Target database name

    /**
     * JDBC URL assembled from the individual connection parameters.
     * Query-string options used:
     * <ul>
     *   <li>{@code useSSL=false}               – disable SSL for local dev</li>
     *   <li>{@code serverTimezone=UTC}          – avoid timezone mismatch errors</li>
     *   <li>{@code allowPublicKeyRetrieval=true} – needed with MySQL 8 auth</li>
     *   <li>{@code useUnicode=true} / {@code characterEncoding=UTF-8} – emoji support</li>
     *   <li>{@code autoReconnect=true}          – recover from dropped connections</li>
     * </ul>
     */
    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE +
                    "?useSSL=false" +
                    "&serverTimezone=UTC" +
                    "&allowPublicKeyRetrieval=true" +
                    "&useUnicode=true" +
                    "&characterEncoding=UTF-8" +
                    "&autoReconnect=true";

    private static final String USERNAME = "root";         // MySQL username
    private static final String PASSWORD = "Atherva@123";  // MySQL password

    /**
     * Static initializer – runs once when this class is first loaded by the JVM.
     * Registers the MySQL JDBC driver with the {@link DriverManager} by
     * loading the driver class into memory. If the driver JAR is missing from
     * the classpath, a {@link RuntimeException} is thrown immediately to give
     * a clear early-failure message instead of a cryptic SQL error later.
     */
    static {
        try {
            // Force-load the MySQL driver; registration happens in its static block.
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver Loaded Successfully.");
        } catch (ClassNotFoundException e) {
            // Driver JAR not on classpath – fail fast with a helpful message.
            throw new RuntimeException(
                    "MySQL JDBC Driver not found. Add mysql-connector-j.jar",
                    e
            );
        }
    }

    /**
     * Private constructor prevents instantiation.
     * This class is intentionally a static helper only.
     */
    private DBConnection() {
        throw new UnsupportedOperationException(
                "DBConnection utility class cannot be instantiated."
        );
    }

    /**
     * Opens and returns a new database connection.
     *
     * <p>Each call creates a brand-new {@link Connection}. The caller is
     * responsible for closing it when finished (use try-with-resources).</p>
     *
     * @return An open, ready-to-use {@link Connection}.
     * @throws SQLException if the database is unreachable or credentials fail.
     */
    public static Connection getConnection() throws SQLException {

        // Bundle credentials into a Properties object (instead of URL params)
        // so the password is not embedded in exception stack traces.
        Properties props = new Properties();
        props.setProperty("user", USERNAME);
        props.setProperty("password", PASSWORD);

        // Ask the DriverManager to open a connection using the URL + props.
        Connection connection = DriverManager.getConnection(URL, props);

        // Verify the connection is genuinely open before returning it.
        if (connection != null && !connection.isClosed()) {
            System.out.println("Database Connected Successfully.");
        }

        return connection;
    }

    /**
     * Safely closes a database connection, suppressing any {@link SQLException}
     * that may occur during the close operation (common with already-closed
     * connections).
     *
     * @param con The connection to close. A {@code null} value is silently ignored.
     */
    public static void close(Connection con) {

        if (con != null) {
            try {
                con.close(); // Release the connection back to MySQL
            } catch (SQLException e) {
                // Log but do not propagate – closing errors are non-fatal.
                System.err.println(
                        "Failed to close DB connection: " + e.getMessage()
                );
            }
        }
    }

    /**
     * Convenience method that opens a connection, prints the result, and closes
     * it again. Useful for verifying database connectivity at startup.
     */
    public static void testConnection() {

        // try-with-resources ensures the connection is closed even if an
        // exception is thrown inside the block.
        try (Connection con = getConnection()) {
            System.out.println("Connection Test Passed.");
        } catch (Exception e) {
            System.err.println("Connection Test Failed.");
            e.printStackTrace();
        }
    }
}