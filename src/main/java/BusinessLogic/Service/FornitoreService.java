package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import BusinessLogic.Exception.DataAccessException;
import ORM.FornitoreDAO;
import DomainModel.Fornitore;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;


public class FornitoreService {
    private final FornitoreDAO fornitoreDAO;
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public FornitoreService(FornitoreDAO fornitoreDAO) {
        this.fornitoreDAO = fornitoreDAO;
    }


    private void validaFornitore(Fornitore fornitore) throws ValidationException {
        if (fornitore == null) {
            throw new ValidationException("Fornitore non può essere null");
        }

        if (fornitore.getNome() == null || fornitore.getNome().trim().isEmpty()) {
            throw ValidationException.requiredField("Nome fornitore");
        }

        if (fornitore.getIndirizzo() == null || fornitore.getIndirizzo().trim().isEmpty()) {
            throw ValidationException.requiredField("Indirizzo");
        }

        if (fornitore.getEmail() != null && !fornitore.getEmail().trim().isEmpty()
            && !EMAIL_PATTERN.matcher(fornitore.getEmail()).matches()) {
            throw ValidationException.invalidFormat("Email", "formato@esempio.com");
        }

        if (fornitore.getNumeroTelefono() != null && !fornitore.getNumeroTelefono().trim().isEmpty()
            && fornitore.getNumeroTelefono().trim().length() < 8) {
            throw new ValidationException("numeroTelefono", fornitore.getNumeroTelefono(),
                    "deve contenere almeno 8 caratteri");
        }
    }

    public void aggiungiFornitore(Fornitore fornitore) throws ValidationException, DataAccessException, BusinessLogicException {
        validaFornitore(fornitore);

        try {
            // Verifica duplicati
            List<Fornitore> esistenti = fornitoreDAO.findAll();
            boolean duplicato = esistenti.stream()
                    .anyMatch(f -> f.getNome().equalsIgnoreCase(fornitore.getNome()) ||
                                  (f.getEmail() != null && fornitore.getEmail() != null &&
                                   f.getEmail().equalsIgnoreCase(fornitore.getEmail())));

            if (duplicato) {
                throw BusinessLogicException.duplicateEntry("Fornitore", "nome o email", fornitore.getNome());
            }

            fornitoreDAO.create(fornitore);
        } catch (SQLException e) {
            throw DataAccessException.queryError("inserimento fornitore", e);
        }
    }

    public void aggiornaFornitore(Fornitore fornitore) throws ValidationException, DataAccessException, BusinessLogicException {
        validaFornitore(fornitore);

        if (fornitore.getId() == null) {
            throw ValidationException.requiredField("ID fornitore per aggiornamento");
        }

        try {
            // Verifica che il fornitore esista
            Fornitore esistente = fornitoreDAO.read(fornitore.getId());
            if (esistente == null) {
                throw BusinessLogicException.entityNotFound("Fornitore", fornitore.getId());
            }

            fornitoreDAO.update(fornitore);
        } catch (SQLException e) {
            throw DataAccessException.queryError("aggiornamento fornitore", e);
        }
    }

    public void eliminaFornitore(Integer id) throws ValidationException, DataAccessException, BusinessLogicException {
        if (id == null) {
            throw ValidationException.requiredField("ID fornitore per eliminazione");
        }

        try {
            // Verifica che il fornitore esista
            Fornitore esistente = fornitoreDAO.read(id);
            if (esistente == null) {
                throw BusinessLogicException.entityNotFound("Fornitore", id);
            }

            fornitoreDAO.delete(id);
        } catch (SQLException e) {
            throw DataAccessException.queryError("eliminazione fornitore", e);
        }
    }

    public Fornitore getFornitoreById(Integer id) throws ValidationException, DataAccessException {
        if (id == null) {
            throw ValidationException.requiredField("ID fornitore");
        }

        try {
            return fornitoreDAO.read(id);
        } catch (SQLException e) {
            throw DataAccessException.queryError("lettura fornitore", e);
        }
    }

    public List<Fornitore> getAllFornitori() throws DataAccessException {
        try {
            return fornitoreDAO.findAll();
        } catch (SQLException e) {
            throw DataAccessException.queryError("lettura lista fornitori", e);
        }
    }
}
