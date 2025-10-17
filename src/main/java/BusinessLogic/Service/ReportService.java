package BusinessLogic.Service;

import BusinessLogic.Exception.BusinessLogicException;
import BusinessLogic.Exception.DataAccessException;
import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Strategy.*;
import DomainModel.Raccolto;

import java.util.List;
import java.util.Map;

/**
 * Service dedicato alla generazione di report sui raccolti.
 * Incapsula la logica di business per i report e coordina le strategy specifiche.
 * Fa parte del layer BusinessLogic e non deve gestire aspetti di presentazione.
 */
public class ReportService {

    private final RaccoltoService raccoltoService;

    public ReportService(RaccoltoService raccoltoService) {
        this.raccoltoService = raccoltoService;
    }

    /**
     * Genera un report completo con tutte le statistiche sui raccolti
     */
    public ProcessingResult<Map<String, Object>> generaReportCompleto()
            throws DataAccessException, BusinessLogicException, ValidationException {

        List<Raccolto> raccolti = raccoltoService.getAllRaccolti();

        if (raccolti.isEmpty()) {
            throw new BusinessLogicException("Nessun raccolto disponibile per generare il report",
                "Database vuoto o nessun raccolto inserito");
        }

        ReportRaccoltiStrategy strategy = new ReportRaccoltiStrategy();
        return strategy.execute(raccolti);
    }

    /**
     * Genera solo le statistiche generali dei raccolti
     */
    public ProcessingResult<Map<String, Object>> generaStatisticheGenerali()
            throws DataAccessException, BusinessLogicException, ValidationException {

        List<Raccolto> raccolti = raccoltoService.getAllRaccolti();

        if (raccolti.isEmpty()) {
            throw new BusinessLogicException("Nessun raccolto disponibile",
                "Non ci sono raccolti nel database");
        }

        StatisticheGeneraliRaccoltiStrategy strategy = new StatisticheGeneraliRaccoltiStrategy();
        return strategy.execute(raccolti);
    }

    /**
     * Genera le statistiche mensili dei raccolti
     */
    public ProcessingResult<Map<String, Object>> generaStatisticheMensili()
            throws DataAccessException, BusinessLogicException, ValidationException {

        List<Raccolto> raccolti = raccoltoService.getAllRaccolti();

        if (raccolti.isEmpty()) {
            throw new BusinessLogicException("Nessun raccolto disponibile",
                "Non ci sono raccolti nel database");
        }

        StatisticheMensiliRaccoltiStrategy strategy = new StatisticheMensiliRaccoltiStrategy();
        return strategy.execute(raccolti);
    }

    /**
     * Calcola il periodo coperto dai raccolti (prima e ultima data)
     */
    public ProcessingResult<Map<String, Object>> calcolaPeriodoCoperto()
            throws DataAccessException, BusinessLogicException, ValidationException {

        List<Raccolto> raccolti = raccoltoService.getAllRaccolti();

        if (raccolti.isEmpty()) {
            throw new BusinessLogicException("Nessun raccolto disponibile",
                "Non ci sono raccolti nel database");
        }

        PeriodoCopertoRaccoltiStrategy strategy = new PeriodoCopertoRaccoltiStrategy();
        return strategy.execute(raccolti);
    }

    /**
     * Verifica se ci sono dati disponibili per generare report
     */
    public boolean hasRaccoltiDisponibili() throws DataAccessException {
        try {
            List<Raccolto> raccolti = raccoltoService.getAllRaccolti();
            return !raccolti.isEmpty();
        } catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw DataAccessException.queryError("verifica raccolti disponibili", e);
        }
    }
}
