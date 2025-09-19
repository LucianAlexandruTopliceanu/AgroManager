package ORM;
import DomainModel.Piantagione;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PiantagioneDAO {

    public void create(Piantagione piantagione) throws SQLException {
        String sql = "INSERT INTO piantagione (quantita_pianta, messa_a_dimora, id_pianta, id_zona, " +
                "data_creazione, data_aggiornamento) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, piantagione.getQuantitaPianta());
            stmt.setDate(2, Date.valueOf(piantagione.getMessaADimora()));
            stmt.setInt(3, piantagione.getPiantaId());
            stmt.setInt(4, piantagione.getZonaId());
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    piantagione.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public Piantagione read(int id) throws SQLException {
        String sql = "SELECT * FROM piantagione WHERE id = ?";
        Piantagione piantagione = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    piantagione = mapResultSetToPiantagione(rs);
                }
            }
        }
        return piantagione;
    }

    public void update(Piantagione piantagione) throws SQLException {
        String sql = "UPDATE piantagione SET quantita_pianta = ?, messa_a_dimora = ?, id_pianta = ?, " +
                "id_zona = ?, data_aggiornamento = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, piantagione.getQuantitaPianta());
            stmt.setDate(2, Date.valueOf(piantagione.getMessaADimora()));
            stmt.setInt(3, piantagione.getPiantaId());
            stmt.setInt(4, piantagione.getZonaId());
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(6, piantagione.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM piantagione WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Piantagione> findAll() throws SQLException {
        List<Piantagione> piantagioni = new ArrayList<>();
        String sql = "SELECT * FROM piantagione";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                piantagioni.add(mapResultSetToPiantagione(rs));
            }
        }
        return piantagioni;
    }

    private Piantagione mapResultSetToPiantagione(ResultSet rs) throws SQLException {
        Piantagione piantagione = new Piantagione();
        piantagione.setId(rs.getInt("id"));
        piantagione.setQuantitaPianta(rs.getInt("quantita_pianta"));
        piantagione.setMessaADimora(rs.getDate("messa_a_dimora").toLocalDate());
        piantagione.setPiantaId(rs.getInt("id_pianta"));
        piantagione.setZonaId(rs.getInt("id_zona"));
        piantagione.setDataCreazione(rs.getTimestamp("data_creazione").toLocalDateTime());
        piantagione.setDataAggiornamento(rs.getTimestamp("data_aggiornamento").toLocalDateTime());
        return piantagione;
    }
}