package View;

import BusinessLogic.Strategy.DataProcessingStrategy;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.concurrent.Task;
import javafx.application.Platform;
import java.time.LocalDate;
import java.util.function.Supplier;

public class DataProcessingView extends VBox {
    private final ComboBox<DataProcessingStrategy.ProcessingType> tipoElaborazioneCombo;
    private final ComboBox<String> strategiaCombo;
    private final TextArea risultatoArea;
    private final Button eseguiBtn;
    private final Button salvaRisultatiBtn;
    private final Button clearBtn;
    private final ProgressIndicator progressIndicator;
    private final Label statusLabel;

    // Controlli per parametri
    private final VBox parametriContainer;
    private final TextField piantagioneIdField;
    private final DatePicker dataInizioField;
    private final DatePicker dataFineField;
    private final Spinner<Integer> topNSpinner;
    private final ComboBox<String> zonaCombo;
    private final ComboBox<String> piantagioneCombo;

    // Validazione input
    private final Label validationLabel;

    private Supplier<String> onEseguiElaborazioneListener;
    private Runnable onAggiornaDati;
    private Runnable onSalvaRisultati;

    public DataProcessingView() {
        setPadding(new Insets(15));
        setSpacing(15);
        setStyle("-fx-background-color: #f8f9fa;");

        // Inizializzazione componenti
        tipoElaborazioneCombo = new ComboBox<>();
        strategiaCombo = new ComboBox<>();
        risultatoArea = new TextArea();
        eseguiBtn = new Button("🚀 Esegui Analisi");
        salvaRisultatiBtn = new Button("💾 Salva Risultati");
        clearBtn = new Button("🗑️ Pulisci");
        progressIndicator = new ProgressIndicator();
        statusLabel = new Label("Pronto per l'elaborazione");
        parametriContainer = new VBox(10);
        piantagioneIdField = new TextField();
        dataInizioField = new DatePicker(LocalDate.now().minusMonths(1));
        dataFineField = new DatePicker(LocalDate.now());
        topNSpinner = new Spinner<>(1, 20, 5);
        zonaCombo = new ComboBox<>();
        piantagioneCombo = new ComboBox<>();
        validationLabel = new Label();

        setupComponents();
        setupLayout();
        setupValidation();
    }

    private void setupComponents() {
        // Configurazione ComboBox principale
        tipoElaborazioneCombo.getItems().addAll(DataProcessingStrategy.ProcessingType.values());
        tipoElaborazioneCombo.setValue(DataProcessingStrategy.ProcessingType.CALCULATION);
        tipoElaborazioneCombo.setPromptText("Seleziona tipo di elaborazione");
        tipoElaborazioneCombo.setPrefWidth(200);
        tipoElaborazioneCombo.setOnAction(e -> aggiornaStrategieDisponibili());

        strategiaCombo.setPrefWidth(250);
        strategiaCombo.setPromptText("Seleziona strategia specifica");
        strategiaCombo.setOnAction(e -> aggiornaParametriVisibili());

        // Configurazione area risultati
        risultatoArea.setEditable(false);
        risultatoArea.setPrefRowCount(20);
        risultatoArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        risultatoArea.setWrapText(true);

        // Configurazione bottoni
        eseguiBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        salvaRisultatiBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 8 16;");
        clearBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 8 16;");

        salvaRisultatiBtn.setDisable(true);
        clearBtn.setOnAction(e -> clearResults());

        // Configurazione progress indicator
        progressIndicator.setVisible(false);
        progressIndicator.setPrefSize(24, 24);

        // Configurazione status label
        statusLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic;");

        // Configurazione validation label
        validationLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
        validationLabel.setVisible(false);

        // Configurazione campi parametri
        setupParameterFields();

        aggiornaStrategieDisponibili();
    }

