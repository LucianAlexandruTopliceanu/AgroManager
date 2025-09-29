package BusinessLogic.Service;

import DomainModel.Fornitore;
import ORM.FornitoreDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


public class FornitoreServiceTest {
    private FornitoreService fornitoreService;
    private Fornitore fornitoreValido;

    // Mock DAO che simula successo nelle operazioni
    static class MockFornitoreDAO extends FornitoreDAO {
        @Override
        public void create(Fornitore fornitore) {
            // Simula l'assegnazione di un ID dopo il salvataggio
            fornitore.setId(1);
        }

        @Override
        public void update(Fornitore fornitore) {
            // Simula aggiornamento riuscito
        }
    }

    @BeforeEach
    void setUp() {
        fornitoreService = new FornitoreService(new MockFornitoreDAO());
        fornitoreValido = new Fornitore();
        fornitoreValido.setId(1);
        fornitoreValido.setNome("Fornitore Test SRL");
        fornitoreValido.setIndirizzo("Via Test 123, Milano");
        fornitoreValido.setNumeroTelefono("02-12345678");
        fornitoreValido.setEmail("test@fornitore.it");
        fornitoreValido.setPartitaIva("12345678901");
        fornitoreValido.setDataCreazione(LocalDateTime.now());
        fornitoreValido.setDataAggiornamento(LocalDateTime.now());
    }

    @Test
    void testAggiungiFornitoreNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fornitoreService.aggiungiFornitore(null);
        });
        assertEquals("Fornitore non può essere null", exception.getMessage());
    }

    @Test
    void testAggiungiFornitoreNomeVuoto() {
        fornitoreValido.setNome("");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fornitoreService.aggiungiFornitore(fornitoreValido);
        });
        assertEquals("Il nome del fornitore è obbligatorio", exception.getMessage());
    }

    @Test
    void testAggiungiFornitoreNomeNull() {
        fornitoreValido.setNome(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fornitoreService.aggiungiFornitore(fornitoreValido);
        });
        assertEquals("Il nome del fornitore è obbligatorio", exception.getMessage());
    }

    @Test
    void testAggiungiFornitoreIndirizzoNull() {
        fornitoreValido.setIndirizzo(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fornitoreService.aggiungiFornitore(fornitoreValido);
        });
        assertEquals("L'indirizzo è obbligatorio", exception.getMessage());
    }

    @Test
    void testAggiungiFornitoreEmailFormatoNonValido() {
        fornitoreValido.setEmail("email-non-valida");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fornitoreService.aggiungiFornitore(fornitoreValido);
        });
        assertEquals("Formato email non valido", exception.getMessage());
    }

    @Test
    void testAggiungiFornitoreTelefonoTroppoCorto() {
        fornitoreValido.setNumeroTelefono("123");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fornitoreService.aggiungiFornitore(fornitoreValido);
        });
        assertEquals("Numero di telefono troppo corto", exception.getMessage());
    }

    @Test
    void testAggiungiFornitoreValido() {
        assertDoesNotThrow(() -> fornitoreService.aggiungiFornitore(fornitoreValido));
        // Verifica che l'ID sia stato assegnato dal mock DAO
        assertEquals(1, fornitoreValido.getId());
    }

    @Test
    void testAggiornaFornitoreSenzaId() {
        fornitoreValido.setId(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fornitoreService.aggiornaFornitore(fornitoreValido);
        });
        assertEquals("ID fornitore richiesto per l'aggiornamento", exception.getMessage());
    }

    @Test
    void testAggiornaFornitoreValido() {
        assertDoesNotThrow(() -> fornitoreService.aggiornaFornitore(fornitoreValido));
    }
}
