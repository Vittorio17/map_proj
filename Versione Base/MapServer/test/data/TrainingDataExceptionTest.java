package data;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe TrainingDataException
 */
class TrainingDataExceptionTest {

    /** Verifica che getMessage() di TrainingDataException restituisce
     * il messaggio di errore passato in input.
     */
    @Test
    void trainingDataException_message() {
        TrainingDataException ex = new TrainingDataException("errore test");
        assertEquals("errore test", ex.getMessage());
    }

}
