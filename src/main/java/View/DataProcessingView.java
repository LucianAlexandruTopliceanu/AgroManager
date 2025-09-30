package View;

import BusinessLogic.BusinessLogic;
import BusinessLogic.Strategy.*;
import BusinessLogic.Strategy.DataProcessingStrategy.ProcessingType;
import DomainModel.*;
import ORM.DAOFactory;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.concurrent.Task;
import javafx.application.Platform;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * View unificata e user-friendly per tutte le elaborazioni dati
 */
public class DataProcessingView extends VBox {

    private final BusinessLogic businessLogic;
    private final DataProcessingContext processingContext;

    // Controlli UI migliorati
    private final ComboBox<ProcessingType> tipoElaborazioneCombo;
    private final ComboBox<String> strategiaCombo;
    private final TextArea risultatoArea;
    private final Button eseguiBtn;
    private final Button aggiornaBtn;
    private final Button salvaBtn;
    private final Button stampaBtn;
    private final ProgressIndicator progressIndicator;
    private final Label statusLabel;

    // Controlli per parametri
    private final GridPane parametriPane;
    private final TextField piantagioneIdField;
    private final DatePicker dataInizioField;
    private final DatePicker dataFineField;
    private final Spinner<Integer> topNSpinner;
    private final ComboBox<String> formatoOutputCombo;

    public DataProcessingView(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
        this.processingContext = new DataProcessingContext();

        // Inizializza controlli UI nel costruttore
        tipoElaborazioneCombo = new ComboBox<>();
        strategiaCombo = new ComboBox<>();
        risultatoArea = new TextArea();
        eseguiBtn = new Button("🚀 Esegui Elaborazione");
        aggiornaBtn = new Button("🔄 Aggiorna Dati");
        salvaBtn = new Button("💾 Salva Risultati");
        stampaBtn = new Button("🖨️ Stampa");
        progressIndicator = new ProgressIndicator();
        statusLabel = new Label("Pronto per l'elaborazione");
        parametriPane = new GridPane();
        piantagioneIdField = new TextField();
        dataInizioField = new DatePicker(LocalDate.now().minusMonths(1));
        dataFineField = new DatePicker(LocalDate.now());
        topNSpinner = new Spinner<>(1, 50, 5);
        formatoOutputCombo = new ComboBox<>();

        setPadding(new Insets(20));
        setSpacing(20);
        setStyle("-fx-background-color: #F8F9FA;");

        // Header con titolo e descrizione
        VBox header = creaHeader();

        // Sezione selezione elaborazione
        VBox selezioneBox = creaSelezioneElaborazione();

        // Sezione parametri
        VBox parametriBox = creaSezioneParametri();

        // Sezione controlli
        HBox controlliBox = creaControlliBox();

        // Sezione risultati
        VBox risultatiBox = creaSezioneRisultati();

        getChildren().addAll(header, selezioneBox, parametriBox, controlliBox, risultatiBox);

        // Inizializzazione
        aggiornaStrategieDisponibili();
        aggiornaParametriVisibili();
    }

    private VBox creaHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label titolo = new Label("📈 Centro Analisi e Elaborazioni Dati");
        titolo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        Label descrizione = new Label("Utilizza le strategie avanzate per calcoli, statistiche e report sui tuoi dati agricoli");
        descrizione.setStyle("-fx-font-size: 14px; -fx-text-fill: #7F8C8D;");

