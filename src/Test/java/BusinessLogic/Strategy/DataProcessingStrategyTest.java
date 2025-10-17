package BusinessLogic.Strategy;

import BusinessLogic.Service.TestLogger;
import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import DomainModel.Piantagione;
import DomainModel.Raccolto;
import DomainModel.Zona;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DataProcessingStrategy Test Suite")
public class DataProcessingStrategyTest {

    private static final TestLogger testLogger = new TestLogger(DataProcessingStrategyTest.class);
    private List<Raccolto> raccolti;
    private List<Piantagione> piantagioni;
    private List<Zona> zone;

    @BeforeAll
    static void setupSuite() {
        testLogger.startTestSuite("DataProcessingStrategy");
    }

    @AfterAll
    static void tearDownSuite() {
        testLogger.endTestSuite("DataProcessingStrategy", 8, 8, 0);
    }

    @BeforeEach
    void setUp() {
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
        raccolto2.setPiantagioneId(1);

        Raccolto raccolto3 = new Raccolto();
        raccolto3.setId(3);
        raccolto3.setDataRaccolto(LocalDate.now().minusDays(1));
        raccolto3.setQuantitaKg(new BigDecimal("32.75"));
        raccolto3.setPiantagioneId(2);

        raccolti = Arrays.asList(raccolto1, raccolto2, raccolto3);

        // Piantagioni mock
        Piantagione piantagione1 = new Piantagione();
        piantagione1.setId(1);
        piantagione1.setPiantaId(1);
        piantagione1.setZonaId(1);
        piantagione1.setQuantitaPianta(100);
        piantagione1.setMessaADimora(LocalDate.now().minusDays(60));

        Piantagione piantagione2 = new Piantagione();
        piantagione2.setId(2);
        piantagione2.setPiantaId(2);
        piantagione2.setZonaId(2);
        piantagione2.setQuantitaPianta(50);
        piantagione2.setMessaADimora(LocalDate.now().minusDays(45));

        piantagioni = Arrays.asList(piantagione1, piantagione2);

        // Zone mock
        Zona zona1 = new Zona();
        zona1.setId(1);
        zona1.setNome("Zona Nord");
        zona1.setDimensione(100.0);

        Zona zona2 = new Zona();
        zona2.setId(2);
        zona2.setNome("Zona Sud");
        zona2.setDimensione(80.0);

        zone = Arrays.asList(zona1, zona2);
    }

    @Test
    @DisplayName("Test ProduzioneTotaleStrategy - Calcolo corretto")
    void testProduzioneTotaleStrategy() throws Exception {
        testLogger.startTest("ProduzioneTotaleStrategy calcolo");

        ProduzioneTotaleStrategy strategy = new ProduzioneTotaleStrategy();

        // Test con piantagione ID 1
        ProcessingResult<BigDecimal> result = strategy.execute(raccolti, 1);

        assertNotNull(result);
        assertNotNull(result.data());
        assertEquals(DataProcessingStrategy.ProcessingType.CALCULATION, strategy.getType());

        // Verifica il calcolo: raccolto1 (25.50) + raccolto2 (18.25) = 43.75
        BigDecimal expected = new BigDecimal("43.75");
        assertEquals(0, expected.compareTo(result.data()));

        testLogger.operation("Produzione totale calcolata", result.data().toString() + " kg");
        testLogger.testPassed("ProduzioneTotaleStrategy calcolo");
    }

    @Test
    @DisplayName("Test MediaProduzioneStrategy - Calcolo per pianta")
    void testMediaProduzioneStrategy() throws Exception {
        testLogger.startTest("MediaProduzioneStrategy calcolo");

        MediaProduzioneStrategy strategy = new MediaProduzioneStrategy();

        ProcessingResult<BigDecimal> result = strategy.execute(raccolti, piantagioni, 1);

        assertNotNull(result);
        assertNotNull(result.data());
        assertEquals(DataProcessingStrategy.ProcessingType.CALCULATION, strategy.getType());

        // Verifica il calcolo: totale 43.75 kg / 100 piante = 0.44 kg per pianta
        BigDecimal expected = new BigDecimal("0.44");
        assertEquals(0, expected.compareTo(result.data()));

        testLogger.operation("Media produzione calcolata", result.data().toString() + " kg/pianta");
        testLogger.testPassed("MediaProduzioneStrategy calcolo");
    }

