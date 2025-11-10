public class ProduzioneTotaleStrategy implements DataProcessingStrategy<BigDecimal> {
    @Override
    public ProcessingResult<BigDecimal> execute(Object... data) throws ValidationException {...}

    @Override
    public void validateParameters(Object... data) throws ValidationException {...}

    private List<Raccolto> castToRaccoltiList(Object obj) {...}

    @Override
    public ProcessingType getType() {...}
}
