package database;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test unitari per la classe EmptySetException
 * appartenente alla classe database.
 */
class EmptySetExcpetionTest{

    /** Verifica che il messaggio memorizzato nell'eccezione EmptySetException corrisponda a quello fornito nel costruttore. */
    @Test
    @DisplayName("Verifica che il messaggio memorizzato nell'eccezione EmptySetException corrisponda a quello fornito nel costruttore.")
    void emptySetException_message() {
        EmptySetException ex = new EmptySetException();
        assertTrue(ex.getMessage().contains("empty"));
    }

    /** Verifica che l'eccezione personalizzata EmptySetException estenda correttamente la classe base standard Exception. */
    @Test
    @DisplayName("Verifica che l'eccezione personalizzata EmptySetException estenda correttamente la classe base standard Exception.")
    void emptySetException_isException() {
        assertInstanceOf(Exception.class, new EmptySetException());
    }
}