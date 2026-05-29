package data;
/**
 * La classe ContinuousAttribute estende la classe {@link Attribute} e rappresenta 
 * un attributo di tipo continuo (numerico). Viene utilizzata per gestire variabili
 * che possono assumeri valori in un intervallo reale.
 */
public class ContinuousAttribute extends Attribute{

    /**
     * Costruttore di classe. Invoca il costruttore della super-classe per
     * inizializzare il nome e l'identificativo dell'attributo.
     * @param name Nome simboli dell'attributo.
     * @param index Identificativo numerico dell'attributo.
    */
    ContinuousAttribute(String name,int index){
        super(name,index);
    }
}

