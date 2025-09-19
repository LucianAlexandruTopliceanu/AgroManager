package Test;

import DomainModel.Fornitore;
import ORM.FornitoreDAO;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FornitoreDAOTest {

    private FornitoreDAO fornitoreDAO;
    private Fornitore testFornitore;

    @BeforeAll
    void setUp() throws SQLException {
        fornitoreDAO = new FornitoreDAO();
        ORM.DatabaseConnection.setTestMode(true);
    }

    @BeforeEach
    void createTestFornitore() throws SQLException {
        testFornitore = new Fornitore();
        testFornitore.setNome("Test Fornitore");
        testFornitore.setIndirizzo("Via Test 123");
        testFornitore.setNumeroTelefono("0123456789");
        testFornitore.setEmail("test@fornitore.com");
        testFornitore.setPartitaIva("12345678901");

        fornitoreDAO.create(testFornitore);
    }

    @AfterAll
    void setTestModeOff() throws SQLException {
        ORM.DatabaseConnection.setTestMode(false);
    }

    @AfterEach
    void cleanUp() throws SQLException {
        if (testFornitore != null && testFornitore.getId() > 0) {
            fornitoreDAO.delete(testFornitore.getId());
        }
    }

    @Test
    void testCreateFornitore() throws SQLException {
        assertNotNull(testFornitore.getId());
        assertTrue(testFornitore.getId() > 0);
    }

    @Test
    void testReadFornitore() throws SQLException {
        Fornitore retrieved = fornitoreDAO.read(testFornitore.getId());

        assertNotNull(retrieved);
        assertEquals(testFornitore.getNome(), retrieved.getNome());
        assertEquals(testFornitore.getEmail(), retrieved.getEmail());
        assertEquals(testFornitore.getPartitaIva(), retrieved.getPartitaIva());
    }

    @Test
    void testUpdateFornitore() throws SQLException {
        testFornitore.setNome("Fornitore Aggiornato");
        testFornitore.setEmail("aggiornato@fornitore.com");

        fornitoreDAO.update(testFornitore);

        Fornitore updated = fornitoreDAO.read(testFornitore.getId());
        assertEquals("Fornitore Aggiornato", updated.getNome());
        assertEquals("aggiornato@fornitore.com", updated.getEmail());
    }

    @Test
    void testDeleteFornitore() throws SQLException {
        int id = testFornitore.getId();
        fornitoreDAO.delete(id);

        Fornitore deleted = fornitoreDAO.read(id);
        assertNull(deleted);
    }

    @Test
    void testFindAllFornitori() throws SQLException {
        // Creiamo un secondo fornitore per il test
        Fornitore anotherFornitore = new Fornitore();
        anotherFornitore.setNome("Second Fornitore");
        anotherFornitore.setIndirizzo("Via Second 456");
        anotherFornitore.setNumeroTelefono("0987654321");
        anotherFornitore.setEmail("second@fornitore.com");
        anotherFornitore.setPartitaIva("98765432109");

        fornitoreDAO.create(anotherFornitore);

        List<Fornitore> fornitori = fornitoreDAO.findAll();

        assertNotNull(fornitori);
        assertTrue(fornitori.size() >= 2);

        // Pulizia
        fornitoreDAO.delete(anotherFornitore.getId());
    }

    @Test
    void testReadNonExistentFornitore() throws SQLException {
        Fornitore nonExistent = fornitoreDAO.read(-1);
        assertNull(nonExistent);
    }
}