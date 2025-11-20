package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
import DomainModel.Raccolto;
import ORM.RaccoltoDAO;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("RaccoltoService Test Suite")
public class RaccoltoServiceTest {

    private static final TestLogger testLogger = new TestLogger(RaccoltoServiceTest.class);
    private RaccoltoService raccoltoService;
    private Raccolto raccoltoValido;

    // Mock DAO che simula successo nelle operazioni
    static class MockRaccoltoDAO extends RaccoltoDAO {
        @Override
        public void create(Raccolto raccolto) {
            // Simula l'assegnazione di un ID dopo il salvataggio
            raccolto.setId(1);
        }

        @Override
        public void update(Raccolto raccolto) {
            // Simula aggiornamento riuscito
        }

        @Override
        public java.util.List<Raccolto> findByPiantagione(Integer piantagioneId) {
            // Restituisce una lista vuota per evitare conflitti con il database reale
            return new java.util.ArrayList<>();
        }

        @Override
        public Raccolto read(int id) {
            // Simula che il raccolto esista
            if (id > 0) {
                Raccolto raccolto = new Raccolto();
                raccolto.setId(id);
                raccolto.setDataRaccolto(LocalDate.now().minusDays(1));
                raccolto.setQuantitaKg(new BigDecimal("25.50"));
                raccolto.setPiantagioneId(1);
                return raccolto;
            }
            return null;
        }
    }

    @BeforeAll
    static void setupSuite() {
        testLogger.startTestSuite("RaccoltoService");
    }

    @AfterAll
    static void tearDownSuite() {
        testLogger.endTestSuite("RaccoltoService", 8, 8, 0);
    }

    @BeforeEach
    void setUp() {
        raccoltoService = new RaccoltoService(new MockRaccoltoDAO());
        raccoltoValido = new Raccolto();
        raccoltoValido.setId(1);
        raccoltoValido.setDataRaccolto(LocalDate.now().minusDays(1));
        raccoltoValido.setQuantitaKg(new BigDecimal("25.50"));
        raccoltoValido.setNote("Raccolto di buona qualità");
        raccoltoValido.setPiantagioneId(1);
        raccoltoValido.setDataCreazione(LocalDateTime.now());
        raccoltoValido.setDataAggiornamento(LocalDateTime.now());
    }

    @Test
    @DisplayName("Test aggiunta raccolto null")
    void testAggiungiRaccoltoNull() {
        testLogger.startTest("aggiungiRaccolto con raccolto null");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            raccoltoService.aggiungiRaccolto(null);
        });
        assertTrue(exception.getMessage().contains("Raccolto non può essere null"));

        testLogger.expectedError("aggiungiRaccolto null", "ValidationException");
        testLogger.testPassed("aggiungiRaccolto con raccolto null");
    }

    @Test
    @DisplayName("Test aggiunta raccolto con data futura")
    void testAggiungiRaccoltoDataFutura() {
        testLogger.startTest("aggiungiRaccolto con data futura");

        raccoltoValido.setDataRaccolto(LocalDate.now().plusDays(1));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            raccoltoService.aggiungiRaccolto(raccoltoValido);
        });
        assertTrue(exception.getMessage().contains("dataRaccolto") || exception.getMessage().contains("futuro"));

        testLogger.expectedError("aggiungiRaccolto data futura", "ValidationException");
        testLogger.testPassed("aggiungiRaccolto con data futura");
    }

    @Test
    @DisplayName("Test aggiunta raccolto con quantità negativa")
    void testAggiungiRaccoltoQuantitaNegativa() {
        testLogger.startTest("aggiungiRaccolto con quantità negativa");

        raccoltoValido.setQuantitaKg(new BigDecimal("-1.0"));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            raccoltoService.aggiungiRaccolto(raccoltoValido);
        });
        assertTrue(exception.getMessage().contains("quantitaKg") || exception.getMessage().contains("maggiore"));

        testLogger.expectedError("aggiungiRaccolto quantità negativa", "ValidationException");
        testLogger.testPassed("aggiungiRaccolto con quantità negativa");
    }

    @Test
    @DisplayName("Test aggiunta raccolto con piantagione ID null")
    void testAggiungiRaccoltoPiantagioneIdNull() {
        testLogger.startTest("aggiungiRaccolto con piantagione ID null");

        raccoltoValido.setPiantagioneId(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            raccoltoService.aggiungiRaccolto(raccoltoValido);
        });
        assertTrue(exception.getMessage().contains("Piantagione"));

        testLogger.expectedError("aggiungiRaccolto piantagione ID null", "ValidationException");
        testLogger.testPassed("aggiungiRaccolto con piantagione ID null");
    }

    @Test
    @DisplayName("Test aggiunta raccolto valido")
    void testAggiungiRaccoltoValido() {
        testLogger.startTest("aggiungiRaccolto con raccolto valido");

        assertDoesNotThrow(() -> {
            raccoltoService.aggiungiRaccolto(raccoltoValido);
        });

        assertEquals(1, raccoltoValido.getId(), "Il raccolto deve avere ID assegnato");

        testLogger.operation("Raccolto aggiunto", raccoltoValido.getQuantitaKg() + " kg");
        testLogger.testPassed("aggiungiRaccolto con raccolto valido");
    }

    @Test
    @DisplayName("Test aggiornamento raccolto valido")
    void testAggiornaRaccoltoValido() {
        testLogger.startTest("aggiornaRaccolto con raccolto valido");

        assertDoesNotThrow(() -> {
            raccoltoService.aggiornaRaccolto(raccoltoValido);
        });

        testLogger.operation("Raccolto aggiornato", raccoltoValido.getQuantitaKg() + " kg");
        testLogger.testPassed("aggiornaRaccolto con raccolto valido");
    }
}
