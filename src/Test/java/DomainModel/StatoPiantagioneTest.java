package DomainModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StatoPiantagione Test Suite")
public class StatoPiantagioneTest {
    private StatoPiantagione statoPiantagione;

    @BeforeEach
    void setUp() {
        statoPiantagione = new StatoPiantagione();
    }

    @Test
    @DisplayName("Test costruttore vuoto")
    void testCostruttoreVuoto() {
        assertNotNull(statoPiantagione);
        assertNull(statoPiantagione.getId());
        assertNull(statoPiantagione.getCodice());
        assertNull(statoPiantagione.getDescrizione());
        assertNull(statoPiantagione.getDataCreazione());
        assertNull(statoPiantagione.getDataAggiornamento());
    }

    @Test
    @DisplayName("Test costruttore con parametri completi")
    void testCostruttoreConParametri() {
        StatoPiantagione stato = new StatoPiantagione(1, "TEST_CODICE", "Descrizione Test");

        assertEquals(1, stato.getId());
        assertEquals("TEST_CODICE", stato.getCodice());
        assertEquals("Descrizione Test", stato.getDescrizione());
        assertNull(stato.getDataCreazione()); // Non impostate dal costruttore
        assertNull(stato.getDataAggiornamento());
    }

    @Test
    @DisplayName("Test getter e setter di base")
    void testGetterESetterBasici() {
        // Test ID
        statoPiantagione.setId(123);
        assertEquals(123, statoPiantagione.getId());

        // Test codice
        statoPiantagione.setCodice("STATO_TEST");
        assertEquals("STATO_TEST", statoPiantagione.getCodice());

        // Test descrizione
        statoPiantagione.setDescrizione("Descrizione di test");
        assertEquals("Descrizione di test", statoPiantagione.getDescrizione());
    }

    @Test
    @DisplayName("Test gestione date")
    void testGestioneDate() {
        LocalDateTime now = LocalDateTime.now();

        // Test data creazione
        statoPiantagione.setDataCreazione(now);
        assertEquals(now, statoPiantagione.getDataCreazione());

        // Test data aggiornamento
        LocalDateTime aggiornamento = now.plusHours(2);
        statoPiantagione.setDataAggiornamento(aggiornamento);
        assertEquals(aggiornamento, statoPiantagione.getDataAggiornamento());
    }

    @Test
    @DisplayName("Test costanti stati predefiniti")
    void testCostantiStati() {
        // Verifica che tutte le costanti siano definite correttamente
        assertEquals("ATTIVA", StatoPiantagione.ATTIVA);
        assertEquals("RIMOSSA", StatoPiantagione.RIMOSSA);
        assertEquals("IN_RACCOLTA", StatoPiantagione.IN_RACCOLTA);
        assertEquals("COMPLETATA", StatoPiantagione.COMPLETATA);
        assertEquals("SOSPESA", StatoPiantagione.SOSPESA);

        // Verifica che le costanti non siano null
        assertNotNull(StatoPiantagione.ATTIVA);
        assertNotNull(StatoPiantagione.RIMOSSA);
        assertNotNull(StatoPiantagione.IN_RACCOLTA);
        assertNotNull(StatoPiantagione.COMPLETATA);
        assertNotNull(StatoPiantagione.SOSPESA);
    }

    @Test
    @DisplayName("Test creazione stati con codici predefiniti")
    void testCreazioneStatiPredefiniti() {
        // Stato ATTIVA
        StatoPiantagione statoAttiva = new StatoPiantagione(1, StatoPiantagione.ATTIVA, "Piantagione attiva");
        assertEquals(1, statoAttiva.getId());
        assertEquals("ATTIVA", statoAttiva.getCodice());
        assertEquals("Piantagione attiva", statoAttiva.getDescrizione());

        // Stato RIMOSSA
        StatoPiantagione statoRimossa = new StatoPiantagione(2, StatoPiantagione.RIMOSSA, "Piantagione rimossa");
        assertEquals(2, statoRimossa.getId());
        assertEquals("RIMOSSA", statoRimossa.getCodice());
        assertEquals("Piantagione rimossa", statoRimossa.getDescrizione());

        // Stato IN_RACCOLTA
        StatoPiantagione statoRaccolta = new StatoPiantagione(3, StatoPiantagione.IN_RACCOLTA, "In raccolta");
        assertEquals(3, statoRaccolta.getId());
        assertEquals("IN_RACCOLTA", statoRaccolta.getCodice());
        assertEquals("In raccolta", statoRaccolta.getDescrizione());

        // Stato COMPLETATA
        StatoPiantagione statoCompletata = new StatoPiantagione(4, StatoPiantagione.COMPLETATA, "Completata");
        assertEquals(4, statoCompletata.getId());
        assertEquals("COMPLETATA", statoCompletata.getCodice());
        assertEquals("Completata", statoCompletata.getDescrizione());

        // Stato SOSPESA
        StatoPiantagione statoSospesa = new StatoPiantagione(5, StatoPiantagione.SOSPESA, "Sospesa");
        assertEquals(5, statoSospesa.getId());
        assertEquals("SOSPESA", statoSospesa.getCodice());
        assertEquals("Sospesa", statoSospesa.getDescrizione());
    }

    @Test
    @DisplayName("Test modifica valori dopo creazione")
    void testModificaValori() {
        StatoPiantagione stato = new StatoPiantagione(1, "INIZIALE", "Descrizione iniziale");

        // Modifica ID
        stato.setId(99);
        assertEquals(99, stato.getId());

        // Modifica codice
        stato.setCodice("MODIFICATO");
        assertEquals("MODIFICATO", stato.getCodice());

        // Modifica descrizione
        stato.setDescrizione("Nuova descrizione");
        assertEquals("Nuova descrizione", stato.getDescrizione());
    }

    @Test
    @DisplayName("Test gestione valori null")
    void testGestioneValoriNull() {
        StatoPiantagione stato = new StatoPiantagione(1, "TEST", "Descrizione");

        // Test impostazione valori null
        stato.setId(null);
        assertNull(stato.getId());

        stato.setCodice(null);
        assertNull(stato.getCodice());

        stato.setDescrizione(null);
        assertNull(stato.getDescrizione());

        stato.setDataCreazione(null);
        assertNull(stato.getDataCreazione());

        stato.setDataAggiornamento(null);
        assertNull(stato.getDataAggiornamento());
    }

    @Test
    @DisplayName("Test scenario completo di utilizzo")
    void testScenarioCompleto() {
        LocalDateTime now = LocalDateTime.now();

        // Creazione di uno stato piantagione completo
        StatoPiantagione stato = new StatoPiantagione(1, StatoPiantagione.ATTIVA, "Piantagione attiva");
        stato.setDataCreazione(now);
        stato.setDataAggiornamento(now);

        // Verifica tutti i valori
        assertEquals(1, stato.getId());
        assertEquals("ATTIVA", stato.getCodice());
        assertEquals("Piantagione attiva", stato.getDescrizione());
        assertEquals(now, stato.getDataCreazione());
        assertEquals(now, stato.getDataAggiornamento());

        // Simulazione di un aggiornamento
        LocalDateTime aggiornamento = now.plusDays(1);
        stato.setDataAggiornamento(aggiornamento);
        stato.setDescrizione("Piantagione attiva - aggiornata");

        assertEquals(aggiornamento, stato.getDataAggiornamento());
        assertEquals("Piantagione attiva - aggiornata", stato.getDescrizione());
        // La data di creazione non dovrebbe cambiare
        assertEquals(now, stato.getDataCreazione());
    }
}
