package View;

import BusinessLogic.Strategy.DataProcessingStrategy;
import BusinessLogic.Strategy.ProcessingResult;
import Controller.DataProcessingController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.Map;
import java.math.BigDecimal;


public class DataProcessingView extends VBox {

    // Selezione strategia
    private final ComboBox<DataProcessingStrategy.ProcessingType> tipoElaborazioneCombo = new ComboBox<>();
    private final ComboBox<String> strategiaCombo = new ComboBox<>();

    // Parametri input
    private final VBox parametriContainer = new VBox(10);
    private final TextField piantagioneIdField = new TextField();
    private final ComboBox<String> piantagioneCombo = new ComboBox<>();
    private final DatePicker dataInizioField = new DatePicker(LocalDate.now().minusMonths(1));
    private final DatePicker dataFineField = new DatePicker(LocalDate.now());
    private final Spinner<Integer> topNSpinner = new Spinner<>(1, 20, 5);
    private final ComboBox<String> zonaCombo = new ComboBox<>();

    // Controlli azione
    private final Button eseguiBtn = new Button("🚀 Esegui Analisi");
    private final Button salvaRisultatiBtn = new Button("💾 Salva");
    private final Button clearBtn = new Button("🗑️ Pulisci");
    private final Button aggiornaDatiBtn = new Button("🔄 Aggiorna");

    // Feedback
    private final ProgressIndicator progressIndicator = new ProgressIndicator();
    private final Label statusLabel = new Label("Pronto per l'elaborazione");
    private final Label validationLabel = new Label();

    // Output
    private final TextArea risultatoArea = new TextArea();

    // Controller
    private DataProcessingController controller;

    public DataProcessingView() {
        setupStyles();
        setupLayout();
        setupEventHandlers();
        setupValidation();
    }

    private void setupStyles() {
        getStyleClass().add("main-container");


        tipoElaborazioneCombo.getItems().addAll(DataProcessingStrategy.ProcessingType.values());
        tipoElaborazioneCombo.setValue(DataProcessingStrategy.ProcessingType.CALCULATION);
        tipoElaborazioneCombo.setPromptText("Tipo elaborazione");
        tipoElaborazioneCombo.setPrefWidth(200);
        tipoElaborazioneCombo.getStyleClass().add("combo-box-standard");


        strategiaCombo.setPrefWidth(250);
        strategiaCombo.setPromptText("Seleziona strategia");
        strategiaCombo.getStyleClass().add("combo-box-standard");


        piantagioneIdField.setPromptText("ID piantagione");
        piantagioneIdField.setPrefWidth(150);
        piantagioneIdField.getStyleClass().add("text-field-standard");

        piantagioneCombo.setPromptText("Seleziona dalla lista");
        piantagioneCombo.setPrefWidth(200);
        piantagioneCombo.getStyleClass().add("combo-box-standard");

        dataInizioField.setPromptText("Data inizio");
        dataInizioField.getStyleClass().add("date-picker-standard");

        dataFineField.setPromptText("Data fine");
        dataFineField.getStyleClass().add("date-picker-standard");

        topNSpinner.setEditable(true);
        topNSpinner.setPrefWidth(100);
        topNSpinner.getStyleClass().add("spinner-standard");

        zonaCombo.setPromptText("Zona (opzionale)");
        zonaCombo.setPrefWidth(180);
        zonaCombo.getStyleClass().add("combo-box-standard");


        eseguiBtn.getStyleClass().add("btn-primary");
        salvaRisultatiBtn.getStyleClass().add("btn-secondary");
        salvaRisultatiBtn.setDisable(true);
        clearBtn.getStyleClass().add("btn-danger");
        aggiornaDatiBtn.getStyleClass().add("btn-support");


        progressIndicator.setVisible(false);
        progressIndicator.getStyleClass().add("progress-standard");
        progressIndicator.setMaxSize(24, 24);

        statusLabel.getStyleClass().add("status-label");

        validationLabel.getStyleClass().add("validation-error");
        validationLabel.setVisible(false);
        validationLabel.setWrapText(true);


        risultatoArea.setEditable(false);
        risultatoArea.setPrefRowCount(18);
        risultatoArea.setWrapText(true);
        risultatoArea.getStyleClass().add("results-area");
        risultatoArea.setPromptText("I risultati dell'elaborazione appariranno qui...");

        parametriContainer.getStyleClass().add("parameters-container");

        aggiornaStrategieDisponibili();
    }

