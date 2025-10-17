package BusinessLogic.Strategy;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.util.List;

public class ProduzioneTotaleStrategy implements DataProcessingStrategy<BigDecimal> {
    @Override
    public ProcessingResult<BigDecimal> execute(Object... data) throws ValidationException {
        validateParameters(data);

        List<Raccolto> raccolti = castToRaccoltiList(data[0]);
        int piantagioneId = (Integer) data[1];

        BigDecimal totale = raccolti.stream()
            .filter(r -> r.getPiantagioneId() != null && r.getPiantagioneId() == piantagioneId)
            .map(Raccolto::getQuantitaKg)
            .filter(q -> q != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ProcessingResult<>(totale);
    }

    @Override
    public void validateParameters(Object... data) throws ValidationException {
        if (data == null) throw new ValidationException("I parametri non possono essere null");
        if (data.length < 2) throw new ValidationException("Necessari: lista raccolti e ID piantagione");
        if (!(data[0] instanceof List)) throw new ValidationException("Primo parametro deve essere List<Raccolto>");
        if (!(data[1] instanceof Integer)) throw new ValidationException("Secondo parametro deve essere Integer (ID piantagione)");
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
