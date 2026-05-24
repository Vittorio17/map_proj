package data;

/**
 * Eccezione per gestire errori di acquisizione del training set.
 */
public class TrainingDataException extends Exception {
    /**
     * Costruttore che riceve un messaggio di errore.
     * @param message Il dettaglio dell'errore.
     */
    public TrainingDataException(String message) {
        super(message);
    }
}