    private void setupParameterFields() {
        piantagioneIdField.setPromptText("Inserisci ID piantagione (es. 1, 2, 3...)");
        piantagioneIdField.setPrefWidth(200);

        dataInizioField.setPromptText("Data inizio periodo");
        dataFineField.setPromptText("Data fine periodo");

        topNSpinner.setEditable(true);
        topNSpinner.setPrefWidth(100);

        zonaCombo.setPromptText("Seleziona zona");
        zonaCombo.setPrefWidth(200);

        piantagioneCombo.setPromptText("Seleziona piantagione");
        piantagioneCombo.setPrefWidth(200);
    }

    private void setupLayout() {
        // Header con titolo
        Label titleLabel = new Label("📊 Centro di Elaborazione Dati Agricoli");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Selezione tipo e strategia in card
        VBox selectionCard = createCard("Configurazione Analisi");
        GridPane selectionGrid = new GridPane();
        selectionGrid.setHgap(15);
        selectionGrid.setVgap(10);
        selectionGrid.addRow(0, new Label("Tipo elaborazione:"), tipoElaborazioneCombo);
        selectionGrid.addRow(1, new Label("Strategia:"), strategiaCombo);
        selectionCard.getChildren().add(selectionGrid);

        // Parametri in card
        VBox parametriCard = createCard("Parametri di Input");
        parametriCard.getChildren().addAll(parametriContainer, validationLabel);

        // Controlli in card
        VBox controlCard = createCard("Azioni");
        HBox controlBox = new HBox(15);
        controlBox.setAlignment(Pos.CENTER_LEFT);
        controlBox.getChildren().addAll(
            eseguiBtn, salvaRisultatiBtn, clearBtn,
            new Separator(javafx.geometry.Orientation.VERTICAL),
            progressIndicator, statusLabel
        );
        controlCard.getChildren().add(controlBox);

        // Risultati in card
        VBox risultatiCard = createCard("Risultati Elaborazione");
        risultatiCard.getChildren().add(risultatoArea);
        VBox.setVgrow(risultatiCard, Priority.ALWAYS);

        getChildren().addAll(
            titleLabel,
            selectionCard,
            parametriCard,
            controlCard,
            risultatiCard
        );
    }

    private VBox createCard(String title) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #495057;");
        card.getChildren().add(titleLabel);

