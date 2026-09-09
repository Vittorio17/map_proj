package controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import model.NodeDTO;
import model.TreeDTO;
import network.InitTreeWorker;
import network.PredictionWorker;
import network.ServerConnection;
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
		// Evento 1: Inizializzazione albero
        view.getControlPanel().getLoadTreeButton().addActionListener(e -> handleInitTree());

        // Evento 2: Avvio sessione interattiva di predizione
        view.getControlPanel().getPredictButton().addActionListener(e -> handlePredict());
	
        // Chiusura sicura del socket alla chiusura della finestra
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                connection.close();
            }
        });
	}
	
	private void handleInitTree() {
        String ip = view.getControlPanel().getServerAddress();
        String table = view.getControlPanel().getTableName();
        boolean fromDB = view.getControlPanel().isDatabaseSource();
        int port;

        // Validazione tabella
        if (table == null || table.isEmpty()) {
            JOptionPane.showMessageDialog(
                view,
                "Inserire il nome della tabella o del file di archivio!",
                "Parametro Mancante",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Validazione porta
        try {
            port = view.getControlPanel().getServerPort();
            if (port <= 0 || port > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                view,
                "La porta deve essere un intero valido compreso tra 1 e 65535.",
                "Errore Formato Porta",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Reset visivo e disabilitazione comandi durante il caricamento
        view.getTreePanel().resetTree();
        view.getControlPanel().getLoadTreeButton().setEnabled(false);
        view.getControlPanel().setPredictionEnabled(false);

        // Avvio del thread in background
        InitTreeWorker worker = new InitTreeWorker(
            connection,
            ip,
            port,
            table,
            fromDB,
            view
        );
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
		// Reset vista, sidebar e disabilitazione bottone
        view.getTreePanel().resetTree();
        view.getSummaryPanel().setSteps(0);
        view.getSummaryPanel().setPrediction(null);
        view.getControlPanel().getPredictButton().setEnabled(false);

        // Avvio del ciclo interattivo
        PredictionWorker worker = new PredictionWorker(connection, view);
        worker.execute();
    }
}
