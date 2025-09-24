package Controller;

import ORM.DAOFactory;
import ORM.ZonaDAO;
import DomainModel.Zona;
import java.sql.SQLException;
import java.util.List;
import BussinesLogic.ZonaService;

public class ZonaController {
    private final ZonaDAO zonaDAO;
    private final ZonaService zonaService;

    public ZonaController() {
        this.zonaDAO = DAOFactory.getZonaDAO();
        this.zonaService = new ZonaService();
    }

    public void aggiungiZona(Zona zona) throws SQLException {
        zonaService.validaZona(zona);
        zonaDAO.create(zona);
    }

    public Zona getZona(int id) throws SQLException {
        return zonaDAO.read(id);
    }

    public void aggiornaZona(Zona zona) throws SQLException {
        zonaDAO.update(zona);
    }

    public void eliminaZona(int id) throws SQLException {
        zonaDAO.delete(id);
    }

    public List<Zona> getAllZone() throws SQLException {
        return zonaDAO.findAll();
    }
}
