    public ProcessingResult<?> eseguiStrategiaConDati(DataProcessingStrategy.ProcessingType tipo, String strategia, String piantagioneId,
                                                       LocalDate dataInizio, LocalDate dataFine, Integer topN)
            throws ValidationException, DataAccessException, BusinessLogicException {
        try {
            List<Raccolto> raccolti = DAOFactory.getRaccoltoDAO().findAll();
            List<Piantagione> piantagioni = DAOFactory.getPiantagioneDAO().findAll();
            List<Zona> zone = DAOFactory.getZonaDAO().findAll();

            DataProcessingStrategy<?> strategy = StrategyFactory.createStrategy(strategia);

            if (strategy.getType() != tipo) {
                throw new ValidationException("tipoStrategia", strategia,
                        "non è del tipo richiesto " + tipo);
            }

            Object[] params = prepareParameters(strategy, raccolti, piantagioni, zone,
                piantagioneId, dataInizio, dataFine, topN);

            return strategy.execute(params);

        } catch (SQLException e) {
            throw DataAccessException.queryError("recupero dati per elaborazione", e);
        }
    }