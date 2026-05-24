package database;

/**
 * Eccezione per gestire errori durante la connessione al Database.
 */
public class DatabaseConnectionException extends Exception{
    
    /**
     * Costruttore che riceve un messaggio di errore.
     * @param message Messaggio di errore.
     */
    public DatabaseConnectionException(String message){
        super(message);
    }
}
