package database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe di test per l'eccezione personalizzata EmptySetException.
 */
class EmptySetExceptionTest {

    @Test
    void testMessage() {
        EmptySetException ex = new EmptySetException();
        assertEquals("[!] The result set is empty", ex.getMessage(), "Il messaggio predefinito deve corrispondere a quello cablato nel costruttore");
    }

    @Test
    void testIsException() {
        assertInstanceOf(Exception.class, new EmptySetException(), "L'eccezione EmptySetException deve estendere la classe base Exception");
    }
}