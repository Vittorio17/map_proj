package data;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;



/**
 * Classe di test per la classe Data.
 * Verifica il corretto caricamento dei dati dal database reale MySQL (tabella provaC)
 * e il corretto funzionamento degli algoritmi di ordinamento interni.
 * Prerequisiti:
 * - MySQL attivo su indirizzo 127.0.0.1, porta 3306
 * - Database MapDB presente con l'utente MapUser/map
 * - Tabella provaC creata e popolata
 */
class DataTest {

    // Istanza della classe Data che verrà ricreata prima di ogni test
    private Data data;

    /**
     * Questo metodo viene eseguito prima di ogni test.
     * Ricarica i dati direttamente dalla tabella "provaC" del database MySQL.
     */
    @BeforeEach
    void setUp() throws TrainingDataException {
        data = new Data("provaC");
    }

    /**
     * Verifica che il costruttore lanci correttamente TrainingDataException
     * se si tenta di specificare una tabella che non esiste nel database.
     */
    @Test
    @DisplayName("Verifica che lancia un eccezione se la tabella richiesta non esiste")
    void tabellaInesistente_throwsTrainingDataException() {
        assertThrows(TrainingDataException.class, () -> {
            new Data("tabella_inesistente");
        });
    }

    @Test
    @DisplayName("Verifica che lancia un eccezione se la tabella ha una sola colonna (manca il target)")
    void tabellaConSingolaColonna_throwsTrainingDataException() {
        assertThrows(TrainingDataException.class, () -> {
            new Data("provaC_singola");
        });
    }

    @Test
    @DisplayName("Verifica che lancia un eccezione se la colonna target non è di tipo numerico")
    void targetNonNumerico_throwsTrainingDataException() {
        assertThrows(TrainingDataException.class, () -> {
            new Data("provaC_errata"); 
        });
    }

    /**
     * Verifica che il costruttore riconosca i tipi di colonna corretti.
     * La colonna 'X' deve essere un DiscreteAttribute, la colonna 'Y' un ContinuousAttribute.
     */
    @Test
    @DisplayName("Verifica se riconosce correttamente gli attributi Discreti e Continui")
    void identificaTipoColonnaCorrettamente() {
        // Il primo attributo (indice 0) corrisponde alla colonna X (varchar)
        Attribute attrX = data.getExplanatoryAttribute(0);
        assertTrue(attrX instanceof DiscreteAttribute, "La colonna X dovrebbe essere un attributo discreto");
        assertEquals("X", attrX.getName());

        // Il secondo attributo (indice 1) corrisponde alla colonna Y (double)
        Attribute attrY = data.getExplanatoryAttribute(1);
        assertTrue(attrY instanceof ContinuousAttribute, "La colonna Y dovrebbe essere un attributo continuo");
        assertEquals("Y", attrY.getName());
    }


    /**
     * Verifica che il metodo getExplanatoryValue legga correttamente i dati 
     * e che non restituisca valori nulli per indici validi.
     */
    @Test
    @DisplayName("Verifica che estrae correttamente un valore esplicativo valido, non nullo")
    void getExplanatoryValue_ritornaValoreNonNullo() {
        Object valoreIniziale = data.getExplanatoryValue(0, 0);
        assertNotNull(valoreIniziale, "Il valore letto nel dataset non deve essere nullo");
    }

    /**
     * Verifica che il metodo getExplanatoryValue lanci un'eccezione di tipo
     * IndexOutOfBoundsException se si richiede una riga fuori dai limiti.
     */
    @Test
    @DisplayName("Verifica che lancia IndexOutOfBoundsException se si richiede una riga inesistente")
    void getExplanatoryValue_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            data.getExplanatoryValue(20, 0);
        });
    }

    /**
     * Verifica che il metodo getClassAttribute restituisca l'attributo della classe
     * target, verificando nome e indice.
     */
    @Test
    @DisplayName("Verifica se identifica correttamente l'attributo di classe")
    void getClassAttribute_attributoTargetCorretto() {
        ContinuousAttribute target = data.getClassAttribute();
        assertNotNull(target, "L'attributo di classe non deve essere nullo");
        assertEquals("C", target.getName());
        assertEquals(2, target.getIndex(), "L'indice del target deve essere 2 (terza colonna)");
    }

    /**
     * Verifica che il metodo getClassValue restituisca un valore valido senza lanciare
     * eccezioni di casting o errori di lettura.
     */
    @Test
    @DisplayName("Verifica se legge il valore numerico della classe da predirre senza errori")
    void getClassValue_ritornaIndiceValido() {
        assertDoesNotThrow(() -> {
            double valoreTarget = data.getClassValue(0);
        });
    }

    /**
     * Testa il funzionamento dell'ordinamento Quicksort sull'attributo discreto 'X'.
     * Al termine dell'esecuzione, i valori della colonna 'X' devono risultare ordinati alfabeticamente.
     */
    @Test
    @DisplayName("Verifica che il Quicksort ordina alfabeticamente gli attributi discreti")
    void ordinamentoCorretto_discreteAttribute() {
        Attribute attrX = data.getExplanatoryAttribute(0); // Colonna X
        
        // Esegue l'ordinamento su tutte le righe 
        data.sort(attrX, 0, data.getNumberOfExamples() - 1);
        
        // Controllo che ogni elemento sia minore o uguale al successivo 
        for (int i = 0; i < data.getNumberOfExamples() - 1; i++) {
            String corrente = (String) data.getExplanatoryValue(i, attrX.getIndex());
            String successivo = (String) data.getExplanatoryValue(i + 1, attrX.getIndex());
            assertTrue(corrente.compareTo(successivo) <= 0, "Errore di ordinamento alfabetico alla riga " + i);
        }
    }

    /**
     * Testa il funzionamento dell'ordinamento Quicksort sull'attributo continuo 'Y'.
     * Al termine dell'esecuzione, i valori numerici della colonna 'Y' devono risultare in ordine crescente.
     */
    @Test
    @DisplayName("Verifica che il Quicksort ordina in modo crescente gli attributi continui")
    void ordinamentoCorretto_continuousAttribute() {
        Attribute attrY = data.getExplanatoryAttribute(1); // Colonna Y
        
        // Esegue l'ordinamento su tutte le righe
        data.sort(attrY, 0, data.getNumberOfExamples() - 1);
        
        // Controllo che ogni numero sia minore o uguale al successivo
        for (int i = 0; i < data.getNumberOfExamples() - 1; i++) {
            double corrente = (Double) data.getExplanatoryValue(i, attrY.getIndex());
            double successivo = (Double) data.getExplanatoryValue(i + 1, attrY.getIndex());
            assertTrue(corrente <= successivo, "Errore di ordinamento numerico alla riga " + i);
        }
    }


}