package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
import DomainModel.Pianta;
import ORM.PiantaDAO;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("PiantaService Test Suite")
public class PiantaServiceTest {

    private static final TestLogger testLogger = new TestLogger(PiantaServiceTest.class);
    private PiantaService piantaService;
    private Pianta piantaValida;

    // Mock DAO che simula successo nelle operazioni
    static class MockPiantaDAO extends PiantaDAO {
        @Override
        public void create(Pianta pianta) {
            // Simula l'assegnazione di un ID dopo il salvataggio
            pianta.setId(1);
        }

        @Override
        public void update(Pianta pianta) {
            // Simula aggiornamento riuscito
        }

        @Override
        public List<Pianta> findAll() {
            // Restituisce una lista vuota per evitare conflitti di duplicati nei test
            return new java.util.ArrayList<>();
        }
    }

    @BeforeAll
    static void setupSuite() {
        testLogger.startTestSuite("PiantaService");
    }

    @AfterAll
    static void tearDownSuite() {
        testLogger.endTestSuite("PiantaService", 10, 10, 0);
    }

    @BeforeEach
    void setUp() {
        piantaService = new PiantaService(new MockPiantaDAO());
        piantaValida = new Pianta();
        piantaValida.setId(1);
        piantaValida.setTipo("Pomodoro");
        piantaValida.setVarieta("San Marzano");
        piantaValida.setCosto(new BigDecimal("2.50"));
        piantaValida.setNote("Pianta di alta qualità");
        piantaValida.setFornitoreId(1);
        piantaValida.setDataCreazione(LocalDateTime.now());
        piantaValida.setDataAggiornamento(LocalDateTime.now());
    }

    @Test
    @DisplayName("Test aggiunta pianta null")
    void testAggiungiPiantaNull() {
        testLogger.startTest("aggiungiPianta con pianta null");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantaService.aggiungiPianta(null);
        });
        assertTrue(exception.getMessage().contains("Pianta non può essere null"));

        testLogger.expectedError("aggiungiPianta null", "ValidationException");
        testLogger.testPassed("aggiungiPianta con pianta null");
    }

    @Test
    @DisplayName("Test aggiunta pianta con tipo vuoto")
    void testAggiungiPiantaTipoVuoto() {
        testLogger.startTest("aggiungiPianta con tipo vuoto");

        piantaValida.setTipo("");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantaService.aggiungiPianta(piantaValida);
        });
        assertTrue(exception.getMessage().contains("Tipo"));

        testLogger.expectedError("aggiungiPianta tipo vuoto", "ValidationException");
        testLogger.testPassed("aggiungiPianta con tipo vuoto");
    }

    @Test
    @DisplayName("Test aggiunta pianta con varietà null")
    void testAggiungiPiantaVarietaNull() {
        testLogger.startTest("aggiungiPianta con varietà null");

        piantaValida.setVarieta(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantaService.aggiungiPianta(piantaValida);
        });
        assertTrue(exception.getMessage().contains("Varietà"));

        testLogger.expectedError("aggiungiPianta varietà null", "ValidationException");
        testLogger.testPassed("aggiungiPianta con varietà null");
    }

    @Test
    @DisplayName("Test aggiunta pianta con costo negativo")
    void testAggiungiPiantaCostoNegativo() {
        testLogger.startTest("aggiungiPianta con costo negativo");

        piantaValida.setCosto(new BigDecimal("-1.00"));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantaService.aggiungiPianta(piantaValida);
        });
        assertTrue(exception.getMessage().contains("costo") || exception.getMessage().contains("negativo"));

        testLogger.expectedError("aggiungiPianta costo negativo", "ValidationException");
        testLogger.testPassed("aggiungiPianta con costo negativo");
    }

    @Test
    @DisplayName("Test aggiunta pianta con fornitore ID nullo")
    void testAggiungiPiantaFornitoreIdNull() {
        testLogger.startTest("aggiungiPianta con fornitore ID null");

        piantaValida.setFornitoreId(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantaService.aggiungiPianta(piantaValida);
        });
        assertTrue(exception.getMessage().contains("Fornitore"));

        testLogger.expectedError("aggiungiPianta fornitore ID null", "ValidationException");
        testLogger.testPassed("aggiungiPianta con fornitore ID null");
    }

    @Test
    @DisplayName("Test aggiunta pianta valida")
    void testAggiungiPiantaValida() {
        testLogger.startTest("aggiungiPianta con pianta valida");

        assertDoesNotThrow(() -> {
            piantaService.aggiungiPianta(piantaValida);
        });

        assertEquals(1, piantaValida.getId(), "La pianta deve avere ID assegnato");

        testLogger.operation("Pianta aggiunta", piantaValida.getTipo());
        testLogger.testPassed("aggiungiPianta con pianta valida");
    }

    @Test
    @DisplayName("Test aggiornamento pianta valida")
    void testAggiornaPiantaValida() {
        testLogger.startTest("aggiornaPianta con pianta valida");

        assertDoesNotThrow(() -> {
            piantaService.aggiornaPianta(piantaValida);
        });

        testLogger.operation("Pianta aggiornata", piantaValida.getTipo());
        testLogger.testPassed("aggiornaPianta con pianta valida");
    }
}
