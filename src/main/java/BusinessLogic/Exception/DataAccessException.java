package BusinessLogic.Exception;


public class DataAccessException extends AgroManagerException {

    public DataAccessException(String technicalMessage, String userMessage, Throwable cause) {
        super(ErrorCode.DATA_ACCESS_ERROR, technicalMessage, userMessage, cause);
    }

    public static DataAccessException connectionError(Throwable cause) {
        return new DataAccessException(
                ErrorCode.DATABASE_CONNECTION_ERROR,
                "Impossibile connettersi al database: " + cause.getMessage(),
                "Errore di connessione al database. Contatta l'amministratore di sistema.",
                cause
        );
    }

    public static DataAccessException queryError(String operation, Throwable cause) {
        return new DataAccessException(
                ErrorCode.DATA_ACCESS_ERROR,
                String.format("Errore durante l'operazione '%s': %s", operation, cause.getMessage()),
                "Errore durante l'accesso ai dati. Riprova più tardi.",
                cause
        );
    }

    public static DataAccessException transactionError(Throwable cause) {
        return new DataAccessException(
                ErrorCode.TRANSACTION_ERROR,
                "Errore durante la transazione: " + cause.getMessage(),
                "Operazione non completata. Riprova.",
                cause
        );
    }

    private DataAccessException(ErrorCode errorCode, String technicalMessage, String userMessage, Throwable cause) {
        super(errorCode, technicalMessage, userMessage, cause);
    }
}
