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
    private final Button annullaBtn = new Button("Annulla");
    private final Zona zona;
    private boolean confermato = false;

    public ZonaDialog(Zona zona) {
        this.zona = zona != null ? zona : new Zona();
        setTitle(zona == null || zona.getId() == null ? "Nuova Zona" : "Modifica Zona");
        initModality(Modality.APPLICATION_MODAL);

        nomeField.setPromptText("es. Zona Nord, Campo A...");
        dimensioneField.setPromptText("es. 2.5");
        tipoTerrenoField.setPromptText("es. Argilloso, Sabbioso...");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        int row = 0;
        grid.add(new Label("Nome:*"), 0, row);
        grid.add(nomeField, 1, row++);

        grid.add(new Label("Dimensione (ha):*"), 0, row);
        grid.add(dimensioneField, 1, row++);

        grid.add(new Label("Tipo terreno:*"), 0, row);
        grid.add(tipoTerrenoField, 1, row++);

        // Pulsanti
        var buttonBox = new javafx.scene.layout.HBox(10);
        buttonBox.getChildren().addAll(salvaBtn, annullaBtn);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        grid.add(buttonBox, 1, row);

        salvaBtn.getStyleClass().add("btn-primary");
        annullaBtn.getStyleClass().add("btn-secondary");

        // Popola campi se modifica
        if (this.zona != null && this.zona.getId() != null) {
            nomeField.setText(this.zona.getNome());
            dimensioneField.setText(this.zona.getDimensione() != null ?
                this.zona.getDimensione().toString() : "");
            tipoTerrenoField.setText(this.zona.getTipoTerreno());
        }

        salvaBtn.setOnAction(e -> {
            if (validaInput()) {
                aggiornaZona();
                confermato = true;
                close();
            } else {
                NotificationHelper.showError("Errore validazione",
                    "Compila tutti i campi obbligatori (*).\nLa dimensione deve essere un numero positivo.");
            }
        });

        annullaBtn.setOnAction(e -> close());

        setScene(new Scene(grid, 400, 250));
    }

    private void aggiornaZona() {
        zona.setNome(nomeField.getText().trim());
        zona.setDimensione(Double.parseDouble(dimensioneField.getText().trim()));
        zona.setTipoTerreno(tipoTerrenoField.getText().trim());
    }

    private boolean validaInput() {
        try {
            if (nomeField.getText().trim().isEmpty()) return false;
            if (dimensioneField.getText().trim().isEmpty()) return false;
            if (tipoTerrenoField.getText().trim().isEmpty()) return false;

            double dimensione = Double.parseDouble(dimensioneField.getText().trim());
            if (dimensione <= 0) return false;

            return true;
        } catch (NumberFormatException e) {
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
