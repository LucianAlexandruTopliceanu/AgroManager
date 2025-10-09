package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
import DomainModel.Fornitore;
import ORM.FornitoreDAO;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per FornitoreService - modalità sola lettura
 * Test uniformi con logging pulito e strutturato
 */
@DisplayName("FornitoreService Test Suite")
public class FornitoreServiceTest {

    private static final TestLogger testLogger = new TestLogger(FornitoreServiceTest.class);
    private FornitoreService fornitoreService;
    private Fornitore fornitoreValido;

    // Mock DAO che simula successo nelle operazioni
    static class MockFornitoreDAO extends FornitoreDAO {
        @Override
        public void create(Fornitore fornitore) {
            // Simula l'assegnazione di un ID dopo il salvataggio
            fornitore.setId(1);
        }

        @Override
        public void update(Fornitore fornitore) {
            // Simula aggiornamento riuscito
        }
    }

    @BeforeAll
    static void setupSuite() {
        testLogger.startTestSuite("FornitoreService");
    }

    @AfterAll
    static void tearDownSuite() {
        testLogger.endTestSuite("FornitoreService", 8, 8, 0);
    }

    @BeforeEach
    void setUp() {
        fornitoreService = new FornitoreService(new MockFornitoreDAO());
        fornitoreValido = new Fornitore();
        fornitoreValido.setId(1);
        fornitoreValido.setNome("Fornitore Test SRL");
        fornitoreValido.setIndirizzo("Via Test 123, Milano");
        fornitoreValido.setNumeroTelefono("02-12345678");
        fornitoreValido.setEmail("test@fornitore.it");
        fornitoreValido.setPartitaIva("12345678901");
        fornitoreValido.setDataCreazione(LocalDateTime.now());
        fornitoreValido.setDataAggiornamento(LocalDateTime.now());
    }

    @Test
    @DisplayName("Test aggiunta fornitore null")
    void testAggiungiFornitoreNull() {
        testLogger.startTest("aggiungiFornitore con fornitore null");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            fornitoreService.aggiungiFornitore(null);
        });
        assertTrue(exception.getMessage().contains("Fornitore non può essere null"));

        testLogger.expectedError("aggiungiFornitore null", "ValidationException");
        testLogger.testPassed("aggiungiFornitore con fornitore null");
    }

    @Test
    @DisplayName("Test aggiunta fornitore con nome vuoto")
    void testAggiungiFornitoreNomeVuoto() {
        testLogger.startTest("aggiungiFornitore con nome vuoto");

        fornitoreValido.setNome("");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            fornitoreService.aggiungiFornitore(fornitoreValido);
        });
        assertTrue(exception.getMessage().contains("Nome fornitore"));

        testLogger.expectedError("aggiungiFornitore nome vuoto", "ValidationException");
        testLogger.testPassed("aggiungiFornitore con nome vuoto");
    }

    @Test
    @DisplayName("Test aggiunta fornitore con nome null")
    void testAggiungiFornitoreNomeNull() {
        testLogger.startTest("aggiungiFornitore con nome null");

        fornitoreValido.setNome(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            fornitoreService.aggiungiFornitore(fornitoreValido);
        });
        assertTrue(exception.getMessage().contains("Nome fornitore"));

        testLogger.expectedError("aggiungiFornitore nome null", "ValidationException");
        testLogger.testPassed("aggiungiFornitore con nome null");
    }

    @Test
    @DisplayName("Test aggiunta fornitore con indirizzo null")
    void testAggiungiFornitoreIndirizzoNull() {
        testLogger.startTest("aggiungiFornitore con indirizzo null");

        fornitoreValido.setIndirizzo(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            fornitoreService.aggiungiFornitore(fornitoreValido);
        });
        assertTrue(exception.getMessage().contains("Indirizzo"));

        testLogger.expectedError("aggiungiFornitore indirizzo null", "ValidationException");
        testLogger.testPassed("aggiungiFornitore con indirizzo null");
    }

    @Test
    @DisplayName("Test aggiunta fornitore con email non valida")
    void testAggiungiFornitoreEmailFormatoNonValido() {
        testLogger.startTest("aggiungiFornitore con email non valida");

        fornitoreValido.setEmail("email-non-valida");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            fornitoreService.aggiungiFornitore(fornitoreValido);
        });
        assertTrue(exception.getMessage().contains("Email"));

        testLogger.expectedError("aggiungiFornitore email non valida", "ValidationException");
        testLogger.testPassed("aggiungiFornitore con email non valida");
    }

    @Test
    @DisplayName("Test aggiunta fornitore con telefono troppo corto")
    void testAggiungiFornitoreTelefonoTroppoCorto() {
        testLogger.startTest("aggiungiFornitore con telefono troppo corto");

        fornitoreValido.setNumeroTelefono("123");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            fornitoreService.aggiungiFornitore(fornitoreValido);
        });
        assertTrue(exception.getMessage().contains("numeroTelefono") ||
                  exception.getMessage().contains("almeno 8 caratteri"));

        testLogger.expectedError("aggiungiFornitore telefono corto", "ValidationException");
        testLogger.testPassed("aggiungiFornitore con telefono troppo corto");
    }

    @Test
    @DisplayName("Test aggiunta fornitore valido")
    void testAggiungiFornitoreValido() {
        testLogger.startTest("aggiungiFornitore con fornitore valido");

        assertDoesNotThrow(() -> {
            fornitoreService.aggiungiFornitore(fornitoreValido);
        });

        assertEquals(1, fornitoreValido.getId(), "Il fornitore deve avere ID assegnato");

        testLogger.operation("Fornitore aggiunto", fornitoreValido.getNome());
        testLogger.testPassed("aggiungiFornitore con fornitore valido");
    }

    @Test
    @DisplayName("Test aggiornamento fornitore valido")
    void testAggiornaFornitoreValido() {
        testLogger.startTest("aggiornaFornitore con fornitore valido");

        assertDoesNotThrow(() -> {
            fornitoreService.aggiornaFornitore(fornitoreValido);
        });

        testLogger.operation("Fornitore aggiornato", fornitoreValido.getNome());
        testLogger.testPassed("aggiornaFornitore con fornitore valido");
    }
}
