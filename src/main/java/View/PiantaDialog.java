package View;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import DomainModel.Pianta;

public class PiantaDialog extends Stage {
    private final TextField tipoField = new TextField();
    private final TextField varietaField = new TextField();
    private final Button salvaBtn = new Button("Salva");
    private Pianta pianta;
    private boolean confermato = false;

    public PiantaDialog(Pianta pianta) {
        this.pianta = pianta != null ? pianta : new Pianta();
        setTitle(pianta == null ? "Nuova Pianta" : "Modifica Pianta");
        initModality(Modality.APPLICATION_MODAL);
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Tipo:"), 0, 0);
        grid.add(tipoField, 1, 0);
        grid.add(new Label("Varietà:"), 0, 1);
        grid.add(varietaField, 1, 1);
        grid.add(salvaBtn, 1, 2);
        if (pianta != null) {
            tipoField.setText(pianta.getTipo());
            varietaField.setText(pianta.getVarieta());
        }
        salvaBtn.setOnAction(e -> {
            if (validaInput()) {
                this.pianta.setTipo(tipoField.getText());
                this.pianta.setVarieta(varietaField.getText());
                confermato = true;
                close();
            } else {
                new Alert(Alert.AlertType.ERROR, "Compila tutti i campi correttamente.").showAndWait();
            }
        });
        setScene(new Scene(grid));
    }

    private boolean validaInput() {
        return !tipoField.getText().isEmpty() && !varietaField.getText().isEmpty();
    }

    public Pianta getPianta() { return pianta; }
    public boolean isConfermato() { return confermato; }
}

