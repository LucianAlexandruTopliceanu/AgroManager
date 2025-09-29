package BusinessLogic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class BusinessLogicTest {

    private List<MockRaccolto> raccolti;
    private List<MockPiantagione> piantagioni;

    @BeforeEach
    void setUp() {
        raccolti = new ArrayList<>();
        piantagioni = new ArrayList<>();

        // Dati di test
        piantagioni.add(new MockPiantagione(1, LocalDate.of(2024, 3, 1), 100, "Pomodoro"));
        piantagioni.add(new MockPiantagione(2, LocalDate.of(2024, 4, 1), 50, "Basilico"));

        raccolti.add(new MockRaccolto(LocalDate.of(2024, 7, 15), new BigDecimal("25.5"), 1));
        raccolti.add(new MockRaccolto(LocalDate.of(2024, 8, 1), new BigDecimal("30.0"), 1));
        raccolti.add(new MockRaccolto(LocalDate.of(2024, 7, 20), new BigDecimal("5.2"), 2));
    }

    @Test
    void testCalcoloProduzioneTotale() {
        BigDecimal produzioneTotalePomodoro = calcolaProduzioneTotalePiantagione(1);
        BigDecimal produzioneBasilico = calcolaProduzioneTotalePiantagione(2);

        assertEquals(new BigDecimal("55.5"), produzioneTotalePomodoro);
        assertEquals(new BigDecimal("5.2"), produzioneBasilico);
    }

    @Test
    void testCalcoloMediaProduzione() {
        BigDecimal mediaPomodoro = calcolaMediaProduzionePianta(1);
        BigDecimal mediaBasilico = calcolaMediaProduzionePianta(2);

        // 55.5 kg / 100 piante = 0.555 kg per pianta
        assertEquals(new BigDecimal("0.56"), mediaPomodoro.setScale(2, RoundingMode.HALF_UP));
        // 5.2 kg / 50 piante = 0.104 kg per pianta
        assertEquals(new BigDecimal("0.10"), mediaBasilico.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testCalcoloGiorniPiantagioneRaccolto() {
        long giorniPomodoro = calcolaGiorniDallaMessaADimora(1);
        long giorniBasilico = calcolaGiorniDallaMessaADimora(2);

        // Dal 1 marzo al 15 luglio = 136 giorni
        assertEquals(136, giorniPomodoro);
        // Dal 1 aprile al 20 luglio = 110 giorni
        assertEquals(110, giorniBasilico);
    }

    @Test
    void testPiantagionePiuProduttiva() {
        int piantagionePiuProduttiva = trovaIdPiantagionePiuProduttiva();
        assertEquals(1, piantagionePiuProduttiva);
    }

    @Test
    void testCalcoloEfficienzaProduttiva() {
        BigDecimal efficienzaPomodoro = calcolaEfficienzaProduttiva(1);
        BigDecimal efficienzaBasilico = calcolaEfficienzaProduttiva(2);

        // Pomodoro: 55.5 kg / 100 piante / 136 giorni = 0.00408
        assertTrue(efficienzaPomodoro.compareTo(new BigDecimal("0.004")) > 0);
        assertTrue(efficienzaPomodoro.compareTo(new BigDecimal("0.005")) < 0);

        // Basilico: 5.2 kg / 50 piante / 110 giorni = 0.00095
        assertTrue(efficienzaBasilico.compareTo(new BigDecimal("0.0009")) > 0);
        assertTrue(efficienzaBasilico.compareTo(new BigDecimal("0.001")) < 0);
    }

    @Test
    void testValidazionePeriodoRaccolto() {
        LocalDate inizioEstate = LocalDate.of(2024, 6, 21);
        LocalDate fineEstate = LocalDate.of(2024, 9, 22);

        assertTrue(isRaccoltoInPeriodo(LocalDate.of(2024, 7, 15), inizioEstate, fineEstate));
        assertFalse(isRaccoltoInPeriodo(LocalDate.of(2024, 5, 15), inizioEstate, fineEstate));
        assertFalse(isRaccoltoInPeriodo(LocalDate.of(2024, 10, 15), inizioEstate, fineEstate));
    }

    // Metodi di supporto per i test - simulano la logica di business

    private BigDecimal calcolaProduzioneTotalePiantagione(int piantagioneId) {
        return raccolti.stream()
            .filter(r -> r.piantagioneId == piantagioneId)
            .map(r -> r.quantitaKg)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcolaMediaProduzionePianta(int piantagioneId) {
        BigDecimal totale = calcolaProduzioneTotalePiantagione(piantagioneId);
        int numeroPiante = piantagioni.stream()
            .filter(p -> p.id == piantagioneId)
            .mapToInt(p -> p.quantitaPiante)
            .findFirst()
            .orElse(1);

        return totale.divide(new BigDecimal(numeroPiante), 3, RoundingMode.HALF_UP);
    }

    private long calcolaGiorniDallaMessaADimora(int piantagioneId) {
        LocalDate messaADimora = piantagioni.stream()
            .filter(p -> p.id == piantagioneId)
            .map(p -> p.messaADimora)
            .findFirst()
            .orElse(LocalDate.now());

        LocalDate primoRaccolto = raccolti.stream()
            .filter(r -> r.piantagioneId == piantagioneId)
            .map(r -> r.dataRaccolto)
            .min(LocalDate::compareTo)
            .orElse(LocalDate.now());

        return ChronoUnit.DAYS.between(messaADimora, primoRaccolto);
    }

    private int trovaIdPiantagionePiuProduttiva() {
        return raccolti.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                r -> r.piantagioneId,
                java.util.stream.Collectors.reducing(
                    BigDecimal.ZERO,
                    r -> r.quantitaKg,
                    BigDecimal::add)))
            .entrySet()
            .stream()
            .max(java.util.Map.Entry.comparingByValue())
            .map(java.util.Map.Entry::getKey)
            .orElse(0);
    }

    private BigDecimal calcolaEfficienzaProduttiva(int piantagioneId) {
        BigDecimal totaleProduzione = calcolaProduzioneTotalePiantagione(piantagioneId);
        int numeroPiante = piantagioni.stream()
            .filter(p -> p.id == piantagioneId)
            .mapToInt(p -> p.quantitaPiante)
            .findFirst()
            .orElse(1);
        long giorni = calcolaGiorniDallaMessaADimora(piantagioneId);

        return totaleProduzione.divide(
            new BigDecimal(numeroPiante).multiply(new BigDecimal(giorni)),
            6,
            RoundingMode.HALF_UP
        );
    }

    private boolean isRaccoltoInPeriodo(LocalDate dataRaccolto, LocalDate inizio, LocalDate fine) {
        return !dataRaccolto.isBefore(inizio) && !dataRaccolto.isAfter(fine);
    }

    // Classi mock per i test
    private static class MockRaccolto {
        LocalDate dataRaccolto;
        BigDecimal quantitaKg;
        int piantagioneId;

        MockRaccolto(LocalDate dataRaccolto, BigDecimal quantitaKg, int piantagioneId) {
            this.dataRaccolto = dataRaccolto;
            this.quantitaKg = quantitaKg;
            this.piantagioneId = piantagioneId;
        }
    }

    private static class MockPiantagione {
        int id;
        LocalDate messaADimora;
        int quantitaPiante;
        String tipoPianta;

        MockPiantagione(int id, LocalDate messaADimora, int quantitaPiante, String tipoPianta) {
            this.id = id;
            this.messaADimora = messaADimora;
            this.quantitaPiante = quantitaPiante;
            this.tipoPianta = tipoPianta;
        }
    }
}
