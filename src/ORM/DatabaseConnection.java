package ORM;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5433/agromanager";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";
    private static final String TEST_URL = "jdbc:postgresql://localhost:5433/agromanager_test";
    private static final String TEST_USER = "postgres";
    private static final String TEST_PASSWORD = "password";

    private static boolean isTest = false;

    public static void setTestMode(boolean testMode) {
        isTest = testMode;
    }

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = isTest ? TEST_URL : URL;
            String user = isTest ? TEST_USER : USER;
            String password = isTest ? TEST_PASSWORD : PASSWORD;

            try {
                // Tentativo 1: se il driver è auto-registrato (JDBC 4+), questa chiamata basta.
                connection = DriverManager.getConnection(url, user, password);
            } catch (SQLException first) {
                // Tentativo 2: prova a caricare esplicitamente il driver e ritenta.
                try {
                    Class.forName("org.postgresql.Driver");
                    connection = DriverManager.getConnection(url, user, password);
                } catch (ClassNotFoundException cnfe) {
                    throw new SQLException("Driver PostgreSQL non disponibile nel classpath", cnfe);
                }
            }
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        // Ripristino stato per test successivi
        connection = null;
        isTest = false;
    }
}
