package ORM;

import DomainModel.Pianta;
import DomainModel.Fornitore;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PiantaDAOTest {

    private PiantaDAO piantaDAO;
    private FornitoreDAO fornitoreDAO;
    private Pianta testPianta;
    private Fornitore testFornitore;

    @BeforeAll
    void setUp() throws SQLException {
        piantaDAO = new PiantaDAO();
        fornitoreDAO = new FornitoreDAO();
        DatabaseConnection.setTestMode(true);
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

    @AfterAll
    void setTestModeOff() throws SQLException {
        DatabaseConnection.setTestMode(false);
    }

    @AfterEach
    void cleanUp() throws SQLException {
        if (testPianta != null && testPianta.getId() != null && testPianta.getId() > 0) {
            piantaDAO.delete(testPianta.getId());
        }
        if (testFornitore != null && testFornitore.getId() != null && testFornitore.getId() > 0) {
            fornitoreDAO.delete(testFornitore.getId());
        }
    }

    @Test
    void testCreatePianta() throws SQLException {
        assertNotNull(testPianta.getId());
        assertTrue(testPianta.getId() > 0);
    }

    @Test
    void testReadPianta() throws SQLException {
        Pianta retrieved = piantaDAO.read(testPianta.getId());

        assertNotNull(retrieved);
        assertEquals(testPianta.getTipo(), retrieved.getTipo());
        assertEquals(testPianta.getVarieta(), retrieved.getVarieta());
        assertEquals(testPianta.getCosto(), retrieved.getCosto());
        assertEquals(testPianta.getFornitoreId(), retrieved.getFornitoreId());
    }

    @Test
    void testUpdatePianta() throws SQLException {
        testPianta.setTipo("Arbusto");
        testPianta.setVarieta("Rosmarino");
        testPianta.setCosto(new BigDecimal("15.75"));
        testPianta.setNote("Pianta aggiornata");

        piantaDAO.update(testPianta);

        Pianta updated = piantaDAO.read(testPianta.getId());
        assertEquals("Arbusto", updated.getTipo());
        assertEquals("Rosmarino", updated.getVarieta());
        assertEquals(new BigDecimal("15.75"), updated.getCosto());
        assertEquals("Pianta aggiornata", updated.getNote());
    }

    @Test
    void testDeletePianta() throws SQLException {
        int id = testPianta.getId();
        piantaDAO.delete(id);

        Pianta deleted = piantaDAO.read(id);
        assertNull(deleted);
    }

    @Test
    void testFindAllPiante() throws SQLException {
        // Create a second Pianta for testing
        Pianta secondPianta = new Pianta();
        secondPianta.setTipo("Ortaggio");
        secondPianta.setVarieta("Pomodoro");
        secondPianta.setCosto(new BigDecimal("5.25"));
        secondPianta.setNote("Seconda pianta di test");
        secondPianta.setFornitoreId(testFornitore.getId());
        
        piantaDAO.create(secondPianta);

        List<Pianta> piante = piantaDAO.findAll();

        assertNotNull(piante);
        assertTrue(piante.size() >= 2);

        // Cleanup
        piantaDAO.delete(secondPianta.getId());
    }

    @Test
    void testReadNonExistentPianta() throws SQLException {
        Pianta nonExistent = piantaDAO.read(-1);
        assertNull(nonExistent);
    }
}