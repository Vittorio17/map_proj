package network;

import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import view.MainFrame;

/**
 * Worker asincrono responsabile del recupero dell'elenco delle tabelle dal server.
 * Esegue la richiesta di rete in background per ottenere i nomi delle tabelle
 * disponibili nel database remoto e aggiorna i componenti dell'interfaccia grafica
 * al completamento dell'operazione.
 */
public class TableLoaderWorker extends SwingWorker<List<String>, Void> {
	/** Oggetto per la gestione della connessione socket verso il server. */
    private ServerConnection connection;
    /** Indirizzo IP del server remoto. */
    private String ip;
    /** Porta TCP del server su cui stabilire la connessione. */
    private int port;
    /** Riferimento al frame principale dell'applicazione per l'aggiornamento della UI. */
    private MainFrame view;

    /**
     * Costruttore che inizializza la connessione, i parametri di rete e la vista.
     *
     * @param connection istanza della connessione al server
     * @param ip indirizzo IP del server
     * @param port numero di porta del server
     * @param view finestra principale dell'interfaccia grafica
     */
    public TableLoaderWorker(ServerConnection connection, String ip, int port, MainFrame view) {
        this.connection = connection;
        this.ip = ip;
        this.port = port;
        this.view = view;
    }

    /**
     * Esegue la richiesta delle tabelle in background.
     * Verifica la connessione, invia il codice di comando corrispondente e
     * riceve la lista dei nomi delle tabelle dal server remoto.
     *
     * @return lista dei nomi delle tabelle presenti nel database
     * @throws Exception se la risposta del server segnala un errore o si verificano problemi di comunicazione
     */
    @Override
    protected List<String> doInBackground() throws Exception {
        if (!connection.isConnected()) {
            connection.connect(ip, port);
        }

        connection.send(4);

        String status = (String) connection.receive();
        if ("OK".equals(status)) {
            @SuppressWarnings("unchecked")
            List<String> tables = (List<String>) connection.receive();
            return tables;
        } else {
            String errorMsg = (String) connection.receive();
            throw new Exception(errorMsg);
        }
    }

    /**
     * Eseguito nell'Event Dispatch Thread (EDT) al termine dell'elaborazione in background.
     * Riabilita il pulsante di aggiornamento delle tabelle e aggiorna il menu di selezione
     * e il log della vista con i risultati ricevuti o con l'eventuale errore riscontrato.
     */
    @Override
    protected void done() {
        view.getControlPanel().getRefreshTablesButton().setEnabled(true);

        try {
            List<String> tables = get();
            if (tables != null && !tables.isEmpty()) {
                view.getControlPanel().updateTableList(tables);
                view.getLogPanel().log("Tabelle recuperate: " + tables.size() + " trovate.");
            } else {
                view.getLogPanel().log("Nessuna tabella trovata nel database.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            view.getLogPanel().log("Errore recupero tabelle: " + e.getMessage());
        }
    }
}