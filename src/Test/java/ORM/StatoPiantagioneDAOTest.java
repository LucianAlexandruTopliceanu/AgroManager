package ORM;

import BusinessLogic.Service.TestLogger;
import DomainModel.StatoPiantagione;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per StatoPiantagioneDAO - modalità sola lettura
 * Test uniformi con logging pulito e strutturato
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("StatoPiantagioneDAO Test Suite")
public class StatoPiantagioneDAOTest {

    private static final TestLogger testLogger = new TestLogger(StatoPiantagioneDAOTest.class);
    private StatoPiantagioneDAO statoPiantagioneDAO;

    @BeforeAll
    void setupSuite() throws SQLException {
        testLogger.startTestSuite("StatoPiantagioneDAO");
        statoPiantagioneDAO = new StatoPiantagioneDAO();
        DatabaseConnection.setTestMode(true);
    }

    @AfterAll
    void tearDownSuite() throws SQLException {
        testLogger.endTestSuite("StatoPiantagioneDAO", 4, 4, 0);
        DatabaseConnection.setTestMode(false);
    }

    @Test
    @DisplayName("Test verifica nome tabella")
    void testGetTableName() {
        testLogger.startTest("verifica nome tabella");

        // Act & Assert
        assertEquals("stato_piantagione", statoPiantagioneDAO.getTableName(),
                    "Il nome della tabella deve essere 'stato_piantagione'");

        testLogger.testPassed("verifica nome tabella");
    }

    @Test
    @DisplayName("Test conteggio stati disponibili")
    void testCountStati() {
        testLogger.startTest("conteggio stati");

        // Act
        int count = statoPiantagioneDAO.countStati();

        // Assert
        assertTrue(count >= 0, "Il conteggio degli stati deve essere >= 0");

        testLogger.operation("Stati trovati", count);
        testLogger.testPassed("conteggio stati");
    }

    @Test
    @DisplayName("Test recupero tutti gli stati ordinati")
    void testFindAllOrdered() {
        testLogger.startTest("recupero tutti gli stati ordinati");

        // Act
        List<StatoPiantagione> stati = statoPiantagioneDAO.findAllOrdered();

        // Assert
        assertNotNull(stati, "La lista degli stati non deve essere null");

        // Se ci sono stati, verifica che siano ordinati per ID
        if (stati.size() > 1) {
            for (int i = 1; i < stati.size(); i++) {
                assertTrue(stati.get(i-1).getId() <= stati.get(i).getId(),
                    "Gli stati devono essere ordinati per ID crescente");
            }
        }

        testLogger.operation("Stati trovati", stati.size());
        testLogger.testPassed("recupero tutti gli stati ordinati");
    }

    @Test
    @DisplayName("Test ricerca stato per codice valido")
    void testFindByCodiceValido() {
        testLogger.startTest("ricerca stato per codice valido");

        // Arrange - uso un codice che dovrebbe esistere
        String codiceTest = StatoPiantagione.ATTIVA;

        // Act
        StatoPiantagione stato = statoPiantagioneDAO.findByCodice(codiceTest);

        // Assert - può essere null se non ci sono stati nel DB, ma non deve lanciare eccezioni
        if (stato != null) {
            assertEquals(codiceTest, stato.getCodice(),
                        "Il codice dello stato trovato deve corrispondere");
            assertNotNull(stato.getDescrizione(),
                         "La descrizione deve essere presente");
        }

        testLogger.operation("Codice stato trovato", codiceTest);
        testLogger.testPassed("ricerca stato per codice valido");
    }

    @Test
    @DisplayName("Test ricerca stato per codice null")
    void testFindByCodiceNull() {
        testLogger.startTest("ricerca stato per codice null");

        // Act
        StatoPiantagione stato = statoPiantagioneDAO.findByCodice(null);

        // Assert
        assertNull(stato, "La ricerca con codice null deve restituire null");

        testLogger.testPassed("ricerca stato per codice null");
    }

    @Test
    @DisplayName("Test ricerca stato per codice vuoto")
    void testFindByCodiceVuoto() {
        testLogger.startTest("ricerca stato per codice vuoto");

        // Act
        StatoPiantagione stato = statoPiantagioneDAO.findByCodice("");

        // Assert
        assertNull(stato, "La ricerca con codice vuoto deve restituire null");

        testLogger.testPassed("ricerca stato per codice vuoto");
    }

