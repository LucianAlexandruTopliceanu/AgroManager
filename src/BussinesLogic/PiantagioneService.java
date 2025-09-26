package BussinesLogic;

import ORM.PiantagioneDAO;
import DomainModel.Piantagione;
import java.util.List;

public class PiantagioneService {
    private final PiantagioneDAO piantagioneDAO;
    public PiantagioneService(PiantagioneDAO piantagioneDAO) {
        this.piantagioneDAO = piantagioneDAO;
    }
    private void validaPiantagione(Piantagione p) {
        if (p == null) throw new IllegalArgumentException("Piantagione non può essere null");
        if (p.getZonaId() == null) throw new IllegalArgumentException("Zona obbligatoria");
        if (p.getPiantaId() == null) throw new IllegalArgumentException("Pianta obbligatoria");
        // altre regole di validazione se necessario
    }
    public void aggiungiPiantagione(Piantagione p) {
        validaPiantagione(p);
        try {
            piantagioneDAO.create(p);
        } catch (Exception e) {
            throw new RuntimeException("Errore salvataggio piantagione", e);
        }
    }
    public void aggiornaPiantagione(Piantagione p) {
        validaPiantagione(p);
        try {
            piantagioneDAO.update(p);
        } catch (Exception e) {
            throw new RuntimeException("Errore aggiornamento piantagione", e);
        }
    }
    public void eliminaPiantagione(int id) {
        try {
            piantagioneDAO.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore eliminazione piantagione", e);
        }
    }
    public Piantagione getPiantagione(int id) {
        try {
            return piantagioneDAO.read(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore lettura piantagione", e);
        }
    }
    public List<Piantagione> getAllPiantagioni() {
        try {
            return piantagioneDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Errore lettura piantagioni", e);
        }
    }
}
