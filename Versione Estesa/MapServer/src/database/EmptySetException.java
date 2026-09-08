package database;

/**
 * Eccezione lanciata quando un'interrogazione al database restituisce un insieme di risultati vuoto.
*/
public class EmptySetException extends Exception{
    
	/**
     * Costruisce l'eccezione con un messaggio di errore predefinito.
     */
	public EmptySetException(){
        super("[!] The result set is empty");
    }
}
