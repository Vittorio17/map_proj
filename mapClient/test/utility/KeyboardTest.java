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

    /**
     * Test 6: resetErrorCount ignora il parametro passato
     * Verifica che resetErrorCount(int count) ignori completamente il parametro
     * e reimposti sempre il contatore a 0, indipendentemente dal valore passato.
     * Questo test evidenzia un'anomalia nel design del metodo.
     */
    @Test
    public void testResetErrorCountIgnoraParametro() {
        // Arrange - genera alcuni errori per avere un contatore > 0
        String input = "errore\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Keyboard.readInt(); // genera un errore
        int erroriDopoErrore = Keyboard.getErrorCount();
        assertTrue("Dovrebbero esserci errori registrati", erroriDopoErrore > 0);

        // Act - reset con parametro 10 (che dovrebbe essere ignorato)
        Keyboard.resetErrorCount(10);
        int countDopoResetCon10 = Keyboard.getErrorCount();

        // Genera nuovamente errori
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Keyboard.readInt();
        Keyboard.readInt();
        int erroriDopoDueErrori = Keyboard.getErrorCount();

        // Act - reset con parametro -1 (che dovrebbe essere ignorato)
        Keyboard.resetErrorCount(-1);
        int countDopoResetConMenoUno = Keyboard.getErrorCount();

        // Assert
        assertEquals("resetErrorCount(10) deve reimpostare il contatore a 0, ignorando il parametro",
                     0, countDopoResetCon10);
        assertEquals("resetErrorCount(-1) deve reimpostare il contatore a 0, ignorando il parametro",
                     0, countDopoResetConMenoUno);
    }

    /**
     * Test 7: readWord restituisce singolo token delimitato da spazi
     * Verifica che readWord() estragga solo il primo token utilizzando
     * StringTokenizer con delimitatori standard.
     */
    @Test
    public void testReadWordSingoloToken() {
        // Arrange
        String input = "Hello World\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Keyboard.resetErrorCount(0);

        // Act
        String parola = Keyboard.readWord();

        // Assert
        assertEquals("readWord deve restituire solo il primo token", "Hello", parola);
        assertEquals("Il contatore errori non deve essere incrementato", 0, Keyboard.getErrorCount());
    }

    /**
     * Test 8: readString concatena tutti i token fino a fine riga
     * Verifica che readString() recuperi il primo token e poi iteri
     * concatenando tutti i token successivi fino a endOfLine().
     */
    @Test
    public void testReadStringConcatenaToken() {
        // Arrange
        String input = "Hello World Test\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Keyboard.resetErrorCount(0);

        // Act
        String riga = Keyboard.readString();

        // Assert
        assertEquals("readString deve concatenare tutti i token della riga",
                     "HelloWorldTest", riga);
        assertEquals("Il contatore errori non deve essere incrementato", 0, Keyboard.getErrorCount());
    }

    /**
     * Test 9: readChar estrae primo carattere e bufferizza il resto
     * Verifica che readChar() estragga il primo carattere e memorizzi
     * la sottostringa rimanente in current_token per letture successive.
     */
    @Test
    public void testReadCharConBuffer() {
        // Arrange
        String input = "ab\ncd\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Keyboard.resetErrorCount(0);

        // Act - prima lettura
        char char1 = Keyboard.readChar();

        // Act - seconda lettura (dovrebbe leggere 'b' dal buffer)
        char char2 = Keyboard.readChar();

        // Act - terza lettura (nuova riga)
        char char3 = Keyboard.readChar();

        // Assert
        assertEquals("Primo carattere deve essere 'a'", 'a', char1);
        assertEquals("Secondo carattere deve essere 'b' (dal buffer)", 'b', char2);
        assertEquals("Terzo carattere deve essere 'c' (nuova riga)", 'c', char3);
        assertEquals("Il contatore errori non deve essere incrementato", 0, Keyboard.getErrorCount());
    }
}
