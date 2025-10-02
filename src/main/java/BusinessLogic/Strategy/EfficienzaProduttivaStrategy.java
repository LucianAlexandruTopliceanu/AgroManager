package BusinessLogic.Strategy;

import DomainModel.Piantagione;
import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class EfficienzaProduttivaStrategy implements DataProcessingStrategy<BigDecimal> {
    @Override
    public ProcessingResult<BigDecimal> execute(Object... data) {
        validateParameters(data);

        // Cast type-safe con validazione runtime
        List<Raccolto> raccolti = castToRaccoltiList(data[0]);
        List<Piantagione> piantagioni = castToPiantagioniList(data[1]);
        int piantagioneId = (Integer) data[2];

        // Trova la piantagione
        Piantagione piantagione = piantagioni.stream()
            .filter(p -> p.getId() == piantagioneId)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Piantagione non trovata: " + piantagioneId));

        if (piantagione.getMessaADimora() == null) {
            throw new IllegalArgumentException("Data di messa a dimora non disponibile per la piantagione " + piantagioneId);
        }

        // Calcola il totale dei raccolti
        BigDecimal totale = raccolti.stream()
            .filter(r -> r.getPiantagioneId() != null && r.getPiantagioneId() == piantagioneId)
            .map(Raccolto::getQuantitaKg)
            .filter(q -> q != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcola i giorni dalla messa a dimora
        long giorniTotali = ChronoUnit.DAYS.between(piantagione.getMessaADimora(), java.time.LocalDate.now());
        if (giorniTotali <= 0) {
            throw new IllegalArgumentException("La piantagione è stata messa a dimora oggi o in futuro");
        }

        // Calcola l'efficienza (kg per pianta per giorno)
        BigDecimal numeroPiante = piantagione.getQuantitaPianta() != null ?
            new BigDecimal(piantagione.getQuantitaPianta()) : BigDecimal.ONE;
        BigDecimal mediaPerPianta = totale.divide(numeroPiante, 4, RoundingMode.HALF_UP);
        BigDecimal efficienzaGiornaliera = mediaPerPianta.divide(new BigDecimal(giorniTotali), 4, RoundingMode.HALF_UP);

        String output = String.format("Efficienza produttiva piantagione %d:\n" +
            "Periodo: %s (%d giorni)\n" +
            "Produzione totale: %.2f kg\n" +
            "Numero piante: %s\n" +
            "Media per pianta: %.2f kg/pianta\n" +
            "Efficienza: %.4f kg/pianta/giorno",
            piantagioneId,
            piantagione.getMessaADimora(),
            giorniTotali,
            totale,
            numeroPiante,
            mediaPerPianta,
            efficienzaGiornaliera);

        return new ProcessingResult<>(efficienzaGiornaliera, output);
    }

    @Override
    public void validateParameters(Object... data) {
        if (data == null) throw new IllegalArgumentException("I parametri non possono essere null");
        if (data.length < 3) throw new IllegalArgumentException("Necessari: lista raccolti, lista piantagioni e ID piantagione");
        if (!(data[0] instanceof List)) throw new IllegalArgumentException("Primo parametro deve essere List<Raccolto>");
        if (!(data[1] instanceof List)) throw new IllegalArgumentException("Secondo parametro deve essere List<Piantagione>");
        if (!(data[2] instanceof Integer)) throw new IllegalArgumentException("Terzo parametro deve essere Integer");
    }

    @SuppressWarnings("unchecked")
    private List<Raccolto> castToRaccoltiList(Object obj) {
        return (List<Raccolto>) obj;
    }

    @SuppressWarnings("unchecked")
    private List<Piantagione> castToPiantagioniList(Object obj) {
        return (List<Piantagione>) obj;
    }

    @Override
    public ProcessingType getType() {
        return ProcessingType.CALCULATION;
    }
}
