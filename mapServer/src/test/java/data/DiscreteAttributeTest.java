package data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di unità della classe DiscreteAttribute
 */
class DiscreteAttributeTest {

    /** Verifica se assegna correttamente nome e indice nel costruttore */
    @Test
    @DisplayName("Verifica se assegna correttamente nome e indice nel costruttore")
    void discreteAttribute_nameAndIndex() {
        DiscreteAttribute attr = new DiscreteAttribute("colore", 1, new String[]{"rosso", "verde", "blu"});
        assertEquals("colore", attr.getName());
        assertEquals(1, attr.getIndex());
    }

    /** Verifica se conta correttamente il numero di valori distinti. */
    @Test
    @DisplayName("Verifica se conta correttamente il numero di valori distinti")
    void discreteAttribute_numberOfDistinctValues() {
        DiscreteAttribute attr = new DiscreteAttribute("colore", 1, new String[]{"rosso", "verde", "blu"});
        assertEquals(3, attr.getNumberOfDistinctValue());
    }

    /** Verifica che il costruttore elimini i valori duplicati. */
    @Test
    @DisplayName("Verifica che il costruttore elimini i valori duplicati")
    void costruttore_eliminaValoriDuplicati() {
        // Passiamo due volte "Fiat", ma l'attributo deve gestire correttamente la deduplicazione
        DiscreteAttribute attr = new DiscreteAttribute("marca", 0, new String[]{"Fiat", "Fiat", "Ford"});
        assertEquals(2, attr.getNumberOfDistinctValue()); 
    }

    /** Verifiche che l'iteratore scorre correttamente tutti i valori distinti memorizzati. */
    @Test
    @DisplayName("Verifiche che l'iteratore scorre correttamente tutti i valori distinti memorizzati")
    void discreteAttribute_iteratorReturnsValues() {
        String[] marche = {"Fiat", "Ford", "Toyota"};
        DiscreteAttribute attr = new DiscreteAttribute("marca", 0, marche);
        
        int count = 0;
        for (String v : attr) {
            count++;
        }
        assertEquals(3, count);
    }
}