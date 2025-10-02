package BusinessLogic.Strategy;

import DomainModel.Piantagione;
import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class MediaProduzioneStrategy implements DataProcessingStrategy<BigDecimal> {
    @Override
    public ProcessingResult<BigDecimal> execute(Object... data) {
        validateParameters(data);


        List<Raccolto> raccolti = castToRaccoltiList(data[0]);
        List<Piantagione> piantagioni = castToPiantagioniList(data[1]);
        int piantagioneId = (Integer) data[2];

        // Trova la piantagione
        Piantagione piantagione = piantagioni.stream()
            .filter(p -> p.getId() == piantagioneId)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Piantagione non trovata: " + piantagioneId));

        // Calcola il totale dei raccolti
        BigDecimal totale = raccolti.stream()
            .filter(r -> r.getPiantagioneId() != null && r.getPiantagioneId() == piantagioneId)
            .map(Raccolto::getQuantitaKg)
            .filter(q -> q != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcola la media per pianta
        BigDecimal numeroPiante = piantagione.getQuantitaPianta() != null ?
            new BigDecimal(piantagione.getQuantitaPianta()) : BigDecimal.ONE;
        BigDecimal media = totale.divide(numeroPiante, 2, RoundingMode.HALF_UP);

        String output = String.format("Media produzione per piantagione %d:\n" +
            "Totale produzione: %.2f kg\n" +
            "Numero piante: %s\n" +
            "Media per pianta: %.2f kg/pianta",
            piantagioneId, totale, numeroPiante, media);

        return new ProcessingResult<>(media, output);
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
