package view;

import java.awt.BorderLayout;
import javax.swing.JFrame;

public class MainFrame extends JFrame {
    private final ControlPanel controlPanel;
    private final TreePanel treePanel;
    private final SummaryPanel summaryPanel;
    private final LogPanel logPanel;

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

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public TreePanel getTreePanel() {
        return treePanel;
    }

    public SummaryPanel getSummaryPanel() {
        return summaryPanel;
    }

    public LogPanel getLogPanel() {
        return logPanel;
    }
}