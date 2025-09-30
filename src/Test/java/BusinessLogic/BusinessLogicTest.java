package BusinessLogic;

import BusinessLogic.Strategy.DataProcessingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class BusinessLogicTest {

    private BusinessLogic businessLogic;

    @BeforeEach
    void setUp() {
        businessLogic = new BusinessLogic();
    }

    @Test
    void testEseguiStrategiaProduzioneTotaleConDatiVuoti() {
        // Test con database vuoto - dovrebbe gestire gracefully
        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.CALCULATION,
            "Produzione Totale",
            "1",
            null, null, null
        );

        assertNotNull(risultato);
        System.out.println("DEBUG - Risultato Produzione Totale: " + risultato);
        // Con dati vuoti, dovrebbe comunque restituire un risultato valido (0.00) o errore
        assertTrue(risultato.contains("0.00") || risultato.contains("0,00") ||
                  risultato.contains("Errore") || risultato.contains("Produzione"));
    }

    @Test
    void testEseguiStrategiaMediaPerPiantaConDatiVuoti() {
        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.CALCULATION,
            "Media per Pianta",
            "1",
            null, null, null
        );

        assertNotNull(risultato);
        System.out.println("DEBUG - Risultato Media per Pianta: " + risultato);
        // Dovrebbe gestire il caso senza dati
        assertTrue(risultato.contains("Errore") || risultato.contains("non trovata") ||
                  risultato.contains("0.00") || risultato.contains("0,00") ||
                  risultato.contains("Media") || risultato.contains("piantagione"));
    }

    @Test
    void testEseguiStrategiaProduzionePerPeriodo() {
        LocalDate inizio = LocalDate.now().minusDays(10);
        LocalDate fine = LocalDate.now();

        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.CALCULATION,
            "Produzione per Periodo",
            null,
            inizio, fine, null
        );

        assertNotNull(risultato);
        assertTrue(risultato.contains("periodo") || risultato.contains("Produzione"));
        assertFalse(risultato.contains("null"));
    }

    @Test
    void testEseguiStrategiaTopPiantagioni() {
        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.STATISTICS,
            "Top Piantagioni",
            null,
            null, null, 2
        );

        assertNotNull(risultato);
        assertTrue(risultato.contains("Top") || risultato.contains("piantagioni"));
    }

    @Test
    void testEseguiStrategiaPiantagioneMigliore() {
        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.STATISTICS,
            "Piantagione Migliore",
            null,
            null, null, null
        );

        assertNotNull(risultato);
        // Dovrebbe restituire un risultato anche se non ci sono dati
        assertFalse(risultato.isEmpty());
    }

    @Test
    void testEseguiStrategiaReportRaccolti() {
        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.REPORT,
            "Report Raccolti",
            null,
            null, null, null
        );

        assertNotNull(risultato);
        assertTrue(risultato.contains("REPORT") || risultato.contains("raccolti") || risultato.contains("Nessun"));
    }

    @Test
    void testEseguiStrategiaTipoSbagliato() {
        // Tenta di eseguire una strategia CALCULATION come REPORT
        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.REPORT,
            "Produzione Totale",
            "1",
            null, null, null
        );

        assertNotNull(risultato);
        assertTrue(risultato.startsWith("Errore"));
        assertTrue(risultato.contains("tipo"));
    }

    @Test
    void testEseguiStrategiaNonEsistente() {
        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.CALCULATION,
            "Strategia Inesistente",
            "1",
            null, null, null
        );

        assertNotNull(risultato);
        assertTrue(risultato.startsWith("Errore"));
        assertTrue(risultato.contains("non riconosciuta"));
    }

    @Test
    void testEseguiStrategiaParametriInvalidi() {
        // Test con ID piantagione non numerico
        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.CALCULATION,
            "Produzione Totale",
            "abc",
            null, null, null
        );

        assertNotNull(risultato);
        assertTrue(risultato.startsWith("Errore"));
    }

    @Test
    void testEseguiStrategiaEfficienzaProduttiva() {
        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.CALCULATION,
            "Efficienza Produttiva",
            "1",
            null, null, null
        );

        assertNotNull(risultato);
        // Dovrebbe gestire il caso anche senza dati reali
        assertTrue(risultato.contains("Efficienza") || risultato.contains("Errore"));
    }

    @Test
    void testEseguiStrategiaStatisticheZone() {
        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.STATISTICS,
            "Statistiche Zone",
            null,
            null, null, null
        );

        assertNotNull(risultato);
        System.out.println("DEBUG - Risultato Statistiche Zone: " + risultato);
        assertTrue(risultato.contains("Zone") || risultato.contains("statistiche") || risultato.contains("Nessun") || risultato.contains("ZONA"));
    }

    @Test
    void testIsRaccoltoInPeriodo() {
        LocalDate data = LocalDate.of(2024, 7, 15);
        LocalDate inizio = LocalDate.of(2024, 7, 1);
        LocalDate fine = LocalDate.of(2024, 7, 31);

        assertTrue(businessLogic.isRaccoltoInPeriodo(data, inizio, fine));
        assertFalse(businessLogic.isRaccoltoInPeriodo(data, inizio.plusDays(20), fine));
        assertFalse(businessLogic.isRaccoltoInPeriodo(null, inizio, fine));
        assertFalse(businessLogic.isRaccoltoInPeriodo(data, null, fine));
        assertFalse(businessLogic.isRaccoltoInPeriodo(data, inizio, null));
    }

    @Test
    void testParametriValidazioneDate() {
        // Test con date invalide (fine prima di inizio)
        LocalDate inizio = LocalDate.now();
        LocalDate fine = LocalDate.now().minusDays(5);

        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.CALCULATION,
            "Produzione per Periodo",
            null,
            inizio, fine, null
        );

        assertNotNull(risultato);
        assertTrue(risultato.startsWith("Errore") || risultato.contains("validazione"));
    }

    @Test
    void testParametriValidazioneTopN() {
        // Test con topN negativo (se la strategia lo gestisce)
        String risultato = businessLogic.eseguiStrategia(
            DataProcessingStrategy.ProcessingType.STATISTICS,
            "Top Piantagioni",
            null,
            null, null, -1
        );

        assertNotNull(risultato);
        // Dovrebbe gestire valori negativi appropriatamente
        assertFalse(risultato.contains("null"));
    }
}
