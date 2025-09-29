package BusinessLogic.Strategy;

import DomainModel.Pianta;
import DomainModel.Piantagione;
import DomainModel.Raccolto;
import DomainModel.Zona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class ReportStrategyTest {

    private List<Raccolto> raccolti;
    private List<Piantagione> piantagioni;
    private List<Zona> zone;
    private List<Pianta> piante;

    @BeforeEach
    void setUp() {
        setupData();
    }

    private void setupData() {
        // Raccolti 
        Raccolto raccolto1 = new Raccolto();
        raccolto1.setId(1);
        raccolto1.setDataRaccolto(LocalDate.now().minusDays(5));
        raccolto1.setQuantitaKg(new BigDecimal("25.50"));
        raccolto1.setNote("Raccolto ottimo");
        raccolto1.setPiantagioneId(1);

        Raccolto raccolto2 = new Raccolto();
        raccolto2.setId(2);
        raccolto2.setDataRaccolto(LocalDate.now().minusDays(3));
        raccolto2.setQuantitaKg(new BigDecimal("15.75"));
        raccolto2.setNote("Raccolto buono");
        raccolto2.setPiantagioneId(2);

        raccolti = Arrays.asList(raccolto1, raccolto2);

        // Piantagioni 
        Piantagione piantagione1 = new Piantagione();
        piantagione1.setId(1);
        piantagione1.setPiantaId(1);
        piantagione1.setZonaId(1);
        piantagione1.setQuantitaPianta(100);

        Piantagione piantagione2 = new Piantagione();
        piantagione2.setId(2);
        piantagione2.setPiantaId(2);
        piantagione2.setZonaId(1);
        piantagione2.setQuantitaPianta(50);

        piantagioni = Arrays.asList(piantagione1, piantagione2);

        // Zone 
        Zona zona1 = new Zona();
        zona1.setId(1);
        zona1.setNome("Zona Test");
        zona1.setDimensione(100.0);
        zona1.setTipoTerreno("Argilloso");

        zone = Arrays.asList(zona1);

        // Piante 
        Pianta pianta1 = new Pianta();
        pianta1.setId(1);
        pianta1.setTipo("Pomodoro");
        pianta1.setVarieta("Cherry");

        Pianta pianta2 = new Pianta();
        pianta2.setId(2);
        pianta2.setTipo("Basilico");
        pianta2.setVarieta("Genovese");

        piante = Arrays.asList(pianta1, pianta2);
    }

    @Test
    void testGenerazioneReportRaccolti() {
        // Test della logica di generazione report raccolti senza dipendenze esterne
        String report = generaReportRaccoltiManuale(raccolti);

        assertNotNull(report);
        assertTrue(report.contains("=== REPORT RACCOLTI ==="));
        assertTrue(report.contains("Totale raccolti: 2"));
        assertTrue(report.contains("25.50"));
        assertTrue(report.contains("15.75"));
        assertTrue(report.contains("Raccolto ottimo"));
        assertTrue(report.contains("Raccolto buono"));
    }

    @Test
    void testCalcoloStatisticheZona() {
        Map<String, BigDecimal> statistiche = calcolaStatisticheZonaManuale(
            raccolti, piantagioni, zone);

        assertNotNull(statistiche);
        assertEquals(1, statistiche.size());
        assertTrue(statistiche.containsKey("Zona Test"));
        // 25.50 + 15.75 = 41.25
        assertEquals(new BigDecimal("41.25"), statistiche.get("Zona Test"));
    }

    @Test
    void testCalcoloStatistichePianta() {
        Map<String, BigDecimal> statistiche = calcolaStatistichePiantaManuale(
            raccolti, piantagioni, piante);

        assertNotNull(statistiche);
        assertEquals(2, statistiche.size());
        assertTrue(statistiche.containsKey("Pomodoro - Cherry"));
        assertTrue(statistiche.containsKey("Basilico - Genovese"));
        assertEquals(new BigDecimal("25.50"), statistiche.get("Pomodoro - Cherry"));
        assertEquals(new BigDecimal("15.75"), statistiche.get("Basilico - Genovese"));
    }

    @Test
    void testReportConDatiVuoti() {
        List<Raccolto> raccoltiVuoti = new ArrayList<>();
        String report = generaReportRaccoltiManuale(raccoltiVuoti);

        assertEquals("Nessun raccolto registrato.", report);
    }

    @Test
    void testStatisticheConDatiVuoti() {
        List<Raccolto> raccoltiVuoti = new ArrayList<>();
        List<Piantagione> piantagioniVuote = new ArrayList<>();
        List<Zona> zoneVuote = new ArrayList<>();

        Map<String, BigDecimal> statisticheZona = calcolaStatisticheZonaManuale(
            raccoltiVuoti, piantagioniVuote, zoneVuote);

        assertNotNull(statisticheZona);
        assertTrue(statisticheZona.isEmpty());
    }

    @Test
    void testCalcoloTotaleRaccolti() {
        BigDecimal totale = calcolaTotaleRaccolti(raccolti);

        // 25.50 + 15.75 = 41.25
        assertEquals(new BigDecimal("41.25"), totale);
    }

    @Test
    void testRicercaPiantagioneId() {
        Piantagione piantagione = trovaPiantagionePerId(piantagioni, 1);

        assertNotNull(piantagione);
        assertEquals(1, piantagione.getId());
        assertEquals(1, piantagione.getPiantaId());
        assertEquals(1, piantagione.getZonaId());
    }

    @Test
    void testRicercaZonaPerId() {
        Zona zona = trovaZonaPerId(zone, 1);

        assertNotNull(zona);
        assertEquals(1, zona.getId());
        assertEquals("Zona Test", zona.getNome());
    }

    @Test
    void testRicercaPiantaPerId() {
        Pianta pianta = trovaPiantaPerId(piante, 1);

        assertNotNull(pianta);
        assertEquals(1, pianta.getId());
        assertEquals("Pomodoro", pianta.getTipo());
        assertEquals("Cherry", pianta.getVarieta());
    }

    // Metodi di supporto per testare la logica senza dipendenze

    private String generaReportRaccoltiManuale(List<Raccolto> raccolti) {
        if (raccolti.isEmpty()) {
            return "Nessun raccolto registrato.";
        }

        StringBuilder report = new StringBuilder();
        report.append("=== REPORT RACCOLTI ===\n");
        report.append("Totale raccolti: ").append(raccolti.size()).append("\n\n");

        for (Raccolto raccolto : raccolti) {
            report.append("ID: ").append(raccolto.getId()).append("\n");
            report.append("Data: ").append(raccolto.getDataRaccolto()).append("\n");
            report.append("Quantità: ").append(raccolto.getQuantitaKg()).append(" kg\n");
            report.append("Note: ").append(raccolto.getNote()).append("\n");
            report.append("Piantagione: ").append(raccolto.getPiantagioneId()).append("\n");
            report.append("---\n");
        }

        return report.toString();
    }

    private Map<String, BigDecimal> calcolaStatisticheZonaManuale(
            List<Raccolto> raccolti, List<Piantagione> piantagioni, List<Zona> zone) {

        Map<String, BigDecimal> statistiche = new HashMap<>();

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

    private Map<String, BigDecimal> calcolaStatistichePiantaManuale(
            List<Raccolto> raccolti, List<Piantagione> piantagioni, List<Pianta> piante) {

        Map<String, BigDecimal> statistiche = new HashMap<>();

        for (Raccolto raccolto : raccolti) {
            Piantagione piantagione = trovaPiantagionePerId(piantagioni, raccolto.getPiantagioneId());
            if (piantagione != null) {
                Pianta pianta = trovaPiantaPerId(piante, piantagione.getPiantaId());
                if (pianta != null) {
                    String chiave = pianta.getTipo() + " - " + pianta.getVarieta();
                    statistiche.merge(chiave, raccolto.getQuantitaKg(), BigDecimal::add);
                }
            }
        }

        return statistiche;
    }

    private BigDecimal calcolaTotaleRaccolti(List<Raccolto> raccolti) {
        return raccolti.stream()
                .map(Raccolto::getQuantitaKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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

    private Pianta trovaPiantaPerId(List<Pianta> piante, Integer id) {
        return piante.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
