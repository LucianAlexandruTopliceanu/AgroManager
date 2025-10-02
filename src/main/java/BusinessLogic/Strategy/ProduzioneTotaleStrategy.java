package BusinessLogic.Strategy;

import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.util.List;

public class ProduzioneTotaleStrategy implements DataProcessingStrategy<BigDecimal> {
    @Override
    public ProcessingResult<BigDecimal> execute(Object... data) {
        validateParameters(data);


        List<Raccolto> raccolti = castToRaccoltiList(data[0]);
        int piantagioneId = (Integer) data[1];

        BigDecimal totale = raccolti.stream()
            .filter(r -> r.getPiantagioneId() != null && r.getPiantagioneId() == piantagioneId)
            .map(Raccolto::getQuantitaKg)
            .filter(q -> q != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        String output = String.format("Produzione totale per piantagione %d: %.2f kg",
            piantagioneId, totale);

        return new ProcessingResult<>(totale, output);
    }

    @Override
    public void validateParameters(Object... data) {
        if (data == null) throw new IllegalArgumentException("I parametri non possono essere null");
        if (data.length < 2) throw new IllegalArgumentException("Necessari: lista raccolti e ID piantagione");
        if (!(data[0] instanceof List)) throw new IllegalArgumentException("Primo parametro deve essere List<Raccolto>");
        if (!(data[1] instanceof Integer)) throw new IllegalArgumentException("Secondo parametro deve essere Integer");
    }

    @SuppressWarnings("unchecked")
    private List<Raccolto> castToRaccoltiList(Object obj) {
        return (List<Raccolto>) obj;
    }

    @Override
    public ProcessingType getType() {
        return ProcessingType.CALCULATION;
    }
}
