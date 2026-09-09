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

public class TreePanel extends JPanel {

	private TreeDTO currentTree;

    private static final int NODE_WIDTH = 150;
    private static final int NODE_HEIGHT = 44;
    private static final int VERTICAL_GAP = 70;
    
    public TreePanel() {
        setBackground(Color.WHITE);
    }

    public void resetTree() {
        this.currentTree = null;
        repaint();
    }

    public void updateTree(TreeDTO treeDTO) {
        this.currentTree = treeDTO;
        repaint();
    }
    
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
    
    private void drawTree(Graphics2D g2d, NodeDTO node, int x, int y, int availableWidth) {
        List<NodeDTO> children = node.getChildren();

        if (children.size() == 1) {
            int childX = x;
            int childY = y + NODE_HEIGHT + VERTICAL_GAP;
            g2d.setColor(Color.BLACK);
            g2d.drawLine(x, y + NODE_HEIGHT, childX, childY);
            drawTree(g2d, children.get(0), childX, childY, availableWidth);
        } else if (children.size() >= 2) {
            int offset = availableWidth / 4;

            // Ramo sinistro (opzione 0)
            int leftX = x - offset;
            int leftY = y + NODE_HEIGHT + VERTICAL_GAP;
            g2d.setColor(Color.BLACK);
            g2d.drawLine(x, y + NODE_HEIGHT, leftX, leftY);
            drawTree(g2d, children.get(0), leftX, leftY, availableWidth / 2);

            // Ramo destro (opzione 1)
            int rightX = x + offset;
            int rightY = y + NODE_HEIGHT + VERTICAL_GAP;
            g2d.setColor(Color.BLACK);
            g2d.drawLine(x, y + NODE_HEIGHT, rightX, rightY);
            drawTree(g2d, children.get(1), rightX, rightY, availableWidth / 2);
        }

        drawNodeBox(g2d, node, x - (NODE_WIDTH / 2), y);
    }
    
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
