package database;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per la classe Example appartenente al 
 * package Database.
 */
class ExampleTest{

    /** Verifica che la struttura memorizzi e restituisca gli elementi nell'ordine esatto in cui sono stati aggiunti. */    
    @Test
    @DisplayName("Example: get() restituisce il valore aggiunto con add()")
    void example_addAndGet() {
        Example e = new Example();
        e.add("sunny");
        e.add(25.0);
        assertEquals("sunny", e.get(0));
        assertEquals(25.0, e.get(1));
    }

    /** Verifica che la rappresentazione testuale di un Example contenga i valori dei singoli elementi inseriti. */
    @Test
    @DisplayName("Example: toString() contiene i valori separati da spazio")
    void example_toString() {
        Example e = new Example();
        e.add("hot");
        e.add(30.0);
        String s = e.toString();
        assertTrue(s.contains("hot"));
        assertTrue(s.contains("30.0"));
    }

    /** Verifica che il metodo compareTo() restituisca 0 nel caso in cui vengano confrontate due istanze di Example identiche. */
    @Test
    @DisplayName("Example: compareTo() restituisce 0 per esempi identici")
    void example_compareTo_equal() {
        Example a = new Example();
        a.add("A"); a.add(1.0);
        Example b = new Example();
        b.add("A"); b.add(1.0);
        assertEquals(0, a.compareTo(b));
    }

    /** Verifica che il metodo compareTo() distingua correttamente due istanze che contengono valori differenti. */
    @Test
    @DisplayName("Example: compareTo() distingue due esempi diversi")
    void example_compareTo_different() {
        Example a = new Example();
        a.add(1.0);
        Example b = new Example();
        b.add(2.0);
        assertNotEquals(0, a.compareTo(b));
    }

    /** Verifica che l'iteratore interno espoto permetta il corretto ciclo for-each e lo scorrimento di tutti gli elementi inseriti. */   
    @Test
    @DisplayName("Example: iterator() scorre tutti gli elementi")
    void example_iterator() {
        Example e = new Example();
        e.add("X"); e.add("Y"); e.add("Z");
        int count = 0;
        for (Object o : e) count++;
        assertEquals(3, count);
    }

}