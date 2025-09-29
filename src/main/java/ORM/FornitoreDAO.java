package ORM;

import DomainModel.Fornitore;
import java.sql.*;

public class FornitoreDAO extends BaseDAO<Fornitore> {
    @Override
    protected String getTableName() {
        return "fornitore";
    }

    @Override
    protected Fornitore mapResultSetToEntity(ResultSet rs) throws SQLException {
        Fornitore fornitore = new Fornitore();
        fornitore.setId(rs.getInt("id"));
        fornitore.setNome(rs.getString("nome"));
        fornitore.setIndirizzo(rs.getString("indirizzo"));
        fornitore.setNumeroTelefono(rs.getString("numero_telefono"));
        fornitore.setEmail(rs.getString("email"));
        fornitore.setPartitaIva(rs.getString("partita_iva"));
        // altri campi se necessari
        return fornitore;
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Fornitore fornitore) throws SQLException {
        stmt.setString(1, fornitore.getNome());
        stmt.setString(2, fornitore.getIndirizzo());
        stmt.setString(3, fornitore.getNumeroTelefono());
        stmt.setString(4, fornitore.getEmail());
        stmt.setString(5, fornitore.getPartitaIva());
        stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
        stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Fornitore fornitore) throws SQLException {
        stmt.setString(1, fornitore.getNome());
        stmt.setString(2, fornitore.getIndirizzo());
        stmt.setString(3, fornitore.getNumeroTelefono());
        stmt.setString(4, fornitore.getEmail());
        stmt.setString(5, fornitore.getPartitaIva());
        stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
        stmt.setInt(7, fornitore.getId());
    }

    @Override
    protected String getInsertSQL() {
        return "INSERT INTO fornitore (nome, indirizzo, numero_telefono, email, partita_iva, data_creazione, data_aggiornamento) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSQL() {
        return "UPDATE fornitore SET nome = ?, indirizzo = ?, numero_telefono = ?, email = ?, partita_iva = ?, data_aggiornamento = ? WHERE id = ?";
    }

    @Override
    protected void setEntityId(Fornitore fornitore, int id) {
        fornitore.setId(id);
    }
}
