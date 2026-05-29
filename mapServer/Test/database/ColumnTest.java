package database;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per la classe Column appartenente al package Database.
 */
class ColumnTest{

    /** Verifica che il metodo getColumnName() restituisca correttamente il nome assegnato alla colonna. */
    @Test
    @DisplayName("Verifica che il metodo getColumnName() restituisca correttamente il nome assegnato alla colonna. ")
    void column_getName() {
        Column c = new Column("eta", "number");
        assertEquals("eta", c.getColumnName());
    }

    /** Verifica che isNumber() restituisca true quando il tipo di dato della colonna è impostato su "number". */
    @Test
    @DisplayName("Verifica che isNumber() restituisca true quando il tipo di dato della colonna è impostato su 'number'.")
    void column_isNumber_true() {
        Column c = new Column("eta", "number");
        assertTrue(c.isNumber());
    }

    /** Verifica che isNumber() restituisca false quando il tipo di dato della colonna è impostato su "string". */
    @Test
    @DisplayName("Verifica che isNumber() restituisca false quando il tipo di dato della colonna è impostato su 'string'.")
    void column_isNumber_false() {
        Column c = new Column("nome", "string");
        assertFalse(c.isNumber());
    }

    /** Verifica che il metodo toString() generi la stringa nel formato atteso 'nome:tipo'. */
    @Test
    @DisplayName("Verifica che il metodo toString() generi la stringa nel formato atteso 'nome:tipo'.")
    void column_toString() {
        Column c = new Column("altezza", "number");
        assertEquals("altezza:number", c.toString());
    }

    /** Verifica che isNumber() valuti correttamente come false qualsiasi tipo non riconosciuto (es. "date"). */
    @Test
    @DisplayName("Verifica che isNumber() valuti correttamente come false qualsiasi tipo non riconosciuto (es. 'date').")
    void column_unknownTypeIsNotNumber() {
        Column c = new Column("x", "date");
        assertFalse(c.isNumber());
    }
}