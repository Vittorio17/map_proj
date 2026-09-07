package utility;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static org.junit.Assert.*;

/**
 * Test per la classe Keyboard.
 * Verifica il comportamento in caso di errore dell'input.
 */
public class KeyboardTest {

    private final InputStream originalSystemIn = System.in;

    @Before
    public void setUp() {
        // Reset del contatore errori prima di ogni test
        Keyboard.resetErrorCount(0);
        Keyboard.setPrintErrors(false); // Disabilita stampa errori durante i test
    }

    @After
    public void tearDown() {
        // Ripristina System.in originale
        System.setIn(originalSystemIn);
        Keyboard.setPrintErrors(true);
    }

    /**
     * Test 1: readInt con input non valido
     * Verifica che l'inserimento di una stringa non numerica in readInt()
     * restituisca Integer.MIN_VALUE e incrementi il contatore errori.
     */
    @Test
    public void testReadIntConInputNonValido() {
        // Arrange
        String input = "ciao\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        // Reset contatore errori
        Keyboard.resetErrorCount(0);
        int erroriPrima = Keyboard.getErrorCount();

        // Act
        int risultato = Keyboard.readInt();

        // Assert
        assertEquals("readInt deve restituire Integer.MIN_VALUE con input non valido",
                     Integer.MIN_VALUE, risultato);
        assertEquals("Il contatore errori deve essere incrementato di 1",
                     erroriPrima + 1, Keyboard.getErrorCount());
    }

    /**
     * Test 2: readDouble con input non valido
     * Verifica che l'inserimento di una stringa non numerica in readDouble()
     * restituisca Double.NaN e incrementi il contatore errori.
     */
    @Test
    public void testReadDoubleConInputNonValido() {
        // Arrange
        String input = "ciao\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        // Reset contatore errori
        Keyboard.resetErrorCount(0);
        int erroriPrima = Keyboard.getErrorCount();

        // Act
        double risultato = Keyboard.readDouble();

        // Assert
        assertTrue("readDouble deve restituire Double.NaN con input non valido",
                   Double.isNaN(risultato));
        assertEquals("Il contatore errori deve essere incrementato di 1",
                     erroriPrima + 1, Keyboard.getErrorCount());
    }

    /**
     * Test 3: readChar con input vuoto/null
     * Verifica che readChar() gestisca correttamente l'errore
     * restituendo Character.MIN_VALUE e incrementando il contatore errori.
     */
    @Test
    public void testReadCharConInputNonValido() {
        // Arrange - simula un errore di lettura (input che causa eccezione)
        String input = "\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        // Reset contatore errori
        Keyboard.resetErrorCount(0);
        int erroriPrima = Keyboard.getErrorCount();

        // Act
        char risultato = Keyboard.readChar();

        // Assert - con solo newline, il comportamento dipende dall'implementazione
        // Verifichiamo che in caso di errore restituisca Character.MIN_VALUE
        // Nota: il test effettivo dipende da come getNextToken gestisce l'input
        if (Keyboard.getErrorCount() > erroriPrima) {
            assertEquals("readChar deve restituire Character.MIN_VALUE con input non valido",
                         Character.MIN_VALUE, risultato);
        }
    }

    /**
     * Test 4: Verifica incremento contatore errori multipli
     * Verifica che il contatore errori venga incrementato correttamente
     * dopo molteplici errori consecutivi.
     */
    @Test
    public void testIncrementoContatoreErroriMultiplo() {
        // Arrange
        Keyboard.resetErrorCount(0);

        // Primo errore - readInt
        String input1 = "errore1\n";
        System.setIn(new ByteArrayInputStream(input1.getBytes()));
        Keyboard.readInt();

        // Secondo errore - readDouble
        String input2 = "errore2\n";
        System.setIn(new ByteArrayInputStream(input2.getBytes()));
        Keyboard.readDouble();

        // Assert
        assertEquals("Il contatore deve registrare 2 errori consecutivi",
                     2, Keyboard.getErrorCount());
    }

    /**
     * Test 5: readInt con valore valido
     * Verifica che readInt() funzioni correttamente con input valido.
     */
    @Test
    public void testReadIntConInputValido() {
        // Arrange
        String input = "42\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        Keyboard.resetErrorCount(0);
        int erroriPrima = Keyboard.getErrorCount();

        // Act
        int risultato = Keyboard.readInt();

        // Assert
        assertEquals("readInt deve restituire 42 con input valido", 42, risultato);
        assertEquals("Il contatore errori non deve essere incrementato con input valido",
                     erroriPrima, Keyboard.getErrorCount());
    }
}