    private void setupLayout() {
        // Header con titolo e descrizione
        VBox header = createHeader();

        // Sezione configurazione in card compatta
        VBox configCard = createConfigSection();

        // Sezione parametri dinamica
        VBox paramCard = createParametersSection();

        // Barra azioni compatta
        HBox actionBar = createActionBar();

        // Sezione risultati espandibile
        VBox resultCard = createResultSection();

        getChildren().addAll(header, configCard, paramCard, actionBar, resultCard);

        // L'area risultati cresce per occupare lo spazio disponibile
        VBox.setVgrow(resultCard, Priority.ALWAYS);
    }


    private VBox createHeader() {
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("header-section");

        Label title = new Label("📊 Centro Elaborazione Dati Agricoli");
        title.getStyleClass().add("main-title");

        Label subtitle = new Label("Analisi, calcoli e report sui dati delle piantagioni");
        subtitle.getStyleClass().add("subtitle");

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private VBox createConfigSection() {
        VBox card = new VBox(15);
        card.getStyleClass().add("styled-card");

        Label cardTitle = new Label("⚙️ Configurazione");
        cardTitle.getStyleClass().add("card-title");

        // Griglia compatta per la configurazione
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER_LEFT);

        Label tipoLabel = new Label("Tipo:");
        tipoLabel.getStyleClass().add("field-label");

        Label strategiaLabel = new Label("Strategia:");
        strategiaLabel.getStyleClass().add("field-label");

        grid.add(tipoLabel, 0, 0);
        grid.add(tipoElaborazioneCombo, 1, 0);
        grid.add(strategiaLabel, 0, 1);
        grid.add(strategiaCombo, 1, 1);

        card.getChildren().addAll(cardTitle, grid);
        return card;
    }

    private VBox createParametersSection() {
        VBox card = new VBox(15);
        card.getStyleClass().add("styled-card");

        Label cardTitle = new Label("📋 Parametri");
        cardTitle.getStyleClass().add("card-title");

        card.getChildren().addAll(cardTitle, parametriContainer, validationLabel);
        return card;
    }

