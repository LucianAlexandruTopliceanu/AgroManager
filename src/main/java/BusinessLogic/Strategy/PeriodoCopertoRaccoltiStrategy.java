package BusinessLogic.Strategy;

import BusinessLogic.Exception.ValidationException;
import DomainModel.Raccolto;
import java.time.LocalDate;
import java.util.*;

/**
 * Strategy per calcolare il periodo coperto dai raccolti
 */
public class PeriodoCopertoRaccoltiStrategy implements DataProcessingStrategy<Map<String, Object>> {

    @Override
    public ProcessingResult<Map<String, Object>> execute(Object... data) throws ValidationException {
        validateParameters(data);

        List<Raccolto> raccolti = castToRaccoltiList(data[0]);

        Map<String, Object> periodo = new LinkedHashMap<>();

        Optional<LocalDate> primaData = raccolti.stream()
            .map(Raccolto::getDataRaccolto)
            .filter(Objects::nonNull)
            .min(LocalDate::compareTo);

        Optional<LocalDate> ultimaData = raccolti.stream()
            .map(Raccolto::getDataRaccolto)
            .filter(Objects::nonNull)
            .max(LocalDate::compareTo);

        if (primaData.isPresent() && ultimaData.isPresent()) {
            periodo.put("primaData", primaData.get());
            periodo.put("ultimaData", ultimaData.get());
            periodo.put("hasData", true);
        } else {
            periodo.put("primaData", null);
            periodo.put("ultimaData", null);
            periodo.put("hasData", false);
        }

        return new ProcessingResult<>(periodo);
    }

    @Override
    public void validateParameters(Object... data) throws ValidationException {
        if (data == null) {
            throw new ValidationException("I parametri non possono essere null");
        }
        if (data.length < 1) {
            throw new ValidationException("Necessaria lista raccolti");
        }
        if (!(data[0] instanceof List)) {
            throw new ValidationException("Primo parametro deve essere List<Raccolto>");
        }
    }

    @Override
    public ProcessingType getType() {
        return ProcessingType.CALCULATION;
    }

    @SuppressWarnings("unchecked")
    private List<Raccolto> castToRaccoltiList(Object obj) {
        return (List<Raccolto>) obj;
    }
}
