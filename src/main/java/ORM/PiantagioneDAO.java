package ORM;
import DomainModel.Piantagione;
import java.sql.*;

public class PiantagioneDAO extends BaseDAO<Piantagione> {
    @Override
    protected String getTableName() {
        return "piantagione";
    }

    @Override
    protected Piantagione mapResultSetToEntity(ResultSet rs) throws SQLException {
        Piantagione piantagione = new Piantagione();
        piantagione.setId(rs.getInt("id"));
        piantagione.setQuantitaPianta(rs.getInt("quantita_pianta"));
        piantagione.setMessaADimora(rs.getDate("messa_a_dimora").toLocalDate());
        piantagione.setPiantaId(rs.getInt("id_pianta"));
        piantagione.setZonaId(rs.getInt("id_zona"));
        // altri campi se necessari
        return piantagione;
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Piantagione piantagione) throws SQLException {
        stmt.setInt(1, piantagione.getQuantitaPianta());
        stmt.setDate(2, Date.valueOf(piantagione.getMessaADimora()));
        stmt.setInt(3, piantagione.getPiantaId());
        stmt.setInt(4, piantagione.getZonaId());
        stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
        stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Piantagione piantagione) throws SQLException {
        stmt.setInt(1, piantagione.getQuantitaPianta());
        stmt.setDate(2, Date.valueOf(piantagione.getMessaADimora()));
        stmt.setInt(3, piantagione.getPiantaId());
        stmt.setInt(4, piantagione.getZonaId());
        stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
        stmt.setInt(6, piantagione.getId());
    }

    @Override
    protected String getInsertSQL() {
        return "INSERT INTO piantagione (quantita_pianta, messa_a_dimora, id_pianta, id_zona, data_creazione, data_aggiornamento) VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSQL() {
        return "UPDATE piantagione SET quantita_pianta = ?, messa_a_dimora = ?, id_pianta = ?, id_zona = ?, data_aggiornamento = ? WHERE id = ?";
    }

    @Override
    protected void setEntityId(Piantagione piantagione, int id) {
        piantagione.setId(id);
    }


    public java.util.List<Piantagione> findByZona(Integer zonaId) throws SQLException {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id_zona = ?";
        java.util.List<Piantagione> piantagioni = new java.util.ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, zonaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    piantagioni.add(mapResultSetToEntity(rs));
                }
            }
        }
        return piantagioni;
    }


    public java.util.List<Piantagione> findByPianta(Integer piantaId) throws SQLException {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id_pianta = ?";
        java.util.List<Piantagione> piantagioni = new java.util.ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, piantaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    piantagioni.add(mapResultSetToEntity(rs));
                }
            }
        }
        return piantagioni;
    }
}