package ORM;

import DomainModel.StatoPiantagione;
import BusinessLogic.Service.ErrorService;
import BusinessLogic.Exception.DataAccessException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO per gestione stati piantagione - MODALITÀ SOLA LETTURA
 * Gli stati sono pre-inseriti nel database e non possono essere modificati
 * Implementa il principio Single Responsibility (SRP) - solo lettura
 */
public class StatoPiantagioneDAO extends BaseDAO<StatoPiantagione> {

    @Override
    protected String getTableName() {
        return "stato_piantagione";
    }

    @Override
    protected StatoPiantagione mapResultSetToEntity(ResultSet rs) throws SQLException {
        StatoPiantagione stato = new StatoPiantagione();
        stato.setId(rs.getInt("id"));
        stato.setCodice(rs.getString("codice"));
        stato.setDescrizione(rs.getString("descrizione"));

        Timestamp dataCreazione = rs.getTimestamp("data_creazione");
        if (dataCreazione != null) {
            stato.setDataCreazione(dataCreazione.toLocalDateTime());
        }

        Timestamp dataAggiornamento = rs.getTimestamp("data_aggiornamento");
        if (dataAggiornamento != null) {
            stato.setDataAggiornamento(dataAggiornamento.toLocalDateTime());
        }

        return stato;
    }

    @Override
    protected void setEntityId(StatoPiantagione entity, int id) {
        entity.setId(id);
    }

    // Sovrascrivo tutti i metodi di scrittura per impedire operazioni non consentite
    @Override
    public void create(StatoPiantagione entity) throws SQLException {
        throw new UnsupportedOperationException("Operazione non supportata: gli stati piantagione sono read-only");
    }

    @Override
    public void update(StatoPiantagione entity) throws SQLException {
        throw new UnsupportedOperationException("Operazione non supportata: gli stati piantagione sono read-only");
    }

    @Override
    public void delete(int id) throws SQLException {
        throw new UnsupportedOperationException("Operazione non supportata: gli stati piantagione sono read-only");
    }

    // Metodi non utilizzati in modalità read-only ma richiesti da BaseDAO
    @Override
    protected String getInsertSQL() {
        throw new UnsupportedOperationException("Operazione non supportata: gli stati piantagione sono read-only");
    }

    @Override
    protected String getUpdateSQL() {
        throw new UnsupportedOperationException("Operazione non supportata: gli stati piantagione sono read-only");
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, StatoPiantagione entity) throws SQLException {
        throw new UnsupportedOperationException("Operazione non supportata: gli stati piantagione sono read-only");
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, StatoPiantagione entity) throws SQLException {
        throw new UnsupportedOperationException("Operazione non supportata: gli stati piantagione sono read-only");
    }

    //TODO:Controllare se e meglio fare le query o cercare nelle liste in memoria
    public StatoPiantagione findByCodice(String codice) {
        if (codice == null || codice.trim().isEmpty()) {
            DataAccessException ex = new DataAccessException(
                "Parametro codice null o vuoto in findByCodice",
                "Il codice dello stato non può essere vuoto",
                null
            );
            ErrorService.handleException(ex);
            return null;
        }

        String query = "SELECT * FROM stato_piantagione WHERE codice = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, codice.trim().toUpperCase());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }

            return null;

        } catch (SQLException e) {
            DataAccessException ex = new DataAccessException(
                String.format("Errore SQL durante ricerca stato per codice '%s': %s", codice, e.getMessage()),
                "Errore durante la ricerca dello stato piantagione",
                e
            );
            ErrorService.handleException(ex);
            return null;
        }
    }


    public StatoPiantagione findById(Integer id) {
        if (id == null || id <= 0) {
            DataAccessException ex = new DataAccessException(
                String.format("ID non valido in findById: %s", id),
                "ID stato non valido",
                null
            );
            ErrorService.handleException(ex);
            return null;
        }

        try {
            return read(id);
        } catch (SQLException e) {
            DataAccessException ex = new DataAccessException(
                String.format("Errore SQL durante ricerca stato per ID %d: %s", id, e.getMessage()),
                "Errore durante la ricerca dello stato piantagione",
                e
            );
            ErrorService.handleException(ex);
            return null;
        }
    }


    public List<StatoPiantagione> findAllOrdered() {
        String query = "SELECT * FROM stato_piantagione ORDER BY id";
        List<StatoPiantagione> stati = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                stati.add(mapResultSetToEntity(rs));
            }

            if (stati.isEmpty()) {
                DataAccessException ex = new DataAccessException(
                    "La tabella stato_piantagione è vuota - dovrebbe contenere gli stati standard",
                    "Nessuno stato piantagione trovato nel sistema",
                    null
                );
                ErrorService.handleException(ex);
            }

            return stati;

        } catch (SQLException e) {
            DataAccessException ex = new DataAccessException(
                String.format("Errore SQL durante recupero stati: %s", e.getMessage()),
                "Errore durante il recupero degli stati piantagione",
                e
            );
            ErrorService.handleException(ex);
            return new ArrayList<>();
        }
    }

    public boolean existsByCodice(String codice) {
        return findByCodice(codice) != null;
    }


    public int countStati() {
        String query = "SELECT COUNT(*) FROM stato_piantagione";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            DataAccessException ex = new DataAccessException(
                String.format("Errore SQL durante count stati: %s", e.getMessage()),
                "Errore durante il conteggio degli stati",
                e
            );
            ErrorService.handleException(ex);
            return 0;
        }
    }
}
