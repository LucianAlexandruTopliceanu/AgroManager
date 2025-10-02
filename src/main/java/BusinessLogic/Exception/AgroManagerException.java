package BusinessLogic.Exception;


public class AgroManagerException extends Exception {
    private final ErrorCode errorCode;
    private final String userMessage;

    public AgroManagerException(ErrorCode errorCode, String technicalMessage, String userMessage) {
        super(technicalMessage);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }

    public AgroManagerException(ErrorCode errorCode, String technicalMessage, String userMessage, Throwable cause) {
        super(technicalMessage, cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getTechnicalMessage() {
        return getMessage();
    }
}
