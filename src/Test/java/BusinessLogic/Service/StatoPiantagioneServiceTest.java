package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import DomainModel.StatoPiantagione;
import ORM.StatoPiantagioneDAO;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("StatoPiantagioneService Test Suite")
public class StatoPiantagioneServiceTest {

    private static final TestLogger testLogger = new TestLogger(StatoPiantagioneServiceTest.class);
    private StatoPiantagioneService statoPiantagioneService;

    // Mock DAO che simula stati pre-inseriti nel database
    static class MockStatoPiantagioneDAO extends StatoPiantagioneDAO {

        private final List<StatoPiantagione> statiStandard = Arrays.asList(
            createStato(1, StatoPiantagione.ATTIVA, "Piantagione attiva e produttiva"),
            createStato(2, StatoPiantagione.SOSPESA, "Piantagione temporaneamente sospesa"),
            createStato(3, StatoPiantagione.IN_RACCOLTA, "Piantagione in fase di raccolta"),
            createStato(4, StatoPiantagione.COMPLETATA, "Piantagione completata"),
            createStato(5, StatoPiantagione.RIMOSSA, "Piantagione rimossa")
        );

        private static StatoPiantagione createStato(int id, String codice, String descrizione) {
            StatoPiantagione stato = new StatoPiantagione(id, codice, descrizione);
            stato.setDataCreazione(LocalDateTime.now());
            stato.setDataAggiornamento(LocalDateTime.now());
            return stato;
        }

        @Override
        public StatoPiantagione findByCodice(String codice) {
            return statiStandard.stream()
                .filter(s -> s.getCodice().equals(codice))
                .findFirst()
                .orElse(null);
        }

        @Override
        public StatoPiantagione findById(Integer id) {
            return statiStandard.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
        }

        @Override
        public List<StatoPiantagione> findAllOrdered() {
            return statiStandard;
        }

        @Override
        public boolean existsByCodice(String codice) {
            return findByCodice(codice) != null;
        }

        @Override
        public int countStati() {
            return statiStandard.size();
        }
    }

    @BeforeAll
    static void setupSuite() {
        testLogger.startTestSuite("StatoPiantagioneService");
    }

    @AfterAll
    static void tearDownSuite() {
        testLogger.endTestSuite("StatoPiantagioneService", 13, 13, 0);
    }

    @BeforeEach
    void setUp() {
        statoPiantagioneService = new StatoPiantagioneService(new MockStatoPiantagioneDAO());
    }

    @Test
    @DisplayName("Test recupero tutti gli stati")
    void testGetAllStati() {
        testLogger.startTest("getAllStati");

        // Act
        List<StatoPiantagione> result = statoPiantagioneService.getAllStati();

        // Assert
        assertNotNull(result, "La lista degli stati non deve essere null");
        assertEquals(5, result.size(), "Devono essere presenti 5 stati standard");

        testLogger.operation("Stati trovati", result.size());
        testLogger.testPassed("getAllStati");
    }

    @Test
    @DisplayName("Test ricerca stato per codice valido")
    void testGetStatoByCodiceValido() throws ValidationException, BusinessLogicException {
        testLogger.startTest("getStatoByCodice con codice valido");

        // Act
        StatoPiantagione result = statoPiantagioneService.getStatoByCodice(StatoPiantagione.ATTIVA);

        // Assert
        assertNotNull(result, "Lo stato trovato non deve essere null");
        assertEquals(1, result.getId(), "L'ID dello stato ATTIVA deve essere 1");
        assertEquals(StatoPiantagione.ATTIVA, result.getCodice(), "Il codice deve corrispondere");

        testLogger.operation("Stato trovato", result.getCodice());
        testLogger.testPassed("getStatoByCodice valido");
    }

    @Test
    @DisplayName("Test ricerca stato per codice null")
    void testGetStatoByCodiceNull() {
        testLogger.startTest("getStatoByCodice con codice null");

        // Act & Assert
        TestErrorHandler.runWithSuppressedErrors(() -> {
            ValidationException exception = assertThrows(ValidationException.class, () -> {
                statoPiantagioneService.getStatoByCodice(null);
            });

            assertTrue(exception.getMessage().contains("Codice stato"),
                      "Il messaggio di errore deve contenere 'Codice stato'");
        });

        testLogger.expectedError("getStatoByCodice null", "ValidationException");
        testLogger.testPassed("getStatoByCodice con codice null");
    }

