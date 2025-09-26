package View;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import DomainModel.Piantagione;
import DomainModel.Zona;
import DomainModel.Pianta;
import java.time.LocalDate;
import java.util.List;

public class PiantagioneDialog extends Stage {
    private final ComboBox<Zona> zonaCombo = new ComboBox<>();
    private final ComboBox<Pianta> piantaCombo = new ComboBox<>();
    private final TextField quantitaField = new TextField();
    private final DatePicker messaADimoraPicker = new DatePicker();
    private final Button salvaBtn = new Button("Salva");
    private Piantagione piantagione;
    private boolean confermato = false;

    public PiantagioneDialog(Piantagione piantagione, List<Zona> zone, List<Pianta> piante) {
        this.piantagione = piantagione != null ? piantagione : new Piantagione();
        setTitle(piantagione == null ? "Nuova Piantagione" : "Modifica Piantagione");
        initModality(Modality.APPLICATION_MODAL);
        zonaCombo.getItems().addAll(zone);
        piantaCombo.getItems().addAll(piante);
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Zona:"), 0, 0);
        grid.add(zonaCombo, 1, 0);
        grid.add(new Label("Pianta:"), 0, 1);
        grid.add(piantaCombo, 1, 1);
        grid.add(new Label("Quantità:"), 0, 2);
        grid.add(quantitaField, 1, 2);
        grid.add(new Label("Messa a dimora:"), 0, 3);
        grid.add(messaADimoraPicker, 1, 3);
        grid.add(salvaBtn, 1, 4);
        if (piantagione != null) {
            zonaCombo.getSelectionModel().select(zone.stream().filter(z -> z.getId().equals(piantagione.getZonaId())).findFirst().orElse(null));
            piantaCombo.getSelectionModel().select(piante.stream().filter(p -> p.getId().equals(piantagione.getPiantaId())).findFirst().orElse(null));
            quantitaField.setText(piantagione.getQuantitaPianta() != null ? piantagione.getQuantitaPianta().toString() : "");
            if (piantagione.getMessaADimora() != null) messaADimoraPicker.setValue(piantagione.getMessaADimora());
        }
        salvaBtn.setOnAction(e -> {
            if (validaInput()) {
                this.piantagione.setZonaId(zonaCombo.getValue().getId());
                this.piantagione.setPiantaId(piantaCombo.getValue().getId());
                this.piantagione.setQuantitaPianta(Integer.parseInt(quantitaField.getText()));
                this.piantagione.setMessaADimora(messaADimoraPicker.getValue());
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
            return zonaCombo.getValue() != null && piantaCombo.getValue() != null &&
                   !quantitaField.getText().isEmpty() && Integer.parseInt(quantitaField.getText()) > 0 &&
                   messaADimoraPicker.getValue() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public Piantagione getPiantagione() { return piantagione; }
    public boolean isConfermato() { return confermato; }
}