        return card;
    }

    private void setupValidation() {
        // Validazione in tempo reale per ID piantagione
        piantagioneIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            validatePiantagioneId(newVal);
        });

        // Validazione date
        dataInizioField.valueProperty().addListener((obs, oldVal, newVal) -> validateDateRange());
        dataFineField.valueProperty().addListener((obs, oldVal, newVal) -> validateDateRange());
    }

    private void validatePiantagioneId(String value) {
        if (value != null && !value.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(value.trim());
                if (id <= 0) {
                    showValidationError("L'ID piantagione deve essere un numero positivo");
                    return;
                }
            } catch (NumberFormatException e) {
                showValidationError("L'ID piantagione deve essere un numero valido");
                return;
            }
        }
        hideValidationError();
    }

    private void validateDateRange() {
        LocalDate inizio = dataInizioField.getValue();
        LocalDate fine = dataFineField.getValue();

        if (inizio != null && fine != null) {
            if (inizio.isAfter(fine)) {
                showValidationError("La data di inizio deve essere precedente alla data di fine");
                return;
            }
            if (fine.isAfter(LocalDate.now())) {
                showValidationError("La data di fine non può essere nel futuro");
                return;
            }
        }
        hideValidationError();
    }

    private void showValidationError(String message) {
        validationLabel.setText("⚠️ " + message);
        validationLabel.setVisible(true);
        eseguiBtn.setDisable(true);
    }

    private void hideValidationError() {
        validationLabel.setVisible(false);
        eseguiBtn.setDisable(false);
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
        parametriContainer.getChildren().clear();
        String strategia = strategiaCombo.getValue();
        if (strategia == null) return;

        switch (strategia) {
            case "Produzione Totale", "Media per Pianta", "Efficienza Produttiva" -> {
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(8);
                grid.addRow(0, new Label("ID Piantagione:"), piantagioneIdField);
                grid.addRow(1, new Label("Oppure seleziona:"), piantagioneCombo);
                parametriContainer.getChildren().add(grid);
            }
            case "Produzione per Periodo" -> {
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(8);
                grid.addRow(0, new Label("Data inizio:"), dataInizioField);
                grid.addRow(1, new Label("Data fine:"), dataFineField);
                grid.addRow(2, new Label("Zona (opzionale):"), zonaCombo);
                parametriContainer.getChildren().add(grid);
            }
            case "Top Piantagioni" -> {
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(8);
                grid.addRow(0, new Label("Numero top:"), topNSpinner);
                Label infoLabel = new Label("Mostra le migliori N piantagioni per produttività");
                infoLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic; -fx-font-size: 11px;");
                grid.addRow(1, new Label(), infoLabel);
                parametriContainer.getChildren().add(grid);
            }
            case "Piantagione Migliore", "Statistiche Zone", "Report Raccolti" -> {
                Label infoLabel = new Label("ℹ️ Nessun parametro aggiuntivo richiesto");
                infoLabel.setStyle("-fx-text-fill: #28a745; -fx-font-style: italic; -fx-padding: 10;");
                parametriContainer.getChildren().add(infoLabel);
            }
        }
    }

    private void clearResults() {
        risultatoArea.clear();
        salvaRisultatiBtn.setDisable(true);
        setStatus("Risultati cancellati");
    }

    // Getters e setters essenziali
    public DataProcessingStrategy.ProcessingType getTipoElaborazioneSelezionato() {
        return tipoElaborazioneCombo.getValue();
    }

    public String getStrategiaSelezionata() {
        return strategiaCombo.getValue();
    }

    public String getPiantagioneId() {
        String selected = piantagioneCombo.getValue();
        if (selected != null && !selected.isEmpty()) {
            return selected.split(" - ")[0]; // Estrae l'ID dalla selezione
        }
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

    public String getZonaSelezionata() {
        return zonaCombo.getValue();
    }

    public void setOnEseguiElaborazioneListener(Supplier<String> listener) {
        this.onEseguiElaborazioneListener = listener;
        eseguiBtn.setOnAction(e -> eseguiElaborazioneAsync());
    }

    public void setOnSalvaRisultati(Runnable listener) {
        this.onSalvaRisultati = listener;
        salvaRisultatiBtn.setOnAction(e -> {
            if (onSalvaRisultati != null) {
                onSalvaRisultati.run();
            }
        });
    }

    public void setOnAggiornaDati(Runnable listener) {
        this.onAggiornaDati = listener;
    }

    private void eseguiElaborazioneAsync() {
        if (onEseguiElaborazioneListener == null) return;

        setProgress(true);
        setStatus("Elaborazione in corso...");
        eseguiBtn.setDisable(true);

        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                Thread.sleep(500); // Simula elaborazione
                return onEseguiElaborazioneListener.get();
            }
        };

        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                String risultato = task.getValue();
                setRisultato(risultato);
                salvaRisultatiBtn.setDisable(risultato.startsWith("❌"));
                setProgress(false);
                setStatus("Elaborazione completata");
                eseguiBtn.setDisable(false);
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                setRisultato("❌ Errore durante l'elaborazione: " + task.getException().getMessage());
                setProgress(false);
                setStatus("Errore nell'elaborazione");
                eseguiBtn.setDisable(false);
            });
        });

        new Thread(task).start();
    }

    public void setRisultato(String risultato) {
        risultatoArea.setText(risultato);
        if (!risultato.trim().isEmpty() && !risultato.startsWith("❌")) {
            salvaRisultatiBtn.setDisable(false);
        }
    }

    public void setProgress(boolean visible) {
        progressIndicator.setVisible(visible);
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }

    public void updateComboBoxes(java.util.List<String> piantagioni, java.util.List<String> zone) {
        Platform.runLater(() -> {
            piantagioneCombo.getItems().clear();
            piantagioneCombo.getItems().addAll(piantagioni);

            zonaCombo.getItems().clear();
            zonaCombo.getItems().addAll(zone);
        });
    }
}
