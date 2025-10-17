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
import DomainModel.StatoPiantagione;
import java.util.List;

public class PiantagioneDialog extends Stage {
    private final ComboBox<Zona> zonaCombo = new ComboBox<>();
    private final ComboBox<Pianta> piantaCombo = new ComboBox<>();
    private final ComboBox<StatoPiantagione> statoCombo = new ComboBox<>();
    private final TextField quantitaField = new TextField();
    private final DatePicker messaADimoraPicker = new DatePicker();
    private final Button salvaBtn = new Button("Salva");
    private final Piantagione piantagione;
    private boolean confermato = false;

    public PiantagioneDialog(Piantagione piantagione, List<Zona> zone, List<Pianta> piante, List<StatoPiantagione> stati) {
        this.piantagione = piantagione != null ? piantagione : new Piantagione();
        setTitle(piantagione == null ? "Nuova Piantagione" : "Modifica Piantagione");
        initModality(Modality.APPLICATION_MODAL);

        // Setup ComboBoxes
        zonaCombo.getItems().addAll(zone);
        piantaCombo.getItems().addAll(piante);
        statoCombo.getItems().addAll(stati);

        // Custom cell factories per visualizzare correttamente gli oggetti
        setupComboBoxCellFactories();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Zona:"), 0, 0);
        grid.add(zonaCombo, 1, 0);
        grid.add(new Label("Pianta:"), 0, 1);
        grid.add(piantaCombo, 1, 1);
        grid.add(new Label("Stato:"), 0, 2);
        grid.add(statoCombo, 1, 2);
        grid.add(new Label("Quantità:"), 0, 3);
        grid.add(quantitaField, 1, 3);
        grid.add(new Label("Messa a dimora:"), 0, 4);
        grid.add(messaADimoraPicker, 1, 4);
        grid.add(salvaBtn, 1, 5);

        // Popola i campi se stiamo modificando
        if (piantagione != null && piantagione.getId() != null) {
            popolaCampi(zone, piante, stati);
        } else {
            // Per nuove piantagioni, seleziona stato ATTIVA di default
            StatoPiantagione statoAttiva = stati.stream()
                .filter(s -> StatoPiantagione.ATTIVA.equals(s.getCodice()))
                .findFirst().orElse(null);
            if (statoAttiva != null) {
                statoCombo.setValue(statoAttiva);
            }
        }

        salvaBtn.setOnAction(e -> {
            if (validaInput()) {
                aggiornaPiantagione();
                confermato = true;
                close();
            } else {
                NotificationHelper.showError("Errore validazione", "Compila tutti i campi correttamente.");
            }
        });

        setScene(new Scene(grid));
    }

    private void setupComboBoxCellFactories() {
        // Cell factory per Zone
        zonaCombo.setCellFactory(lv -> new ListCell<Zona>() {
            @Override
            protected void updateItem(Zona item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getNome());
            }
        });
        zonaCombo.setButtonCell(new ListCell<Zona>() {
            @Override
            protected void updateItem(Zona item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getNome());
            }
        });

        // Cell factory per Piante
        piantaCombo.setCellFactory(lv -> new ListCell<Pianta>() {
            @Override
            protected void updateItem(Pianta item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getTipo() + " - " + item.getVarieta());
            }
        });
        piantaCombo.setButtonCell(new ListCell<Pianta>() {
            @Override
            protected void updateItem(Pianta item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getTipo() + " - " + item.getVarieta());
            }
        });

        // Cell factory per Stati
        statoCombo.setCellFactory(lv -> new ListCell<StatoPiantagione>() {
            @Override
            protected void updateItem(StatoPiantagione item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getDescrizione());
            }
        });
        statoCombo.setButtonCell(new ListCell<StatoPiantagione>() {
            @Override
            protected void updateItem(StatoPiantagione item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getDescrizione());
            }
        });
    }

    private void popolaCampi(List<Zona> zone, List<Pianta> piante, List<StatoPiantagione> stati) {
        // Seleziona zona
        zone.stream()
            .filter(z -> z.getId().equals(piantagione.getZonaId()))
            .findFirst()
            .ifPresent(zonaCombo::setValue);

        // Seleziona pianta
        piante.stream()
            .filter(p -> p.getId().equals(piantagione.getPiantaId()))
            .findFirst()
            .ifPresent(piantaCombo::setValue);

        // Seleziona stato
        if (piantagione.getStatoPiantagione() != null) {
            statoCombo.setValue(piantagione.getStatoPiantagione());
        } else if (piantagione.getIdStatoPiantagione() != null) {
            stati.stream()
                .filter(s -> s.getId().equals(piantagione.getIdStatoPiantagione()))
                .findFirst()
                .ifPresent(statoCombo::setValue);
        }

        // Altri campi
        quantitaField.setText(piantagione.getQuantitaPianta() != null ?
            piantagione.getQuantitaPianta().toString() : "");
        if (piantagione.getMessaADimora() != null) {
            messaADimoraPicker.setValue(piantagione.getMessaADimora());
        }
    }

    private void aggiornaPiantagione() {
        piantagione.setZonaId(zonaCombo.getValue().getId());
        piantagione.setPiantaId(piantaCombo.getValue().getId());
        piantagione.setStatoPiantagione(statoCombo.getValue());
        piantagione.setIdStatoPiantagione(statoCombo.getValue().getId());
        piantagione.setQuantitaPianta(Integer.parseInt(quantitaField.getText()));
        piantagione.setMessaADimora(messaADimoraPicker.getValue());
    }

    private boolean validaInput() {
        try {
            return zonaCombo.getValue() != null &&
                   piantaCombo.getValue() != null &&
                   statoCombo.getValue() != null &&
                   !quantitaField.getText().isEmpty() &&
                   Integer.parseInt(quantitaField.getText()) > 0 &&
                   messaADimoraPicker.getValue() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public Piantagione getPiantagione() {
        return piantagione;
    }

    public boolean isConfermato() {
        return confermato;
    }
}