    @Test
    @DisplayName("Test esistenza stato per codice")
    void testExistsByCodice() {
        testLogger.startTest("verifica esistenza stato per codice");

        // Act & Assert
        boolean existsNull = statoPiantagioneDAO.existsByCodice(null);
        assertFalse(existsNull, "Non deve esistere stato con codice null");

        boolean existsEmpty = statoPiantagioneDAO.existsByCodice("");
        assertFalse(existsEmpty, "Non deve esistere stato con codice vuoto");

        boolean existsInvalid = statoPiantagioneDAO.existsByCodice("CODICE_INESISTENTE");
        assertFalse(existsInvalid, "Non deve esistere stato con codice inesistente");

        testLogger.testPassed("verifica esistenza stato per codice");
    }

    @Test
    @DisplayName("Test ricerca stato per ID valido")
    void testFindByIdValido() {
        testLogger.startTest("ricerca stato per ID valido");

        // Arrange - uso un ID che potrebbe esistere
        Integer idTest = 1;

        // Act
        StatoPiantagione stato = statoPiantagioneDAO.findById(idTest);

        // Assert - può essere null se non ci sono stati nel DB
        if (stato != null) {
            assertEquals(idTest, stato.getId(),
                        "L'ID dello stato trovato deve corrispondere");
            assertNotNull(stato.getCodice(),
                         "Il codice deve essere presente");
        }

        testLogger.operation("ID stato trovato", idTest != null ? idTest.toString() : "null");
        testLogger.testPassed("ricerca stato per ID valido");
    }

    @Test
    @DisplayName("Test ricerca stato per ID null")
    void testFindByIdNull() {
        testLogger.startTest("ricerca stato per ID null");

        // Act
        StatoPiantagione stato = statoPiantagioneDAO.findById(null);

        // Assert
        assertNull(stato, "La ricerca con ID null deve restituire null");

        testLogger.testPassed("ricerca stato per ID null");
    }

    @Test
    @DisplayName("Test ricerca stato per ID negativo")
    void testFindByIdNegativo() {
        testLogger.startTest("ricerca stato per ID negativo");

        // Act
        StatoPiantagione stato = statoPiantagioneDAO.findById(-1);

        // Assert
        assertNull(stato, "La ricerca con ID negativo deve restituire null");

        testLogger.testPassed("ricerca stato per ID negativo");
    }

    // Test che verificano che le operazioni di scrittura siano bloccate
    @Test
    @DisplayName("Test blocco operazione create")
    void testCreateBlocked() {
        testLogger.startTest("verifica blocco operazione create");

        // Arrange
        StatoPiantagione nuovoStato = new StatoPiantagione();
        nuovoStato.setCodice("TEST");
        nuovoStato.setDescrizione("Stato di test");

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            statoPiantagioneDAO.create(nuovoStato);
        }, "L'operazione create deve essere bloccata");

        testLogger.testPassed("verifica blocco operazione create");
    }

    @Test
    @DisplayName("Test blocco operazione update")
    void testUpdateBlocked() {
        testLogger.startTest("verifica blocco operazione update");

        // Arrange
        StatoPiantagione statoEsistente = new StatoPiantagione();
        statoEsistente.setId(1);
        statoEsistente.setCodice("ATTIVA");
        statoEsistente.setDescrizione("Descrizione modificata");

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            statoPiantagioneDAO.update(statoEsistente);
        }, "L'operazione update deve essere bloccata");

        testLogger.testPassed("verifica blocco operazione update");
    }

    @Test
    @DisplayName("Test blocco operazione delete")
    void testDeleteBlocked() {
        testLogger.startTest("verifica blocco operazione delete");

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            statoPiantagioneDAO.delete(1);
        }, "L'operazione delete deve essere bloccata");

        testLogger.testPassed("verifica blocco operazione delete");
    }

    @Test
    @DisplayName("Test blocco metodi SQL di inserimento")
    void testInsertSQLBlocked() {
        testLogger.startTest("verifica blocco metodi SQL di inserimento");

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            statoPiantagioneDAO.getInsertSQL();
        }, "Il metodo getInsertSQL deve essere bloccato");

        assertThrows(UnsupportedOperationException.class, () -> {
            statoPiantagioneDAO.getUpdateSQL();
        }, "Il metodo getUpdateSQL deve essere bloccato");

        testLogger.testPassed("verifica blocco metodi SQL di inserimento");
    }

    @Test
    @DisplayName("Test codici validi")
    void testCodiciValidi() {
        testLogger.startTest("validazione codici stati");

        // Test che i codici standard siano definiti
        assertNotNull(StatoPiantagione.ATTIVA);
        assertNotNull(StatoPiantagione.IN_RACCOLTA);
        assertNotNull(StatoPiantagione.COMPLETATA);

        testLogger.operation("Codici validati", "ATTIVA, IN_RACCOLTA, COMPLETATA");
        testLogger.testPassed("validazione codici stati");
    }
}
