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

        // Validazione stato piantagione
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

        // Assicura che abbia uno stato valido (default: ATTIVA)
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

    public void cambiaStatoPiantagione(Integer piantagioneId, String codiceStato) throws ValidationException, DataAccessException, BusinessLogicException {
        if (piantagioneId == null) {
            throw ValidationException.requiredField("ID piantagione");
        }

        StatoPiantagione nuovoStato = statoPiantagioneService.getStatoByCodice(codiceStato);

        try {
            piantagioneDAO.cambiaStato(piantagioneId, nuovoStato.getId());
        } catch (Exception e) {
            throw DataAccessException.queryError("cambio stato piantagione", e);
        }
    }

    public List<Piantagione> getAllPiantagioni() throws DataAccessException {
        try {
            return piantagioneDAO.findAllWithStato();
        } catch (Exception e) {
            throw DataAccessException.queryError("lettura lista piantagioni", e);
        }
    }

    public Piantagione getPiantagioneById(Integer id) throws ValidationException, DataAccessException {
        if (id == null) {
            throw ValidationException.requiredField("ID piantagione");
        }

        try {
            return piantagioneDAO.read(id);
        } catch (Exception e) {
            throw DataAccessException.queryError("lettura piantagione", e);
        }
    }

    // Metodo per filtri - richiesto dal controller
    public List<Piantagione> getPiantagioniConFiltri(View.PiantagioneView.CriteriFiltro criteriFiltro) throws DataAccessException {
        try {
            var tuttePiantagioni = piantagioneDAO.findAll();

            return tuttePiantagioni.stream()
                .filter(p -> {
                    boolean matchPianta = criteriFiltro.pianta() == null || criteriFiltro.pianta().isEmpty() ||
                                         (p.getPiantaId() != null && p.getPiantaId().toString().equals(criteriFiltro.pianta()));
                    boolean matchZona = criteriFiltro.zona() == null || criteriFiltro.zona().isEmpty() ||
                                       (p.getZonaId() != null && p.getZonaId().toString().equals(criteriFiltro.zona()));
                    boolean matchStato = criteriFiltro.stato() == null || criteriFiltro.stato().isEmpty() ||
                                        (p.getStatoPiantagione() != null && p.getStatoPiantagione().getDescrizione().contains(criteriFiltro.stato()));
                    boolean matchData = true;
                    if (criteriFiltro.dataDa() != null && p.getMessaADimora() != null) {
                        matchData = !p.getMessaADimora().isBefore(criteriFiltro.dataDa());
                    }
                    if (criteriFiltro.dataA() != null && p.getMessaADimora() != null) {
                        matchData = matchData && !p.getMessaADimora().isAfter(criteriFiltro.dataA());
                    }
                    return matchPianta && matchZona && matchStato && matchData;
                })
                .toList();
        } catch (SQLException e) {
            throw DataAccessException.queryError("applicazione filtri piantagioni", e);
        }
    }
}
