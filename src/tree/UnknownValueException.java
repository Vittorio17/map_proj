package tree;

/**
 * Eccezione per gestire input utente non validi durante la predizione.
 */
public class UnknownValueException extends Exception {
    /**
     * Costruttore che riceve un messaggio di errore.
     * @param message Il dettaglio dell'errore.
     */
    public UnknownValueException(String message) {
        super(message);
    }
}