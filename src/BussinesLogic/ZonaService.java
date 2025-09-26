package BussinesLogic;

import ORM.ZonaDAO;
import DomainModel.Zona;
import java.util.List;

public class ZonaService {
    private final ZonaDAO zonaDAO;
    public ZonaService(ZonaDAO zonaDAO) {
        this.zonaDAO = zonaDAO;
    }
    private void validaZona(Zona z) {
        if (z == null) throw new IllegalArgumentException("Zona non può essere null");
        if (z.getNome() == null || z.getNome().isBlank()) throw new IllegalArgumentException("Il nome della zona è obbligatorio");
        // aggiungi altre regole di validazione qui se necessario
    }
    public void aggiungiZona(Zona z) {
        validaZona(z);
        try {
            zonaDAO.create(z);
        } catch (Exception e) {
            throw new RuntimeException("Errore salvataggio zona", e);
        }
    }
    public void aggiornaZona(Zona z) {
        validaZona(z);
        try {
            zonaDAO.update(z);
        } catch (Exception e) {
            throw new RuntimeException("Errore aggiornamento zona", e);
        }
    }
    public void eliminaZona(int id) {
        try {
            zonaDAO.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore eliminazione zona", e);
        }
    }
    public Zona getZona(int id) {
        try {
            return zonaDAO.read(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore lettura zona", e);
        }
    }
    public List<Zona> getAllZone() {
        try {
            return zonaDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Errore lettura zone", e);
        }
    }
}
