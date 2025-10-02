package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
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
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            fornitoreService.aggiungiFornitore(null);
        });
        assertTrue(exception.getMessage().contains("Fornitore non può essere null"));
    }

    @Test
    void testAggiungiFornitoreNomeVuoto() {
        fornitoreValido.setNome("");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            fornitoreService.aggiungiFornitore(fornitoreValido);
        });
        assertTrue(exception.getMessage().contains("Nome fornitore"));
    }

    @Test
    void testAggiungiFornitoreNomeNull() {
        fornitoreValido.setNome(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            fornitoreService.aggiungiFornitore(fornitoreValido);
        });
        assertTrue(exception.getMessage().contains("Nome fornitore"));
    }

    @Test
    void testAggiungiFornitoreIndirizzoNull() {
        fornitoreValido.setIndirizzo(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            fornitoreService.aggiungiFornitore(fornitoreValido);
        });
        assertTrue(exception.getMessage().contains("Indirizzo"));
    }

    @Test
    void testAggiungiFornitoreEmailFormatoNonValido() {
        fornitoreValido.setEmail("email-non-valida");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            fornitoreService.aggiungiFornitore(fornitoreValido);
        });
        assertTrue(exception.getMessage().contains("Email"));
    }

    @Test
    void testAggiungiFornitoreTelefonoTroppoCorto() {
        fornitoreValido.setNumeroTelefono("123");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            fornitoreService.aggiungiFornitore(fornitoreValido);
        });
        assertTrue(exception.getMessage().contains("numeroTelefono") ||
                  exception.getMessage().contains("almeno 8 caratteri"));
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
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            fornitoreService.aggiornaFornitore(fornitoreValido);
        });
        assertTrue(exception.getMessage().contains("ID fornitore per aggiornamento"));
    }

    @Test
    void testAggiornaFornitoreValido() {
        assertDoesNotThrow(() -> fornitoreService.aggiornaFornitore(fornitoreValido));
    }
}
