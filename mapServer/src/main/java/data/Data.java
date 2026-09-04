package data;

import java.sql.SQLException;
import java.util.*;

import database.Column;
import database.DatabaseConnectionException;
import database.EmptySetException;
import database.DbAccess;
import database.Example;
import database.TableData;
import database.TableSchema;

/**
 * La classe Data modella l'insieme di esempi di training. Si occupa 
 * di caricare i dati da file, permettendo di gestire gli attributi
 * indipendenti (esplicativi) e l'attributo di classe (target).
 */
public class Data {

   	/** Lista di esempi. */
	private List<Example> data = new ArrayList<Example>();
	/** Numero di righe del dataset. */
	private int numberOfExamples;
	/** Lista degli attributi indipendenti. */
	private List<Attribute> explanatorySet = new LinkedList<Attribute>();
	/** Attributo target da predire. */
	private ContinuousAttribute classAttribute;

	/**
 	* Inizializza un oggetto Data caricando il training set da un file.
 	* Il file deve seguire una struttura specifica con le keyword @schema, @desc, @target e @data.
 	* @param fileName Il percorso o il nome del file contenente i dati.
 	* @throws TrainingDataException Sollevata se si verifica una delle seguenti condizioni:
 	*         - Il file non esiste sul disco.
 	*         - L'intestazione @schema è mancante o malformata.
 	*         - Il training set non contiene esempi (@data 0).
 	*         - La variabile target non è definita o non è di tipo numerico.
	*/
    public Data(String tableName) throws TrainingDataException {
		DbAccess db = new DbAccess();

		try{
			db.initConnection();
			TableSchema ts = new TableSchema(db, tableName);
			int numAttributes = ts.getNumberOfAttributes();
		
			if(numAttributes==0){
				throw new TrainingDataException("Table '" + tableName + "' does not exist or contains no valid attributes.");
			}
			if(numAttributes<2){
				throw new TrainingDataException("Table contains less than two columns ("+ numAttributes + ").");
			}
			//Controllo che l'attributo target sia numerico
			Column lastColumn = ts.getColumn(numAttributes-1);
			if(!lastColumn.isNumber()){
				throw new TrainingDataException("The target attribute corresponding to the last column is non-numeric.");
			}
			TableData tData = new TableData(db);
			this.data = tData.getTransazioni(tableName);
			this.numberOfExamples = this.data.size();

			for(int i=0;i<numAttributes-1;i++){
				Column col = ts.getColumn(i);
				if(col.isNumber()){
					explanatorySet.add(new ContinuousAttribute(col.getColumnName(), i));
				}else{
					Set<Object> distinctValues = tData.getDistinctColumnValues(tableName, col);
					String[] values = new String[distinctValues.size()];
					int k=0;
					for(Object v:distinctValues){
						values[k++] = v.toString();
					}
					explanatorySet.add(new DiscreteAttribute(col.getColumnName(), i, values));
				}
			}
			classAttribute = new ContinuousAttribute(lastColumn.getColumnName(), numAttributes - 1);
		}catch(DatabaseConnectionException e){
			throw new TrainingDataException("Database connection failed: " + e.getMessage());
		}catch(SQLException e){
			throw new TrainingDataException("SQL error while loading data: " + e.getMessage());
		}catch(EmptySetException e){
			throw new TrainingDataException("The selected table has zero tuples");
		}finally{
			db.closeConnection();
		}
	}
	
			

	/**
	 * Restituisce il numero di esempi del training set.
	 * @return Numero di esempi memorizzati.
	 */
    public int getNumberOfExamples(){
        return numberOfExamples;
    }

	/**
	 * Restituisce il numero di attributi indipendenti.
	 * @return Numero di attributi del training set.
	*/
    public int getNumberOfExplanatoryAttributes(){
        return explanatorySet.size();
    }

	/**
	 * Restituisce il valore dell'attributo di classe per uno specifico esempio.
	 * @param exampleIndex Indice di riga dell'esempio
	 * @return Valore numerico (Double) dell'attributo target per l'esempio scelto.
	 */
    public double getClassValue(int exampleIndex){
        int columnIndex = classAttribute.getIndex();
        return (Double) data.get(exampleIndex).get(columnIndex);
    }
    
	/**
	 * Restituisce il valore di un attributo indipendente per uno specifico esempio.
	 * @param exampleIndex Indice di riga dell'esempio.
	 * @param attributeIndex Indice dell'attributo indipendente.
	 * @return L'oggetto rappresentante il valore dell'attributo richiesto.
	 */
    public Object getExplanatoryValue(int exampleIndex,int attributeIndex) {
        return data.get(exampleIndex).get(attributeIndex);
    }
	
