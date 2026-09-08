package database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe di test per la classe Column.
 */
class ColumnTest {

    private Column colonnaNumerica;
    private Column colonnaTestuale;

    @BeforeEach
    void setUp() {
        colonnaNumerica = new Column("eta", "number");
        colonnaTestuale = new Column("nome", "string");
    }

    @Test
    void testGetColumnName() {
        assertEquals("eta", colonnaNumerica.getColumnName(), "Il metodo getColumnName deve restituire il nome impostato");
    }

    @Test
    void testIsNumberTrue() {
        assertTrue(colonnaNumerica.isNumber(),"Una colonna con tipo 'number' deve restituire true");
    }

    @Test
    void testIsNumberFalse() {
        assertFalse(colonnaTestuale.isNumber(),"Una colonna con tipo 'string' deve restituire false");
    }

    @Test
    void testUnknownTypeIsNotNumber() {
        Column colonnaGenerica = new Column("data_nascita", "date");
        assertFalse(colonnaGenerica.isNumber(), "Qualsiasi tipo diverso da 'number' deve restituire false");
    }

    @Test
    void testToString() {
        assertEquals("eta:number", colonnaNumerica.toString(), "Il metodo toString deve restituire il formato 'nome:tipo'");
    }
}