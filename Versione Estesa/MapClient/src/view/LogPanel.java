package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class LogPanel extends JPanel{
	private JTextArea logArea;
	private JScrollPane scrollPane;
	
	public LogPanel() {
		super(new BorderLayout());
        setPreferredSize(new Dimension(0, 150));
        setBorder(BorderFactory.createTitledBorder("Console Log"));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(24, 24, 24));
        logArea.setForeground(new Color(75, 215, 120)); // Verde terminale
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));

        scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        log("Client avviato. In attesa di comandi...");
	}
	
	public void log(String message) {
        logArea.append("> " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void clear() {
        logArea.setText("");
    }
}
