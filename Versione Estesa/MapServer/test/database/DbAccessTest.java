package database;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.Connection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe di test per la classe DbAccess.
 * Richiede che il DBMS MySQL sia in esecuzione e accetti connessioni sulla porta 3306.
 */
class DbAccessTest {

    private DbAccess dbAccess;

    @BeforeEach
    void setUp() {
        dbAccess = new DbAccess();
    }

    @Test
    void testInitAndGetConnection() throws Exception {
        dbAccess.initConnection();
        Connection conn = dbAccess.getConnection();
        
        assertNotNull(conn, "L'oggetto connessione non deve essere nullo dopo l'inizializzazione");
        assertFalse(conn.isClosed(), "La connessione deve risultare attiva e aperta");
        
        // Pulizia post-test
        dbAccess.closeConnection();
    }

    @Test
    void testCloseConnection() throws Exception {
        dbAccess.initConnection();
        dbAccess.closeConnection();
        Connection conn = dbAccess.getConnection();
        
        assertTrue(conn.isClosed(), "La connessione deve risultare dismessa dopo l'invocazione di closeConnection");
    }
}