package BussinesLogic;

import ORM.FornitoreDAO;
import DomainModel.Fornitore;
import java.util.List;

public class FornitoreService {
    private final FornitoreDAO fornitoreDAO;
    public FornitoreService(FornitoreDAO fornitoreDAO) {
        this.fornitoreDAO = fornitoreDAO;
    }
    private void validaFornitore(Fornitore f) {
        if (f == null) throw new IllegalArgumentException("Fornitore non può essere null");
        if (f.getNome() == null || f.getNome().isBlank()) throw new IllegalArgumentException("Il nome del fornitore è obbligatorio");
        // altre regole di validazione se necessario
    }
    public void aggiungiFornitore(Fornitore f) {
        validaFornitore(f);
        try {
            fornitoreDAO.create(f);
        } catch (Exception e) {
            throw new RuntimeException("Errore salvataggio fornitore", e);
        }
    }
    public void aggiornaFornitore(Fornitore f) {
        validaFornitore(f);
        try {
            fornitoreDAO.update(f);
        } catch (Exception e) {
            throw new RuntimeException("Errore aggiornamento fornitore", e);
        }
    }
    public void eliminaFornitore(int id) {
        try {
            fornitoreDAO.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore eliminazione fornitore", e);
        }
    }
    public Fornitore getFornitore(int id) {
        try {
            return fornitoreDAO.read(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore lettura fornitore", e);
        }
    }
    public List<Fornitore> getAllFornitori() {
        try {
            return fornitoreDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Errore lettura fornitori", e);
        }
    }
}
