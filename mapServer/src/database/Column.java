package database;

/**
 * Rappresenta la definizione di una colonna all'interno di una tabella di un database.
 * Mantiene le informazioni relative al nome della colonna e al suo tipo di dato.
*/
public class Column{
    /** Nome della colonna. */
	private String name;
    /** Tipo di dato della colonna*/
	private String type;
	
    /**
     * Costruisce una nuova colonna con il nome e il tipo specificati.
     * 
     * @param name Nome da assegnare alla colonna.
     * @param type Tipo di dato associato alla colonna.
     */
    Column(String name,String type){
		this.name=name;
		this.type=type;
	}

    /**
     * Restituisce il nome della colonna.
     * @return Stringa rappresenta il nome della colonna.
    */
	public String getColumnName(){
		return name;
	}

    /** 
     * Verifica se la colonna è di tipo numerico.
     * @return true se la colonna è di tipo "number", false altrimenti. 
    */
	public boolean isNumber(){
		return type.equals("number");
	}

    /**
     * Restituisce una rappresentazione testuale della colonna nel formato "name:type".
     * @return una stringa nel formato "name:type" che descrive la colonna.
     */
	public String toString(){
		return name+":"+type;
	}
}