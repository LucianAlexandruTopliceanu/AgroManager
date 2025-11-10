package View;

import DomainModel.Piantagione;
import DomainModel.StatoPiantagione;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;


public class CambiaStatoDialog extends Stage {
    private final ComboBox<StatoPiantagione> statoCombo = new ComboBox<>();
    private final TextArea noteArea = new TextArea();
    private final Button salvaBtn = new Button("Cambia Stato");
    private final Button annullaBtn = new Button("Annulla");

    private boolean confermato = false;
    private StatoPiantagione statoSelezionato;

    public CambiaStatoDialog(Piantagione piantagione, List<StatoPiantagione> statiDisponibili) {
        setTitle("Cambia Stato Piantagione - ID: " + piantagione.getId());
        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);


        statoCombo.getItems().addAll(statiDisponibili);
        statoCombo.setCellFactory(lv -> new ListCell<StatoPiantagione>() {
            @Override
            protected void updateItem(StatoPiantagione item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getDescrizione());
            }
        });
        statoCombo.setButtonCell(new ListCell<StatoPiantagione>() {
            @Override
            protected void updateItem(StatoPiantagione item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getDescrizione());
            }
        });

        // Seleziona lo stato attuale se presente
        if (piantagione.getStatoPiantagione() != null) {
            statoCombo.setValue(piantagione.getStatoPiantagione());
        }


        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        // Informazioni piantagione
        grid.add(new Label("Piantagione ID:"), 0, 0);
        grid.add(new Label(piantagione.getId().toString()), 1, 0);

        grid.add(new Label("Stato attuale:"), 0, 1);
        String statoAttuale = piantagione.getStatoPiantagione() != null ?
            piantagione.getStatoPiantagione().getDescrizione() : "N/A";
        grid.add(new Label(statoAttuale), 1, 1);

        grid.add(new Label("Nuovo stato:"), 0, 2);
        grid.add(statoCombo, 1, 2);

        grid.add(new Label("Note (opzionale):"), 0, 3);
        noteArea.setPrefRowCount(3);
        noteArea.setPrefColumnCount(30);
        noteArea.setPromptText("Inserisci eventuali note sul cambio stato...");
        grid.add(noteArea, 1, 3);

        // Pulsanti
        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(salvaBtn, annullaBtn);
        grid.add(buttonBar, 1, 4);

        // Event handlers
        salvaBtn.setOnAction(e -> {
            if (validaInput()) {
                statoSelezionato = statoCombo.getValue();
                confermato = true;
                close();
            } else {
                NotificationHelper.showWarning("Selezione richiesta", "Seleziona un nuovo stato per la piantagione.");
            }
        });

        annullaBtn.setOnAction(e -> close());

        setScene(new Scene(grid));
    }

    private boolean validaInput() {
        return statoCombo.getValue() != null;
    }

    public boolean isConfermato() {
        return confermato;
    }

    public StatoPiantagione getStatoSelezionato() {
        return statoSelezionato;
    }

    public String getNote() {
        return noteArea.getText().trim();
    }
}
