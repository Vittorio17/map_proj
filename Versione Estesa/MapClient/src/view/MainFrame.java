package view;

import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 * Finestra principale dell'applicazione per la gestione e visualizzazione
 * dell'albero di regressione.
 * Coordina la disposizione grafica integrando i quattro pannelli di controllo,
 * visualizzazione dell'albero, riepilogo e log.
 */
public class MainFrame extends JFrame {
	/** Pannello superiore per la configurazione dei parametri di rete e dei comandi. */
    private ControlPanel controlPanel;
    /** Pannello centrale per la renderizzazione grafica dell'albero decisionale. */ 
    private TreePanel treePanel;
    /** Pannello laterale destro dedicato al riepilogo delle metriche e allo storico. */
    private SummaryPanel summaryPanel;
    /** Pannello inferiore con funzione di console per i messaggi di log. */
    private LogPanel logPanel;

    /**
     * Costruttore della finestra principale.
     * Imposta il titolo, il layout a zone BorderLayout, inizializza i quattro sottomoduli
     * della vista posizionandoli nei rispettivi settori e massimizza la finestra a schermo intero.
    */
    public MainFrame() {
        super("Regression Tree Dashboard");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Inizializzazione dei quattro pannelli
        controlPanel = new ControlPanel();
        treePanel = new TreePanel();
        summaryPanel = new SummaryPanel();
        logPanel = new LogPanel();

        // Composizione nel layout principale
        add(controlPanel, BorderLayout.NORTH);
        add(treePanel, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.EAST);
        add(logPanel, BorderLayout.SOUTH);

        // Occupa tutto lo schermo disponibile
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    /**
     * Restituisce il pannello dei controlli dell'applicazione.
     *
     * @return istanza di ControlPanel presente nella zona superiore
     */
    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    /**
     * Restituisce il pannello adibito al disegno dell'albero.
     *
     * @return istanza di TreePanel presente nella zona centrale
     */
    public TreePanel getTreePanel() {
        return treePanel;
    }

    /**
     * Restituisce il pannello di riepilogo e storico delle predizioni.
     *
     * @return istanza di SummaryPanel presente nella zona orientale
     */
    public SummaryPanel getSummaryPanel() {
        return summaryPanel;
    }

    /**
     * Restituisce il pannello che funge da console per i messaggi di sistema.
     *
     * @return istanza di LogPanel presente nella zona inferiore
     */
    public LogPanel getLogPanel() {
        return logPanel;
    }
}