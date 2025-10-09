package DomainModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class StatoPiantagioneTest {
    private StatoPiantagione statoPiantagione;

    @BeforeEach
    void setUp() {
        statoPiantagione = new StatoPiantagione();
    }

    @Test
    void testCostruttoreVuoto() {
        assertNotNull(statoPiantagione);
        assertNull(statoPiantagione.getId());
        assertNull(statoPiantagione.getCodice());
        assertNull(statoPiantagione.getDescrizione());
    }

    @Test
    void testCostruttoreConParametri() {
        StatoPiantagione stato = new StatoPiantagione(1, "TEST_CODICE", "Descrizione Test");

        assertEquals(1, stato.getId());
        assertEquals("TEST_CODICE", stato.getCodice());
        assertEquals("Descrizione Test", stato.getDescrizione());
    }

    @Test
    void testCostruttoreSenzaId() {
        StatoPiantagione stato = new StatoPiantagione("CODICE_TEST", "Descrizione");

        assertNull(stato.getId());
        assertEquals("CODICE_TEST", stato.getCodice());
        assertEquals("Descrizione", stato.getDescrizione());
    }

    @Test
    void testGetterESetterBasici() {
        statoPiantagione.setId(123);
        statoPiantagione.setCodice("STATO_TEST");
        statoPiantagione.setDescrizione("Descrizione di test");

        assertEquals(123, statoPiantagione.getId());
        assertEquals("STATO_TEST", statoPiantagione.getCodice());
        assertEquals("Descrizione di test", statoPiantagione.getDescrizione());
    }

    @Test
    void testGetterESetterTimestamp() {
        LocalDateTime now = LocalDateTime.now();

        statoPiantagione.setDataCreazione(now);
        statoPiantagione.setDataAggiornamento(now);

        assertEquals(now, statoPiantagione.getDataCreazione());
        assertEquals(now, statoPiantagione.getDataAggiornamento());
    }

    @Test
    void testCostantiStatiStandard() {
        assertEquals("ATTIVA", StatoPiantagione.ATTIVA);
        assertEquals("RIMOSSA", StatoPiantagione.RIMOSSA);
        assertEquals("IN_RACCOLTA", StatoPiantagione.IN_RACCOLTA);
        assertEquals("COMPLETATA", StatoPiantagione.COMPLETATA);
        assertEquals("SOSPESA", StatoPiantagione.SOSPESA);
    }

    @Test
    void testIsAttiva() {
        statoPiantagione.setCodice(StatoPiantagione.ATTIVA);
        assertTrue(statoPiantagione.isAttiva());

        statoPiantagione.setCodice(StatoPiantagione.RIMOSSA);
        assertFalse(statoPiantagione.isAttiva());
    }

    @Test
    void testIsRimossa() {
        statoPiantagione.setCodice(StatoPiantagione.RIMOSSA);
        assertTrue(statoPiantagione.isRimossa());

        statoPiantagione.setCodice(StatoPiantagione.ATTIVA);
        assertFalse(statoPiantagione.isRimossa());
    }

    @Test
    void testIsCompletata() {
        statoPiantagione.setCodice(StatoPiantagione.COMPLETATA);
        assertTrue(statoPiantagione.isCompletata());

        statoPiantagione.setCodice(StatoPiantagione.ATTIVA);
        assertFalse(statoPiantagione.isCompletata());
    }

    @Test
    @DisplayName("Test metodo toString")
    void testToString() {
        // Test con descrizione normale
        statoPiantagione.setDescrizione("Descrizione di test");
        assertEquals("Descrizione di test", statoPiantagione.toString(),
                    "Il toString deve restituire la descrizione");

        // Test con descrizione null - il metodo String.valueOf(null) restituisce "null"
        statoPiantagione.setDescrizione(null);
        assertEquals("null", statoPiantagione.toString(),
                    "Il toString con descrizione null deve restituire 'null'");
    }

    @Test
    void testEquals() {
        StatoPiantagione stato1 = new StatoPiantagione();
        StatoPiantagione stato2 = new StatoPiantagione();

        // Due stati senza ID dovrebbero essere uguali
        assertTrue(stato1.equals(stato2));

        // Stato con se stesso
        assertTrue(stato1.equals(stato1));

        // Stato con null
        assertFalse(stato1.equals(null));

        // Stato con oggetto di classe diversa
        assertFalse(stato1.equals("stringa"));

        // Stati con ID diversi
        stato1.setId(1);
        stato2.setId(2);
        assertFalse(stato1.equals(stato2));

        // Stati con stesso ID
        stato2.setId(1);
        assertTrue(stato1.equals(stato2));
    }

    @Test
    void testHashCode() {
        StatoPiantagione stato1 = new StatoPiantagione();
        StatoPiantagione stato2 = new StatoPiantagione();

        // HashCode dovrebbe essere 0 per ID null
        assertEquals(0, stato1.hashCode());
        assertEquals(0, stato2.hashCode());

        // HashCode dovrebbe essere uguale per stesso ID
        stato1.setId(123);
        stato2.setId(123);
        assertEquals(stato1.hashCode(), stato2.hashCode());

        // HashCode dovrebbe essere diverso per ID diversi
        stato2.setId(456);
        assertNotEquals(stato1.hashCode(), stato2.hashCode());
    }

    @Test
    void testMetodiUtilitaConCodiceNull() {
        statoPiantagione.setCodice(null);

        assertFalse(statoPiantagione.isAttiva());
        assertFalse(statoPiantagione.isRimossa());
        assertFalse(statoPiantagione.isCompletata());
    }

    @Test
    void testMetodiUtilitaConCodiceVuoto() {
        statoPiantagione.setCodice("");

        assertFalse(statoPiantagione.isAttiva());
        assertFalse(statoPiantagione.isRimossa());
        assertFalse(statoPiantagione.isCompletata());
    }

    @Test
    void testTuttiGliStatiStandard() {
        // Test che tutti i metodi di utilità funzionino per tutti gli stati standard
        String[] statiStandard = {
            StatoPiantagione.ATTIVA,
            StatoPiantagione.RIMOSSA,
            StatoPiantagione.IN_RACCOLTA,
            StatoPiantagione.COMPLETATA,
            StatoPiantagione.SOSPESA
        };

        for (String codice : statiStandard) {
            statoPiantagione.setCodice(codice);

            // Verifica che almeno uno dei metodi di utilità ritorni true
            boolean haStatoRiconosciuto = statoPiantagione.isAttiva() ||
                                        statoPiantagione.isRimossa() ||
                                        statoPiantagione.isCompletata();

            // ATTIVA, RIMOSSA e COMPLETATA dovrebbero essere riconosciute
            if (StatoPiantagione.ATTIVA.equals(codice) ||
                StatoPiantagione.RIMOSSA.equals(codice) ||
                StatoPiantagione.COMPLETATA.equals(codice)) {
                assertTrue(haStatoRiconosciuto, "Stato " + codice + " dovrebbe essere riconosciuto");
            }
        }
    }

    @Test
    void testImmutabilitaCostanti() {
        // Verifica che le costanti non siano cambiate accidentalmente
        assertNotNull(StatoPiantagione.ATTIVA);
        assertNotNull(StatoPiantagione.RIMOSSA);
        assertNotNull(StatoPiantagione.IN_RACCOLTA);
        assertNotNull(StatoPiantagione.COMPLETATA);
        assertNotNull(StatoPiantagione.SOSPESA);

        // Verifica che siano tutte diverse
        assertNotEquals(StatoPiantagione.ATTIVA, StatoPiantagione.RIMOSSA);
        assertNotEquals(StatoPiantagione.ATTIVA, StatoPiantagione.IN_RACCOLTA);
        assertNotEquals(StatoPiantagione.ATTIVA, StatoPiantagione.COMPLETATA);
        assertNotEquals(StatoPiantagione.ATTIVA, StatoPiantagione.SOSPESA);
    }
}
