package network;

import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import view.MainFrame;

public class InitTreeWorker extends SwingWorker<String, Void>{
	private ServerConnection connection;
	private String ip;
	private int port;
	private String tableName;
	private boolean fromDB;
	private MainFrame view;
	
	public InitTreeWorker(ServerConnection connection, String ip, int port, String tableName, boolean fromDB, MainFrame view) {
		this.connection = connection;
        this.ip = ip;
        this.port = port;
        this.tableName = tableName;
        this.fromDB = fromDB;
        this.view = view;
	}
	
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
