package BusinessLogic.Strategy;

import DomainModel.Piantagione;
import DomainModel.Raccolto;
import DomainModel.Zona;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Strategia per generare statistiche per zona
 */
public class ReportStatisticheZonaStrategy implements DataProcessingStrategy<Map<String, BigDecimal>> {

    private List<Raccolto> raccolti;
    private List<Piantagione> piantagioni;
    private List<Zona> zone;

    @Override
    public Map<String, BigDecimal> execute() {
        Map<String, BigDecimal> statistiche = new HashMap<>();

        if (raccolti == null || piantagioni == null || zone == null) {
            return statistiche;
        }

        for (Raccolto raccolto : raccolti) {
            Piantagione piantagione = trovaPiantagionePerId(piantagioni, raccolto.getPiantagioneId());
            if (piantagione != null) {
                Zona zona = trovaZonaPerId(zone, piantagione.getZonaId());
                if (zona != null) {
                    statistiche.merge(zona.getNome(), raccolto.getQuantitaKg(), BigDecimal::add);
                }
            }
        }

        return statistiche;
    }

    @Override
    public String getProcessingName() {
        return "Statistiche per Zona";
    }

    @Override
    public ProcessingType getProcessingType() {
        return ProcessingType.STATISTICS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setData(Object... data) {
        if (data.length >= 3) {
            this.raccolti = (List<Raccolto>) data[0];
            this.piantagioni = (List<Piantagione>) data[1];
            this.zone = (List<Zona>) data[2];
        }
    }

    private Piantagione trovaPiantagionePerId(List<Piantagione> piantagioni, Integer id) {
        return piantagioni.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private Zona trovaZonaPerId(List<Zona> zone, Integer id) {
        return zone.stream()
                .filter(z -> z.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
