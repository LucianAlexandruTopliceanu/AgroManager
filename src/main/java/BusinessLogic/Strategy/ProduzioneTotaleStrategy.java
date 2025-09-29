package BusinessLogic.Strategy;

import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.util.List;

/**
 * Strategia per calcolare la produzione totale di una piantagione
 */
public class ProduzioneTotaleStrategy implements DataProcessingStrategy<BigDecimal> {

    private List<Raccolto> raccolti;
    private int piantagioneId;

    @Override
    public BigDecimal execute() {
        if (raccolti == null) {
            return BigDecimal.ZERO;
        }

        return raccolti.stream()
            .filter(r -> r.getPiantagioneId() != null && r.getPiantagioneId().equals(piantagioneId))
            .map(Raccolto::getQuantitaKg)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String getProcessingName() {
        return "Produzione Totale Piantagione";
    }

    @Override
    public ProcessingType getProcessingType() {
        return ProcessingType.CALCULATION;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setData(Object... data) {
        if (data.length >= 2) {
            this.raccolti = (List<Raccolto>) data[0];
            this.piantagioneId = (Integer) data[1];
        }
    }
}
