package View;

import DomainModel.Fornitore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;

public class FornitoreView extends VBox {
    private final TableView<Fornitore> tableFornitori = new TableView<>();
    private final ObservableList<Fornitore> fornitoriData = FXCollections.observableArrayList();
    private final Button nuovoBtn = new Button("Nuovo Fornitore");
    private final Button modificaBtn = new Button("Modifica");
    private final Button eliminaBtn = new Button("Elimina");

    public FornitoreView() {
        setSpacing(10);
        setPadding(new Insets(10));
        TableColumn<Fornitore, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        TableColumn<Fornitore, String> nomeCol = new TableColumn<>("Nome");
        nomeCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNome()));
        TableColumn<Fornitore, String> indirizzoCol = new TableColumn<>("Indirizzo");
        indirizzoCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getIndirizzo()));
        TableColumn<Fornitore, String> telCol = new TableColumn<>("Telefono");
        telCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNumeroTelefono()));
        TableColumn<Fornitore, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEmail()));
        TableColumn<Fornitore, String> pivaCol = new TableColumn<>("Partita IVA");
        pivaCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPartitaIva()));
        tableFornitori.getColumns().setAll(idCol, nomeCol, indirizzoCol, telCol, emailCol, pivaCol);
        tableFornitori.setItems(fornitoriData);
        HBox btnBox = new HBox(10, nuovoBtn, modificaBtn, eliminaBtn);
        btnBox.setPadding(new Insets(10,0,10,0));
        getChildren().addAll(tableFornitori, btnBox);
    }

    public void setFornitori(java.util.List<Fornitore> fornitori) {
        fornitoriData.setAll(fornitori);
    }
    public Fornitore getFornitoreSelezionato() {
        return tableFornitori.getSelectionModel().getSelectedItem();
    }
    public void setOnNuovoFornitore(Consumer<Void> handler) {
        nuovoBtn.setOnAction(e -> handler.accept(null));
    }
    public void setOnModificaFornitore(Consumer<Void> handler) {
        modificaBtn.setOnAction(e -> handler.accept(null));
    }
    public void setOnEliminaFornitore(Consumer<Void> handler) {
        eliminaBtn.setOnAction(e -> handler.accept(null));
    }
}
