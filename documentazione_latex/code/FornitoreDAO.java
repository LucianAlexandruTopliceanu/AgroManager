public class FornitoreDAO extends BaseDAO<Fornitore> {
    @Override
    protected String getTableName() {
        return "fornitore";
    }

    @Override
    protected Fornitore mapResultSetToEntity(ResultSet rs) throws SQLException {...}

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Fornitore fornitore) throws SQLException {...}

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Fornitore fornitore) throws SQLException {...}

    @Override
    protected String getInsertSQL() {
        return "INSERT INTO fornitore (nome, indirizzo, numero_telefono, email, partita_iva, data_creazione, data_aggiornamento) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSQL() {
        return "UPDATE fornitore SET nome = ?, indirizzo = ?, numero_telefono = ?, email = ?, partita_iva = ?, data_aggiornamento = ? WHERE id = ?";
    }

    @Override
    protected void setEntityId(Fornitore fornitore, int id) {...}