    private HBox createActionBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 0, 10, 0));

        // Gruppo principale
        HBox mainGroup = new HBox(10);
        mainGroup.setAlignment(Pos.CENTER_LEFT);
        mainGroup.getChildren().add(eseguiBtn);

        // Gruppo secondario
        HBox secondaryGroup = new HBox(8);
        secondaryGroup.setAlignment(Pos.CENTER_LEFT);
        secondaryGroup.getChildren().addAll(salvaRisultatiBtn, clearBtn);

        // Gruppo utility
        HBox utilityGroup = new HBox(8);
        utilityGroup.setAlignment(Pos.CENTER_LEFT);
        utilityGroup.getChildren().addAll(aggiornaDatiBtn, progressIndicator);

        // Separatori
        Separator sep1 = new Separator(javafx.geometry.Orientation.VERTICAL);
        sep1.getStyleClass().add("v-separator");
        Separator sep2 = new Separator(javafx.geometry.Orientation.VERTICAL);
        sep2.getStyleClass().add("v-separator");

        bar.getChildren().addAll(mainGroup, sep1, secondaryGroup, sep2, utilityGroup);

        // Spacer per allineare a sinistra
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        bar.getChildren().add(spacer);

        return bar;
    }

    private VBox createResultSection() {
        VBox card = new VBox(12);
        card.getStyleClass().add("styled-card");
        VBox.setVgrow(card, Priority.ALWAYS);

        Label cardTitle = new Label("📈 Risultati");
        cardTitle.getStyleClass().add("card-title");

        ScrollPane scrollPane = new ScrollPane(risultatoArea);
        scrollPane.getStyleClass().add("results-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        card.getChildren().addAll(cardTitle, scrollPane, statusLabel);
        return card;
    }

    private void aggiornaStrategieDisponibili() {
        DataProcessingStrategy.ProcessingType tipo = tipoElaborazioneCombo.getValue();
        strategiaCombo.getItems().clear();

        switch (tipo) {
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
                "📋 Report Completo",
                "📊 Statistiche Generali",
                "📅 Analisi Mensile",
                "⏰ Periodo Coperto"
            );
        }

        if (!strategiaCombo.getItems().isEmpty()) {
            strategiaCombo.setValue(strategiaCombo.getItems().get(0));
        }

        aggiornaParametriVisibili();
    }

    private void aggiornaParametriVisibili() {
        parametriContainer.getChildren().clear();
        eseguiBtn.setDisable(false);

        String strategia = strategiaCombo.getValue();
        if (strategia == null) return;

        switch (strategia) {
            case "Produzione Totale", "Media per Pianta", "Efficienza Produttiva" -> mostraParametriPiantagione();
            case "Produzione per Periodo" -> mostraParametriPeriodo();
            case "Top Piantagioni" -> mostraParametriTopN();
            case "Report Raccolti" -> mostraInfoNessunParametro();
            case "Piantagione Migliore", "Statistiche Zone" -> mostraInfoNessunParametro();
        }
    }

    private void mostraParametriPiantagione() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.getStyleClass().add("input-grid");

        Label idLabel = new Label("ID Piantagione:");
        idLabel.getStyleClass().add("field-label");

        Label comboLabel = new Label("Oppure seleziona:");
        comboLabel.getStyleClass().add("field-label");

        grid.add(idLabel, 0, 0);
        grid.add(piantagioneIdField, 1, 0);
        grid.add(comboLabel, 0, 1);
        grid.add(piantagioneCombo, 1, 1);

        parametriContainer.getChildren().add(grid);
    }

    private void mostraParametriPeriodo() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.getStyleClass().add("input-grid");

        Label inizioLabel = new Label("Data inizio:");
        inizioLabel.getStyleClass().add("field-label");

        Label fineLabel = new Label("Data fine:");
        fineLabel.getStyleClass().add("field-label");

        Label zonaLabel = new Label("Zona:");
        zonaLabel.getStyleClass().add("field-label");

        grid.add(inizioLabel, 0, 0);
        grid.add(dataInizioField, 1, 0);
        grid.add(fineLabel, 0, 1);
        grid.add(dataFineField, 1, 1);
        grid.add(zonaLabel, 0, 2);
        grid.add(zonaCombo, 1, 2);

        parametriContainer.getChildren().add(grid);
    }

    private void mostraParametriTopN() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.getStyleClass().add("input-grid");

        Label topLabel = new Label("Top N:");
        topLabel.getStyleClass().add("field-label");

        Label helpLabel = new Label("Mostra le migliori N piantagioni per produttività");
        helpLabel.getStyleClass().add("help-label");

        grid.add(topLabel, 0, 0);
        grid.add(topNSpinner, 1, 0);
        grid.add(helpLabel, 1, 1);

        parametriContainer.getChildren().add(grid);
    }

    private void mostraInfoNessunParametro() {
        Label infoLabel = new Label("ℹ️ Nessun parametro richiesto per questa analisi");
        infoLabel.getStyleClass().add("info-label");
        parametriContainer.getChildren().add(infoLabel);
    }


    private void setupEventHandlers() {
        tipoElaborazioneCombo.setOnAction(e -> aggiornaStrategieDisponibili());
        strategiaCombo.setOnAction(e -> aggiornaParametriVisibili());
        clearBtn.setOnAction(e -> clearResults());
    }

    private void clearResults() {
        risultatoArea.clear();
        salvaRisultatiBtn.setDisable(true);
        statusLabel.setText("Risultati cancellati");
    }



    private String getTipoReportLabel(String tipoReport) {
        return switch (tipoReport) {
            case "completo" -> "Report Completo";
            case "statistiche_generali" -> "Statistiche Generali";
            case "statistiche_mensili" -> "Statistiche Mensili";
            case "periodo_coperto" -> "Periodo Coperto";
            default -> "Report";
        };
    }


    private void setControlsEnabled(boolean enabled) {
        tipoElaborazioneCombo.setDisable(!enabled);
        strategiaCombo.setDisable(!enabled);
        eseguiBtn.setDisable(!enabled);
        aggiornaDatiBtn.setDisable(!enabled);
        clearBtn.setDisable(!enabled);

        // Disabilita anche i controlli nei parametri
        parametriContainer.getChildren().forEach(node -> {
            if (node instanceof GridPane grid) {
                grid.getChildren().forEach(child -> child.setDisable(!enabled));
            } else if (node instanceof VBox vbox) {
                vbox.getChildren().forEach(child -> child.setDisable(!enabled));
            }
        });
    }


    public DataProcessingStrategy.ProcessingType getTipoElaborazioneSelezionato() {
        return tipoElaborazioneCombo.getValue();
    }

    public String getStrategiaSelezionata() {
        return strategiaCombo.getValue();
    }

    public String getPiantagioneId() {
        String fromField = piantagioneIdField.getText();
        String fromCombo = piantagioneCombo.getValue();
        return (fromField != null && !fromField.trim().isEmpty()) ? fromField : fromCombo;
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


    public void updateComboBoxes(java.util.List<String> piantagioni, java.util.List<String> zone) {
        piantagioneCombo.getItems().setAll(piantagioni);
        zonaCombo.getItems().setAll(zone);
    }

    public void mostraRisultato(ProcessingResult<?> result, DataProcessingController.ParametriElaborazione parametri) {
        if (result == null) {
            risultatoArea.setText("Nessun risultato disponibile");
            return;
        }

        StringBuilder output = new StringBuilder();
        output.append("=== RISULTATI ELABORAZIONE ===\n");
        output.append("Strategia: ").append(parametri.strategia()).append("\n");
        output.append("Timestamp: ").append(java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n\n");

        // Formattazione risultati basata sul tipo
        if (result.data() instanceof BigDecimal value) {
            output.append("Valore: ").append(String.format("%.2f", value)).append("\n");
        } else if (result.data() instanceof Map<?, ?> map) {
            formatMapResults(output, map);
        } else if (result.data() instanceof java.util.List<?> list) {
            formatListResults(output, list);
        } else {
            output.append("Risultato: ").append(result.data().toString()).append("\n");
        }

        if (result.metadata() != null && !result.metadata().isEmpty()) {
            output.append("\n=== INFORMAZIONI AGGIUNTIVE ===\n");
            result.metadata().forEach((key, value) ->
                output.append(key).append(": ").append(value).append("\n"));
        }

        risultatoArea.setText(output.toString());
        salvaRisultatiBtn.setDisable(false);
    }

    private void formatMapResults(StringBuilder output, Map<?, ?> map) {
        output.append("Risultati per elemento:\n");
        map.forEach((key, value) -> {
            if (value instanceof BigDecimal bd) {
                output.append("- ").append(key).append(": ").append(String.format("%.2f", bd)).append("\n");
            } else {
                output.append("- ").append(key).append(": ").append(value).append("\n");
            }
        });
    }

    private void formatListResults(StringBuilder output, java.util.List<?> list) {
        output.append("Lista risultati:\n");
        for (int i = 0; i < list.size(); i++) {
            output.append((i + 1)).append(". ").append(list.get(i)).append("\n");
        }
    }

    public void mostraStatistiche(Object statistiche) {
        statusLabel.setText("Statistiche aggiornate: " + statistiche.toString());
    }

    public void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText("Si è verificato un errore");
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }


    public String formatPerSalvataggio(ProcessingResult<?> result) {
        return risultatoArea.getText();
    }


    @SuppressWarnings("unchecked")
    public void mostraReportCompleto(Map<String, Object> reportData) {
        StringBuilder contenuto = new StringBuilder();
        contenuto.append("📋 REPORT COMPLETO RACCOLTI\n");
        contenuto.append("═".repeat(50)).append("\n\n");

        // Statistiche generali
        Map<String, Object> statisticheGenerali = (Map<String, Object>) reportData.get("statisticheGenerali");
        if (statisticheGenerali != null) {
            contenuto.append("📊 STATISTICHE GENERALI\n");
            contenuto.append("─".repeat(25)).append("\n");
            contenuto.append(String.format("▸ Numero totale raccolti: %d\n", statisticheGenerali.get("numeroTotaleRaccolti")));
            contenuto.append(String.format("▸ Produzione totale: %.2f kg\n\n", statisticheGenerali.get("produzioneTotale")));
        }

        // Periodo coperto
        Map<String, Object> periodoCoperto = (Map<String, Object>) reportData.get("periodoCoperto");
        if (periodoCoperto != null && (Boolean) periodoCoperto.get("hasData")) {
            contenuto.append("⏰ PERIODO ANALIZZATO\n");
            contenuto.append("─".repeat(20)).append("\n");
            contenuto.append(String.format("▸ Dal: %s\n", periodoCoperto.get("primaData")));
            contenuto.append(String.format("▸ Al:  %s\n\n", periodoCoperto.get("ultimaData")));
        }

        // Statistiche mensili
        Map<String, Map<String, Object>> raccoltiPerMese =
            (Map<String, Map<String, Object>>) reportData.get("raccoltiPerMese");
        if (raccoltiPerMese != null && !raccoltiPerMese.isEmpty()) {
            contenuto.append("📅 DETTAGLIO MENSILE\n");
            contenuto.append("─".repeat(20)).append("\n");

            raccoltiPerMese.forEach((mese, stats) -> {
                contenuto.append(String.format("\n🗓️  %s\n", formatMese(mese)));
                contenuto.append(String.format("   • Raccolti: %d\n", stats.get("numeroRaccolti")));
                contenuto.append(String.format("   • Produzione: %.2f kg\n", stats.get("totaleProduzione")));
                contenuto.append(String.format("   • Piantagioni coinvolte: %d\n", stats.get("numeroPiantagioniCoinvolte")));
            });
        }

        risultatoArea.setText(contenuto.toString());
        setStatus("Report completo raccolti generato con successo");
    }

    public void mostraStatisticheGenerali(Map<String, Object> statistiche) {

        String contenuto = "📈 STATISTICHE GENERALI RACCOLTI\n" +
                "═".repeat(40) + "\n\n" +
                String.format("▸ Numero totale raccolti: %d\n", statistiche.get("numeroTotaleRaccolti")) +
                String.format("▸ Produzione totale: %.2f kg\n", statistiche.get("produzioneTotale"));

        risultatoArea.setText(contenuto);
        setStatus("Statistiche generali raccolti generate con successo");
    }

    @SuppressWarnings("unchecked")
    public void mostraStatisticheMensili(Map<String, Object> statisticheMensili) {
        StringBuilder contenuto = new StringBuilder();
        contenuto.append("📅 STATISTICHE MENSILI RACCOLTI\n");
        contenuto.append("═".repeat(50)).append("\n\n");

        if (statisticheMensili.isEmpty()) {
            contenuto.append("Nessun dato mensile disponibile.");
        } else {
            statisticheMensili.forEach((mese, statsObj) -> {
                Map<String, Object> stats = (Map<String, Object>) statsObj;
                contenuto.append(String.format("🗓️  %s\n", formatMese(mese)));
                contenuto.append(String.format("   • Raccolti effettuati: %d\n", stats.get("numeroRaccolti")));
                contenuto.append(String.format("   • Produzione totale: %.2f kg\n", stats.get("totaleProduzione")));
                contenuto.append(String.format("   • Piantagioni coinvolte: %d\n\n", stats.get("numeroPiantagioniCoinvolte")));
            });
        }

        risultatoArea.setText(contenuto.toString());
        setStatus("Statistiche mensili raccolti generate con successo");
    }


    public void mostraPeriodoCoperto(Map<String, Object> periodo) {
        StringBuilder contenuto = new StringBuilder();
        contenuto.append("⏰ PERIODO COPERTO DAI RACCOLTI\n");
        contenuto.append("═".repeat(40)).append("\n\n");

        Boolean hasData = (Boolean) periodo.get("hasData");
        if (hasData != null && hasData) {
            contenuto.append(String.format("▸ Data primo raccolto: %s\n", periodo.get("primaData")));
            contenuto.append(String.format("▸ Data ultimo raccolto: %s\n", periodo.get("ultimaData")));

            // Calcola giorni se possibile
            LocalDate primaData = (LocalDate) periodo.get("primaData");
            LocalDate ultimaData = (LocalDate) periodo.get("ultimaData");
            if (primaData != null && ultimaData != null) {
                long giorni = java.time.temporal.ChronoUnit.DAYS.between(primaData, ultimaData);
                contenuto.append(String.format("▸ Periodo totale: %d giorni\n", giorni));
            }
        } else {
            contenuto.append("Nessun dato disponibile sui periodi.");
        }

        risultatoArea.setText(contenuto.toString());
        setStatus("Periodo coperto raccolti calcolato con successo");
    }

    private String formatMese(String meseAnno) {
        String[] parti = meseAnno.split("-");
        if (parti.length != 2) return meseAnno;

        String anno = parti[0];
        String mese = parti[1];

        String[] nomiMesi = {
            "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
            "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"
        };

        try {
            int numeroMese = Integer.parseInt(mese);
            if (numeroMese >= 1 && numeroMese <= 12) {
                return nomiMesi[numeroMese - 1] + " " + anno;
            }
        } catch (NumberFormatException ignored) {}

        return meseAnno;
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

    // Event handlers per il controller
    public void setController(DataProcessingController controller) {
        this.controller = controller;
    }

    public void setOnEseguiElaborazioneListener(Runnable handler) {
        eseguiBtn.setOnAction(e -> handler.run());
    }

    public void setOnAggiornaDati(Runnable handler) {
        aggiornaDatiBtn.setOnAction(e -> handler.run());
    }

    public void setOnSalvaRisultati(Runnable handler) {
        salvaRisultatiBtn.setOnAction(e -> handler.run());
    }
}
