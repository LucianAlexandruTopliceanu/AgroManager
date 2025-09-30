package BusinessLogic.Strategy;

import DomainModel.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ReportStatisticheZonaStrategy implements DataProcessingStrategy<Map<String, Map<String, BigDecimal>>> {
    @Override
    public ProcessingResult<Map<String, Map<String, BigDecimal>>> execute(Object... data) {
        validateParameters(data);

        List<Raccolto> raccolti = (List<Raccolto>) data[0];
        List<Piantagione> piantagioni = (List<Piantagione>) data[1];
        List<Zona> zone = (List<Zona>) data[2];

        // Mappa per tenere traccia delle statistiche per zona
        Map<String, Map<String, BigDecimal>> statistichePerZona = new LinkedHashMap<>();

        // Calcola le statistiche per ogni zona
        for (Zona zona : zone) {
            // Trova tutte le piantagioni in questa zona
            Set<Integer> piantagioniInZona = piantagioni.stream()
                .filter(p -> p.getZonaId() != null && p.getZonaId().equals(zona.getId()))
                .map(Piantagione::getId)
                .collect(Collectors.toSet());

            // Calcola le statistiche per questa zona
            BigDecimal produzioneTotale = raccolti.stream()
                .filter(r -> r.getPiantagioneId() != null &&
                           piantagioniInZona.contains(r.getPiantagioneId()))
                .map(Raccolto::getQuantitaKg)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal produzioneMQ = zona.getDimensione() != null && zona.getDimensione() > 0 ?
                produzioneTotale.divide(new BigDecimal(zona.getDimensione() * 10000), 4, java.math.RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

            // Salva le statistiche
            Map<String, BigDecimal> stats = new LinkedHashMap<>();
            stats.put("produzioneTotale", produzioneTotale);
            stats.put("produzioneMQ", produzioneMQ);
            stats.put("numeroPiantagioni", new BigDecimal(piantagioniInZona.size()));

            statistichePerZona.put(zona.getNome(), stats);
        }

        // Genera il report formattato
        StringBuilder report = new StringBuilder();
        report.append("🗺️ REPORT STATISTICHE PER ZONA\n");
        report.append("════════════════════════════\n\n");

        statistichePerZona.forEach((nomeZona, stats) -> {
            report.append(String.format("📍 ZONA: %s\n", nomeZona));
            report.append(String.format("▸ Produzione totale: %.2f kg\n", stats.get("produzioneTotale")));
            report.append(String.format("▸ Produzione per m²: %.4f kg/m²\n", stats.get("produzioneMQ")));
            report.append(String.format("▸ Numero piantagioni: %d\n\n", stats.get("numeroPiantagioni").intValue()));
        });

        return new ProcessingResult<>(statistichePerZona, report.toString());
    }

    @Override
    public void validateParameters(Object... data) {
        if (data == null) throw new IllegalArgumentException("I parametri non possono essere null");
        if (data.length < 3) throw new IllegalArgumentException("Necessari: lista raccolti, piantagioni e zone");
        if (!(data[0] instanceof List)) throw new IllegalArgumentException("Primo parametro deve essere List<Raccolto>");
        if (!(data[1] instanceof List)) throw new IllegalArgumentException("Secondo parametro deve essere List<Piantagione>");
        if (!(data[2] instanceof List)) throw new IllegalArgumentException("Terzo parametro deve essere List<Zona>");
    }

    @Override
    public ProcessingType getType() {
        return ProcessingType.STATISTICS;
    }
}
