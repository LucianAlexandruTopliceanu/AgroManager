package BusinessLogic.Strategy;

import BusinessLogic.Exception.ValidationException;


public class DataProcessingContext {
    private DataProcessingStrategy strategy;

    public ProcessingResult<?> executeStrategy(DataProcessingStrategy strategy, Object... data) throws ValidationException {
        try {
            strategy.validateParameters(data);
            return strategy.execute(data);
        } catch (ValidationException e) {
            throw e; // Rilancia le ValidationException
        } catch (Exception e) {
            throw new ValidationException("Errore durante l'esecuzione della strategia: " + e.getMessage());
        }
    }


    public ProcessingResult<?> executeStrategyOfType(DataProcessingStrategy.ProcessingType expectedType,
                                                   DataProcessingStrategy<?> strategy,
                                                   Object... data) throws ValidationException {
        if (strategy.getType() != expectedType) {
            throw new IllegalArgumentException(
                String.format("La strategia è di tipo %s ma è richiesto il tipo %s",
                    strategy.getType(), expectedType)
            );
        }
        return executeStrategy(strategy, data);
    }
}
