package model;

import java.util.List;
import java.util.ArrayList;

/**
 * Rappresenta un nodo di un albero decisionale.
 * Un nodo può essere interno, caratterizzato da una condizione di split,
 * oppure una foglia, caratterizzata da un valore predetto.
 */
public class NodeDTO{
	/** Indica se il nodo è una foglia dell'albero. */
	private boolean leaf;
	/** Condizione utilizzata per effettuare lo split del nodo. */
	private String splitCondition;
	/** Valore predetto dal nodo, utilizzato nel caso di un nodo foglia. */
	private Double predictedValue;
	/** Lista dei nodi figli. */
	private List<NodeDTO> children;
	/** Indica se il nodo fa parte del percorso decisionale selezionato dall'utente. */
	private boolean selected = true;
	
	/**
	 * Costruttore per la creazione di un nodo interno dell'albero.
	 *
	 * @param splitCondition condizione utilizzata per effettuare lo split del nodo
	 */
	public NodeDTO(String splitCondition) {
		leaf = false;
		predictedValue = null;
		children = new ArrayList<NodeDTO>();
		this.splitCondition = splitCondition;
	}
	
	/**
	 * Costruttore per la creazione di un nodo foglia.
	 *
	 * @param predictedValue valore predetto dal nodo foglia
	 */
	public NodeDTO(Double predictedValue) {
		leaf = true;
		splitCondition = null;
		children = new ArrayList<NodeDTO>();
		this.predictedValue = predictedValue;
	}
	
	/**
	 * Imposta se il nodo è una foglia.
	 *
	 * @param leaf true se il nodo è una foglia, false altrimenti
	 */
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	
	/**
	 * Restituisce se il nodo è una foglia.
	 *
	 * @return true se il nodo è una foglia, false altrimenti
	 */
	public boolean isLeaf() {
		return leaf;
	}
	
	/**
	 * Imposta la condizione di split del nodo.
	 *
	 * @param splitCondition condizione utilizzata per effettuare lo split
	 */
	public void setSplitCondition(String splitCondition) {
		this.splitCondition = splitCondition;
	}
	
	/**
	 * Restituisce la condizione di split del nodo.
	 *
	 * @return la condizione di split
	 */
	public String getSplitCondition() {
		return splitCondition;
	}
	
	/**
	 * Imposta il valore predetto dal nodo.
	 *
	 * @param predictedValue valore predetto
	 */
	public void setPredictedValue(Double predictedValue) {
		this.predictedValue = predictedValue;
	}
	
	/**
	 * Restituisce il valore predetto dal nodo.
	 *
	 * @return il valore predetto
	 */
	public Double getPredictedValue() {
		return predictedValue;
	}
	
	/**
	 * Restituisce la lista dei nodi figli.
	 *
	 * @return lista dei nodi figli
	 */
	public List<NodeDTO> getChildren(){
		return children;
	}
	
	/**
	 * Aggiunge un nodo figlio alla lista dei figli.
	 *
	 * @param child nodo da aggiungere come figlio
	 */
	public void addChild(NodeDTO child) {
		children.add(child);
	}
	
	/**
	 * Restituisce una rappresentazione testuale del nodo.
	 * Se il nodo contiene una condizione di split, viene restituita quest'ultima;
	 * altrimenti viene restituito il valore della predizione.
	 *
	 * @return rappresentazione testuale del nodo
	 */
	public String toString() {
		if(splitCondition!=null) {
			return splitCondition;
		}
		return ("Predizione: " + predictedValue);
	}
	
	/**
     * Verifica se il nodo è contrassegnato come selezionato nel percorso decisionale.
     * @return true se il nodo è selezionato, false altrimenti
     */
	public boolean isSelected() {
        return selected;
    }

	/**
     * Imposta lo stato di selezione del nodo per la visualizzazione del percorso.
     *
     * @param selected true se il nodo deve risultare selezionato, false altrimenti
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
