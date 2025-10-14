package View;

import DomainModel.Zona;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ZonaView extends VBox {

    private final TableView<Zona> tableZone;
    private final ObservableList<Zona> zoneData;

    // Pulsanti essenziali
    private final Button nuovoBtn;
    private final Button modificaBtn;
    private final Button eliminaBtn;
    private final Button applicaFiltriBtn;
    private final Button resetFiltriBtn;

    // Controlli di ricerca e filtro
    private final TextField ricercaField;
    private final ComboBox<String> filtroTipoTerreno;

    public ZonaView() {
        tableZone = new TableView<>();
        zoneData = FXCollections.observableArrayList();
        nuovoBtn = new Button("➕ Nuova Zona");
        modificaBtn = new Button("✏️ Modifica");
        eliminaBtn = new Button("🗑️ Elimina");
        applicaFiltriBtn = new Button("🔍 Applica Filtri");
        resetFiltriBtn = new Button("🔄 Reset Filtri");
        ricercaField = new TextField();
        filtroTipoTerreno = new ComboBox<>();

        setupLayout();
        setupTable();
        setupControls();
    }

    private void setupLayout() {
        setPadding(new Insets(20));
        setSpacing(20);
        setStyle("-fx-background-color: #F8F9FA;");
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

        // Double-click per modifica
        tableZone.setRowFactory(tv -> {
            TableRow<Zona> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    // Il controller gestirà l'apertura del dialog
                }
            });
            return row;
        });

        VBox.setVgrow(tableZone, Priority.ALWAYS);
    }

    private void setupControls() {
        // Sezione ricerca in card
        VBox ricercaCard = createCard("🔍 Ricerca Zone");
        GridPane ricercaGrid = new GridPane();
        ricercaGrid.setHgap(10);
        ricercaGrid.setVgap(10);

        ricercaField.setPromptText("Cerca per nome zona...");
        filtroTipoTerreno.setPromptText("Tutti i tipi di terreno");

        // Aggiunta opzioni comuni per tipo terreno
        filtroTipoTerreno.getItems().addAll(
            "Argilloso", "Sabbioso", "Limoso", "Calcareo", "Vulcanico", "Misto"
        );

        ricercaGrid.addRow(0, new Label("Nome:"), ricercaField);
        ricercaGrid.addRow(1, new Label("Tipo Terreno:"), filtroTipoTerreno);

        HBox filtriActions = new HBox(10);
        applicaFiltriBtn.setStyle("-fx-background-color: #17A2B8; -fx-text-fill: white; " +
                                 "-fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;");
        resetFiltriBtn.setStyle("-fx-background-color: #6C757D; -fx-text-fill: white; " +
                               "-fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;");
        filtriActions.getChildren().addAll(applicaFiltriBtn, resetFiltriBtn);

        ricercaCard.getChildren().addAll(ricercaGrid, filtriActions);

        // Sezione azioni in card
        VBox azioniCard = createCard("⚡ Azioni");
        HBox azioniBox = new HBox(10);

        // Stili pulsanti
        nuovoBtn.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; " +
                         "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");

        modificaBtn.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; " +
                            "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
        modificaBtn.setDisable(true);

        eliminaBtn.setStyle("-fx-background-color: #DC3545; -fx-text-fill: white; " +
                           "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
        eliminaBtn.setDisable(true);

        // Gestione selezione
        tableZone.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean hasSelection = newSel != null;
            modificaBtn.setDisable(!hasSelection);
            eliminaBtn.setDisable(!hasSelection);
        });

        azioniBox.getChildren().addAll(nuovoBtn, modificaBtn, eliminaBtn);
        azioniCard.getChildren().add(azioniBox);

        getChildren().addAll(ricercaCard, azioniCard, tableZone);
    }

    private VBox createCard(String title) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        card.getChildren().add(titleLabel);

        return card;
    }

    // Metodi pubblici per il controller
    public void setZone(java.util.List<Zona> zone) {
        zoneData.setAll(zone);
    }

    public Zona getZonaSelezionata() {
        return tableZone.getSelectionModel().getSelectedItem();
    }

    // Event handlers
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

    // Gestione filtri
    public CriteriFiltro getCriteriFiltro() {
        return new CriteriFiltro(
            ricercaField.getText().trim(),
            filtroTipoTerreno.getValue()
        );
    }

    public void resetFiltri() {
        ricercaField.clear();
        filtroTipoTerreno.setValue(null);
    }

    // Metodo per conferma eliminazione
    public boolean confermaEliminazione(Zona zona) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText("Stai per eliminare la zona:");
        alert.setContentText(zona.getNome() + " (" + zona.getTipoTerreno() + ")" +
                            "\nDimensione: " + zona.getDimensione() + " ha" +
                            "\n\nQuesta operazione non può essere annullata.");

        return alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .isPresent();
    }

    // Record per criteri di filtro
    public record CriteriFiltro(String nome, String tipoTerreno) {}
}
