package BusinessLogic.Strategy;

import DomainModel.Piantagione;
import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Strategia per calcolare la media di produzione per pianta
 */
public class MediaProduzioneStrategy implements DataProcessingStrategy<BigDecimal> {

    private List<Raccolto> raccolti;
    private List<Piantagione> piantagioni;
    private int piantagioneId;

    @Override
    public BigDecimal execute() {
        if (raccolti == null || piantagioni == null) {
            return BigDecimal.ZERO;
        }

        // Calcola produzione totale
        BigDecimal totale = raccolti.stream()
            .filter(r -> r.getPiantagioneId() != null && r.getPiantagioneId().equals(piantagioneId))
            .map(Raccolto::getQuantitaKg)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Trova numero piante
        int numeroPiante = piantagioni.stream()
            .filter(p -> p.getId() != null && p.getId().equals(piantagioneId))
            .mapToInt(p -> p.getQuantitaPianta() != null ? p.getQuantitaPianta() : 1)
            .findFirst()
            .orElse(1);

        if (numeroPiante == 0) {
            return BigDecimal.ZERO;
        }

        return totale.divide(new BigDecimal(numeroPiante), 3, RoundingMode.HALF_UP);
    }

    @Override
    public String getProcessingName() {
        return "Media Produzione per Pianta";
    }

    @Override
    public ProcessingType getProcessingType() {
        return ProcessingType.CALCULATION;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setData(Object... data) {
        if (data.length >= 3) {
            this.raccolti = (List<Raccolto>) data[0];
            this.piantagioni = (List<Piantagione>) data[1];
            this.piantagioneId = (Integer) data[2];
        }
    }
}
