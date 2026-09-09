package network;

import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import view.MainFrame;

public class TableLoaderWorker extends SwingWorker<List<String>, Void> {

    private ServerConnection connection;
    private String ip;
    private int port;
    private MainFrame view;

    public TableLoaderWorker(ServerConnection connection, String ip, int port, MainFrame view) {
        this.connection = connection;
        this.ip = ip;
        this.port = port;
        this.view = view;
    }

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
            e.printStackTrace(); // Mostra l'eccezione esatta nella console del Client
            view.getLogPanel().log("Errore recupero tabelle: " + e.getMessage());
        }
    }
}