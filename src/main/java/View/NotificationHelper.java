package View;

import BusinessLogic.Service.ErrorService;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;


public class NotificationHelper {
    private static Stage toastStage;

    public static void initialize() {
        ErrorService.addErrorListener(NotificationHelper::showErrorNotification);
        ErrorService.addInfoListener(NotificationHelper::showInfoNotification);
        ErrorService.addConfirmationListener(NotificationHelper::showConfirmationRequest);
        initializeToastSystem();
    }

    private static void initializeToastSystem() {
        if (toastStage == null) {
            toastStage = new Stage();
            toastStage.setAlwaysOnTop(true);
            toastStage.setResizable(false);
        }
    }

    private static void showErrorNotification(ErrorService.ErrorNotification notification) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("⚠️ Errore - " + notification.errorCode().getCode());
            alert.setHeaderText(notification.errorCode().getDescription());
            alert.setContentText(notification.userMessage());

            customizeAlertBySeverity(alert, notification.severity());

            alert.showAndWait();
        });
    }

    private static void showInfoNotification(ErrorService.InfoNotification notification) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ℹ️ " + notification.title());
            alert.setHeaderText(null);
            alert.setContentText(notification.message());

            // Applica stili personalizzati
            alert.getDialogPane().getStyleClass().add("info-dialog");

            alert.showAndWait();
        });
    }

    private static void showConfirmationRequest(ErrorService.ConfirmationRequest request) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("❓ " + request.getTitle());
            alert.setHeaderText(null);
            alert.setContentText(request.getMessage());

            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

            alert.showAndWait().ifPresent(buttonType -> {
                boolean confirmed = buttonType == ButtonType.YES;
                request.respond(confirmed);
            });
        });
    }

    public static void showSuccess(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("✅ " + title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.getDialogPane().getStyleClass().add("success-dialog");
            alert.showAndWait();
        });
    }

    public static void showWarning(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("⚠️ " + title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.getDialogPane().getStyleClass().add("warning-dialog");
            alert.showAndWait();
        });
    }

    public static void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("❌ " + title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.getDialogPane().getStyleClass().add("error-dialog");
            alert.showAndWait();
        });
    }

    public static void showToast(String message, ToastType type) {
        Platform.runLater(() -> {
            if (toastStage == null) {
                initializeToastSystem();
            }

            VBox toastBox = new VBox();
            toastBox.setAlignment(Pos.CENTER);
            toastBox.setPrefWidth(300);
            toastBox.setMaxWidth(300);

            Label toastLabel = new Label(getToastIcon(type) + " " + message);
            toastLabel.setWrapText(true);
            toastLabel.setStyle(getToastStyle(type));

            toastBox.getChildren().add(toastLabel);

            Scene toastScene = new Scene(toastBox);
            toastScene.setFill(null);

            toastStage.setScene(toastScene);
            toastStage.setX(System.getProperty("javafx.screen.width") != null ?
                           Double.parseDouble(System.getProperty("javafx.screen.width")) - 320 : 1000);
            toastStage.setY(50);

            toastStage.show();

            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> toastStage.hide());
            delay.play();
        });
    }

    public enum ToastType {
        SUCCESS, WARNING, ERROR, INFO
    }

    private static String getToastIcon(ToastType type) {
        switch (type) {
            case SUCCESS: return "✅";
            case WARNING: return "⚠️";
            case ERROR: return "❌";
            case INFO:
            default: return "ℹ️";
        }
    }

    private static String getToastStyle(ToastType type) {
        String baseStyle = "-fx-padding: 12px 16px; -fx-background-radius: 6px; -fx-text-fill: white; -fx-font-weight: 500; -fx-font-size: 13px; ";

        switch (type) {
            case SUCCESS:
                return baseStyle + "-fx-background-color: #27ae60;";
            case WARNING:
                return baseStyle + "-fx-background-color: #f39c12;";
            case ERROR:
                return baseStyle + "-fx-background-color: #e74c3c;";
            case INFO:
            default:
                return baseStyle + "-fx-background-color: #3498db;";
        }
    }

    private static void customizeAlertBySeverity(Alert alert, ErrorService.SeverityLevel severity) {
        switch (severity) {
            case HIGH:
                alert.getDialogPane().getStyleClass().add("high-error-dialog");
                break;
            case MEDIUM:
                alert.getDialogPane().getStyleClass().add("medium-error-dialog");
                break;
            case LOW:
            default:
                alert.getDialogPane().getStyleClass().add("low-error-dialog");
                break;
        }
    }

    public static void showDatabaseError(String operation) {
        showError("Errore Database",
                  "Impossibile completare l'operazione: " + operation + "\n\n" +
                  "Possibili cause:\n" +
                  "• Database non disponibile\n" +
                  "• Problemi di connessione\n" +
                  "• Errore nei dati\n\n" +
                  "Verificare la connessione e riprovare.");
    }
}

