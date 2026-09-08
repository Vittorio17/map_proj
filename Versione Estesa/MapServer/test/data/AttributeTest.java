package data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe di test unitario per verificare le funzionalità di base
 * definite nella classe astratta Attribute.
 */
class AttributeTest {

    private Attribute attribute;

    /**
     * Setup iniziale: sfrutta un'istanza concreta (ContinuousAttribute)
     * per testare i metodi ereditati della superclasse astratta Attribute.
     */
    @BeforeEach
    void setUp() {
        attribute = new ContinuousAttribute("temperatura", 5);
    }

    /**
     * Verifica la corretta restituzione del nome simbolico dell'attributo.
     */
    @Test
    void testGetName() {
        assertEquals("temperatura", attribute.getName(),"Il metodo getName deve restituire il nome corretto passato al costruttore");
    }

    /**
     * Verifica la corretta restituzione dell'indice numerico identificativo.
     */
    @Test
    void testGetIndex() {
        assertEquals(5, attribute.getIndex(),"Il metodo getIndex deve restituire l'indice numerico corretto passato al costruttore");
    }
}