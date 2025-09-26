package View;

import DomainModel.Raccolto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;

public class RaccoltoView extends VBox {
    private final TableView<Raccolto> tableRaccolti = new TableView<>();
    private final ObservableList<Raccolto> raccoltiData = FXCollections.observableArrayList();
    private final Button nuovoBtn = new Button("Nuovo Raccolto");
    private final Button modificaBtn = new Button("Modifica");
    private final Button eliminaBtn = new Button("Elimina");

    public RaccoltoView() {
        setSpacing(10);
        setPadding(new Insets(10));
        TableColumn<Raccolto, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        TableColumn<Raccolto, Integer> piantagioneCol = new TableColumn<>("Piantagione");
        piantagioneCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getPiantagioneId()));
        TableColumn<Raccolto, Integer> quantitaCol = new TableColumn<>("Quantità raccolta");
        quantitaCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getQuantitaKgInt()));
        TableColumn<Raccolto, String> dataCol = new TableColumn<>("Data raccolta");
        dataCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDataRaccolto().toString()));
        tableRaccolti.getColumns().setAll(idCol, piantagioneCol, quantitaCol, dataCol);
        tableRaccolti.setItems(raccoltiData);
        HBox btnBox = new HBox(10, nuovoBtn, modificaBtn, eliminaBtn);
        btnBox.setPadding(new Insets(10,0,10,0));
        getChildren().addAll(tableRaccolti, btnBox);
    }

    public void setRaccolti(java.util.List<Raccolto> raccolti) {
        raccoltiData.setAll(raccolti);
    }
    public Raccolto getRaccoltoSelezionato() {
        return tableRaccolti.getSelectionModel().getSelectedItem();
    }
    public void setOnNuovoRaccolto(Consumer<Void> handler) {
        nuovoBtn.setOnAction(e -> handler.accept(null));
    }
    public void setOnModificaRaccolto(Consumer<Void> handler) {
        modificaBtn.setOnAction(e -> handler.accept(null));
    }
    public void setOnEliminaRaccolto(Consumer<Void> handler) {
        eliminaBtn.setOnAction(e -> handler.accept(null));
    }
}
