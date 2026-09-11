package network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import model.NodeDTO;
import model.TreeDTO;
import view.MainFrame;

/**
 * Worker asincrono responsabile del processo di predizione tramite albero di regressione.
 * Gestisce l'interazione client-server: riceve i nodi di decisione, richiede
 * l'input all'utente per la scelta dei rami, aggiorna progressivamente la vista dell'albero
 * e infine restituisce il valore continuo predetto.
 */
public class PredictionWorker extends SwingWorker<Double, NodeDTO> {
	/** Oggetto per la gestione della comunicazione di rete via socket con il server. */
    private ServerConnection connection;
    /** Riferimento al frame principale dell'interfaccia grafica per aggiornare viste e dialoghi. */
    private MainFrame view;
    /** Nodo radice del percorso di decisione tracciato e visualizzato nell'interfaccia grafica. */
    private NodeDTO rootNode;
    /** Nodo dell'albero attualmente attivo sul quale innestare i successivi rami o la foglia. */
    private NodeDTO currentNode;
    /** Lista contenente le etichette delle decisioni/condizioni scelte dall'utente lungo il cammino. */
    private List<String> visitedConditions = new ArrayList<>();
    
    /**
     * Costruttore che inizializza la connessione di rete e il riferimento alla vista principale.
     *
     * @param connection connessione attiva con il server
     * @param view finestra principale dell'applicazione
     */
    public PredictionWorker(ServerConnection connection, MainFrame view) {
        this.connection = connection;
        this.view = view;
    }

    /**
     * Esegue la sessione di predizione in background.
     *
     * @return valore continuo predetto dalla foglia dell'albero
     * @throws Exception in caso di interruzione, errore di comunicazione o annullamento da parte dell'utente
     */
    @Override
    protected Double doInBackground() throws Exception {
        connection.send(3);
        int steps = 0;

        SwingUtilities.invokeLater(() -> {
            view.getSummaryPanel().setSteps(0);
            view.getLogPanel().log("Avvio nuova sessione di predizione.");
        });

        while (true) {
            String status = (String) connection.receive();

            if ("OK".equals(status)) {
                return handleOkResponse();
            }

            if ("QUERY".equals(status)) {
                steps++;
                handleQueryResponse(steps);
            }
        }
    }

    /**
     * Gestisce la risposta terminale OK proveniente dal server. Riceve il valore
     * stimato, aggiunge il nodo foglia all'albero visivo, salva il cammino nello
     * storico e aggiorna i pannelli dell'interfaccia utente.
     *
     * @return valore numerico predetto dal server
     * @throws Exception se si verificano errori nella ricezione o nella sincronizzazione con l'EDT
     */
    private Double handleOkResponse() throws Exception {
        Double predictedValue = (Double) connection.receive();

        SwingUtilities.invokeAndWait(() -> {
            NodeDTO leafNode = new NodeDTO(predictedValue);
            if (currentNode != null) {
                currentNode.addChild(leafNode);
            } else {
                rootNode = leafNode;
            }

            String currentTable = view.getControlPanel().getTableName();
            view.getTreePanel().updateTree(new TreeDTO(rootNode, currentTable));

            view.getSummaryPanel().setPrediction(predictedValue);

            view.getSummaryPanel().addHistoryEntry(currentTable, visitedConditions, predictedValue);

            view.getLogPanel().log("Predizione completata. Valore stimato: " + predictedValue);
        });

        return predictedValue;
    }
    
    /**
     * Gestisce la fase di interrogazione del server. Riceve il testo del quesito,
     * chiede la decisione dell'utente, trasmette l'indice scelto al server e aggiorna
     * la struttura dell'albero grafico evidenziando il ramo selezionato.
     *
     * @param step numero del passo decisionale corrente
     * @throws Exception se l'utente annulla la scelta o si verificano problemi di trasmissione
    */
    private void handleQueryResponse(int step) throws Exception {
        String queryText = (String) connection.receive();

        int choice = promptUserForChoice(queryText);
        if (choice < 0) {
            throw new IllegalArgumentException("Navigazione annullata dall'utente.");
        }

        SwingUtilities.invokeLater(() -> {
            view.getSummaryPanel().setSteps(step);
            view.getLogPanel().log("Scelta effettuata per nodo: " + choice);
        });

        connection.send(choice);

        List<String> options = parseBranchOptions(queryText);

        if (choice >= 0 && choice < options.size()) {
            visitedConditions.add(options.get(choice));
        }

        SwingUtilities.invokeAndWait(() -> {
            if (rootNode == null) {
                rootNode = new NodeDTO("Decision Path");
                currentNode = rootNode;
            }

            NodeDTO selectedBranch = null;
            for (int i = 0; i < options.size(); i++) {
                NodeDTO branch = new NodeDTO(options.get(i));
                boolean isChosen = (i == choice);
                branch.setSelected(isChosen);
                currentNode.addChild(branch);

                if (isChosen) {
                    selectedBranch = branch;
                }
            }

            if (selectedBranch != null) {
                currentNode = selectedBranch;
            }

            String currentTable = view.getControlPanel().getTableName();
            view.getTreePanel().updateTree(new TreeDTO(rootNode, currentTable));
        });
    }
    
