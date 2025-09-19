package ORM;
import DomainModel.Fornitore;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FornitoreDAO {

    public void create(Fornitore fornitore) throws SQLException {
        String sql = "INSERT INTO fornitore (nome, indirizzo, numero_telefono, email, partita_iva, " +
                "data_creazione, data_aggiornamento) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, fornitore.getNome());
            stmt.setString(2, fornitore.getIndirizzo());
            stmt.setString(3, fornitore.getNumeroTelefono());
            stmt.setString(4, fornitore.getEmail());
            stmt.setString(5, fornitore.getPartitaIva());
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    fornitore.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public Fornitore read(int id) throws SQLException {
        String sql = "SELECT * FROM fornitore WHERE id = ?";
        Fornitore fornitore = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    fornitore = mapResultSetToFornitore(rs);
                }
            }
        }
        return fornitore;
    }

    public void update(Fornitore fornitore) throws SQLException {
        String sql = "UPDATE fornitore SET nome = ?, indirizzo = ?, numero_telefono = ?, email = ?, " +
                "partita_iva = ?, data_aggiornamento = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fornitore.getNome());
            stmt.setString(2, fornitore.getIndirizzo());
            stmt.setString(3, fornitore.getNumeroTelefono());
            stmt.setString(4, fornitore.getEmail());
            stmt.setString(5, fornitore.getPartitaIva());
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(7, fornitore.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM fornitore WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Fornitore> findAll() throws SQLException {
        List<Fornitore> fornitori = new ArrayList<>();
        String sql = "SELECT * FROM fornitore";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                fornitori.add(mapResultSetToFornitore(rs));
            }
        }
        return fornitori;
    }

    private Fornitore mapResultSetToFornitore(ResultSet rs) throws SQLException {
        Fornitore fornitore = new Fornitore();
        fornitore.setId(rs.getInt("id"));
        fornitore.setNome(rs.getString("nome"));
        fornitore.setIndirizzo(rs.getString("indirizzo"));
        fornitore.setNumeroTelefono(rs.getString("numero_telefono"));
        fornitore.setEmail(rs.getString("email"));
        fornitore.setPartitaIva(rs.getString("partita_iva"));
        fornitore.setDataCreazione(rs.getTimestamp("data_creazione").toLocalDateTime());
        fornitore.setDataAggiornamento(rs.getTimestamp("data_aggiornamento").toLocalDateTime());
        return fornitore;
    }
}
