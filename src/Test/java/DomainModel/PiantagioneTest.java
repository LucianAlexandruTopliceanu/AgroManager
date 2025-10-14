package DomainModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Piantagione Test Suite")
public class PiantagioneTest {
    private Piantagione piantagione;
    private StatoPiantagione statoTest;

    @BeforeEach
    void setUp() {
        piantagione = new Piantagione();

        statoTest = new StatoPiantagione(1, StatoPiantagione.ATTIVA, "Piantagione attiva");
    }

    @Test
    @DisplayName("Test costruttore vuoto")
    void testCostruttoreVuoto() {
        assertNotNull(piantagione);
        assertNull(piantagione.getId());
        assertEquals(1, piantagione.getIdStatoPiantagione()); // Default: ATTIVA
    }

    @Test
    @DisplayName("Test costruttore completo")
    void testCostruttoreCompleto() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate oggi = LocalDate.now();

        Piantagione p = new Piantagione(1, 10, oggi, 1, 2, now, now);

        assertEquals(1, p.getId());
        assertEquals(10, p.getQuantitaPianta());
        assertEquals(oggi, p.getMessaADimora());
        assertEquals(1, p.getPiantaId());
        assertEquals(2, p.getZonaId());
        assertEquals(now, p.getDataCreazione());
        assertEquals(now, p.getDataAggiornamento());
        assertEquals(1, p.getIdStatoPiantagione()); // Default: ATTIVA
    }

    @Test
    @DisplayName("Test getter e setter di base")
    void testGetterESetterBasici() {
        // Test ID
        piantagione.setId(123);
        assertEquals(123, piantagione.getId());

        // Test quantità pianta
        piantagione.setQuantitaPianta(50);
        assertEquals(50, piantagione.getQuantitaPianta());

        // Test data messa a dimora
        LocalDate data = LocalDate.of(2024, 3, 15);
        piantagione.setMessaADimora(data);
        assertEquals(data, piantagione.getMessaADimora());

        // Test pianta ID
        piantagione.setPiantaId(10);
        assertEquals(10, piantagione.getPiantaId());

        // Test zona ID
        piantagione.setZonaId(5);
        assertEquals(5, piantagione.getZonaId());
    }

    @Test
    @DisplayName("Test date di creazione e aggiornamento")
    void testDateCreazione() {
        LocalDateTime now = LocalDateTime.now();

        piantagione.setDataCreazione(now);
        assertEquals(now, piantagione.getDataCreazione());

        piantagione.setDataAggiornamento(now.plusHours(1));
        assertEquals(now.plusHours(1), piantagione.getDataAggiornamento());
    }

    @Test
    @DisplayName("Test gestione stato piantagione - ID")
    void testGestioneStatoId() {
        // Test ID stato piantagione
        piantagione.setIdStatoPiantagione(2);
        assertEquals(2, piantagione.getIdStatoPiantagione());

        // Test default
        Piantagione nuovaPiantagione = new Piantagione();
        assertEquals(1, nuovaPiantagione.getIdStatoPiantagione());
    }

    @Test
    @DisplayName("Test gestione stato piantagione - Oggetto")
    void testGestioneStatoOggetto() {
        // Test con oggetto StatoPiantagione
        piantagione.setStatoPiantagione(statoTest);
        assertEquals(statoTest, piantagione.getStatoPiantagione());
        assertEquals(1, piantagione.getIdStatoPiantagione()); // Dovrebbe sincronizzare l'ID

        // Test con null
        piantagione.setStatoPiantagione(null);
        assertNull(piantagione.getStatoPiantagione());
        assertEquals(1, piantagione.getIdStatoPiantagione()); // L'ID non dovrebbe cambiare
    }

    @Test
    @DisplayName("Test stati piantagione predefiniti")
    void testStatiPredefiniti() {
        // Test costanti della classe StatoPiantagione
        assertEquals("ATTIVA", StatoPiantagione.ATTIVA);
        assertEquals("RIMOSSA", StatoPiantagione.RIMOSSA);
        assertEquals("IN_RACCOLTA", StatoPiantagione.IN_RACCOLTA);
        assertEquals("COMPLETATA", StatoPiantagione.COMPLETATA);
        assertEquals("SOSPESA", StatoPiantagione.SOSPESA);
    }

    @Test
    @DisplayName("Test creazione piantagione con tutti i parametri")
    void testCreazionePiantagioneCompleta() {
        LocalDate dataDimora = LocalDate.of(2024, 3, 1);
        LocalDateTime dataCreazione = LocalDateTime.of(2024, 3, 1, 10, 0);
        LocalDateTime dataAggiornamento = LocalDateTime.of(2024, 3, 15, 15, 30);

        Piantagione p = new Piantagione(100, 25, dataDimora, 2, 3, dataCreazione, dataAggiornamento);

        assertEquals(100, p.getId());
        assertEquals(25, p.getQuantitaPianta());
        assertEquals(dataDimora, p.getMessaADimora());
        assertEquals(2, p.getPiantaId());
        assertEquals(3, p.getZonaId());
        assertEquals(dataCreazione, p.getDataCreazione());
        assertEquals(dataAggiornamento, p.getDataAggiornamento());
        assertEquals(1, p.getIdStatoPiantagione()); // Default ATTIVA
    }

    @Test
    @DisplayName("Test modifica stato con oggetti StatoPiantagione")
    void testModificaStatoConOggetti() {
        // Stato attiva
        StatoPiantagione statoAttiva = new StatoPiantagione(1, StatoPiantagione.ATTIVA, "Attiva");
        piantagione.setStatoPiantagione(statoAttiva);
        assertEquals(statoAttiva, piantagione.getStatoPiantagione());

        // Stato in raccolta
        StatoPiantagione statoRaccolta = new StatoPiantagione(3, StatoPiantagione.IN_RACCOLTA, "In Raccolta");
        piantagione.setStatoPiantagione(statoRaccolta);
        assertEquals(statoRaccolta, piantagione.getStatoPiantagione());

        // Stato completata
        StatoPiantagione statoCompletata = new StatoPiantagione(4, StatoPiantagione.COMPLETATA, "Completata");
        piantagione.setStatoPiantagione(statoCompletata);
        assertEquals(statoCompletata, piantagione.getStatoPiantagione());
    }

    @Test
    @DisplayName("Test validazione dati piantagione")
    void testValidazioneDati() {
        // Test con valori null - dovrebbero essere gestiti senza errori
        piantagione.setId(null);
        assertNull(piantagione.getId());

        piantagione.setQuantitaPianta(null);
        assertNull(piantagione.getQuantitaPianta());

        piantagione.setMessaADimora(null);
        assertNull(piantagione.getMessaADimora());

        piantagione.setPiantaId(null);
        assertNull(piantagione.getPiantaId());

        piantagione.setZonaId(null);
        assertNull(piantagione.getZonaId());

        // L'ID stato piantagione dovrebbe rimanere quello di default
        assertEquals(1, piantagione.getIdStatoPiantagione());
    }
}
