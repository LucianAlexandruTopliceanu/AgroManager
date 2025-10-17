package View;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import DomainModel.Pianta;
import DomainModel.Fornitore;
import java.math.BigDecimal;
import java.util.List;

public class PiantaDialog extends Stage {
    private final TextField tipoField = new TextField();
    private final TextField varietaField = new TextField();
    private final TextField costoField = new TextField();
    private final TextArea noteArea = new TextArea();
    private final ComboBox<Fornitore> fornitoreCombo = new ComboBox<>();
    private final Button salvaBtn = new Button("Salva");
    private final Button annullaBtn = new Button("Annulla");
    private final Pianta pianta;
    private boolean confermato = false;

    public PiantaDialog(Pianta pianta, List<Fornitore> fornitori) {
        this.pianta = pianta != null ? pianta : new Pianta();
        setTitle(pianta == null || pianta.getId() == null ? "Nuova Pianta" : "Modifica Pianta");
        initModality(Modality.APPLICATION_MODAL);

        // Setup ComboBox fornitori
        fornitoreCombo.getItems().addAll(fornitori);
        fornitoreCombo.setPromptText("Seleziona fornitore...");
        fornitoreCombo.setPrefWidth(250);

        // Cell factory per visualizzare nome fornitore
        fornitoreCombo.setCellFactory(lv -> new ListCell<Fornitore>() {
            @Override
            protected void updateItem(Fornitore item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNome());
            }
        });
        fornitoreCombo.setButtonCell(new ListCell<Fornitore>() {
            @Override
            protected void updateItem(Fornitore item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNome());
            }
        });

        // Setup campi
        tipoField.setPromptText("es. Pomodoro, Zucchina...");
        varietaField.setPromptText("es. Ciliegino, San Marzano...");
        costoField.setPromptText("0.00");
        noteArea.setPromptText("Note aggiuntive (opzionale)");
        noteArea.setPrefRowCount(3);
        noteArea.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        int row = 0;
        grid.add(new Label("Tipo:*"), 0, row);
        grid.add(tipoField, 1, row++);

        grid.add(new Label("Varietà:*"), 0, row);
        grid.add(varietaField, 1, row++);

        grid.add(new Label("Fornitore:*"), 0, row);
        grid.add(fornitoreCombo, 1, row++);

        grid.add(new Label("Costo (€):"), 0, row);
        grid.add(costoField, 1, row++);

        grid.add(new Label("Note:"), 0, row);
        grid.add(noteArea, 1, row++);

        // Pulsanti
        var buttonBox = new javafx.scene.layout.HBox(10);
        buttonBox.getChildren().addAll(salvaBtn, annullaBtn);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        grid.add(buttonBox, 1, row);

        salvaBtn.getStyleClass().add("btn-primary");
        annullaBtn.getStyleClass().add("btn-secondary");

        // Popola campi se modifica
        if (this.pianta != null && this.pianta.getId() != null) {
            tipoField.setText(this.pianta.getTipo());
            varietaField.setText(this.pianta.getVarieta());
            costoField.setText(this.pianta.getCosto() != null ? this.pianta.getCosto().toString() : "");
            noteArea.setText(this.pianta.getNote());

            // Seleziona fornitore
            if (this.pianta.getFornitoreId() != null) {
                fornitori.stream()
                    .filter(f -> f.getId().equals(this.pianta.getFornitoreId()))
                    .findFirst()
                    .ifPresent(fornitoreCombo::setValue);
            }
        }

        salvaBtn.setOnAction(e -> {
            if (validaInput()) {
                aggiornaPlanta();
                confermato = true;
                close();
            } else {
                NotificationHelper.showError("Errore validazione",
                    "Compila tutti i campi obbligatori (*).\nIl costo deve essere un numero positivo.");
            }
        });

        annullaBtn.setOnAction(e -> close());

        setScene(new Scene(grid, 450, 400));
    }

    private void aggiornaPlanta() {
        pianta.setTipo(tipoField.getText().trim());
        pianta.setVarieta(varietaField.getText().trim());
        pianta.setFornitoreId(fornitoreCombo.getValue().getId());

        String costoText = costoField.getText().trim();
        if (!costoText.isEmpty()) {
            pianta.setCosto(new BigDecimal(costoText));
        } else {
            pianta.setCosto(null);
        }

        String note = noteArea.getText().trim();
        pianta.setNote(note.isEmpty() ? null : note);
    }

    private boolean validaInput() {
        try {
            if (tipoField.getText().trim().isEmpty()) return false;
            if (varietaField.getText().trim().isEmpty()) return false;
            if (fornitoreCombo.getValue() == null) return false;

            String costoText = costoField.getText().trim();
            if (!costoText.isEmpty()) {
                BigDecimal costo = new BigDecimal(costoText);
                if (costo.compareTo(BigDecimal.ZERO) < 0) return false;
            }

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Pianta getPianta() { return pianta; }
    public boolean isConfermato() { return confermato; }
}
