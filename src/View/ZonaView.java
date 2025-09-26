package View;

import DomainModel.Zona;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class ZonaView extends VBox {
    private final TableView<Zona> tableZone = new TableView<>();
    private final ObservableList<Zona> zoneData = FXCollections.observableArrayList();
    private final Button nuovoBtn = new Button("Nuova Zona");
    private final Button modificaBtn = new Button("Modifica");
    private final Button eliminaBtn = new Button("Elimina");

    public ZonaView() {
        setSpacing(10);
        setPadding(new Insets(10));
        TableColumn<Zona, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        TableColumn<Zona, String> nomeCol = new TableColumn<>("Nome");
        nomeCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNome()));
        TableColumn<Zona, String> dimCol = new TableColumn<>("Dimensione");
        dimCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cell.getValue().getDimensione())));
        TableColumn<Zona, String> tipoCol = new TableColumn<>("Tipo Terreno");
        tipoCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTipoTerreno()));
        tableZone.getColumns().setAll(idCol, nomeCol, dimCol, tipoCol);
        tableZone.setItems(zoneData);
        HBox btnBox = new HBox(10, nuovoBtn, modificaBtn, eliminaBtn);
        btnBox.setPadding(new Insets(10,0,10,0));
        getChildren().addAll(tableZone, btnBox);
    }

    public void setZone(java.util.List<Zona> zone) {
        zoneData.setAll(zone);
    }
    public Zona getZonaSelezionata() {
        return tableZone.getSelectionModel().getSelectedItem();
    }
    public void setOnNuovaZona(Consumer<Void> handler) {
        nuovoBtn.setOnAction(e -> handler.accept(null));
    }
    public void setOnModificaZona(Consumer<Void> handler) {
        modificaBtn.setOnAction(e -> handler.accept(null));
    }
    public void setOnEliminaZona(Consumer<Void> handler) {
        eliminaBtn.setOnAction(e -> handler.accept(null));
    }
}
