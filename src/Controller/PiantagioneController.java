package Controller;

import ORM.DAOFactory;
import ORM.PiantagioneDAO;
import DomainModel.Piantagione;
import java.sql.SQLException;
import java.util.List;

public class PiantagioneController {
    private final PiantagioneDAO piantagioneDAO;

    public PiantagioneController() {
        this.piantagioneDAO = DAOFactory.getPiantagioneDAO();
    }

    public void aggiungiPiantagione(Piantagione piantagione) throws SQLException {
        piantagioneDAO.create(piantagione);
    }

    public Piantagione getPiantagione(int id) throws SQLException {
        return piantagioneDAO.read(id);
    }

    public void aggiornaPiantagione(Piantagione piantagione) throws SQLException {
        piantagioneDAO.update(piantagione);
    }

    public void eliminaPiantagione(int id) throws SQLException {
        piantagioneDAO.delete(id);
    }

    public List<Piantagione> getAllPiantagioni() throws SQLException {
        return piantagioneDAO.findAll();
    }
}

