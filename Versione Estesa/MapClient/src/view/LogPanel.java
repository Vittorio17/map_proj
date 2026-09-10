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

public class LogPanel extends JPanel {

    private JTextPane textPane;
    private StyledDocument doc;

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

    private void createStyle(String name, Color color) {
        Style style = textPane.addStyle(name, null);
        StyleConstants.setForeground(style, color);
    }

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

    public void clear() {
        textPane.setText("");
    }
}