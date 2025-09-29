package BusinessLogic.Service;

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
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            raccoltoService.aggiungiRaccolto(null);
        });
        assertEquals("Raccolto non può essere null", exception.getMessage());
    }

    @Test
    void testAggiungiRaccoltoDataNull() {
        raccoltoValido.setDataRaccolto(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            raccoltoService.aggiungiRaccolto(raccoltoValido);
        });
        assertEquals("La data di raccolto è obbligatoria", exception.getMessage());
    }

    @Test
    void testAggiungiRaccoltoDataFutura() {
        raccoltoValido.setDataRaccolto(LocalDate.now().plusDays(1));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            raccoltoService.aggiungiRaccolto(raccoltoValido);
        });
        assertEquals("La data di raccolto non può essere nel futuro", exception.getMessage());
    }

    @Test
    void testAggiungiRaccoltoQuantitaNull() {
        raccoltoValido.setQuantitaKg(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            raccoltoService.aggiungiRaccolto(raccoltoValido);
        });
        assertEquals("La quantità deve essere positiva", exception.getMessage());
    }

    @Test
    void testAggiungiRaccoltoQuantitaNegativa() {
        raccoltoValido.setQuantitaKg(new BigDecimal("-5.0"));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            raccoltoService.aggiungiRaccolto(raccoltoValido);
        });
        assertEquals("La quantità deve essere positiva", exception.getMessage());
    }

    @Test
    void testAggiungiRaccoltoQuantitaZero() {
        raccoltoValido.setQuantitaKg(BigDecimal.ZERO);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            raccoltoService.aggiungiRaccolto(raccoltoValido);
        });
        assertEquals("La quantità deve essere positiva", exception.getMessage());
    }

    @Test
    void testAggiungiRaccoltoPiantagioneNull() {
        raccoltoValido.setPiantagioneId(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            raccoltoService.aggiungiRaccolto(raccoltoValido);
        });
        assertEquals("La piantagione è obbligatoria", exception.getMessage());
    }

    @Test
    void testAggiungiRaccoltoValido() {
        assertDoesNotThrow(() -> raccoltoService.aggiungiRaccolto(raccoltoValido));
        assertEquals(1, raccoltoValido.getId());
    }

    @Test
    void testAggiornaRaccoltoSenzaId() {
        raccoltoValido.setId(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            raccoltoService.aggiornaRaccolto(raccoltoValido);
        });
        assertEquals("ID raccolto richiesto per l'aggiornamento", exception.getMessage());
    }

    @Test
    void testAggiornaRaccoltoValido() {
        assertDoesNotThrow(() -> raccoltoService.aggiornaRaccolto(raccoltoValido));
    }
}
