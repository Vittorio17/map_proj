package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class SummaryPanel extends JPanel {

    private final JLabel tableValueLabel;
    private final JLabel stepsValueLabel;
    private final JLabel statusBadgeLabel;
    private final JLabel predictionValueLabel;
    private final DefaultListModel<String> historyModel;
    private final JList<String> historyList;

    public SummaryPanel() {
        setPreferredSize(new Dimension(310, 0));
        setLayout(new BorderLayout(0, 12));
        setBackground(new Color(248, 249, 250));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(220, 224, 230)),
            new EmptyBorder(14, 14, 14, 14)
        ));

        // Contenitore superiore (Sessione + Stima)
        JPanel topContainer = new JPanel();
        topContainer.setOpaque(false);
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));

        JPanel sessionCard = initSessionCard();
        topContainer.add(sessionCard);
        topContainer.add(Box.createVerticalStrut(12));

        JPanel resultCard = initResultCard();
        topContainer.add(resultCard);

        add(topContainer, BorderLayout.NORTH);

        // Cronologia Sessione
        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        add(initHistoryCard(), BorderLayout.CENTER);

        tableValueLabel = (JLabel) sessionCard.getClientProperty("tableValueLabel");
        stepsValueLabel = (JLabel) sessionCard.getClientProperty("stepsValueLabel");
        statusBadgeLabel = (JLabel) sessionCard.getClientProperty("statusBadgeLabel");
        predictionValueLabel = (JLabel) resultCard.getClientProperty("predictionValueLabel");
    }

    // Helper per creare contenitori con stile, raggio d'angolo e intestazione identici
    private JPanel createCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(218, 222, 229), 1, true),
            new EmptyBorder(10, 12, 12, 12)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(new Color(70, 80, 95));
        card.add(titleLabel, BorderLayout.NORTH);

        return card;
    }

    private JPanel initSessionCard() {
        JPanel card = createCard("Sessione Attiva");
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 125));

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 4, 3, 4);

        gbc.gridx = 0; gbc.gridy = 0;
        body.add(new JLabel("Stato:"), gbc);
        JLabel statusLbl = new JLabel("In attesa");
        statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLbl.setForeground(new Color(120, 130, 140));
        gbc.gridx = 1;
        body.add(statusLbl, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        body.add(new JLabel("Tabella:"), gbc);
        JLabel tableLbl = new JLabel("-");
        tableLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 1;
        body.add(tableLbl, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        body.add(new JLabel("Passi:"), gbc);
        JLabel stepsLbl = new JLabel("0");
        stepsLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 1;
        body.add(stepsLbl, gbc);

        card.add(body, BorderLayout.CENTER);

        card.putClientProperty("tableValueLabel", tableLbl);
        card.putClientProperty("stepsValueLabel", stepsLbl);
        card.putClientProperty("statusBadgeLabel", statusLbl);
        return card;
    }

    private JPanel initResultCard() {
        JPanel card = createCard("Stima Valore Continuo");
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 95));

        JLabel predLbl = new JLabel("---");
        predLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        predLbl.setForeground(new Color(40, 140, 60));
        predLbl.setHorizontalAlignment(JLabel.CENTER);

        card.add(predLbl, BorderLayout.CENTER);
        card.putClientProperty("predictionValueLabel", predLbl);
        return card;
    }

    private JPanel initHistoryCard() {
        JPanel card = createCard("Cronologia Sessione");
        
        historyList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(historyList);
        card.add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }

    public void addHistoryEntry(String table, List<String> path, Double prediction) {
        int index = historyModel.getSize() + 1;
        StringBuilder sb = new StringBuilder();
        sb.append("<html><div style='margin-bottom:2px;'><b>#").append(index).append(" [").append(table != null ? table : "Table").append("]</b></div>");
        sb.append("<div style='color:#555555; margin-bottom:4px; font-size:10px;'>Percorso: ");
        if (path == null || path.isEmpty()) {
            sb.append("Radice diretta");
        } else {
            sb.append(String.join(" &rarr; ", path));
        }
        sb.append("</div>");
        // Badge visivo per l'esito
        sb.append("<span style='background-color:#e8f5e9; border:1px solid #c8e6c9; color:#1e7e34; font-weight:bold; font-size:10px;'>&nbsp;Stima: ")
          .append(String.format("%.4f", prediction))
          .append("&nbsp;</span></html>");

        historyModel.add(0, sb.toString());
    }

    public void setStatus(String text, boolean connected) {
        statusBadgeLabel.setText(text);
        statusBadgeLabel.setForeground(connected ? new Color(30, 140, 50) : new Color(200, 50, 50));
    }

    public void setTable(String tableName) {
        tableValueLabel.setText(tableName != null ? tableName : "-");
    }

    public void setSteps(int steps) {
        stepsValueLabel.setText(String.valueOf(steps));
    }

    public void setPrediction(Double value) {
        predictionValueLabel.setText(value != null ? String.format("%.4f", value) : "---");
    }

    public void reset() {
        stepsValueLabel.setText("0");
        predictionValueLabel.setText("---");
    }
}