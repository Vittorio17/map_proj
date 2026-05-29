package database;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test unitari per la classe DatabaseConnectionException
 * appartenente al package Database.
 */
class DatabaseConnectionExceptionTest{

    /** Verifica che il messaggio memorizzato nell'eccezione DatabaseConnectionException corrisponda a quello fornito nel costruttore. */
    @Test
    @DisplayName("Verifica che il messaggio memorizzato nell'eccezione DatabaseConnectionException corrisponda a quello fornito nel costruttore.")
    void databaseConnectionException_message() {
        DatabaseConnectionException ex = new DatabaseConnectionException("conn failed");
        assertEquals("conn failed", ex.getMessage());
    }

    /** Verifica che l'eccezione personalizzata estenda correttamente la classe base standard Exception. */
    @Test
    @DisplayName("Verifica che il messaggio memorizzato nell'eccezione DatabaseConnectionException corrisponda a quello fornito nel costruttore.")
    void databaseConnectionException_isException() {
        assertInstanceOf(Exception.class, new DatabaseConnectionException("msg"));
    }
}