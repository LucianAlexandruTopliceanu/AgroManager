package Controller;

import BusinessLogic.BusinessLogic;
import BusinessLogic.Strategy.DataProcessingStrategy;
import View.DataProcessingView;
import DomainModel.*;
import ORM.DAOFactory;
import java.util.List;

public class DataProcessingController {
    private final DataProcessingView view;
    private final BusinessLogic businessLogic;

    public DataProcessingController(DataProcessingView view, BusinessLogic businessLogic) {
        this.view = view;
        this.businessLogic = businessLogic;

        // Configura i callback
        view.setOnEseguiElaborazioneListener(this::eseguiElaborazione);
        view.setOnAggiornaDati(this::aggiornaDati);
    }

    private String eseguiElaborazione() {
        try {
            // Ottieni i parametri dalla view
            DataProcessingStrategy.ProcessingType tipo = view.getTipoElaborazioneSelezionato();
            String strategia = view.getStrategiaSelezionata();

            // Ottieni i dati necessari
            List<Raccolto> raccolti = DAOFactory.getRaccoltoDAO().findAll();

            if (raccolti.isEmpty()) {
                return "⚠️ Nessun dato trovato nel database. Assicurati che ci siano raccolti registrati.";
            }

            // Esegui la strategia appropriata
            return businessLogic.eseguiStrategia(
                tipo,
                strategia,
                view.getPiantagioneId(),
                view.getDataInizio(),
                view.getDataFine(),
                view.getTopN()
            );

        } catch (NumberFormatException e) {
            return "❌ Errore: Inserire un numero valido per l'ID piantagione.";
        } catch (Exception e) {
            return "❌ Errore durante l'elaborazione: " + e.getMessage();
        }
    }

    private void aggiornaDati() {
        try {
            // Ricarica i dati dal database
            DAOFactory.getRaccoltoDAO().findAll();
            DAOFactory.getPiantagioneDAO().findAll();
            DAOFactory.getZonaDAO().findAll();
            DAOFactory.getPiantaDAO().findAll();
            view.setRisultato("✅ Dati aggiornati con successo");
        } catch (Exception e) {
            view.setRisultato("❌ Errore durante l'aggiornamento: " + e.getMessage());
        }
    }
}
