package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * Gestisce l'estrazione e il recupero dei dati memorizzati nelle tabelle del database.
 * Fornisce metodi per recuperare intere transazioni (tuple) o valori distinti 
 * da specifiche colonne, mappandoli in strutture dati Java.
 */
public class TableData {

    /** Riferimento all'oggetto per l'accesso e la gestione della connessione al database. */
	private DbAccess db;
	
    /**
     * Costruisce un oggetto TableData collegandolo all'istanza di accesso al database specificata.
     * @param db Oggetto DbAccess da utilizzare per le connessioni e le interrogazioni.
     */
	public TableData(DbAccess db) {
		this.db=db;
	}

    /**
     * Recupera tutte le transazioni (righe) presenti nella tabella specificata.
     * Genera dinamicamente una query SQL basata sullo schema della tabella, estrae i dati
     * differenziando tra valori numerici e testuali, e li incapsula in oggetti Example.
     * 
     * @param table Nome della tabella da cui estrarre i dati.
     * @return Lista ordinata contenente le transazioni estratte.
     * @throws SQLException se si verifica un errore di accesso al database, se la tabella 
     *                      non esiste o se lo schema risulta privo di attributi.
     * @throws EmptySetException se la tabella esiste ma l'interrogazione non restituisce alcuna riga.
     */
	public List<Example> getTransazioni(String table) throws SQLException, EmptySetException{
		LinkedList<Example> transSet = new LinkedList<Example>();
		Statement statement;
		TableSchema tSchema=new TableSchema(db,table);
		
		
		String query="select ";
		
		for(int i=0;i<tSchema.getNumberOfAttributes();i++){
			Column c=tSchema.getColumn(i);
			if(i>0)
				query+=",";
			query += c.getColumnName();
		}
		if(tSchema.getNumberOfAttributes()==0)
			throw new SQLException();
		query += (" FROM "+table);
		
		statement = db.getConnection().createStatement();
		ResultSet rs = statement.executeQuery(query);
		boolean empty=true;
		while (rs.next()) {
			empty=false;
			Example currentTuple=new Example();
			for(int i=0;i<tSchema.getNumberOfAttributes();i++)
				if(tSchema.getColumn(i).isNumber())
					currentTuple.add(rs.getDouble(i+1));
				else
					currentTuple.add(rs.getString(i+1));
			transSet.add(currentTuple);
		}
		rs.close();
		statement.close();
		if(empty) throw new EmptySetException();
		
		return transSet;
	}

    /**
     * Esegue una query per ottenere tutti i valori distinti presenti in una specifica colonna.
     * I valori estratti vengono inseriti all'interno di un insieme ordinato in modo ascendente.
     * 
     * @param table Nome della tabella a cui appartiene la colonna.
     * @param column Oggetto Column che rappresenta la colonna da interrogare.
     * @return Set di oggetti (ordinati tramite TreeSet) contenente i valori distinti.
     * @throws SQLException se si verifica un errore nell'esecuzione della query SQL o nell'accesso al database.
     */
	public Set<Object> getDistinctColumnValues(String table,Column column) throws SQLException{
        Set<Object> value = new TreeSet<Object>();
        String query = "SELECT DISTINCT " + column.getColumnName() + " FROM " + table + " ORDER BY " + column.getColumnName() + " ASC";

        Statement st = db.getConnection().createStatement();
        ResultSet rs = st.executeQuery(query);
    
        while(rs.next()){
            if(column.isNumber()){
                value.add(rs.getDouble(1));
            }else{
                value.add(rs.getString(1));
            }
        }

        rs.close();
        st.close();
        return value;
    }	

}
