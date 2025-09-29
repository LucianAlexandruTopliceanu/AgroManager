package BusinessLogic.Strategy;

import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.util.List;

/**
 * Strategia per generare un report testuale dei raccolti
 */
public class ReportRaccoltiStrategy implements DataProcessingStrategy<String> {

    private List<Raccolto> raccolti;

    @Override
    public String execute() {
        if (raccolti == null || raccolti.isEmpty()) {
            return "Nessun raccolto registrato.";
        }

        StringBuilder report = new StringBuilder();
        report.append("=== REPORT RACCOLTI ===\n");
        report.append("Totale raccolti: ").append(raccolti.size()).append("\n\n");

        BigDecimal quantitaTotale = raccolti.stream()
            .map(Raccolto::getQuantitaKg)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        report.append("Quantità totale raccolta: ").append(quantitaTotale).append(" kg\n\n");

        for (Raccolto raccolto : raccolti) {
            report.append("ID: ").append(raccolto.getId()).append("\n");
            report.append("Data: ").append(raccolto.getDataRaccolto()).append("\n");
            report.append("Quantità: ").append(raccolto.getQuantitaKg()).append(" kg\n");
            report.append("Piantagione: ").append(raccolto.getPiantagioneId()).append("\n");
            if (raccolto.getNote() != null && !raccolto.getNote().trim().isEmpty()) {
                report.append("Note: ").append(raccolto.getNote()).append("\n");
            }
            report.append("---\n");
        }

        return report.toString();
    }

    @Override
    public String getProcessingName() {
        return "Report Raccolti";
    }

    @Override
    public ProcessingType getProcessingType() {
        return ProcessingType.REPORT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setData(Object... data) {
        if (data.length >= 1) {
            this.raccolti = (List<Raccolto>) data[0];
        }
    }
}