    @Test
    @DisplayName("Test validazione parametri - ProduzioneTotaleStrategy")
    void testValidazioneParametriProduzioneTotale() {
        testLogger.startTest("validazione parametri ProduzioneTotaleStrategy");

        ProduzioneTotaleStrategy strategy = new ProduzioneTotaleStrategy();

        // Test parametri null
        assertThrows(ValidationException.class, () -> {
            strategy.execute((Object[]) null);
        });

        // Test parametri insufficienti
        assertThrows(ValidationException.class, () -> {
            strategy.execute(raccolti);
        });

        // Test tipo parametro errato
        assertThrows(ValidationException.class, () -> {
            strategy.execute("wrong type", 1);
        });

        testLogger.expectedError("parametri non validi", "ValidationException");
        testLogger.testPassed("validazione parametri ProduzioneTotaleStrategy");
    }

    @Test
    @DisplayName("Test validazione parametri - MediaProduzioneStrategy")
    void testValidazioneParametriMediaProduzione() {
        testLogger.startTest("validazione parametri MediaProduzioneStrategy");

        MediaProduzioneStrategy strategy = new MediaProduzioneStrategy();

        // Test parametri insufficienti
        assertThrows(ValidationException.class, () -> {
            strategy.execute(raccolti, piantagioni);
        });

        // Test tipo parametro errato per piantagione ID
        assertThrows(ValidationException.class, () -> {
            strategy.execute(raccolti, piantagioni, "wrong type");
        });

        testLogger.expectedError("parametri non validi", "ValidationException");
        testLogger.testPassed("validazione parametri MediaProduzioneStrategy");
    }

    @Test
    @DisplayName("Test BusinessLogicException - Piantagione non trovata")
    void testPiantagioneNonTrovata() {
        testLogger.startTest("piantagione non trovata");

        MediaProduzioneStrategy strategy = new MediaProduzioneStrategy();

        // Test con ID piantagione inesistente
        assertThrows(BusinessLogicException.class, () -> {
            strategy.execute(raccolti, piantagioni, 999);
        });

        testLogger.expectedError("piantagione non trovata", "BusinessLogicException");
        testLogger.testPassed("piantagione non trovata");
    }

    @Test
    @DisplayName("Test ProcessingResult - Struttura dati")
    void testProcessingResultStruttura() throws Exception {
        testLogger.startTest("ProcessingResult struttura");

        ProduzioneTotaleStrategy strategy = new ProduzioneTotaleStrategy();
        ProcessingResult<BigDecimal> result = strategy.execute(raccolti, 1);

        // Test metodi della classe ProcessingResult
        assertNotNull(result.data());
        assertNotNull(result.metadata());
        assertTrue(result.metadata().isEmpty()); // Metadata vuoto per default

        // Test compatibilità metodo legacy
        assertEquals(result.data(), result.getValue());

        testLogger.operation("ProcessingResult validato", "getData() e metadata verificati");
        testLogger.testPassed("ProcessingResult struttura");
    }

    @Test
    @DisplayName("Test separazione responsabilità - Solo dati numerici")
    void testSeparazioneResponsabilita() throws Exception {
        testLogger.startTest("separazione responsabilità");

        ProduzioneTotaleStrategy strategy = new ProduzioneTotaleStrategy();
        ProcessingResult<BigDecimal> result = strategy.execute(raccolti, 1);

        // Verifica che la strategy restituisca solo dati numerici puri
        assertInstanceOf(BigDecimal.class, result.data());
        assertFalse(result.data().toString().contains("kg")); // Nessuna unità di misura
        assertFalse(result.data().toString().contains("€")); // Nessun simbolo di valuta

        // Il dato deve essere numerico puro per permettere alla view di formattarlo
        BigDecimal data = result.data();
        assertTrue(data.compareTo(BigDecimal.ZERO) >= 0);

        testLogger.operation("Dati puri verificati", "Nessuna formattazione nella strategy");
        testLogger.testPassed("separazione responsabilità");
    }

    @Test
    @DisplayName("Test tipi di processing - Enum ProcessingType")
    void testTipiProcessing() {
        testLogger.startTest("tipi processing");

        ProduzioneTotaleStrategy calculationStrategy = new ProduzioneTotaleStrategy();
        MediaProduzioneStrategy mediaStrategy = new MediaProduzioneStrategy();

        // Verifica tipi delle strategy
        assertEquals(DataProcessingStrategy.ProcessingType.CALCULATION, calculationStrategy.getType());
        assertEquals(DataProcessingStrategy.ProcessingType.CALCULATION, mediaStrategy.getType());

        // Verifica enum ProcessingType
        assertNotNull(DataProcessingStrategy.ProcessingType.CALCULATION.getDescription());
        assertNotNull(DataProcessingStrategy.ProcessingType.STATISTICS.getDescription());
        assertNotNull(DataProcessingStrategy.ProcessingType.REPORT.getDescription());

        assertEquals("Calcoli numerici", DataProcessingStrategy.ProcessingType.CALCULATION.getDescription());

        testLogger.operation("Tipi processing verificati", "Enum e strategy types corretti");
        testLogger.testPassed("tipi processing");
    }
}
