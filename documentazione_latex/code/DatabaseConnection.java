public class DatabaseConnection {
    
    private static Connection connection; // Singleton pattern
    // Altri attributi per la configurazione del database

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
}