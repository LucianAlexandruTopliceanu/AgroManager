package ORM;
import DomainModel.Pianta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PiantaDAO {

    public void create(Pianta pianta) throws SQLException {
        String sql = "INSERT INTO pianta (tipo, varieta, costo, note, fornitore_id, data_creazione, data_aggiornamento) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, pianta.getTipo());
            stmt.setString(2, pianta.getVarieta());
            stmt.setBigDecimal(3, pianta.getCosto());
            stmt.setString(4, pianta.getNote());
            stmt.setInt(5, pianta.getFornitoreId());
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pianta.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public Pianta read(int id) throws SQLException {
        String sql = "SELECT * FROM pianta WHERE id = ?";
        Pianta pianta = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pianta = mapResultSetToPianta(rs);
                }
            }
        }
        return pianta;
    }

    public void update(Pianta pianta) throws SQLException {
        String sql = "UPDATE pianta SET tipo = ?, varieta = ?, costo = ?, note = ?, fornitore_id = ?, " +
                "data_aggiornamento = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pianta.getTipo());
            stmt.setString(2, pianta.getVarieta());
            stmt.setBigDecimal(3, pianta.getCosto());
            stmt.setString(4, pianta.getNote());
            stmt.setInt(5, pianta.getFornitoreId());
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(7, pianta.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM pianta WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Pianta> findAll() throws SQLException {
        List<Pianta> piante = new ArrayList<>();
        String sql = "SELECT * FROM pianta";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                piante.add(mapResultSetToPianta(rs));
            }
        }
        return piante;
    }

    private Pianta mapResultSetToPianta(ResultSet rs) throws SQLException {
        Pianta pianta = new Pianta();
        pianta.setId(rs.getInt("id"));
        pianta.setTipo(rs.getString("tipo"));
        pianta.setVarieta(rs.getString("varieta"));
        pianta.setCosto(rs.getBigDecimal("costo"));
        pianta.setNote(rs.getString("note"));
        pianta.setFornitoreId(rs.getInt("fornitore_id"));
        pianta.setDataCreazione(rs.getTimestamp("data_creazione").toLocalDateTime());
        pianta.setDataAggiornamento(rs.getTimestamp("data_aggiornamento").toLocalDateTime());
        return pianta;
    }
}
