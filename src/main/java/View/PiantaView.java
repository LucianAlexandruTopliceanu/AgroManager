package View;

import DomainModel.Pianta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PiantaView extends VBox {

    private final TableView<Pianta> tablePiante;
    private final ObservableList<Pianta> pianteData;

    // Pulsanti essenziali
    private final Button nuovoBtn;
    private final Button modificaBtn;
    private final Button eliminaBtn;
    private final Button applicaFiltriBtn;
    private final Button resetFiltriBtn;

    // Controlli di ricerca e filtro
    private final TextField ricercaTipoField;
    private final TextField ricercaVarietaField;
    private final ComboBox<String> filtroFornitoreCombo;

    public PiantaView() {
        tablePiante = new TableView<>();
        pianteData = FXCollections.observableArrayList();
        nuovoBtn = new Button("➕ Nuova Pianta");
        modificaBtn = new Button("✏️ Modifica");
        eliminaBtn = new Button("🗑️ Elimina");
        applicaFiltriBtn = new Button("🔍 Applica Filtri");
        resetFiltriBtn = new Button("🔄 Reset Filtri");
        ricercaTipoField = new TextField();
        ricercaVarietaField = new TextField();
        filtroFornitoreCombo = new ComboBox<>();

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
        TableColumn<Pianta, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        idCol.setPrefWidth(60);

        TableColumn<Pianta, String> tipoCol = new TableColumn<>("Tipo");
        tipoCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getTipo()));
        tipoCol.setPrefWidth(150);

        TableColumn<Pianta, String> varietaCol = new TableColumn<>("Varietà");
        varietaCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getVarieta()));
        varietaCol.setPrefWidth(150);

        TableColumn<Pianta, String> costoCol = new TableColumn<>("Costo (€)");
        costoCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getCosto() != null ?
                String.format("%.2f", cell.getValue().getCosto()) : "N/A"));
        costoCol.setPrefWidth(100);

        TableColumn<Pianta, String> fornitoreCol = new TableColumn<>("Fornitore ID");
        fornitoreCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getFornitoreId() != null ?
                cell.getValue().getFornitoreId().toString() : "N/A"));
        fornitoreCol.setPrefWidth(100);

        TableColumn<Pianta, String> noteCol = new TableColumn<>("Note");
        noteCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getNote()));
        noteCol.setPrefWidth(200);

        tablePiante.getColumns().clear();
        tablePiante.getColumns().addAll(idCol, tipoCol, varietaCol, costoCol, fornitoreCol, noteCol);
        tablePiante.setItems(pianteData);

        // Double-click per modifica
        tablePiante.setRowFactory(tv -> {
            TableRow<Pianta> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    // Il controller gestirà l'apertura del dialog
                }
            });
            return row;
        });

        VBox.setVgrow(tablePiante, Priority.ALWAYS);
    }

    private void setupControls() {
        // Sezione ricerca in card
        VBox ricercaCard = createCard("🔍 Ricerca Piante");
        GridPane ricercaGrid = new GridPane();
        ricercaGrid.setHgap(10);
        ricercaGrid.setVgap(10);

        ricercaTipoField.setPromptText("Cerca per tipo...");
        ricercaVarietaField.setPromptText("Cerca per varietà...");
        filtroFornitoreCombo.setPromptText("Tutti i fornitori");

        ricercaGrid.addRow(0, new Label("Tipo:"), ricercaTipoField);
        ricercaGrid.addRow(1, new Label("Varietà:"), ricercaVarietaField);
        ricercaGrid.addRow(2, new Label("Fornitore:"), filtroFornitoreCombo);

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
        tablePiante.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean hasSelection = newSel != null;
            modificaBtn.setDisable(!hasSelection);
            eliminaBtn.setDisable(!hasSelection);
        });

        azioniBox.getChildren().addAll(nuovoBtn, modificaBtn, eliminaBtn);
        azioniCard.getChildren().add(azioniBox);

        getChildren().addAll(ricercaCard, azioniCard, tablePiante);
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
    public void setPiante(java.util.List<Pianta> piante) {
        pianteData.setAll(piante);
    }

    public Pianta getPiantaSelezionata() {
        return tablePiante.getSelectionModel().getSelectedItem();
    }

    // Event handlers
    public void setOnNuovaPianta(Runnable handler) {
        nuovoBtn.setOnAction(e -> handler.run());
    }

    public void setOnModificaPianta(Runnable handler) {
        modificaBtn.setOnAction(e -> handler.run());
    }

    public void setOnEliminaPianta(Runnable handler) {
        eliminaBtn.setOnAction(e -> handler.run());
    }

    public void setOnApplicaFiltri(Runnable handler) {
        applicaFiltriBtn.setOnAction(e -> handler.run());
    }

    public void setOnResetFiltri(Runnable handler) {
        resetFiltriBtn.setOnAction(e -> handler.run());
    }

    // Gestione filtri
    public void setFornitori(java.util.List<String> fornitori) {
        filtroFornitoreCombo.getItems().setAll(fornitori);
    }

    public CriteriFiltro getCriteriFiltro() {
        return new CriteriFiltro(
            ricercaTipoField.getText().trim(),
            ricercaVarietaField.getText().trim(),
            filtroFornitoreCombo.getValue()
        );
    }

    public void resetFiltri() {
        ricercaTipoField.clear();
        ricercaVarietaField.clear();
        filtroFornitoreCombo.setValue(null);
    }

    // Metodo per conferma eliminazione
    public boolean confermaEliminazione(Pianta pianta) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText("Stai per eliminare la pianta:");
        alert.setContentText(pianta.getTipo() + " - " + pianta.getVarieta() +
                            "\n\nQuesta operazione non può essere annullata.");

        return alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .isPresent();
    }

    // Record per criteri di filtro
    public record CriteriFiltro(String tipo, String varieta, String fornitore) {}
}
