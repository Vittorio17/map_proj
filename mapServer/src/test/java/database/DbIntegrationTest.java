package database;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di INTEGRAZIONE per DbAccess, TableSchema e TableData.
 *
 * Prerequisiti:
 *   - MySQL in esecuzione su localhost:3306
 *   - Database "MapDB" raggiungibile con utente MapUser/map
 *   - Tabella "playtennis" presente e con almeno una riga
 *
 * Questi test sono annotati con @Tag("integration") per poterli
 * escludere dall'esecuzione standard:
 *   mvn test -Dgroups="!integration"
 */
@Tag("integration")
@DisplayName("Test di integrazione - database")
class DbIntegrationTest {

    private DbAccess db;

    @BeforeEach
    void setUp() throws DatabaseConnectionException {
        db = new DbAccess();
        db.initConnection();
    }

    @AfterEach
    void tearDown() {
        db.closeConnection();
    }

    // -------------------------------------------------------
    // DbAccess
    // -------------------------------------------------------

    @Test
    @DisplayName("DbAccess: initConnection() produce una connessione non nulla")
    void dbAccess_connectionNotNull() {
        assertNotNull(db.getConnection());
    }

    @Test
    @DisplayName("DbAccess: la connessione è valida (isValid)")
    void dbAccess_connectionIsValid() throws Exception {
        assertTrue(db.getConnection().isValid(2));
    }

    // -------------------------------------------------------
    // TableSchema
    // -------------------------------------------------------

    @Test
    @DisplayName("TableSchema: tabella esistente ha almeno un attributo")
    void tableSchema_hasAttributes() throws Exception {
        TableSchema ts = new TableSchema(db, "playtennis");
        assertTrue(ts.getNumberOfAttributes() > 0);
    }

    @Test
    @DisplayName("TableSchema: getColumn(0) non è null")
    void tableSchema_firstColumnNotNull() throws Exception {
        TableSchema ts = new TableSchema(db, "playtennis");
        assertNotNull(ts.getColumn(0));
    }

    @Test
    @DisplayName("TableSchema: iterator scorre tutte le colonne")
    void tableSchema_iteratorCoversAllColumns() throws Exception {
        TableSchema ts = new TableSchema(db, "playtennis");
        int count = 0;
        for (Column c : ts) count++;
        assertEquals(ts.getNumberOfAttributes(), count);
    }

    @Test
    @DisplayName("TableSchema: tabella inesistente produce schema vuoto (0 attributi)")
    void tableSchema_nonExistentTableEmpty() throws Exception {
        TableSchema ts = new TableSchema(db, "tabella_inesistente_xyz");
        assertEquals(0, ts.getNumberOfAttributes());
    }

    // -------------------------------------------------------
    // TableData
    // -------------------------------------------------------

    @Test
    @DisplayName("TableData: getTransazioni() restituisce lista non vuota")
    void tableData_getTransazioni_notEmpty() throws Exception {
        TableData td = new TableData(db);
        var list = td.getTransazioni("playtennis");
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    @DisplayName("TableData: ogni Example ha tanti elementi quante le colonne dello schema")
    void tableData_exampleSizeMatchesSchema() throws Exception {
        TableSchema ts = new TableSchema(db, "playtennis");
        TableData td = new TableData(db);
        var list = td.getTransazioni("playtennis");
        int ncols = ts.getNumberOfAttributes();
        for (var ex : list) {
            // Verifica accesso senza IndexOutOfBoundsException
            assertDoesNotThrow(() -> ex.get(ncols - 1));
        }
    }

    @Test
    @DisplayName("TableData: getDistinctColumnValues() per colonna discreta non è vuoto")
    void tableData_distinctValues_notEmpty() throws Exception {
        TableSchema ts = new TableSchema(db, "playtennis");
        // Troviamo la prima colonna non numerica
        Column discreteCol = null;
        for (Column c : ts) {
            if (!c.isNumber()) { discreteCol = c; break; }
        }
        assertNotNull(discreteCol, "Nessuna colonna discreta trovata in playtennis");
        TableData td = new TableData(db);
        var values = td.getDistinctColumnValues("playtennis", discreteCol);
        assertFalse(values.isEmpty());
    }

    @Test
    @DisplayName("TableData: getTransazioni() su tabella vuota lancia EmptySetException")
    void tableData_emptyTable_throwsEmptySetException() {
        // Richiede una tabella vuota "empty_test" nel DB di test
        TableData td = new TableData(db);
        assertThrows(EmptySetException.class, () -> td.getTransazioni("empty_test"));
    }
}
