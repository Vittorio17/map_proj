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

/**
 * Pannello laterale per la visualizzazione del riepilogo della sessione corrente.
 * Mostra informazioni sullo stato della connessione, la tabella caricata,
 * il conteggio dei passi decisionali, il valore stimato della predizione
 * e la cronologia delle predizioni effettuate.
 */
public class SummaryPanel extends JPanel {
	/** Etichetta che visualizza il nome della tabella o del dump attualmente caricato. */
    private JLabel tableValueLabel;
    /** Etichetta che visualizza il numero di passi o interrogazioni eseguite nella sessione corrente. */
    private JLabel stepsValueLabel;
    /** Etichetta con badge visivo per lo stato della connessione. */
    private JLabel statusBadgeLabel;
    /** Etichetta che mostra il valore continuo predetto dall'albero decisionale. */
    private JLabel predictionValueLabel;
    /** Modello dati associato alla lista della cronologia delle predizioni. */
    private DefaultListModel<String> historyModel;
    /** Lista grafica per visualizzare le sessioni di predizione precedenti. */
    private JList<String> historyList;

    /**
     * Costruttore del pannello di riepilogo.
     * Inizializza i componenti grafici, configura i bordi, organizza
     * i riquadri per la sessione, il risultato e la cronologia, collegando
     * i riferimenti alle etichette di stato.
     */
    public SummaryPanel() {
        setPreferredSize(new Dimension(310, 0));
        setLayout(new BorderLayout(0, 12));
        setBackground(new Color(248, 249, 250));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(220, 224, 230)),
            new EmptyBorder(14, 14, 14, 14)
        ));

        // Contenitore superiore
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

    /**
     * Crea un riquadro grafico con stile standardizzato, bordo arrotondato e titolo di intestazione.
     *
     * @param title testo dell'intestazione del riquadro
     * @return istanza di JPanel stilizzata
     */
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

    /**
     * Inizializza il riquadro informativo relativo alla sessione attiva.
     * Crea le etichette per lo stato, il nome della tabella e il conteggio dei passi.
     *
     * @return il pannello contenente i dati della sessione
     */
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

    /**
     * Inizializza il riquadro per la visualizzazione del valore numerico predetto.
     *
     * @return il pannello dedicato al risultato della stima
     */
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

    /**
     * Inizializza il riquadro contenente l'elenco scorrevole della cronologia predizioni.
     *
     * @return il pannello contenente la lista delle sessioni passate
     */
    private JPanel initHistoryCard() {
        JPanel card = createCard("Cronologia Sessione");
        
        historyList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(historyList);
        card.add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }

    /**
     * Aggiunge una nuova voce in cima alla lista della cronologia delle predizioni.
     *
     * @param table nome della tabella su cui è stata eseguita la predizione
     * @param path lista ordinata delle condizioni decisionali selezionate
     * @param prediction valore numerico continuo calcolato
     */
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

    /**
     * Aggiorna il testo e il colore dell'indicatore di stato della sessione.
     *
     * @param text descrizione testuale dello stato
     * @param connected true se il client risulta connesso, false altrimenti
     */
    public void setStatus(String text, boolean connected) {
        statusBadgeLabel.setText(text);
        statusBadgeLabel.setForeground(connected ? new Color(30, 140, 50) : new Color(200, 50, 50));
    }

    /**
     * Aggiorna l'etichetta indicante il nome della tabella o dump corrente.
     *
     * @param tableName nome della tabella da mostrare
     */
    public void setTable(String tableName) {
        tableValueLabel.setText(tableName != null ? tableName : "-");
    }

    /**
     * Imposta il numero progressivo di passi decisionali effettuati.
     *
     * @param steps conteggio dei passi decisionali
     */
    public void setSteps(int steps) {
        stepsValueLabel.setText(String.valueOf(steps));
    }

    /**
     * Imposta il valore predetto da mostrare nel riquadro della stima continua.
     *
     * @param value valore numerico stimato, o null per ripristinare il valore iniziale
     */
    public void setPrediction(Double value) {
        predictionValueLabel.setText(value != null ? String.format("%.4f", value) : "---");
    }

    /**
     * Ripristina i valori predefiniti per il contatore dei passi e per l'etichetta di stima.
     */
    public void reset() {
        stepsValueLabel.setText("0");
        predictionValueLabel.setText("---");
    }
}