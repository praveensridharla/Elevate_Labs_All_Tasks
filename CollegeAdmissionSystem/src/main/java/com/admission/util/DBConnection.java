package com.admission.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

/**
 * Singleton database connection manager using JDBC.
 */
public class DBConnection {

    private static final String CONFIG_FILE = "/db.properties";
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try (InputStream is = DBConnection.class.getResourceAsStream(CONFIG_FILE)) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                URL      = props.getProperty("db.url",      "jdbc:mysql://localhost:3306/college_admission?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
                USER     = props.getProperty("db.user",     "root");
                PASSWORD = props.getProperty("db.password", "root");
            } else {
                // Fallback defaults
                URL      = "jdbc:mysql://localhost:3306/college_admission?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                USER     = "root";
                PASSWORD = "root";
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize DB connection: " + e.getMessage(), e);
        }
    }

    private DBConnection() {}

    /**
     * Returns a new Connection each call.
     * Callers are responsible for closing it (use try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /** Quick connectivity test. */
    public static boolean testConnection() {
        try (Connection c = getConnection()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
