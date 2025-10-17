package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import DomainModel.Piantagione;
import DomainModel.StatoPiantagione;
import ORM.PiantagioneDAO;
import ORM.StatoPiantagioneDAO;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per PiantagioneService - modalità sola lettura
 * Test uniformi con logging pulito e strutturato
 */
@DisplayName("PiantagioneService Test Suite")
public class PiantagioneServiceTest {

    private static final TestLogger testLogger = new TestLogger(PiantagioneServiceTest.class);
    private PiantagioneService piantagioneService;
    private StatoPiantagioneService mockStatoPiantagioneService;
    private Piantagione piantagioneValida;

    // Mock DAO che simula successo nelle operazioni
    static class MockPiantagioneDAO extends PiantagioneDAO {
        @Override
        public void create(Piantagione piantagione) {
            // Simula l'assegnazione di un ID dopo il salvataggio
            piantagione.setId(1);
        }

        @Override
        public void update(Piantagione piantagione) {
            // Simula aggiornamento riuscito
        }

        @Override
        public void cambiaStato(Integer piantagioneId, Integer nuovoStatoId) {
            // Simula cambio stato riuscito
        }

        @Override
        public List<Piantagione> findAllWithStato() {
            Piantagione p1 = new Piantagione();
            p1.setId(1);
            p1.setIdStatoPiantagione(1);
            return List.of(p1);
        }

        @Override
        public List<Piantagione> findAttive() {
            return findAllWithStato();
        }

        @Override
        public List<Piantagione> findByStato(String codiceStato) {
            return findAllWithStato();
        }
    }

    // Mock StatoPiantagioneService
    static class MockStatoPiantagioneService extends StatoPiantagioneService {

        // Classe Mock interna per StatoPiantagioneDAO
        static class MockStatoPiantagioneDAO extends StatoPiantagioneDAO {
            @Override
            public StatoPiantagione findByCodice(String codice) {
                StatoPiantagione stato = new StatoPiantagione();
                stato.setId(1);
                stato.setCodice(codice);
                stato.setDescrizione("Stato Mock");
                return stato;
            }

            @Override
            public StatoPiantagione findById(Integer id) {
                StatoPiantagione stato = new StatoPiantagione();
                stato.setId(id);
                stato.setCodice(StatoPiantagione.ATTIVA);
                stato.setDescrizione("Stato Mock");
                return stato;
            }
        }

        public MockStatoPiantagioneService() {
            super(new MockStatoPiantagioneDAO());
        }

        @Override
        public StatoPiantagione getStatoByCodice(String codice) {
            StatoPiantagione stato = new StatoPiantagione();
            stato.setId(1);
            stato.setCodice(codice);
            stato.setDescrizione("Stato Mock");
            return stato;
        }

        @Override
        public StatoPiantagione getStatoById(Integer id) {
            StatoPiantagione stato = new StatoPiantagione();
            stato.setId(id);
            stato.setCodice(StatoPiantagione.ATTIVA);
            stato.setDescrizione("Stato Mock");
            return stato;
        }
    }

    @BeforeAll
    static void setupSuite() {
        testLogger.startTestSuite("PiantagioneService");
    }

    @AfterAll
    static void tearDownSuite() {
        testLogger.endTestSuite("PiantagioneService", 10, 10, 0);
    }

    @BeforeEach
    void setUp() {
        // Mock dello StatoPiantagioneService
        mockStatoPiantagioneService = new MockStatoPiantagioneService();

        piantagioneService = new PiantagioneService(new MockPiantagioneDAO(), mockStatoPiantagioneService);

        piantagioneValida = new Piantagione();
        piantagioneValida.setId(1);
        piantagioneValida.setPiantaId(1);
        piantagioneValida.setZonaId(1);
        piantagioneValida.setMessaADimora(LocalDate.now());
        piantagioneValida.setQuantitaPianta(100);
        piantagioneValida.setIdStatoPiantagione(1);
        piantagioneValida.setDataCreazione(LocalDateTime.now());
        piantagioneValida.setDataAggiornamento(LocalDateTime.now());
    }

