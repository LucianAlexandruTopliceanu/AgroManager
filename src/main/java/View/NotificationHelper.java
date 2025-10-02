package View;

import BusinessLogic.Service.ErrorService;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;


public class NotificationHelper {


    public static void initialize() {
        // Registra i listener per le diverse tipologie di notifiche
        ErrorService.addErrorListener(NotificationHelper::showErrorNotification);
        ErrorService.addInfoListener(NotificationHelper::showInfoNotification);
        ErrorService.addConfirmationListener(NotificationHelper::showConfirmationRequest);
    }


    private static void showErrorNotification(ErrorService.ErrorNotification notification) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore - " + notification.getErrorCode().getCode());
        alert.setHeaderText(notification.getErrorCode().getDescription());
        alert.setContentText(notification.getUserMessage());

        // Personalizza l'aspetto in base alla severità
        customizeAlertBySeverity(alert, notification.getSeverity());

        alert.showAndWait();
    }


    private static void showInfoNotification(ErrorService.InfoNotification notification) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(notification.getTitle());
        alert.setHeaderText(null);
        alert.setContentText(notification.getMessage());
        alert.showAndWait();
    }


    private static void showConfirmationRequest(ErrorService.ConfirmationRequest request) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(request.getTitle());
        alert.setHeaderText(null);
        alert.setContentText(request.getMessage());

        alert.showAndWait().ifPresent(buttonType -> {
            boolean confirmed = buttonType == ButtonType.OK;
            request.respond(confirmed);
        });
    }


    private static void customizeAlertBySeverity(Alert alert, ErrorService.SeverityLevel severity) {
        switch (severity) {
            case LOW -> {
                // Errori di validazione - colore giallo/warning
                alert.getDialogPane().setStyle("-fx-border-color: #FFC107; -fx-border-width: 2px;");
            }
            case MEDIUM -> {
                // Errori di business logic - colore arancione
                alert.getDialogPane().setStyle("-fx-border-color: #FF6B35; -fx-border-width: 2px;");
            }
            case HIGH -> {
                // Errori di sistema - colore rosso
                alert.getDialogPane().setStyle("-fx-border-color: #DC3545; -fx-border-width: 3px;");
            }
        }
    }


    public static void showSuccess(String message) {
        ErrorService.showInfo("Successo", message);
    }

    public static void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attenzione");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void requestConfirmation(String message, Runnable onConfirm) {
        ErrorService.requestConfirmation("Conferma", message, confirmed -> {
            if (confirmed && onConfirm != null) {
                onConfirm.run();
            }
        });
    }
}
