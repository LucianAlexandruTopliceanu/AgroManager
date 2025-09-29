package BusinessLogic.Service;

import DomainModel.Piantagione;
import ORM.PiantagioneDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PiantagioneServiceTest {
    private PiantagioneService piantagioneService;
    private Piantagione piantagioneValida;

    // Mock DAO che simula successo nelle operazioni
    static class MockPiantagioneDAO extends PiantagioneDAO {
        @Override
        public void create(Piantagione piantagione) {
            // Simula l'assegnazione di un ID dopo il salvataggio
            piantagione.setId(1);
        }

        @Override
        public void update(Piantagione piantagione) {
            // Simula aggiornamento riuscito
        }
    }

    @BeforeEach
    void setUp() {
        piantagioneService = new PiantagioneService(new MockPiantagioneDAO());
        piantagioneValida = new Piantagione();
        piantagioneValida.setId(1);
        piantagioneValida.setQuantitaPianta(10);
        piantagioneValida.setMessaADimora(LocalDate.now());
        piantagioneValida.setPiantaId(1);
        piantagioneValida.setZonaId(1);
    }

    @Test
    void testAggiungiPiantagioneNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            piantagioneService.aggiungiPiantagione(null);
        });
        assertEquals("Piantagione non può essere null", exception.getMessage());
    }

    @Test
    void testAggiungiPiantagioneQuantitaNegativa() {
        piantagioneValida.setQuantitaPianta(-1);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            piantagioneService.aggiungiPiantagione(piantagioneValida);
        });
        assertEquals("La quantità deve essere positiva", exception.getMessage());
    }

    @Test
    void testAggiungiPiantagioneQuantitaNull() {
        piantagioneValida.setQuantitaPianta(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            piantagioneService.aggiungiPiantagione(piantagioneValida);
        });
        assertEquals("La quantità deve essere positiva", exception.getMessage());
    }

    @Test
    void testAggiungiPiantagioneDataNull() {
        piantagioneValida.setMessaADimora(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            piantagioneService.aggiungiPiantagione(piantagioneValida);
        });
        assertEquals("La data di messa a dimora è obbligatoria", exception.getMessage());
    }

    @Test
    void testAggiungiPiantagionePiantaIdNull() {
        piantagioneValida.setPiantaId(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            piantagioneService.aggiungiPiantagione(piantagioneValida);
        });
        assertEquals("La pianta è obbligatoria", exception.getMessage());
    }

    @Test
    void testAggiungiPiantagioneZonaIdNull() {
        piantagioneValida.setZonaId(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            piantagioneService.aggiungiPiantagione(piantagioneValida);
        });
        assertEquals("La zona è obbligatoria", exception.getMessage());
    }

    @Test
    void testAggiungiPiantagioneValida() {
        assertDoesNotThrow(() -> piantagioneService.aggiungiPiantagione(piantagioneValida));
        assertEquals(1, piantagioneValida.getId());
    }

    @Test
    void testAggiornaPiantagioneSenzaId() {
        piantagioneValida.setId(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            piantagioneService.aggiornaPiantagione(piantagioneValida);
        });
        assertEquals("ID piantagione richiesto per l'aggiornamento", exception.getMessage());
    }

    @Test
    void testAggiornaPiantagioneValida() {
        assertDoesNotThrow(() -> piantagioneService.aggiornaPiantagione(piantagioneValida));
    }
}
