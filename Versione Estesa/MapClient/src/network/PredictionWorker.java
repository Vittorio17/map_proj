package network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import model.NodeDTO;
import model.TreeDTO;
import view.MainFrame;

public class PredictionWorker extends SwingWorker<Double, NodeDTO> {
    private ServerConnection connection;
    private MainFrame view;
    private NodeDTO rootNode;
    private NodeDTO currentNode;

    public PredictionWorker(ServerConnection connection, MainFrame view) {
        this.connection = connection;
        this.view = view;
    }

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

    private Double handleOkResponse() throws Exception {
        Double predictedValue = (Double) connection.receive();

        // Creazione e aggancio atomico della foglia al ramo attivo
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
            view.getLogPanel().log("Predizione completata. Valore stimato: " + predictedValue);
        });

        return predictedValue;
    }

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
     * Sincronizza il thread di background con l'EDT per richiedere la scelta all'utente.
     */
    private int promptUserForChoice(String queryText) throws Exception {
        UserQueryDialog dialogRunnable = new UserQueryDialog(queryText);
        SwingUtilities.invokeAndWait(dialogRunnable);
        return dialogRunnable.getChoice();
    }

    private class UserQueryDialog implements Runnable {
        private String queryText;
        private int choice = -1;

        public UserQueryDialog(String queryText) {
            this.queryText = queryText;
        }

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

        public int getChoice() {
            return choice;
        }
    }
    

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