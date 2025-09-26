package BussinesLogic;

import ORM.PiantaDAO;
import DomainModel.Pianta;
import java.util.List;

public class PiantaService {
    private final PiantaDAO piantaDAO;
    public PiantaService(PiantaDAO piantaDAO) {
        this.piantaDAO = piantaDAO;
    }
    private void validaPianta(Pianta p) {
        if (p == null) throw new IllegalArgumentException("Pianta non può essere null");
        if (p.getTipo() == null || p.getTipo().isBlank()) throw new IllegalArgumentException("Il tipo di pianta è obbligatorio");
        // altre regole di validazione se necessario
    }
    public void aggiungiPianta(Pianta p) {
        validaPianta(p);
        try {
            piantaDAO.create(p);
        } catch (Exception e) {
            throw new RuntimeException("Errore salvataggio pianta", e);
        }
    }
    public void aggiornaPianta(Pianta p) {
        validaPianta(p);
        try {
            piantaDAO.update(p);
        } catch (Exception e) {
            throw new RuntimeException("Errore aggiornamento pianta", e);
        }
    }
    public void eliminaPianta(int id) {
        try {
            piantaDAO.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore eliminazione pianta", e);
        }
    }
    public Pianta getPianta(int id) {
        try {
            return piantaDAO.read(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore lettura pianta", e);
        }
    }
    public List<Pianta> getAllPiante() {
        try {
            return piantaDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Errore lettura piante", e);
        }
    }
}
