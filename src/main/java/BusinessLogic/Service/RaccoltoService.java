package BusinessLogic.Service;

import ORM.RaccoltoDAO;
import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public class RaccoltoService {
    private final RaccoltoDAO raccoltoDAO;

    public RaccoltoService(RaccoltoDAO raccoltoDAO) {
        this.raccoltoDAO = raccoltoDAO;
    }


    private void validaRaccolto(Raccolto raccolto) {
        if (raccolto == null) {
            throw new IllegalArgumentException("Raccolto non può essere null");
        }
        if (raccolto.getDataRaccolto() == null) {
            throw new IllegalArgumentException("La data di raccolto è obbligatoria");
        }
        if (raccolto.getDataRaccolto().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La data di raccolto non può essere nel futuro");
        }
        if (raccolto.getQuantitaKg() == null || raccolto.getQuantitaKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La quantità deve essere positiva");
        }
        if (raccolto.getPiantagioneId() == null) {
            throw new IllegalArgumentException("La piantagione è obbligatoria");
        }
    }

    public void aggiungiRaccolto(Raccolto raccolto) {
        validaRaccolto(raccolto);
        try {
            raccoltoDAO.create(raccolto);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante il salvataggio del raccolto: " + e.getMessage(), e);
        }
    }

    public void aggiornaRaccolto(Raccolto raccolto) {
        validaRaccolto(raccolto);
        if (raccolto.getId() == null) {
            throw new IllegalArgumentException("ID raccolto richiesto per l'aggiornamento");
        }
        try {
            raccoltoDAO.update(raccolto);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'aggiornamento del raccolto: " + e.getMessage(), e);
        }
    }

    public void eliminaRaccolto(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID raccolto richiesto per l'eliminazione");
        }
        try {
            raccoltoDAO.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'eliminazione del raccolto: " + e.getMessage(), e);
        }
    }

    public Raccolto getRaccoltoById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID raccolto richiesto");
        }
        try {
            return raccoltoDAO.read(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la lettura del raccolto: " + e.getMessage(), e);
        }
    }

    public List<Raccolto> getAllRaccolti() {
        try {
            return raccoltoDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la lettura dei raccolti: " + e.getMessage(), e);
        }
    }


    public List<Raccolto> getRaccoltiByPiantagione(Integer piantagioneId) {
        if (piantagioneId == null) {
            throw new IllegalArgumentException("ID piantagione richiesto");
        }
        try {
            return getAllRaccolti().stream()
                    .filter(r -> r.getPiantagioneId().equals(piantagioneId))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca dei raccolti per piantagione: " + e.getMessage(), e);
        }
    }


    public List<Raccolto> getRaccoltiByPeriodo(LocalDate dataInizio, LocalDate dataFine) {
        if (dataInizio == null || dataFine == null) {
            throw new IllegalArgumentException("Date di inizio e fine richieste");
        }
        if (dataInizio.isAfter(dataFine)) {
            throw new IllegalArgumentException("La data di inizio deve essere precedente alla data di fine");
        }
        try {
            return getAllRaccolti().stream()
                    .filter(r -> !r.getDataRaccolto().isBefore(dataInizio) && !r.getDataRaccolto().isAfter(dataFine))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca dei raccolti per periodo: " + e.getMessage(), e);
        }
    }
}
