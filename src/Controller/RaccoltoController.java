package Controller;

import ORM.DAOFactory;
import ORM.RaccoltoDAO;
import DomainModel.Raccolto;
import java.sql.SQLException;
import java.util.List;

public class RaccoltoController {
    private final RaccoltoDAO raccoltoDAO;

    public RaccoltoController() {
        this.raccoltoDAO = DAOFactory.getRaccoltoDAO();
    }

    public void aggiungiRaccolto(Raccolto raccolto) throws SQLException {
        raccoltoDAO.create(raccolto);
    }

    public Raccolto getRaccolto(int id) throws SQLException {
        return raccoltoDAO.read(id);
    }

    public void aggiornaRaccolto(Raccolto raccolto) throws SQLException {
        raccoltoDAO.update(raccolto);
    }

    public void eliminaRaccolto(int id) throws SQLException {
        raccoltoDAO.delete(id);
    }

    public List<Raccolto> getAllRaccolti() throws SQLException {
        return raccoltoDAO.findAll();
    }
}