	/**
	 * Restituisce l'attributo indipendente corrispondente all'indnice specificato.
	 * @param index Indice dell'array {@code explanatorySet[]}
	 * @return L'oggetto {@link Attribute} indicizzato.
	 */
    public Attribute getExplanatoryAttribute(int index){
        return explanatorySet.get(index);
    }

	/**
	 * Restituisce l'oggetto rappresentante l'attributo di classe.
	 * @return L'oggetto {@link ContinuousAttribute} associato al valore target.
	 */
    ContinuousAttribute getClassAttribute(){
        return classAttribute;
    }

	/**
	 * Legge i valori di tutti gli attributi per ogni esempio e li concatena 
	 * in una stringa, restituendo la rappresentazione testual del dataset.
	 * @return Una stringa contenente l'intero training set in formato testuale.
	 */
    public String toString(){
		String value="";
		for(int i=0; i<data.size(); i++){
        	value += (i+1) + ": " + data.get(i).toString() + "\n";
    	}	
		return value;
	}
    
	/**
	 * Ordina il sottoinsieme di esempi comprensi nell'intervallo [beginExampleIndex,endExampleindex]
	 * rispetto a uno specifico attributo passato come prametro al metodo.
	 * @param attribute 		L'attributo in base al quale ordinare i dati.
	 * @param beginExampleIndex Indice iniziale dell'intervallo di esempi.
	 * @param endExampleIndex 	Indice finale dell'intervallo di esempi.
	 */
	public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex){
		quicksort(attribute, beginExampleIndex, endExampleIndex);
	}
	
	/**
	 * Scambia i valori alle posizioni i e j.
	 * @param i indice del primo esempio.
	 * @param j indice del secondo esempio.
	 */
	private void swap(int i,int j){
		Example temp = data.get(i);
		data.set(i, data.get(j));
		data.set(j, temp);
	}
	
	
	/**
	 * Partiziona il training set rispetto a un attributo discreto.
	 * @param attribute Attributo discreto per il partizionamento.
	 * @param inf Indice inferiore.
	 * @param sup Indice superiore.
	 * @return Indice del pivot.
	 */
	private  int partition(DiscreteAttribute attribute, int inf, int sup){
		int i,j;
	
		i=inf; 
		j=sup; 
		int	med=(inf+sup)/2;
		String x=(String)getExplanatoryValue(med, attribute.getIndex());
		swap(inf,med);
	
		while (true) 
		{
			
			while(i<=sup && ((String)getExplanatoryValue(i, attribute.getIndex())).compareTo(x)<=0){ 
				i++; 
				
			}
		
			while(((String)getExplanatoryValue(j, attribute.getIndex())).compareTo(x)>0) {
				j--;
			
			}
			
			if(i<j) { 
				swap(i,j);
			}
			else break;
		}
		swap(inf,j);
		return j;

	}

	/**
	 * Partiziona il training set rispetto a un attributo continuo.
	 * @param attribute Attributo continuo per il partizionamento.
	 * @param inf Indice inferiore.
	 * @param sup Indice superiore.
	 * @return Indice del pivot.
	 */
	private  int partition(ContinuousAttribute attribute, int inf, int sup){
		int i,j;
	
		i=inf; 
		j=sup; 
		int	med=(inf+sup)/2;
		Double x=(Double)getExplanatoryValue(med, attribute.getIndex());
		swap(inf,med);
	
		while (true) 
		{
			
			while(i<=sup && ((Double)getExplanatoryValue(i, attribute.getIndex())).compareTo(x)<=0){ 
				i++; 
				
			}
		
			while(((Double)getExplanatoryValue(j, attribute.getIndex())).compareTo(x)>0) {
				j--;
			
			}
			
			if(i<j) { 
				swap(i,j);
			}
			else break;
		}
		swap(inf,j);
		return j;

	}

	
	/**
	 * Ordina il dataset utilizzando l'algoritmo Quicksort.
	 * @param attribute Attributo su cui basare l'ordinamento.
	 * @param inf Indice inferiore.
	 * @param sup Indice superiore.
	 */
	private void quicksort(Attribute attribute, int inf, int sup){
		if(sup>=inf){
			
			int pos;
			if(attribute instanceof DiscreteAttribute)
				pos=partition((DiscreteAttribute)attribute, inf, sup);
			else
				pos=partition((ContinuousAttribute)attribute, inf, sup);
					
			if ((pos-inf) < (sup-pos+1)) {
				quicksort(attribute, inf, pos-1); 
				quicksort(attribute, pos+1,sup);
			}
			else
			{
				quicksort(attribute, pos+1, sup); 
				quicksort(attribute, inf, pos-1);
			}	
		}
	}

}
