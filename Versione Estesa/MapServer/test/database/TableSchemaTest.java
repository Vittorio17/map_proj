package database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe di test di integrazione per la classe TableSchema.
 * Richiede una connessione attiva al database MySQL e la tabella "provaC".
 */
class TableSchemaTest {

    private DbAccess db;
    private TableSchema schema;

    @BeforeEach
    void setUp() throws Exception {
        db = new DbAccess();
        db.initConnection();
        // Inizializza lo schema interrogando i metadati della tabella "provaC"
        schema = new TableSchema(db, "provaC");
    }

    @AfterEach
    void tearDown() {
        db.closeConnection();
    }

    @Test
    void testGetNumberOfAttributes() {
        assertTrue(schema.getNumberOfAttributes() > 0, "Lo schema della tabella 'provaC' deve contenere almeno una colonna");
    }

    @Test
    void testGetColumn() {
        Column colonna = schema.getColumn(0);
        assertNotNull(colonna, "La colonna estratta non deve essere nulla");
        assertNotNull(colonna.getColumnName(), "Il nome della colonna estratta non deve essere nullo");
    }

    @Test
    void testGetColumnOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> schema.getColumn(100), "Fornire un indice superiore al numero di colonne deve generare IndexOutOfBoundsException");
    }

    @Test
    void testIterator() {
        int conteggio = 0;
        for (Column c : schema) {
            conteggio++;
        }
        assertEquals(schema.getNumberOfAttributes(), conteggio, "L'iteratore deve attraversare esattamente tutte le colonne presenti nello schema");
    }

    @Test
    void testTabellaInesistente() throws Exception {
        TableSchema schemaVuoto = new TableSchema(db, "tabella_inesistente_xyz");
        assertEquals(0, schemaVuoto.getNumberOfAttributes(), "Una tabella inesistente non deve popolare lo schema interno, risultando in 0 colonne estratti");
    }
}