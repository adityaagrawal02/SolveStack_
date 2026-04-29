package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "solvestack";

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE +
                    "?useSSL=false" +
                    "&serverTimezone=UTC" +
                    "&allowPublicKeyRetrieval=true" +
                    "&useUnicode=true" +
                    "&characterEncoding=UTF-8" +
                    "&autoReconnect=true";

    private static final String USERNAME = "root";
    private static final String PASSWORD = "Atherva@123";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver Loaded Successfully.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "MySQL JDBC Driver not found. Add mysql-connector-j.jar",
                    e
            );
        }
    }

    private DBConnection() {
        throw new UnsupportedOperationException(
                "DBConnection utility class cannot be instantiated."
        );
    }

    public static Connection getConnection() throws SQLException {

        Properties props = new Properties();
        props.setProperty("user", USERNAME);
        props.setProperty("password", PASSWORD);

        Connection connection = DriverManager.getConnection(URL, props);

        if (connection != null && !connection.isClosed()) {
            System.out.println("Database Connected Successfully.");
        }

        return connection;
    }

    public static void close(Connection con) {

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.err.println(
                        "Failed to close DB connection: " + e.getMessage()
                );
            }
        }
    }

    public static void testConnection() {

        try (Connection con = getConnection()) {
            System.out.println("Connection Test Passed.");
        } catch (Exception e) {
            System.err.println("Connection Test Failed.");
            e.printStackTrace();
        }
    }
}