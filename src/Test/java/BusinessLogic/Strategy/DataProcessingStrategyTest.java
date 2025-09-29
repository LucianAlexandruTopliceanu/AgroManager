package BusinessLogic.Strategy;

import BusinessLogic.Strategy.*;
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

public class DataProcessingStrategyTest {

    private List<Raccolto> raccolti;
    private List<Piantagione> piantagioni;
    private List<Zona> zone;
    private List<Pianta> piante;
    private DataProcessingContext context;

    @BeforeEach
    void setUp() {
        context = new DataProcessingContext();
        setupData();
    }

    private void setupData() {
        // Raccolti mock
        Raccolto raccolto1 = new Raccolto();
        raccolto1.setId(1);
        raccolto1.setDataRaccolto(LocalDate.now().minusDays(5));
        raccolto1.setQuantitaKg(new BigDecimal("25.50"));
        raccolto1.setPiantagioneId(1);

        Raccolto raccolto2 = new Raccolto();
        raccolto2.setId(2);
        raccolto2.setDataRaccolto(LocalDate.now().minusDays(3));
        raccolto2.setQuantitaKg(new BigDecimal("18.25"));
        raccolto2.setPiantagioneId(2);

        raccolti = Arrays.asList(raccolto1, raccolto2);

        // Piantagioni mock
        Piantagione piantagione1 = new Piantagione();
        piantagione1.setId(1);
        piantagione1.setPiantaId(1);
        piantagione1.setZonaId(1);

        Piantagione piantagione2 = new Piantagione();
        piantagione2.setId(2);
        piantagione2.setPiantaId(2);
        piantagione2.setZonaId(2);

        piantagioni = Arrays.asList(piantagione1, piantagione2);

        // Zone mock
        Zona zona1 = new Zona();
        zona1.setId(1);
        zona1.setNome("Zona Nord");

        Zona zona2 = new Zona();
        zona2.setId(2);
        zona2.setNome("Zona Sud");

        zone = Arrays.asList(zona1, zona2);

        // Piante mock
        Pianta pianta1 = new Pianta();
        pianta1.setId(1);
        pianta1.setTipo("Pomodoro");
        pianta1.setVarieta("San Marzano");

        Pianta pianta2 = new Pianta();
        pianta2.setId(2);
        pianta2.setTipo("Basilico");
        pianta2.setVarieta("Genovese");

        piante = Arrays.asList(pianta1, pianta2);
    }

    @Test
    void testReportRaccoltiStrategy() {
        String report = (String) context.executeProcessing(new ReportRaccoltiStrategy(), raccolti);

        assertNotNull(report);
        assertTrue(report.contains("=== REPORT RACCOLTI ==="));
        assertTrue(report.contains("Totale raccolti: 2"));
        assertTrue(report.contains("43.75 kg"));
    }

    @Test
    void testReportStatisticheZonaStrategy() {
        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> statistiche = (Map<String, BigDecimal>) context.executeProcessing(
            new ReportStatisticheZonaStrategy(), raccolti, piantagioni, zone);

        assertNotNull(statistiche);
        assertEquals(2, statistiche.size());
        assertTrue(statistiche.containsKey("Zona Nord"));
        assertTrue(statistiche.containsKey("Zona Sud"));
    }

    @Test
    void testProduzioneTotaleStrategy() {
        BigDecimal produzione = (BigDecimal) context.executeProcessing(
            new ProduzioneTotaleStrategy(), raccolti, 1);

        assertEquals(new BigDecimal("25.50"), produzione);
    }

    @Test
    void testTopPiantagioniStrategy() {
        @SuppressWarnings("unchecked")
        Map<Integer, BigDecimal> top = (Map<Integer, BigDecimal>) context.executeProcessing(
            new TopPiantagioniStrategy(), raccolti, 2);

        assertNotNull(top);
        assertEquals(2, top.size());
        // Prima piantagione (25.50) dovrebbe essere al primo posto
        Iterator<Map.Entry<Integer, BigDecimal>> iter = top.entrySet().iterator();
        Map.Entry<Integer, BigDecimal> first = iter.next();
        assertEquals(1, first.getKey());
        assertEquals(new BigDecimal("25.50"), first.getValue());
    }

    @Test
    void testProcessingTypeFiltering() {
        // Test che verifica il filtro per tipo di elaborazione
        String report = (String) context.executeIfType(
            DataProcessingStrategy.ProcessingType.REPORT,
            new ReportRaccoltiStrategy(),
            raccolti);

        assertNotNull(report);
        assertTrue(report.contains("REPORT RACCOLTI"));

        // Test che un calcolo non viene eseguito se si cerca un report
        Object result = context.executeIfType(
            DataProcessingStrategy.ProcessingType.REPORT,
            new ProduzioneTotaleStrategy(),
            raccolti, 1);

        assertNull(result);
    }

    @Test
    void testReportConDatiVuoti() {
        String report = (String) context.executeProcessing(
            new ReportRaccoltiStrategy(), Collections.emptyList());

        assertEquals("Nessun raccolto registrato.", report);
    }
}
