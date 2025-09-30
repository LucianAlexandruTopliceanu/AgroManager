package BusinessLogic;

import BusinessLogic.Strategy.*;
import DomainModel.*;
import ORM.DAOFactory;
import java.math.BigDecimal;
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
            // Recupera i dati necessari
            List<Raccolto> raccolti = DAOFactory.getRaccoltoDAO().findAll();
            List<Piantagione> piantagioni = DAOFactory.getPiantagioneDAO().findAll();
            List<Zona> zone = DAOFactory.getZonaDAO().findAll();

            // Crea la strategia appropriata
            DataProcessingStrategy<?> strategy = StrategyFactory.createStrategy(strategia);

            // Verifica che la strategia sia del tipo richiesto
            if (strategy.getType() != tipo) {
                return "Errore: La strategia selezionata non è del tipo " + tipo;
            }

            // Prepara i parametri e valida
            Object[] params = prepareParameters(strategy, raccolti, piantagioni, zone,
                piantagioneId, dataInizio, dataFine, topN);
            strategy.validateParameters(params);

            // Esegui la strategia e ottieni il risultato formattato
            ProcessingResult<?> result = strategy.execute(params);
            return result.getFormattedOutput();
        } catch (IllegalArgumentException e) {
            return "Errore di validazione: " + e.getMessage();
        } catch (Exception e) {
            return "Errore durante l'elaborazione: " + e.getMessage();
        }
    }

    private Object[] prepareParameters(DataProcessingStrategy<?> strategy,
                                     List<Raccolto> raccolti,
                                     List<Piantagione> piantagioni,
                                     List<Zona> zone,
                                     String piantagioneId,
                                     LocalDate dataInizio,
                                     LocalDate dataFine,
                                     Integer topN) {
        // Prepara i parametri in base al tipo di strategia
        if (strategy instanceof ProduzioneTotaleStrategy) {
            // Gestisce il caso di ID piantagione non valido
            if (piantagioneId == null || piantagioneId.trim().isEmpty()) {
                throw new IllegalArgumentException("ID piantagione richiesto per questa strategia");
            }
            try {
                return new Object[]{raccolti, Integer.parseInt(piantagioneId)};
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("ID piantagione deve essere un numero valido");
            }
        } else if (strategy instanceof MediaProduzioneStrategy ||
                   strategy instanceof EfficienzaProduttivaStrategy) {
            if (piantagioneId == null || piantagioneId.trim().isEmpty()) {
                throw new IllegalArgumentException("ID piantagione richiesto per questa strategia");
            }
            try {
                return new Object[]{raccolti, piantagioni, Integer.parseInt(piantagioneId)};
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("ID piantagione deve essere un numero valido");
            }
        } else if (strategy instanceof ProduzionePerPeriodoStrategy) {
            if (dataInizio == null || dataFine == null) {
                throw new IllegalArgumentException("Date di inizio e fine richieste per questa strategia");
            }
            return new Object[]{raccolti, dataInizio, dataFine};
        } else if (strategy instanceof TopPiantagioniStrategy) {
            // Per "Piantagione Migliore" usa 1, per "Top Piantagioni" usa il valore specificato
            return new Object[]{raccolti, topN != null ? topN : 1};
        } else if (strategy instanceof ReportStatisticheZonaStrategy) {
            return new Object[]{raccolti, piantagioni, zone};
        } else if (strategy instanceof ReportRaccoltiStrategy) {
            return new Object[]{raccolti, piantagioni};
        } else {
            return new Object[]{raccolti};
        }
    }

    public boolean isRaccoltoInPeriodo(LocalDate dataRaccolto, LocalDate inizio, LocalDate fine) {
        if (dataRaccolto == null || inizio == null || fine == null) return false;
        return !dataRaccolto.isBefore(inizio) && !dataRaccolto.isAfter(fine);
    }
}
