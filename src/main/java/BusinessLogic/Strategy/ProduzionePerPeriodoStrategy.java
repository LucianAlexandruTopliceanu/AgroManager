package BusinessLogic.Strategy;

import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Strategia per calcolare la produzione in un periodo specifico
 */
public class ProduzionePerPeriodoStrategy implements DataProcessingStrategy<BigDecimal> {

    private List<Raccolto> raccolti;
    private LocalDate inizio;
    private LocalDate fine;

    @Override
    public BigDecimal execute() {
        if (raccolti == null || inizio == null || fine == null) {
            return BigDecimal.ZERO;
        }

        return raccolti.stream()
            .filter(r -> isRaccoltoInPeriodo(r.getDataRaccolto(), inizio, fine))
            .map(Raccolto::getQuantitaKg)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isRaccoltoInPeriodo(LocalDate dataRaccolto, LocalDate inizio, LocalDate fine) {
        if (dataRaccolto == null) {
            return false;
        }
        return !dataRaccolto.isBefore(inizio) && !dataRaccolto.isAfter(fine);
    }

    @Override
    public String getProcessingName() {
        return "Produzione per Periodo";
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
            this.inizio = (LocalDate) data[1];
            this.fine = (LocalDate) data[2];
        }
    }
}
