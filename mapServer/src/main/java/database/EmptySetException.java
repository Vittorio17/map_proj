package database;

/**
 * Eccezione lanciata quando un'interrogazione al database restituisce un insieme di risultati vuoto.
*/
public class EmptySetException extends Exception{
    
    /** @param message Messaggio di errore dettagliato che descrive la causa dell'eccezione. */
    public EmptySetException(){
        super("[!] The result set is empty");
    }
}
