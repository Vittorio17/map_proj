package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.JPanel;
import model.NodeDTO;
import model.TreeDTO;

/**
 * Pannello grafico dedicato al rendering visuale dell'albero.
 * Gestisce il disegno ricorsivo dei nodi (intermedi e foglie) e dei collegamenti,
 * calcolando dinamicamente la disposizione nello spazio disponibile.
 */
public class TreePanel extends JPanel {
	/** Dati dell'albero corrente da rappresentare a video. */
	private TreeDTO currentTree;
	/** Larghezza fissa in pixel del rettangolo che rappresenta ciascun nodo. */
    private static final int NODE_WIDTH = 180;
    /** Altezza fissa in pixel del rettangolo che rappresenta ciascun nodo. */
    private static final int NODE_HEIGHT = 54;
    /** Distanza verticale in pixel tra i livelli dell'albero. */
    private static final int VERTICAL_GAP = 75;
    
    /**
     * Costruttore del pannello di visualizzazione dell'albero.
     * Imposta lo sfondo bianco per l'area di disegno.
     */
    public TreePanel() {
        setBackground(Color.WHITE);
    }

    /**
     * Reimposta lo stato dell'albero a null e forza il ridisegno del pannello,
     * mostrando il messaggio di attesa.
     */
    public void resetTree() {
        this.currentTree = null;
        repaint();
    }

    /**
     * Aggiorna l'albero da visualizzare e richiede il ridisegno dei componenti.
     *
     * @param treeDTO oggetto di trasferimento dati contenente la radice dell'albero
     */
    public void updateTree(TreeDTO treeDTO) {
        this.currentTree = treeDTO;
        repaint();
    }
    
    /**
     * Esegue il rendering personalizzato dell'albero sul pannello.
     * Se l'albero non è presente, disegna un testo segnaposto al centro;
     * altrimenti avvia il disegno ricorsivo partendo dalla radice.
     *
     * @param g contesto grafico per le operazioni di disegno
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (currentTree == null || currentTree.getRoot() == null) {
            g.setColor(new Color(150, 150, 150));
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            String placeholder = "Nessun percorso da mostrare. Avviare una predizione.";
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(placeholder)) / 2;
            int y = getHeight() / 2;
            g.drawString(placeholder, x, y);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int startX = getWidth() / 2;
        int startY = 40;
        drawTree(g2d, currentTree.getRoot(), startX, startY, getWidth());
    }
    
    /**
     * Disegna ricorsivamente un nodo, le linee di collegamento e i suoi sottoalberi figli.
     *
     * @param g2d contesto grafico 2D
     * @param node nodo corrente da disegnare
     * @param x coordinata orizzontale del centro del nodo
     * @param y coordinata verticale superiore del nodo
     * @param availableWidth larghezza orizzontale disponibile per il sottoalbero corrente
     */
    private void drawTree(Graphics2D g2d, NodeDTO node, int x, int y, int availableWidth) {
    	List<NodeDTO> children = node.getChildren();
        int numChildren = children.size();

        if (numChildren == 1) {
            int childX = x;
            int childY = y + NODE_HEIGHT + VERTICAL_GAP;
            g2d.setColor(Color.BLACK);
            g2d.drawLine(x, y + NODE_HEIGHT, childX, childY);
            drawTree(g2d, children.get(0), childX, childY, availableWidth);
        } else if (numChildren > 1) {
            int slotWidth = availableWidth / numChildren;
            int startSlotX = x - (availableWidth / 2);
            int childY = y + NODE_HEIGHT + VERTICAL_GAP;

            for (int i = 0; i < numChildren; i++) {
                int childX = startSlotX + (i * slotWidth) + (slotWidth / 2);

                g2d.setColor(Color.BLACK);
                g2d.drawLine(x, y + NODE_HEIGHT, childX, childY);
                drawTree(g2d, children.get(i), childX, childY, slotWidth);
            }
        }

        drawNodeBox(g2d, node, x - (NODE_WIDTH / 2), y);
    }
    
    /**
     * Disegna la casella grafica per il nodo specificato, applicando colori,
     * bordi e stili tipografici differenti a seconda che si tratti di una foglia o di un nodo intermedio.
     *
     * @param g2d contesto grafico 2D
     * @param node nodo da rappresentare nel box
     * @param x coordinata orizzontale dell'angolo superiore sinistro del box
     * @param y coordinata verticale dell'angolo superiore sinistro del box
     */
    private void drawNodeBox(Graphics2D g2d, NodeDTO node, int x, int y) {
        if (node.isLeaf()) {
            // Nodo Foglia
            g2d.setColor(new Color(235, 247, 238));
            g2d.fillRoundRect(x, y, NODE_WIDTH, NODE_HEIGHT, 16, 16);
            g2d.setColor(new Color(46, 125, 50));
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRoundRect(x, y, NODE_WIDTH, NODE_HEIGHT, 16, 16);

            g2d.setColor(new Color(27, 94, 32));
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            String text = "Pred: " + String.format("%.4f", node.getPredictedValue());
            centerText(g2d, text, x, y, NODE_WIDTH, NODE_HEIGHT);
        } else {
            // Nodo Intermedio
            g2d.setColor(new Color(245, 247, 250));
            g2d.fillRoundRect(x, y, NODE_WIDTH, NODE_HEIGHT, 12, 12);
            g2d.setColor(new Color(66, 133, 244));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(x, y, NODE_WIDTH, NODE_HEIGHT, 12, 12);

            g2d.setColor(new Color(30, 30, 30));
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
            centerText(g2d, node.getSplitCondition(), x, y, NODE_WIDTH, NODE_HEIGHT);
        }
    }
    
    /**
     * Centra orizzontalmente e verticalmente una stringa di testo all'interno di un'area rettangolare.
     *
     * @param g2d contesto grafico 2D
     * @param text stringa di testo da disegnare
     * @param x coordinata orizzontale dell'area
     * @param y coordinata verticale dell'area
     * @param width larghezza del rettangolo di riferimento
     * @param height altezza del rettangolo di riferimento
     */
    private void centerText(Graphics2D g2d, String text, int x, int y, int width, int height) {
        if (text == null) return;
        FontMetrics fm = g2d.getFontMetrics();
        
        // Troncamento se il testo supera i limiti del box
        String printableText = text;
        if (fm.stringWidth(printableText) > width - 10) {
            while (fm.stringWidth(printableText + "...") > width - 10 && printableText.length() > 3) {
                printableText = printableText.substring(0, printableText.length() - 1);
            }
            printableText += "...";
        }

        int textX = x + (width - fm.stringWidth(printableText)) / 2;
        int textY = y + ((height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(printableText, textX, textY);
    }
    
}
