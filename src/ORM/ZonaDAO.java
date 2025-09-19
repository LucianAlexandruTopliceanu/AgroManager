package ORM;

import DomainModel.Zona;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ZonaDAO {

    public void create(Zona zona) throws SQLException {
        String sql = "INSERT INTO zona (nome, dimensione, tipo_terreno, data_creazione, data_aggiornamento) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, zona.getNome());
            stmt.setDouble(2, zona.getDimensione());
            stmt.setString(3, zona.getTipoTerreno());
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    zona.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public Zona read(int id) throws SQLException {
        String sql = "SELECT * FROM zona WHERE id = ?";
        Zona zona = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    zona = mapResultSetToZona(rs);
                }
            }
        }
        return zona;
    }

    public void update(Zona zona) throws SQLException {
        String sql = "UPDATE zona SET nome = ?, dimensione = ?, tipo_terreno = ?, data_aggiornamento = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, zona.getNome());
            stmt.setDouble(2, zona.getDimensione());
            stmt.setString(3, zona.getTipoTerreno());
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(5, zona.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM zona WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Zona> findAll() throws SQLException {
        List<Zona> zone = new ArrayList<>();
        String sql = "SELECT * FROM zona";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                zone.add(mapResultSetToZona(rs));
            }
        }
        return zone;
    }

    private Zona mapResultSetToZona(ResultSet rs) throws SQLException {
        Zona zona = new Zona();
        zona.setId(rs.getInt("id"));
        zona.setNome(rs.getString("nome"));
        zona.setDimensione(rs.getDouble("dimensione"));
        zona.setTipoTerreno(rs.getString("tipo_terreno"));
        zona.setDataCreazione(rs.getTimestamp("data_creazione").toLocalDateTime());
        zona.setDataAggiornamento(rs.getTimestamp("data_aggiornamento").toLocalDateTime());
        return zona;
    }
}