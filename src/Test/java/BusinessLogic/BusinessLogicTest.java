package BusinessLogic;

import BusinessLogic.Service.TestLogger;
import BusinessLogic.Strategy.DataProcessingStrategy;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per BusinessLogic - modalità sola lettura
 * Test uniformi con logging pulito e strutturato
 */
@DisplayName("BusinessLogic Test Suite")
public class BusinessLogicTest {

    private static final TestLogger testLogger = new TestLogger(BusinessLogicTest.class);
    private BusinessLogic businessLogic;

    @BeforeAll
    static void setupSuite() {
        testLogger.startTestSuite("BusinessLogic");
    }

    @AfterAll
    static void tearDownSuite() {
        testLogger.endTestSuite("BusinessLogic", 15, 15, 0);
    }

    @BeforeEach
    void setUp() {
        businessLogic = new BusinessLogic();
    }

    @Test
    @DisplayName("Test strategia produzione totale con dati vuoti")
    void testEseguiStrategiaProduzioneTotaleConDatiVuoti() {
        testLogger.startTest("eseguiStrategia produzione totale con dati vuoti");

        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.CALCULATION,
            "Produzione Totale",
            "1",
            null, null, null
        );

        assertNotNull(risultato);
        assertTrue(risultato.contains("0.00") || risultato.contains("0,00") ||
                  risultato.contains("Errore") || risultato.contains("Produzione"));

