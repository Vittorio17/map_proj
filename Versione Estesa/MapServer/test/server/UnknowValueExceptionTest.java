package server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe di test per l'eccezione personalizzata UnknownValueException.
 */
class UnknownValueExceptionTest {

    @Test
    void testMessage() {
        String messaggioAtteso = "Valore inserito non riconosciuto per il nodo corrente";
        UnknownValueException eccezione = new UnknownValueException(messaggioAtteso);
        
        assertEquals(messaggioAtteso, eccezione.getMessage(),"Il metodo getMessage deve restituire fedelmente la stringa fornita al costruttore");
    }

    @Test
    void testIsException() {
        UnknownValueException eccezione = new UnknownValueException("Errore generico");
        assertInstanceOf(Exception.class, eccezione, "L'eccezione UnknownValueException deve estendere la classe base Exception");
    }
}