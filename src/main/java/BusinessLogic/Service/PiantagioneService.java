package BusinessLogic.Service;

import ORM.PiantagioneDAO;
import DomainModel.Piantagione;
import java.util.List;


public class PiantagioneService {
    private final PiantagioneDAO piantagioneDAO;

    public PiantagioneService(PiantagioneDAO piantagioneDAO) {
        this.piantagioneDAO = piantagioneDAO;
    }


    private void validaPiantagione(Piantagione piantagione) {
        if (piantagione == null) {
            throw new IllegalArgumentException("Piantagione non può essere null");
        }
        if (piantagione.getQuantitaPianta() == null || piantagione.getQuantitaPianta() <= 0) {
            throw new IllegalArgumentException("La quantità deve essere positiva");
        }
        if (piantagione.getMessaADimora() == null) {
            throw new IllegalArgumentException("La data di messa a dimora è obbligatoria");
        }
        if (piantagione.getPiantaId() == null) {
            throw new IllegalArgumentException("La pianta è obbligatoria");
        }
        if (piantagione.getZonaId() == null) {
            throw new IllegalArgumentException("La zona è obbligatoria");
        }
    }

    public void aggiungiPiantagione(Piantagione piantagione) {
        validaPiantagione(piantagione);
        try {
            piantagioneDAO.create(piantagione);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante il salvataggio della piantagione: " + e.getMessage(), e);
        }
    }

    public void aggiornaPiantagione(Piantagione piantagione) {
        validaPiantagione(piantagione);
        if (piantagione.getId() == null) {
            throw new IllegalArgumentException("ID piantagione richiesto per l'aggiornamento");
        }
        try {
            piantagioneDAO.update(piantagione);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'aggiornamento della piantagione: " + e.getMessage(), e);
        }
    }

    public void eliminaPiantagione(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID piantagione richiesto per l'eliminazione");
        }
        try {
            piantagioneDAO.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'eliminazione della piantagione: " + e.getMessage(), e);
        }
    }

    public Piantagione getPiantagioneById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID piantagione richiesto");
        }
        try {
            return piantagioneDAO.read(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la lettura della piantagione: " + e.getMessage(), e);
        }
    }

    public List<Piantagione> getAllPiantagioni() {
        try {
            return piantagioneDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la lettura delle piantagioni: " + e.getMessage(), e);
        }
    }


    public List<Piantagione> getPiantagioniByZona(Integer zonaId) {
        if (zonaId == null) {
            throw new IllegalArgumentException("ID zona richiesto");
        }
        try {
            return getAllPiantagioni().stream()
                    .filter(p -> p.getZonaId().equals(zonaId))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca delle piantagioni per zona: " + e.getMessage(), e);
        }
    }


    public List<Piantagione> getPiantagioniByPianta(Integer piantaId) {
        if (piantaId == null) {
            throw new IllegalArgumentException("ID pianta richiesto");
        }
        try {
            return getAllPiantagioni().stream()
                    .filter(p -> p.getPiantaId().equals(piantaId))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca delle piantagioni per pianta: " + e.getMessage(), e);
        }
    }
}
