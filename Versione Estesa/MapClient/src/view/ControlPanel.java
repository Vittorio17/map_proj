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
	/** Campo di testo per l'inserimento dell'indirizzo IP del server. */
	private JTextField ipField;
	/** Campo di testo per l'inserimento della porta del server. */
    private JTextField portField;
    /** Menu a discesa per la selezione dell'origine dei dati */
    private JComboBox<String> sourceComboBox;
    /** Pulsante per avviare l'inizializzazione o il caricamento dell'albero sul server. */
    private JButton loadTreeButton;
    /** Pulsante per avviare una nuova sessione interattiva di predizione. */
    private JButton predictButton;
    /** Menu a discesa editabile per selezionare o digitare il nome della tabella o file. */
    private JComboBox<String> tableComboBox;
    /** Pulsante per richiedere al server l'elenco aggiornato delle tabelle del database. */
    private JButton refreshTablesButton;

    /**
     * Costruttore del pannello di configurazione.
     * Inizializza i componenti grafici, imposta il layout e compone l'interfaccia.
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

        // Menu tabelle con digitazione
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
    	return Integer.parseInt(portField.getText().trim());
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

    /**
     * Restituisce il pulsante per il recupero delle tabelle disponibili.
     *
     * @return pulsante di refresh delle tabelle
     */
    public JButton getRefreshTablesButton() {
        return refreshTablesButton;
    }
    
    /**
     * Aggiorna il menu a tendina delle tabelle con l'elenco specificato.
     *
     * @param tables lista dei nomi delle tabelle da mostrare
     */
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

    /**
     * Verifica se la sorgente selezionata dall'utente è il database relazionale.
     *
     * @return true se l'opzione selezionata è da database, false se da file dump
     */
    public boolean isDatabaseSource() {
        return sourceComboBox.getSelectedIndex() == 0;
    }

    /**
     * Restituisce il pulsante per avviare una predizione.
     *
     * @return pulsante per la predizione
     */
    public JButton getPredictButton() {
        return predictButton;
    }

    /**
     * Modifica lo stato di abilitazione del pulsante di predizione.
     *
     * @param enabled true per abilitare il pulsante, false per disabilitarlo
     */
    public void setPredictionEnabled(boolean enabled) {
        predictButton.setEnabled(enabled);
    }
}