package View;

import DomainModel.Fornitore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class FornitoreView extends VBox {

    private final TableView<Fornitore> tableFornitori = new TableView<>();
    private final ObservableList<Fornitore> fornitoriData = FXCollections.observableArrayList();
    private final Button nuovoBtn = new Button("➕ Nuovo Fornitore");
    private final Button modificaBtn = new Button("✏️ Modifica");
    private final Button eliminaBtn = new Button("🗑️ Elimina");
    private final Button applicaFiltriBtn = new Button("🔍 Applica Filtri");
    private final Button resetFiltriBtn = new Button("🔄 Reset");
    private final TextField ricercaNomeField = new TextField();
    private final TextField ricercaCittaField = new TextField();

    public FornitoreView() {
        setupStyles();
        setupLayout();
        setupTable();
    }

    private void setupStyles() {
        getStyleClass().add("main-container");

        ricercaNomeField.setPromptText("Cerca per nome...");
        ricercaNomeField.setPrefWidth(200);
        ricercaNomeField.getStyleClass().add("text-field-standard");

        ricercaCittaField.setPromptText("Cerca per città...");
        ricercaCittaField.setPrefWidth(150);
        ricercaCittaField.getStyleClass().add("text-field-standard");

        nuovoBtn.getStyleClass().add("btn-primary");
        modificaBtn.getStyleClass().add("btn-secondary");
        modificaBtn.setDisable(true);
        eliminaBtn.getStyleClass().add("btn-danger");
        eliminaBtn.setDisable(true);
        applicaFiltriBtn.getStyleClass().add("btn-secondary");
        resetFiltriBtn.getStyleClass().add("btn-support");

        tableFornitori.setPlaceholder(new Label("Nessun fornitore trovato. Aggiungi il primo fornitore!"));
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

        Label title = new Label("🏢 Gestione Fornitori");
        title.getStyleClass().add("main-title");

        Label subtitle = new Label("Gestisci i fornitori di piante e materiali");
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

        Label cittaLabel = new Label("Città:");
        cittaLabel.getStyleClass().add("field-label");

        grid.add(nomeLabel, 0, 0);
        grid.add(ricercaNomeField, 1, 0);
        grid.add(cittaLabel, 2, 0);
        grid.add(ricercaCittaField, 3, 0);

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

        Label cardTitle = new Label("📋 Elenco Fornitori");
        cardTitle.getStyleClass().add("card-title");

        VBox.setVgrow(tableFornitori, Priority.ALWAYS);

        card.getChildren().addAll(cardTitle, tableFornitori);
        return card;
    }

    @SuppressWarnings("unchecked")
    private void setupTable() {
        TableColumn<Fornitore, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        idCol.setPrefWidth(60);

        TableColumn<Fornitore, String> nomeCol = new TableColumn<>("Nome Fornitore");
        nomeCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getNome()));
        nomeCol.setPrefWidth(200);

        TableColumn<Fornitore, String> indirizzoCol = new TableColumn<>("Indirizzo");
        indirizzoCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getIndirizzo()));
        indirizzoCol.setPrefWidth(250);

        TableColumn<Fornitore, String> telefonoCol = new TableColumn<>("Telefono");
        telefonoCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getNumeroTelefono()));
        telefonoCol.setPrefWidth(120);

        TableColumn<Fornitore, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getEmail()));
        emailCol.setPrefWidth(180);

        TableColumn<Fornitore, String> pivaCol = new TableColumn<>("P.IVA");
        pivaCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getPartitaIva()));
        pivaCol.setPrefWidth(120);

        tableFornitori.getColumns().clear();
        tableFornitori.getColumns().addAll(idCol, nomeCol, indirizzoCol, telefonoCol, emailCol, pivaCol);
        tableFornitori.setItems(fornitoriData);

        tableFornitori.setRowFactory(tv -> {
            TableRow<Fornitore> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    modificaFornitore();
                }
            });
            return row;
        });

        tableFornitori.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean hasSelection = newSel != null;
            modificaBtn.setDisable(!hasSelection);
            eliminaBtn.setDisable(!hasSelection);
        });
    }

    private void modificaFornitore() {
    }

    public void setFornitori(java.util.List<Fornitore> fornitori) {
        fornitoriData.setAll(fornitori);
    }

    public Fornitore getFornitoreSelezionato() {
        return tableFornitori.getSelectionModel().getSelectedItem();
    }

    public void setOnNuovoFornitore(Runnable handler) {
        nuovoBtn.setOnAction(e -> handler.run());
    }

    public void setOnModificaFornitore(Runnable handler) {
        modificaBtn.setOnAction(e -> handler.run());
    }

    public void setOnEliminaFornitore(Runnable handler) {
        eliminaBtn.setOnAction(e -> handler.run());
    }

    public void setOnApplicaFiltri(Runnable handler) {
        applicaFiltriBtn.setOnAction(e -> handler.run());
    }

    public void setOnResetFiltri(Runnable handler) {
        resetFiltriBtn.setOnAction(e -> handler.run());
    }

    public CriteriFiltro getCriteriFiltro() {
        return new CriteriFiltro(
            ricercaNomeField.getText().trim(),
            ricercaCittaField.getText().trim()
        );
    }

    public void resetFiltri() {
        ricercaNomeField.clear();
        ricercaCittaField.clear();
    }

    public boolean confermaEliminazione(Fornitore fornitore) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText("Stai per eliminare il fornitore:");
        alert.setContentText(fornitore.getNome() + " - " + fornitore.getEmail() +
                            "\n\nQuesta operazione non può essere annullata.");

        return alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .isPresent();
    }

    public record CriteriFiltro(String nome, String citta) {}
}
