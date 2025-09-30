package BusinessLogic.Strategy;


public class DataProcessingContext {

    public ProcessingResult<?> executeStrategy(DataProcessingStrategy<?> strategy, Object... data) {
        strategy.validateParameters(data);
        return strategy.execute(data);
    }


    public ProcessingResult<?> executeStrategyOfType(DataProcessingStrategy.ProcessingType expectedType,
                                                   DataProcessingStrategy<?> strategy,
                                                   Object... data) {
        if (strategy.getType() != expectedType) {
            throw new IllegalArgumentException(
                String.format("La strategia è di tipo %s ma è richiesto il tipo %s",
                    strategy.getType(), expectedType)
            );
        }
        return executeStrategy(strategy, data);
    }
}
