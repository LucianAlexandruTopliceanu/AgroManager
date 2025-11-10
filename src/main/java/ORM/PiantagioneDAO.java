package ORM;
import DomainModel.Piantagione;
import DomainModel.StatoPiantagione;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PiantagioneDAO extends BaseDAO<Piantagione> {
    @Override
    protected String getTableName() {
        return "piantagione";
    }

    @Override
    protected void setEntityId(Piantagione entity, int id) {
        entity.setId(id);
    }

    @Override
    protected Piantagione mapResultSetToEntity(ResultSet rs) throws SQLException {
        Piantagione piantagione = new Piantagione();
        piantagione.setId(rs.getInt("id"));
        piantagione.setQuantitaPianta(rs.getInt("quantita_pianta"));
        piantagione.setMessaADimora(rs.getDate("messa_a_dimora").toLocalDate());
        piantagione.setPiantaId(rs.getInt("id_pianta"));
        piantagione.setZonaId(rs.getInt("id_zona"));
        piantagione.setIdStatoPiantagione(rs.getInt("id_stato_piantagione")); // NUOVO

        Timestamp dataCreazione = rs.getTimestamp("data_creazione");
        if (dataCreazione != null) {
            piantagione.setDataCreazione(dataCreazione.toLocalDateTime());
        }

        Timestamp dataAggiornamento = rs.getTimestamp("data_aggiornamento");
        if (dataAggiornamento != null) {
            piantagione.setDataAggiornamento(dataAggiornamento.toLocalDateTime());
        }

        return piantagione;
    }

    protected Piantagione mapResultSetWithStato(ResultSet rs) throws SQLException {
        Piantagione piantagione = mapResultSetToEntity(rs);

        try {
            StatoPiantagione stato = new StatoPiantagione();
            stato.setId(rs.getInt("stato_id"));
            stato.setCodice(rs.getString("stato_codice"));
            stato.setDescrizione(rs.getString("stato_descrizione"));
            piantagione.setStatoPiantagione(stato);
        } catch (SQLException e) {
        }

        return piantagione;
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Piantagione piantagione) throws SQLException {
        stmt.setInt(1, piantagione.getQuantitaPianta());
        stmt.setDate(2, Date.valueOf(piantagione.getMessaADimora()));
        stmt.setInt(3, piantagione.getPiantaId());
        stmt.setInt(4, piantagione.getZonaId());
        stmt.setInt(5, piantagione.getIdStatoPiantagione() != null ? piantagione.getIdStatoPiantagione() : 1); // NUOVO
        stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
        stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Piantagione piantagione) throws SQLException {
        stmt.setInt(1, piantagione.getQuantitaPianta());
        stmt.setDate(2, Date.valueOf(piantagione.getMessaADimora()));
        stmt.setInt(3, piantagione.getPiantaId());
        stmt.setInt(4, piantagione.getZonaId());
        stmt.setInt(5, piantagione.getIdStatoPiantagione() != null ? piantagione.getIdStatoPiantagione() : 1); // NUOVO
        stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
        stmt.setInt(7, piantagione.getId());
    }

    @Override
    protected String getInsertSQL() {
        return "INSERT INTO piantagione (quantita_pianta, messa_a_dimora, id_pianta, id_zona, id_stato_piantagione, data_creazione, data_aggiornamento) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSQL() {
        return "UPDATE piantagione SET quantita_pianta = ?, messa_a_dimora = ?, id_pianta = ?, id_zona = ?, id_stato_piantagione = ?, data_aggiornamento = ? WHERE id = ?";
    }


    public List<Piantagione> findAllWithStato() {
        String query = """
            SELECT p.*, s.id as stato_id, s.codice as stato_codice, s.descrizione as stato_descrizione
            FROM piantagione p
            JOIN stato_piantagione s ON p.id_stato_piantagione = s.id
            ORDER BY p.id
            """;

        List<Piantagione> piantagioni = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                piantagioni.add(mapResultSetWithStato(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero delle piantagioni con stato", e);
        }

        return piantagioni;
    }


    public List<Piantagione> findByStato(String codiceStato) {
        String query = """
            SELECT p.*, s.id as stato_id, s.codice as stato_codice, s.descrizione as stato_descrizione
            FROM piantagione p
            JOIN stato_piantagione s ON p.id_stato_piantagione = s.id
            WHERE s.codice = ?
            ORDER BY p.messa_a_dimora DESC
            """;

        List<Piantagione> piantagioni = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, codiceStato);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    piantagioni.add(mapResultSetWithStato(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca piantagioni per stato: " + codiceStato, e);
        }

        return piantagioni;
    }


    public List<Piantagione> findAttive() {
        return findByStato(StatoPiantagione.ATTIVA);
    }

    public void cambiaStato(Integer piantagioneId, Integer nuovoStatoId) {
        String query = "UPDATE piantagione SET id_stato_piantagione = ?, data_aggiornamento = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, nuovoStatoId);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(3, piantagioneId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Nessuna piantagione trovata con ID: " + piantagioneId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il cambio stato piantagione", e);
        }
    }

    public java.util.List<Piantagione> findByZona(Integer zonaId) throws SQLException {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id_zona = ?";
        java.util.List<Piantagione> piantagioni = new java.util.ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, zonaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    piantagioni.add(mapResultSetToEntity(rs));
                }
            }
        }
        return piantagioni;
    }

    public java.util.List<Piantagione> findByPianta(Integer piantaId) throws SQLException {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id_pianta = ?";
        java.util.List<Piantagione> piantagioni = new java.util.ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, piantaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    piantagioni.add(mapResultSetToEntity(rs));
                }
            }
        }
        return piantagioni;
    }
}
