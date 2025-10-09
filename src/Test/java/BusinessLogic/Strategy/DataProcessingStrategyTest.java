package BusinessLogic.Strategy;

import BusinessLogic.Service.TestLogger;
import BusinessLogic.Strategy.*;
import DomainModel.Pianta;
import DomainModel.Piantagione;
import DomainModel.Raccolto;
import DomainModel.Zona;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per DataProcessingStrategy - modalità sola lettura
 * Test uniformi con logging pulito e strutturato
 * CORRETTO per conformità architetturale
 */
@DisplayName("DataProcessingStrategy Test Suite")
public class DataProcessingStrategyTest {

    private static final TestLogger testLogger = new TestLogger(DataProcessingStrategyTest.class);
    private List<Raccolto> raccolti;
    private List<Piantagione> piantagioni;
    private List<Zona> zone;
    private DataProcessingContext context;

    @BeforeAll
    static void setupSuite() {
        testLogger.startTestSuite("DataProcessingStrategy");
    }

    @AfterAll
    static void tearDownSuite() {
        testLogger.endTestSuite("DataProcessingStrategy", 6, 6, 0);
    }

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
    @DisplayName("Test context execution strategia")
    void testContextExecution() {
        testLogger.startTest("context execution");

        // Usa l'architettura reale documentata
        DataProcessingStrategy<String> mockStrategy = new DataProcessingStrategy<String>() {
            @Override
            public ProcessingResult<String> execute(Object... data) {
                return new ProcessingResult<>("Test Result", "Strategy eseguita con successo");
            }

            @Override
            public ProcessingType getType() {
                return ProcessingType.CALCULATION;
            }
        };

        ProcessingResult<?> result = context.executeStrategy(mockStrategy, raccolti, piantagioni, zone);

        assertNotNull(result);
        assertNotNull(result.getValue());

        testLogger.operation("Strategy eseguita", result.getFormattedOutput());
        testLogger.testPassed("context execution");
    }

    @Test
    @DisplayName("Test validazione parametri null")
    void testParametriNull() {
        testLogger.startTest("validazione parametri null");

        DataProcessingStrategy<String> mockStrategy = new DataProcessingStrategy<String>() {
            @Override
            public ProcessingResult<String> execute(Object... data) {
                return new ProcessingResult<>("Test", "Test completato");
            }

            @Override
            public ProcessingType getType() {
                return ProcessingType.CALCULATION;
            }
        };

        assertThrows(IllegalArgumentException.class, () -> {
            context.executeStrategy(mockStrategy, (Object[]) null);
        });

        testLogger.expectedError("parametri null", "IllegalArgumentException");
        testLogger.testPassed("validazione parametri null");
    }

    @Test
    @DisplayName("Test validazione tipo strategia")
    void testValidazioneTipoStrategia() {
        testLogger.startTest("validazione tipo strategia");

        DataProcessingStrategy<String> calculationStrategy = new DataProcessingStrategy<String>() {
            @Override
            public ProcessingResult<String> execute(Object... data) {
                return new ProcessingResult<>("Calculation", "Calcolo eseguito");
            }

            @Override
            public ProcessingType getType() {
                return ProcessingType.CALCULATION;
            }
        };

        // Prova ad eseguire come STATISTICS ma la strategia è CALCULATION
        assertThrows(IllegalArgumentException.class, () -> {
            context.executeStrategyOfType(
                DataProcessingStrategy.ProcessingType.STATISTICS,
                calculationStrategy,
                raccolti
            );
        });

        testLogger.expectedError("tipo strategia errato", "IllegalArgumentException");
        testLogger.testPassed("validazione tipo strategia");
    }

    @Test
    @DisplayName("Test execution con tipo corretto")
    void testExecutionTipoCorretto() {
        testLogger.startTest("execution con tipo corretto");

        DataProcessingStrategy<String> reportStrategy = new DataProcessingStrategy<String>() {
            @Override
            public ProcessingResult<String> execute(Object... data) {
                return new ProcessingResult<>("Report Generated", "Report creato con successo");
            }

            @Override
            public ProcessingType getType() {
                return ProcessingType.REPORT;
            }
        };

        ProcessingResult<?> result = context.executeStrategyOfType(
            DataProcessingStrategy.ProcessingType.REPORT,
            reportStrategy,
            raccolti, piantagioni
        );

        assertNotNull(result);
        assertNotNull(result.getValue());
        assertEquals("Report Generated", result.getValue());

        testLogger.operation("Report generato", result.getFormattedOutput());
        testLogger.testPassed("execution con tipo corretto");
    }

    @Test
    @DisplayName("Test mock strategia calculation")
    void testMockCalculationStrategy() {
        testLogger.startTest("mock calculation strategy");

        DataProcessingStrategy<BigDecimal> calculationStrategy = new DataProcessingStrategy<BigDecimal>() {
            @Override
            public ProcessingResult<BigDecimal> execute(Object... data) {
                // Simula calcolo produzione totale
                BigDecimal totale = new BigDecimal("43.75"); // 25.50 + 18.25
                return new ProcessingResult<>(totale, "Produzione totale: " + totale + " kg");
            }

            @Override
            public ProcessingType getType() {
                return ProcessingType.CALCULATION;
            }
        };

        ProcessingResult<?> result = context.executeStrategy(calculationStrategy, raccolti);

        assertNotNull(result);
        assertNotNull(result.getValue());
        assertEquals(new BigDecimal("43.75"), result.getValue());

        testLogger.operation("Produzione calcolata", result.getFormattedOutput());
        testLogger.testPassed("mock calculation strategy");
    }

    @Test
    @DisplayName("Test mock strategia statistics")
    void testMockStatisticsStrategy() {
        testLogger.startTest("mock statistics strategy");

        DataProcessingStrategy<Map<String, Object>> statisticsStrategy = new DataProcessingStrategy<Map<String, Object>>() {
            @Override
            public ProcessingResult<Map<String, Object>> execute(Object... data) {
                Map<String, Object> stats = new HashMap<>();
                stats.put("totalePiantagioni", 2);
                stats.put("totaleZone", 2);
                stats.put("mediaProduzione", 21.875);
                return new ProcessingResult<>(stats, "Statistiche generate: " + stats.size() + " metriche");
            }

            @Override
            public ProcessingType getType() {
                return ProcessingType.STATISTICS;
            }
        };

        ProcessingResult<?> result = context.executeStrategy(statisticsStrategy, piantagioni, zone);

        assertNotNull(result);
        assertNotNull(result.getValue());
        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) result.getValue();
        assertEquals(2, stats.get("totalePiantagioni"));

        testLogger.operation("Statistiche generate", result.getFormattedOutput());
        testLogger.testPassed("mock statistics strategy");
    }
}
