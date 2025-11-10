package ORM;

import DomainModel.Raccolto;
import DomainModel.Piantagione;
import DomainModel.Pianta;
import DomainModel.Zona;
import DomainModel.Fornitore;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RaccoltoDAOTest {

    private RaccoltoDAO raccoltoDAO;
    private PiantagioneDAO piantagioneDAO;
    private PiantaDAO piantaDAO;
    private ZonaDAO zonaDAO;
    private FornitoreDAO fornitoreDAO;
    private Raccolto testRaccolto;
    private Piantagione testPiantagione;
    private Pianta testPianta;
    private Zona testZona;
    private Fornitore testFornitore;

    @BeforeAll
    void setUp() {
        raccoltoDAO = new RaccoltoDAO();
        piantagioneDAO = new PiantagioneDAO();
        piantaDAO = new PiantaDAO();
        zonaDAO = new ZonaDAO();
        fornitoreDAO = new FornitoreDAO();
        DatabaseConnection.getInstance().setTestMode(true);
    }

    @BeforeEach
    void createTestObjects() throws SQLException {
        // First create the hierarchy of required objects
        testFornitore = new Fornitore();
        testFornitore.setNome("Fornitore Raccolto Test");
        testFornitore.setIndirizzo("Via Raccolto 123");
        testFornitore.setNumeroTelefono("0123456789");
        testFornitore.setEmail("raccolto@fornitore.com");
        testFornitore.setPartitaIva("12345678901");
        fornitoreDAO.create(testFornitore);

        testPianta = new Pianta();
        testPianta.setTipo("Ortaggio");
        testPianta.setVarieta("Zucchina");
        testPianta.setCosto(new BigDecimal("8.50"));
        testPianta.setNote("Pianta per raccolto test");
        testPianta.setFornitoreId(testFornitore.getId());
        piantaDAO.create(testPianta);

        testZona = new Zona();
        testZona.setNome("Zona Raccolto Test");
        testZona.setDimensione(150.0);
        testZona.setTipoTerreno("Umido");
        zonaDAO.create(testZona);

        testPiantagione = new Piantagione();
        testPiantagione.setQuantitaPianta(100);
        testPiantagione.setMessaADimora(LocalDate.now().minusMonths(3));
        testPiantagione.setPiantaId(testPianta.getId());
        testPiantagione.setZonaId(testZona.getId());
        piantagioneDAO.create(testPiantagione);

        // Then create the Raccolto
        testRaccolto = new Raccolto();
        testRaccolto.setDataRaccolto(LocalDate.now());
        testRaccolto.setQuantitaKg(new BigDecimal("120.5"));
        testRaccolto.setNote("Raccolto di test");
        testRaccolto.setPiantagioneId(testPiantagione.getId());
        
        raccoltoDAO.create(testRaccolto);
    }

    @AfterAll
    void setTestModeOff() {
        DatabaseConnection.getInstance().setTestMode(false);
    }

    @AfterEach
    void cleanUp() throws SQLException {
        if (testRaccolto != null && testRaccolto.getId() != null && testRaccolto.getId() > 0) {
            raccoltoDAO.delete(testRaccolto.getId());
        }
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
    void testCreateRaccolto() {
        assertNotNull(testRaccolto.getId());
        assertTrue(testRaccolto.getId() > 0);
    }

    @Test
    void testReadRaccolto() throws SQLException {
        Raccolto retrieved = raccoltoDAO.read(testRaccolto.getId());

        assertNotNull(retrieved);
        assertNotNull(testRaccolto.getQuantitaKg());
        assertNotNull(retrieved.getQuantitaKg());
        assertEquals(testRaccolto.getDataRaccolto(), retrieved.getDataRaccolto());
        assertEquals(0, testRaccolto.getQuantitaKg().compareTo(retrieved.getQuantitaKg()));
        assertEquals(testRaccolto.getNote(), retrieved.getNote());
        assertEquals(testRaccolto.getPiantagioneId(), retrieved.getPiantagioneId());
    }

    @Test
    void testUpdateRaccolto() throws SQLException {
        testRaccolto.setDataRaccolto(LocalDate.now().minusDays(5));
        testRaccolto.setQuantitaKg(new BigDecimal("150.75"));
        testRaccolto.setNote("Raccolto aggiornato");

        raccoltoDAO.update(testRaccolto);

        Raccolto updated = raccoltoDAO.read(testRaccolto.getId());
        assertEquals(LocalDate.now().minusDays(5), updated.getDataRaccolto());
        assertEquals(new BigDecimal("150.75"), updated.getQuantitaKg());
        assertEquals("Raccolto aggiornato", updated.getNote());
    }

    @Test
    void testDeleteRaccolto() throws SQLException {
        int id = testRaccolto.getId();
        raccoltoDAO.delete(id);

        Raccolto deleted = raccoltoDAO.read(id);
        assertNull(deleted);
    }

    @Test
    void testFindAllRaccolti() throws SQLException {
        // Create a second Raccolto for testing
        Raccolto secondRaccolto = new Raccolto();
        secondRaccolto.setDataRaccolto(LocalDate.now().minusDays(10));
        secondRaccolto.setQuantitaKg(new BigDecimal("80.25"));
        secondRaccolto.setNote("Secondo raccolto di test");
        secondRaccolto.setPiantagioneId(testPiantagione.getId());
        
        raccoltoDAO.create(secondRaccolto);

        List<Raccolto> raccolti = raccoltoDAO.findAll();

        assertNotNull(raccolti);
        assertTrue(raccolti.size() >= 2);

        // Cleanup
        raccoltoDAO.delete(secondRaccolto.getId());
    }

    @Test
    void testReadNonExistentRaccolto() throws SQLException {
        Raccolto nonExistent = raccoltoDAO.read(-1);
        assertNull(nonExistent);
    }
}
