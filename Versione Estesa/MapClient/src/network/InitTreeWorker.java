package network;

import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import view.MainFrame;

/**
 * Worker asincrono per l'inizializzazione dell'albero di regressione.
 * Gestisce la comunicazione di rete in background con il server remoto per
 * addestrare un albero da database o caricarne uno preesistente da file,
 * aggiornando l'interfaccia grafica al termine dell'operazione.
 */
public class InitTreeWorker extends SwingWorker<String, Void>{
	/** Oggetto per la gestione della comunicazione via socket con il server. */
	private ServerConnection connection;
	/** Indirizzo IP del server remoto. */
	private String ip;
	/** Porta su cui il server remoto accetta le connessioni. */
	private int port;
	/** Nome della tabella del database o del file da cui caricare l'albero. */
	private String tableName;
	/**
     * Flag indicante la sorgente dei dati: true se l'albero va generato da database,
     * false se va caricato da file di dump (.dmp).
     */
	private boolean fromDB;
	/** Riferimento al frame principale dell'interfaccia grafica per gli aggiornamenti UI. */
	private MainFrame view;
	
	/**
     * Costruttore che inizializza tutti i parametri necessari per la connessione
     * e per il popolamento dell'albero.
     *
     * @param connection istanza della connessione al server
     * @param ip indirizzo IP del server
     * @param port porta del server
     * @param tableName nome della tabella o del file da elaborare
     * @param fromDB true per acquisire dal database, false per caricare da file
     * @param view finestra principale dell'applicazione
     */
	public InitTreeWorker(ServerConnection connection, String ip, int port, String tableName, boolean fromDB, MainFrame view) {
		this.connection = connection;
        this.ip = ip;
        this.port = port;
        this.tableName = tableName;
        this.fromDB = fromDB;
        this.view = view;
	}
	
	/**
     * Esegue le operazioni di rete in un thread in background.
     * Connette il client al server se necessario, invia i comandi appropriati
     * in base alla sorgente scelta e attende le risposte.
     *
     * @return "OK" se l'operazione è riuscita, altrimenti una stringa contenente l'errore
     * @throws Exception se si verifica un errore durante la trasmissione o ricezione dati
     */
	@Override
    protected String doInBackground() throws Exception {
        // Connessione al server
        if (!connection.isConnected()) {
            connection.connect(ip, port);
        }

        if (fromDB) {
            // Comando 0: acquisizione da database
            connection.send(0);
            connection.send(tableName);
            String response = (String) connection.receive();
            if (!"OK".equals(response)) {
                return "Errore acquisizione dati DB: " + response;
            }

            // Comando 1: costruzione e salvataggio albero
            connection.send(1);
            response = (String) connection.receive();
            if (!"OK".equals(response)) {
                return "Errore apprendimento albero: " + response;
            }
            return "OK";

        } else {
            // Comando 2: caricamento da file .dmp
            connection.send(2);
            connection.send(tableName);
            String response = (String) connection.receive();
            if (!"OK".equals(response)) {
                return response;
            }
            return "OK";
        }
    }
	
	/**
     * Eseguito nell'Event Dispatch Thread (EDT) al termine di doInBackground.
     * Riabilita i componenti della vista, gestisce l'esito dell'operazione
     * aggiornando log, pannelli e mostrando dialoghi informativi o di errore.
     */
	@Override
    protected void done() {
        view.getControlPanel().getLoadTreeButton().setEnabled(true);

        try {
            String result = get();
            if ("OK".equals(result)) {
                // Aggiorna la sidebar e il terminale
                view.getSummaryPanel().setTable(tableName);
                view.getLogPanel().log("Albero '" + tableName + "' inizializzato con successo.");
                
                JOptionPane.showMessageDialog(
                    view,
                    "Albero inizializzato con successo sul Server!",
                    "Operazione Completata",
                    JOptionPane.INFORMATION_MESSAGE
                );
                view.getControlPanel().setPredictionEnabled(true);
            } else {
                view.getLogPanel().log("Errore inizializzazione: " + result);
                JOptionPane.showMessageDialog(
                    view,
                    result,
                    "Errore Server",
                    JOptionPane.ERROR_MESSAGE
                );
                view.getControlPanel().setPredictionEnabled(false);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            connection.close();
            view.getLogPanel().log("Errore di rete: " + e.getCause().getMessage());
            JOptionPane.showMessageDialog(
                view,
                "Impossibile comunicare con il Server: " + e.getCause().getMessage(),
                "Errore di Connessione",
                JOptionPane.ERROR_MESSAGE
            );
            view.getControlPanel().setPredictionEnabled(false);
        }
    }
}
