package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData; 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator; 
import java.util.List;


/**
 * Modella lo schema di una tabella all'interno del database relazionale.
 * La classe estrae i metadati di una specifica tabella e mappa i tipi SQL 
 * nativi nei tipi astratti gestiti dall'applicazione ("string" o "number").
*/
public class TableSchema implements Iterable<Column>{
	
	/** Lista che memorizza le colonne che compongono lo schema della tabella. */
    private List<Column> tableSchema=new ArrayList<Column>();
	
	/**
     * Costruisce lo schema della tabella estraendo i metadati dal database.
     * Inizializza una mappa di conversione per i tipi di dato e interroga il DBMS
     * per ottenere il nome e il tipo di ciascuna colonna della tabella specificata.
     * 
     * @param db l'oggetto per l'accesso e la gestione della connessione al database.
     * @param tableName il nome della tabella di cui si vuole estrarre lo schema.
     * @throws SQLException se si verifica un errore di accesso al database o durante l'estrazione dei metadati.
     */
	public TableSchema(DbAccess db, String tableName) throws SQLException{
		
		HashMap<String,String> mapSQL_JAVATypes=new HashMap<String, String>();
	
		mapSQL_JAVATypes.put("CHAR","string");
		mapSQL_JAVATypes.put("VARCHAR","string");
		mapSQL_JAVATypes.put("LONGVARCHAR","string");
		mapSQL_JAVATypes.put("BIT","string");
		mapSQL_JAVATypes.put("SHORT","number");
		mapSQL_JAVATypes.put("INT","number");
		mapSQL_JAVATypes.put("LONG","number");
		mapSQL_JAVATypes.put("FLOAT","number");
		mapSQL_JAVATypes.put("DOUBLE","number");
		
		
	
		Connection con=db.getConnection();
		DatabaseMetaData meta = con.getMetaData();
	    ResultSet res = meta.getColumns(null, null, tableName, null);
		   
	    while (res.next()) {
	        if(mapSQL_JAVATypes.containsKey(res.getString("TYPE_NAME"))){
	        	tableSchema.add(new Column(
	        		res.getString("COLUMN_NAME"),
	        		mapSQL_JAVATypes.get(res.getString("TYPE_NAME")))
	        	);
			}
	    }
	    res.close();
	}
	  
	/** @return il numero di colonne (attributi) presenti nello schema della tabella. */
	public int getNumberOfAttributes(){
		return tableSchema.size();
	}
		
	/**
     * Restituisce la colonna situata in una posizione specificata nello schema.
     * @param index Indice posizionale della colonna desiderata.
     * @return Oggetto {@link Column} corrispondente all'indice fornito.
	*/
	public Column getColumn(int index){
		return tableSchema.get(index);
	}

	/**
     * Restituisce un iteratore per scorrere sequenzialmente le colonne dello schema.
     * Permette l'utilizzo della classe all'interno di cicli for-each.
     * 
     * @return Iterator configurato per oggetti di tipo Column.
     */
	@Override
	public Iterator<Column> iterator() {
		return tableSchema.iterator();
	}
}
