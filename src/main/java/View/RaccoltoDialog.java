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
    private final Button salvaBtn = new Button("Salva");
    private Raccolto raccolto;
    private boolean confermato = false;

    public RaccoltoDialog(Raccolto raccolto, List<Piantagione> piantagioni) {
        this.raccolto = raccolto != null ? raccolto : new Raccolto();
        setTitle(raccolto == null ? "Nuovo Raccolto" : "Modifica Raccolto");
        initModality(Modality.APPLICATION_MODAL);
        piantagioneCombo.getItems().addAll(piantagioni);
        piantagioneCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Piantagione item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "ID: " + item.getId() + " | Zona: " + item.getZonaId() + " | Pianta: " + item.getPiantaId());
            }
        });
        piantagioneCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Piantagione item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "ID: " + item.getId() + " | Zona: " + item.getZonaId() + " | Pianta: " + item.getPiantaId());
            }
        });
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Piantagione:"), 0, 0);
        grid.add(piantagioneCombo, 1, 0);
        grid.add(new Label("Quantità raccolta:"), 0, 1);
        grid.add(quantitaField, 1, 1);
        grid.add(new Label("Data raccolta:"), 0, 2);
        grid.add(dataRaccoltaPicker, 1, 2);
        grid.add(salvaBtn, 1, 3);
        if (raccolto != null) {
            Piantagione selected = piantagioni.stream().filter(p -> p.getId().equals(raccolto.getPiantagioneId())).findFirst().orElse(null);
            piantagioneCombo.getSelectionModel().select(selected);
            quantitaField.setText(raccolto.getQuantitaKg() != null ? raccolto.getQuantitaKg().toString() : "");
            if (raccolto.getDataRaccolto() != null) dataRaccoltaPicker.setValue(raccolto.getDataRaccolto());
        }
        salvaBtn.setOnAction(e -> {
            if (validaInput()) {
                Piantagione selected = piantagioneCombo.getValue();
                this.raccolto.setPiantagioneId(selected != null ? selected.getId() : null);
                this.raccolto.setQuantitaKg(BigDecimal.valueOf(Integer.parseInt(quantitaField.getText())));
                this.raccolto.setDataRaccolto(dataRaccoltaPicker.getValue());
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
            return piantagioneCombo.getValue() != null &&
                   !quantitaField.getText().isEmpty() && Integer.parseInt(quantitaField.getText()) > 0 &&
                   dataRaccoltaPicker.getValue() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public Raccolto getRaccolto() { return raccolto; }
    public boolean isConfermato() { return confermato; }
}
