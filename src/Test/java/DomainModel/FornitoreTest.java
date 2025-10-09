package DomainModel;

import BusinessLogic.Service.TestLogger;
import org.junit.jupiter.api.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per Fornitore - modalità sola lettura
 * Test uniformi con logging pulito e strutturato
 */
@DisplayName("Fornitore Test Suite")
public class FornitoreTest {

    private static final TestLogger testLogger = new TestLogger(FornitoreTest.class);
    private Fornitore fornitore;
    private LocalDateTime dataTest;

    @BeforeAll
    static void setupSuite() {
        testLogger.startTestSuite("Fornitore");
    }

    @AfterAll
    static void tearDownSuite() {
        testLogger.endTestSuite("Fornitore", 8, 8, 0);
    }

    @BeforeEach
    void setUp() {
        dataTest = LocalDateTime.now();
        fornitore = new Fornitore();
    }

    @Test
    @DisplayName("Test costruttore vuoto")
    void testCostruttoreVuoto() {
        testLogger.startTest("costruttore vuoto");

        assertNotNull(fornitore);
        assertNull(fornitore.getId());
        assertNull(fornitore.getNome());
        assertNull(fornitore.getIndirizzo());
        assertNull(fornitore.getNumeroTelefono());
        assertNull(fornitore.getEmail());
        assertNull(fornitore.getPartitaIva());
        assertNull(fornitore.getDataCreazione());
        assertNull(fornitore.getDataAggiornamento());

        testLogger.operation("Fornitore creato", "vuoto");
        testLogger.testPassed("costruttore vuoto");
    }

    @Test
    @DisplayName("Test setter e getter ID")
    void testSetterGetterID() {
        testLogger.startTest("setter e getter ID");

        Integer testId = 123;
        fornitore.setId(testId);
        assertEquals(testId, fornitore.getId());

        testLogger.operation("ID impostato", testId);
        testLogger.testPassed("setter e getter ID");
    }

    @Test
    @DisplayName("Test setter e getter nome")
    void testSetterGetterNome() {
        testLogger.startTest("setter e getter nome");

        String testNome = "Fornitore Test SRL";
        fornitore.setNome(testNome);
        assertEquals(testNome, fornitore.getNome());

        testLogger.operation("Nome impostato", testNome);
        testLogger.testPassed("setter e getter nome");
    }

    @Test
    @DisplayName("Test setter e getter indirizzo")
    void testSetterGetterIndirizzo() {
        testLogger.startTest("setter e getter indirizzo");

        String testIndirizzo = "Via Test 123, Milano";
        fornitore.setIndirizzo(testIndirizzo);
        assertEquals(testIndirizzo, fornitore.getIndirizzo());

        testLogger.operation("Indirizzo impostato", testIndirizzo);
        testLogger.testPassed("setter e getter indirizzo");
    }

    @Test
    @DisplayName("Test setter e getter email")
    void testSetterGetterEmail() {
        testLogger.startTest("setter e getter email");

        String testEmail = "test@fornitore.it";
        fornitore.setEmail(testEmail);
        assertEquals(testEmail, fornitore.getEmail());

        testLogger.operation("Email impostata", testEmail);
        testLogger.testPassed("setter e getter email");
    }

    @Test
    @DisplayName("Test setter e getter partita IVA")
    void testSetterGetterPartitaIva() {
        testLogger.startTest("setter e getter partita IVA");

        String testPartitaIva = "12345678901";
        fornitore.setPartitaIva(testPartitaIva);
        assertEquals(testPartitaIva, fornitore.getPartitaIva());

        testLogger.operation("Partita IVA impostata", testPartitaIva);
        testLogger.testPassed("setter e getter partita IVA");
    }

    @Test
    @DisplayName("Test setter e getter data creazione")
    void testSetterGetterDataCreazione() {
        testLogger.startTest("setter e getter data creazione");

        fornitore.setDataCreazione(dataTest);
        assertEquals(dataTest, fornitore.getDataCreazione());

        testLogger.operation("Data creazione impostata", dataTest.toString());
        testLogger.testPassed("setter e getter data creazione");
    }

    @Test
    @DisplayName("Test validazione fornitore completo")
    void testValidazioneFornitoreCompleto() {
        testLogger.startTest("validazione fornitore completo");

        // Configura un fornitore completo
        fornitore.setId(1);
        fornitore.setNome("Fornitore Completo SRL");
        fornitore.setIndirizzo("Via Completa 456");
        fornitore.setNumeroTelefono("02-87654321");
        fornitore.setEmail("completo@fornitore.it");
        fornitore.setPartitaIva("98765432109");
        fornitore.setDataCreazione(dataTest);
        fornitore.setDataAggiornamento(dataTest);

        // Verifica tutti i campi
        assertNotNull(fornitore.getId());
        assertNotNull(fornitore.getNome());
        assertNotNull(fornitore.getIndirizzo());
        assertNotNull(fornitore.getNumeroTelefono());
        assertNotNull(fornitore.getEmail());
        assertNotNull(fornitore.getPartitaIva());
        assertNotNull(fornitore.getDataCreazione());
        assertNotNull(fornitore.getDataAggiornamento());

        testLogger.operation("Fornitore completo validato", fornitore.getNome());
        testLogger.testPassed("validazione fornitore completo");
    }
}
