package BusinessLogic.Exception;


public class ValidationException extends AgroManagerException {

    public ValidationException(String field, String value, String reason) {
        super(ErrorCode.VALIDATION_ERROR,
              String.format("Validazione fallita per campo '%s' con valore '%s': %s", field, value, reason),
              String.format("Il campo '%s' non è valido: %s", field, reason));
    }

    public ValidationException(String message) {
        super(ErrorCode.VALIDATION_ERROR, message,
              "I dati inseriti non sono validi. Controlla i campi e riprova.");
    }

    public static ValidationException requiredField(String fieldName) {
        return new ValidationException(ErrorCode.REQUIRED_FIELD_MISSING,
                String.format("Campo obbligatorio '%s' non fornito", fieldName),
                String.format("Il campo '%s' è obbligatorio", fieldName));
    }

    public static ValidationException invalidFormat(String fieldName, String expectedFormat) {
        return new ValidationException(ErrorCode.INVALID_FORMAT,
                String.format("Formato non valido per campo '%s', atteso: %s", fieldName, expectedFormat),
                String.format("Il formato del campo '%s' non è corretto. Formato atteso: %s", fieldName, expectedFormat));
    }

    private ValidationException(ErrorCode errorCode, String technicalMessage, String userMessage) {
        super(errorCode, technicalMessage, userMessage);
    }
}
