package DomainModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PiantagioneTest {
    private Piantagione piantagione;
    private StatoPiantagione statoTest;

    @BeforeEach
    void setUp() {
        piantagione = new Piantagione();

        statoTest = new StatoPiantagione();
        statoTest.setId(1);
        statoTest.setCodice(StatoPiantagione.ATTIVA);
        statoTest.setDescrizione("Piantagione attiva");
    }

    @Test
    void testCostruttoreVuoto() {
        assertNotNull(piantagione);
        assertNull(piantagione.getId());
        assertEquals(1, piantagione.getIdStatoPiantagione()); // Default: ATTIVA
    }

    @Test
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
    void testGetterESetterBasici() {
        LocalDate data = LocalDate.now().minusDays(10);
        LocalDateTime timestamp = LocalDateTime.now();

        piantagione.setId(123);
        piantagione.setQuantitaPianta(15);
        piantagione.setMessaADimora(data);
        piantagione.setPiantaId(5);
        piantagione.setZonaId(3);
        piantagione.setDataCreazione(timestamp);
        piantagione.setDataAggiornamento(timestamp);

        assertEquals(123, piantagione.getId());
        assertEquals(15, piantagione.getQuantitaPianta());
        assertEquals(data, piantagione.getMessaADimora());
        assertEquals(5, piantagione.getPiantaId());
        assertEquals(3, piantagione.getZonaId());
        assertEquals(timestamp, piantagione.getDataCreazione());
        assertEquals(timestamp, piantagione.getDataAggiornamento());
    }

    // Test sistema di stati
    @Test
    void testGetterESetterStato() {
        piantagione.setIdStatoPiantagione(2);
        assertEquals(2, piantagione.getIdStatoPiantagione());

        piantagione.setStatoPiantagione(statoTest);
        assertEquals(statoTest, piantagione.getStatoPiantagione());
        assertEquals(1, piantagione.getIdStatoPiantagione()); // Dovrebbe sincronizzarsi
    }

    @Test
    void testSincronizzazioneStatoEId() {
        // Quando impostiamo lo stato, l'ID dovrebbe sincronizzarsi
        piantagione.setStatoPiantagione(statoTest);
        assertEquals(statoTest.getId(), piantagione.getIdStatoPiantagione());

        // Test con stato null
        piantagione.setStatoPiantagione(null);
        assertNull(piantagione.getStatoPiantagione());
        // L'ID non dovrebbe cambiare quando impostiamo null
    }

    @Test
    void testIsAttiva() {
        // Con oggetto stato
        piantagione.setStatoPiantagione(statoTest);
        assertTrue(piantagione.isAttiva());

        // Con solo ID stato (fallback)
        piantagione.setStatoPiantagione(null);
        piantagione.setIdStatoPiantagione(1);
        assertTrue(piantagione.isAttiva());

        // Con stato diverso
        piantagione.setIdStatoPiantagione(2);
        assertFalse(piantagione.isAttiva());
    }

    @Test
    void testIsRimossa() {
        // Con oggetto stato RIMOSSA
        StatoPiantagione statoRimossa = new StatoPiantagione();
        statoRimossa.setId(2);
        statoRimossa.setCodice(StatoPiantagione.RIMOSSA);

        piantagione.setStatoPiantagione(statoRimossa);
        assertTrue(piantagione.isRimossa());

        // Con solo ID stato (fallback)
        piantagione.setStatoPiantagione(null);
        piantagione.setIdStatoPiantagione(2);
        assertTrue(piantagione.isRimossa());

        // Con stato diverso
        piantagione.setIdStatoPiantagione(1);
        assertFalse(piantagione.isRimossa());
    }

    @Test
    void testIsCompletata() {
        // Con oggetto stato COMPLETATA
        StatoPiantagione statoCompletata = new StatoPiantagione();
        statoCompletata.setId(4);
        statoCompletata.setCodice(StatoPiantagione.COMPLETATA);

        piantagione.setStatoPiantagione(statoCompletata);
        assertTrue(piantagione.isCompletata());

        // Con solo ID stato (fallback)
        piantagione.setStatoPiantagione(null);
        piantagione.setIdStatoPiantagione(4);
        assertTrue(piantagione.isCompletata());

        // Con stato diverso
        piantagione.setIdStatoPiantagione(1);
        assertFalse(piantagione.isCompletata());
    }

    @Test
    void testCambiaStato() {
        LocalDateTime prima = LocalDateTime.now().minusMinutes(1);
        piantagione.setDataAggiornamento(prima);

        StatoPiantagione nuovoStato = new StatoPiantagione();
        nuovoStato.setId(3);
        nuovoStato.setCodice(StatoPiantagione.IN_RACCOLTA);

        piantagione.cambiaStato(nuovoStato);

        assertEquals(nuovoStato, piantagione.getStatoPiantagione());
        assertEquals(nuovoStato.getId(), piantagione.getIdStatoPiantagione());
        assertTrue(piantagione.getDataAggiornamento().isAfter(prima));
    }

    @Test
    void testRimuovi() {
        LocalDateTime prima = LocalDateTime.now().minusMinutes(1);
        piantagione.setDataAggiornamento(prima);

        piantagione.rimuovi();

        assertEquals(2, piantagione.getIdStatoPiantagione()); // ID stato RIMOSSA
        assertTrue(piantagione.getDataAggiornamento().isAfter(prima));
    }

    @Test
    void testCompleta() {
        LocalDateTime prima = LocalDateTime.now().minusMinutes(1);
        piantagione.setDataAggiornamento(prima);

        piantagione.completa();

        assertEquals(4, piantagione.getIdStatoPiantagione()); // ID stato COMPLETATA
        assertTrue(piantagione.getDataAggiornamento().isAfter(prima));
    }

    @Test
    void testRiattiva() {
        // Prima rimuovi
        piantagione.rimuovi();
        assertEquals(2, piantagione.getIdStatoPiantagione());

        LocalDateTime prima = LocalDateTime.now().minusMinutes(1);
        piantagione.setDataAggiornamento(prima);

        // Poi riattiva
        piantagione.riattiva();

        assertEquals(1, piantagione.getIdStatoPiantagione()); // ID stato ATTIVA
        assertTrue(piantagione.getDataAggiornamento().isAfter(prima));
    }

    @Test
    void testGetDurataGiorni() {
        // Test con data di messa a dimora
        LocalDate trentaGiorniFa = LocalDate.now().minusDays(30);
        piantagione.setMessaADimora(trentaGiorniFa);

        long durata = piantagione.getDurataGiorni();
        assertEquals(30, durata);

        // Test con data null
        piantagione.setMessaADimora(null);
        assertEquals(0, piantagione.getDurataGiorni());
    }

    @Test
    void testGetDescrizioneStato() {
        // Con oggetto stato
        piantagione.setStatoPiantagione(statoTest);
        assertEquals("Piantagione attiva", piantagione.getDescrizioneStato());

        // Con solo ID stato (fallback)
        piantagione.setStatoPiantagione(null);

        piantagione.setIdStatoPiantagione(1);
        assertEquals("Attiva", piantagione.getDescrizioneStato());

        piantagione.setIdStatoPiantagione(2);
        assertEquals("Rimossa", piantagione.getDescrizioneStato());

        piantagione.setIdStatoPiantagione(3);
        assertEquals("In Raccolta", piantagione.getDescrizioneStato());

        piantagione.setIdStatoPiantagione(4);
        assertEquals("Completata", piantagione.getDescrizioneStato());

        piantagione.setIdStatoPiantagione(5);
        assertEquals("Sospesa", piantagione.getDescrizioneStato());

        piantagione.setIdStatoPiantagione(999);
        assertEquals("Sconosciuto", piantagione.getDescrizioneStato());

        // Con ID null
        piantagione.setIdStatoPiantagione(null);
        assertEquals("Attiva", piantagione.getDescrizioneStato()); // Default
    }

    @Test
    void testStatoNullSafeOperations() {
        // Test che i metodi funzionino anche con stato null
        piantagione.setStatoPiantagione(null);
        piantagione.setIdStatoPiantagione(null);

        assertFalse(piantagione.isAttiva());
        assertFalse(piantagione.isRimossa());
        assertFalse(piantagione.isCompletata());
        assertEquals("Attiva", piantagione.getDescrizioneStato()); // Default
    }

    @Test
    @DisplayName("Test flusso completo ciclo di vita piantagione")
    void testFlussoCompletoCicloVita() {
        // Arrange
        Piantagione piantagione = new Piantagione();
        piantagione.setMessaADimora(LocalDate.now().minusDays(90));
        piantagione.setQuantitaPianta(20);

        // Inizialmente dovrebbe essere in stato ATTIVA (default)
        piantagione.setIdStatoPiantagione(1); // ATTIVA
        assertTrue(piantagione.isAttiva(), "La piantagione dovrebbe iniziare come attiva");

        // Act & Assert - Passaggio a IN_RACCOLTA (id 3)
        StatoPiantagione statoRaccolta = new StatoPiantagione();
        statoRaccolta.setId(3);
        statoRaccolta.setCodice(StatoPiantagione.IN_RACCOLTA);
        piantagione.cambiaStato(statoRaccolta);

        // Verifica stato in raccolta
        assertFalse(piantagione.isAttiva(), "Non dovrebbe più essere attiva");
        assertFalse(piantagione.isCompletata(), "Non dovrebbe ancora essere completata");
        assertEquals(3, piantagione.getIdStatoPiantagione(), "Dovrebbe essere in raccolta");

        // Completamento
        piantagione.completa();
        assertTrue(piantagione.isCompletata(), "Dovrebbe essere completata");
        assertFalse(piantagione.isAttiva(), "Non dovrebbe più essere attiva");
        assertEquals(4, piantagione.getIdStatoPiantagione(), "Dovrebbe avere ID stato COMPLETATA");

        // Verifica durata (dovrebbe essere esattamente 90 giorni)
        assertEquals(90, piantagione.getDurataGiorni(), "La durata dovrebbe essere 90 giorni");
    }
}
