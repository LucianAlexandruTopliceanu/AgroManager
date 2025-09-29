package BusinessLogic.Service;

import ORM.PiantaDAO;
import DomainModel.Pianta;
import java.math.BigDecimal;
import java.util.List;


public class PiantaService {
    private final PiantaDAO piantaDAO;

    public PiantaService(PiantaDAO piantaDAO) {
        this.piantaDAO = piantaDAO;
    }


    private void validaPianta(Pianta pianta) {
        if (pianta == null) {
            throw new IllegalArgumentException("Pianta non può essere null");
        }
        if (pianta.getTipo() == null || pianta.getTipo().trim().isEmpty()) {
            throw new IllegalArgumentException("Il tipo di pianta è obbligatorio");
        }
        if (pianta.getVarieta() == null || pianta.getVarieta().trim().isEmpty()) {
            throw new IllegalArgumentException("La varietà è obbligatoria");
        }
        if (pianta.getCosto() != null && pianta.getCosto().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Il costo non può essere negativo");
        }
        if (pianta.getFornitoreId() == null) {
            throw new IllegalArgumentException("Il fornitore è obbligatorio");
        }
    }

    public void aggiungiPianta(Pianta pianta) {
        validaPianta(pianta);
        try {
            piantaDAO.create(pianta);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante il salvataggio della pianta: " + e.getMessage(), e);
        }
    }

    public void aggiornaPianta(Pianta pianta) {
        validaPianta(pianta);
        if (pianta.getId() == null) {
            throw new IllegalArgumentException("ID pianta richiesto per l'aggiornamento");
        }
        try {
            piantaDAO.update(pianta);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'aggiornamento della pianta: " + e.getMessage(), e);
        }
    }

    public void eliminaPianta(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID pianta richiesto per l'eliminazione");
        }
        try {
            piantaDAO.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'eliminazione della pianta: " + e.getMessage(), e);
        }
    }

    public Pianta getPiantaById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID pianta richiesto");
        }
        try {
            return piantaDAO.read(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la lettura della pianta: " + e.getMessage(), e);
        }
    }

    public List<Pianta> getAllPiante() {
        try {
            return piantaDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la lettura delle piante: " + e.getMessage(), e);
        }
    }


    public List<Pianta> getPianteByTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            return getAllPiante();
        }
        try {
            return getAllPiante().stream()
                    .filter(p -> p.getTipo().toLowerCase().contains(tipo.toLowerCase().trim()))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca delle piante: " + e.getMessage(), e);
        }
    }


    public List<Pianta> getPianteByFornitore(Integer fornitoreId) {
        if (fornitoreId == null) {
            throw new IllegalArgumentException("ID fornitore richiesto");
        }
        try {
            return getAllPiante().stream()
                    .filter(p -> p.getFornitoreId().equals(fornitoreId))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca delle piante per fornitore: " + e.getMessage(), e);
        }
    }
}