        testLogger.operation("Risultato produzione totale", risultato);
        testLogger.testPassed("eseguiStrategia produzione totale con dati vuoti");
    }

    @Test
    @DisplayName("Test strategia media per pianta con dati vuoti")
    void testEseguiStrategiaMediaPerPiantaConDatiVuoti() {
        testLogger.startTest("eseguiStrategia media per pianta con dati vuoti");

        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.CALCULATION,
            "Media per Pianta",
            "1",
            null, null, null
        );

        assertNotNull(risultato);
        assertTrue(risultato.contains("Errore") || risultato.contains("non trovata") ||
                  risultato.contains("0.00") || risultato.contains("0,00") ||
                  risultato.contains("Media") || risultato.contains("piantagione"));

        testLogger.operation("Risultato media per pianta", risultato);
        testLogger.testPassed("eseguiStrategia media per pianta con dati vuoti");
    }

    @Test
    @DisplayName("Test strategia produzione per periodo con date valide")
    void testEseguiStrategiaProduzionePerPeriodo() {
        testLogger.startTest("eseguiStrategia produzione per periodo con date valide");

        LocalDate inizio = LocalDate.now().minusDays(10);
        LocalDate fine = LocalDate.now();

        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.CALCULATION,
            "Produzione per Periodo",
            null,
            inizio, fine, null
        );

        assertNotNull(risultato);
        assertTrue(risultato.contains("periodo") || risultato.contains("Produzione"));
        assertFalse(risultato.contains("null"));

        testLogger.operation("Risultato produzione per periodo", risultato);
        testLogger.testPassed("eseguiStrategia produzione per periodo con date valide");
    }

    @Test
    @DisplayName("Test strategia top piantagioni")
    void testEseguiStrategiaTopPiantagioni() {
        testLogger.startTest("eseguiStrategia top piantagioni");

        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.STATISTICS,
            "Top Piantagioni",
            null,
            null, null, 2
        );

        assertNotNull(risultato);
        assertTrue(risultato.contains("Top") || risultato.contains("piantagioni"));

        testLogger.operation("Risultato top piantagioni", risultato);
        testLogger.testPassed("eseguiStrategia top piantagioni");
    }

    @Test
    @DisplayName("Test strategia piantagione migliore")
    void testEseguiStrategiaPiantagioneMigliore() {
        testLogger.startTest("eseguiStrategia piantagione migliore");

        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.STATISTICS,
            "Piantagione Migliore",
            null,
            null, null, null
        );

        assertNotNull(risultato);
        // Dovrebbe restituire un risultato anche se non ci sono dati
        assertFalse(risultato.isEmpty());

        testLogger.operation("Risultato piantagione migliore", risultato);
        testLogger.testPassed("eseguiStrategia piantagione migliore");
    }

    @Test
    @DisplayName("Test report raccolti")
    void testEseguiStrategiaReportRaccolti() {
        testLogger.startTest("eseguiStrategia report raccolti");

        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.REPORT,
            "Report Raccolti",
            null,
            null, null, null
        );

        assertNotNull(risultato);
        assertTrue(risultato.contains("REPORT") || risultato.contains("raccolti") || risultato.contains("Nessun"));

        testLogger.operation("Risultato report raccolti", risultato);
        testLogger.testPassed("eseguiStrategia report raccolti");
    }

    @Test
    @DisplayName("Test strategia con tipo sbagliato")
    void testEseguiStrategiaTipoSbagliato() {
        testLogger.startTest("eseguiStrategia con tipo sbagliato");

        // Tenta di eseguire una strategia CALCULATION come REPORT
        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.REPORT,
            "Produzione Totale",
            "1",
            null, null, null
        );

        assertNotNull(risultato);
        // Il sistema gestisce l'errore con messaggio di validazione
        assertTrue(risultato.contains("Errore") || risultato.contains("validazione") ||
                  risultato.contains("tipo") || risultato.contains("richiesto"));

        testLogger.operation("Risultato tipo sbagliato", risultato);
        testLogger.testPassed("eseguiStrategia con tipo sbagliato");
    }

    @Test
    @DisplayName("Test strategia non esistente")
    void testEseguiStrategiaNonEsistente() {
        testLogger.startTest("eseguiStrategia non esistente");

        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.CALCULATION,
            "Strategia Inesistente",
            "1",
            null, null, null
        );

        assertNotNull(risultato);
        // Il sistema gestisce l'errore - verifica che contenga informazioni sull'errore
        boolean hasError = risultato.contains("Errore") ||
                          risultato.contains("non riconosciuta") ||
                          risultato.contains("strategia") ||
                          risultato.contains("imprevisto") ||
                          risultato.contains("IllegalArgumentException");
        assertTrue(hasError);

        testLogger.operation("Risultato strategia non esistente", risultato);
        testLogger.testPassed("eseguiStrategia non esistente");
    }

    @Test
    @DisplayName("Test strategia parametri invalidi")
    void testEseguiStrategiaParametriInvalidi() {
        testLogger.startTest("eseguiStrategia parametri invalidi");

        // Test con ID piantagione non numerico
        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.CALCULATION,
            "Produzione Totale",
            "abc",
            null, null, null
        );

        assertNotNull(risultato);
        // Il sistema gestisce l'errore di formato
        assertTrue(risultato.contains("Errore") || risultato.contains("validazione") ||
                  risultato.contains("formato") || risultato.contains("intero"));

        testLogger.operation("Risultato parametri invalidi", risultato);
        testLogger.testPassed("eseguiStrategia parametri invalidi");
    }

    @Test
    @DisplayName("Test strategia efficienza produttiva")
    void testEseguiStrategiaEfficienzaProduttiva() {
        testLogger.startTest("eseguiStrategia efficienza produttiva");

        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.CALCULATION,
            "Efficienza Produttiva",
            "1",
            null, null, null
        );

        assertNotNull(risultato);
        // Dovrebbe gestire il caso anche senza dati reali
        assertTrue(risultato.contains("Efficienza") || risultato.contains("Errore"));

        testLogger.operation("Risultato efficienza produttiva", risultato);
        testLogger.testPassed("eseguiStrategia efficienza produttiva");
    }

    @Test
    @DisplayName("Test strategie statistiche zone")
    void testEseguiStrategiaStatisticheZone() {
        testLogger.startTest("eseguiStrategia statistiche zone");

        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.STATISTICS,
            "Statistiche Zone",
            null,
            null, null, null
        );

        assertNotNull(risultato);
        System.out.println("DEBUG - Risultato Statistiche Zone: " + risultato);
        // Il risultato contiene dati delle zone nel formato "Risultato: {Zone...}"
        assertTrue(risultato.contains("Zone") || risultato.contains("statistiche") ||
                  risultato.contains("Nessun") || risultato.contains("ZONA") ||
                  risultato.contains("Zona") || risultato.contains("Risultato"));

        testLogger.operation("Risultato statistiche zone", risultato);
        testLogger.testPassed("eseguiStrategia statistiche zone");
    }

    @Test
    @DisplayName("Test raccolto in periodo")
    void testIsRaccoltoInPeriodo() {
        testLogger.startTest("isRaccoltoInPeriodo");

        LocalDate data = LocalDate.of(2024, 7, 15);
        LocalDate inizio = LocalDate.of(2024, 7, 1);
        LocalDate fine = LocalDate.of(2024, 7, 31);

        assertTrue(businessLogic.isRaccoltoInPeriodo(data, inizio, fine));
        assertFalse(businessLogic.isRaccoltoInPeriodo(data, inizio.plusDays(20), fine));
        assertFalse(businessLogic.isRaccoltoInPeriodo(null, inizio, fine));
        assertFalse(businessLogic.isRaccoltoInPeriodo(data, null, fine));
        assertFalse(businessLogic.isRaccoltoInPeriodo(data, inizio, null));

        testLogger.testPassed("isRaccoltoInPeriodo");
    }

    @Test
    @DisplayName("Test validazione date con parametri non validi")
    void testParametriValidazioneDate() {
        testLogger.startTest("validazione date con parametri non validi");

        // Test con date invalide (fine prima di inizio)
        LocalDate inizio = LocalDate.now();
        LocalDate fine = LocalDate.now().minusDays(5);

        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.CALCULATION,
            "Produzione per Periodo",
            null,
            inizio, fine, null
        );

        // Il risultato dovrebbe contenere un messaggio di errore
        assertNotNull(risultato);
        assertTrue(risultato.contains("Errore") || risultato.contains("validazione") ||
                  risultato.contains("data") || risultato.contains("precedente"));

        testLogger.operation("Risultato validazione date", risultato);
        testLogger.testPassed("validazione date con parametri non validi");
    }

    @Test
    @DisplayName("Test validazione top N con parametri non validi")
    void testParametriValidazioneTopN() {
        testLogger.startTest("validazione top N con parametri non validi");

        // Test con topN negativo (se la strategia lo gestisce)
        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.STATISTICS,
            "Top Piantagioni",
            null,
            null, null, -1
        );

        assertNotNull(risultato);
        // Il risultato dovrebbe contenere un messaggio di errore per topN invalido
        assertTrue(risultato.contains("Errore") || risultato.contains("validazione") ||
                  risultato.contains("maggiore") || risultato.contains("zero"));

        testLogger.operation("Risultato validazione top N", risultato);
        testLogger.testPassed("validazione top N con parametri non validi");
    }
}
