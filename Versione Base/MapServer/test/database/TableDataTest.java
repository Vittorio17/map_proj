package database;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe di test di integrazione per la classe TableData.
 * Richiede un database MySQL attivo con la tabella "provaC" correttamente popolata.
 */
class TableDataTest {

    private DbAccess db;
    private TableData tableData;

    @BeforeEach
    void setUp() throws DatabaseConnectionException {
        db = new DbAccess();
        db.initConnection();
        tableData = new TableData(db);
    }

    @AfterEach
    void tearDown() {
        db.closeConnection();
    }

    @Test
    void testGetTransazioniValide() throws Exception {
        List<Example> transazioni = tableData.getTransazioni("provaC");
        assertNotNull(transazioni, "La lista delle transazioni non deve essere nulla");
        assertFalse(transazioni.isEmpty(), "La lista delle transazioni deve contenere elementi caricati dal DB");
    }

    @Test
    void testGetTransazioniTabellaInesistente() {
        assertThrows(SQLException.class, () -> tableData.getTransazioni("tabella_inesistente_123"), "Richiedere una tabella non esistente deve sollevare SQLException");
    }

    @Test
    void testGetDistinctColumnValuesString() throws Exception {
        Column colonna = new Column("X", "string"); 
        Set<Object> valoriDistinti = tableData.getDistinctColumnValues("provaC", colonna);
        
        assertNotNull(valoriDistinti, "Il set dei valori distinti non deve essere nullo");
        assertFalse(valoriDistinti.isEmpty(), "Il set deve contenere i valori alfanumerici unici della colonna X");
    }
    
    @Test
    void testGetDistinctColumnValuesNumber() throws Exception {
        Column colonna = new Column("Y", "number"); 
        Set<Object> valoriDistinti = tableData.getDistinctColumnValues("provaC", colonna);
        
        assertNotNull(valoriDistinti, "Il set dei valori distinti non deve essere nullo");
        assertFalse(valoriDistinti.isEmpty(), "Il set deve contenere i valori numerici unici della colonna Y");
    }
}