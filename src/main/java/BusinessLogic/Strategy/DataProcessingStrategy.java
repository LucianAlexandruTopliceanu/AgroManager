package BusinessLogic.Strategy;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;

public interface DataProcessingStrategy<T> {

    enum ProcessingType {
        CALCULATION("Calcoli numerici"),
        STATISTICS("Analisi statistiche"),
        REPORT("Report dettagliati");

        private final String description;

        ProcessingType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    ProcessingResult<T> execute(Object... data) throws ValidationException, BusinessLogicException;

    ProcessingType getType();

    default void validateParameters(Object... data) throws ValidationException {
        if (data == null) throw new ValidationException("I parametri non possono essere null");
    }
}
