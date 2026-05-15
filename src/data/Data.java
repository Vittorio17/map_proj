package data;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * La classe Data modella l'insieme di esempi di training. Si occupa 
 * di caricare i dati da file, permettendo di gestire gli attributi
 * indipendenti (esplicativi) e l'attributo di classe (target).
 */
public class Data {

   	/** Matrice degli esempi. */
	private Object[][] data;
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
    public Data(String fileName) throws TrainingDataException {
		File inFile = new File(fileName);
		
		// 1. Controllo: File inesistente
		if (!inFile.exists()) {
			throw new TrainingDataException("Errore: Il file " + fileName + " non esiste.");
		}
	
		Scanner sc = null;
		try {
			sc = new Scanner(inFile);
			if (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] s = line.split(" ");
	
				// 2. Controllo: Schema mancante o errato
				if (!line.contains("@schema")) {
					throw new TrainingDataException("Errore: Lo schema del training set è mancante o errato.");
				}
	
				short iAttribute = 0;
				line = sc.nextLine();
	
				while (!line.contains("@data")) {
					s = line.split(" ");
					if (s[0].equals("@desc")) {
						String[] discreteValues = s[2].split(",");
						explanatorySet.add(new DiscreteAttribute(s[1], iAttribute, discreteValues));
						iAttribute++;
					} else if (s[0].equals("@target")) {
						classAttribute = new ContinuousAttribute(s[1], iAttribute);
					}
					line = sc.nextLine();
				}
	
				// 3. Controllo: Variabile target numerica 
				if (classAttribute == null || !(classAttribute instanceof ContinuousAttribute)) {
					throw new TrainingDataException("Errore: Variabile target non trovata o non numerica.");
				}
	
				// 4. Controllo: Training set vuoto
				s = line.split(" ");
				numberOfExamples = Integer.parseInt(s[1]);
				if (numberOfExamples == 0) {
					throw new TrainingDataException("Errore: Il training set non contiene esempi.");
				}
	
				//Popolamento dati
				data = new Object[numberOfExamples][explanatorySet.size() + 1];
				short iRow = 0;
				while (sc.hasNextLine() && iRow < numberOfExamples) {
					line = sc.nextLine();
					s = line.split(",");
					for (short jColumn = 0; jColumn < s.length - 1; jColumn++) {
						data[iRow][jColumn] = s[jColumn];
					}
					data[iRow][s.length - 1] = Double.valueOf(s[s.length - 1]);
					iRow++;
				}
	
			} else {
				throw new TrainingDataException("Errore: Il file è vuoto.");
			}
		} catch (FileNotFoundException e) {
			throw new TrainingDataException(e.toString());
		} finally {
			if (sc != null) sc.close();
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
        return (Double) data[exampleIndex][columnIndex];
    }
    
	/**
	 * Restituisce il valore di un attributo indipendente per uno specifico esempio.
	 * @param exampleIndex Indice di riga dell'esempio.
	 * @param attributeIndex Indice dell'attributo indipendente.
	 * @return L'oggetto rappresentante il valore dell'attributo richiesto.
	 */
    public Object getExplanatoryValue(int exampleIndex,int attributeIndex) {
        return data[exampleIndex][attributeIndex];
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
		for(int i=0;i<numberOfExamples;i++){
			for(int j=0;j<explanatorySet.size();j++)
				value+=data[i][j]+",";
			
			value+=data[i][explanatorySet.size()]+"\n";
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
		Object temp;
		for (int k=0;k<getNumberOfExplanatoryAttributes()+1;k++){
			temp=data[i][k];
			data[i][k]=data[j][k];
			data[j][k]=temp;
		}
		
	}
	

	
	
	/**
	 * Partiziona il training set rispetto a un attributo.
	 * @param attribute Attributo per il partizionamento.
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
	 * Ordina il dataset utilizzando l'algoritmo Quicksort.
	 * @param attribute Attributo su cui basare l'ordinamento.
	 * @param inf Indice inferiore.
	 * @param sup Indice superiore.
	 */
	private void quicksort(Attribute attribute, int inf, int sup){
		
		if(sup>=inf){
			
			int pos;
			
			pos=partition((DiscreteAttribute)attribute, inf, sup);
					
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
