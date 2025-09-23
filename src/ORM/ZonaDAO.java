package ORM;

import DomainModel.Zona;
import java.sql.*;

public class ZonaDAO extends BaseDAO<Zona> {
    @Override
    protected String getTableName() {
        return "zona";
    }

    @Override
    protected Zona mapResultSetToEntity(ResultSet rs) throws SQLException {
        Zona zona = new Zona();
        zona.setId(rs.getInt("id"));
        zona.setNome(rs.getString("nome"));
        zona.setDimensione(rs.getDouble("dimensione"));
        zona.setTipoTerreno(rs.getString("tipo_terreno"));
        // altri campi se necessari
        return zona;
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Zona zona) throws SQLException {
        stmt.setString(1, zona.getNome());
        stmt.setDouble(2, zona.getDimensione());
        stmt.setString(3, zona.getTipoTerreno());
        stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
        stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Zona zona) throws SQLException {
        stmt.setString(1, zona.getNome());
        stmt.setDouble(2, zona.getDimensione());
        stmt.setString(3, zona.getTipoTerreno());
        stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
        stmt.setInt(5, zona.getId());
    }

    @Override
    protected String getInsertSQL() {
        return "INSERT INTO zona (nome, dimensione, tipo_terreno, data_creazione, data_aggiornamento) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSQL() {
        return "UPDATE zona SET nome = ?, dimensione = ?, tipo_terreno = ?, data_aggiornamento = ? WHERE id = ?";
    }

    @Override
    protected void setEntityId(Zona zona, int id) {
        zona.setId(id);
    }
}