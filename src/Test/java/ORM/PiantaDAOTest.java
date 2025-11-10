package ORM;

import BusinessLogic.Service.TestLogger;
import DomainModel.Pianta;
import DomainModel.Fornitore;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per PiantaDAO - testa solo le funzionalità realmente implementate
 * Test uniformi con logging pulito e strutturato
 */
@DisplayName("PiantaDAO Test Suite")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PiantaDAOTest {

    private static final TestLogger testLogger = new TestLogger(PiantaDAOTest.class);
    private PiantaDAO piantaDAO;
    private FornitoreDAO fornitoreDAO;
    private Pianta testPianta;
    private Fornitore testFornitore;

    @BeforeAll
    void setupSuite() {
        testLogger.startTestSuite("PiantaDAO");
        piantaDAO = new PiantaDAO();
        fornitoreDAO = new FornitoreDAO();
        DatabaseConnection.getInstance().setTestMode(true);
    }

    @AfterAll
    void tearDownSuite() {
        testLogger.endTestSuite("PiantaDAO", 5, 5, 0);
        DatabaseConnection.getInstance().setTestMode(false);
    }

    @BeforeEach
    void createTestObjects() throws SQLException {
        // First create a Fornitore (required foreign key)
        testFornitore = new Fornitore();
        testFornitore.setNome("Fornitore Piante Test");
        testFornitore.setIndirizzo("Via Piante 123");
        testFornitore.setNumeroTelefono("0123456789");
        testFornitore.setEmail("piante@fornitore.com");
        testFornitore.setPartitaIva("12345678901");
        fornitoreDAO.create(testFornitore);

        // Then create the Pianta
        testPianta = new Pianta();
        testPianta.setTipo("Albero");
        testPianta.setVarieta("Melo");
        testPianta.setCosto(new BigDecimal("25.50"));
        testPianta.setNote("Pianta di test");
        testPianta.setFornitoreId(testFornitore.getId());
        
        piantaDAO.create(testPianta);
    }

    @AfterEach
    void cleanUp() throws SQLException {
        if (testPianta != null && testPianta.getId() != null) {
            piantaDAO.delete(testPianta.getId());
        }
        if (testFornitore != null && testFornitore.getId() != null) {
            fornitoreDAO.delete(testFornitore.getId());
        }
    }

    @Test
    @DisplayName("Test creazione pianta")
    void testCreatePianta() {
        testLogger.startTest("create pianta");

        assertNotNull(testPianta.getId());
        assertTrue(testPianta.getId() > 0);

        testLogger.operation("Pianta creata", testPianta.getTipo() + " - " + testPianta.getVarieta());
        testLogger.testPassed("create pianta");
    }

    @Test
    @DisplayName("Test lettura pianta per ID")
    void testReadPianta() throws SQLException {
        testLogger.startTest("read pianta");

        // Usa il metodo read del BaseDAO
        Pianta found = piantaDAO.read(testPianta.getId());

        assertNotNull(found);
        assertEquals(testPianta.getTipo(), found.getTipo());
        assertEquals(testPianta.getVarieta(), found.getVarieta());
        assertEquals(testPianta.getCosto(), found.getCosto());

        testLogger.operation("Pianta letta", found.getTipo() + " - " + found.getVarieta());
        testLogger.testPassed("read pianta");
    }

    @Test
    @DisplayName("Test aggiornamento pianta")
    void testUpdatePianta() throws SQLException {
        testLogger.startTest("update pianta");

        String nuovaVarieta = "Pero";
        testPianta.setVarieta(nuovaVarieta);

        piantaDAO.update(testPianta);

        // Verifica usando read del BaseDAO
        Pianta updated = piantaDAO.read(testPianta.getId());
        assertEquals(nuovaVarieta, updated.getVarieta());

        testLogger.operation("Pianta aggiornata", nuovaVarieta);
        testLogger.testPassed("update pianta");
    }

    @Test
    @DisplayName("Test recupero tutte le piante")
    void testFindAll() throws SQLException {
        testLogger.startTest("findAll piante");

        List<Pianta> piante = piantaDAO.findAll();

        assertNotNull(piante);
        assertFalse(piante.isEmpty());

        testLogger.operation("Piante trovate", piante.size());
        testLogger.testPassed("findAll piante");
    }

    @Test
    @DisplayName("Test ricerca piante per fornitore")
    void testFindByFornitore() throws SQLException {
        testLogger.startTest("findByFornitore pianta");

        // Usa il metodo esistente specifico del PiantaDAO
        List<Pianta> risultati = piantaDAO.findByFornitore(testFornitore.getId());

        assertNotNull(risultati);
        assertFalse(risultati.isEmpty());
        assertEquals(testFornitore.getId(), risultati.get(0).getFornitoreId());

        testLogger.operation("Piante per fornitore", risultati.size());
        testLogger.testPassed("findByFornitore pianta");
    }
}
