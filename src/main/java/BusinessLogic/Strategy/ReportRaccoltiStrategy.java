package BusinessLogic.Strategy;

import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportRaccoltiStrategy implements DataProcessingStrategy<String> {
    @Override
    public ProcessingResult<String> execute(Object... data) {
        validateParameters(data);


        List<Raccolto> raccolti = castToRaccoltiList(data[0]);

        // Raggruppa i raccolti per mese
        Map<String, List<Raccolto>> raccoltiPerMese = raccolti.stream()
            .filter(r -> r.getDataRaccolto() != null)
            .collect(Collectors.groupingBy(r ->
                r.getDataRaccolto().format(DateTimeFormatter.ofPattern("yyyy-MM"))));

        // Genera il report
        StringBuilder report = new StringBuilder();
        report.append("📊 REPORT DETTAGLIATO RACCOLTI\n");
        report.append("═══════════════════════════════\n\n");

        // Statistiche generali
        report.append("STATISTICHE GENERALI:\n");
        report.append("▸ Numero totale raccolti: ").append(raccolti.size()).append("\n");
        BigDecimal totaleComplessivo = raccolti.stream()
            .map(Raccolto::getQuantitaKg)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        report.append("▸ Produzione totale: ").append(String.format("%.2f kg\n", totaleComplessivo));
        report.append("▸ Periodo coperto: ").append(getPeriodoCoperto(raccolti)).append("\n\n");

        // Dettagli per mese
        report.append("DETTAGLIO MENSILE:\n");
        raccoltiPerMese.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
            .forEach(entry -> {
                String mese = entry.getKey();
                List<Raccolto> raccoltiMese = entry.getValue();
                BigDecimal totaleMese = raccoltiMese.stream()
                    .map(Raccolto::getQuantitaKg)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                report.append(String.format("📅 %s:\n", mese));
                report.append(String.format("  ▸ Numero raccolti: %d\n", raccoltiMese.size()));
                report.append(String.format("  ▸ Totale produzione: %.2f kg\n", totaleMese));
                report.append("  ▸ Piantagioni coinvolte: ").append(
                    raccoltiMese.stream()
                        .map(Raccolto::getPiantagioneId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .map(Object::toString)
                        .collect(Collectors.joining(", "))
                ).append("\n\n");
            });

        // Il report stesso è sia il risultato che l'output formattato
        return new ProcessingResult<>(report.toString(), report.toString());
    }

    private String getPeriodoCoperto(List<Raccolto> raccolti) {
        Optional<java.time.LocalDate> primaData = raccolti.stream()
            .map(Raccolto::getDataRaccolto)
            .filter(Objects::nonNull)
            .min(java.time.LocalDate::compareTo);

        Optional<java.time.LocalDate> ultimaData = raccolti.stream()
            .map(Raccolto::getDataRaccolto)
            .filter(Objects::nonNull)
            .max(java.time.LocalDate::compareTo);

        if (primaData.isPresent() && ultimaData.isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return String.format("dal %s al %s",
                primaData.get().format(formatter),
                ultimaData.get().format(formatter));
        }
        return "Nessun dato temporale disponibile";
    }

    @Override
    public void validateParameters(Object... data) {
        if (data == null) throw new IllegalArgumentException("I parametri non possono essere null");
        if (data.length < 1) throw new IllegalArgumentException("Necessaria lista raccolti");
        if (!(data[0] instanceof List)) throw new IllegalArgumentException("Primo parametro deve essere List<Raccolto>");
    }

    @Override
    public ProcessingType getType() {
        return ProcessingType.REPORT;
    }

    @SuppressWarnings("unchecked")
    private List<Raccolto> castToRaccoltiList(Object obj) {
        return (List<Raccolto>) obj;
    }
}
