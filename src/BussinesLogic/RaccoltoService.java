package BussinesLogic;

import ORM.RaccoltoDAO;
import DomainModel.Raccolto;
import java.util.List;

public class RaccoltoService {
    private final RaccoltoDAO raccoltoDAO;
    public RaccoltoService(RaccoltoDAO raccoltoDAO) {
        this.raccoltoDAO = raccoltoDAO;
    }
    private void validaRaccolto(Raccolto r) {
        if (r == null) throw new IllegalArgumentException("Raccolto non può essere null");
        if (r.getPiantagioneId() == null) throw new IllegalArgumentException("Piantagione obbligatoria");
        if (r.getQuantitaKg() == null || r.getQuantitaKg().signum() <= 0) throw new IllegalArgumentException("Quantità raccolta deve essere positiva");
        // altre regole di validazione se necessario
    }
    public void aggiungiRaccolto(Raccolto r) {
        validaRaccolto(r);
        try {
            raccoltoDAO.create(r);
        } catch (Exception e) {
            throw new RuntimeException("Errore salvataggio raccolto", e);
        }
    }
    public void aggiornaRaccolto(Raccolto r) {
        validaRaccolto(r);
        try {
            raccoltoDAO.update(r);
        } catch (Exception e) {
            throw new RuntimeException("Errore aggiornamento raccolto", e);
        }
    }
    public void eliminaRaccolto(int id) {
        try {
            raccoltoDAO.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore eliminazione raccolto", e);
        }
    }
    public Raccolto getRaccolto(int id) {
        try {
            return raccoltoDAO.read(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore lettura raccolto", e);
        }
    }
    public List<Raccolto> getAllRaccolti() {
        try {
            return raccoltoDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Errore lettura raccolti", e);
        }
    }
}
