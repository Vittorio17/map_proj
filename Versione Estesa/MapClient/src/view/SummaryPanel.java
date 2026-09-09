package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SummaryPanel extends JPanel{
	private JLabel tableValueLabel;
	private JLabel stepsValueLabel;
	private JLabel predictionValueLabel;
	
	public SummaryPanel() {
		super(new BorderLayout(0, 15));
        setPreferredSize(new Dimension(240, 0));
        setBorder(BorderFactory.createTitledBorder("Riepilogo Sessione"));

        tableValueLabel = new JLabel("-", SwingConstants.LEFT);
        stepsValueLabel = new JLabel("0", SwingConstants.LEFT);
        predictionValueLabel = new JLabel("N/D", SwingConstants.CENTER);

        tableValueLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        stepsValueLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        predictionValueLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        add(initMetaPanel(), BorderLayout.NORTH);
        add(initResultCard(), BorderLayout.CENTER);
	}
	
	private JPanel initMetaPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 2, 2));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panel.add(new JLabel("Tabella:"));
        panel.add(tableValueLabel);
        panel.add(new JLabel("Passi Eseguiti:"));
        panel.add(stepsValueLabel);

        return panel;
    }
	
	private JPanel initResultCard() {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBorder(BorderFactory.createTitledBorder("Stima Finale"));

        card.add(predictionValueLabel, BorderLayout.CENTER);

        return card;
    }
	
	public void setTable(String tableName) {
        tableValueLabel.setText((tableName == null || tableName.isEmpty()) ? "-" : tableName);
    }

    public void setSteps(int steps) {
        stepsValueLabel.setText(String.valueOf(steps));
    }

    public void setPrediction(Double value) {
        if (value == null) {
            predictionValueLabel.setText("N/D");
        } else {
            predictionValueLabel.setText(String.format("%.4f", value));
        }
    }

    public void reset() {
        tableValueLabel.setText("-");
        stepsValueLabel.setText("0");
        predictionValueLabel.setText("N/D");
    }
}

