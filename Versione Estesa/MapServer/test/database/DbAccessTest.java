package database;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Classe di test per la classe DbAccess.
 * Richiede che il DBMS MySQL sia in esecuzione e accetti connessioni sulla porta 3306.
 */
class DbAccessTest {

    private DbAccess dbAccess;

    @BeforeEach
    void setUp() throws DatabaseConnectionException {
        dbAccess = new DbAccess();
        dbAccess.initConnection(); // Apre la connessione prima di ogni test
    }

    @AfterEach
    void tearDown() {
        if (dbAccess != null) {
            dbAccess.closeConnection(); // Rilascia la connessione al termine
        }
    }

    @Test
    void testInitAndGetConnection() throws Exception {
        Connection conn = dbAccess.getConnection();
        
        assertNotNull(conn, "L'oggetto connessione non deve essere nullo dopo l'inizializzazione");
        assertFalse(conn.isClosed(), "La connessione deve risultare attiva e aperta");
    }

    @Test
    void testCloseConnection() throws Exception {
        dbAccess.closeConnection();
        Connection conn = dbAccess.getConnection();
        
        assertTrue(conn.isClosed(), "La connessione deve risultare dismessa dopo l'invocazione di closeConnection");
    }
    
    @Test
    void testGetAvailableTablesSuccess() throws SQLException, DatabaseConnectionException {
        List<String> tables = dbAccess.getAvailableTables();

        assertNotNull(tables, "La lista delle tabelle non deve essere nulla.");
        assertFalse(tables.isEmpty(), "Il database deve contenere almeno una tabella.");
        assertTrue(tables.contains("provac"), "La lista deve contenere la tabella di test 'provac'.");
    }
    
    @Test
    void testGetAvailableTablesConnectionClosed() {
        dbAccess.closeConnection();

        assertThrows(SQLException.class, () -> {
            dbAccess.getAvailableTables();
        }, "L'invocazione su una connessione chiusa deve sollevare una SQLException.");
    }
}