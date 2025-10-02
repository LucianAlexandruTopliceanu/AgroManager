package BusinessLogic.Strategy;

import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class TopPiantagioniStrategy implements DataProcessingStrategy<Map<Integer, BigDecimal>> {
    @Override
    public ProcessingResult<Map<Integer, BigDecimal>> execute(Object... data) {
        validateParameters(data);

        List<Raccolto> raccolti = castToRaccoltiList(data[0]);
        int topN = data.length > 1 ? (Integer) data[1] : 1; // Se non specificato, prende solo la migliore

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

        // Formatta l'output in base al numero di risultati richiesti
        String output;
        if (topN == 1) {
            Map.Entry<Integer, BigDecimal> best = topPiantagioni.entrySet().iterator().next();
            output = String.format("🏆 Piantagione più produttiva:\n" +
                                 "ID: %d\n" +
                                 "Produzione totale: %.2f kg",
                                 best.getKey(), best.getValue());
        } else {
            StringBuilder sb = new StringBuilder(String.format("Top %d piantagioni per produzione:\n", topN));
            int pos = 1;
            for (Map.Entry<Integer, BigDecimal> entry : topPiantagioni.entrySet()) {
                String medal = pos == 1 ? "🥇" : pos == 2 ? "🥈" : pos == 3 ? "🥉" : "▫️";
                sb.append(String.format("%s #%d: Piantagione %d - %.2f kg\n",
                    medal, pos++, entry.getKey(), entry.getValue()));
            }
            output = sb.toString();
        }

        return new ProcessingResult<>(topPiantagioni, output);
    }

    @Override
    public void validateParameters(Object... data) {
        if (data == null) throw new IllegalArgumentException("I parametri non possono essere null");
        if (data.length < 1) throw new IllegalArgumentException("Necessaria almeno la lista raccolti");
        if (!(data[0] instanceof List)) throw new IllegalArgumentException("Primo parametro deve essere List<Raccolto>");
        if (data.length > 1) {
            if (!(data[1] instanceof Integer)) throw new IllegalArgumentException("Secondo parametro deve essere Integer");
            int topN = (Integer) data[1];
            if (topN <= 0) throw new IllegalArgumentException("Il numero di piantagioni deve essere positivo");
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
