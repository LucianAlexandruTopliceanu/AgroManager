package View;

import DomainModel.Zona;
import java.util.List;

public class ZonaView {
    public void mostraZona(Zona zona) {
        System.out.println("--- Dettagli Zona ---");
        System.out.println("ID: " + zona.getId());
        System.out.println("Nome: " + zona.getNome());
        System.out.println("Dimensione: " + zona.getDimensione());
        System.out.println("Tipo terreno: " + zona.getTipoTerreno());
        System.out.println();
    }

    public void mostraZone(List<Zona> zone) {
        System.out.println("--- Elenco Zone ---");
        for (Zona zona : zone) {
            mostraZona(zona);
        }
    }
}

