package ORM;

import DomainModel.Piantagione;
import DomainModel.Pianta;
import DomainModel.Zona;
import DomainModel.Fornitore;
import DomainModel.StatoPiantagione;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per PiantagioneDAO con gestione migliorata delle duplicazioni
 * e logging uniforme seguendo le best practice
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("PiantagioneDAO Test Suite")
public class PiantagioneDAOTest {

    private static final Logger logger = Logger.getLogger(PiantagioneDAOTest.class.getName());
    private static final AtomicInteger testCounter = new AtomicInteger(0);

    private PiantagioneDAO piantagioneDAO;
    private PiantaDAO piantaDAO;
    private ZonaDAO zonaDAO;
    private FornitoreDAO fornitoreDAO;
    private StatoPiantagioneDAO statoPiantagioneDAO;

    // Test objects con ID univoci per evitare conflitti
    private Pianta testPianta;
    private Zona testZona;
    private Fornitore testFornitore;

    @BeforeAll
    void setUp() {
        logger.info("Inizializzazione test suite PiantagioneDAO");
        piantagioneDAO = new PiantagioneDAO();
        piantaDAO = new PiantaDAO();
        zonaDAO = new ZonaDAO();
        fornitoreDAO = new FornitoreDAO();
        statoPiantagioneDAO = new StatoPiantagioneDAO();
        DatabaseConnection.setTestMode(true);
        logger.info("Test mode attivato per PiantagioneDAO");
    }

    @BeforeEach
    void createTestObjects() throws SQLException {
        int testId = testCounter.incrementAndGet();
        logger.info("Creazione oggetti test #" + testId);

        // Creo oggetti con identificatori univoci per evitare conflitti
        testFornitore = new Fornitore();
        testFornitore.setNome("Fornitore Test " + testId);
        testFornitore.setIndirizzo("Via Test " + testId);
        testFornitore.setNumeroTelefono("012345678" + (testId % 10));
        testFornitore.setEmail("test" + testId + "@fornitore.com");
        testFornitore.setPartitaIva("1234567890" + testId);
        fornitoreDAO.create(testFornitore);
        logger.info("Fornitore creato con ID: " + testFornitore.getId());

        testPianta = new Pianta();
        testPianta.setTipo("Albero Test " + testId);
        testPianta.setVarieta("Varietà " + testId);
        testPianta.setCosto(new java.math.BigDecimal("30.00"));
        testPianta.setNote("Pianta test #" + testId);
        testPianta.setFornitoreId(testFornitore.getId());
        piantaDAO.create(testPianta);
        logger.info("Pianta creata con ID: " + testPianta.getId());

        testZona = new Zona();
        testZona.setNome("Zona Test " + testId);
        testZona.setDimensione(200.0 + testId);
        testZona.setTipoTerreno("Fertile Test " + testId);
        zonaDAO.create(testZona);
        logger.info("Zona creata con ID: " + testZona.getId());
    }

    @AfterEach
    void cleanUp() {
        logger.info("Pulizia oggetti test");

        // Elimino tutte le piantagioni create in questo test
        try {
            List<Piantagione> piantagioni = piantagioneDAO.findAll();
            for (Piantagione p : piantagioni) {
                if (p.getPiantaId().equals(testPianta.getId()) ||
                    p.getZonaId().equals(testZona.getId())) {
                    piantagioneDAO.delete(p.getId());
                    logger.info("Piantagione eliminata: " + p.getId());
                }
            }
        } catch (Exception e) {
            logger.warning("Errore durante eliminazione piantagioni: " + e.getMessage());
        }

        // Elimino gli oggetti di supporto
        if (testPianta != null && testPianta.getId() != null) {
            try {
                piantaDAO.delete(testPianta.getId());
                logger.info("Pianta eliminata: " + testPianta.getId());
            } catch (Exception e) {
                logger.warning("Errore eliminazione pianta: " + e.getMessage());
            }
        }

        if (testZona != null && testZona.getId() != null) {
            try {
                zonaDAO.delete(testZona.getId());
                logger.info("Zona eliminata: " + testZona.getId());
            } catch (Exception e) {
                logger.warning("Errore eliminazione zona: " + e.getMessage());
            }
        }

        if (testFornitore != null && testFornitore.getId() != null) {
            try {
                fornitoreDAO.delete(testFornitore.getId());
                logger.info("Fornitore eliminato: " + testFornitore.getId());
            } catch (Exception e) {
                logger.warning("Errore eliminazione fornitore: " + e.getMessage());
            }
        }
    }

    @AfterAll
    void tearDown() {
        logger.info("Spegnimento test mode per PiantagioneDAO");
        DatabaseConnection.setTestMode(false);
    }

