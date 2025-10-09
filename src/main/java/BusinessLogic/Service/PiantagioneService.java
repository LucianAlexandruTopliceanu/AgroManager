package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import BusinessLogic.Exception.DataAccessException;
import ORM.PiantagioneDAO;
import ORM.DAOFactory;
import DomainModel.Piantagione;
import DomainModel.StatoPiantagione;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public class PiantagioneService {
    private final PiantagioneDAO piantagioneDAO;
    private final StatoPiantagioneService statoPiantagioneService;

    public PiantagioneService(PiantagioneDAO piantagioneDAO) {
        this.piantagioneDAO = piantagioneDAO;
        this.statoPiantagioneService = new StatoPiantagioneService(DAOFactory.getStatoPiantagioneDAO());
    }

    public PiantagioneService(PiantagioneDAO piantagioneDAO, StatoPiantagioneService statoPiantagioneService) {
        this.piantagioneDAO = piantagioneDAO;
        this.statoPiantagioneService = statoPiantagioneService;
    }

    private void validaPiantagione(Piantagione piantagione) throws ValidationException, BusinessLogicException {
        if (piantagione == null) {
            throw ValidationException.requiredField("Piantagione");
        }

        if (piantagione.getQuantitaPianta() == null || piantagione.getQuantitaPianta() <= 0) {
            throw new ValidationException("quantitaPianta",
                piantagione.getQuantitaPianta() != null ? piantagione.getQuantitaPianta().toString() : "null",
                "deve essere positiva");
        }

        if (piantagione.getMessaADimora() == null) {
            throw ValidationException.requiredField("Data messa a dimora");
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

        // NUOVO: Validazione stato piantagione
        if (piantagione.getIdStatoPiantagione() != null) {
            try {
                StatoPiantagione stato = statoPiantagioneService.getStatoById(piantagione.getIdStatoPiantagione());
                if (stato == null) {
                    throw new ValidationException("Stato piantagione non valido: " + piantagione.getIdStatoPiantagione());
                }
            } catch (BusinessLogicException e) {
                throw new ValidationException("Stato piantagione non valido: " + piantagione.getIdStatoPiantagione());
            }
        }
    }

    public void aggiungiPiantagione(Piantagione piantagione) throws ValidationException, DataAccessException, BusinessLogicException {
        validaPiantagione(piantagione);

        // NUOVO: Assicura che abbia uno stato valido (default: ATTIVA)
        if (piantagione.getIdStatoPiantagione() == null) {
            StatoPiantagione statoAttiva = statoPiantagioneService.getStatoByCodice(StatoPiantagione.ATTIVA);
            piantagione.setIdStatoPiantagione(statoAttiva.getId());
            piantagione.setStatoPiantagione(statoAttiva);
        }

        // Imposta timestamp
        piantagione.setDataCreazione(LocalDateTime.now());
        piantagione.setDataAggiornamento(LocalDateTime.now());

        try {
            piantagioneDAO.create(piantagione);
        } catch (Exception e) {
            throw DataAccessException.queryError("creazione piantagione", e);
        }
    }

    public void aggiornaPiantagione(Piantagione piantagione) throws ValidationException, DataAccessException, BusinessLogicException {
        if (piantagione.getId() == null) {
            throw ValidationException.requiredField("ID piantagione per aggiornamento");
        }

        validaPiantagione(piantagione);
        piantagione.setDataAggiornamento(LocalDateTime.now());

        try {
            piantagioneDAO.update(piantagione);
        } catch (Exception e) {
            throw DataAccessException.queryError("aggiornamento piantagione", e);
        }
    }

    // NUOVI METODI per gestione stati

    /**
     * Cambia lo stato di una piantagione
     */
    public void cambiaStatoPiantagione(Integer piantagioneId, String codiceStat) throws ValidationException, DataAccessException, BusinessLogicException {
        if (piantagioneId == null) {
            throw ValidationException.requiredField("ID piantagione");
        }

        StatoPiantagione nuovoStato = statoPiantagioneService.getStatoByCodice(codiceStat);

        try {
            piantagioneDAO.cambiaStato(piantagioneId, nuovoStato.getId());
        } catch (Exception e) {
            throw DataAccessException.queryError("cambio stato piantagione", e);
        }
    }

    /**
     * Rimuove una piantagione (cambia stato a RIMOSSA)
     */
    public void rimuoviPiantagione(Integer piantagioneId) throws ValidationException, DataAccessException, BusinessLogicException {
        cambiaStatoPiantagione(piantagioneId, StatoPiantagione.RIMOSSA);
    }

    /**
     * Completa una piantagione
     */
    public void completaPiantagione(Integer piantagioneId) throws ValidationException, DataAccessException, BusinessLogicException {
        cambiaStatoPiantagione(piantagioneId, StatoPiantagione.COMPLETATA);
    }

    /**
     * Riattiva una piantagione
     */
    public void riattivaPiantagione(Integer piantagioneId) throws ValidationException, DataAccessException, BusinessLogicException {
        cambiaStatoPiantagione(piantagioneId, StatoPiantagione.ATTIVA);
    }

    /**
     * Ottiene tutte le piantagioni con i loro stati
     */
    public List<Piantagione> getTutteLePiantagioniConStato() {
        return piantagioneDAO.findAllWithStato();
    }

    /**
     * Ottiene solo le piantagioni attive
     */
    public List<Piantagione> getPiantagioniAttive() {
        return piantagioneDAO.findAttive();
    }

    /**
     * Ottiene piantagioni per stato
     */
    public List<Piantagione> getPiantagioniPerStato(String codiceStato) {
        return piantagioneDAO.findByStato(codiceStato);
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
            return piantagioneDAO.findAllWithStato();
        } catch (Exception e) {
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
