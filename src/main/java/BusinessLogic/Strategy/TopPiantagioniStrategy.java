package BusinessLogic.Strategy;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class TopPiantagioniStrategy implements DataProcessingStrategy<Map<Integer, BigDecimal>> {
    @Override
    public ProcessingResult<Map<Integer, BigDecimal>> execute(Object... data) throws ValidationException, BusinessLogicException {
        validateParameters(data);

        List<Raccolto> raccolti = castToRaccoltiList(data[0]);
        int topN = data.length > 1 ? (Integer) data[1] : 1;

        // Raggruppa per piantagione e somma le quantità
        Map<Integer, BigDecimal> produzioniPerPiantagione = raccolti.stream()
            .filter(r -> r.getPiantagioneId() != null && r.getQuantitaKg() != null)
            .collect(Collectors.groupingBy(
                Raccolto::getPiantagioneId,
                Collectors.reducing(
                    BigDecimal.ZERO,
                    Raccolto::getQuantitaKg,
                    BigDecimal::add
                )
            ));

        // Verifica che ci siano piantagioni da analizzare
        if (produzioniPerPiantagione.isEmpty()) {
            throw new BusinessLogicException("Nessuna piantagione trovata",
                "Non ci sono raccolti validi per calcolare le top piantagioni");
        }

        // Ordina per produzione e prendi i top N
        Map<Integer, BigDecimal> topPiantagioni = produzioniPerPiantagione.entrySet().stream()
            .sorted(Map.Entry.<Integer, BigDecimal>comparingByValue().reversed())
            .limit(topN)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));

        // Restituisce solo i dati - nessuna formattazione
        return new ProcessingResult<>(topPiantagioni);
    }

    @Override
    public void validateParameters(Object... data) throws ValidationException {
        if (data == null) throw new ValidationException("I parametri non possono essere null");
        if (data.length < 1) throw new ValidationException("Necessaria almeno la lista raccolti");
        if (!(data[0] instanceof List)) throw new ValidationException("Primo parametro deve essere List<Raccolto>");

        if (data.length > 1) {
            if (!(data[1] instanceof Integer)) throw new ValidationException("Secondo parametro deve essere Integer (numero top piantagioni)");
            int topN = (Integer) data[1];
            if (topN <= 0) throw new ValidationException("Il numero di piantagioni deve essere positivo");
            if (topN > 100) throw new ValidationException("Il numero di piantagioni non può essere superiore a 100");
        }
    }

    @SuppressWarnings("unchecked")
    private List<Raccolto> castToRaccoltiList(Object obj) {
        return (List<Raccolto>) obj;
    }

    @Override
    public ProcessingType getType() {
        return ProcessingType.STATISTICS;
    }
}
