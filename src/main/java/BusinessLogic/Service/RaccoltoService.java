package BusinessLogic.Service;

import BusinessLogic.BusinessLogic;
import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import BusinessLogic.Exception.DataAccessException;
import DomainModel.Raccolto;
import ORM.RaccoltoDAO;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RaccoltoService {
    private final RaccoltoDAO raccoltoDAO;

    public RaccoltoService(RaccoltoDAO raccoltoDAO) {
        this.raccoltoDAO = raccoltoDAO;
    }

    private void validaRaccolto(Raccolto raccolto) throws ValidationException {
        if (raccolto == null) {
            throw new ValidationException("Raccolto non può essere null");
        }

        if (raccolto.getDataRaccolto() == null) {
            throw ValidationException.requiredField("Data di raccolto");
        }

        if (raccolto.getDataRaccolto().isAfter(LocalDate.now())) {
            throw new ValidationException("dataRaccolto", raccolto.getDataRaccolto().toString(),
                    "non può essere nel futuro");
        }

        if (raccolto.getQuantitaKg() == null || raccolto.getQuantitaKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("quantitaKg",
                    raccolto.getQuantitaKg() != null ? raccolto.getQuantitaKg().toString() : "null",
                    "deve essere maggiore di zero");
        }

        if (raccolto.getPiantagioneId() == null) {
            throw ValidationException.requiredField("Piantagione");
        }
    }

    public List<Raccolto> getAllRaccolti() throws DataAccessException {
        try {
            return raccoltoDAO.findAll();
        } catch (SQLException e) {
            throw DataAccessException.queryError("lettura lista raccolti", e);
        }
    }

    public void aggiungiRaccolto(Raccolto raccolto) throws ValidationException, DataAccessException, BusinessLogicException {
        validaRaccolto(raccolto);

        try {
            List<Raccolto> raccoltiEsistenti = raccoltoDAO.findByPiantagione(raccolto.getPiantagioneId());
            boolean raccoltoGiaEsistente = raccoltiEsistenti.stream()
                    .anyMatch(r -> r.getDataRaccolto().equals(raccolto.getDataRaccolto()));

            if (raccoltoGiaEsistente) {
                throw new BusinessLogicException("Raccolto già registrato per questa data",
                        "Esiste già un raccolto per questa piantagione nella data specificata");
            }

            raccoltoDAO.create(raccolto);
        } catch (SQLException e) {
            throw DataAccessException.queryError("inserimento raccolto", e);
        }
    }

    public void aggiornaRaccolto(Raccolto raccolto) throws ValidationException, DataAccessException, BusinessLogicException {
        validaRaccolto(raccolto);

        if (raccolto.getId() == null) {
            throw ValidationException.requiredField("ID raccolto per aggiornamento");
        }

        try {
            Raccolto esistente = raccoltoDAO.read(raccolto.getId());
            if (esistente == null) {
                throw BusinessLogicException.entityNotFound("Raccolto", raccolto.getId());
            }

            raccoltoDAO.update(raccolto);
        } catch (SQLException e) {
            throw DataAccessException.queryError("aggiornamento raccolto", e);
        }
    }

    public void eliminaRaccolto(Integer id) throws ValidationException, DataAccessException, BusinessLogicException {
        if (id == null) {
            throw ValidationException.requiredField("ID raccolto per eliminazione");
        }

        try {
            Raccolto esistente = raccoltoDAO.read(id);
            if (esistente == null) {
                throw BusinessLogicException.entityNotFound("Raccolto", id);
            }

            raccoltoDAO.delete(id);
        } catch (SQLException e) {
            throw DataAccessException.queryError("eliminazione raccolto", e);
        }
    }

    public Raccolto getRaccoltoById(Integer id) throws ValidationException, DataAccessException {
        if (id == null) {
            throw ValidationException.requiredField("ID raccolto");
        }

        try {
            return raccoltoDAO.read(id);
        } catch (SQLException e) {
            throw DataAccessException.queryError("lettura raccolto", e);
        }
    }

    public List<Raccolto> getRaccoltiByPiantagione(Integer piantagioneId) throws ValidationException, DataAccessException {
        if (piantagioneId == null) {
            throw ValidationException.requiredField("ID piantagione");
        }

        try {
            return raccoltoDAO.findByPiantagione(piantagioneId);
        } catch (SQLException e) {
            throw DataAccessException.queryError("lettura raccolti per piantagione", e);
        }
    }

    public List<Raccolto> getRaccoltiDelMese() throws DataAccessException {
        try {
            LocalDate inizioMese = LocalDate.now().withDayOfMonth(1);
            LocalDate fineMese = inizioMese.plusMonths(1).minusDays(1);

            return raccoltoDAO.findAll().stream()
                    .filter(r -> r.getDataRaccolto() != null)
                    .filter(r -> !r.getDataRaccolto().isBefore(inizioMese) &&
                               !r.getDataRaccolto().isAfter(fineMese))
                    .toList();
        } catch (SQLException e) {
            throw DataAccessException.queryError("lettura raccolti del mese", e);
        }
    }

    public BigDecimal getProduzioneTotale() throws DataAccessException {
        try {
            return raccoltoDAO.findAll().stream()
                    .filter(r -> r.getQuantitaKg() != null)
                    .map(Raccolto::getQuantitaKg)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (SQLException e) {
            throw DataAccessException.queryError("calcolo produzione totale", e);
        }
    }

    public List<Raccolto> getRaccoltiPerPeriodo(LocalDate dataInizio, LocalDate dataFine)
            throws ValidationException, DataAccessException {
        BusinessLogic.isDataRangeValid(dataInizio, dataFine);

        try {
            return raccoltoDAO.findAll().stream()
                    .filter(r -> r.getDataRaccolto() != null)
                    .filter(r -> !r.getDataRaccolto().isBefore(dataInizio) &&
                               !r.getDataRaccolto().isAfter(dataFine))
                    .toList();
        } catch (SQLException e) {
            throw DataAccessException.queryError("lettura raccolti per periodo", e);
        }
    }

    public List<Raccolto> getRaccoltiConFiltri(View.RaccoltoView.CriteriFiltro criteriFiltro) throws DataAccessException {
        try {
            var tuttiRaccolti = raccoltoDAO.findAll();

            return tuttiRaccolti.stream()
                .filter(r -> {
                    boolean matchPiantagione = true;
                    if (criteriFiltro.piantagione() != null && !criteriFiltro.piantagione().isEmpty()) {
                        // Estrae l'ID dalla descrizione "ID X - ..."
                        try {
                            String idStr = criteriFiltro.piantagione().replaceFirst("ID (\\d+).*", "$1");
                            Integer idPiantagione = Integer.parseInt(idStr);
                            matchPiantagione = r.getPiantagioneId() != null && r.getPiantagioneId().equals(idPiantagione);
                        } catch (NumberFormatException e) {
                            matchPiantagione = false;
                        }
                    }

                    boolean matchDataDa = criteriFiltro.dataDa() == null ||
                                         (r.getDataRaccolto() != null && !r.getDataRaccolto().isBefore(criteriFiltro.dataDa()));
                    boolean matchDataA = criteriFiltro.dataA() == null ||
                                        (r.getDataRaccolto() != null && !r.getDataRaccolto().isAfter(criteriFiltro.dataA()));
                    boolean matchQuantitaMin = criteriFiltro.quantitaMin() == null ||
                                              (r.getQuantitaKg() != null && r.getQuantitaKg().doubleValue() >= criteriFiltro.quantitaMin());
                    boolean matchQuantitaMax = criteriFiltro.quantitaMax() == null ||
                                              (r.getQuantitaKg() != null && r.getQuantitaKg().doubleValue() <= criteriFiltro.quantitaMax());
                    return matchPiantagione && matchDataDa && matchDataA && matchQuantitaMin && matchQuantitaMax;
                })
                .toList();
        } catch (SQLException e) {
            throw DataAccessException.queryError("applicazione filtri raccolti", e);
        }
    }
}
