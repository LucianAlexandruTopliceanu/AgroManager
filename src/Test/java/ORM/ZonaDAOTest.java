package ORM;

import DomainModel.Zona;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ZonaDAOTest {

    private ZonaDAO zonaDAO;
    private Zona testZona;

    @BeforeAll
    void setUp() {
        zonaDAO = new ZonaDAO();
        DatabaseConnection.getInstance().setTestMode(true);
    }

    @BeforeEach
    void createTestZona() throws SQLException {
        testZona = new Zona();
        testZona.setNome("Zona Test");
        testZona.setDimensione(100.5);
        testZona.setTipoTerreno("Argilloso");
        
        zonaDAO.create(testZona);
    }

    @AfterAll
    void setTestModeOff() {
        DatabaseConnection.getInstance().setTestMode(false);
    }

    @AfterEach
    void cleanUp() throws SQLException {
        if (testZona != null && testZona.getId() != null && testZona.getId() > 0) {
            zonaDAO.delete(testZona.getId());
        }
    }

    @Test
    void testCreateZona() {
        assertNotNull(testZona.getId());
        assertTrue(testZona.getId() > 0);
    }

    @Test
    void testReadZona() throws SQLException {
        Zona retrieved = zonaDAO.read(testZona.getId());

        assertNotNull(retrieved);
        assertEquals(testZona.getNome(), retrieved.getNome());
        assertEquals(testZona.getDimensione(), retrieved.getDimensione());
        assertEquals(testZona.getTipoTerreno(), retrieved.getTipoTerreno());
    }

    @Test
    void testUpdateZona() throws SQLException {
        testZona.setNome("Zona Aggiornata");
        testZona.setDimensione(150.75);
        testZona.setTipoTerreno("Sabbioso");

        zonaDAO.update(testZona);

        Zona updated = zonaDAO.read(testZona.getId());
        assertEquals("Zona Aggiornata", updated.getNome());
        assertEquals(150.75, updated.getDimensione());
        assertEquals("Sabbioso", updated.getTipoTerreno());
    }

    @Test
    void testDeleteZona() throws SQLException {
        int id = testZona.getId();
        zonaDAO.delete(id);

        Zona deleted = zonaDAO.read(id);
        assertNull(deleted);
    }

    @Test
    void testFindAllZone() throws SQLException {
        // Create a second Zona for testing
        Zona secondZona = new Zona();
        secondZona.setNome("Seconda Zona");
        secondZona.setDimensione(75.25);
        secondZona.setTipoTerreno("Limoso");
        
        zonaDAO.create(secondZona);

        List<Zona> zone = zonaDAO.findAll();

        assertNotNull(zone);
        assertTrue(zone.size() >= 2);

        // Cleanup
        zonaDAO.delete(secondZona.getId());
    }

    @Test
    void testReadNonExistentZona() throws SQLException {
        Zona nonExistent = zonaDAO.read(-1);
        assertNull(nonExistent);
    }
}
