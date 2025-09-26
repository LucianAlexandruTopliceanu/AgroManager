package View;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ModernTableUtils {
    public static <T> void setupZonaTable(TableView<T> table) {
        TableColumn<T, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<T, String> nomeCol = new TableColumn<>("Nome");
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        TableColumn<T, Double> dimCol = new TableColumn<>("Dimensione (ha)");
        dimCol.setCellValueFactory(new PropertyValueFactory<>("dimensione"));
        TableColumn<T, String> tipoCol = new TableColumn<>("Tipo terreno");
        tipoCol.setCellValueFactory(new PropertyValueFactory<>("tipoTerreno"));
        table.getColumns().setAll(idCol, nomeCol, dimCol, tipoCol);
    }
}
