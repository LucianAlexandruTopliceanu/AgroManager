package ORM;
import DomainModel.Raccolto;
import java.sql.*;

public class RaccoltoDAO extends BaseDAO<Raccolto> {
    @Override
    protected String getTableName() {
        return "raccolto";
    }

    @Override
    protected Raccolto mapResultSetToEntity(ResultSet rs) throws SQLException {
        Raccolto raccolto = new Raccolto();
        raccolto.setId(rs.getInt("id"));
        raccolto.setDataRaccolto(rs.getDate("data_raccolto").toLocalDate());
        raccolto.setQuantitaKg(rs.getBigDecimal("quantita_kg"));
        raccolto.setNote(rs.getString("note"));
        raccolto.setPiantagioneId(rs.getInt("piantagione_id"));
        return raccolto;
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Raccolto raccolto) throws SQLException {
        stmt.setDate(1, Date.valueOf(raccolto.getDataRaccolto()));
        stmt.setBigDecimal(2, raccolto.getQuantitaKg());
        stmt.setString(3, raccolto.getNote());
        stmt.setInt(4, raccolto.getPiantagioneId());
        stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
        stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Raccolto raccolto) throws SQLException {
        stmt.setDate(1, Date.valueOf(raccolto.getDataRaccolto()));
        stmt.setBigDecimal(2, raccolto.getQuantitaKg());
        stmt.setString(3, raccolto.getNote());
        stmt.setInt(4, raccolto.getPiantagioneId());
        stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
        stmt.setInt(6, raccolto.getId());
    }

    @Override
    protected String getInsertSQL() {
        return "INSERT INTO raccolto (data_raccolto, quantita_kg, note, piantagione_id, data_creazione, data_aggiornamento) VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSQL() {
        return "UPDATE raccolto SET data_raccolto = ?, quantita_kg = ?, note = ?, piantagione_id = ?, data_aggiornamento = ? WHERE id = ?";
    }

    @Override
    protected void setEntityId(Raccolto raccolto, int id) {
        raccolto.setId(id);
    }


    public java.util.List<Raccolto> findByPiantagione(Integer piantagioneId) throws SQLException {
        String sql = "SELECT * FROM " + getTableName() + " WHERE piantagione_id = ?";
        java.util.List<Raccolto> raccolti = new java.util.ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, piantagioneId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    raccolti.add(mapResultSetToEntity(rs));
                }
            }
        }
        return raccolti;
    }
}