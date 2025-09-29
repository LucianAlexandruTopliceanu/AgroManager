package BusinessLogic.Strategy;

import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Strategia per trovare la piantagione più produttiva
 */
public class PiantagionePiuProduttivaStrategy implements DataProcessingStrategy<Integer> {

    private List<Raccolto> raccolti;

    @Override
    public Integer execute() {
        if (raccolti == null || raccolti.isEmpty()) {
            return 0;
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
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(0);
    }

    @Override
    public String getProcessingName() {
        return "Piantagione Più Produttiva";
    }

    @Override
    public ProcessingType getProcessingType() {
        return ProcessingType.STATISTICS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setData(Object... data) {
        if (data.length >= 1) {
            this.raccolti = (List<Raccolto>) data[0];
        }
    }
}
