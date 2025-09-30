package View;

import BusinessLogic.Strategy.DataProcessingStrategy;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.concurrent.Task;
import java.time.LocalDate;
import java.util.function.Supplier;

public class DataProcessingView extends VBox {
    private final ComboBox<DataProcessingStrategy.ProcessingType> tipoElaborazioneCombo;
    private final ComboBox<String> strategiaCombo;
    private final TextArea risultatoArea;
    private final Button eseguiBtn;
    private final Button aggiornaBtn;
    private final ProgressIndicator progressIndicator;
    private final Label statusLabel;

    // Controlli per parametri
    private final GridPane parametriPane;
    private final TextField piantagioneIdField;
    private final DatePicker dataInizioField;
    private final DatePicker dataFineField;
    private final Spinner<Integer> topNSpinner;

    private Supplier<String> onEseguiElaborazioneListener;
    private Runnable onAggiornaDati;

    public DataProcessingView() {
        setPadding(new Insets(10));
        setSpacing(10);

        // Inizializzazione componenti
        tipoElaborazioneCombo = new ComboBox<>();
        strategiaCombo = new ComboBox<>();
        risultatoArea = new TextArea();
        eseguiBtn = new Button("Esegui");
        aggiornaBtn = new Button("Aggiorna");
        progressIndicator = new ProgressIndicator();
        statusLabel = new Label("Pronto");
        parametriPane = new GridPane();
        piantagioneIdField = new TextField();
        dataInizioField = new DatePicker(LocalDate.now().minusMonths(1));
        dataFineField = new DatePicker(LocalDate.now());
        topNSpinner = new Spinner<>(1, 10, 3);

        setupComponents();
        setupLayout();
    }

    private void setupComponents() {
        tipoElaborazioneCombo.getItems().addAll(DataProcessingStrategy.ProcessingType.values());
        tipoElaborazioneCombo.setValue(DataProcessingStrategy.ProcessingType.CALCULATION);
        tipoElaborazioneCombo.setOnAction(e -> aggiornaStrategieDisponibili());

        risultatoArea.setEditable(false);
        risultatoArea.setPrefRowCount(15);

        progressIndicator.setVisible(false);
        progressIndicator.setPrefSize(20, 20);

        aggiornaStrategieDisponibili();
    }

    private void setupLayout() {
        // Selezione tipo e strategia
        HBox selectionBox = new HBox(10);
        selectionBox.getChildren().addAll(
            new Label("Tipo:"), tipoElaborazioneCombo,
            new Label("Strategia:"), strategiaCombo
        );

        // Parametri
        parametriPane.setHgap(10);
        parametriPane.setVgap(5);

        // Controlli
        HBox controlBox = new HBox(10);
        controlBox.getChildren().addAll(
            eseguiBtn, aggiornaBtn, progressIndicator, statusLabel
        );

        getChildren().addAll(
            selectionBox,
            parametriPane,
            controlBox,
            risultatoArea
        );
    }

    private void aggiornaStrategieDisponibili() {
        DataProcessingStrategy.ProcessingType tipoSelezionato = tipoElaborazioneCombo.getValue();
        strategiaCombo.getItems().clear();

        switch (tipoSelezionato) {
            case CALCULATION -> strategiaCombo.getItems().addAll(
                "Produzione Totale",
                "Media per Pianta",
                "Efficienza Produttiva",
                "Produzione per Periodo"
            );
            case STATISTICS -> strategiaCombo.getItems().addAll(
                "Piantagione Migliore",
                "Top Piantagioni",
                "Statistiche Zone"
            );
            case REPORT -> strategiaCombo.getItems().addAll(
                "Report Raccolti"
            );
        }

        if (!strategiaCombo.getItems().isEmpty()) {
            strategiaCombo.setValue(strategiaCombo.getItems().get(0));
        }

        aggiornaParametriVisibili();
    }

    private void aggiornaParametriVisibili() {
        parametriPane.getChildren().clear();
        String strategia = strategiaCombo.getValue();
        if (strategia == null) return;

        switch (strategia) {
            case "Produzione Totale", "Media per Pianta", "Efficienza Produttiva" -> {
                parametriPane.addRow(0, new Label("ID Piantagione:"), piantagioneIdField);
            }
            case "Produzione per Periodo" -> {
                parametriPane.addRow(0, new Label("Data inizio:"), dataInizioField);
                parametriPane.addRow(1, new Label("Data fine:"), dataFineField);
            }
            case "Top Piantagioni" -> {
                parametriPane.addRow(0, new Label("Numero top:"), topNSpinner);
            }
            case "Piantagione Migliore", "Statistiche Zone", "Report Raccolti" -> {
                // Nessun parametro aggiuntivo necessario
                Label infoLabel = new Label("Nessun parametro richiesto");
                infoLabel.setStyle("-fx-text-fill: #7F8C8D; -fx-font-style: italic;");
                parametriPane.addRow(0, infoLabel);
            }
        }
    }

    // Getters e setters essenziali
    public DataProcessingStrategy.ProcessingType getTipoElaborazioneSelezionato() {
        return tipoElaborazioneCombo.getValue();
    }

    public String getStrategiaSelezionata() {
        return strategiaCombo.getValue();
    }

    public String getPiantagioneId() {
        return piantagioneIdField.getText();
    }

    public LocalDate getDataInizio() {
        return dataInizioField.getValue();
    }

    public LocalDate getDataFine() {
        return dataFineField.getValue();
    }

    public Integer getTopN() {
        return topNSpinner.getValue();
    }

    public void setOnEseguiElaborazioneListener(Supplier<String> listener) {
        this.onEseguiElaborazioneListener = listener;
        eseguiBtn.setOnAction(e -> {
            if (onEseguiElaborazioneListener != null) {
                setRisultato(onEseguiElaborazioneListener.get());
            }
        });
    }

    public void setOnAggiornaDati(Runnable listener) {
        this.onAggiornaDati = listener;
        aggiornaBtn.setOnAction(e -> {
            if (onAggiornaDati != null) {
                onAggiornaDati.run();
            }
        });
    }

    public void setRisultato(String risultato) {
        risultatoArea.setText(risultato);
    }

    public void setProgress(boolean visible) {
        progressIndicator.setVisible(visible);
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }

    public void setOnEseguiElaborazione(Runnable handler) {
        eseguiBtn.setOnAction(e -> {
            setProgress(true);
            setStatus("Elaborazione...");
            eseguiBtn.setDisable(true);

            new Thread(() -> {
                try {
                    handler.run();
                } finally {
                    javafx.application.Platform.runLater(() -> {
                        setProgress(false);
                        setStatus("Completato");
                        eseguiBtn.setDisable(false);
                    });
                }
            }).start();
        });
    }
}
