package View;

import DomainModel.Piantagione;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class PiantagioneView extends VBox {
    private final TableView<Piantagione> tablePiantagioni = new TableView<>();
    private final ObservableList<Piantagione> piantagioniData = FXCollections.observableArrayList();
    private final Button nuovoBtn = new Button("Nuova Piantagione");
    private final Button modificaBtn = new Button("Modifica");
    private final Button eliminaBtn = new Button("Elimina");

    public PiantagioneView() {
        setSpacing(10);
        setPadding(new Insets(10));
        TableColumn<Piantagione, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        TableColumn<Piantagione, Integer> zonaCol = new TableColumn<>("Zona");
        zonaCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getZonaId()));
        TableColumn<Piantagione, Integer> piantaCol = new TableColumn<>("Pianta");
        piantaCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getPiantaId()));
        TableColumn<Piantagione, Integer> quantitaCol = new TableColumn<>("Quantità");
        quantitaCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getQuantitaPianta()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        TableColumn<Piantagione, String> dataCol = new TableColumn<>("Messa a dimora");
        dataCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getMessaADimora() != null ? cell.getValue().getMessaADimora().format(formatter) : ""
        ));
        tablePiantagioni.getColumns().setAll(idCol, zonaCol, piantaCol, quantitaCol, dataCol);
        tablePiantagioni.setItems(piantagioniData);
        HBox btnBox = new HBox(10, nuovoBtn, modificaBtn, eliminaBtn);
        btnBox.setPadding(new Insets(10,0,10,0));
        getChildren().addAll(tablePiantagioni, btnBox);
    }

    public void setPiantagioni(java.util.List<Piantagione> piantagioni) {
        piantagioniData.setAll(piantagioni);
    }
    public Piantagione getPiantagioneSelezionata() {
        return tablePiantagioni.getSelectionModel().getSelectedItem();
    }
    public void setOnNuovaPiantagione(Consumer<Void> handler) {
        nuovoBtn.setOnAction(e -> handler.accept(null));
    }
    public void setOnModificaPiantagione(Consumer<Void> handler) {
        modificaBtn.setOnAction(e -> handler.accept(null));
    }
    public void setOnEliminaPiantagione(Consumer<Void> handler) {
        eliminaBtn.setOnAction(e -> handler.accept(null));
    }
}
