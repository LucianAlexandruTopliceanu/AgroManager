package BusinessLogic.Service;

import DomainModel.Raccolto;
import ORM.RaccoltoDAO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class RaccoltoService {
    private final RaccoltoDAO raccoltoDAO;

    public RaccoltoService(RaccoltoDAO raccoltoDAO) {
        this.raccoltoDAO = raccoltoDAO;
    }

    public List<Raccolto> getAllRaccolti() {
        try {
            return raccoltoDAO.findAll();
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Errore durante il recupero dei raccolti", e);
        }
    }

    public void aggiungiRaccolto(Raccolto raccolto) {
        try {
            raccoltoDAO.create(raccolto);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Errore durante l'inserimento del raccolto", e);
        }
    }

    public void aggiornaRaccolto(Raccolto raccolto) {
        try {
            raccoltoDAO.update(raccolto);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Errore durante l'aggiornamento del raccolto", e);
        }
    }

    public void eliminaRaccolto(Integer id) {
        try {
            raccoltoDAO.delete(id);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Errore durante l'eliminazione del raccolto", e);
        }
    }

    // Nuovi metodi per supportare la dashboard
    public List<Raccolto> getRaccoltiDelMese() {
        LocalDate inizioMese = LocalDate.now().withDayOfMonth(1);
        LocalDate fineMese = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        return getAllRaccolti().stream()
            .filter(r -> r.getDataRaccolto() != null &&
                    !r.getDataRaccolto().isBefore(inizioMese) &&
                    !r.getDataRaccolto().isAfter(fineMese))
            .collect(Collectors.toList());
    }

    public BigDecimal getProduzioneTotale() {
        return getAllRaccolti().stream()
            .map(Raccolto::getQuantitaKg)
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
