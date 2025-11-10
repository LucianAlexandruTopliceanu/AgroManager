package BusinessLogic.Strategy;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import DomainModel.Raccolto;
import java.util.*;

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

        Map<String, Object> reportData = new LinkedHashMap<>();

        try {
            ProcessingResult<Map<String, Object>> statisticheResult =
                statisticheGeneraliStrategy.execute(raccolti);
            reportData.put("statisticheGenerali", statisticheResult.data());

            ProcessingResult<Map<String, Object>> mensiliResult =
                statisticheMensiliStrategy.execute(raccolti);
            reportData.put("raccoltiPerMese", mensiliResult.data());

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
