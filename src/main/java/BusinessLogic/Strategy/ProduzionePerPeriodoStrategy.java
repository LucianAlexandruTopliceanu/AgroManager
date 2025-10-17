package BusinessLogic.Strategy;

import BusinessLogic.Exception.BusinessLogicException;
import BusinessLogic.Exception.ValidationException;
import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public class ProduzionePerPeriodoStrategy implements DataProcessingStrategy<BigDecimal> {

    @Override
    public ProcessingResult<BigDecimal> execute(Object... data) throws ValidationException {
        validateParameters(data);

        List<Raccolto> raccolti = castToRaccoltiList(data[0]);
        LocalDate inizio = (LocalDate) data[1];
        LocalDate fine = (LocalDate) data[2];

        BigDecimal produzione = raccolti.stream()
            .filter(r -> isRaccoltoInPeriodo(r.getDataRaccolto(), inizio, fine))
            .map(Raccolto::getQuantitaKg)
            .filter(q -> q != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Restituisce solo il dato numerico - nessuna formattazione
        return new ProcessingResult<>(produzione);
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
    public void validateParameters(Object... data) throws ValidationException {
        if (data == null) throw new ValidationException("I parametri non possono essere null");
        if (data.length < 3) throw new ValidationException("Necessari: lista raccolti, data inizio e data fine");
        if (!(data[0] instanceof List)) throw new ValidationException("Primo parametro deve essere List<Raccolto>");
        if (!(data[1] instanceof LocalDate inizio)) throw new ValidationException("Secondo parametro deve essere LocalDate (data inizio)");
        if (!(data[2] instanceof LocalDate fine)) throw new ValidationException("Terzo parametro deve essere LocalDate (data fine)");

        if (fine.isBefore(inizio)) throw new ValidationException("La data di fine non può essere precedente alla data di inizio");
    }

    @Override
    public ProcessingType getType() {
        return ProcessingType.CALCULATION;
    }
}
