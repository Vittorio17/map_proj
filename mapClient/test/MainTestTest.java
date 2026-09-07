import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import static org.junit.Assert.*;

/**
 * Test per la classe MainTest.
 * Verifica il ciclo di interazione utente simulando l'input da tastiera.
 */
public class MainTestTest {

    private final InputStream originalSystemIn = System.in;
    private final PrintStream originalSystemOut = System.out;
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @After
    public void tearDown() {
        System.setIn(originalSystemIn);
        System.setOut(originalSystemOut);
    }

    /**
     * Test 1: Verifica selezione opzione 1 (Learn Regression Tree)
     * Simula l'utente che seleziona l'opzione di apprendimento da database.
     * Nota: Questo test verifica solo l'input iniziale, non la connessione socket.
     */
    @Test
    public void testSelezioneOpzioneLearn() {
        // Arrange - Simula: selezione 1, nome tabella, risposta 'n' per non ripetere
        String input = "1\ntestTable\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act & Assert
        // Nota: Il test completo richiederebbe un server socket attivo
        // Qui verifichiamo solo che l'input venga letto correttamente
        int decision = Keyboard.readInt();
        String tableName = Keyboard.readString();
        char risp = Keyboard.readChar();

        assertEquals("L'opzione selezionata deve essere 1", 1, decision);
        assertEquals("Il nome della tabella deve essere 'testTable'", "testTable", tableName);
        assertEquals("La risposta deve essere 'n'", 'n', risp);
    }

    /**
     * Test 2: Verifica selezione opzione 2 (Load Regression Tree)
     * Simula l'utente che seleziona l'opzione di caricamento da archivio.
     */
    @Test
    public void testSelezioneOpzioneLoad() {
        // Arrange - Simula: selezione 2, nome file
        String input = "2\narchiveFile\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        int decision = Keyboard.readInt();
        String fileName = Keyboard.readString();

        // Assert
        assertEquals("L'opzione selezionata deve essere 2", 2, decision);
        assertEquals("Il nome del file deve essere 'archiveFile'", "archiveFile", fileName);
    }

    /**
     * Test 3: Verifica ciclo di iterazione con risposta affermativa
     * Simula l'utente che vuole ripetere la predizione.
     */
    @Test
    public void testCicloIterazioneRipeti() {
        // Arrange - Simula risposte per più cicli
        String input = "y\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        char risp1 = Keyboard.readChar();
        char risp2 = Keyboard.readChar();

        // Assert
        assertEquals("Prima risposta deve essere 'y'", 'y', risp1);
        assertEquals("Seconda risposta deve essere 'n'", 'n', risp2);
    }

    /**
     * Test 4: Verifica input non valido nella selezione iniziale
     * Simula l'utente che inserisce un valore non valido prima di quello corretto.
     */
    @Test
    public void testSelezioneNonValidaPoiCorretta() {
        // Arrange - Simula: input non valido, poi input valido
        String input = "3\nabc\n1\ntestTable\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        Keyboard.resetErrorCount(0);
        int firstInput = Keyboard.readInt(); // Legge 3 (non valido nel ciclo)
        int secondInput = Keyboard.readInt(); // Legge 1 (valido)
        String tableName = Keyboard.readString();

        // Assert - Il contatore errori potrebbe essere incrementato
        // ma il focus è sulla lettura corretta dei valori validi
        assertEquals("Il primo input deve essere 3", 3, firstInput);
        assertEquals("Il secondo input deve essere 1", 1, secondInput);
        assertEquals("Il nome tabella deve essere 'testTable'", "testTable", tableName);
    }
}
