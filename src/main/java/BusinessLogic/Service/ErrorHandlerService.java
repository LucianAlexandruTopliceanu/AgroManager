package BusinessLogic.Service;

import javafx.scene.control.Alert;
import java.util.logging.Logger;
import java.util.logging.Level;


public class ErrorHandlerService {
    private static final Logger LOGGER = Logger.getLogger(ErrorHandlerService.class.getName());


    public static void handleError(String title, String message, Exception e) {
        // Log dell'errore
        LOGGER.log(Level.SEVERE, message, e);

        // Mostra alert all'utente
        showErrorAlert(title, message, e.getMessage());
    }


    public static void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public static void showInfoAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public static boolean showConfirmAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait().orElse(null) == javafx.scene.control.ButtonType.OK;
    }
}
