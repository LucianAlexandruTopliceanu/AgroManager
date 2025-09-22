package Test;

import DomainModel.Piantagione;
import DomainModel.Pianta;
import DomainModel.Zona;
import DomainModel.Fornitore;
import ORM.PiantagioneDAO;
import ORM.PiantaDAO;
import ORM.ZonaDAO;
import ORM.FornitoreDAO;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PiantagioneDAOTest {

    private PiantagioneDAO piantagioneDAO;
    private PiantaDAO piantaDAO;
    private ZonaDAO zonaDAO;
    private FornitoreDAO fornitoreDAO;
    private Piantagione testPiantagione;
    private Pianta testPianta;
    private Zona testZona;
    private Fornitore testFornitore;

    @BeforeAll
    void setUp() throws SQLException {
        piantagioneDAO = new PiantagioneDAO();
        piantaDAO = new PiantaDAO();
        zonaDAO = new ZonaDAO();
        fornitoreDAO = new FornitoreDAO();
        ORM.DatabaseConnection.setTestMode(true);
    }

    @BeforeEach
    void createTestObjects() throws SQLException {
        // First create required foreign key objects
        testFornitore = new Fornitore();
        testFornitore.setNome("Fornitore Piantagione Test");
        testFornitore.setIndirizzo("Via Piantagione 123");
        testFornitore.setNumeroTelefono("0123456789");
        testFornitore.setEmail("piantagione@fornitore.com");
        testFornitore.setPartitaIva("12345678901");
        fornitoreDAO.create(testFornitore);

        testPianta = new Pianta();
        testPianta.setTipo("Albero");
        testPianta.setVarieta("Pesco");
        testPianta.setCosto(new java.math.BigDecimal("30.00"));
        testPianta.setNote("Pianta per piantagione test");
        testPianta.setFornitoreId(testFornitore.getId());
        piantaDAO.create(testPianta);

        testZona = new Zona();
        testZona.setNome("Zona Piantagione Test");
        testZona.setDimensione(200.0);
        testZona.setTipoTerreno("Fertile");
        zonaDAO.create(testZona);

        // Then create the Piantagione
        testPiantagione = new Piantagione();
        testPiantagione.setQuantitaPianta(50);
        testPiantagione.setMessaADimora(LocalDate.now());
        testPiantagione.setPiantaId(testPianta.getId());
        testPiantagione.setZonaId(testZona.getId());
        
        piantagioneDAO.create(testPiantagione);
    }

    @AfterAll
    void setTestModeOff() throws SQLException {
        ORM.DatabaseConnection.setTestMode(false);
    }

    @AfterEach
    void cleanUp() throws SQLException {
        if (testPiantagione != null && testPiantagione.getId() != null && testPiantagione.getId() > 0) {
            piantagioneDAO.delete(testPiantagione.getId());
        }
        if (testPianta != null && testPianta.getId() != null && testPianta.getId() > 0) {
            piantaDAO.delete(testPianta.getId());
        }
        if (testZona != null && testZona.getId() != null && testZona.getId() > 0) {
            zonaDAO.delete(testZona.getId());
        }
        if (testFornitore != null && testFornitore.getId() != null && testFornitore.getId() > 0) {
            fornitoreDAO.delete(testFornitore.getId());
        }
    }

    @Test
    void testCreatePiantagione() throws SQLException {
        assertNotNull(testPiantagione.getId());
        assertTrue(testPiantagione.getId() > 0);
    }

    @Test
    void testReadPiantagione() throws SQLException {
        Piantagione retrieved = piantagioneDAO.read(testPiantagione.getId());

        assertNotNull(retrieved);
        assertEquals(testPiantagione.getQuantitaPianta(), retrieved.getQuantitaPianta());
        assertEquals(testPiantagione.getMessaADimora(), retrieved.getMessaADimora());
        assertEquals(testPiantagione.getPiantaId(), retrieved.getPiantaId());
        assertEquals(testPiantagione.getZonaId(), retrieved.getZonaId());
    }

    @Test
    void testUpdatePiantagione() throws SQLException {
        testPiantagione.setQuantitaPianta(75);
        testPiantagione.setMessaADimora(LocalDate.now().minusDays(10));

        piantagioneDAO.update(testPiantagione);

        Piantagione updated = piantagioneDAO.read(testPiantagione.getId());
        assertEquals(75, updated.getQuantitaPianta());
        assertEquals(LocalDate.now().minusDays(10), updated.getMessaADimora());
    }

    @Test
    void testDeletePiantagione() throws SQLException {
        int id = testPiantagione.getId();
        piantagioneDAO.delete(id);

        Piantagione deleted = piantagioneDAO.read(id);
        assertNull(deleted);
    }

    @Test
    void testFindAllPiantagioni() throws SQLException {
        // Create a second Piantagione for testing
        Piantagione secondPiantagione = new Piantagione();
        secondPiantagione.setQuantitaPianta(25);
        secondPiantagione.setMessaADimora(LocalDate.now().plusDays(5));
        secondPiantagione.setPiantaId(testPianta.getId());
        secondPiantagione.setZonaId(testZona.getId());
        
        piantagioneDAO.create(secondPiantagione);

        List<Piantagione> piantagioni = piantagioneDAO.findAll();

        assertNotNull(piantagioni);
        assertTrue(piantagioni.size() >= 2);

        // Cleanup
        piantagioneDAO.delete(secondPiantagione.getId());
    }

    @Test
    void testReadNonExistentPiantagione() throws SQLException {
        Piantagione nonExistent = piantagioneDAO.read(-1);
        assertNull(nonExistent);
    }
}