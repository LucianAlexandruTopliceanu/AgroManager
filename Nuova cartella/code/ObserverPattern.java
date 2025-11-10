public class FornitoreView extends VBox {
    private final Button nuovoBtn = new Button("Nuovo Fornitore");
    // Altri componenti UI...

    public void setOnNuovoFornitore(Runnable handler) {
        nuovoBtn.setOnAction(e -> handler.run());
    }
    // Altri metodi per gestire l'interfaccia...
}

public class FornitoreController {
    private final FornitoreService fornitoreService;
    private final FornitoreView fornitoreView;
    // Altri attributi...

    private void setupEventHandlers() {
        fornitoreView.setOnNuovoFornitore(this::onNuovoFornitore);
        // Altri handler...
    }
    public void onNuovoFornitore() {...}
    
    // Altri metodi...
}