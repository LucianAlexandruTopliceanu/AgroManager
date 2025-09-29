package BusinessLogic.Strategy;

import DomainModel.Piantagione;
import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Strategia per calcolare l'efficienza produttiva (kg per pianta per giorno)
 */
public class EfficienzaProduttivaStrategy implements DataProcessingStrategy<BigDecimal> {

    private List<Raccolto> raccolti;
    private List<Piantagione> piantagioni;
    private int piantagioneId;

    @Override
    public BigDecimal execute() {
        if (raccolti == null || piantagioni == null) {
            return BigDecimal.ZERO;
        }

        // Calcola produzione totale
        BigDecimal totaleProduzione = raccolti.stream()
            .filter(r -> r.getPiantagioneId() != null && r.getPiantagioneId().equals(piantagioneId))
            .map(Raccolto::getQuantitaKg)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Trova numero piante
        int numeroPiante = piantagioni.stream()
            .filter(p -> p.getId() != null && p.getId().equals(piantagioneId))
            .mapToInt(p -> p.getQuantitaPianta() != null ? p.getQuantitaPianta() : 1)
            .findFirst()
            .orElse(1);

        // Calcola giorni dalla messa a dimora
        LocalDate messaADimora = piantagioni.stream()
            .filter(p -> p.getId() != null && p.getId().equals(piantagioneId))
            .map(Piantagione::getMessaADimora)
            .findFirst()
            .orElse(LocalDate.now());

        LocalDate primoRaccolto = raccolti.stream()
            .filter(r -> r.getPiantagioneId() != null && r.getPiantagioneId().equals(piantagioneId))
            .map(Raccolto::getDataRaccolto)
            .min(LocalDate::compareTo)
            .orElse(LocalDate.now());

        long giorni = ChronoUnit.DAYS.between(messaADimora, primoRaccolto);

        if (numeroPiante == 0 || giorni == 0) {
            return BigDecimal.ZERO;
        }

        return totaleProduzione.divide(
            new BigDecimal(numeroPiante).multiply(new BigDecimal(giorni)),
            6,
            RoundingMode.HALF_UP
        );
    }

    @Override
    public String getProcessingName() {
        return "Efficienza Produttiva (kg/pianta/giorno)";
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