    @Test
    @DisplayName("Test ricerca stato per codice inesistente")
    void testGetStatoByCodiceNonTrovato() {
        testLogger.startTest("getStatoByCodice con codice inesistente");

        // Act & Assert
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
            statoPiantagioneService.getStatoByCodice("INESISTENTE");
        });

        assertTrue(exception.getMessage().contains("StatoPiantagione"),
                  "Il messaggio di errore deve contenere 'StatoPiantagione'");

        testLogger.expectedError("getStatoByCodice inesistente", "BusinessLogicException");
        testLogger.testPassed("getStatoByCodice con codice inesistente");
    }

    @Test
    @DisplayName("Test ricerca stato per ID valido")
    void testGetStatoByIdValido() throws ValidationException, BusinessLogicException {
        testLogger.startTest("getStatoById con ID valido");

        // Act
        StatoPiantagione result = statoPiantagioneService.getStatoById(1);

        // Assert
        assertNotNull(result, "Lo stato trovato non deve essere null");
        assertEquals(1, result.getId(), "L'ID deve corrispondere");
        assertEquals(StatoPiantagione.ATTIVA, result.getCodice(), "Il codice deve essere ATTIVA");

        testLogger.operation("Stato per ID 1", result.getCodice());
        testLogger.testPassed("getStatoById valido");
    }

    @Test
    @DisplayName("Test ricerca stato per ID null")
    void testGetStatoByIdNull() {
        testLogger.startTest("getStatoById con ID null");

        // Act & Assert
        TestErrorHandler.runWithSuppressedErrors(() -> {
            ValidationException exception = assertThrows(ValidationException.class, () -> {
                statoPiantagioneService.getStatoById(null);
            });

            assertTrue(exception.getMessage().contains("ID stato"),
                      "Il messaggio di errore deve contenere 'ID stato'");
        });

        testLogger.expectedError("getStatoById null", "ValidationException");
        testLogger.testPassed("getStatoById con ID null");
    }

    @Test
    @DisplayName("Test ricerca stato per ID inesistente")
    void testGetStatoByIdNonTrovato() {
        testLogger.startTest("getStatoById con ID inesistente");

        // Act & Assert
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
            statoPiantagioneService.getStatoById(999);
        });

        assertTrue(exception.getMessage().contains("StatoPiantagione"),
                  "Il messaggio di errore deve contenere 'StatoPiantagione'");

        testLogger.expectedError("getStatoById inesistente", "BusinessLogicException");
        testLogger.testPassed("getStatoById con ID inesistente");
    }

    @Test
    @DisplayName("Test conteggio stati disponibili")
    void testCountStatiDisponibili() {
        testLogger.startTest("countStatiDisponibili");

        // Act
        int count = statoPiantagioneService.countStatiDisponibili();

        // Assert
        assertEquals(5, count, "Il conteggio deve essere 5");

        testLogger.operation("Stati contati", count);
        testLogger.testPassed("countStatiDisponibili");
    }

    @Test
    @DisplayName("Test verifica stati standard")
    void testVerificaStatiStandard() throws BusinessLogicException {
        testLogger.startTest("verificaStatiStandard");

        // Act & Assert - dovrebbe passare senza eccezioni
        assertTrue(statoPiantagioneService.verificaStatiStandard(),
                  "La verifica degli stati standard deve restituire true");

        testLogger.assertion("Tutti gli stati standard presenti", true);
        testLogger.testPassed("verificaStatiStandard");
    }

    @Test
    @DisplayName("Test recupero stato default")
    void testGetStatoDefault() throws ValidationException, BusinessLogicException {
        testLogger.startTest("getStatoDefault");

        // Act
        StatoPiantagione result = statoPiantagioneService.getStatoDefault();

        // Assert
        assertNotNull(result, "Lo stato default non deve essere null");
        assertEquals(StatoPiantagione.ATTIVA, result.getCodice(),
                    "Lo stato default deve essere ATTIVA");

        testLogger.operation("Stato default", result.getCodice());
        testLogger.testPassed("getStatoDefault");
    }

    @Test
    @DisplayName("Test esistenza stato per codice")
    void testExistsStatoByCodice() {
        testLogger.startTest("existsStatoByCodice");

        // Act & Assert
        assertTrue(statoPiantagioneService.existsStatoByCodice(StatoPiantagione.ATTIVA),
                  "Lo stato ATTIVA deve esistere");
        assertTrue(statoPiantagioneService.existsStatoByCodice(StatoPiantagione.COMPLETATA),
                  "Lo stato COMPLETATA deve esistere");
        assertFalse(statoPiantagioneService.existsStatoByCodice("INESISTENTE"),
                   "Lo stato INESISTENTE non deve esistere");
        assertFalse(statoPiantagioneService.existsStatoByCodice(null),
                   "Il controllo con codice null deve restituire false");
        assertFalse(statoPiantagioneService.existsStatoByCodice(""),
                   "Il controllo con codice vuoto deve restituire false");

        testLogger.assertion("Verifica esistenza stati", true);
        testLogger.testPassed("existsStatoByCodice");
    }

    @Test
    @DisplayName("Test validazione stato")
    void testIsStatoValido() {
        testLogger.startTest("isStatoValido");

        // Act & Assert
        assertTrue(statoPiantagioneService.isStatoValido(StatoPiantagione.ATTIVA),
                  "Lo stato ATTIVA deve essere valido");
        assertTrue(statoPiantagioneService.isStatoValido(StatoPiantagione.SOSPESA),
                  "Lo stato SOSPESA deve essere valido");
        assertFalse(statoPiantagioneService.isStatoValido("INESISTENTE"),
                   "Lo stato INESISTENTE non deve essere valido");
        assertFalse(statoPiantagioneService.isStatoValido(null),
                   "Il controllo con codice null deve restituire false");

        testLogger.assertion("Validazione stati", true);
        testLogger.testPassed("isStatoValido");
    }

    @Test
    @DisplayName("Test recupero descrizione stato")
    void testGetDescrizioneStato() {
        testLogger.startTest("getDescrizioneStato");

        // Act & Assert
        assertEquals("Piantagione attiva e produttiva",
                    statoPiantagioneService.getDescrizioneStato(StatoPiantagione.ATTIVA),
                    "La descrizione dello stato ATTIVA deve corrispondere");
        assertEquals("Stato non trovato",
                    statoPiantagioneService.getDescrizioneStato("INESISTENTE"),
                    "La descrizione per stato inesistente deve essere 'Stato non trovato'");
        assertEquals("Stato non trovato",
                    statoPiantagioneService.getDescrizioneStato(null),
                    "La descrizione per codice null deve essere 'Stato non trovato'");

        testLogger.operation("Descrizioni verificate", "OK");
        testLogger.testPassed("getDescrizioneStato");
    }
}
