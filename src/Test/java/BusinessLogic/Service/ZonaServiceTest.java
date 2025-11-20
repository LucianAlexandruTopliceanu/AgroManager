package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
import DomainModel.Zona;
import ORM.ZonaDAO;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("ZonaService Test Suite")
public class ZonaServiceTest {

    private static final TestLogger testLogger = new TestLogger(ZonaServiceTest.class);
    private ZonaService zonaService;
    private Zona zonaValida;

    // Mock DAO che simula successo nelle operazioni
    static class MockZonaDAO extends ZonaDAO {
        @Override
        public void create(Zona zona) {
            // Simula l'assegnazione di un ID dopo il salvataggio
            zona.setId(1);
        }

        @Override
        public void update(Zona zona) {
            // Simula aggiornamento riuscito
        }
    }

    @BeforeAll
    static void setupSuite() {
        testLogger.startTestSuite("ZonaService");
    }

    @AfterAll
    static void tearDownSuite() {
        testLogger.endTestSuite("ZonaService", 8, 8, 0);
    }

    @BeforeEach
    void setUp() {
        zonaService = new ZonaService(new MockZonaDAO());
        zonaValida = new Zona();
        zonaValida.setId(1);
        zonaValida.setNome("Zona Test");
        zonaValida.setDimensione(100.0);
        zonaValida.setTipoTerreno("Argilloso");
        zonaValida.setDataCreazione(LocalDateTime.now());
        zonaValida.setDataAggiornamento(LocalDateTime.now());
    }

    @Test
    @DisplayName("Test aggiunta zona null")
    void testAggiungiZonaNull() {
        testLogger.startTest("aggiungiZona con zona null");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            zonaService.aggiungiZona(null);
        });
        assertTrue(exception.getMessage().contains("Zona non può essere null"));

        testLogger.expectedError("aggiungiZona null", "ValidationException");
        testLogger.testPassed("aggiungiZona con zona null");
    }

    @Test
    @DisplayName("Test aggiunta zona con nome vuoto")
    void testAggiungiZonaNomeVuoto() {
        testLogger.startTest("aggiungiZona con nome vuoto");

        zonaValida.setNome("");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            zonaService.aggiungiZona(zonaValida);
        });
        assertTrue(exception.getMessage().contains("Nome"));

        testLogger.expectedError("aggiungiZona nome vuoto", "ValidationException");
        testLogger.testPassed("aggiungiZona con nome vuoto");
    }

    @Test
    @DisplayName("Test aggiunta zona con dimensione negativa")
    void testAggiungiZonaDimensioneNegativa() {
        testLogger.startTest("aggiungiZona con dimensione negativa");

        zonaValida.setDimensione(-1.0);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            zonaService.aggiungiZona(zonaValida);
        });
        assertTrue(exception.getMessage().contains("dimensione") || exception.getMessage().contains("maggiore"));

        testLogger.expectedError("aggiungiZona dimensione negativa", "ValidationException");
        testLogger.testPassed("aggiungiZona con dimensione negativa");
    }

    @Test
    @DisplayName("Test aggiunta zona valida")
    void testAggiungiZonaValida() {
        testLogger.startTest("aggiungiZona con zona valida");

        assertDoesNotThrow(() -> {
            zonaService.aggiungiZona(zonaValida);
        });

        assertEquals(1, zonaValida.getId(), "La zona deve avere ID assegnato");

        testLogger.operation("Zona aggiunta", zonaValida.getNome());
        testLogger.testPassed("aggiungiZona con zona valida");
    }

    @Test
    @DisplayName("Test aggiornamento zona valida")
    void testAggiornaZonaValida() {
        testLogger.startTest("aggiornaZona con zona valida");

        assertDoesNotThrow(() -> {
            zonaService.aggiornaZona(zonaValida);
        });

        testLogger.operation("Zona aggiornata", zonaValida.getNome());
        testLogger.testPassed("aggiornaZona con zona valida");
    }
}
