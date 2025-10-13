package ORM;
import DomainModel.Pianta;
import java.sql.*;
import java.util.List;

public class PiantaDAO extends BaseDAO<Pianta> {
    @Override
    protected String getTableName() {
        return "pianta";
    }

    @Override
    protected Pianta mapResultSetToEntity(ResultSet rs) throws SQLException {
        Pianta pianta = new Pianta();
        pianta.setId(rs.getInt("id"));
        pianta.setTipo(rs.getString("tipo"));
        pianta.setVarieta(rs.getString("varieta"));
        pianta.setCosto(rs.getBigDecimal("costo"));
        pianta.setNote(rs.getString("note"));
        pianta.setFornitoreId(rs.getInt("fornitore_id"));
        return pianta;
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Pianta pianta) throws SQLException {
        stmt.setString(1, pianta.getTipo());
        stmt.setString(2, pianta.getVarieta());
        stmt.setBigDecimal(3, pianta.getCosto());
        stmt.setString(4, pianta.getNote());
        stmt.setInt(5, pianta.getFornitoreId());
        stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
        stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Pianta pianta) throws SQLException {
        stmt.setString(1, pianta.getTipo());
        stmt.setString(2, pianta.getVarieta());
        stmt.setBigDecimal(3, pianta.getCosto());
        stmt.setString(4, pianta.getNote());
        stmt.setInt(5, pianta.getFornitoreId());
        stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
        stmt.setInt(7, pianta.getId());
    }

    @Override
    protected String getInsertSQL() {
        return "INSERT INTO pianta (tipo, varieta, costo, note, fornitore_id, data_creazione, data_aggiornamento) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSQL() {
        return "UPDATE pianta SET tipo = ?, varieta = ?, costo = ?, note = ?, fornitore_id = ?, data_aggiornamento = ? WHERE id = ?";
    }

    @Override
    protected void setEntityId(Pianta pianta, int id) {
        pianta.setId(id);
    }

    //TODO: Valutare se meglio cercare nel db o nelle liste gia caricate in memoria
    public List<Pianta> findByFornitore(Integer fornitoreId) throws SQLException {
        String sql = "SELECT * FROM " + getTableName() + " WHERE fornitore_id = ?";
        List<Pianta> piante = new java.util.ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fornitoreId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    piante.add(mapResultSetToEntity(rs));
                }
            }
        }
        return piante;
    }
}
