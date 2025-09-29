package BusinessLogic.Service;

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
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            zonaService.aggiungiZona(null);
        });
        assertEquals("Zona non può essere null", exception.getMessage());
    }

    @Test
    void testAggiungiZonaNomeNull() {
        zonaValida.setNome(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            zonaService.aggiungiZona(zonaValida);
        });
        assertEquals("Il nome della zona è obbligatorio", exception.getMessage());
    }

    @Test
    void testAggiungiZonaNomeVuoto() {
        zonaValida.setNome("");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            zonaService.aggiungiZona(zonaValida);
        });
        assertEquals("Il nome della zona è obbligatorio", exception.getMessage());
    }

    @Test
    void testAggiungiZonaDimensioneNegativa() {
        zonaValida.setDimensione(-5.0);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            zonaService.aggiungiZona(zonaValida);
        });
        assertEquals("La dimensione deve essere positiva", exception.getMessage());
    }

    @Test
    void testAggiungiZonaDimensioneZero() {
        zonaValida.setDimensione(0.0);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            zonaService.aggiungiZona(zonaValida);
        });
        assertEquals("La dimensione deve essere positiva", exception.getMessage());
    }

    @Test
    void testAggiungiZonaTipoTerrenoNull() {
        zonaValida.setTipoTerreno(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            zonaService.aggiungiZona(zonaValida);
        });
        assertEquals("Il tipo di terreno è obbligatorio", exception.getMessage());
    }

    @Test
    void testAggiungiZonaTipoTerrenoVuoto() {
        zonaValida.setTipoTerreno("");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            zonaService.aggiungiZona(zonaValida);
        });
        assertEquals("Il tipo di terreno è obbligatorio", exception.getMessage());
    }

    @Test
    void testAggiungiZonaValida() {
        assertDoesNotThrow(() -> zonaService.aggiungiZona(zonaValida));
        assertEquals(1, zonaValida.getId());
    }

    @Test
    void testAggiornaZonaSenzaId() {
        zonaValida.setId(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            zonaService.aggiornaZona(zonaValida);
        });
        assertEquals("ID zona richiesto per l'aggiornamento", exception.getMessage());
    }

    @Test
    void testAggiornaZonaValida() {
        assertDoesNotThrow(() -> zonaService.aggiornaZona(zonaValida));
    }
}
