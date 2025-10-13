package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import BusinessLogic.Exception.DataAccessException;
import ORM.ZonaDAO;
import DomainModel.Zona;
import java.sql.SQLException;
import java.util.List;

public class ZonaService {
    private final ZonaDAO zonaDAO;

    public ZonaService(ZonaDAO zonaDAO) {
        this.zonaDAO = zonaDAO;
    }


    private void validaZona(Zona zona) throws ValidationException {
        if (zona == null) {
            throw new ValidationException("Zona non può essere null");
        }

        if (zona.getNome() == null || zona.getNome().trim().isEmpty()) {
            throw ValidationException.requiredField("Nome della zona");
        }

        if (zona.getDimensione() <= 0) {
            throw new ValidationException("dimensione", zona.getDimensione().toString(),
                    "deve essere maggiore di zero");
        }

        if (zona.getTipoTerreno() == null || zona.getTipoTerreno().trim().isEmpty()) {
            throw ValidationException.requiredField("Tipo di terreno");
        }
    }

    public void aggiungiZona(Zona zona) throws ValidationException, DataAccessException, BusinessLogicException {
        validaZona(zona);

        try {
            // Verifica duplicati per nome zona
            List<Zona> esistenti = zonaDAO.findAll();
            boolean duplicato = esistenti.stream()
                    .anyMatch(z -> z.getNome().equalsIgnoreCase(zona.getNome()));

            if (duplicato) {
                throw BusinessLogicException.duplicateEntry("Zona", "nome", zona.getNome());
            }

            zonaDAO.create(zona);
        } catch (SQLException e) {
            throw DataAccessException.queryError("inserimento zona", e);
        }
    }

    public void aggiornaZona(Zona zona) throws ValidationException, DataAccessException, BusinessLogicException {
        validaZona(zona);

        if (zona.getId() == null) {
            throw ValidationException.requiredField("ID zona per aggiornamento");
        }

        try {
            // Verifica che la zona esista
            Zona esistente = zonaDAO.read(zona.getId());
            if (esistente == null) {
                throw BusinessLogicException.entityNotFound("Zona", zona.getId());
            }

            zonaDAO.update(zona);
        } catch (SQLException e) {
            throw DataAccessException.queryError("aggiornamento zona", e);
        }
    }

    public void eliminaZona(Integer id) throws ValidationException, DataAccessException, BusinessLogicException {
        if (id == null) {
            throw ValidationException.requiredField("ID zona per eliminazione");
        }

        try {
            // Verifica che la zona esista
            Zona esistente = zonaDAO.read(id);
            if (esistente == null) {
                throw BusinessLogicException.entityNotFound("Zona", id);
            }

            zonaDAO.delete(id);
        } catch (SQLException e) {
            throw DataAccessException.queryError("eliminazione zona", e);
        }
    }

    public Zona getZonaById(Integer id) throws ValidationException, DataAccessException {
        if (id == null) {
            throw ValidationException.requiredField("ID zona");
        }

        try {
            return zonaDAO.read(id);
        } catch (SQLException e) {
            throw DataAccessException.queryError("lettura zona", e);
        }
    }

    public List<Zona> getAllZone() throws DataAccessException {
        try {
            return zonaDAO.findAll();
        } catch (SQLException e) {
            throw DataAccessException.queryError("lettura lista zone", e);
        }
    }

    // Metodo per filtri - richiesto dal controller
    public List<Zona> getZoneConFiltri(View.ZonaView.CriteriFiltro criteriFiltro) throws DataAccessException {
        try {
            var tutteZone = zonaDAO.findAll();

            return tutteZone.stream()
                .filter(z -> {
                    boolean matchNome = criteriFiltro.nome() == null || criteriFiltro.nome().isEmpty() ||
                                       z.getNome().toLowerCase().contains(criteriFiltro.nome().toLowerCase());
                    boolean matchTipoTerreno = criteriFiltro.tipoTerreno() == null || criteriFiltro.tipoTerreno().isEmpty() ||
                                              z.getTipoTerreno().toLowerCase().contains(criteriFiltro.tipoTerreno().toLowerCase());
                    return matchNome && matchTipoTerreno;
                })
                .toList();
        } catch (SQLException e) {
            throw DataAccessException.queryError("applicazione filtri zone", e);
        }
    }
}
