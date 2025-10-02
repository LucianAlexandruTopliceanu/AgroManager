package BusinessLogic;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import BusinessLogic.Exception.DataAccessException;
import BusinessLogic.Service.ErrorService;
import BusinessLogic.Strategy.*;
import DomainModel.*;
import ORM.DAOFactory;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class BusinessLogic {
    private final DataProcessingContext processingContext;

    public BusinessLogic() {
        this.processingContext = new DataProcessingContext();
    }

    public String eseguiStrategia(DataProcessingStrategy.ProcessingType tipo, String strategia, String piantagioneId,
                                  LocalDate dataInizio, LocalDate dataFine, Integer topN) {
        try {
            // Recupera i dati necessari con gestione errori tipizzata
            List<Raccolto> raccolti = DAOFactory.getRaccoltoDAO().findAll();
            List<Piantagione> piantagioni = DAOFactory.getPiantagioneDAO().findAll();
            List<Zona> zone = DAOFactory.getZonaDAO().findAll();

            // Crea la strategia appropriata
            DataProcessingStrategy<?> strategy = StrategyFactory.createStrategy(strategia);

            // Verifica che la strategia sia del tipo richiesto
            if (strategy.getType() != tipo) {
                throw new ValidationException("tipoStrategia", strategia,
                        "non è del tipo richiesto " + tipo);
            }

            // Prepara i parametri e valida
            Object[] params = prepareParameters(strategy, raccolti, piantagioni, zone,
                piantagioneId, dataInizio, dataFine, topN);
            strategy.validateParameters(params);

            // Esegui la strategia e ottieni il risultato formattato
            ProcessingResult<?> result = strategy.execute(params);
            return result.getFormattedOutput();

        } catch (ValidationException e) {
            ErrorService.handleException(e);
            return "Errore di validazione: " + e.getUserMessage();
        } catch (SQLException e) {
            DataAccessException dae = DataAccessException.queryError("recupero dati per elaborazione", e);
            ErrorService.handleException(dae);
            return "Errore accesso dati: " + dae.getUserMessage();
        } catch (Exception e) {
            ErrorService.handleException("elaborazione strategia", e);
            return "Errore durante l'elaborazione: Si è verificato un errore imprevisto";
        }
    }


    private Object[] prepareParameters(DataProcessingStrategy<?> strategy,
                                     List<Raccolto> raccolti,
                                     List<Piantagione> piantagioni,
                                     List<Zona> zone,
                                     String piantagioneId,
                                     LocalDate dataInizio,
                                     LocalDate dataFine,
                                     Integer topN) throws ValidationException {

        // Prepara i parametri in base al tipo di strategia
        if (strategy instanceof ProduzioneTotaleStrategy) {
            return preparePiantagioneParams(raccolti, piantagioneId);

        } else if (strategy instanceof MediaProduzioneStrategy ||
                   strategy instanceof EfficienzaProduttivaStrategy) {
            return preparePiantagioneWithListParams(raccolti, piantagioni, piantagioneId);

        } else if (strategy instanceof ProduzionePerPeriodoStrategy) {
            return preparePeriodoParams(raccolti, dataInizio, dataFine);

        } else if (strategy instanceof TopPiantagioniStrategy) {
            return prepareTopParams(raccolti, topN);

        } else if (strategy instanceof ReportStatisticheZonaStrategy) {
            return new Object[]{raccolti, piantagioni, zone};

        } else if (strategy instanceof ReportRaccoltiStrategy) {
            return new Object[]{raccolti, piantagioni};

        } else {
            return new Object[]{raccolti};
        }
    }


    private Object[] preparePiantagioneParams(List<Raccolto> raccolti, String piantagioneId) throws ValidationException {
        if (piantagioneId == null || piantagioneId.trim().isEmpty()) {
            throw ValidationException.requiredField("ID piantagione");
        }

        try {
            return new Object[]{raccolti, Integer.parseInt(piantagioneId)};
        } catch (NumberFormatException e) {
            throw ValidationException.invalidFormat("ID piantagione", "numero intero");
        }
    }


    private Object[] preparePiantagioneWithListParams(List<Raccolto> raccolti, List<Piantagione> piantagioni,
                                                    String piantagioneId) throws ValidationException {
        if (piantagioneId == null || piantagioneId.trim().isEmpty()) {
            throw ValidationException.requiredField("ID piantagione");
        }

        try {
            return new Object[]{raccolti, piantagioni, Integer.parseInt(piantagioneId)};
        } catch (NumberFormatException e) {
            throw ValidationException.invalidFormat("ID piantagione", "numero intero");
        }
    }


    private Object[] preparePeriodoParams(List<Raccolto> raccolti, LocalDate dataInizio, LocalDate dataFine)
            throws ValidationException {
        if (dataInizio == null) {
            throw ValidationException.requiredField("Data inizio periodo");
        }
        if (dataFine == null) {
            throw ValidationException.requiredField("Data fine periodo");
        }
        if (dataFine.isBefore(dataInizio)) {
            throw new ValidationException("periodo", dataInizio + " - " + dataFine,
                    "la data di fine non può essere precedente alla data di inizio");
        }

        return new Object[]{raccolti, dataInizio, dataFine};
    }


    private Object[] prepareTopParams(List<Raccolto> raccolti, Integer topN) throws ValidationException {
        // Per "Piantagione Migliore" usa 1, per "Top Piantagioni" usa il valore specificato
        int n = topN != null ? topN : 1;

        if (n <= 0) {
            throw new ValidationException("topN", String.valueOf(n),
                    "deve essere maggiore di zero");
        }

        return new Object[]{raccolti, n};
    }


    public boolean isRaccoltoInPeriodo(LocalDate dataRaccolto, LocalDate inizio, LocalDate fine) {
        if (dataRaccolto == null || inizio == null || fine == null) {
            return false;
        }
        return !dataRaccolto.isBefore(inizio) && !dataRaccolto.isAfter(fine);
    }


    public ProcessingResult<?> eseguiStrategiaAvanzata(String nomeStrategia, Object... parametri)
            throws ValidationException, BusinessLogicException, DataAccessException {
        try {
            DataProcessingStrategy<?> strategy = StrategyFactory.createStrategy(nomeStrategia);
            return processingContext.executeStrategy(strategy, parametri);

        } catch (IllegalArgumentException e) {
            throw new ValidationException("Parametri strategia non validi: " + e.getMessage());
        }
    }
}
