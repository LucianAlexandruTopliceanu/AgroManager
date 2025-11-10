public abstract class BaseDAO<T> {
    protected abstract String getTableName();
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
    protected abstract void setInsertParameters(PreparedStatement stmt, T entity) throws SQLException;
    protected abstract void setUpdateParameters(PreparedStatement stmt, T entity) throws SQLException;
    protected abstract String getInsertSQL();
    protected abstract String getUpdateSQL();

    public void create(T entity) throws SQLException {...}

    public T read(int id) throws SQLException {...}

    public void update(T entity) throws SQLException {...}

    public void delete(int id) throws SQLException {...}

    public List<T> findAll() throws SQLException {...}

    protected abstract void setEntityId(T entity, int id);
}