package View;

import DomainModel.Piantagione;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PiantagioneView extends VBox {
    private final TableView<Piantagione> tablePiantagioni;
    private final ObservableList<Piantagione> piantagioniData;

    // Controlli essenziali
    private final Button nuovoBtn;
    private final Button modificaBtn;
    private final Button eliminaBtn;
    private final ComboBox<String> filtroPiantaCombo;
    private final ComboBox<String> filtroZonaCombo;
    private final DatePicker filtroDataDa;
    private final DatePicker filtroDataA;

    public PiantagioneView() {
        setPadding(new Insets(10));
        setSpacing(10);

        // Inizializzazione componenti
        tablePiantagioni = new TableView<>();
        piantagioniData = FXCollections.observableArrayList();
        nuovoBtn = new Button("Nuova");
        modificaBtn = new Button("Modifica");
        eliminaBtn = new Button("Elimina");
        filtroPiantaCombo = new ComboBox<>();
        filtroZonaCombo = new ComboBox<>();
        filtroDataDa = new DatePicker();
        filtroDataA = new DatePicker();

        setupTable();
        setupLayout();
    }

    private void setupTable() {
        // Colonne essenziali
        TableColumn<Piantagione, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));

        TableColumn<Piantagione, String> piantaCol = new TableColumn<>("Pianta");
        piantaCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getPiantaId() != null ? cell.getValue().getPiantaId().toString() : ""
        ));

        TableColumn<Piantagione, String> zonaCol = new TableColumn<>("Zona");
        zonaCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getZonaId() != null ? cell.getValue().getZonaId().toString() : ""
        ));

        TableColumn<Piantagione, String> dataCol = new TableColumn<>("Data");
        dataCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getMessaADimora() != null ?
            cell.getValue().getMessaADimora().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""
        ));

        // Correzione per evitare generic array creation warning
        tablePiantagioni.getColumns().clear();
        tablePiantagioni.getColumns().add(idCol);
        tablePiantagioni.getColumns().add(piantaCol);
        tablePiantagioni.getColumns().add(zonaCol);
        tablePiantagioni.getColumns().add(dataCol);

        tablePiantagioni.setItems(piantagioniData);

        // Gestione selezione
        tablePiantagioni.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            boolean hasSelection = sel != null;
            modificaBtn.setDisable(!hasSelection);
            eliminaBtn.setDisable(!hasSelection);
        });
    }

    private void setupLayout() {
        // Filtri
        HBox filtriBox = new HBox(10);
        filtriBox.getChildren().addAll(
            new Label("Pianta:"), filtroPiantaCombo,
            new Label("Zona:"), filtroZonaCombo,
            new Label("Da:"), filtroDataDa,
            new Label("A:"), filtroDataA
        );

        // Azioni
        HBox azioniBox = new HBox(10);
        azioniBox.getChildren().addAll(nuovoBtn, modificaBtn, eliminaBtn);

        getChildren().addAll(filtriBox, azioniBox, tablePiantagioni);
        VBox.setVgrow(tablePiantagioni, Priority.ALWAYS);
    }

    // Metodi pubblici per il controller
    public void setPiantagioni(java.util.List<Piantagione> piantagioni) {
        piantagioniData.setAll(piantagioni);
    }

    public Piantagione getPiantagioneSelezionata() {
        return tablePiantagioni.getSelectionModel().getSelectedItem();
    }

    public void setOnNuovaPiantagione(Runnable handler) {
        nuovoBtn.setOnAction(e -> handler.run());
    }
    public void setOnModificaPiantagione(Runnable handler) {
        modificaBtn.setOnAction(e -> handler.run());
    }
    public void setOnEliminaPiantagione(Runnable handler) {
        eliminaBtn.setOnAction(e -> handler.run());
    }

    // Filtri
    public void setFiltroPiantaItems(java.util.List<String> items) {
        filtroPiantaCombo.getItems().setAll(items);
    }

    public void setFiltroZonaItems(java.util.List<String> items) {
        filtroZonaCombo.getItems().setAll(items);
    }

    public void setOnFiltroPiantaChanged(java.util.function.Consumer<String> listener) {
        filtroPiantaCombo.setOnAction(e -> listener.accept(filtroPiantaCombo.getValue()));
    }

    public void setOnFiltroZonaChanged(java.util.function.Consumer<String> listener) {
        filtroZonaCombo.setOnAction(e -> listener.accept(filtroZonaCombo.getValue()));
    }

    public void setOnFiltroDataDaChanged(java.util.function.Consumer<java.time.LocalDate> listener) {
        filtroDataDa.setOnAction(e -> listener.accept(filtroDataDa.getValue()));
    }

    public void setOnFiltroDataAChanged(java.util.function.Consumer<java.time.LocalDate> listener) {
        filtroDataA.setOnAction(e -> listener.accept(filtroDataA.getValue()));
    }
}
