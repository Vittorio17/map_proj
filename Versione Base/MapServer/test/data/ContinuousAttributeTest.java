package data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe di test unitario per verificare il corretto funzionamento
 * della classe ContinuousAttribute.
 */
class ContinuousAttributeTest {
    
    private ContinuousAttribute attribute;

    /**
     * Setup iniziale: crea un'istanza di ContinuousAttribute da riutilizzare nei test.
     */
    @BeforeEach
    void setUp() {
        attribute = new ContinuousAttribute("chilometri", 2);
    }

    /**
     * Verifica che il metodo getName restituisca correttamente il nome assegnato.
     */
    @Test
    void testGetName() {
        assertEquals("chilometri", attribute.getName(), "Il nome dell'attributo continuo deve corrispondere a quello impostato");
    }

    /**
     * Verifica che il metodo getIndex restituisca correttamente l'indice numerico assegnato.
     */
    @Test
    void testGetIndex() {
        assertEquals(2, attribute.getIndex(), 
            "L'indice numerico dell'attributo continuo deve corrispondere a quello impostato");
    }
}