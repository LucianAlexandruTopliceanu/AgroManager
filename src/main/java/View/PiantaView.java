package View;

import DomainModel.Pianta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * View moderna per la gestione delle piante.
 * Stile coerente con l'applicazione.
 */
public class PiantaView extends VBox {

    // Componenti UI
    private final TableView<Pianta> tablePiante = new TableView<>();
    private final ObservableList<Pianta> pianteData = FXCollections.observableArrayList();

    // Pulsanti azione
    private final Button nuovoBtn = new Button("➕ Nuova Pianta");
    private final Button modificaBtn = new Button("✏️ Modifica");
    private final Button eliminaBtn = new Button("🗑️ Elimina");
    private final Button applicaFiltriBtn = new Button("🔍 Applica Filtri");
    private final Button resetFiltriBtn = new Button("🔄 Reset");

    // Controlli ricerca
    private final TextField ricercaTipoField = new TextField();
    private final TextField ricercaVarietaField = new TextField();
    private final ComboBox<String> filtroFornitoreCombo = new ComboBox<>();

    public PiantaView() {
        setupStyles();
        setupLayout();
        setupTable();
    }

    private void setupStyles() {
        getStyleClass().add("main-container");

        // Configurazione campi ricerca
        ricercaTipoField.setPromptText("Cerca per tipo...");
        ricercaTipoField.setPrefWidth(180);
        ricercaTipoField.getStyleClass().add("text-field-standard");

        ricercaVarietaField.setPromptText("Cerca per varietà...");
        ricercaVarietaField.setPrefWidth(180);
        ricercaVarietaField.getStyleClass().add("text-field-standard");

        filtroFornitoreCombo.setPromptText("Tutti i fornitori");
        filtroFornitoreCombo.setPrefWidth(200);
        filtroFornitoreCombo.getStyleClass().add("combo-box-standard");

        // Configurazione bottoni
        nuovoBtn.getStyleClass().add("btn-primary");
        modificaBtn.getStyleClass().add("btn-secondary");
        modificaBtn.setDisable(true);
        eliminaBtn.getStyleClass().add("btn-danger");
        eliminaBtn.setDisable(true);
        applicaFiltriBtn.getStyleClass().add("btn-secondary");
        resetFiltriBtn.getStyleClass().add("btn-support");
    }

    private void setupLayout() {
        // Header
        VBox header = createHeader();

        // Card ricerca
        VBox ricercaCard = createRicercaSection();

        // Barra azioni
        HBox actionBar = createActionBar();

        // Card tabella
        VBox tableCard = createTableSection();

        getChildren().addAll(header, ricercaCard, actionBar, tableCard);
        VBox.setVgrow(tableCard, Priority.ALWAYS);
    }

    private VBox createHeader() {
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("header-section");

        Label title = new Label("🌱 Gestione Piante");
        title.getStyleClass().add("main-title");

        Label subtitle = new Label("Catalogo delle varietà di piante disponibili");
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

        Label tipoLabel = new Label("Tipo:");
        tipoLabel.getStyleClass().add("field-label");

        Label varietaLabel = new Label("Varietà:");
        varietaLabel.getStyleClass().add("field-label");

        Label fornitoreLabel = new Label("Fornitore:");
        fornitoreLabel.getStyleClass().add("field-label");

        grid.add(tipoLabel, 0, 0);
        grid.add(ricercaTipoField, 1, 0);
        grid.add(varietaLabel, 2, 0);
        grid.add(ricercaVarietaField, 3, 0);
        grid.add(fornitoreLabel, 0, 1);
        grid.add(filtroFornitoreCombo, 1, 1);

        card.getChildren().addAll(cardTitle, grid);
        return card;
    }

    private HBox createActionBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 0, 10, 0));

        // Gruppo principale
        HBox mainGroup = new HBox(10);
        mainGroup.setAlignment(Pos.CENTER_LEFT);
        mainGroup.getChildren().add(nuovoBtn);

        // Gruppo secondario
        HBox secondaryGroup = new HBox(8);
        secondaryGroup.setAlignment(Pos.CENTER_LEFT);
        secondaryGroup.getChildren().addAll(modificaBtn, eliminaBtn);

        // Gruppo filtri
        HBox filterGroup = new HBox(8);
        filterGroup.setAlignment(Pos.CENTER_LEFT);
        filterGroup.getChildren().addAll(applicaFiltriBtn, resetFiltriBtn);

        // Separatori
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

        Label cardTitle = new Label("📋 Catalogo Piante");
        cardTitle.getStyleClass().add("card-title");

        VBox.setVgrow(tablePiante, Priority.ALWAYS);

        card.getChildren().addAll(cardTitle, tablePiante);
        return card;
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

        TableColumn<Pianta, String> fornitoreCol = new TableColumn<>("Fornitore");
        fornitoreCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getFornitoreId() != null ?
                "ID " + cell.getValue().getFornitoreId().toString() : "N/A"));
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
                    modificaPianta();
                }
            });
            return row;
        });

        // Gestione selezione
        tablePiante.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean hasSelection = newSel != null;
            modificaBtn.setDisable(!hasSelection);
            eliminaBtn.setDisable(!hasSelection);
        });
    }

    private void modificaPianta() {
        if (getPiantaSelezionata() != null) {
            // Il controller gestirà l'apertura del dialog
        }
    }

    // Metodi pubblici per il controller
    public void setPiante(java.util.List<Pianta> piante) {
        pianteData.setAll(piante);
    }

    public Pianta getPiantaSelezionata() {
        return tablePiante.getSelectionModel().getSelectedItem();
    }

    public void setFornitori(java.util.List<String> fornitori) {
        filtroFornitoreCombo.getItems().clear();
        filtroFornitoreCombo.getItems().add("Tutti i fornitori");
        filtroFornitoreCombo.getItems().addAll(fornitori);
        filtroFornitoreCombo.getSelectionModel().selectFirst();
    }

    // Handler per i pulsanti
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
    public CriteriFiltro getCriteriFiltro() {
        String fornitore = filtroFornitoreCombo.getValue();
        if (fornitore != null && fornitore.equals("Tutti i fornitori")) {
            fornitore = null;
        }
        return new CriteriFiltro(
            ricercaTipoField.getText().trim(),
            ricercaVarietaField.getText().trim(),
            fornitore
        );
    }

    public void resetFiltri() {
        ricercaTipoField.clear();
        ricercaVarietaField.clear();
        filtroFornitoreCombo.getSelectionModel().selectFirst();
    }

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

    public record CriteriFiltro(String tipo, String varieta, String fornitore) {}
}
