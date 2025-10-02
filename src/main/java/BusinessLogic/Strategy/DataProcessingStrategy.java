package BusinessLogic.Strategy;


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


    ProcessingResult<T> execute(Object... data);


    ProcessingType getType();

    default void validateParameters(Object... data) {
        if (data == null) throw new IllegalArgumentException("I parametri non possono essere null");
    }
}
