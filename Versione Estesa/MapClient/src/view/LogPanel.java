package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Pannello grafico che funge da console di log dell'applicazione.
 * Mostra messaggi di stato, avvisi ed errori colorati in base al contenuto,
 */
public class LogPanel extends JPanel {
	/** Area di testo formattata non modificabile per la visualizzazione dei log. */
    private JTextPane textPane;
    /** Modello del documento associato all'area di testo per la gestione degli stili. */
    private StyledDocument doc;

    /**
     * Costruttore del pannello di log.
     * Inizializza il componente testuale, definisce la palette di colori per i diversi
     * livelli di severità e inserisce la console all'interno di un pannello a scorrimento.
     */
    public LogPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 130));

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(new Color(24, 25, 27));
        textPane.setFont(new Font("Consolas", Font.PLAIN, 12));
        doc = textPane.getStyledDocument();

        createStyle("ERROR", new Color(255, 85, 85));
        createStyle("SUCCESS", new Color(80, 250, 123));
        createStyle("WARN", new Color(255, 184, 108));
        createStyle("INFO", new Color(248, 248, 242));

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Console Log"));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Registra un nuovo stile di formattazione del testo impostando il colore di primo piano.
     *
     * @param name identificatore univoco dello stile
     * @param color colore del testo associato allo stile
     */
    private void createStyle(String name, Color color) {
        Style style = textPane.addStyle(name, null);
        StyleConstants.setForeground(style, color);
    }

    /**
     * Inserisce una nuova riga di messaggio nella console.
     * Deduce automaticamente il livello di log (ERROR, SUCCESS, WARN, INFO) analizzando
     * le parole chiave nel testo e aggiorna il cursore a fine documento.
     *
     * @param message stringa da registrare nel terminale visivo
     */
    public void log(String message) {
        String level = "INFO";
        String lower = message.toLowerCase();

        if (lower.contains("errore") || lower.contains("refused") || lower.contains("fail") || lower.contains("eccezione")) {
            level = "ERROR";
        } else if (lower.contains("successo") || lower.contains("completat") || lower.contains("avvio")) {
            level = "SUCCESS";
        } else if (lower.contains("annullata") || lower.contains("warning") || lower.contains("attesa")) {
            level = "WARN";
        }

        try {
            doc.insertString(doc.getLength(), "> " + message + "\n", textPane.getStyle(level));
            textPane.setCaretPosition(doc.getLength());
        } catch (Exception ignored) {}
    }

    /**
     * Pulisce l'intera area di testo eliminando tutti i messaggi registrati.
     */
    public void clear() {
        textPane.setText("");
    }
}