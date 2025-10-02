package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
import DomainModel.Raccolto;
import ORM.RaccoltoDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class RaccoltoServiceTest {
    private RaccoltoService raccoltoService;
    private Raccolto raccoltoValido;

    // Mock DAO che simula successo nelle operazioni
    static class MockRaccoltoDAO extends RaccoltoDAO {
        @Override
        public void create(Raccolto raccolto) {
            // Simula l'assegnazione di un ID dopo il salvataggio
            raccolto.setId(1);
        }

        @Override
        public void update(Raccolto raccolto) {
            // Simula aggiornamento riuscito
        }
    }

    @BeforeEach
    void setUp() {
        raccoltoService = new RaccoltoService(new MockRaccoltoDAO());
        raccoltoValido = new Raccolto();
        raccoltoValido.setId(1);
        raccoltoValido.setDataRaccolto(LocalDate.now().minusDays(1));
        raccoltoValido.setQuantitaKg(new BigDecimal("25.50"));
        raccoltoValido.setNote("Raccolto di buona qualità");
        raccoltoValido.setPiantagioneId(1);
        raccoltoValido.setDataCreazione(LocalDateTime.now());
        raccoltoValido.setDataAggiornamento(LocalDateTime.now());
    }

    @Test
    void testAggiungiRaccoltoNull() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            raccoltoService.aggiungiRaccolto(null);
        });
        assertTrue(exception.getMessage().contains("Raccolto non può essere null"));
    }

    @Test
    void testAggiungiRaccoltoDataNull() {
        raccoltoValido.setDataRaccolto(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            raccoltoService.aggiungiRaccolto(raccoltoValido);
        });
        assertTrue(exception.getMessage().contains("Data di raccolto"));
    }

    @Test
    void testAggiungiRaccoltoDataFutura() {
        raccoltoValido.setDataRaccolto(LocalDate.now().plusDays(1));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            raccoltoService.aggiungiRaccolto(raccoltoValido);
        });
        assertTrue(exception.getMessage().contains("dataRaccolto") ||
                  exception.getMessage().contains("futuro"));
    }

    @Test
    void testAggiungiRaccoltoQuantitaNull() {
        raccoltoValido.setQuantitaKg(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            raccoltoService.aggiungiRaccolto(raccoltoValido);
        });
        assertTrue(exception.getMessage().contains("quantitaKg") ||
                  exception.getMessage().contains("maggiore di zero"));
    }

    @Test
    void testAggiungiRaccoltoQuantitaNegativa() {
        raccoltoValido.setQuantitaKg(new BigDecimal("-5.0"));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            raccoltoService.aggiungiRaccolto(raccoltoValido);
        });
        assertTrue(exception.getMessage().contains("quantitaKg") ||
                  exception.getMessage().contains("maggiore di zero"));
    }

    @Test
    void testAggiungiRaccoltoQuantitaZero() {
        raccoltoValido.setQuantitaKg(BigDecimal.ZERO);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            raccoltoService.aggiungiRaccolto(raccoltoValido);
        });
        assertTrue(exception.getMessage().contains("quantitaKg") ||
                  exception.getMessage().contains("maggiore di zero"));
    }

    @Test
    void testAggiungiRaccoltoPiantagioneNull() {
        raccoltoValido.setPiantagioneId(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            raccoltoService.aggiungiRaccolto(raccoltoValido);
        });
        assertTrue(exception.getMessage().contains("Piantagione"));
    }

    @Test
    void testAggiungiRaccoltoValido() {
        assertDoesNotThrow(() -> raccoltoService.aggiungiRaccolto(raccoltoValido));
        assertEquals(1, raccoltoValido.getId());
    }

    @Test
    void testAggiornaRaccoltoSenzaId() {
        raccoltoValido.setId(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            raccoltoService.aggiornaRaccolto(raccoltoValido);
        });
        assertTrue(exception.getMessage().contains("ID raccolto") ||
                  exception.getMessage().contains("aggiornamento"));
    }

    @Test
    void testAggiornaRaccoltoValido() {
        assertDoesNotThrow(() -> raccoltoService.aggiornaRaccolto(raccoltoValido));
    }
}