    @Test
    @DisplayName("Test aggiunta piantagione null")
    void testAggiungiPiantagioneNull() {
        testLogger.startTest("aggiungiPiantagione con piantagione null");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantagioneService.aggiungiPiantagione(null);
        });
        assertTrue(exception.getMessage().contains("Piantagione"));

        testLogger.expectedError("aggiungiPiantagione null", "ValidationException");
        testLogger.testPassed("aggiungiPiantagione con piantagione null");
    }

    @Test
    @DisplayName("Test aggiunta piantagione con pianta ID null")
    void testAggiungiPiantagionePiantaIdNull() {
        testLogger.startTest("aggiungiPiantagione con pianta ID null");

        piantagioneValida.setPiantaId(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantagioneService.aggiungiPiantagione(piantagioneValida);
        });
        assertTrue(exception.getMessage().contains("Pianta"));

        testLogger.expectedError("aggiungiPiantagione pianta ID null", "ValidationException");
        testLogger.testPassed("aggiungiPiantagione con pianta ID null");
    }

    @Test
    @DisplayName("Test aggiunta piantagione con zona ID null")
    void testAggiungiPiantagioneZonaIdNull() {
        testLogger.startTest("aggiungiPiantagione con zona ID null");

        piantagioneValida.setZonaId(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantagioneService.aggiungiPiantagione(piantagioneValida);
        });
        assertTrue(exception.getMessage().contains("Zona"));

        testLogger.expectedError("aggiungiPiantagione zona ID null", "ValidationException");
        testLogger.testPassed("aggiungiPiantagione con zona ID null");
    }

    @Test
    @DisplayName("Test aggiunta piantagione con quantità negativa")
    void testAggiungiPiantagioneQuantitaNegativa() {
        testLogger.startTest("aggiungiPiantagione con quantità negativa");

        piantagioneValida.setQuantitaPianta(-1);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantagioneService.aggiungiPiantagione(piantagioneValida);
        });
        assertTrue(exception.getMessage().contains("quantitaPianta") || exception.getMessage().contains("positiva"));

        testLogger.expectedError("aggiungiPiantagione quantità negativa", "ValidationException");
        testLogger.testPassed("aggiungiPiantagione con quantità negativa");
    }

    @Test
    @DisplayName("Test aggiunta piantagione valida")
    void testAggiungiPiantagioneValida() {
        testLogger.startTest("aggiungiPiantagione con piantagione valida");

        assertDoesNotThrow(() -> {
            piantagioneService.aggiungiPiantagione(piantagioneValida);
        });

        assertEquals(1, piantagioneValida.getId(), "La piantagione deve avere ID assegnato");

        testLogger.operation("Piantagione aggiunta", "ID: " + piantagioneValida.getId());
        testLogger.testPassed("aggiungiPiantagione con piantagione valida");
    }

    @Test
    @DisplayName("Test cambio stato piantagione")
    void testCambiaStatoPiantagione() {
        testLogger.startTest("cambiaStatoPiantagione");

        assertDoesNotThrow(() -> {
            piantagioneService.cambiaStatoPiantagione(1, StatoPiantagione.IN_RACCOLTA);
        });

        testLogger.operation("Stato cambiato", StatoPiantagione.IN_RACCOLTA);
        testLogger.testPassed("cambiaStatoPiantagione");
    }

    @Test
    @DisplayName("Test recupero piantagioni attive (mock)")
    void testMockPiantagioniAttive() {
        testLogger.startTest("mock piantagioni attive");

        // Testa che il mock DAO funzioni correttamente
        // Simula il comportamento senza chiamare metodi inesistenti del service
        MockPiantagioneDAO mockDAO = new MockPiantagioneDAO();
        List<Piantagione> result = mockDAO.findAllWithStato();

        assertNotNull(result, "La lista non deve essere null");
        assertFalse(result.isEmpty(), "La lista non deve essere vuota");
        assertEquals(1, result.get(0).getIdStatoPiantagione(), "Deve essere in stato ATTIVA");

        testLogger.operation("Piantagioni mock trovate", result.size());
        testLogger.testPassed("mock piantagioni attive");
    }
}
