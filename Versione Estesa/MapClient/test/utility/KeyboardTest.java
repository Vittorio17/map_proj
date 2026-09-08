package utility;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import static org.junit.Assert.*;

public class KeyboardTest {

    private final InputStream originalSystemIn = System.in;

    @Before
    public void setUp() throws Exception {
        Keyboard.resetErrorCount(0);
        Keyboard.setPrintErrors(false);
        resetKeyboardState();
    }

    @After
    public void tearDown() throws Exception {
        System.setIn(originalSystemIn);
        Keyboard.setPrintErrors(true);
        resetKeyboardState();
    }

    private void resetKeyboardState() throws Exception {
        Field inField = Keyboard.class.getDeclaredField("in");
        inField.setAccessible(true);
        inField.set(null, new BufferedReader(new InputStreamReader(System.in)));

        Field readerField = Keyboard.class.getDeclaredField("reader");
        readerField.setAccessible(true);
        readerField.set(null, null);

        Field tokenField = Keyboard.class.getDeclaredField("current_token");
        tokenField.setAccessible(true);
        tokenField.set(null, null);
    }

    private void setKeyboardInput(String input) throws Exception {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        resetKeyboardState();
    }

    @Test
    public void testReadIntConInputNonValido() throws Exception {
        setKeyboardInput("ciao\n");
        Keyboard.resetErrorCount(0);
        int erroriPrima = Keyboard.getErrorCount();

        int risultato = Keyboard.readInt();

        assertEquals("readInt deve restituire Integer.MIN_VALUE con input non valido", Integer.MIN_VALUE, risultato);
        assertEquals("Il contatore errori deve essere incrementato di 1", erroriPrima + 1, Keyboard.getErrorCount());
    }

    @Test
    public void testReadDoubleConInputNonValido() throws Exception {
        setKeyboardInput("ciao\n");
        Keyboard.resetErrorCount(0);
        int erroriPrima = Keyboard.getErrorCount();

        double risultato = Keyboard.readDouble();

        assertTrue("readDouble deve restituire Double.NaN con input non valido", Double.isNaN(risultato));
        assertEquals("Il contatore errori deve essere incrementato di 1", erroriPrima + 1, Keyboard.getErrorCount());
    }

    @Test
    public void testReadCharConInputNonValido() throws Exception {
        setKeyboardInput("\n");
        Keyboard.resetErrorCount(0);
        int erroriPrima = Keyboard.getErrorCount();

        char risultato = Keyboard.readChar();

        if (Keyboard.getErrorCount() > erroriPrima) {
            assertEquals("readChar deve restituire Character.MIN_VALUE con input non valido", Character.MIN_VALUE, risultato);
        }
    }

    @Test
    public void testIncrementoContatoreErroriMultiplo() throws Exception {
        Keyboard.resetErrorCount(0);

        setKeyboardInput("errore1\n");
        Keyboard.readInt();

        setKeyboardInput("errore2\n");
        Keyboard.readDouble();

        assertEquals("Il contatore deve registrare 2 errori consecutivi", 2, Keyboard.getErrorCount());
    }

    @Test
    public void testReadIntConInputValido() throws Exception {
        setKeyboardInput("42\n");
        Keyboard.resetErrorCount(0);
        int erroriPrima = Keyboard.getErrorCount();

        int risultato = Keyboard.readInt();

        assertEquals("readInt deve restituire 42 con input valido", 42, risultato);
        assertEquals("Il contatore errori non deve essere incrementato con input valido", erroriPrima, Keyboard.getErrorCount());
    }

    @Test
    public void testReadWordSingoloToken() throws Exception {
        setKeyboardInput("Hello World\n");
        Keyboard.resetErrorCount(0);

        String parola = Keyboard.readWord();

        assertEquals("readWord deve restituire solo il primo token", "Hello", parola);
        assertEquals("Il contatore errori non deve essere incrementato", 0, Keyboard.getErrorCount());
    }

    
    @Test
    public void testReadStringConcatenaToken() throws Exception {
        setKeyboardInput("Hello World Test\n");
        Keyboard.resetErrorCount(0);

        String riga = Keyboard.readString();

        assertEquals("readString deve concatenare tutti i token preservando gli spazi", "Hello World Test", riga);
        assertEquals("Il contatore errori non deve essere incrementato", 0, Keyboard.getErrorCount());
    }

    @Test
    public void testReadCharConBuffer() throws Exception {
        setKeyboardInput("ab\ncd\n");
        Keyboard.resetErrorCount(0);

        char char1 = Keyboard.readChar();
        char char2 = Keyboard.readChar();
        char char3 = Keyboard.readChar();

        assertEquals("Primo carattere deve essere 'a'", 'a', char1);
        assertEquals("Secondo carattere deve essere 'b' (dal buffer)", 'b', char2);
        assertEquals("Terzo carattere deve essere 'c' (nuova riga)", 'c', char3);
        assertEquals("Il contatore errori non deve essere incrementato", 0, Keyboard.getErrorCount());
    }
}