package View;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import DomainModel.Raccolto;
import DomainModel.Piantagione;

import java.math.BigDecimal;
import java.util.List;

public class RaccoltoDialog extends Stage {
    private final ComboBox<Piantagione> piantagioneCombo = new ComboBox<>();
    private final TextField quantitaField = new TextField();
    private final DatePicker dataRaccoltaPicker = new DatePicker();
    private final TextArea noteArea = new TextArea();
    private final Button salvaBtn = new Button("Salva");
    private final Button annullaBtn = new Button("Annulla");
    private final Raccolto raccolto;
    private boolean confermato = false;

    public RaccoltoDialog(Raccolto raccolto, List<Piantagione> piantagioni) {
        this.raccolto = raccolto != null ? raccolto : new Raccolto();
        setTitle(raccolto == null || raccolto.getId() == null ? "Nuovo Raccolto" : "Modifica Raccolto");
        initModality(Modality.APPLICATION_MODAL);

        // Setup ComboBox piantagioni
        piantagioneCombo.getItems().addAll(piantagioni);
        piantagioneCombo.setPromptText("Seleziona piantagione...");
        piantagioneCombo.setPrefWidth(300);

        piantagioneCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Piantagione item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("ID %d - Zona: %d | Pianta: %d",
                        item.getId(), item.getZonaId(), item.getPiantaId()));
                }
            }
        });
        piantagioneCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Piantagione item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("ID %d - Zona: %d | Pianta: %d",
                        item.getId(), item.getZonaId(), item.getPiantaId()));
                }
            }
        });

        // Setup campi
        quantitaField.setPromptText("Quantità in kg");
        noteArea.setPromptText("Note sul raccolto (opzionale)");
        noteArea.setPrefRowCount(3);
        noteArea.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        int row = 0;
        grid.add(new Label("Piantagione:*"), 0, row);
        grid.add(piantagioneCombo, 1, row++);

        grid.add(new Label("Data raccolta:*"), 0, row);
        grid.add(dataRaccoltaPicker, 1, row++);

        grid.add(new Label("Quantità (kg):*"), 0, row);
        grid.add(quantitaField, 1, row++);

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
        if (this.raccolto != null && this.raccolto.getId() != null) {
            Piantagione selected = piantagioni.stream()
                .filter(p -> p.getId().equals(this.raccolto.getPiantagioneId()))
                .findFirst()
                .orElse(null);
            piantagioneCombo.getSelectionModel().select(selected);

            quantitaField.setText(this.raccolto.getQuantitaKg() != null ?
                this.raccolto.getQuantitaKg().toString() : "");

            if (this.raccolto.getDataRaccolto() != null) {
                dataRaccoltaPicker.setValue(this.raccolto.getDataRaccolto());
            }

            noteArea.setText(this.raccolto.getNote());
        }

        salvaBtn.setOnAction(e -> {
            if (validaInput()) {
                aggiornaRaccolto();
                confermato = true;
                close();
            } else {
                NotificationHelper.showError("Errore validazione",
                    "Compila tutti i campi obbligatori (*).\nLa quantità deve essere un numero positivo.");
            }
        });

        annullaBtn.setOnAction(e -> close());

        setScene(new Scene(grid, 450, 350));
    }

    private void aggiornaRaccolto() {
        Piantagione selected = piantagioneCombo.getValue();
        raccolto.setPiantagioneId(selected != null ? selected.getId() : null);
        raccolto.setQuantitaKg(new BigDecimal(quantitaField.getText().trim()));
        raccolto.setDataRaccolto(dataRaccoltaPicker.getValue());

        String note = noteArea.getText().trim();
        raccolto.setNote(note.isEmpty() ? null : note);
    }

    private boolean validaInput() {
        try {
            if (piantagioneCombo.getValue() == null) return false;
            if (dataRaccoltaPicker.getValue() == null) return false;
            if (quantitaField.getText().trim().isEmpty()) return false;

            BigDecimal quantita = new BigDecimal(quantitaField.getText().trim());
            if (quantita.compareTo(BigDecimal.ZERO) <= 0) return false;

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Raccolto getRaccolto() { return raccolto; }
    public boolean isConfermato() { return confermato; }
}
