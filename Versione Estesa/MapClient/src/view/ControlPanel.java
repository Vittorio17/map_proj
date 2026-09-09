package view;

import java.awt.FlowLayout;
import java.util.List;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;

/**
 * Pannello per la configurazione della connessione al server
 * e per l'inserimento del nome della tabella.
 */
public class ControlPanel extends JPanel {

	private JTextField ipField;
    private JTextField portField;
    private JComboBox<String> sourceComboBox;
    private JButton loadTreeButton;
    private JButton predictButton;
    private JComboBox<String> tableComboBox;
    private JButton refreshTablesButton;

    /**
     * Costruttore del pannello di configurazione.
     * Inizializza i campi di testo e il pulsante.
     */
    public ControlPanel() {
        super(new FlowLayout(FlowLayout.LEFT, 10, 8));
        setBorder(BorderFactory.createTitledBorder("Configurazione Connessione & Operazioni"));

        // Campi di configurazione socket
        ipField = new JTextField("127.0.0.1", 9);
        portField = new JTextField("8080", 4);

        // Selezione origine dati
        String[] options = {"Da Database", "Da Archivio (.dmp)"};
        sourceComboBox = new JComboBox<>(options);

        // Menu tabelle con digitazione e pulsante refresh
        tableComboBox = new JComboBox<>(new String[]{"provac"});
        tableComboBox.setEditable(true);
        tableComboBox.setPreferredSize(new Dimension(130, 26));

        refreshTablesButton = new JButton("Carica Tabelle");
        refreshTablesButton.setToolTipText("Interroga il database per ottenere le tabelle disponibili");

        // Pulsanti d'azione
        loadTreeButton = new JButton("Inizializza Albero");
        predictButton = new JButton("Nuova Predizione");
        predictButton.setEnabled(false);

        // Composizione nel pannello
        add(new JLabel("IP:"));
        add(ipField);

        add(new JLabel("Porta:"));
        add(portField);

        add(new JLabel("Origine:"));
        add(sourceComboBox);

        add(new JLabel("Tabella:"));
        add(tableComboBox);
        add(refreshTablesButton);

        add(loadTreeButton);
        add(predictButton);
    }

    /**
     * Restituisce l'indirizzo IP inserito nel campo.
     *
     * @return indirizzo IP del server
     */
    public String getServerAddress() {
        return ipField.getText();
    }

    /**
     * Restituisce la porta inserita nel campo.
     *
     * @return porta del server come numero intero
     * @throws NumberFormatException se il valore inserito non è un numero
     */
    public int getServerPort() {
        return Integer.parseInt(portField.getText());
    }

    /**
     * Restituisce il nome della tabella inserito nel campo.
     *
     * @return nome della tabella
     */
    public String getTableName() {
        Object selected = tableComboBox.getSelectedItem();
        return (selected != null) ? selected.toString().trim() : "";
    }

    public JButton getRefreshTablesButton() {
        return refreshTablesButton;
    }
    
    public void updateTableList(List<String> tables) {
        tableComboBox.removeAllItems();
        for (String t : tables) {
            tableComboBox.addItem(t);
        }
        if (!tables.isEmpty()) {
            tableComboBox.setSelectedIndex(0);
        }
    }
    
    /**
     * Restituisce il pulsante per caricare l'albero.
     *
     * @return pulsante per il caricamento dell'albero
     */
    public JButton getLoadTreeButton() {
        return loadTreeButton;
    }

    public boolean isDatabaseSource() {
        return sourceComboBox.getSelectedIndex() == 0;
    }

    public JButton getPredictButton() {
        return predictButton;
    }

    public void setPredictionEnabled(boolean enabled) {
        predictButton.setEnabled(enabled);
    }
}