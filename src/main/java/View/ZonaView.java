package View;

import DomainModel.Zona;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ZonaView extends VBox {

    private final TableView<Zona> tableZone = new TableView<>();
    private final ObservableList<Zona> zoneData = FXCollections.observableArrayList();
    private final Button nuovoBtn = new Button("➕ Nuova Zona");
    private final Button modificaBtn = new Button("✏️ Modifica");
    private final Button eliminaBtn = new Button("🗑️ Elimina");
    private final Button applicaFiltriBtn = new Button("🔍 Applica Filtri");
    private final Button resetFiltriBtn = new Button("🔄 Reset");
    private final TextField ricercaField = new TextField();
    private final ComboBox<String> filtroTipoTerreno = new ComboBox<>();

    public ZonaView() {
        setupStyles();
        setupLayout();
        setupTable();
    }

    private void setupStyles() {
        getStyleClass().add("main-container");

        ricercaField.setPromptText("Cerca zona per nome...");
        ricercaField.setPrefWidth(250);
        ricercaField.getStyleClass().add("text-field-standard");

        filtroTipoTerreno.setPromptText("Tutti i tipi");
        filtroTipoTerreno.setPrefWidth(180);
        filtroTipoTerreno.getStyleClass().add("combo-box-standard");

        nuovoBtn.getStyleClass().add("btn-primary");
        modificaBtn.getStyleClass().add("btn-secondary");
        modificaBtn.setDisable(true);
        eliminaBtn.getStyleClass().add("btn-danger");
        eliminaBtn.setDisable(true);
        applicaFiltriBtn.getStyleClass().add("btn-secondary");
        resetFiltriBtn.getStyleClass().add("btn-support");
    }

    private void setupLayout() {
        VBox header = createHeader();
        VBox ricercaCard = createRicercaSection();
        HBox actionBar = createActionBar();
        VBox tableCard = createTableSection();

        getChildren().addAll(header, ricercaCard, actionBar, tableCard);
        VBox.setVgrow(tableCard, Priority.ALWAYS);
    }

    private VBox createHeader() {
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("header-section");

        Label title = new Label("🗺️ Gestione Zone");
        title.getStyleClass().add("main-title");

        Label subtitle = new Label("Organizza e gestisci le aree di coltivazione");
        subtitle.getStyleClass().add("subtitle");

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private VBox createRicercaSection() {
        VBox card = new VBox(15);
        card.getStyleClass().add("styled-card");

        Label cardTitle = new Label("🔍 Ricerca");
        cardTitle.getStyleClass().add("card-title");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.getStyleClass().add("input-grid");

        Label nomeLabel = new Label("Nome:");
        nomeLabel.getStyleClass().add("field-label");

        Label tipoLabel = new Label("Tipo terreno:");
        tipoLabel.getStyleClass().add("field-label");

        grid.add(nomeLabel, 0, 0);
        grid.add(ricercaField, 1, 0);
        grid.add(tipoLabel, 2, 0);
        grid.add(filtroTipoTerreno, 3, 0);

        card.getChildren().addAll(cardTitle, grid);
        return card;
    }

    private HBox createActionBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 0, 10, 0));

        HBox mainGroup = new HBox(10);
        mainGroup.setAlignment(Pos.CENTER_LEFT);
        mainGroup.getChildren().add(nuovoBtn);

        HBox secondaryGroup = new HBox(8);
        secondaryGroup.setAlignment(Pos.CENTER_LEFT);
        secondaryGroup.getChildren().addAll(modificaBtn, eliminaBtn);

        HBox filterGroup = new HBox(8);
        filterGroup.setAlignment(Pos.CENTER_LEFT);
        filterGroup.getChildren().addAll(applicaFiltriBtn, resetFiltriBtn);

        Separator sep1 = new Separator(javafx.geometry.Orientation.VERTICAL);
        sep1.getStyleClass().add("v-separator");
        Separator sep2 = new Separator(javafx.geometry.Orientation.VERTICAL);
        sep2.getStyleClass().add("v-separator");

        bar.getChildren().addAll(mainGroup, sep1, secondaryGroup, sep2, filterGroup);
        return bar;
    }

    private VBox createTableSection() {
        VBox card = new VBox(12);
        card.getStyleClass().add("styled-card");
        VBox.setVgrow(card, Priority.ALWAYS);

        Label cardTitle = new Label("📋 Elenco Zone");
        cardTitle.getStyleClass().add("card-title");

        VBox.setVgrow(tableZone, Priority.ALWAYS);

        card.getChildren().addAll(cardTitle, tableZone);
        return card;
    }

    @SuppressWarnings("unchecked")
    private void setupTable() {
        TableColumn<Zona, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        idCol.setPrefWidth(80);

        TableColumn<Zona, String> nomeCol = new TableColumn<>("Nome Zona");
        nomeCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getNome()));
        nomeCol.setPrefWidth(200);

        TableColumn<Zona, String> dimCol = new TableColumn<>("Dimensione (ha)");
        dimCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getDimensione() != null ?
                String.format("%.2f", cell.getValue().getDimensione()) : "N/A"));
        dimCol.setPrefWidth(120);

        TableColumn<Zona, String> tipoCol = new TableColumn<>("Tipo Terreno");
        tipoCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getTipoTerreno()));
        tipoCol.setPrefWidth(150);

        TableColumn<Zona, String> dataCol = new TableColumn<>("Data Creazione");
        dataCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getDataCreazione() != null ?
                cell.getValue().getDataCreazione().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"));
        dataCol.setPrefWidth(120);

        tableZone.getColumns().clear();
        tableZone.getColumns().addAll(idCol, nomeCol, dimCol, tipoCol, dataCol);
        tableZone.setItems(zoneData);

        tableZone.setRowFactory(tv -> {
            TableRow<Zona> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    modificaZona();
                }
            });
            return row;
        });

        tableZone.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean hasSelection = newSel != null;
            modificaBtn.setDisable(!hasSelection);
            eliminaBtn.setDisable(!hasSelection);
        });
    }

    private void modificaZona() {
        if (getZonaSelezionata() != null) {
        }
    }

    public void setZone(java.util.List<Zona> zone) {
        zoneData.setAll(zone);
    }

    public Zona getZonaSelezionata() {
        return tableZone.getSelectionModel().getSelectedItem();
    }

    public void setTipiTerreno(java.util.List<String> tipi) {
        filtroTipoTerreno.getItems().clear();
        filtroTipoTerreno.getItems().add("Tutti i tipi");
        filtroTipoTerreno.getItems().addAll(tipi);
        filtroTipoTerreno.getSelectionModel().selectFirst();
    }

    public void setOnNuovaZona(Runnable handler) {
        nuovoBtn.setOnAction(e -> handler.run());
    }

    public void setOnModificaZona(Runnable handler) {
        modificaBtn.setOnAction(e -> handler.run());
    }

    public void setOnEliminaZona(Runnable handler) {
        eliminaBtn.setOnAction(e -> handler.run());
    }

    public void setOnApplicaFiltri(Runnable handler) {
        applicaFiltriBtn.setOnAction(e -> handler.run());
    }

    public void setOnResetFiltri(Runnable handler) {
        resetFiltriBtn.setOnAction(e -> handler.run());
    }

    public CriteriFiltro getCriteriFiltro() {
        String tipo = filtroTipoTerreno.getValue();
        if (tipo != null && tipo.equals("Tutti i tipi")) {
            tipo = null;
        }
        return new CriteriFiltro(ricercaField.getText().trim(), tipo);
    }

    public void resetFiltri() {
        ricercaField.clear();
        filtroTipoTerreno.getSelectionModel().selectFirst();
    }

    public boolean confermaEliminazione(Zona zona) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText("Stai per eliminare la zona:");
        alert.setContentText(zona.getNome() + " (" + zona.getDimensione() + " ha)" +
                            "\n\nQuesta operazione non può essere annullata.");
        return alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .isPresent();
    }

    public record CriteriFiltro(String nome, String tipoTerreno) {}
}
