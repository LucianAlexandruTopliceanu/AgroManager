package BussinesLogic;

import DomainModel.Zona;

public class ZonaService {
    public void validaZona(Zona zona) {
        if (zona.getNome() == null || zona.getNome().isEmpty()) {
            throw new IllegalArgumentException("Il nome della zona non può essere vuoto.");
        }
        if (zona.getDimensione() <= 0) {
            throw new IllegalArgumentException("La dimensione della zona deve essere positiva.");
        }
        if (zona.getTipoTerreno() == null || zona.getTipoTerreno().isEmpty()) {
            throw new IllegalArgumentException("Il tipo di terreno non può essere vuoto.");
        }
    }
}

