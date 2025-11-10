package BusinessLogic.Strategy;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import DomainModel.Piantagione;
import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class EfficienzaProduttivaStrategy implements DataProcessingStrategy<BigDecimal> {
    @Override
    public ProcessingResult<BigDecimal> execute(Object... data) throws ValidationException, BusinessLogicException {
        validateParameters(data);

        List<Raccolto> raccolti = castToRaccoltiList(data[0]);
        List<Piantagione> piantagioni = castToPiantagioniList(data[1]);
        int piantagioneId = (Integer) data[2];

        // Trova la piantagione
        Piantagione piantagione = piantagioni.stream()
            .filter(p -> p.getId() == piantagioneId)
            .findFirst()
            .orElseThrow(() -> new BusinessLogicException("Piantagione non trovata", "ID piantagione: " + piantagioneId));

        if (piantagione.getMessaADimora() == null) {
            throw new BusinessLogicException("Data di messa a dimora mancante", "Piantagione ID: " + piantagioneId);
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
            throw new BusinessLogicException("Piantagione troppo recente", "La piantagione è stata messa a dimora oggi o in futuro");
        }

        // Calcola l'efficienza (kg per pianta per giorno)
        BigDecimal numeroPiante = piantagione.getQuantitaPianta() != null ?
            new BigDecimal(piantagione.getQuantitaPianta()) : BigDecimal.ONE;
        BigDecimal mediaPerPianta = totale.divide(numeroPiante, 4, RoundingMode.HALF_UP);
        BigDecimal efficienzaGiornaliera = mediaPerPianta.divide(new BigDecimal(giorniTotali), 4, RoundingMode.HALF_UP);

        // Restituisce solo il dato numerico - nessuna formattazione
        return new ProcessingResult<>(efficienzaGiornaliera);
    }

    @Override
    public void validateParameters(Object... data) throws ValidationException {
        if (data == null) throw new ValidationException("I parametri non possono essere null");
        if (data.length < 3) throw new ValidationException("Necessari: lista raccolti, lista piantagioni e ID piantagione");
        if (!(data[0] instanceof List)) throw new ValidationException("Primo parametro deve essere List<Raccolto>");
        if (!(data[1] instanceof List)) throw new ValidationException("Secondo parametro deve essere List<Piantagione>");
        if (!(data[2] instanceof Integer)) throw new ValidationException("Terzo parametro deve essere Integer (ID piantagione)");
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
