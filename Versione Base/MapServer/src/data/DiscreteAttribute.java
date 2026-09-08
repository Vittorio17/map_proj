package data;
import java.util.*;

/**
 * La classe DiscreteAttribute estende la classe Attribute, implementa l'interfaccia
 * Iterable e rappresentaun attributo discreto, caratterizzato da un insieme finito di valori testuali.
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
    DiscreteAttribute(String name,int index,String[] values){
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

    Restituisce il valore presente nella posizione specificata.
    @param i indice del valore da restituire, a partire da 0
    @return il valore {@code String} presente all'indice {@code i}
    @throws IndexOutOfBoundsException se l'indice è minore di 0
     	    oppure maggiore o uguale alla dimensione della lista
    */
    public String getValue(int i){
        if (i < 0 || i >= values.size()) {
            throw new IndexOutOfBoundsException("Indice non valido: " + i);
        }
        
        int current = 0;
        for (String value : values) {
            if (current == i) {
                return value;
            }
            current++;
        }
        return null;
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
