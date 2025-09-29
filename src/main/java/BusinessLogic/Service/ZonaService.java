package BusinessLogic.Service;

import ORM.ZonaDAO;
import DomainModel.Zona;
import java.util.List;


public class ZonaService {
    private final ZonaDAO zonaDAO;

    public ZonaService(ZonaDAO zonaDAO) {
        this.zonaDAO = zonaDAO;
    }


    private void validaZona(Zona zona) {
        if (zona == null) {
            throw new IllegalArgumentException("Zona non può essere null");
        }
        if (zona.getNome() == null || zona.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della zona è obbligatorio");
        }
        if (zona.getDimensione() <= 0) {
            throw new IllegalArgumentException("La dimensione deve essere positiva");
        }
        if (zona.getTipoTerreno() == null || zona.getTipoTerreno().trim().isEmpty()) {
            throw new IllegalArgumentException("Il tipo di terreno è obbligatorio");
        }
    }

    public void aggiungiZona(Zona zona) {
        validaZona(zona);
        try {
            zonaDAO.create(zona);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante il salvataggio della zona: " + e.getMessage(), e);
        }
    }

    public void aggiornaZona(Zona zona) {
        validaZona(zona);
        if (zona.getId() == null) {
            throw new IllegalArgumentException("ID zona richiesto per l'aggiornamento");
        }
        try {
            zonaDAO.update(zona);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'aggiornamento della zona: " + e.getMessage(), e);
        }
    }

    public void eliminaZona(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID zona richiesto per l'eliminazione");
        }
        try {
            zonaDAO.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'eliminazione della zona: " + e.getMessage(), e);
        }
    }

    public Zona getZonaById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID zona richiesto");
        }
        try {
            return zonaDAO.read(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la lettura della zona: " + e.getMessage(), e);
        }
    }

    public List<Zona> getAllZone() {
        try {
            return zonaDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la lettura delle zone: " + e.getMessage(), e);
        }
    }


    public List<Zona> getZoneByTipoTerreno(String tipoTerreno) {
        if (tipoTerreno == null || tipoTerreno.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo terreno richiesto");
        }
        try {
            return getAllZone().stream()
                    .filter(z -> z.getTipoTerreno().equalsIgnoreCase(tipoTerreno.trim()))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la ricerca delle zone: " + e.getMessage(), e);
        }
    }
}
