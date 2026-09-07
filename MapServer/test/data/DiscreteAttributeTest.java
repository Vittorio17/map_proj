package data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe di test unitario per verificare il corretto funzionamento
 * della classe DiscreteAttribute.
 */
class DiscreteAttributeTest {

    private DiscreteAttribute attribute;
    private final String[] valoriIniziali = {"rosso", "verde", "blu"};

    /**
     * Setup iniziale: crea un'istanza di DiscreteAttribute da riutilizzare nei test.
     */
    @BeforeEach
    void setUp() {
        attribute = new DiscreteAttribute("colore", 1, valoriIniziali);
    }

    /**
     * Verifica che il nome e l'indice dell'attributo siano impostati correttamente.
     */
    @Test
    void testNameAndIndex() {
        assertEquals("colore", attribute.getName(), "Il nome dell'attributo discreto deve corrispondere a quello impostato");
        assertEquals(1, attribute.getIndex(), "L'indice dell'attributo discreto deve corrispondere a quello impostato");
    }

    /**
     * Verifica che il conteggio dei valori distinti sia corretto.
     */
    @Test
    void testNumberOfDistinctValues() {
        assertEquals(3, attribute.getNumberOfDistinctValue(), "Il numero di valori distinti deve essere pari a 3");
    }

    /**
     * Verifica che il TreeSet interno elimini automaticamente i valori duplicati.
     */
    @Test
    void testEliminazioneValoriDuplicati() {
        DiscreteAttribute attrDuplicati = new DiscreteAttribute("marca", 0, new String[]{"Fiat", "Fiat", "Ford"});
        assertEquals(2, attrDuplicati.getNumberOfDistinctValue(), "I valori duplicati devono essere filtrati, lasciando 2 elementi distinti"); 
    }

    /**
     * Verifica che l'iteratore scorra correttamente tutti i valori memorizzati.
     */
    @Test
    void testIteratorReturnsValues() {
        int count = 0;
        for (String v : attribute) {
            count++;
        }
        assertEquals(3, count, "L'iteratore deve scorrere tutti e 3 i valori dell'attributo");
    }
}