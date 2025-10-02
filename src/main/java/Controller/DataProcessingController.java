package Controller;

import BusinessLogic.BusinessLogic;
import BusinessLogic.Strategy.DataProcessingStrategy;
import View.DataProcessingView;
import View.NotificationHelper;
import DomainModel.*;
import ORM.DAOFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DataProcessingController {
    private final DataProcessingView view;
    private final BusinessLogic businessLogic;
    private String ultimoRisultato = "";

    public DataProcessingController(DataProcessingView view, BusinessLogic businessLogic) {
        this.view = view;
        this.businessLogic = businessLogic;

        // Configura i callback
        view.setOnEseguiElaborazioneListener(this::eseguiElaborazione);
        view.setOnAggiornaDati(this::aggiornaDati);
        view.setOnSalvaRisultati(this::salvaRisultati);

        // Carica dati iniziali
        caricaDatiComboBoxes();
    }

    private String eseguiElaborazione() {
        try {
            // Validazione preventiva
            String validationError = validaInput();
            if (validationError != null) {
                return "❌ " + validationError;
            }

            // Ottieni i parametri dalla view
            DataProcessingStrategy.ProcessingType tipo = view.getTipoElaborazioneSelezionato();
            String strategia = view.getStrategiaSelezionata();

            // Verifica disponibilità dati
            List<Raccolto> raccolti = DAOFactory.getRaccoltoDAO().findAll();
            if (raccolti.isEmpty()) {
                return "⚠️ Nessun raccolto trovato nel database.\n" +
                       "💡 Suggerimento: Aggiungi alcuni raccolti prima di eseguire l'analisi.";
            }

            // Log dell'operazione
            System.out.println("Eseguendo analisi: " + tipo + " - " + strategia);

            // Esegui la strategia appropriata
            String risultato = businessLogic.eseguiStrategia(
                tipo,
                strategia,
                view.getPiantagioneId(),
                view.getDataInizio(),
                view.getDataFine(),
                view.getTopN()
            );

            // Arricchisci il risultato con informazioni aggiuntive
            ultimoRisultato = arricchisciRisultato(risultato, tipo, strategia);
            return ultimoRisultato;

        } catch (NumberFormatException e) {
            return "❌ Errore di formato: Inserire un numero valido per l'ID piantagione.";
        } catch (Exception e) {
            System.err.println("Errore durante l'elaborazione: " + e.getMessage());
            e.printStackTrace();
            return "❌ Errore durante l'elaborazione: " + e.getMessage() + "\n" +
                   "💡 Verifica che tutti i dati necessari siano presenti nel database.";
        }
    }

    private String validaInput() {
        String strategia = view.getStrategiaSelezionata();
        if (strategia == null || strategia.trim().isEmpty()) {
            return "Seleziona una strategia di elaborazione";
        }

        // Validazione specifica per strategia
        switch (strategia) {
            case "Produzione Totale", "Media per Pianta", "Efficienza Produttiva" -> {
                String piantagioneId = view.getPiantagioneId();
                if (piantagioneId == null || piantagioneId.trim().isEmpty()) {
                    return "Inserisci l'ID della piantagione o selezionala dal menu";
                }
            }
            case "Produzione per Periodo" -> {
                if (view.getDataInizio() == null || view.getDataFine() == null) {
                    return "Seleziona sia la data di inizio che quella di fine per il periodo";
                }
            }
            case "Top Piantagioni" -> {
                if (view.getTopN() == null || view.getTopN() < 1) {
                    return "Inserisci un numero valido per le top piantagioni (minimo 1)";
                }
            }
        }

        return null; // Validazione OK
    }

    private String arricchisciRisultato(String risultatoBase, DataProcessingStrategy.ProcessingType tipo, String strategia) {
        if (risultatoBase.startsWith("❌")) {
            return risultatoBase; // Non arricchire i messaggi di errore
        }

        StringBuilder builder = new StringBuilder();

        // Header informativo
        builder.append("📊 ANALISI DATI AGRICOLI\n");
        builder.append("═".repeat(50)).append("\n");
        builder.append("🕒 Generato il: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        builder.append("📋 Tipo: ").append(tipo).append("\n");
        builder.append("🎯 Strategia: ").append(strategia).append("\n");
        builder.append("═".repeat(50)).append("\n\n");

        // Risultato principale
        builder.append(risultatoBase);

        // Footer con suggerimenti
        builder.append("\n\n").append("═".repeat(50)).append("\n");
        builder.append("💡 SUGGERIMENTI:\n");

        switch (strategia) {
            case "Produzione Totale" ->
                builder.append("• Confronta con dati di periodi precedenti\n")
                       .append("• Considera fattori climatici e stagionali");
            case "Top Piantagioni" ->
                builder.append("• Analizza i fattori di successo delle migliori piantagioni\n")
                       .append("• Considera di replicare le tecniche vincenti");
            case "Statistiche Zone" ->
                builder.append("• Identifica le zone più produttive\n")
                       .append("• Pianifica investimenti basati sui dati");
            default ->
                builder.append("• Salva questi risultati per confronti futuri\n")
                       .append("• Considera di eseguire analisi correlate");
        }

        return builder.toString();
    }

    private void aggiornaDati() {
        try {
            view.setStatus("Aggiornamento dati in corso...");

            // Ricarica i dati dal database
            List<Raccolto> raccolti = DAOFactory.getRaccoltoDAO().findAll();
            List<Piantagione> piantagioni = DAOFactory.getPiantagioneDAO().findAll();
            List<Zona> zone = DAOFactory.getZonaDAO().findAll();
            List<Pianta> piante = DAOFactory.getPiantaDAO().findAll();

            // Aggiorna le combo boxes
            caricaDatiComboBoxes();

            // Mostra statistiche di aggiornamento
            String statistiche = String.format(
                "✅ DATI AGGIORNATI CON SUCCESSO\n\n" +
                "📈 Statistiche Database:\n" +
                "• Raccolti: %d\n" +
                "• Piantagioni: %d\n" +
                "• Zone: %d\n" +
                "• Piante: %d\n\n" +
                "🕒 Ultimo aggiornamento: %s",
                raccolti.size(), piantagioni.size(), zone.size(), piante.size(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            );

            view.setRisultato(statistiche);
            view.setStatus("Dati aggiornati");

            NotificationHelper.showSuccess("Aggiornamento Completato", "Dati aggiornati con successo!");

        } catch (Exception e) {
            String errore = "❌ Errore durante l'aggiornamento: " + e.getMessage();
            view.setRisultato(errore);
            view.setStatus("Errore aggiornamento");
            NotificationHelper.showError("Errore Aggiornamento", "Errore durante l'aggiornamento dei dati");
        }
    }

    private void caricaDatiComboBoxes() {
        try {
            // Carica piantagioni per la combo box
            List<Piantagione> piantagioni = DAOFactory.getPiantagioneDAO().findAll();
            List<String> piantagioniItems = piantagioni.stream()
                .map(p -> p.getId() + " - Zona: " + p.getZonaId() + " (" + p.getQuantitaPianta() + " piante)")
                .collect(Collectors.toList());

            // Carica zone per la combo box
            List<Zona> zone = DAOFactory.getZonaDAO().findAll();
            List<String> zoneItems = zone.stream()
                .map(z -> z.getId() + " - " + z.getNome())
                .collect(Collectors.toList());

            view.updateComboBoxes(piantagioniItems, zoneItems);

        } catch (Exception e) {
            System.err.println("Errore nel caricamento dati combo boxes: " + e.getMessage());
        }
    }

    private void salvaRisultati() {
        if (ultimoRisultato == null || ultimoRisultato.trim().isEmpty()) {
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

            // Ottieni lo stage dalla view
            Stage stage = (Stage) view.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(ultimoRisultato);

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

    public void refreshData() {
        aggiornaDati();
    }
}
