package Controller;

import ORM.DAOFactory;
import ORM.FornitoreDAO;
import DomainModel.Fornitore;
import java.sql.SQLException;
import java.util.List;

public class FornitoreController {
    private final FornitoreDAO fornitoreDAO;

    public FornitoreController() {
        this.fornitoreDAO = DAOFactory.getFornitoreDAO();
    }

    public void aggiungiFornitore(Fornitore fornitore) throws SQLException {
        fornitoreDAO.create(fornitore);
    }

    public Fornitore getFornitore(int id) throws SQLException {
        return fornitoreDAO.read(id);
    }

    public void aggiornaFornitore(Fornitore fornitore) throws SQLException {
        fornitoreDAO.update(fornitore);
    }

    public void eliminaFornitore(int id) throws SQLException {
        fornitoreDAO.delete(id);
    }

    public List<Fornitore> getAllFornitori() throws SQLException {
        return fornitoreDAO.findAll();
    }
}

