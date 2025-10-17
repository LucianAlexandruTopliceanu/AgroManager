package BusinessLogic.Strategy;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import DomainModel.Raccolto;
import java.util.*;

/**
 * Composite Strategy che coordina multiple strategy specifiche per generare un report completo dei raccolti.
 * Rispetta il pattern Strategy delegando ogni calcolo specifico alla propria strategy dedicata.
 */
public class ReportRaccoltiStrategy implements DataProcessingStrategy<Map<String, Object>> {

    private final StatisticheGeneraliRaccoltiStrategy statisticheGeneraliStrategy;
    private final StatisticheMensiliRaccoltiStrategy statisticheMensiliStrategy;
    private final PeriodoCopertoRaccoltiStrategy periodoCopertoStrategy;

    public ReportRaccoltiStrategy() {
        this.statisticheGeneraliStrategy = new StatisticheGeneraliRaccoltiStrategy();
        this.statisticheMensiliStrategy = new StatisticheMensiliRaccoltiStrategy();
        this.periodoCopertoStrategy = new PeriodoCopertoRaccoltiStrategy();
    }

    @Override
    public ProcessingResult<Map<String, Object>> execute(Object... data) throws ValidationException, BusinessLogicException {
        validateParameters(data);

        List<Raccolto> raccolti = castToRaccoltiList(data[0]);

        // Prepara i dati strutturati del report coordinando le strategy specifiche
        Map<String, Object> reportData = new LinkedHashMap<>();

        try {
            // Delega il calcolo delle statistiche generali alla strategy specifica
            ProcessingResult<Map<String, Object>> statisticheResult =
                statisticheGeneraliStrategy.execute(raccolti);
            reportData.put("statisticheGenerali", statisticheResult.data());

            // Delega il calcolo delle statistiche mensili alla strategy specifica
            ProcessingResult<Map<String, Object>> mensiliResult =
                statisticheMensiliStrategy.execute(raccolti);
            reportData.put("raccoltiPerMese", mensiliResult.data());

            // Delega il calcolo del periodo coperto alla strategy specifica
            ProcessingResult<Map<String, Object>> periodoResult =
                periodoCopertoStrategy.execute(raccolti);
            reportData.put("periodoCoperto", periodoResult.data());

        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessLogicException("Errore durante la generazione del report raccolti", e.getMessage());
        }

        return new ProcessingResult<>(reportData);
    }

    @Override
    public void validateParameters(Object... data) throws ValidationException {
        if (data == null) {
            throw new ValidationException("I parametri non possono essere null");
        }
        if (data.length < 1) {
            throw new ValidationException("Necessaria lista raccolti");
        }
        if (!(data[0] instanceof List)) {
            throw new ValidationException("Primo parametro deve essere List<Raccolto>");
        }
    }

    @Override
    public ProcessingType getType() {
        return ProcessingType.REPORT;
    }

    @SuppressWarnings("unchecked")
    private List<Raccolto> castToRaccoltiList(Object obj) {
        return (List<Raccolto>) obj;
    }
}
