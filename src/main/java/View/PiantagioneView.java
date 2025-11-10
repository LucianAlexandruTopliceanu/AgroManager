package View;

import DomainModel.Piantagione;
import ORM.DAOFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PiantagioneView extends VBox {

    private final TableView<Piantagione> tablePiantagioni = new TableView<>();
    private final ObservableList<Piantagione> piantagioniData = FXCollections.observableArrayList();
    private final Button nuovoBtn = new Button("➕ Nuova Piantagione");
    private final Button modificaBtn = new Button("✏️ Modifica");
    private final Button eliminaBtn = new Button("🗑️ Elimina");
    private final Button visualizzaStatiBtn = new Button("📊 Stati");
    private final Button applicaFiltriBtn = new Button("🔍 Applica Filtri");
    private final Button resetFiltriBtn = new Button("🔄 Reset");
    private final ComboBox<String> filtroZonaCombo = new ComboBox<>();
    private final ComboBox<String> filtroPiantaCombo = new ComboBox<>();
    private final DatePicker filtroDataDa = new DatePicker();
    private final DatePicker filtroDataA = new DatePicker();

    public PiantagioneView() {
        setupStyles();
        setupLayout();
        setupTable();
    }

    private void setupStyles() {
        getStyleClass().add("main-container");

        filtroZonaCombo.setPromptText("Tutte le zone");
        filtroZonaCombo.setPrefWidth(180);
        filtroZonaCombo.getStyleClass().add("combo-box-standard");

        filtroPiantaCombo.setPromptText("Tutte le piante");
        filtroPiantaCombo.setPrefWidth(180);
        filtroPiantaCombo.getStyleClass().add("combo-box-standard");

        filtroDataDa.setPromptText("Data da");
        filtroDataDa.setPrefWidth(150);
        filtroDataDa.getStyleClass().add("date-picker-standard");

        filtroDataA.setPromptText("Data a");
        filtroDataA.setPrefWidth(150);
        filtroDataA.getStyleClass().add("date-picker-standard");

        nuovoBtn.getStyleClass().add("btn-primary");
        modificaBtn.getStyleClass().add("btn-secondary");
        modificaBtn.setDisable(true);
        eliminaBtn.getStyleClass().add("btn-danger");
        eliminaBtn.setDisable(true);
        visualizzaStatiBtn.getStyleClass().add("btn-secondary");
        visualizzaStatiBtn.setDisable(true);
        applicaFiltriBtn.getStyleClass().add("btn-secondary");
        resetFiltriBtn.getStyleClass().add("btn-support");

        tablePiantagioni.setPlaceholder(new Label("Nessuna piantagione trovata. Aggiungi la prima piantagione!"));
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

        Label title = new Label("🌾 Gestione Piantagioni");
        title.getStyleClass().add("main-title");

        Label subtitle = new Label("Monitora le coltivazioni e il loro stato");
        subtitle.getStyleClass().add("subtitle");

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private VBox createRicercaSection() {
        VBox card = new VBox(15);
        card.getStyleClass().add("styled-card");

        Label cardTitle = new Label("🔍 Filtri Ricerca");
        cardTitle.getStyleClass().add("card-title");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.getStyleClass().add("input-grid");

        Label zonaLabel = new Label("Zona:");
        zonaLabel.getStyleClass().add("field-label");

        Label piantaLabel = new Label("Pianta:");
        piantaLabel.getStyleClass().add("field-label");

        Label dataDaLabel = new Label("Data da:");
        dataDaLabel.getStyleClass().add("field-label");

        Label dataALabel = new Label("Data a:");
        dataALabel.getStyleClass().add("field-label");

        grid.add(zonaLabel, 0, 0);
        grid.add(filtroZonaCombo, 1, 0);
        grid.add(piantaLabel, 2, 0);
        grid.add(filtroPiantaCombo, 3, 0);
        grid.add(dataDaLabel, 0, 1);
        grid.add(filtroDataDa, 1, 1);
        grid.add(dataALabel, 2, 1);
        grid.add(filtroDataA, 3, 1);

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
        secondaryGroup.getChildren().addAll(modificaBtn, eliminaBtn, visualizzaStatiBtn);

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

        Label cardTitle = new Label("📋 Elenco Piantagioni");
        cardTitle.getStyleClass().add("card-title");

        VBox.setVgrow(tablePiantagioni, Priority.ALWAYS);

        card.getChildren().addAll(cardTitle, tablePiantagioni);
        return card;
    }

    @SuppressWarnings("unchecked")
    private void setupTable() {
        TableColumn<Piantagione, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        idCol.setPrefWidth(60);

        TableColumn<Piantagione, String> zonaCol = new TableColumn<>("Zona");
        zonaCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getZonaId() != null ? "ID " + cell.getValue().getZonaId() : "N/A"));
        zonaCol.setPrefWidth(100);

        TableColumn<Piantagione, String> piantaCol = new TableColumn<>("Pianta");
        piantaCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getPiantaId() != null ? "ID " + cell.getValue().getPiantaId() : "N/A"));
        piantaCol.setPrefWidth(100);

        TableColumn<Piantagione, String> dataCol = new TableColumn<>("Data Piantagione");
        dataCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getMessaADimora() != null ?
                cell.getValue().getMessaADimora().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"));
        dataCol.setPrefWidth(130);

        TableColumn<Piantagione, String> numPianteCol = new TableColumn<>("N° Piante");
        numPianteCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getQuantitaPianta() != null ?
                cell.getValue().getQuantitaPianta().toString() : "N/A"));
        numPianteCol.setPrefWidth(100);

        TableColumn<Piantagione, String> statoCol = new TableColumn<>("Stato");
        statoCol.setCellValueFactory(cell -> {
            Integer numPiante = cell.getValue().getQuantitaPianta();
            String stato = numPiante != null && numPiante > 100 ? "🟢 Attiva" :
                          numPiante != null && numPiante > 0 ? "🟡 In crescita" : "⚪ Vuota";
            return new javafx.beans.property.SimpleStringProperty(stato);
        });
        statoCol.setPrefWidth(120);

        TableColumn<Piantagione, String> noteCol = new TableColumn<>("Stato Dettaglio");
        noteCol.setCellValueFactory(cell -> {
            DomainModel.StatoPiantagione stato = cell.getValue().getStatoPiantagione();
            return new javafx.beans.property.SimpleStringProperty(
                stato != null ? stato.getDescrizione() : "N/A");
        });
        noteCol.setPrefWidth(200);

        tablePiantagioni.getColumns().clear();
        tablePiantagioni.getColumns().addAll(idCol, zonaCol, piantaCol, dataCol, numPianteCol, statoCol, noteCol);
        tablePiantagioni.setItems(piantagioniData);

        tablePiantagioni.setRowFactory(tv -> {
            TableRow<Piantagione> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    modificaPiantagione();
                }
            });
            return row;
        });

        tablePiantagioni.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean hasSelection = newSel != null;
            modificaBtn.setDisable(!hasSelection);
            eliminaBtn.setDisable(!hasSelection);
            visualizzaStatiBtn.setDisable(!hasSelection);
        });
    }

    private void modificaPiantagione() {
        if (getPiantagioneSelezionata() != null) {
            // Il controller gestirà l'apertura del dialog
        }
    }

    public void setPiantagioni(java.util.List<Piantagione> piantagioni) {
        piantagioniData.setAll(piantagioni);

        // Carica anche le informazioni di zona e pianta per visualizzare i nomi
        if (!piantagioni.isEmpty()) {
            try {
                DAOFactory daoFactory = DAOFactory.getInstance();
                var zone = daoFactory.getZonaDAO().findAll();
                var piante = daoFactory.getPiantaDAO().findAll();

                // Aggiorna la visualizzazione delle colonne con i nomi
                piantagioni.forEach(p -> {
                    zone.stream()
                        .filter(z -> z.getId().equals(p.getZonaId()))
                        .findFirst()
                        .ifPresent(z -> {});

                    piante.stream()
                        .filter(pi -> pi.getId().equals(p.getPiantaId()))
                        .findFirst()
                        .ifPresent(pi -> {});
                });
            } catch (Exception e) {
                // Ignora errori di caricamento nomi
            }
        }
    }

    public Piantagione getPiantagioneSelezionata() {
        return tablePiantagioni.getSelectionModel().getSelectedItem();
    }

    public void setZone(java.util.List<String> zone) {
        filtroZonaCombo.getItems().clear();
        filtroZonaCombo.getItems().add("Tutte le zone");
        filtroZonaCombo.getItems().addAll(zone);
        filtroZonaCombo.getSelectionModel().selectFirst();
    }

    public void setPiante(java.util.List<String> piante) {
        filtroPiantaCombo.getItems().clear();
        filtroPiantaCombo.getItems().add("Tutte le piante");
        filtroPiantaCombo.getItems().addAll(piante);
        filtroPiantaCombo.getSelectionModel().selectFirst();
    }

    public void setFiltroPiantaItems(java.util.List<String> items) {
        setPiante(items);
    }

    public void setFiltroZonaItems(java.util.List<String> items) {
        setZone(items);
    }

    public void setFiltroStatoItems(java.util.List<String> items) {
    }

    public void setOnCambiaStatoPiantagione(Runnable handler) {
        visualizzaStatiBtn.setOnAction(e -> handler.run());
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

    public void setOnVisualizzaStati(Runnable handler) {
        visualizzaStatiBtn.setOnAction(e -> handler.run());
    }

    public void setOnApplicaFiltri(Runnable handler) {
        applicaFiltriBtn.setOnAction(e -> handler.run());
    }

    public void setOnResetFiltri(Runnable handler) {
        resetFiltriBtn.setOnAction(e -> handler.run());
    }

    public CriteriFiltro getCriteriFiltro() {
        String zona = filtroZonaCombo.getValue();
        if (zona != null && zona.equals("Tutte le zone")) zona = null;

        String pianta = filtroPiantaCombo.getValue();
        if (pianta != null && pianta.equals("Tutte le piante")) pianta = null;

        return new CriteriFiltro(zona, pianta, filtroDataDa.getValue(), filtroDataA.getValue());
    }

    public void resetFiltri() {
        filtroZonaCombo.getSelectionModel().selectFirst();
        filtroPiantaCombo.getSelectionModel().selectFirst();
        filtroDataDa.setValue(null);
        filtroDataA.setValue(null);
    }

    public boolean confermaEliminazione(Piantagione piantagione) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText("Stai per eliminare la piantagione:");
        alert.setContentText("ID: " + piantagione.getId() +
                            "\nData: " + piantagione.getMessaADimora() +
                            "\n\nQuesta operazione non può essere annullata.");
        return alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .isPresent();
    }

    public record CriteriFiltro(String zona, String pianta,
                                java.time.LocalDate dataDa, java.time.LocalDate dataA) {}
}
