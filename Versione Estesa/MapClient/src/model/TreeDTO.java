package model;

/**
 * Rappresenta un albero di regressione, costituito da un nodo radice
 * e associato a una tabella.
 */
public class TreeDTO{

	/** Nodo radice dell'albero decisionale. */
	private NodeDTO root;
	/** Nome della tabella associata all'albero.*/
	private String tableName;
	
	/**
	 * Costruttore della classe TreeDTO.
	 *
	 * @param root nodo radice dell'albero
	 * @param tableName nome della tabella associata all'albero
	 */
	public TreeDTO(NodeDTO root,String tableName) {
		this.root = root;
		this.tableName = tableName;
	}
	
	/**
	 * Imposta il nodo radice dell'albero.
	 *
	 * @param root nuovo nodo radice dell'albero
	 */
	public void setRoot(NodeDTO root) {
		this.root = root;
	}
	
	/**
	 * Restituisce il nodo radice dell'albero.
	 *
	 * @return nodo radice dell'albero
	 */
	public NodeDTO getRoot(){
		return root;
	}
	
	/**
	 * Imposta il nome della tabella associata all'albero.
	 *
	 * @param tableName nome della tabella
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	/**
	 * Restituisce il nome della tabella associata all'albero.
	 *
	 * @return nome della tabella
	 */
	public String getTableName() {
		return tableName;
	}
}
