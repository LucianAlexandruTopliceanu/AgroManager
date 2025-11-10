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

    private static volatile DatabaseConnection instance;
    private volatile Connection connection;
    private volatile boolean isTest = false;

    private DatabaseConnection() {}

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public void setTestMode(boolean testMode) {
        synchronized (this) {
            if (this.isTest != testMode) {
                this.isTest = testMode;
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
                }
                connection = null;
            }
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            synchronized (this) {
                if (connection == null || connection.isClosed()) {
                    String url = isTest ? TEST_URL : URL;
                    String user = isTest ? TEST_USER : USER;
                    String password = isTest ? TEST_PASSWORD : PASSWORD;

                    try {
                        connection = DriverManager.getConnection(url, user, password);
                    } catch (SQLException first) {
                        try {
                            Class.forName("org.postgresql.Driver");
                            connection = DriverManager.getConnection(url, user, password);
                        } catch (ClassNotFoundException cnfe) {
                            throw new SQLException("Driver PostgreSQL non disponibile nel classpath", cnfe);
                        }
                    }
                }
            }
        }
        return connection;
    }

    public void closeConnection() throws SQLException {
        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            connection = null;
            isTest = false;
        }
    }

    public boolean isConnectionActive() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean isTestMode() {
        return isTest;
    }
}
