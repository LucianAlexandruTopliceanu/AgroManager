package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import BusinessLogic.Exception.DataAccessException;
import ORM.PiantagioneDAO;
import DomainModel.Piantagione;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;


public class PiantagioneService {
    private final PiantagioneDAO piantagioneDAO;

    public PiantagioneService(PiantagioneDAO piantagioneDAO) {
        this.piantagioneDAO = piantagioneDAO;
    }


    private void validaPiantagione(Piantagione piantagione) throws ValidationException {
        if (piantagione == null) {
            throw new ValidationException("Piantagione non può essere null");
        }

        if (piantagione.getQuantitaPianta() == null || piantagione.getQuantitaPianta() <= 0) {
            throw new ValidationException("quantitaPianta",
                    piantagione.getQuantitaPianta() != null ? piantagione.getQuantitaPianta().toString() : "null",
                    "deve essere maggiore di zero");
        }

        if (piantagione.getMessaADimora() == null) {
            throw ValidationException.requiredField("Data di messa a dimora");
        }

        // Validazione logica della data
        if (piantagione.getMessaADimora().isAfter(LocalDate.now())) {
            throw new ValidationException("messaADimora", piantagione.getMessaADimora().toString(),
                    "non può essere nel futuro");
        }

        if (piantagione.getPiantaId() == null) {
            throw ValidationException.requiredField("Pianta");
        }

        if (piantagione.getZonaId() == null) {
            throw ValidationException.requiredField("Zona");
        }
    }

    public void aggiungiPiantagione(Piantagione piantagione) throws ValidationException, DataAccessException, BusinessLogicException {
        validaPiantagione(piantagione);

        try {
            // Verifica che non ci siano troppe piantagioni nella stessa zona
            List<Piantagione> piantagioniZona = piantagioneDAO.findByZona(piantagione.getZonaId());
            int totaleQuantita = piantagioniZona.stream()
                    .mapToInt(Piantagione::getQuantitaPianta)
                    .sum();

            // Assumiamo un limite massimo per zona (es. 1000 piante)
            if (totaleQuantita + piantagione.getQuantitaPianta() > 1000) {
                throw new BusinessLogicException("Superato il limite massimo di piante per la zona",
                        "La zona ha raggiunto la capacità massima");
            }

            piantagioneDAO.create(piantagione);
        } catch (SQLException e) {
            throw DataAccessException.queryError("inserimento piantagione", e);
        }
    }

    public void aggiornaPiantagione(Piantagione piantagione) throws ValidationException, DataAccessException, BusinessLogicException {
        validaPiantagione(piantagione);

        if (piantagione.getId() == null) {
            throw ValidationException.requiredField("ID piantagione per aggiornamento");
        }

        try {
            // Verifica che la piantagione esista
            Piantagione esistente = piantagioneDAO.read(piantagione.getId());
            if (esistente == null) {
                throw BusinessLogicException.entityNotFound("Piantagione", piantagione.getId());
            }

            piantagioneDAO.update(piantagione);
        } catch (SQLException e) {
            throw DataAccessException.queryError("aggiornamento piantagione", e);
        }
    }

    public void eliminaPiantagione(Integer id) throws ValidationException, DataAccessException, BusinessLogicException {
        if (id == null) {
            throw ValidationException.requiredField("ID piantagione per eliminazione");
        }

        try {
            // Verifica che la piantagione esista
            Piantagione esistente = piantagioneDAO.read(id);
            if (esistente == null) {
                throw BusinessLogicException.entityNotFound("Piantagione", id);
            }

            piantagioneDAO.delete(id);
        } catch (SQLException e) {
            throw DataAccessException.queryError("eliminazione piantagione", e);
        }
    }

    public Piantagione getPiantagioneById(Integer id) throws ValidationException, DataAccessException {
        if (id == null) {
            throw ValidationException.requiredField("ID piantagione");
        }

        try {
            return piantagioneDAO.read(id);
        } catch (SQLException e) {
            throw DataAccessException.queryError("lettura piantagione", e);
        }
    }

    public List<Piantagione> getAllPiantagioni() throws DataAccessException {
        try {
            return piantagioneDAO.findAll();
        } catch (SQLException e) {
            throw DataAccessException.queryError("lettura lista piantagioni", e);
        }
    }

    public List<Piantagione> getPiantagioniByZona(Integer zonaId) throws ValidationException, DataAccessException {
        if (zonaId == null) {
            throw ValidationException.requiredField("ID zona");
        }

        try {
            return piantagioneDAO.findByZona(zonaId);
        } catch (SQLException e) {
            throw DataAccessException.queryError("lettura piantagioni per zona", e);
        }
    }

    public List<Piantagione> getPiantagioniByPianta(Integer piantaId) throws ValidationException, DataAccessException {
        if (piantaId == null) {
            throw ValidationException.requiredField("ID pianta");
        }

        try {
            return piantagioneDAO.findByPianta(piantaId);
        } catch (SQLException e) {
            throw DataAccessException.queryError("lettura piantagioni per pianta", e);
        }
    }
}
