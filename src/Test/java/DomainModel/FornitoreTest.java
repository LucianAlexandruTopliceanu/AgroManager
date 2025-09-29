package DomainModel;

import org.junit.jupiter.api.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FornitoreTest {

    private Fornitore fornitore;
    private LocalDateTime dataTest;

    @BeforeEach
    void setUp() {
        dataTest = LocalDateTime.now();
        fornitore = new Fornitore();
    }

    @Test
    void testCostruttoreVuoto() {
        assertNotNull(fornitore);
        assertNull(fornitore.getId());
        assertNull(fornitore.getNome());
        assertNull(fornitore.getIndirizzo());
        assertNull(fornitore.getNumeroTelefono());
        assertNull(fornitore.getEmail());
        assertNull(fornitore.getPartitaIva());
        assertNull(fornitore.getDataCreazione());
        assertNull(fornitore.getDataAggiornamento());
    }

    @Test
    void testCostruttoreCompleto() {
        Fornitore fornitoreCompleto = new Fornitore(1, "Test Fornitore", "Via Test 123",
            "0123456789", "test@fornitore.com", "12345678901", dataTest, dataTest);

        assertEquals(1, fornitoreCompleto.getId());
        assertEquals("Test Fornitore", fornitoreCompleto.getNome());
        assertEquals("Via Test 123", fornitoreCompleto.getIndirizzo());
        assertEquals("0123456789", fornitoreCompleto.getNumeroTelefono());
        assertEquals("test@fornitore.com", fornitoreCompleto.getEmail());
        assertEquals("12345678901", fornitoreCompleto.getPartitaIva());
        assertEquals(dataTest, fornitoreCompleto.getDataCreazione());
        assertEquals(dataTest, fornitoreCompleto.getDataAggiornamento());
    }

    @Test
    void testSettersAndGetters() {
        fornitore.setId(5);
        fornitore.setNome("Fornitore Test");
        fornitore.setIndirizzo("Via Roma 456");
        fornitore.setNumeroTelefono("0987654321");
        fornitore.setEmail("nuovo@fornitore.com");
        fornitore.setPartitaIva("98765432109");
        fornitore.setDataCreazione(dataTest);
        fornitore.setDataAggiornamento(dataTest);

        assertEquals(5, fornitore.getId());
        assertEquals("Fornitore Test", fornitore.getNome());
        assertEquals("Via Roma 456", fornitore.getIndirizzo());
        assertEquals("0987654321", fornitore.getNumeroTelefono());
        assertEquals("nuovo@fornitore.com", fornitore.getEmail());
        assertEquals("98765432109", fornitore.getPartitaIva());
        assertEquals(dataTest, fornitore.getDataCreazione());
        assertEquals(dataTest, fornitore.getDataAggiornamento());
    }

    @Test
    void testValidazioneEmail() {
        // Test con email valida
        fornitore.setEmail("valida@test.com");
        assertEquals("valida@test.com", fornitore.getEmail());

        // Test con email vuota
        fornitore.setEmail("");
        assertEquals("", fornitore.getEmail());

        // Test con email null
        fornitore.setEmail(null);
        assertNull(fornitore.getEmail());
    }

    @Test
    void testValidazionePartitaIva() {
        // Test con partita IVA di 11 cifre
        fornitore.setPartitaIva("12345678901");
        assertEquals("12345678901", fornitore.getPartitaIva());

        // Test con partita IVA più corta
        fornitore.setPartitaIva("123");
        assertEquals("123", fornitore.getPartitaIva());

        // Test con partita IVA null
        fornitore.setPartitaIva(null);
        assertNull(fornitore.getPartitaIva());
    }

    @Test
    void testValidazioneTelefono() {
        fornitore.setNumeroTelefono("0123456789");
        assertEquals("0123456789", fornitore.getNumeroTelefono());

        fornitore.setNumeroTelefono("+39 012 345 6789");
        assertEquals("+39 012 345 6789", fornitore.getNumeroTelefono());

        fornitore.setNumeroTelefono(null);
        assertNull(fornitore.getNumeroTelefono());
    }

    @Test
    void testCampiObbligatori() {
        // Test che i campi possano essere null (non c'è validazione nella classe dominio)
        fornitore.setNome(null);
        fornitore.setIndirizzo(null);
        fornitore.setEmail(null);
        fornitore.setPartitaIva(null);

        assertNull(fornitore.getNome());
        assertNull(fornitore.getIndirizzo());
        assertNull(fornitore.getEmail());
        assertNull(fornitore.getPartitaIva());
    }
}
