package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import BusinessLogic.Exception.DataAccessException;
import ORM.PiantaDAO;
import DomainModel.Pianta;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class PiantaService {

    private final PiantaDAO piantaDAO;

    public PiantaService(PiantaDAO piantaDAO) {
        this.piantaDAO = piantaDAO;
    }

    /**
     * Valida una pianta utilizzando le nuove eccezioni custom
     */
    private void validaPianta(Pianta pianta) throws ValidationException {
        if (pianta == null) {
            throw new ValidationException("Pianta non può essere null");
        }

        if (pianta.getTipo() == null || pianta.getTipo().trim().isEmpty()) {
            throw ValidationException.requiredField("Tipo di pianta");
        }

        if (pianta.getVarieta() == null || pianta.getVarieta().trim().isEmpty()) {
            throw ValidationException.requiredField("Varietà");
        }

        if (pianta.getCosto() != null && pianta.getCosto().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("costo", pianta.getCosto().toString(),
                    "non può essere negativo");
        }

        if (pianta.getFornitoreId() == null) {
            throw ValidationException.requiredField("Fornitore");
        }
    }

    public void aggiungiPianta(Pianta pianta) throws ValidationException, DataAccessException, BusinessLogicException {
        validaPianta(pianta);

        try {
            // Verifica duplicati per tipo e varietà
            List<Pianta> esistenti = piantaDAO.findAll();
            boolean duplicato = esistenti.stream()
                    .anyMatch(p -> p.getTipo().equalsIgnoreCase(pianta.getTipo()) &&
                                  p.getVarieta().equalsIgnoreCase(pianta.getVarieta()) &&
                                  p.getFornitoreId().equals(pianta.getFornitoreId()));

            if (duplicato) {
                throw BusinessLogicException.duplicateEntry("Pianta",
                        "tipo e varietà", pianta.getTipo() + " - " + pianta.getVarieta());
            }

            piantaDAO.create(pianta);
        } catch (SQLException e) {
            throw DataAccessException.queryError("inserimento pianta", e);
        }
    }

    public void aggiornaPianta(Pianta pianta) throws ValidationException, DataAccessException, BusinessLogicException {
        validaPianta(pianta);

        if (pianta.getId() == null) {
            throw ValidationException.requiredField("ID pianta per aggiornamento");
        }

        try {
            // Verifica che la pianta esista
            Pianta esistente = piantaDAO.read(pianta.getId());
            if (esistente == null) {
                throw BusinessLogicException.entityNotFound("Pianta", pianta.getId());
            }

            piantaDAO.update(pianta);
        } catch (SQLException e) {
            throw DataAccessException.queryError("aggiornamento pianta", e);
        }
    }

    public void eliminaPianta(Integer id) throws ValidationException, DataAccessException, BusinessLogicException {
        if (id == null) {
            throw ValidationException.requiredField("ID pianta per eliminazione");
        }

        try {
            // Verifica che la pianta esista
            Pianta esistente = piantaDAO.read(id);
            if (esistente == null) {
                throw BusinessLogicException.entityNotFound("Pianta", id);
            }

            piantaDAO.delete(id);
        } catch (SQLException e) {
            throw DataAccessException.queryError("eliminazione pianta", e);
        }
    }

    public Pianta getPiantaById(Integer id) throws ValidationException, DataAccessException {
        if (id == null) {
            throw ValidationException.requiredField("ID pianta");
        }

        try {
            return piantaDAO.read(id);
        } catch (SQLException e) {
            throw DataAccessException.queryError("lettura pianta", e);
        }
    }

    public List<Pianta> getAllPiante() throws DataAccessException {
        try {
            return piantaDAO.findAll();
        } catch (SQLException e) {
            throw DataAccessException.queryError("lettura lista piante", e);
        }
    }

    public List<Pianta> getPianteByFornitore(Integer fornitoreId) throws ValidationException, DataAccessException {
        if (fornitoreId == null) {
            throw ValidationException.requiredField("ID fornitore");
        }

        try {
            return piantaDAO.findByFornitore(fornitoreId);
        } catch (SQLException e) {
            throw DataAccessException.queryError("lettura piante per fornitore", e);
        }
    }
}
