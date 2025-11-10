package Controller;

import BusinessLogic.BusinessLogic;
import BusinessLogic.Strategy.DataProcessingStrategy;
import BusinessLogic.Strategy.ProcessingResult;
import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import BusinessLogic.Exception.DataAccessException;
import BusinessLogic.Service.ErrorService;
import View.DataProcessingView;
import View.NotificationHelper;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.Map;

public class DataProcessingController {
    private final DataProcessingView view;
    private final BusinessLogic businessLogic;
    private ProcessingResult<?> ultimoRisultato = null;

    public DataProcessingController(DataProcessingView view, BusinessLogic businessLogic) {
        this.view = view;
        this.businessLogic = businessLogic;


        setupEventHandlers();

        // Imposta il riferimento al controller nella view per i report
        view.setController(this);

        // Inizializza la view con i dati
        initializeView();
    }

    private void setupEventHandlers() {
        view.setOnEseguiElaborazioneListener(this::onEseguiElaborazione);
        view.setOnAggiornaDati(this::onAggiornaDati);
        view.setOnSalvaRisultati(this::onSalvaRisultati);
    }

    private void initializeView() {
        try {
            // Delega al BusinessLogic per ottenere i dati necessari
            var datiComboBox = businessLogic.getDatiPerComboBox();
            view.updateComboBoxes(datiComboBox.get("piantagioni"), datiComboBox.get("zone"));
        } catch (Exception e) {
            ErrorService.handleException("inizializzazione view", e);
        }
    }

    // Handler per eventi della view - solo coordinamento
    private void onEseguiElaborazione() {
        try {
            // 1. Valida input (coordinamento)
            if (!validaInput()) {
                return;
            }

            // 2. Ottieni parametri dalla view
            var parametri = estraiParametriDallaView();

            // 3. Gestione speciale per i report (che hanno emoji nei nomi)
            if (isReportStrategy(parametri.strategia())) {
                String tipoReport = mapStrategiaToReportType(parametri.strategia());
                elaboraReportRaccolti(tipoReport);
                return;
            }

            // 4. Delega elaborazione normale al BusinessLogic
            ProcessingResult<?> result = businessLogic.eseguiStrategiaConDati(
                parametri.tipo(),
                parametri.strategia(),
                parametri.piantagioneId(),
                parametri.dataInizio(),
                parametri.dataFine(),
                parametri.topN()
            );

            // 5. Passa i dati puri alla view per la presentazione
            ultimoRisultato = result;
            view.mostraRisultato(result, parametri);

        } catch (ValidationException | DataAccessException | BusinessLogicException e) {
            ErrorService.handleException(e);
            view.mostraErrore("Operazione non completata. Controllare i dati inseriti.");
        } catch (Exception e) {
            ErrorService.handleException("elaborazione dati", e);
            view.mostraErrore("Errore imprevisto durante l'elaborazione.");
        }
    }

    private boolean validaInput() {
        String strategia = view.getStrategiaSelezionata();
        if (strategia == null || strategia.trim().isEmpty()) {
            view.mostraErrore("Seleziona una strategia di elaborazione");
            return false;
        }

        // Validazione specifica per strategia
        return switch (strategia) {
            case "Produzione Totale", "Media per Pianta", "Efficienza Produttiva" -> {
                String piantagioneId = view.getPiantagioneId();
                if (piantagioneId == null || piantagioneId.trim().isEmpty()) {
                    view.mostraErrore("Inserisci l'ID della piantagione o selezionala dal menu");
                    yield false;
                }
                yield true;
            }
            case "Produzione per Periodo" -> {
                if (view.getDataInizio() == null || view.getDataFine() == null) {
                    view.mostraErrore("Seleziona sia la data di inizio che quella di fine per il periodo");
                    yield false;
                }
                yield true;
            }
            case "Top Piantagioni" -> {
                if (view.getTopN() == null || view.getTopN() < 1) {
                    view.mostraErrore("Inserisci un numero valido per le top piantagioni (minimo 1)");
                    yield false;
                }
                yield true;
            }
            default -> true;
        };
    }

    private ParametriElaborazione estraiParametriDallaView() {
        return new ParametriElaborazione(
            view.getTipoElaborazioneSelezionato(),
            view.getStrategiaSelezionata(),
            view.getPiantagioneId(),
            view.getDataInizio(),
            view.getDataFine(),
            view.getTopN()
        );
    }

