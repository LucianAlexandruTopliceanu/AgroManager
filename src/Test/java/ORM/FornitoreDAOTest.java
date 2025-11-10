package ORM;

import BusinessLogic.Service.TestLogger;
import DomainModel.Fornitore;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per FornitoreDAO - testa solo le funzionalità realmente implementate
 * Test uniformi con logging pulito e strutturato
 */
@DisplayName("FornitoreDAO Test Suite")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FornitoreDAOTest {

    private static final TestLogger testLogger = new TestLogger(FornitoreDAOTest.class);
    private FornitoreDAO fornitoreDAO;
    private Fornitore testFornitore;

    @BeforeAll
    void setupSuite() {
        testLogger.startTestSuite("FornitoreDAO");
        fornitoreDAO = new FornitoreDAO();
        DatabaseConnection.getInstance().setTestMode(true);
    }

    @AfterAll
    void tearDownSuite() {
        testLogger.endTestSuite("FornitoreDAO", 4, 4, 0);
        DatabaseConnection.getInstance().setTestMode(false);
    }

    @BeforeEach
    void createTestFornitore() throws SQLException {
        testFornitore = new Fornitore();
        testFornitore.setNome("Fornitore Test SRL");
        testFornitore.setIndirizzo("Via Test 123, Milano");
        testFornitore.setNumeroTelefono("02-12345678");
        testFornitore.setEmail("test@fornitore.it");
        testFornitore.setPartitaIva("12345678901");

        fornitoreDAO.create(testFornitore);
    }

    @AfterEach
    void cleanUp() throws SQLException {
        if (testFornitore != null && testFornitore.getId() != null && testFornitore.getId() > 0) {
            fornitoreDAO.delete(testFornitore.getId());
        }
    }

    @Test
    @DisplayName("Test creazione fornitore")
    void testCreateFornitore() {
        testLogger.startTest("create fornitore");

        assertNotNull(testFornitore.getId());
        assertTrue(testFornitore.getId() > 0);

        testLogger.operation("Fornitore creato", "ID: " + testFornitore.getId());
        testLogger.testPassed("create fornitore");
    }

    @Test
    @DisplayName("Test lettura fornitore per ID")
    void testReadFornitore() throws SQLException {
        testLogger.startTest("read fornitore");

        // Usa il metodo read del BaseDAO
        Fornitore found = fornitoreDAO.read(testFornitore.getId());

        assertNotNull(found);
        assertEquals(testFornitore.getNome(), found.getNome());
        assertEquals(testFornitore.getEmail(), found.getEmail());
        assertEquals(testFornitore.getPartitaIva(), found.getPartitaIva());

        testLogger.operation("Fornitore letto", found.getNome());
        testLogger.testPassed("read fornitore");
    }

    @Test
    @DisplayName("Test aggiornamento fornitore")
    void testUpdateFornitore() throws SQLException {
        testLogger.startTest("update fornitore");

        String nuovoNome = "Fornitore Aggiornato SRL";
        testFornitore.setNome(nuovoNome);

        fornitoreDAO.update(testFornitore);

        // Verifica usando read del BaseDAO
        Fornitore updated = fornitoreDAO.read(testFornitore.getId());
        assertEquals(nuovoNome, updated.getNome());

        testLogger.operation("Fornitore aggiornato", nuovoNome);
        testLogger.testPassed("update fornitore");
    }

    @Test
    @DisplayName("Test recupero tutti i fornitori")
    void testFindAll() throws SQLException {
        testLogger.startTest("findAll fornitori");

        List<Fornitore> fornitori = fornitoreDAO.findAll();

        assertNotNull(fornitori);
        assertFalse(fornitori.isEmpty());

        testLogger.operation("Fornitori trovati", fornitori.size());
        testLogger.testPassed("findAll fornitori");
    }
}
