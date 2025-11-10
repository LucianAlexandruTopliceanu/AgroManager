package BusinessLogic.Strategy;

import BusinessLogic.Exception.ValidationException;
import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StatisticheGeneraliRaccoltiStrategy implements DataProcessingStrategy<Map<String, Object>> {

    @Override
    public ProcessingResult<Map<String, Object>> execute(Object... data) throws ValidationException {
        validateParameters(data);

        List<Raccolto> raccolti = castToRaccoltiList(data[0]);

        Map<String, Object> statistiche = new LinkedHashMap<>();

        // Numero totale raccolti
        statistiche.put("numeroTotaleRaccolti", raccolti.size());

        // Produzione totale
        BigDecimal produzioneTotale = raccolti.stream()
            .map(Raccolto::getQuantitaKg)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistiche.put("produzioneTotale", produzioneTotale);

        return new ProcessingResult<>(statistiche);
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
        return ProcessingType.STATISTICS;
    }

    @SuppressWarnings("unchecked")
    private List<Raccolto> castToRaccoltiList(Object obj) {
        return (List<Raccolto>) obj;
    }
}
