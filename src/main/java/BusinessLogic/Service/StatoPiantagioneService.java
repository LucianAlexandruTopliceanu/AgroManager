package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import DomainModel.StatoPiantagione;
import ORM.StatoPiantagioneDAO;
import java.util.List;


public class StatoPiantagioneService {
    private final StatoPiantagioneDAO statoPiantagioneDAO;

    public StatoPiantagioneService(StatoPiantagioneDAO statoPiantagioneDAO) {
        this.statoPiantagioneDAO = statoPiantagioneDAO;
    }


    public List<StatoPiantagione> getAllStati() {
        return statoPiantagioneDAO.findAllOrdered();
    }


    public StatoPiantagione getStatoByCodice(String codice) throws ValidationException, BusinessLogicException {
        if (codice == null || codice.trim().isEmpty()) {
            throw ValidationException.requiredField("Codice stato");
        }

        StatoPiantagione stato = statoPiantagioneDAO.findByCodice(codice);
        if (stato == null) {
            throw BusinessLogicException.entityNotFound("StatoPiantagione", "codice: " + codice);
        }

        return stato;
    }


    public StatoPiantagione getStatoById(Integer id) throws ValidationException, BusinessLogicException {
        if (id == null) {
            throw ValidationException.requiredField("ID stato");
        }

        StatoPiantagione stato = statoPiantagioneDAO.findById(id);
        if (stato == null) {
            throw BusinessLogicException.entityNotFound("StatoPiantagione", id);
        }

        return stato;
    }


    public boolean existsStatoByCodice(String codice) {
        if (codice == null || codice.trim().isEmpty()) {
            return false;
        }
        return statoPiantagioneDAO.existsByCodice(codice);
    }


    public int countStatiDisponibili() {
        return statoPiantagioneDAO.countStati();
    }


    public boolean verificaStatiStandard() throws BusinessLogicException {
        List<String> statiStandard = List.of(
            StatoPiantagione.ATTIVA,
            StatoPiantagione.RIMOSSA,
            StatoPiantagione.IN_RACCOLTA,
            StatoPiantagione.COMPLETATA,
            StatoPiantagione.SOSPESA
        );

        for (String codice : statiStandard) {
            if (!existsStatoByCodice(codice)) {
                throw new BusinessLogicException(
                    "Stato standard mancante: " + codice,
                    "Sistema non configurato correttamente"
                );
            }
        }

        return true;
    }


    public StatoPiantagione getStatoDefault() throws ValidationException, BusinessLogicException {
        return getStatoByCodice(StatoPiantagione.ATTIVA);
    }


    public boolean isStatoValido(String codice) {
        try {
            getStatoByCodice(codice);
            return true;
        } catch (ValidationException | BusinessLogicException e) {
            return false;
        }
    }

    public String getDescrizioneStato(String codice) {
        try {
            StatoPiantagione stato = getStatoByCodice(codice);
            return stato.getDescrizione();
        } catch (ValidationException | BusinessLogicException e) {
            return "Stato non trovato";
        }
    }
}
