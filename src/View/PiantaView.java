package View;

import DomainModel.Pianta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;

public class PiantaView extends VBox {
    private final TableView<Pianta> tablePiante = new TableView<>();
    private final ObservableList<Pianta> pianteData = FXCollections.observableArrayList();
    private final Button nuovoBtn = new Button("Nuova Pianta");
    private final Button modificaBtn = new Button("Modifica");
    private final Button eliminaBtn = new Button("Elimina");

    public PiantaView() {
        setSpacing(10);
        setPadding(new Insets(10));
        TableColumn<Pianta, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        TableColumn<Pianta, String> tipoCol = new TableColumn<>("Tipo");
        tipoCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTipo()));
        TableColumn<Pianta, String> varietaCol = new TableColumn<>("Varietà");
        varietaCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getVarieta()));
        tablePiante.getColumns().setAll(idCol, tipoCol, varietaCol);
        tablePiante.setItems(pianteData);
        HBox btnBox = new HBox(10, nuovoBtn, modificaBtn, eliminaBtn);
        btnBox.setPadding(new Insets(10,0,10,0));
        getChildren().addAll(tablePiante, btnBox);
    }

    public void setPiante(java.util.List<Pianta> piante) {
        pianteData.setAll(piante);
    }
    public Pianta getPiantaSelezionata() {
        return tablePiante.getSelectionModel().getSelectedItem();
    }
    public void setOnNuovaPianta(Consumer<Void> handler) {
        nuovoBtn.setOnAction(e -> handler.accept(null));
    }
    public void setOnModificaPianta(Consumer<Void> handler) {
        modificaBtn.setOnAction(e -> handler.accept(null));
    }
    public void setOnEliminaPianta(Consumer<Void> handler) {
        eliminaBtn.setOnAction(e -> handler.accept(null));
    }
}
