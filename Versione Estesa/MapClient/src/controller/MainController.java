package controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import network.InitTreeWorker;
import network.PredictionWorker;
import network.ServerConnection;
import network.TableLoaderWorker;
import view.MainFrame;

public class MainController {
    private MainFrame view;
    private ServerConnection connection;

    public MainController(MainFrame view) {
        this.view = view;
        this.connection = new ServerConnection();
        initListeners();
    }

    public void initListeners() {
        view.getControlPanel().getLoadTreeButton().addActionListener(e -> handleInitTree());
        view.getControlPanel().getPredictButton().addActionListener(e -> handlePredict());
        view.getControlPanel().getRefreshTablesButton().addActionListener(e -> handleRefreshTables());

        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                connection.close();
            }
        });
    }

    private void handleRefreshTables() {
        String ip = view.getControlPanel().getServerAddress();
        int port;

        try {
            port = view.getControlPanel().getServerPort();
            if (port <= 0 || port > 65535) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Porta non valida.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        view.getControlPanel().getRefreshTablesButton().setEnabled(false);
        view.getLogPanel().log("Richiesta elenco tabelle al server...");

        TableLoaderWorker worker = new TableLoaderWorker(connection, ip, port, view);
        worker.execute();
    }

    private void handleInitTree() {
        String ip = view.getControlPanel().getServerAddress();
        String table = view.getControlPanel().getTableName();
        boolean fromDB = view.getControlPanel().isDatabaseSource();
        int port;

        if (table == null || table.isEmpty()) {
            JOptionPane.showMessageDialog(
                view,
                "Inserire il nome della tabella o del file di archivio!",
                "Parametro Mancante",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            port = view.getControlPanel().getServerPort();
            if (port <= 0 || port > 65535) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                view,
                "La porta deve essere un intero valido compreso tra 1 e 65535.",
                "Errore Formato Porta",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        view.getTreePanel().resetTree();
        view.getControlPanel().getLoadTreeButton().setEnabled(false);
        view.getControlPanel().setPredictionEnabled(false);

        InitTreeWorker worker = new InitTreeWorker(connection, ip, port, table, fromDB, view);
        worker.execute();
    }

    private void handlePredict() {
        if (!connection.isConnected()) {
            JOptionPane.showMessageDialog(
                view,
                "Nessuna connessione attiva. Inizializzare prima l'albero.",
                "Connessione Assente",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        view.getTreePanel().resetTree();
        view.getSummaryPanel().setSteps(0);
        view.getSummaryPanel().setPrediction(null);
        view.getControlPanel().getPredictButton().setEnabled(false);

        PredictionWorker worker = new PredictionWorker(connection, view);
        worker.execute();
    }
}