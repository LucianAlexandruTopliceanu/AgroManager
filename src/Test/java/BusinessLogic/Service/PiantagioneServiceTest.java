package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
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
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantagioneService.aggiungiPiantagione(null);
        });
        assertTrue(exception.getMessage().contains("Piantagione non può essere null"));
    }

    @Test
    void testAggiungiPiantagioneQuantitaNegativa() {
        piantagioneValida.setQuantitaPianta(-1);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantagioneService.aggiungiPiantagione(piantagioneValida);
        });
        assertTrue(exception.getMessage().contains("quantitaPianta") ||
                  exception.getMessage().contains("maggiore di zero"));
    }

    @Test
    void testAggiungiPiantagioneQuantitaNull() {
        piantagioneValida.setQuantitaPianta(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantagioneService.aggiungiPiantagione(piantagioneValida);
        });
        assertTrue(exception.getMessage().contains("quantitaPianta") ||
                  exception.getMessage().contains("maggiore di zero"));
    }

    @Test
    void testAggiungiPiantagioneDataNull() {
        piantagioneValida.setMessaADimora(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantagioneService.aggiungiPiantagione(piantagioneValida);
        });
        assertTrue(exception.getMessage().contains("Data di messa a dimora"));
    }

    @Test
    void testAggiungiPiantagionePiantaIdNull() {
        piantagioneValida.setPiantaId(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantagioneService.aggiungiPiantagione(piantagioneValida);
        });
        assertTrue(exception.getMessage().contains("Pianta"));
    }

    @Test
    void testAggiungiPiantagioneZonaIdNull() {
        piantagioneValida.setZonaId(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantagioneService.aggiungiPiantagione(piantagioneValida);
        });
        assertTrue(exception.getMessage().contains("Zona"));
    }

    @Test
    void testAggiungiPiantagioneValida() {
        assertDoesNotThrow(() -> piantagioneService.aggiungiPiantagione(piantagioneValida));
        assertEquals(1, piantagioneValida.getId());
    }

    @Test
    void testAggiornaPiantagioneSenzaId() {
        piantagioneValida.setId(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantagioneService.aggiornaPiantagione(piantagioneValida);
        });
        assertTrue(exception.getMessage().contains("ID piantagione") ||
                  exception.getMessage().contains("aggiornamento"));
    }

    @Test
    void testAggiornaPiantagioneValida() {
        assertDoesNotThrow(() -> piantagioneService.aggiornaPiantagione(piantagioneValida));
    }
}
