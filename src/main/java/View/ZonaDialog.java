package View;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import DomainModel.Zona;

public class ZonaDialog extends Stage {
    private final TextField nomeField = new TextField();
    private final TextField dimensioneField = new TextField();
    private final TextField tipoTerrenoField = new TextField();
    private final Button salvaBtn = new Button("Salva");
    private Zona zona;
    private boolean confermato = false;

    public ZonaDialog(Zona zona) {
        this.zona = zona != null ? zona : new Zona();
        setTitle(zona == null ? "Nuova Zona" : "Modifica Zona");
        initModality(Modality.APPLICATION_MODAL);
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("Dimensione (ha):"), 0, 1);
        grid.add(dimensioneField, 1, 1);
        grid.add(new Label("Tipo terreno:"), 0, 2);
        grid.add(tipoTerrenoField, 1, 2);
        grid.add(salvaBtn, 1, 3);
        if (zona != null) {
            nomeField.setText(zona.getNome());
            dimensioneField.setText(zona.getDimensione() != null ? zona.getDimensione().toString() : "");
            tipoTerrenoField.setText(zona.getTipoTerreno());
        }
        salvaBtn.setOnAction(e -> {
            if (validaInput()) {
                this.zona.setNome(nomeField.getText());
                this.zona.setDimensione(Double.parseDouble(dimensioneField.getText()));
                this.zona.setTipoTerreno(tipoTerrenoField.getText());
                confermato = true;
                close();
            } else {
                new Alert(Alert.AlertType.ERROR, "Compila tutti i campi correttamente.").showAndWait();
            }
        });
        setScene(new Scene(grid));
    }

    private boolean validaInput() {
        try {
            return !nomeField.getText().isEmpty() &&
                    !dimensioneField.getText().isEmpty() &&
                    Double.parseDouble(dimensioneField.getText()) > 0 &&
                    !tipoTerrenoField.getText().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public Zona getZona() {
        return zona;
    }

    public boolean isConfermato() {
        return confermato;
    }
}
