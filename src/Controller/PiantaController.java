package Controller;

import ORM.DAOFactory;
import ORM.PiantaDAO;
import DomainModel.Pianta;
import java.sql.SQLException;
import java.util.List;

public class PiantaController {
    private final PiantaDAO piantaDAO;

    public PiantaController() {
        this.piantaDAO = DAOFactory.getPiantaDAO();
    }

    public void aggiungiPianta(Pianta pianta) throws SQLException {
        piantaDAO.create(pianta);
    }

    public Pianta getPianta(int id) throws SQLException {
        return piantaDAO.read(id);
    }

    public void aggiornaPianta(Pianta pianta) throws SQLException {
        piantaDAO.update(pianta);
    }

    public void eliminaPianta(int id) throws SQLException {
        piantaDAO.delete(id);
    }

    public List<Pianta> getAllPiante() throws SQLException {
        return piantaDAO.findAll();
    }
}

