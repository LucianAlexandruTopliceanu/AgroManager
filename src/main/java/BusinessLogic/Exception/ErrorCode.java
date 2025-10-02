package BusinessLogic.Exception;


public enum ErrorCode {
    // Errori di validazione
    VALIDATION_ERROR("VALIDATION_001", "Errore di validazione"),
    REQUIRED_FIELD_MISSING("VALIDATION_002", "Campo obbligatorio mancante"),
    INVALID_FORMAT("VALIDATION_003", "Formato non valido"),
    INVALID_DATE_RANGE("VALIDATION_004", "Intervallo di date non valido"),

    // Errori di business logic
    ENTITY_NOT_FOUND("BUSINESS_001", "Entità non trovata"),
    DUPLICATE_ENTRY("BUSINESS_002", "Voce duplicata"),
    BUSINESS_RULE_VIOLATION("BUSINESS_003", "Violazione regola di business"),
    INSUFFICIENT_DATA("BUSINESS_004", "Dati insufficienti per l'operazione"),

    // Errori di database
    DATABASE_CONNECTION_ERROR("DB_001", "Errore di connessione al database"),
    DATA_ACCESS_ERROR("DB_002", "Errore di accesso ai dati"),
    TRANSACTION_ERROR("DB_003", "Errore di transazione"),

    // Errori di sistema
    SYSTEM_ERROR("SYS_001", "Errore di sistema"),
    CONFIGURATION_ERROR("SYS_002", "Errore di configurazione"),
    RESOURCE_NOT_AVAILABLE("SYS_003", "Risorsa non disponibile");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }
}
