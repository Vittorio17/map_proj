package data;
import java.util.*;

/**
 * La classe DiscreteAttribute estende la classe {@link Attribute}, implementa l'interfaccia
 * {@link Iterable} e rappresentaun attributo discreto, caratterizzato da 
 * un insieme finito di valori testuali.
 */
public class DiscreteAttribute extends Attribute implements Iterable<String>{

    /** Set di valori distinti dell'attributo ordinati tramite un albero (TreeSet). */
    private Set<String> values;

    /**
     * Costruttore di classe. Invoca il costruttore della super-classe e avvalora
     * l'array values[] con i valori discreti passati come parametri.
     * @param name  Nome simbolico dell'attributo
     * @param index Identificativo numerico dell'attributo.
     * @param values Set di stringhe contenente i valori discreti dell'attributo.
     */
    public DiscreteAttribute(String name,int index,String[] values){
        super(name,index);
        this.values = new TreeSet<String>();
        for(String v : values){
            this.values.add(v);
        }
    }

    /**
     * Restituisce il numero di valori discreti dell'attributo.
     * @return La cardinalità del set {@code values}
    */
    public int getNumberOfDistinctValue(){
        return values.size();
    }

    

    /**
     * Implementazione del metodo dell'interfaccia Iterable.
     * Consente di iterare sui valori dell'attributo.
     * @return Iteratore sul Set
     */
    @Override 
    public Iterator<String> iterator(){
        return values.iterator();
    }
}
