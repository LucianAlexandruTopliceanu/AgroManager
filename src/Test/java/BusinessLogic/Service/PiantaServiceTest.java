package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
import DomainModel.Pianta;
import ORM.PiantaDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class PiantaServiceTest {
    private PiantaService piantaService;
    private Pianta piantaValida;

    // Mock DAO che simula successo nelle operazioni
    static class MockPiantaDAO extends PiantaDAO {
        @Override
        public void create(Pianta pianta) {
            // Simula l'assegnazione di un ID dopo il salvataggio
            pianta.setId(1);
        }

        @Override
        public void update(Pianta pianta) {
            // Simula aggiornamento riuscito
        }

        @Override
        public List<Pianta> findAll() {
            // Restituisce una lista vuota per evitare conflitti di duplicati nei test
            return new java.util.ArrayList<>();
        }
    }

    @BeforeEach
    void setUp() {
        piantaService = new PiantaService(new MockPiantaDAO());
        piantaValida = new Pianta();
        piantaValida.setId(1);
        piantaValida.setTipo("Pomodoro");
        piantaValida.setVarieta("San Marzano");
        piantaValida.setCosto(new BigDecimal("2.50"));
        piantaValida.setNote("Pianta di alta qualità");
        piantaValida.setFornitoreId(1);
        piantaValida.setDataCreazione(LocalDateTime.now());
        piantaValida.setDataAggiornamento(LocalDateTime.now());
    }

    @Test
    void testAggiungiPiantaNull() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantaService.aggiungiPianta(null);
        });
        assertTrue(exception.getMessage().contains("Pianta non può essere null"));
    }

    @Test
    void testAggiungiPiantaTipoVuoto() {
        piantaValida.setTipo("");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantaService.aggiungiPianta(piantaValida);
        });
        assertTrue(exception.getMessage().contains("Tipo di pianta"));
    }

    @Test
    void testAggiungiPiantaTipoNull() {
        piantaValida.setTipo(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantaService.aggiungiPianta(piantaValida);
        });
        assertTrue(exception.getMessage().contains("Tipo di pianta"));
    }

    @Test
    void testAggiungiPiantaVarietaVuota() {
        piantaValida.setVarieta("");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantaService.aggiungiPianta(piantaValida);
        });
        assertTrue(exception.getMessage().contains("Varietà"));
    }

    @Test
    void testAggiungiPiantaVarietaNull() {
        piantaValida.setVarieta(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantaService.aggiungiPianta(piantaValida);
        });
        assertTrue(exception.getMessage().contains("Varietà"));
    }

    @Test
    void testAggiungiPiantaCostoNegativo() {
        piantaValida.setCosto(new BigDecimal("-1.00"));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantaService.aggiungiPianta(piantaValida);
        });
        assertTrue(exception.getMessage().contains("costo") ||
                  exception.getMessage().contains("negativo"));
    }

    @Test
    void testAggiungiPiantaFornitoreNull() {
        piantaValida.setFornitoreId(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantaService.aggiungiPianta(piantaValida);
        });
        assertTrue(exception.getMessage().contains("Fornitore"));
    }

    @Test
    void testAggiungiPiantaValida() {
        assertDoesNotThrow(() -> piantaService.aggiungiPianta(piantaValida));
        assertEquals(1, piantaValida.getId());
    }

    @Test
    void testAggiornaPiantaSenzaId() {
        piantaValida.setId(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            piantaService.aggiornaPianta(piantaValida);
        });
        assertTrue(exception.getMessage().contains("ID pianta") ||
                  exception.getMessage().contains("aggiornamento"));
    }

    @Test
    void testAggiornaPiantaValida() {
        assertDoesNotThrow(() -> piantaService.aggiornaPianta(piantaValida));
    }
}
