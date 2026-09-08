package database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe di test per l'eccezione personalizzata DatabaseConnectionException.
 */
class DatabaseConnectionExceptionTest {

    @Test
    void testMessage() {
        String msg = "Connessione fallita: timeout del server";
        DatabaseConnectionException ex = new DatabaseConnectionException(msg);
        assertEquals(msg, ex.getMessage(),"Il metodo getMessage deve restituire il messaggio passato al costruttore");
    }

    @Test
    void testIsException() {
        DatabaseConnectionException ex = new DatabaseConnectionException("errore");
        assertInstanceOf(Exception.class, ex,"La classe deve estendere direttamente la classe base Exception");
    }
}