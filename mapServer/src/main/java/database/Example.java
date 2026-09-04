package database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Rappresenta un vettore di oggetti che modella una riga o una tupla di dati.
 * Implementa Comparable per consentire il confronto posizionale tra istanze
 * e Iterable per supportare l'iterazione sui suoi elementi.
*/
public class Example implements Comparable<Example>, Iterable<Object>{

    /** Lista interna che memorizza i valori della tupla di dati. */
	private List<Object> example=new ArrayList<Object>();

    /**
     * Aggiunge un nuovo elemento in coda al vettore.
     * @param o Oggetto da inserire nel vettore.
     */
	void add(Object o){
		example.add(o);
	}
	
    /**
     * Restituisce l'elemento situato alla posizione specificata.
     * @param i Indice posizionale dell'oggetto desiderato.
     * @return Oggetto presente all'indice specificato.
     */
	public Object get(int i){
		return example.get(i);
	}

    /**
     * Confronta questa istanza con un'altra istanza di Example elemento per elemento.
     * Al primo elemento differente riscontrato, viene restituito il risultato del confronto 
     * basato sull'interfaccia Comparable.
     *
     * @param ex Oggetto Example da confrontare con l'istanza corrente.
     * @return Intero negativo, zero, o un intero positivo se il primo elemento differente 
     *         di ex è rispettivamente minore, uguale o maggiore dell'elemento corrispondente 
     *         in questa istanza.
     */
	public int compareTo(Example ex) {
		int i=0;
		for(Object o:ex.example){
			if(!o.equals(this.example.get(i)))
				return ((Comparable)o).compareTo(example.get(i));
			i++;
		}
		return 0;
	}

    /** @return una stringa contenente la rappresentazione testuale di tutti gli elementi separati da uno spazio. */
	public String toString(){
		String str="";
		for(Object o:example)
			str+=o.toString()+ " ";
		return str;
	}

    /**
     * Restituisce un iteratore per scorrere gli elementi interni del vettore.
     * @return Iterator per gli oggetti contenuti in questo Example.
     */
	@Override
	public Iterator<Object> iterator() {
		return example.iterator();
	}
	
}