    /**
     * Crea una piantagione di test con data univoca per evitare duplicazioni
     */
    private Piantagione createUniquePiantagione() {
        Piantagione piantagione = new Piantagione();
        piantagione.setQuantitaPianta(5);
        // Uso data univoca basata su System.nanoTime per evitare duplicazioni
        piantagione.setMessaADimora(LocalDate.now().minusDays(System.nanoTime() % 365));
        piantagione.setPiantaId(testPianta.getId());
        piantagione.setZonaId(testZona.getId());
        piantagione.setIdStatoPiantagione(1); // ATTIVA
        return piantagione;
    }

    @Test
    @DisplayName("Test creazione piantagione")
    void testCreatePiantagione() throws SQLException {
        logger.info("Inizio test: creazione piantagione");

        // Arrange
        Piantagione testPiantagione = createUniquePiantagione();

        // Act
        piantagioneDAO.create(testPiantagione);

        // Assert
        assertNotNull(testPiantagione.getId(), "L'ID della piantagione deve essere assegnato");
        assertTrue(testPiantagione.getId() > 0, "L'ID deve essere positivo");

        logger.info("Test creazione piantagione completato - ID: " + testPiantagione.getId());
    }

    @Test
    @DisplayName("Test lettura piantagione")
    void testReadPiantagione() throws SQLException {
        logger.info("Inizio test: lettura piantagione");

        // Arrange
        Piantagione testPiantagione = createUniquePiantagione();
        piantagioneDAO.create(testPiantagione);

        // Act
        Piantagione piantagioneLetta = piantagioneDAO.read(testPiantagione.getId());

        // Assert
        assertNotNull(piantagioneLetta, "La piantagione letta non deve essere null");
        assertEquals(testPiantagione.getId(), piantagioneLetta.getId(), "Gli ID devono corrispondere");
        assertEquals(testPiantagione.getQuantitaPianta(), piantagioneLetta.getQuantitaPianta(),
                    "La quantità deve corrispondere");

        logger.info("Test lettura piantagione completato con successo");
    }

    @Test
    @DisplayName("Test aggiornamento piantagione")
    void testUpdatePiantagione() throws SQLException {
        logger.info("Inizio test: aggiornamento piantagione");

        // Arrange
        Piantagione testPiantagione = createUniquePiantagione();
        piantagioneDAO.create(testPiantagione);
        int nuovaQuantita = 10;

        // Act
        testPiantagione.setQuantitaPianta(nuovaQuantita);
        piantagioneDAO.update(testPiantagione);

        // Assert
        Piantagione piantagioneAggiornata = piantagioneDAO.read(testPiantagione.getId());
        assertEquals(nuovaQuantita, piantagioneAggiornata.getQuantitaPianta(),
                    "La quantità deve essere aggiornata");

        logger.info("Test aggiornamento piantagione completato con successo");
    }

    @Test
    @DisplayName("Test eliminazione piantagione")
    void testDeletePiantagione() throws SQLException {
        logger.info("Inizio test: eliminazione piantagione");

        // Arrange
        Piantagione testPiantagione = createUniquePiantagione();
        piantagioneDAO.create(testPiantagione);
        Integer idPiantagione = testPiantagione.getId();

        // Act
        piantagioneDAO.delete(idPiantagione);

        // Assert
        Piantagione piantagioneEliminata = piantagioneDAO.read(idPiantagione);
        assertNull(piantagioneEliminata, "La piantagione eliminata non deve più esistere");

        logger.info("Test eliminazione piantagione completato con successo");
    }

    @Test
    @DisplayName("Test recupero tutte le piantagioni")
    void testFindAll() throws SQLException {
        logger.info("Inizio test: recupero tutte le piantagioni");

        // Arrange
        Piantagione testPiantagione = createUniquePiantagione();
        piantagioneDAO.create(testPiantagione);

        // Act
        List<Piantagione> piantagioni = piantagioneDAO.findAll();

        // Assert
        assertNotNull(piantagioni, "La lista non deve essere null");
        assertFalse(piantagioni.isEmpty(), "Deve esserci almeno una piantagione");

        boolean trovata = piantagioni.stream()
            .anyMatch(p -> p.getId().equals(testPiantagione.getId()));
        assertTrue(trovata, "La piantagione di test deve essere presente");

        logger.info("Test findAll completato - trovate " + piantagioni.size() + " piantagioni");
    }