    /**
     * Elabora la stringa di testo inviata dal server isolando le singole
     * opzioni di scelta per i rami decisionali, rimuovendo prefissi numerici se presenti.
     *
     * @param queryText testo grezzo contenente le opzioni inviate dal server
     * @return lista delle etichette descrittive per ciascun ramo
    */
    private List<String> parseBranchOptions(String queryText) {
        List<String> options = new ArrayList<>();
        String[] lines = queryText.split("\n");
        for (String line : lines) {
            String opt = line.trim();
            if (!opt.isEmpty()) {
                if (opt.matches("^[0-9]+:.*")) {
                    opt = opt.substring(opt.indexOf(":") + 1).trim();
                }
                options.add(opt);
            }
        }
        return options;
    }
    
    /**
     * Sincronizza il thread di background con l'EDT per richiedere la scelta all'utente
     * bloccando l'esecuzione finché il dialogo non viene chiuso.
     *
     * @param queryText testo descrittivo da cui estrarre i rami presentati all'utente
     * @return indice dell'opzione selezionata dall'utente, oppure un valore negativo se annullata
     * @throws Exception se la sincronizzazione con l'Event Dispatch Thread fallisce
    */
    private int promptUserForChoice(String queryText) throws Exception {
        UserQueryDialog dialogRunnable = new UserQueryDialog(queryText);
        SwingUtilities.invokeAndWait(dialogRunnable);
        return dialogRunnable.getChoice();
    }

    /**
     * Inner Class per visualizzare la finestra di dialogo e catturare la decisione
     * presa dall'utente tra le alternative possibili.
    */
    private class UserQueryDialog implements Runnable {
    	/** Testo contenente le opzioni da mostrare nella finestra di dialogo. */
        private String queryText;
        /** Indice dell'opzione selezionata dall'utente. Assume valore -1 se annullata o non valida. */
        private int choice = -1;

        /**
         * Crea un nuovo dialogo per la selezione dell'opzione.
         *
         * @param queryText testo grezzo con le alternative fornite dal server
        */
        public UserQueryDialog(String queryText) {
            this.queryText = queryText;
        }

        /**
         * Mostra la finestra di dialogo e memorizza la scelta effettuata.
         */
        @Override
        public void run() {
            List<String> validOptions = parseBranchOptions(queryText);

            if (!validOptions.isEmpty()) {
                String[] buttonOptions = validOptions.toArray(new String[0]);
                this.choice = JOptionPane.showOptionDialog(
                    view,
                    "Seleziona il ramo decisionale:",
                    "Scelta Percorso",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    buttonOptions,
                    buttonOptions[0]
                );
            } else {
                this.choice = -1;
            }
        }

        /**
         * Restituisce l'indice della scelta selezionata dall'utente.
         *
         * @return indice numerico del pulsante premuto, oppure un valore negativo se chiuso/annullato
        */
        public int getChoice() {
            return choice;
        }
    }
    

    /**
     * Elabora nodi intermedi ottenuti durante l'esecuzione del worker,
     * aggiornando il grafo dell'albero sul pannello grafico.
     *
     * @param chunks lista di nodi da aggregare alla rappresentazione visiva dell'albero
     */
    @Override
    protected void process(List<NodeDTO> chunks) {
        if (rootNode == null) {
            rootNode = new NodeDTO("Decision Path");
            currentNode = rootNode;
        }

        NodeDTO nextSelectedNode = null;

        for (NodeDTO node : chunks) {
            if (node.isLeaf()) {
                // Collega la foglia direttamente all'ultimo nodo selezionato
                currentNode.addChild(node);
            } else {
                // Collega i due rami di split al nodo genitore corrente
                currentNode.addChild(node);
                if (node.isSelected()) {
                    nextSelectedNode = node;
                }
            }
        }

        if (nextSelectedNode != null) {
            currentNode = nextSelectedNode;
        }

        String currentTable = view.getControlPanel().getTableName();
        view.getTreePanel().updateTree(new TreeDTO(rootNode, currentTable));
    }

    /**
     * Eseguito nell'Event Dispatch Thread al termine del processo di calcolo.
     * Riattiva i componenti di controllo dell'interfaccia e mostra all'utente un messaggio
     * contenente il valore finale stimato o un avviso in caso di errore/annullamento.
     */
    @Override
    protected void done() {
        view.getControlPanel().getPredictButton().setEnabled(true);

        try {
            Double finalValue = get();
            JOptionPane.showMessageDialog(
                view,
                "Predizione completata con successo!\nValore continuo stimato: " + finalValue,
                "Esito Predizione",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            JOptionPane.showMessageDialog(
                view,
                "Errore durante la predizione: " + e.getCause().getMessage(),
                "Errore Predizione",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}