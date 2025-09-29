package BusinessLogic.Strategy;

import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Strategia per trovare le top N piantagioni più produttive
 */
public class TopPiantagioniStrategy implements DataProcessingStrategy<Map<Integer, BigDecimal>> {

    private List<Raccolto> raccolti;
    private int topN;

    @Override
    public Map<Integer, BigDecimal> execute() {
        if (raccolti == null || raccolti.isEmpty()) {
            return new LinkedHashMap<>();
        }

        return raccolti.stream()
            .filter(r -> r.getPiantagioneId() != null)
            .collect(Collectors.groupingBy(
                Raccolto::getPiantagioneId,
                Collectors.reducing(
                    BigDecimal.ZERO,
                    Raccolto::getQuantitaKg,
                    BigDecimal::add)))
            .entrySet()
            .stream()
            .sorted(Map.Entry.<Integer, BigDecimal>comparingByValue().reversed())
            .limit(topN)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new));
    }

    @Override
    public String getProcessingName() {
        return "Top " + topN + " Piantagioni Produttive";
    }

    @Override
    public ProcessingType getProcessingType() {
        return ProcessingType.STATISTICS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setData(Object... data) {
        if (data.length >= 2) {
            this.raccolti = (List<Raccolto>) data[0];
            this.topN = (Integer) data[1];
        }
    }
}
