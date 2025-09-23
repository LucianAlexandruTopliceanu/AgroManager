package ORM;

public class DAOFactory {
    public static ZonaDAO getZonaDAO() {
        return new ZonaDAO();
    }
    public static PiantaDAO getPiantaDAO() {
        return new PiantaDAO();
    }
    public static FornitoreDAO getFornitoreDAO() {
        return new FornitoreDAO();
    }
    public static PiantagioneDAO getPiantagioneDAO() {
        return new PiantagioneDAO();
    }
    public static RaccoltoDAO getRaccoltoDAO() {
        return new RaccoltoDAO();
    }
}

