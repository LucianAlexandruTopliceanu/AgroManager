package BusinessLogic.Exception;


public class BusinessLogicException extends AgroManagerException {

    public BusinessLogicException(String technicalMessage, String userMessage) {
        super(ErrorCode.BUSINESS_RULE_VIOLATION, technicalMessage, userMessage);
    }

    public BusinessLogicException(String technicalMessage, String userMessage, Throwable cause) {
        super(ErrorCode.BUSINESS_RULE_VIOLATION, technicalMessage, userMessage, cause);
    }

    public static BusinessLogicException entityNotFound(String entityType, Object id) {
        return new BusinessLogicException(
                ErrorCode.ENTITY_NOT_FOUND,
                String.format("%s con ID '%s' non trovato", entityType, id),
                String.format("L'elemento richiesto non è stato trovato")
        );
    }

    public static BusinessLogicException duplicateEntry(String entityType, String field, Object value) {
        return new BusinessLogicException(
                ErrorCode.DUPLICATE_ENTRY,
                String.format("%s con %s '%s' già esistente", entityType, field, value),
                String.format("Esiste già un elemento con questo valore")
        );
    }

    public static BusinessLogicException insufficientData(String operation) {
        return new BusinessLogicException(
                ErrorCode.INSUFFICIENT_DATA,
                String.format("Dati insufficienti per l'operazione: %s", operation),
                String.format("Non ci sono abbastanza dati per completare l'operazione")
        );
    }

    private BusinessLogicException(ErrorCode errorCode, String technicalMessage, String userMessage) {
        super(errorCode, technicalMessage, userMessage);
    }
}