    @Test
    @DisplayName("Test recupero piantagioni con stato")
    void testFindAllWithStato() throws SQLException {
        logger.info("Inizio test: recupero piantagioni con stato");

        // Arrange
        Piantagione testPiantagione = createUniquePiantagione();
        piantagioneDAO.create(testPiantagione);

        // Act
        List<Piantagione> piantagioniConStato = piantagioneDAO.findAllWithStato();

        // Assert
        assertNotNull(piantagioniConStato, "La lista non deve essere null");

        boolean trovatoConStato = piantagioniConStato.stream()
            .anyMatch(p -> p.getStatoPiantagione() != null);
        assertTrue(trovatoConStato, "Almeno una piantagione deve avere lo stato popolato");

        logger.info("Test findAllWithStato completato con successo");
    }

    @Test
    @DisplayName("Test ricerca per stato")
    void testFindByStato() throws SQLException {
        logger.info("Inizio test: ricerca per stato");

        // Arrange
        Piantagione testPiantagione = createUniquePiantagione();
        piantagioneDAO.create(testPiantagione);

        // Act
        List<Piantagione> piantagioniAttive = piantagioneDAO.findByStato(StatoPiantagione.ATTIVA);

        // Assert
        assertNotNull(piantagioniAttive, "La lista non deve essere null");

        for (Piantagione p : piantagioniAttive) {
            if (p.getStatoPiantagione() != null) {
                assertEquals(StatoPiantagione.ATTIVA, p.getStatoPiantagione().getCodice(),
                           "Tutte le piantagioni devono essere attive");
            }
        }

        logger.info("Test findByStato completato - trovate " + piantagioniAttive.size() + " piantagioni attive");
    }

    @Test
    @DisplayName("Test ricerca piantagioni attive")
    void testFindAttive() throws SQLException {
        logger.info("Inizio test: ricerca piantagioni attive");

        // Arrange
        Piantagione testPiantagione = createUniquePiantagione();
        piantagioneDAO.create(testPiantagione);

        // Act
        List<Piantagione> piantagioniAttive = piantagioneDAO.findAttive();

        // Assert
        assertNotNull(piantagioniAttive, "La lista non deve essere null");

        boolean trovata = piantagioniAttive.stream()
            .anyMatch(p -> p.getId().equals(testPiantagione.getId()));
        assertTrue(trovata, "La piantagione di test deve essere nella lista delle attive");

        logger.info("Test findAttive completato - trovate " + piantagioniAttive.size() + " piantagioni attive");
    }

    @Test
    @DisplayName("Test cambio stato piantagione")
    void testCambiaStato() throws SQLException {
        logger.info("Inizio test: cambio stato piantagione");

        // Arrange
        Piantagione testPiantagione = createUniquePiantagione();
        piantagioneDAO.create(testPiantagione);

        // Act
        piantagioneDAO.cambiaStato(testPiantagione.getId(), 2); // SOSPESA

        // Assert
        Piantagione piantagioneAggiornata = piantagioneDAO.read(testPiantagione.getId());
        assertEquals(2, piantagioneAggiornata.getIdStatoPiantagione(),
                    "Lo stato deve essere cambiato a SOSPESA");

        logger.info("Test cambiaStato completato con successo");
    }

    @Test
    @DisplayName("Test mapping ResultSet con stato")
    void testMapResultSetWithStato() throws SQLException {
        logger.info("Inizio test: mapping ResultSet con stato");

        // Arrange
        Piantagione testPiantagione = createUniquePiantagione();
        piantagioneDAO.create(testPiantagione);

        // Act
        List<Piantagione> piantagioni = piantagioneDAO.findAllWithStato();

        // Assert
        assertNotNull(piantagioni, "La lista non deve essere null");

        Piantagione piantagioneTrovata = piantagioni.stream()
            .filter(p -> p.getId().equals(testPiantagione.getId()))
            .findFirst()
            .orElse(null);

        assertNotNull(piantagioneTrovata, "La piantagione deve essere trovata");
        assertNotNull(piantagioneTrovata.getStatoPiantagione(),
                     "Lo stato piantagione deve essere mappato correttamente");

        logger.info("Test mapResultSetWithStato completato con successo");
    }

    @Test
    @DisplayName("Test creazione con stato default")
    void testCreazioneConStatoDefault() throws SQLException {
        logger.info("Inizio test: creazione con stato default");

        // Arrange
        Piantagione nuovaPiantagione = createUniquePiantagione();
        // Non impostiamo esplicitamente lo stato

        // Act
        piantagioneDAO.create(nuovaPiantagione);

        // Assert
        assertNotNull(nuovaPiantagione.getId(), "L'ID deve essere assegnato");

        Piantagione piantagioneLetta = piantagioneDAO.read(nuovaPiantagione.getId());
        assertEquals(1, piantagioneLetta.getIdStatoPiantagione(),
                    "Lo stato default deve essere 1 (ATTIVA)");

        logger.info("Test creazione con stato default completato con successo");
    }
}
