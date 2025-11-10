package ORM;

public class DAOFactory {

    private DAOFactory() {}

    private static class FactoryHolder {
        private static final DAOFactory INSTANCE = new DAOFactory();
    }

    public static DAOFactory getInstance() {
        return FactoryHolder.INSTANCE;
    }

    private static class DAOHolders {
        private static final ZonaDAO ZONA_DAO = new ZonaDAO();
        private static final PiantaDAO PIANTA_DAO = new PiantaDAO();
        private static final FornitoreDAO FORNITORE_DAO = new FornitoreDAO();
        private static final PiantagioneDAO PIANTAGIONE_DAO = new PiantagioneDAO();
        private static final RaccoltoDAO RACCOLTO_DAO = new RaccoltoDAO();
        private static final StatoPiantagioneDAO STATO_PIANTAGIONE_DAO = new StatoPiantagioneDAO();
    }

    public ZonaDAO getZonaDAO() {
        return DAOHolders.ZONA_DAO;
    }

    public PiantaDAO getPiantaDAO() {
        return DAOHolders.PIANTA_DAO;
    }

    public FornitoreDAO getFornitoreDAO() {
        return DAOHolders.FORNITORE_DAO;
    }

    public PiantagioneDAO getPiantagioneDAO() {
        return DAOHolders.PIANTAGIONE_DAO;
    }

    public RaccoltoDAO getRaccoltoDAO() {
        return DAOHolders.RACCOLTO_DAO;
    }

    public StatoPiantagioneDAO getStatoPiantagioneDAO() {
        return DAOHolders.STATO_PIANTAGIONE_DAO;
    }
}
