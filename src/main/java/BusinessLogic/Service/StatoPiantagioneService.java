package BusinessLogic.Service;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import DomainModel.StatoPiantagione;
import ORM.StatoPiantagioneDAO;
import java.util.List;

/**
 * Service per la gestione degli stati delle piantagioni - MODALITÀ SOLA LETTURA
 * Gli stati sono pre-inseriti nel database e non possono essere modificati
 */
public class StatoPiantagioneService {
    private final StatoPiantagioneDAO statoPiantagioneDAO;

    public StatoPiantagioneService(StatoPiantagioneDAO statoPiantagioneDAO) {
        this.statoPiantagioneDAO = statoPiantagioneDAO;
    }

    /**
     * Recupera tutti gli stati disponibili
     */
    public List<StatoPiantagione> getAllStati() {
        return statoPiantagioneDAO.findAllOrdered();
    }

    /**
     * Trova uno stato per codice con validazione business
     */
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

    /**
     * Trova uno stato per ID con validazione business
     */
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

    /**
     * Verifica se uno stato esiste per codice
     */
    public boolean existsStatoByCodice(String codice) {
        if (codice == null || codice.trim().isEmpty()) {
            return false;
        }
        return statoPiantagioneDAO.existsByCodice(codice);
    }

    /**
     * Conta il numero totale di stati disponibili
     */
    public int countStatiDisponibili() {
        return statoPiantagioneDAO.countStati();
    }

    /**
     * Verifica che il sistema abbia tutti gli stati standard
     */
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

    /**
     * Ottiene lo stato di default per nuove piantagioni
     */
    public StatoPiantagione getStatoDefault() throws ValidationException, BusinessLogicException {
        return getStatoByCodice(StatoPiantagione.ATTIVA);
    }

    /**
     * Valida che uno stato sia utilizzabile per una piantagione
     */
    public boolean isStatoValido(String codice) {
        try {
            getStatoByCodice(codice);
            return true;
        } catch (ValidationException | BusinessLogicException e) {
            return false;
        }
    }

    /**
     * Ottiene la descrizione di uno stato senza lanciare eccezioni
     */
    public String getDescrizioneStato(String codice) {
        try {
            StatoPiantagione stato = getStatoByCodice(codice);
            return stato.getDescrizione();
        } catch (ValidationException | BusinessLogicException e) {
            return "Stato non trovato";
        }
    }
}
