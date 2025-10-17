package BusinessLogic.Service;

import BusinessLogic.Exception.AgroManagerException;
import BusinessLogic.Exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.logging.Level;


public class ErrorService {
    private static final Logger LOGGER = Logger.getLogger(ErrorService.class.getName());

    // Lista di listener per le notifiche di errore (Observer Pattern)
    private static final List<Consumer<ErrorNotification>> errorListeners = new ArrayList<>();
    private static final List<Consumer<InfoNotification>> infoListeners = new ArrayList<>();
    private static final List<Consumer<ConfirmationRequest>> confirmationListeners = new ArrayList<>();

    // Metodi per registrare i listener (tipicamente i Controller)
    public static void addErrorListener(Consumer<ErrorNotification> listener) {
        errorListeners.add(listener);
    }

    public static void addInfoListener(Consumer<InfoNotification> listener) {
        infoListeners.add(listener);
    }

    public static void addConfirmationListener(Consumer<ConfirmationRequest> listener) {
        confirmationListeners.add(listener);
    }

    public static void handleException(AgroManagerException exception) {
        // Log dell'errore con dettagli tecnici
        LOGGER.log(Level.SEVERE,
                  String.format("[%s] %s", exception.getErrorCode().getCode(), exception.getTechnicalMessage()),
                  exception);

        // Notifica i listener con il messaggio
        ErrorNotification notification = new ErrorNotification(
                exception.getErrorCode(),
                exception.getUserMessage(),
                exception.getTechnicalMessage(),
                getSeverityLevel(exception.getErrorCode())
        );

        notifyErrorListeners(notification);
    }


    public static void handleException(String operation, Exception exception) {
        LOGGER.log(Level.SEVERE, String.format("Errore durante %s: %s", operation, exception.getMessage()), exception);

        ErrorNotification notification = new ErrorNotification(
                ErrorCode.SYSTEM_ERROR,
                "Si è verificato un errore imprevisto. Riprova più tardi.",
                exception.getMessage(),
                SeverityLevel.HIGH
        );

        notifyErrorListeners(notification);
    }


    public static void showInfo(String title, String message) {
        LOGGER.info(String.format("%s: %s", title, message));

        InfoNotification notification = new InfoNotification(title, message);
        notifyInfoListeners(notification);
    }


    public static void requestConfirmation(String title, String message, Consumer<Boolean> callback) {
        ConfirmationRequest request = new ConfirmationRequest(title, message, callback);
        notifyConfirmationListeners(request);
    }

    private static void notifyErrorListeners(ErrorNotification notification) {
        errorListeners.forEach(listener -> {
            try {
                listener.accept(notification);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Errore durante la notifica di errore", e);
            }
        });
    }

    private static void notifyInfoListeners(InfoNotification notification) {
        infoListeners.forEach(listener -> {
            try {
                listener.accept(notification);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Errore durante la notifica informativa", e);
            }
        });
    }

    private static void notifyConfirmationListeners(ConfirmationRequest request) {
        confirmationListeners.forEach(listener -> {
            try {
                listener.accept(request);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Errore durante la richiesta di conferma", e);
            }
        });
    }

    private static SeverityLevel getSeverityLevel(ErrorCode errorCode) {
        return switch (errorCode) {
            case VALIDATION_ERROR, REQUIRED_FIELD_MISSING, INVALID_FORMAT -> SeverityLevel.LOW;
            case ENTITY_NOT_FOUND, DUPLICATE_ENTRY, INSUFFICIENT_DATA -> SeverityLevel.MEDIUM;
            case DATABASE_CONNECTION_ERROR, TRANSACTION_ERROR, SYSTEM_ERROR -> SeverityLevel.HIGH;
            default -> SeverityLevel.MEDIUM;
        };
    }

    // Inner classes per le notifiche
        public record ErrorNotification(ErrorCode errorCode, String userMessage, String technicalMessage,
                                        SeverityLevel severity) {
    }

    public record InfoNotification(String title, String message) {
    }

    public static class ConfirmationRequest {
        private final String title;
        private final String message;
        private final Consumer<Boolean> callback;

        public ConfirmationRequest(String title, String message, Consumer<Boolean> callback) {
            this.title = title;
            this.message = message;
            this.callback = callback;
        }

        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public void respond(boolean confirmed) { callback.accept(confirmed); }
    }

    public enum SeverityLevel {
        LOW, MEDIUM, HIGH
    }
}
