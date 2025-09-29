package ORM;

import DomainModel.Fornitore;
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
        DatabaseConnection.setTestMode(true);
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

    @AfterAll
    void setTestModeOff() throws SQLException {
        DatabaseConnection.setTestMode(false);
    }

    @AfterEach
    void cleanUp() throws SQLException {
        if (testFornitore != null && testFornitore.getId() != null && testFornitore.getId() > 0) {
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
        assertEquals(testFornitore.getIndirizzo(), retrieved.getIndirizzo());
        assertEquals(testFornitore.getNumeroTelefono(), retrieved.getNumeroTelefono());
        assertEquals(testFornitore.getEmail(), retrieved.getEmail());
        assertEquals(testFornitore.getPartitaIva(), retrieved.getPartitaIva());
    }

    @Test
    void testUpdateFornitore() throws SQLException {
        testFornitore.setNome("Fornitore Aggiornato SRL");
        testFornitore.setIndirizzo("Via Aggiornata 456, Roma");
        testFornitore.setNumeroTelefono("06-87654321");
        testFornitore.setEmail("aggiornato@fornitore.it");
        testFornitore.setPartitaIva("10987654321");

        fornitoreDAO.update(testFornitore);

        Fornitore updated = fornitoreDAO.read(testFornitore.getId());
        assertEquals("Fornitore Aggiornato SRL", updated.getNome());
        assertEquals("Via Aggiornata 456, Roma", updated.getIndirizzo());
        assertEquals("06-87654321", updated.getNumeroTelefono());
        assertEquals("aggiornato@fornitore.it", updated.getEmail());
        assertEquals("10987654321", updated.getPartitaIva());
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
        // Create a second Fornitore for testing
        Fornitore secondFornitore = new Fornitore();
        secondFornitore.setNome("Secondo Fornitore SRL");
        secondFornitore.setIndirizzo("Via Secondo 789, Napoli");
        secondFornitore.setNumeroTelefono("081-11111111");
        secondFornitore.setEmail("secondo@fornitore.it");
        secondFornitore.setPartitaIva("11111111111");

        fornitoreDAO.create(secondFornitore);

        List<Fornitore> fornitori = fornitoreDAO.findAll();

        assertNotNull(fornitori);
        assertTrue(fornitori.size() >= 2);

        // Cleanup
        fornitoreDAO.delete(secondFornitore.getId());
    }

    @Test
    void testReadNonExistentFornitore() throws SQLException {
        Fornitore nonExistent = fornitoreDAO.read(-1);
        assertNull(nonExistent);
    }

    @Test
    void testCreateFornitoreWithNullEmail() throws SQLException {
        Fornitore fornitoreNullEmail = new Fornitore();
        fornitoreNullEmail.setNome("Fornitore Senza Email");
        fornitoreNullEmail.setIndirizzo("Via Senza Email 123");
        fornitoreNullEmail.setNumeroTelefono("02-99999999");
        fornitoreNullEmail.setEmail(null);
        fornitoreNullEmail.setPartitaIva("99999999999");

        assertDoesNotThrow(() -> fornitoreDAO.create(fornitoreNullEmail));
        assertNotNull(fornitoreNullEmail.getId());

        // Cleanup
        fornitoreDAO.delete(fornitoreNullEmail.getId());
    }

    @Test
    void testCreateFornitoreWithNullPartitaIva() throws SQLException {
        Fornitore fornitoreNullPIva = new Fornitore();
        fornitoreNullPIva.setNome("Fornitore Senza P.IVA");
        fornitoreNullPIva.setIndirizzo("Via Senza PIVA 123");
        fornitoreNullPIva.setNumeroTelefono("02-88888888");
        fornitoreNullPIva.setEmail("senzapiva@fornitore.it");
        fornitoreNullPIva.setPartitaIva(null);

        assertDoesNotThrow(() -> fornitoreDAO.create(fornitoreNullPIva));
        assertNotNull(fornitoreNullPIva.getId());

        // Cleanup
        fornitoreDAO.delete(fornitoreNullPIva.getId());
    }
}
