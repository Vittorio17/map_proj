package data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per la classe ContinuousAttribute.
 */
class ContinuousAttributeTest {
    
    /** Verifica che il metodo getName() restituisce il nome corretto dell'attributo.*/
    @Test
    @DisplayName("Verifica che il metodo getName() restituisce il nome corretto dell'attributo")
    void continuousAttribute_getName() {
        ContinuousAttribute attr = new ContinuousAttribute("chilometri", 0);
        assertEquals("chilometri", attr.getName());
    }

    /** Verifica che il metodo getIndex() restituisce l'indice numerico corretto. */
    @Test
    @DisplayName("Verifica che il metodo getIndex() restituisce l'indice numerico corretto")
    void continuousAttribute_getIndex() {
        ContinuousAttribute attr = new ContinuousAttribute("prezzo", 3);
        assertEquals(3, attr.getIndex());
    }

    

}