package BusinessLogic.Service;

import DomainModel.Pianta;
import ORM.PiantaDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            piantaService.aggiungiPianta(null);
        });
        assertEquals("Pianta non può essere null", exception.getMessage());
    }

    @Test
    void testAggiungiPiantaTipoVuoto() {
        piantaValida.setTipo("");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            piantaService.aggiungiPianta(piantaValida);
        });
        assertEquals("Il tipo di pianta è obbligatorio", exception.getMessage());
    }

    @Test
    void testAggiungiPiantaTipoNull() {
        piantaValida.setTipo(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            piantaService.aggiungiPianta(piantaValida);
        });
        assertEquals("Il tipo di pianta è obbligatorio", exception.getMessage());
    }

    @Test
    void testAggiungiPiantaVarietaVuota() {
        piantaValida.setVarieta("");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            piantaService.aggiungiPianta(piantaValida);
        });
        assertEquals("La varietà è obbligatoria", exception.getMessage());
    }

    @Test
    void testAggiungiPiantaVarietaNull() {
        piantaValida.setVarieta(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            piantaService.aggiungiPianta(piantaValida);
        });
        assertEquals("La varietà è obbligatoria", exception.getMessage());
    }

    @Test
    void testAggiungiPiantaCostoNegativo() {
        piantaValida.setCosto(new BigDecimal("-1.00"));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            piantaService.aggiungiPianta(piantaValida);
        });
        assertEquals("Il costo non può essere negativo", exception.getMessage());
    }

    @Test
    void testAggiungiPiantaFornitoreNull() {
        piantaValida.setFornitoreId(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            piantaService.aggiungiPianta(piantaValida);
        });
        assertEquals("Il fornitore è obbligatorio", exception.getMessage());
    }

    @Test
    void testAggiungiPiantaValida() {
        assertDoesNotThrow(() -> piantaService.aggiungiPianta(piantaValida));
        assertEquals(1, piantaValida.getId());
    }

    @Test
    void testAggiornaPiantaSenzaId() {
        piantaValida.setId(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            piantaService.aggiornaPianta(piantaValida);
        });
        assertEquals("ID pianta richiesto per l'aggiornamento", exception.getMessage());
    }

    @Test
    void testAggiornaPiantaValida() {
        assertDoesNotThrow(() -> piantaService.aggiornaPianta(piantaValida));
    }
}