    private void onAggiornaDati() {
        try {
            var datiComboBox = businessLogic.getDatiPerComboBox();
            view.updateComboBoxes(datiComboBox.get("piantagioni"), datiComboBox.get("zone"));
        } catch (Exception e) {
            ErrorService.handleException("aggiornamento dati", e);
        }
    }

    private void onSalvaRisultati() {
        if (ultimoRisultato == null) {
            NotificationHelper.showWarning("Attenzione", "Nessun risultato da salvare");
            return;
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salva Risultati Analisi");
            fileChooser.setInitialFileName("analisi_agricola_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".txt");

            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("File di testo", "*.txt"),
                new FileChooser.ExtensionFilter("Tutti i file", "*.*")
            );

            Stage stage = (Stage) view.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                // Delega alla view la formattazione per il salvataggio
                String contenutoFormattato = view.formatPerSalvataggio(ultimoRisultato);

                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(contenutoFormattato);

                    view.setStatus("Risultati salvati: " + file.getName());
                    NotificationHelper.showSuccess("Salvataggio Completato",
                        "Risultati salvati con successo in:\n" + file.getAbsolutePath());
                }
            }

        } catch (IOException e) {
            view.setStatus("Errore nel salvataggio");
            NotificationHelper.showError("Errore Salvataggio",
                "Errore durante il salvataggio:\n" + e.getMessage());
        } catch (Exception e) {
            view.setStatus("Errore nel salvataggio");
            NotificationHelper.showError("Errore Salvataggio",
                "Errore imprevisto durante il salvataggio");
        }
    }


    public void elaboraReportRaccolti(String tipoReport) {
        try {
            // Verifica prima se ci sono dati disponibili
            if (!businessLogic.getReportService().hasRaccoltiDisponibili()) {
                view.mostraErrore("Nessun raccolto disponibile nel database per generare report");
                return;
            }

            ProcessingResult<Map<String, Object>> result;

            switch (tipoReport.toLowerCase()) {
                case "completo" -> {
                    result = businessLogic.getReportService().generaReportCompleto();
                    view.mostraReportCompleto(result.data());
                }
                case "statistiche_generali" -> {
                    result = businessLogic.getReportService().generaStatisticheGenerali();
                    view.mostraStatisticheGenerali(result.data());
                }
                case "statistiche_mensili" -> {
                    result = businessLogic.getReportService().generaStatisticheMensili();
                    view.mostraStatisticheMensili(result.data());
                }
                case "periodo_coperto" -> {
                    result = businessLogic.getReportService().calcolaPeriodoCoperto();
                    view.mostraPeriodoCoperto(result.data());
                }
                default -> {
                    view.mostraErrore("Tipo di report non riconosciuto: " + tipoReport);
                    return;
                }
            }

            // Salva il risultato per eventuale esportazione
            ultimoRisultato = result;

        } catch (DataAccessException e) {
            ErrorService.handleException(e);
            view.mostraErrore("Errore di accesso ai dati durante la generazione del report");
        } catch (BusinessLogicException e) {
            ErrorService.handleException(e);
            view.mostraErrore("Errore di business logic: " + e.getUserMessage());
        } catch (ValidationException e) {
            ErrorService.handleException(e);
            view.mostraErrore("Errore di validazione: " + e.getUserMessage());
        } catch (Exception e) {
            ErrorService.handleException("elaborazione report " + tipoReport, e);
            view.mostraErrore("Errore imprevisto durante l'elaborazione del report");
        }
    }

    private boolean isReportStrategy(String strategia) {
        return strategia != null && (
            strategia.startsWith("📋") ||
            strategia.startsWith("📊") ||
            strategia.startsWith("📅") ||
            strategia.startsWith("⏰")
        );
    }

    private String mapStrategiaToReportType(String strategia) {
        return switch (strategia) {
            case "📋 Report Completo" -> "completo";
            case "📊 Statistiche Generali" -> "statistiche_generali";
            case "📅 Analisi Mensile" -> "statistiche_mensili";
            case "⏰ Periodo Coperto" -> "periodo_coperto";
            default -> "completo";
        };
    }

    // Record per parametri - immutabile
    public record ParametriElaborazione(
        DataProcessingStrategy.ProcessingType tipo,
        String strategia,
        String piantagioneId,
        LocalDate dataInizio,
        LocalDate dataFine,
        Integer topN
    ) {}

}
