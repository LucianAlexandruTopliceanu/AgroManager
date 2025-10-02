package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
import DomainModel.Zona;
import ORM.ZonaDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


public class ZonaServiceTest {
    private ZonaService zonaService;
    private Zona zonaValida;

    // Mock DAO che simula successo nelle operazioni
    static class MockZonaDAO extends ZonaDAO {
        @Override
        public void create(Zona zona) {
            // Simula l'assegnazione di un ID dopo il salvataggio
            zona.setId(1);
        }

        @Override
        public void update(Zona zona) {
            // Simula aggiornamento riuscito
        }
    }

    @BeforeEach
    void setUp() {
        zonaService = new ZonaService(new MockZonaDAO());
        zonaValida = new Zona();
        zonaValida.setId(1);
        zonaValida.setNome("Zona Test");
        zonaValida.setDimensione(100.0);
        zonaValida.setTipoTerreno("Argilloso");
        zonaValida.setDataCreazione(LocalDateTime.now());
        zonaValida.setDataAggiornamento(LocalDateTime.now());
    }

    @Test
    void testAggiungiZonaNull() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            zonaService.aggiungiZona(null);
        });
        assertTrue(exception.getMessage().contains("Zona non può essere null"));
    }

    @Test
    void testAggiungiZonaNomeNull() {
        zonaValida.setNome(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            zonaService.aggiungiZona(zonaValida);
        });
        assertTrue(exception.getMessage().contains("Nome della zona"));
    }

    @Test
    void testAggiungiZonaNomeVuoto() {
        zonaValida.setNome("");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            zonaService.aggiungiZona(zonaValida);
        });
        assertTrue(exception.getMessage().contains("Nome della zona"));
    }

    @Test
    void testAggiungiZonaDimensioneNegativa() {
        zonaValida.setDimensione(-5.0);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            zonaService.aggiungiZona(zonaValida);
        });
        assertTrue(exception.getMessage().contains("dimensione") ||
                  exception.getMessage().contains("maggiore di zero"));
    }

    @Test
    void testAggiungiZonaDimensioneZero() {
        zonaValida.setDimensione(0.0);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            zonaService.aggiungiZona(zonaValida);
        });
        assertTrue(exception.getMessage().contains("dimensione") ||
                  exception.getMessage().contains("maggiore di zero"));
    }

    @Test
    void testAggiungiZonaTipoTerrenoNull() {
        zonaValida.setTipoTerreno(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            zonaService.aggiungiZona(zonaValida);
        });
        assertTrue(exception.getMessage().contains("Tipo di terreno"));
    }

    @Test
    void testAggiungiZonaTipoTerrenoVuoto() {
        zonaValida.setTipoTerreno("");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            zonaService.aggiungiZona(zonaValida);
        });
        assertTrue(exception.getMessage().contains("Tipo di terreno"));
    }

    @Test
    void testAggiungiZonaValida() {
        assertDoesNotThrow(() -> zonaService.aggiungiZona(zonaValida));
        assertEquals(1, zonaValida.getId());
    }

    @Test
    void testAggiornaZonaSenzaId() {
        zonaValida.setId(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            zonaService.aggiornaZona(zonaValida);
        });
        assertTrue(exception.getMessage().contains("ID zona") ||
                  exception.getMessage().contains("aggiornamento"));
    }

    @Test
    void testAggiornaZonaValida() {
        assertDoesNotThrow(() -> zonaService.aggiornaZona(zonaValida));
    }
}
