package BusinessLogic.Strategy;

import BusinessLogic.Exception.ValidationException;
import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class StatisticheMensiliRaccoltiStrategy implements DataProcessingStrategy<Map<String, Object>> {

    @Override
    public ProcessingResult<Map<String, Object>> execute(Object... data) throws ValidationException {
        validateParameters(data);

        List<Raccolto> raccolti = castToRaccoltiList(data[0]);

        // Raggruppa i raccolti per mese
        Map<String, List<Raccolto>> raggruppatiPerMese = raccolti.stream()
            .filter(r -> r.getDataRaccolto() != null)
            .collect(Collectors.groupingBy(r ->
                r.getDataRaccolto().format(DateTimeFormatter.ofPattern("yyyy-MM"))));

        // Calcola statistiche per ogni mese
        Map<String, Object> raccoltiPerMese = new LinkedHashMap<>();

        raggruppatiPerMese.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
            .forEach(entry -> {
                String mese = entry.getKey();
                List<Raccolto> raccoltiMese = entry.getValue();

                Map<String, Object> statsMese = new LinkedHashMap<>();

                // Numero raccolti del mese
                statsMese.put("numeroRaccolti", raccoltiMese.size());

                // Totale produzione del mese
                BigDecimal totaleMese = raccoltiMese.stream()
                    .map(Raccolto::getQuantitaKg)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                statsMese.put("totaleProduzione", totaleMese);

                // Piantagioni coinvolte
                Set<Integer> piantagioniCoinvolte = raccoltiMese.stream()
                    .map(Raccolto::getPiantagioneId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
                statsMese.put("piantagioniCoinvolte", piantagioniCoinvolte);
                statsMese.put("numeroPiantagioniCoinvolte", piantagioniCoinvolte.size());

                raccoltiPerMese.put(mese, statsMese);
            });

        return new ProcessingResult<>(raccoltiPerMese);
    }

    @Override
    public void validateParameters(Object... data) throws ValidationException {
        if (data == null) {
            throw new ValidationException("I parametri non possono essere null");
        }
        if (data.length < 1) {
            throw new ValidationException("Necessaria lista raccolti");
        }
        if (!(data[0] instanceof List)) {
            throw new ValidationException("Primo parametro deve essere List<Raccolto>");
        }
    }

    @Override
    public ProcessingType getType() {
        return ProcessingType.STATISTICS;
    }

    @SuppressWarnings("unchecked")
    private List<Raccolto> castToRaccoltiList(Object obj) {
        return (List<Raccolto>) obj;
    }
}
