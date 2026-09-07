package database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe di test unitario per verificare il comportamento della classe Example.
 */
class ExampleTest {

    private Example example;

    @BeforeEach
    void setUp() {
        example = new Example();
        example.add("sunny");
        example.add(25.0);
    }

    @Test
    void testAddAndGet() {
        assertEquals("sunny", example.get(0), "Il primo elemento memorizzato deve corrispondere alla stringa inserita");
        assertEquals(25.0, example.get(1), 
            "Il secondo elemento memorizzato deve corrispondere al valore numerico inserito");
    }

    @Test
    void testGetOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> example.get(5), 
            "La richiesta di un indice non presente deve sollevare IndexOutOfBoundsException");
    }

    @Test
    void testToString() {
        String output = example.toString();
        assertTrue(output.contains("sunny"),"La rappresentazione testuale deve contenere la stringa dell'attributo discreto");
        assertTrue(output.contains("25.0"), "La rappresentazione testuale deve contenere la stringa dell'attributo continuo");
    }

    @Test
    void testCompareToEqual() {
        Example altroExample = new Example();
        altroExample.add("sunny");
        altroExample.add(25.0);

        assertEquals(0, example.compareTo(altroExample),"Il confronto tra due istanze con gli stessi valori deve restituire 0");
    }

    @Test
    void testCompareToDifferent() {
        Example diverso = new Example();
        diverso.add("rain");
        diverso.add(25.0);

        assertNotEquals(0, example.compareTo(diverso),"Il confronto tra due istanze con valori distinti non deve restituire 0");
    }

    @Test
    void testIterator() {
        int count = 0;
        for (Object item : example) {
            count++;
        }
        assertEquals(2, count,"L'iteratore deve attraversare tutti gli elementi inseriti nel vettore");
    }
}