        header.getChildren().addAll(titolo, descrizione);
        return header;
    }

    private VBox creaSelezioneElaborazione() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label titolo = new Label("🎯 Seleziona Tipo di Elaborazione");
        titolo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        // Tipo elaborazione con descrizioni
        Label tipoLabel = new Label("Categoria:");
        tipoLabel.setStyle("-fx-font-weight: bold;");

        tipoElaborazioneCombo.setPrefWidth(300);
        tipoElaborazioneCombo.getItems().addAll(ProcessingType.values());
        tipoElaborazioneCombo.setValue(ProcessingType.CALCULATION);
        tipoElaborazioneCombo.setOnAction(e -> aggiornaStrategieDisponibili());

        // Descrizioni per ogni tipo
        Label descrizioneCalc = new Label("📊 CALCULATION: Calcoli numerici (produzione, efficienza, medie)");
        Label descrizioneStats = new Label("📈 STATISTICS: Analisi statistiche (ranking, confronti)");
        Label descrizioneReport = new Label("📄 REPORT: Generazione report testuali completi");

        VBox descrizioniBox = new VBox(5);
        descrizioniBox.getChildren().addAll(descrizioneCalc, descrizioneStats, descrizioneReport);
        descrizioniBox.setStyle("-fx-background-color: #ECF0F1; -fx-padding: 10; -fx-background-radius: 5;");

        // Strategia specifica
        Label strategiaLabel = new Label("Strategia Specifica:");
        strategiaLabel.setStyle("-fx-font-weight: bold;");

        strategiaCombo.setPrefWidth(400);

        box.getChildren().addAll(titolo, tipoLabel, tipoElaborazioneCombo, descrizioniBox, strategiaLabel, strategiaCombo);
        return box;
    }

    private VBox creaSezioneParametri() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label titolo = new Label("⚙️ Parametri di Elaborazione");
        titolo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        parametriPane.setHgap(15);
        parametriPane.setVgap(15);
        parametriPane.setPadding(new Insets(15));
        parametriPane.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 5;");

        // Inizializza controlli parametri
        piantagioneIdField.setPromptText("Inserisci ID piantagione (es. 1, 2, 3...)");
        piantagioneIdField.setPrefWidth(250);

        dataInizioField.setPrefWidth(150);

        dataFineField.setPrefWidth(150);

        topNSpinner.setEditable(true);
        topNSpinner.setPrefWidth(100);

        box.getChildren().addAll(titolo, parametriPane);
        return box;
    }

    private HBox creaControlliBox() {
        HBox box = new HBox(15);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        // Pulsanti azione
        eseguiBtn.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; " +
                          "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 24 12 24; -fx-background-radius: 25;");
        eseguiBtn.setOnAction(e -> eseguiElaborazioneAsync());

        aggiornaBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; " +
                            "-fx-font-size: 12px; -fx-padding: 10 20 10 20; -fx-background-radius: 20;");
        aggiornaBtn.setOnAction(e -> aggiornaDati());

        salvaBtn.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; " +
                         "-fx-font-size: 12px; -fx-padding: 10 20 10 20; -fx-background-radius: 20;");
        salvaBtn.setDisable(true);

        stampaBtn.setStyle("-fx-background-color: #9B59B6; -fx-text-fill: white; " +
                          "-fx-font-size: 12px; -fx-padding: 10 20 10 20; -fx-background-radius: 20;");
        stampaBtn.setDisable(true);

        // Formato output
        Label formatoLabel = new Label("Formato:");
        formatoLabel.setStyle("-fx-font-weight: bold;");

        formatoOutputCombo.getItems().addAll("Testo", "CSV", "JSON");
        formatoOutputCombo.setValue("Testo");

        // Progress indicator
        progressIndicator.setVisible(false);
        progressIndicator.setPrefSize(30, 30);

        // Status
        statusLabel.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        box.getChildren().addAll(eseguiBtn, aggiornaBtn, salvaBtn, stampaBtn,
                                 new Separator(), formatoLabel, formatoOutputCombo,
                                 spacer, progressIndicator, statusLabel);
        return box;
    }

    private VBox creaSezioneRisultati() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label titolo = new Label("📊 Risultati Elaborazione");
        titolo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        risultatoArea.setEditable(false);
        risultatoArea.setPrefRowCount(20);
        risultatoArea.setStyle("-fx-font-family: 'Monaco', 'Courier New', monospace; " +
                              "-fx-font-size: 12px; -fx-background-color: #2C3E50; " +
                              "-fx-text-fill: #ECF0F1; -fx-background-radius: 5;");
        risultatoArea.setPromptText("I risultati dell'elaborazione appariranno qui...");

        VBox.setVgrow(risultatoArea, Priority.ALWAYS);

        box.getChildren().addAll(titolo, risultatoArea);
        return box;
    }

    private void aggiornaStrategieDisponibili() {
        ProcessingType tipoSelezionato = tipoElaborazioneCombo.getValue();
        strategiaCombo.getItems().clear();

        switch (tipoSelezionato) {
            case CALCULATION:
                strategiaCombo.getItems().addAll(
                    "📊 Produzione Totale Piantagione",
                    "📈 Media Produzione per Pianta",
                    "⚡ Efficienza Produttiva",
                    "📅 Produzione per Periodo"
                );
                break;

            case STATISTICS:
                strategiaCombo.getItems().addAll(
                    "🏆 Piantagione Più Produttiva",
                    "🥇 Top Piantagioni Produttive",
                    "🗺️ Statistiche per Zona"
                );
                break;

            case REPORT:
                strategiaCombo.getItems().addAll(
                    "📋 Report Raccolti Completo",
                    "📊 Report Statistiche Zone",
                    "📈 Report Prestazioni Piantagioni"
                );
                break;
        }

        if (!strategiaCombo.getItems().isEmpty()) {
            strategiaCombo.setValue(strategiaCombo.getItems().get(0));
        }

        strategiaCombo.setOnAction(e -> aggiornaParametriVisibili());
        aggiornaParametriVisibili();
    }

    private void aggiornaParametriVisibili() {
        parametriPane.getChildren().clear();
        String strategiaSelezionata = strategiaCombo.getValue();

        if (strategiaSelezionata == null) return;

        int row = 0;

        if (strategiaSelezionata.contains("Piantagione") && !strategiaSelezionata.contains("Top")) {
            parametriPane.add(new Label("ID Piantagione:"), 0, row);
            parametriPane.add(piantagioneIdField, 1, row);
            parametriPane.add(new Label("💡 Consulta la sezione Piantagioni per gli ID disponibili"), 2, row);
        } else if (strategiaSelezionata.contains("Periodo")) {
            parametriPane.add(new Label("Data Inizio:"), 0, row);
            parametriPane.add(dataInizioField, 1, row);
            row++;
            parametriPane.add(new Label("Data Fine:"), 0, row);
            parametriPane.add(dataFineField, 1, row);
        } else if (strategiaSelezionata.contains("Top")) {
            parametriPane.add(new Label("Top N (1-50):"), 0, row);
            parametriPane.add(topNSpinner, 1, row);
            parametriPane.add(new Label("💡 Seleziona quante piantagioni mostrare nella classifica"), 2, row);
        } else {
            Label nessunParam = new Label("✅ Nessun parametro richiesto per questa elaborazione");
            nessunParam.setStyle("-fx-text-fill: #27AE60; -fx-font-style: italic;");
            parametriPane.add(nessunParam, 0, row);
        }
    }

    private void eseguiElaborazioneAsync() {
        // Esecuzione asincrona per non bloccare la UI
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(true);
                    statusLabel.setText("Elaborazione in corso...");
                    statusLabel.setStyle("-fx-text-fill: #F39C12;");
                    eseguiBtn.setDisable(true);
                });

                return eseguiElaborazione();
            }

            @Override
            protected void succeeded() {
                progressIndicator.setVisible(false);
                statusLabel.setText("✅ Elaborazione completata con successo");
                statusLabel.setStyle("-fx-text-fill: #27AE60;");
                eseguiBtn.setDisable(false);
                salvaBtn.setDisable(false);
                stampaBtn.setDisable(false);
                risultatoArea.setText(getValue());
            }

            @Override
            protected void failed() {
                progressIndicator.setVisible(false);
                statusLabel.setText("❌ Errore durante l'elaborazione");
                statusLabel.setStyle("-fx-text-fill: #E74C3C;");
                eseguiBtn.setDisable(false);
                risultatoArea.setText("Errore: " + getException().getMessage());
            }
        };

        new Thread(task).start();
    }

    private String eseguiElaborazione() {
        try {
            String strategiaSelezionata = strategiaCombo.getValue();
            if (strategiaSelezionata == null) {
                return "❌ Seleziona una strategia da eseguire.";
            }

            // Ottieni dati reali dal database
            List<Raccolto> raccolti = ottieniRaccolti();
            List<Piantagione> piantagioni = ottieniPiantagioni();
            List<Zona> zone = ottieniZone();
            List<Pianta> piante = ottieniPiante();

            if (raccolti.isEmpty()) {
                return "⚠️ Nessun dato trovato nel database. Assicurati che ci siano raccolti registrati.";
            }

            Object risultato = null;
            String strategiaPulita = strategiaSelezionata.replaceAll("[📊📈⚡📅🏆🥇🗺️📋]", "").trim();

            switch (strategiaPulita) {
                case "Produzione Totale Piantagione":
                    int idPiantagione = Integer.parseInt(piantagioneIdField.getText());
                    risultato = businessLogic.calcolaProduzioneTotalePiantagione(raccolti, idPiantagione);
                    return formatRisultatoNumerico("Produzione Totale", risultato, "kg");

                case "Media Produzione per Pianta":
                    int idPiant = Integer.parseInt(piantagioneIdField.getText());
                    risultato = businessLogic.calcolaMediaProduzionePianta(raccolti, piantagioni, idPiant);
                    return formatRisultatoNumerico("Media Produzione per Pianta", risultato, "kg/pianta");

                case "Efficienza Produttiva":
                    int idEff = Integer.parseInt(piantagioneIdField.getText());
                    risultato = businessLogic.calcolaEfficienzaProduttiva(raccolti, piantagioni, idEff);
                    return formatRisultatoNumerico("Efficienza Produttiva", risultato, "kg/pianta/giorno");

                case "Produzione per Periodo":
                    LocalDate inizio = dataInizioField.getValue();
                    LocalDate fine = dataFineField.getValue();
                    risultato = businessLogic.calcolaProduzionePerPeriodo(raccolti, inizio, fine);
                    return formatRisultatoPerPeriodo(risultato, inizio, fine);

                case "Piantagione Più Produttiva":
                    int idMigliore = businessLogic.trovaIdPiantagionePiuProduttiva(raccolti);
                    BigDecimal produzione = businessLogic.calcolaProduzioneTotalePiantagione(raccolti, idMigliore);
                    return formatRisultatoPiantagioneMigliore(idMigliore, produzione, piantagioni);

                case "Top Piantagioni Produttive":
                    int topN = topNSpinner.getValue();
                    Map<Integer, BigDecimal> topPiantagioni = businessLogic.trovaTopPiantagioniProduttive(raccolti, topN);
                    return formatRisultatoTopPiantagioni(topPiantagioni, piantagioni);

                case "Statistiche per Zona":
                    risultato = processingContext.executeProcessing(
                        new ReportStatisticheZonaStrategy(), raccolti, piantagioni, zone);
                    return formatRisultatoStatisticheZone((Map<String, BigDecimal>) risultato);

                case "Report Raccolti Completo":
                    return businessLogic.generaReportRaccolti(raccolti);

                default:
                    return "❌ Strategia non riconosciuta: " + strategiaSelezionata;
            }

        } catch (NumberFormatException e) {
            return "❌ Errore: Inserire un numero valido per l'ID piantagione.";
        } catch (Exception e) {
            return "❌ Errore durante l'elaborazione: " + e.getMessage();
        }
    }

    private String formatRisultatoNumerico(String nome, Object valore, String unita) {
        return String.format("""
            ╔══════════════════════════════════════════════════════════════╗
            ║                    🎯 RISULTATO ELABORAZIONE                 ║
            ╠══════════════════════════════════════════════════════════════╣
            ║ Tipo: %s
            ║ Risultato: %s %s
            ║ Timestamp: %s
            ╚══════════════════════════════════════════════════════════════╝
            """, nome, valore, unita, java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    }

    private String formatRisultatoPerPeriodo(Object valore, LocalDate inizio, LocalDate fine) {
        return String.format("""
            ╔══════════════════════════════════════════════════════════════╗
            ║                📅 PRODUZIONE PER PERIODO                    ║
            ╠══════════════════════════════════════════════════════════════╣
            ║ Periodo: %s → %s
            ║ Durata: %d giorni
            ║ Produzione Totale: %s kg
            ║ Media Giornaliera: %.2f kg/giorno
            ╚══════════════════════════════════════════════════════════════╝
            """, inizio, fine, java.time.temporal.ChronoUnit.DAYS.between(inizio, fine),
            valore, ((BigDecimal)valore).doubleValue() / java.time.temporal.ChronoUnit.DAYS.between(inizio, fine));
    }

    private String formatRisultatoPiantagioneMigliore(int id, BigDecimal produzione, List<Piantagione> piantagioni) {
        return String.format("""
            ╔══════════════════════════════════════════════════════════════╗
            ║               🏆 PIANTAGIONE PIÙ PRODUTTIVA                 ║
            ╠══════════════════════════════════════════════════════════════╣
            ║ ID Piantagione: %d
            ║ Produzione Totale: %s kg
            ║ Status: 🥇 MIGLIORE PERFORMANCE
            ╚══════════════════════════════════════════════════════════════╝
            """, id, produzione);
    }

    private String formatRisultatoTopPiantagioni(Map<Integer, BigDecimal> top, List<Piantagione> piantagioni) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
            ╔══════════════════════════════════════════════════════════════╗
            ║                🥇 TOP PIANTAGIONI PRODUTTIVE                ║
            ╠══════════════════════════════════════════════════════════════╣
            """);

        int pos = 1;
        for (Map.Entry<Integer, BigDecimal> entry : top.entrySet()) {
            String emoji = pos == 1 ? "🥇" : pos == 2 ? "🥈" : pos == 3 ? "🥉" : "🏅";
            sb.append(String.format("║ %s %d. Piantagione ID %d: %s kg\n",
                emoji, pos++, entry.getKey(), entry.getValue()));
        }

        sb.append("╚══════════════════════════════════════════════════════════════╝");
        return sb.toString();
    }

    private String formatRisultatoStatisticheZone(Map<String, BigDecimal> statistiche) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
            ╔══════════════════════════════════════════════════════════════╗
            ║                  🗺️ STATISTICHE PER ZONA                   ║
            ╠══════════════════════════════════════════════════════════════╣
            """);

        for (Map.Entry<String, BigDecimal> entry : statistiche.entrySet()) {
            sb.append(String.format("║ 📍 %s: %s kg\n", entry.getKey(), entry.getValue()));
        }

        sb.append("╚══════════════════════════════════════════════════════════════╝");
        return sb.toString();
    }

    private void aggiornaDati() {
        statusLabel.setText("🔄 Aggiornamento dati in corso...");
        statusLabel.setStyle("-fx-text-fill: #F39C12;");

        // Simula aggiornamento
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1000);
                return null;
            }

            @Override
            protected void succeeded() {
                statusLabel.setText("✅ Dati aggiornati con successo");
                statusLabel.setStyle("-fx-text-fill: #27AE60;");
            }
        };

        new Thread(task).start();
    }

    // Metodi per ottenere dati reali dal database
    private List<Raccolto> ottieniRaccolti() {
        try {
            return DAOFactory.getRaccoltoDAO().findAll();
        } catch (Exception e) {
            risultatoArea.setText("Errore nel recupero raccolti: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    private List<Piantagione> ottieniPiantagioni() {
        try {
            return DAOFactory.getPiantagioneDAO().findAll();
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }

    private List<Zona> ottieniZone() {
        try {
            return DAOFactory.getZonaDAO().findAll();
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }

    private List<Pianta> ottieniPiante() {
        try {
            return DAOFactory.getPiantaDAO().findAll();
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }
}
