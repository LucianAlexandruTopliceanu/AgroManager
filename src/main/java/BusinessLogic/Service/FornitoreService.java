package BusinessLogic.Service;

import ORM.FornitoreDAO;
import DomainModel.Fornitore;
import java.util.List;
import java.util.regex.Pattern;


public class FornitoreService {
    private final FornitoreDAO fornitoreDAO;
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public FornitoreService(FornitoreDAO fornitoreDAO) {
        this.fornitoreDAO = fornitoreDAO;
    }


    private void validaFornitore(Fornitore fornitore) {
        if (fornitore == null) {
            throw new IllegalArgumentException("Fornitore non può essere null");
        }
        if (fornitore.getNome() == null || fornitore.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del fornitore è obbligatorio");
        }
        if (fornitore.getIndirizzo() == null || fornitore.getIndirizzo().trim().isEmpty()) {
            throw new IllegalArgumentException("L'indirizzo è obbligatorio");
        }
        if (fornitore.getEmail() != null && !fornitore.getEmail().trim().isEmpty()
            && !EMAIL_PATTERN.matcher(fornitore.getEmail()).matches()) {
            throw new IllegalArgumentException("Formato email non valido");
        }
        if (fornitore.getNumeroTelefono() != null && !fornitore.getNumeroTelefono().trim().isEmpty()
            && fornitore.getNumeroTelefono().trim().length() < 8) {
            throw new IllegalArgumentException("Numero di telefono troppo corto");
        }
    }

    public void aggiungiFornitore(Fornitore fornitore) {
        validaFornitore(fornitore);
        try {
            fornitoreDAO.create(fornitore);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante il salvataggio del fornitore: " + e.getMessage(), e);
        }
    }

    public void aggiornaFornitore(Fornitore fornitore) {
        validaFornitore(fornitore);
        if (fornitore.getId() == null) {
            throw new IllegalArgumentException("ID fornitore richiesto per l'aggiornamento");
        }
        try {
            fornitoreDAO.update(fornitore);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'aggiornamento del fornitore: " + e.getMessage(), e);
        }
    }

    public void eliminaFornitore(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID fornitore richiesto per l'eliminazione");
        }
        try {
            fornitoreDAO.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'eliminazione del fornitore: " + e.getMessage(), e);
        }
    }

    public Fornitore getFornitoreById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID fornitore richiesto");
        }
        try {
            return fornitoreDAO.read(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la lettura del fornitore: " + e.getMessage(), e);
        }
    }

    public List<Fornitore> getAllFornitori() {
        try {
            return fornitoreDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la lettura dei fornitori: " + e.getMessage(), e);
        }
    }


    public List<Fornitore> getFornitoriByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return getAllFornitori();
        }
        try {
            return getAllFornitori().stream()
                    .filter(f -> f.getNome().toLowerCase().contains(nome.toLowerCase().trim()))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca dei fornitori: " + e.getMessage(), e);
        }
    }
}
