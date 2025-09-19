package ORM;
import DomainModel.Raccolto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RaccoltoDAO {

    public void create(Raccolto raccolto) throws SQLException {
        String sql = "INSERT INTO raccolto (data_raccolto, quantita_kg, note, piantagione_id, " +
                "data_creazione, data_aggiornamento) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, Date.valueOf(raccolto.getDataRaccolto()));
            stmt.setBigDecimal(2, raccolto.getQuantitaKg());
            stmt.setString(3, raccolto.getNote());
            stmt.setInt(4, raccolto.getPiantagioneId());
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    raccolto.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public Raccolto read(int id) throws SQLException {
        String sql = "SELECT * FROM raccolto WHERE id = ?";
        Raccolto raccolto = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    raccolto = mapResultSetToRaccolto(rs);
                }
            }
        }
        return raccolto;
    }

    public void update(Raccolto raccolto) throws SQLException {
        String sql = "UPDATE raccolto SET data_raccolto = ?, quantita_kg = ?, note = ?, " +
                "piantagione_id = ?, data_aggiornamento = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(raccolto.getDataRaccolto()));
            stmt.setBigDecimal(2, raccolto.getQuantitaKg());
            stmt.setString(3, raccolto.getNote());
            stmt.setInt(4, raccolto.getPiantagioneId());
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(6, raccolto.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM raccolto WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Raccolto> findAll() throws SQLException {
        List<Raccolto> raccolti = new ArrayList<>();
        String sql = "SELECT * FROM raccolto";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                raccolti.add(mapResultSetToRaccolto(rs));
            }
        }
        return raccolti;
    }

    private Raccolto mapResultSetToRaccolto(ResultSet rs) throws SQLException {
        Raccolto raccolto = new Raccolto();
        raccolto.setId(rs.getInt("id"));
        raccolto.setDataRaccolto(rs.getDate("data_raccolto").toLocalDate());
        raccolto.setQuantitaKg(rs.getBigDecimal("quantita_kg"));
        raccolto.setNote(rs.getString("note"));
        raccolto.setPiantagioneId(rs.getInt("piantagione_id"));
        raccolto.setDataCreazione(rs.getTimestamp("data_creazione").toLocalDateTime());
        raccolto.setDataAggiornamento(rs.getTimestamp("data_aggiornamento").toLocalDateTime());
        return raccolto;
    }
}