package BusinessLogic.Strategy;

import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.time.temporal.ChronoUnit;


public class ProduzionePerPeriodoStrategy implements DataProcessingStrategy<BigDecimal> {

    @Override
    public ProcessingResult<BigDecimal> execute(Object... data) {
        validateParameters(data);


        List<Raccolto> raccolti = castToRaccoltiList(data[0]);
        LocalDate inizio = (LocalDate) data[1];
        LocalDate fine = (LocalDate) data[2];

        BigDecimal produzione = raccolti.stream()
            .filter(r -> isRaccoltoInPeriodo(r.getDataRaccolto(), inizio, fine))
            .map(Raccolto::getQuantitaKg)
            .filter(q -> q != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        long giorniPeriodo = ChronoUnit.DAYS.between(inizio, fine) + 1;
        BigDecimal mediaGiornaliera = giorniPeriodo > 0 ?
            produzione.divide(new BigDecimal(giorniPeriodo), 2, java.math.RoundingMode.HALF_UP) :
            BigDecimal.ZERO;

        String output = String.format("""
            📅 Produzione nel periodo:
            ▸ Dal: %s
            ▸ Al: %s
            ▸ Durata: %d giorni
            ▸ Produzione totale: %.2f kg
            ▸ Media giornaliera: %.2f kg/giorno""",
            inizio.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            fine.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            giorniPeriodo,
            produzione,
            mediaGiornaliera);

        return new ProcessingResult<>(produzione, output);
    }

    @SuppressWarnings("unchecked")
    private List<Raccolto> castToRaccoltiList(Object obj) {
        return (List<Raccolto>) obj;
    }

    private boolean isRaccoltoInPeriodo(LocalDate dataRaccolto, LocalDate inizio, LocalDate fine) {
        if (dataRaccolto == null) return false;
        return !dataRaccolto.isBefore(inizio) && !dataRaccolto.isAfter(fine);
    }

    @Override
    public void validateParameters(Object... data) {
        if (data == null) throw new IllegalArgumentException("I parametri non possono essere null");
        if (data.length < 3) throw new IllegalArgumentException("Necessari: lista raccolti, data inizio e data fine");
        if (!(data[0] instanceof List)) throw new IllegalArgumentException("Primo parametro deve essere List<Raccolto>");
        if (!(data[1] instanceof LocalDate)) throw new IllegalArgumentException("Secondo parametro deve essere LocalDate");
        if (!(data[2] instanceof LocalDate)) throw new IllegalArgumentException("Terzo parametro deve essere LocalDate");

        LocalDate inizio = (LocalDate) data[1];
        LocalDate fine = (LocalDate) data[2];
        if (fine.isBefore(inizio)) throw new IllegalArgumentException("La data di fine non può essere precedente alla data di inizio");
    }

    @Override
    public ProcessingType getType() {
        return ProcessingType.CALCULATION;
    }
}
