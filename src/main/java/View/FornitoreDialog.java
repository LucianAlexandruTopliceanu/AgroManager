package View;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import DomainModel.Fornitore;

public class FornitoreDialog extends Stage {
    private final TextField nomeField = new TextField();
    private final TextField indirizzoField = new TextField();
    private final TextField telefonoField = new TextField();
    private final Button salvaBtn = new Button("Salva");
    private Fornitore fornitore;
    private boolean confermato = false;

    public FornitoreDialog(Fornitore fornitore) {
        this.fornitore = fornitore != null ? fornitore : new Fornitore();
        setTitle(fornitore == null ? "Nuovo Fornitore" : "Modifica Fornitore");
        initModality(Modality.APPLICATION_MODAL);
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("Indirizzo:"), 0, 1);
        grid.add(indirizzoField, 1, 1);
        grid.add(new Label("Telefono:"), 0, 2);
        grid.add(telefonoField, 1, 2);
        grid.add(salvaBtn, 1, 3);
        if (fornitore != null) {
            nomeField.setText(fornitore.getNome());
            indirizzoField.setText(fornitore.getIndirizzo());
            telefonoField.setText(fornitore.getNumeroTelefono());
        }
        salvaBtn.setOnAction(e -> {
            if (validaInput()) {
                this.fornitore.setNome(nomeField.getText());
                this.fornitore.setIndirizzo(indirizzoField.getText());
                this.fornitore.setNumeroTelefono(telefonoField.getText());
                confermato = true;
                close();
            } else {
                new Alert(Alert.AlertType.ERROR, "Compila tutti i campi correttamente.").showAndWait();
            }
        });
        setScene(new Scene(grid));
    }

    private boolean validaInput() {
        return !nomeField.getText().isEmpty() && !indirizzoField.getText().isEmpty() && !telefonoField.getText().isEmpty();
    }

    public Fornitore getFornitore() { return fornitore; }
    public boolean isConfermato() { return confermato; }
}

